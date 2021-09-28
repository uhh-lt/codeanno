/*
 * Copyright 2021
 * Language Technology (LT) Universität Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uhh.lt.codeanno.automation.generated.apiclient.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModelMetadata
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-27T14:25:41.880Z[GMT]")
public class ModelMetadata
    implements Serializable
{
    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("version")
    private String version = null;

    @SerializedName("dataset_version")
    private String datasetVersion = null;

    @SerializedName("labels")
    private Map<String, String> labels = new HashMap<String, String>();

    @SerializedName("model_type")
    private String modelType = null;

    @SerializedName("evaluation")
    private Map<String, Double> evaluation = new HashMap<String, Double>();

    @SerializedName("model_config")
    private ModelConfig modelConfig = null;

    public ModelMetadata codebookName(String codebookName)
    {
        this.codebookName = codebookName;
        return this;
    }

    /**
     * Get codebookName
     * 
     * @return codebookName
     **/
    @Schema(required = true, description = "")
    public String getCodebookName()
    {
        return codebookName;
    }

    public void setCodebookName(String codebookName)
    {
        this.codebookName = codebookName;
    }

    public ModelMetadata version(String version)
    {
        this.version = version;
        return this;
    }

    /**
     * Get version
     * 
     * @return version
     **/
    @Schema(required = true, description = "")
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public ModelMetadata datasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
        return this;
    }

    /**
     * Get datasetVersion
     * 
     * @return datasetVersion
     **/
    @Schema(required = true, description = "")
    public String getDatasetVersion()
    {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
    }

    public ModelMetadata labels(Map<String, String> labels)
    {
        this.labels = labels;
        return this;
    }

    public ModelMetadata putLabelsItem(String key, String labelsItem)
    {
        this.labels.put(key, labelsItem);
        return this;
    }

    /**
     * Get labels
     * 
     * @return labels
     **/
    @Schema(required = true, description = "")
    public Map<String, String> getLabels()
    {
        return labels;
    }

    public void setLabels(Map<String, String> labels)
    {
        this.labels = labels;
    }

    public ModelMetadata modelType(String modelType)
    {
        this.modelType = modelType;
        return this;
    }

    /**
     * Get modelType
     * 
     * @return modelType
     **/
    @Schema(required = true, description = "")
    public String getModelType()
    {
        return modelType;
    }

    public void setModelType(String modelType)
    {
        this.modelType = modelType;
    }

    public ModelMetadata evaluation(Map<String, Double> evaluation)
    {
        this.evaluation = evaluation;
        return this;
    }

    public ModelMetadata putEvaluationItem(String key, Double evaluationItem)
    {
        this.evaluation.put(key, evaluationItem);
        return this;
    }

    /**
     * Get evaluation
     * 
     * @return evaluation
     **/
    @Schema(required = true, description = "")
    public Map<String, Double> getEvaluation()
    {
        return evaluation;
    }

    public void setEvaluation(Map<String, Double> evaluation)
    {
        this.evaluation = evaluation;
    }

    public ModelMetadata modelConfig(ModelConfig modelConfig)
    {
        this.modelConfig = modelConfig;
        return this;
    }

    /**
     * Get modelConfig
     * 
     * @return modelConfig
     **/
    @Schema(required = true, description = "")
    public ModelConfig getModelConfig()
    {
        return modelConfig;
    }

    public void setModelConfig(ModelConfig modelConfig)
    {
        this.modelConfig = modelConfig;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelMetadata modelMetadata = (ModelMetadata) o;
        return Objects.equals(this.codebookName, modelMetadata.codebookName)
                && Objects.equals(this.version, modelMetadata.version)
                && Objects.equals(this.datasetVersion, modelMetadata.datasetVersion)
                && Objects.equals(this.labels, modelMetadata.labels)
                && Objects.equals(this.modelType, modelMetadata.modelType)
                && Objects.equals(this.evaluation, modelMetadata.evaluation)
                && Objects.equals(this.modelConfig, modelMetadata.modelConfig);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codebookName, version, datasetVersion, labels, modelType, evaluation,
                modelConfig);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ModelMetadata {\n");

        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    datasetVersion: ").append(toIndentedString(datasetVersion)).append("\n");
        sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
        sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
        sb.append("    evaluation: ").append(toIndentedString(evaluation)).append("\n");
        sb.append("    modelConfig: ").append(toIndentedString(modelConfig)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private String toIndentedString(Object o)
    {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
