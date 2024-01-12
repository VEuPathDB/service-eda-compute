package org.veupathdb.service.eda.compute.plugins.correlationassaymetadata;

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
import org.veupathdb.service.eda.generated.model.Correlation1Collection;
import org.veupathdb.service.eda.generated.model.CorrelationAssayMetadataPluginRequest;
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

public class CorrelationAssayMetadataPlugin extends AbstractPlugin<CorrelationAssayMetadataPluginRequest, Correlation1Collection> {
  private static final Logger LOG = LogManager.getLogger(CorrelationAssayMetadataPlugin.class);

  private static final String INPUT_DATA = "correlation_input";

  public CorrelationAssayMetadataPlugin(@NotNull PluginContext<CorrelationAssayMetadataPluginRequest, Correlation1Collection> context) {
    super(context);
  }

  @NotNull
  @Override
  public List<StreamSpec> getStreamSpecs() {
    // Get the collection variable and its entity
    Correlation1Collection computeConfig = getConfig();
    CollectionSpec collectionVariable = computeConfig.getCollectionVariable();
    String entityId = collectionVariable.getEntityId();

    // Wrangle into correct types for what follows
    EntityDef entity = getContext().getReferenceMetadata().getEntity(entityId).orElseThrow();

    // Find ancestors
    ReferenceMetadata metadata = getContext().getReferenceMetadata();
    List<EntityDef> ancestors = metadata.getAncestors(entity);

    // Grab all metadata variables appropriate for this compute. When there are ancestor entities,
    // we only want those variables that are on a one-to-one path with the current entity. When there
    // are no ancestor entities, we'll take all variables that are not part of a collection as metadata variables.
    List<VariableDef> metadataVariables;
    if (ancestors.isEmpty()) {

      LOG.info("No ancestors found, using all variables that are not part of a collection.");
      
      // Grab all non-id variables from this entity
      metadataVariables = entity.getVariables().stream()
          .filter(var -> !var.getVariableId().contains("_stable_id"))
          .collect(Collectors.toList());

      // List the variables that are already in collections
      List<VariableDef> variablesInACollection = entity.getCollections().stream()
          .flatMap(collection -> collection.getMemberVariables().stream())
          .collect(Collectors.toList());

      // Remove variables that are already in a collection from the metadata variables list.
      metadataVariables.removeAll(variablesInACollection);

    } else {
      
      LOG.info("At least one ancestor found. Looking for variables on entities that are on a one-to-one path with entity: {}", entity.getId());
      metadataVariables = ancestors.stream() // Get all ancestors of entity.
          .filter(ancestor -> !getManyToOneWithDescendant(metadata, ancestor, entity)) // Filter to those that are one-to-one with target entity or ancestor of entity.
          .flatMap(entityDef -> entityDef.getVariables().stream()) // Flatten stream of var streams into a single stream of vars.
          .filter(var -> !var.getVariableId().contains("_stable_id")) // Filter out id variables
          .collect(Collectors.toList());

    }

    // Filter variables to only include those that are continuous
    metadataVariables = metadataVariables.stream()
      .filter(var -> var.getDataShape() == APIVariableDataShape.CONTINUOUS && var.getSource().isResident()) // Filter out inherited and non-continuous variables.
      .collect(Collectors.toList());

    LOG.info("Using the following metadata variables for correlation: {}",
        JsonUtil.serializeObject(metadataVariables));

    return List.of(new StreamSpec(INPUT_DATA, getConfig().getCollectionVariable().getEntityId())
        .addVars(getUtil().getCollectionMembers(collectionVariable))
        .addVars(metadataVariables)
      );
  }

  private Boolean getManyToOneWithDescendant(ReferenceMetadata metadata, EntityDef ancestor, EntityDef descendantToMatch) {
    return metadata.getChildren(ancestor).stream()
        .filter(child -> metadata.isEntityAncestorOf(child, descendantToMatch) || descendantToMatch.getId().equals(child.getId())) // Find child on path to descendant to match.
        .findFirst()
        .orElseThrow()
        .isManyToOneWithParent();
  }

