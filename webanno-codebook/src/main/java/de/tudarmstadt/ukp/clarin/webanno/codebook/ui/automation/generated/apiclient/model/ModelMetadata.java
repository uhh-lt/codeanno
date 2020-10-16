/*
 * Copyright 2020
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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

package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModelMetadata
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-10-02T09:35:39.209Z[GMT]")
public class ModelMetadata
    implements Serializable
{
    @SerializedName("labels")
    private Map<String, String> labels = new HashMap<String, String>();

    @SerializedName("model_type")
    private String modelType = null;

    @SerializedName("embeddings")
    private String embeddings = null;

    @SerializedName("evaluation")
    private Map<String, BigDecimal> evaluation = new HashMap<String, BigDecimal>();

    @SerializedName("timestamp")
    private String timestamp = null;

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

    public ModelMetadata embeddings(String embeddings)
    {
        this.embeddings = embeddings;
        return this;
    }

    /**
     * Get embeddings
     * 
     * @return embeddings
     **/
    @Schema(description = "")
    public String getEmbeddings()
    {
        return embeddings;
    }

    public void setEmbeddings(String embeddings)
    {
        this.embeddings = embeddings;
    }

    public ModelMetadata evaluation(Map<String, BigDecimal> evaluation)
    {
        this.evaluation = evaluation;
        return this;
    }

    public ModelMetadata putEvaluationItem(String key, BigDecimal evaluationItem)
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
    public Map<String, BigDecimal> getEvaluation()
    {
        return evaluation;
    }

    public void setEvaluation(Map<String, BigDecimal> evaluation)
    {
        this.evaluation = evaluation;
    }

    public ModelMetadata timestamp(String timestamp)
    {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Get timestamp
     * 
     * @return timestamp
     **/
    @Schema(required = true, description = "")
    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(java.lang.Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelMetadata modelMetadata = (ModelMetadata) o;
        return Objects.equals(this.labels, modelMetadata.labels)
                && Objects.equals(this.modelType, modelMetadata.modelType)
                && Objects.equals(this.embeddings, modelMetadata.embeddings)
                && Objects.equals(this.evaluation, modelMetadata.evaluation)
                && Objects.equals(this.timestamp, modelMetadata.timestamp);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(labels, modelType, embeddings, evaluation, timestamp);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ModelMetadata {\n");

        sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
        sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
        sb.append("    embeddings: ").append(toIndentedString(embeddings)).append("\n");
        sb.append("    evaluation: ").append(toIndentedString(evaluation)).append("\n");
        sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private String toIndentedString(java.lang.Object o)
    {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
