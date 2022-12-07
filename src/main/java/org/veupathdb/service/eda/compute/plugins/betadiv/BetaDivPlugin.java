package org.veupathdb.service.eda.compute.plugins.betadiv;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gusdb.fgputil.ListBuilder;
import org.jetbrains.annotations.NotNull;
import org.veupathdb.service.eda.common.client.spec.StreamSpec;
import org.veupathdb.service.eda.common.model.VariableDef;
import org.veupathdb.service.eda.common.plugin.util.PluginUtil;
import org.veupathdb.service.eda.compute.plugins.AbstractPlugin;
import org.veupathdb.service.eda.compute.plugins.PluginContext;
import org.veupathdb.service.eda.compute.RServe;
import org.veupathdb.service.eda.generated.model.BetaDivComputeConfig;
import org.veupathdb.service.eda.generated.model.BetaDivPluginRequest;
import org.veupathdb.service.eda.generated.model.ComputedVariableMetadata;
import org.veupathdb.service.eda.generated.model.VariableSpec;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class BetaDivPlugin extends AbstractPlugin<BetaDivPluginRequest, BetaDivComputeConfig> {

  private static final String INPUT_DATA = "beta_div_input";

  public BetaDivPlugin(@NotNull PluginContext<BetaDivPluginRequest, BetaDivComputeConfig> context) {
    super(context);
  }

  @NotNull
  @Override
  public List<StreamSpec> getStreamSpecs() {
    return List.of(new StreamSpec(INPUT_DATA, getConfig().getCollectionVariable().getEntityId())
        .addVars(getUtil().getChildrenVariables(getConfig().getCollectionVariable())
      ));
  }

  @Override
  protected void execute() {

    BetaDivComputeConfig computeConfig = getConfig();
    PluginUtil util = getUtil();
    ReferenceMetadata meta = getContext().getReferenceMetadata();
    String entityId = computeConfig.getCollectionVariable().getEntityId();
    EntityDef entity = meta.getEntity(entityId).orElseThrow();
    VariableDef computeEntityIdVarSpec = util.getEntityIdVarSpec(entityId);
    String computeEntityIdColName = util.toColNameOrEmpty(computeEntityIdVarSpec);
    String distanceMethod = computeConfig.getBetaDivDistanceMethod().getName();
    HashMap<String, InputStream> dataStream = new HashMap<>();
    dataStream.put(INPUT_DATA, getWorkspace().openStream(INPUT_DATA));
    List<VariableDef> idColumns = new ArrayList<>();
    for (EntityDef ancestor : getAncestors(entity)) {
      idColumns.add(ancestor.getIdColumnDef());
    }

    RServe.useRConnectionWithRemoteFiles(dataStream, connection -> {
      connection.voidEval("print('starting beta diversity computation')");

      List<VariableSpec> computeInputVars = ListBuilder.asList(computeEntityIdVarSpec);
      computeInputVars.addAll(util.getChildrenVariables(computeConfig.getCollectionVariable()));
      computeInputVars.addAll(idColumns);
      //connection.voidEval(util.getVoidEvalFreadCommand(INPUT_DATA, computeInputVars));
      connection.voidEval(INPUT_DATA + " <- data.table::fread(" + INPUT_DATA + ")");
      List<String> dotNotatedIdColumns = idColumns.map(VariableDef::toDotNotation).collect(Collectors.toList());
      String dotNotatedIdColumnsString;
      for (String idCol : dotNotatedIdColumns) {
        boolean first = true;
        if (first) {
          dotNotatedIdColumnsString = "c(" + util.singleQuote(idCol);
        } else {
          dotNotatedIdColumnsString = dotNotatedIdColumnsString + "," + util.singleQuote(idCol);
        }
        dotNotatedIdColumnsString = dotNotatedIdColumnsString + ")";
      }

      connection.voidEval("abundDT <- microbiomeComputations::AbundanceData(data=" + INPUT_DATA + 
                                                                          ",recordIdColumn=" + util.singleQuote(computeEntityIdColName) + 
                                                                          ",ancestorIdColumns=" + dotNotatedIdColumnsString +
                                                                          ",imputeZero=TRUE)");
      connection.voidEval("betaDivDT <- betaDiv(abundDT, " +
                                                PluginUtil.singleQuote(distanceMethod) + ", TRUE)");
      String dataCmd = "writeData(betaDivDT, NULL, TRUE)";
      String metaCmd = "writeMeta(betaDivDT, NULL, TRUE)";

      getWorkspace().writeDataResult(connection, dataCmd);
      getWorkspace().writeMetaResult(connection, metaCmd);
    });
  }
}