  @Override
  protected void execute() {

    Correlation1Collection computeConfig = getConfig();
    PluginUtil util = getUtil();
    ReferenceMetadata metadata = getContext().getReferenceMetadata();

    // Get compute parameters
    String method = computeConfig.getCorrelationMethod().getValue();
    CollectionSpec collectionVariable = computeConfig.getCollectionVariable();
    String entityId = collectionVariable.getEntityId();

    // Wrangle into helpful types
    EntityDef entity = metadata.getEntity(entityId).orElseThrow();
    VariableDef computeEntityIdVarSpec = util.getEntityIdVarSpec(entityId);
    String computeEntityIdColName = util.toColNameOrEmpty(computeEntityIdVarSpec);

    // Get record id columns
    List<VariableDef> idColumns = new ArrayList<>();
    for (EntityDef ancestor : metadata.getAncestors(entity)) {
      idColumns.add(ancestor.getIdColumnDef());
    }

    HashMap<String, InputStream> dataStream = new HashMap<>();
    dataStream.put(INPUT_DATA, getWorkspace().openStream(INPUT_DATA));


    // Find ancestors
    List<EntityDef> ancestors = metadata.getAncestors(entity);

    // As above, collect all appropriate metadata variables. 
    List<VariableDef> metadataVariables;
    if (ancestors.isEmpty()) {
      LOG.info("No ancestors found, using all variables that are not part of a collection.");
      
      // Grab all non-id variables from this entity
      metadataVariables = entity.getVariables().stream()
          .filter(var -> !var.getVariableId().contains("_stable_id"))
          .collect(Collectors.toList());

      // List the variables that are already in collections
      List<VariableDef> variablesInACollection = entity.getCollections().stream()
          .flatMap(collection -> collection.getMemberVariables().stream())
          .collect(Collectors.toList());

      // Remove variables that are already in a collection from the metadata variables list.
      metadataVariables.removeAll(variablesInACollection);

    } else {

      LOG.info("At least one ancestor found. Looking for variables on entities that are on a one-to-one path with entity: {}", entity.getId());
      metadataVariables = ancestors.stream() // Get all ancestors of entity.
          .filter(ancestor -> !getManyToOneWithDescendant(metadata, ancestor, entity)) // Filter to those that are one-to-one with target entity or ancestor of entity.
          .flatMap(entityDef -> entityDef.getVariables().stream()) // Flatten stream of var streams into a single stream of vars.
          .filter(var -> !var.getVariableId().contains("_stable_id")) // Filter out id variables
          .collect(Collectors.toList());
    }

    // Filter variables to only include those that are continuous
    List<VariableDef> finalMetadataVariables = metadataVariables.stream()
      .filter(var -> var.getDataShape() == APIVariableDataShape.CONTINUOUS && var.getSource().isResident()) // Filter out inherited and non-continuous variables.
      .collect(Collectors.toList());

    LOG.info("Using the following metadata variables for correlation: {}",
        JsonUtil.serializeObject(metadataVariables));

    RServe.useRConnectionWithRemoteFiles(dataStream, connection -> {
      connection.voidEval("print('starting correlation computation')");

      // Read in the abundance data
      List<VariableSpec> abundanceInputVars = ListBuilder.asList(computeEntityIdVarSpec);
      abundanceInputVars.addAll(util.getCollectionMembers(collectionVariable));
      abundanceInputVars.addAll(idColumns);
      connection.voidEval(util.getVoidEvalFreadCommand(INPUT_DATA, abundanceInputVars));
      connection.voidEval("abundanceData <- " + INPUT_DATA); // Renaming here so we can go get the sampleMetadata later

      // Read in the sample metadata
      List<VariableSpec> metadataInputVars = ListBuilder.asList(computeEntityIdVarSpec);
      metadataInputVars.addAll(finalMetadataVariables);
      metadataInputVars.addAll(idColumns);
      connection.voidEval(util.getVoidEvalFreadCommand(INPUT_DATA, metadataInputVars));
      connection.voidEval("sampleMetadata <- " + INPUT_DATA); 

      // Turn the list of id columns into an array of strings for R
      List<String> dotNotatedIdColumns = idColumns.stream().map(VariableDef::toDotNotation).toList();
      String dotNotatedIdColumnsString = util.listToRVector(dotNotatedIdColumns);

      
      
      // Format inputs for R   
      connection.voidEval("sampleMetadata <- SampleMetadata(data = sampleMetadata" +
                                ", recordIdColumn=" + singleQuote(computeEntityIdColName) +
                                ", ancestorIdColumns=as.character(" + dotNotatedIdColumnsString + "))");

      connection.voidEval("abundanceData <- AbundanceData(data=abundanceData" + 
                                ", sampleMetadata=sampleMetadata" +
                                ", recordIdColumn=" + singleQuote(computeEntityIdColName) +
                                ", ancestorIdColumns=as.character(" + dotNotatedIdColumnsString + ")" +
                                ", imputeZero=TRUE)");                          
      
      // Run correlation!
      connection.voidEval("computeResult <- microbiomeComputations::correlation(data1=abundanceData" +
                                                          ", method=" + singleQuote(method) +
                                                          ", verbose=TRUE)");


      String statsCmd = "writeStatistics(computeResult, NULL, TRUE)";

      getWorkspace().writeStatisticsResult(connection, statsCmd);
    });
  }
}
