package org.veupathdb.service.eda.compute.plugins.correlationassayself;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gusdb.fgputil.ListBuilder;
import org.gusdb.fgputil.json.JsonUtil;
import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.common.client.spec.StreamSpec;
import org.veupathdb.service.eda.common.model.EntityDef;
import org.veupathdb.service.eda.common.model.ReferenceMetadata;
import org.veupathdb.service.eda.common.model.VariableDef;
import org.veupathdb.service.eda.common.plugin.util.PluginUtil;
import org.veupathdb.service.eda.compute.RServe;
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin;
import org.veupathdb.service.eda.compute.plugins.PluginContext;
import org.veupathdb.service.eda.generated.model.CorrelationAssaySelfConfig;
import org.veupathdb.service.eda.generated.model.CorrelationAssaySelfPluginRequest;
import org.veupathdb.service.eda.generated.model.FeaturePrefilterThresholds;
import org.veupathdb.service.eda.generated.model.VariableSpec;
import org.veupathdb.service.eda.generated.model.APIVariableDataShape;
import org.veupathdb.service.eda.generated.model.CollectionSpec;

import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.veupathdb.service.eda.common.plugin.util.PluginUtil.singleQuote;

public class CorrelationAssaySelfPlugin extends AbstractPlugin<CorrelationAssaySelfPluginRequest, CorrelationAssaySelfConfig> {
  private static final Logger LOG = LogManager.getLogger(CorrelationAssaySelfPlugin.class);

  private static final String ASSAY_DATA = "assayData";

  public CorrelationAssaySelfPlugin(@NotNull PluginContext<CorrelationAssaySelfPluginRequest, CorrelationAssaySelfConfig> context) {
    super(context);
  }

  @NotNull
  @Override
  public List<StreamSpec> getStreamSpecs() {
    // Get the collection variable and its entity
    CorrelationAssaySelfConfig computeConfig = getConfig();
    CollectionSpec assay = computeConfig.getCollectionVariable();

    return List.of(
      new StreamSpec(ASSAY_DATA, getConfig().getCollectionVariable().getEntityId())
        .addVars(getUtil().getCollectionMembers(assay))
    );
  }

  @Override
  protected void execute() {

    CorrelationAssaySelfConfig computeConfig = getConfig();
    PluginUtil util = getUtil();
    ReferenceMetadata metadata = getContext().getReferenceMetadata();

    // Get compute parameters
    String method = computeConfig.getCorrelationMethod().getValue();
    CollectionSpec assay = computeConfig.getCollectionVariable();
    FeaturePrefilterThresholds featureFilterThresholds = computeConfig.getPrefilterThresholds();
    String proportionNonZeroThresholdRParam = 
      featureFilterThresholds != null &&
      featureFilterThresholds.getProportionNonZero() != null ? 
        ",proportionNonZeroThreshold=" + featureFilterThresholds.getProportionNonZero() : "";
    String varianceThresholdRParam = 
      featureFilterThresholds != null &&
      featureFilterThresholds.getVariance() != null ? 
        ",varianceThreshold=" + featureFilterThresholds.getVariance() : "";
    String stdDevThresholdRParam =
      featureFilterThresholds != null &&
      featureFilterThresholds.getStandardDeviation() != null ? 
        ",stdDevThreshold=" + featureFilterThresholds.getStandardDeviation() : "";

    String entityId = assay.getEntityId();
    EntityDef entity = metadata.getEntity(entityId).orElseThrow();
    VariableDef entityIdVarSpec = util.getEntityIdVarSpec(entityId);
    String computeEntityIdColName = util.toColNameOrEmpty(entityIdVarSpec);

    // Get record id columns
    List<VariableDef> entityAncestorIdColumns = new ArrayList<>();
    for (EntityDef ancestor : metadata.getAncestors(entity)) {
      entityAncestorIdColumns.add(ancestor.getIdColumnDef());
    }    

    HashMap<String, InputStream> dataStream = new HashMap<>();
    dataStream.put(ASSAY_DATA, getWorkspace().openStream(ASSAY_DATA));

    RServe.useRConnectionWithRemoteFiles(dataStream, connection -> {
      connection.voidEval("print('starting correlation computation')");

      // Read in the assay data
      List<VariableSpec> assayInputVars = ListBuilder.asList(entityIdVarSpec);
      assayInputVars.addAll(util.getCollectionMembers(assay));
      assayInputVars.addAll(entityAncestorIdColumns);
      connection.voidEval(util.getVoidEvalFreadCommand(ASSAY_DATA, assayInputVars));

      // Turn the list of id columns into an array of strings for R
      List<String> dotNotatedEntityIdColumns = entityAncestorIdColumns.stream().map(VariableDef::toDotNotation).toList();
      String dotNotatedEntityIdColumnsString = util.listToRVector(dotNotatedEntityIdColumns);

      connection.voidEval("data <- AbundanceData(data=assayData" + 
                                ", recordIdColumn=" + singleQuote(computeEntityIdColName) +
                                ", ancestorIdColumns=as.character(" + dotNotatedEntityIdColumnsString + ")" +
                                ", imputeZero=TRUE)");
      // Run correlation!
      connection.voidEval("computeResult <- microbiomeComputations::selfCorrelation(data=data" +
                                                          ", method=" + singleQuote(method) +
                                                          proportionNonZeroThresholdRParam +
                                                          varianceThresholdRParam +
                                                          stdDevThresholdRParam +
                                                          ", verbose=TRUE)");


      String statsCmd = "writeStatistics(computeResult, NULL, TRUE)";

      getWorkspace().writeStatisticsResult(connection, statsCmd);
    });
  }
}