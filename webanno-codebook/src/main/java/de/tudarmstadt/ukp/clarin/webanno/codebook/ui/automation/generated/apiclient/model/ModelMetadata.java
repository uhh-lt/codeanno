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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.threeten.bp.OffsetDateTime;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModelMetadata
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-10-01T15:38:30.492Z[GMT]")
public class ModelMetadata  implements Serializable
{
    @SerializedName("model_id")
    private Integer modelId = null;

    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("labels")
    private List<String> labels = new ArrayList<String>();

    @SerializedName("size_mb")
    private Integer sizeMb = null;

    @SerializedName("trained_with_samples")
    private Integer trainedWithSamples = null;

    @SerializedName("last_update")
    private OffsetDateTime lastUpdate = null;

    public ModelMetadata modelId(Integer modelId)
    {
        this.modelId = modelId;
        return this;
    }

    /**
     * Get modelId
     * 
     * @return modelId
     **/
    @Schema(required = true, description = "")
    public Integer getModelId()
    {
        return modelId;
    }

    public void setModelId(Integer modelId)
    {
        this.modelId = modelId;
    }

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

    public ModelMetadata labels(List<String> labels)
    {
        this.labels = labels;
        return this;
    }

    public ModelMetadata addLabelsItem(String labelsItem)
    {
        this.labels.add(labelsItem);
        return this;
    }

    /**
     * Get labels
     * 
     * @return labels
     **/
    @Schema(required = true, description = "")
    public List<String> getLabels()
    {
        return labels;
    }

    public void setLabels(List<String> labels)
    {
        this.labels = labels;
    }

    public ModelMetadata sizeMb(Integer sizeMb)
    {
        this.sizeMb = sizeMb;
        return this;
    }

    /**
     * Get sizeMb
     * 
     * @return sizeMb
     **/
    @Schema(description = "")
    public Integer getSizeMb()
    {
        return sizeMb;
    }

    public void setSizeMb(Integer sizeMb)
    {
        this.sizeMb = sizeMb;
    }

    public ModelMetadata trainedWithSamples(Integer trainedWithSamples)
    {
        this.trainedWithSamples = trainedWithSamples;
        return this;
    }

    /**
     * Get trainedWithSamples
     * 
     * @return trainedWithSamples
     **/
    @Schema(description = "")
    public Integer getTrainedWithSamples()
    {
        return trainedWithSamples;
    }

    public void setTrainedWithSamples(Integer trainedWithSamples)
    {
        this.trainedWithSamples = trainedWithSamples;
    }

    public ModelMetadata lastUpdate(OffsetDateTime lastUpdate)
    {
        this.lastUpdate = lastUpdate;
        return this;
    }

    /**
     * Get lastUpdate
     * 
     * @return lastUpdate
     **/
    @Schema(required = true, description = "")
    public OffsetDateTime getLastUpdate()
    {
        return lastUpdate;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate)
    {
        this.lastUpdate = lastUpdate;
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
        return Objects.equals(this.modelId, modelMetadata.modelId)
                && Objects.equals(this.codebookName, modelMetadata.codebookName)
                && Objects.equals(this.labels, modelMetadata.labels)
                && Objects.equals(this.sizeMb, modelMetadata.sizeMb)
                && Objects.equals(this.trainedWithSamples, modelMetadata.trainedWithSamples)
                && Objects.equals(this.lastUpdate, modelMetadata.lastUpdate);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(modelId, codebookName, labels, sizeMb, trainedWithSamples, lastUpdate);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ModelMetadata {\n");

        sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
        sb.append("    sizeMb: ").append(toIndentedString(sizeMb)).append("\n");
        sb.append("    trainedWithSamples: ").append(toIndentedString(trainedWithSamples))
                .append("\n");
        sb.append("    lastUpdate: ").append(toIndentedString(lastUpdate)).append("\n");
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
