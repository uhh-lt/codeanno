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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DatasetMetadata
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-23T17:53:55.514Z[GMT]")
public class DatasetMetadata
    implements Serializable
{
    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("version")
    private String version = null;

    @SerializedName("labels")
    private Map<String, String> labels = new HashMap<String, String>();

    @SerializedName("num_training_samples")
    private Integer numTrainingSamples = null;

    @SerializedName("num_test_samples")
    private Integer numTestSamples = null;

    public DatasetMetadata codebookName(String codebookName)
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

    public DatasetMetadata version(String version)
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

    public DatasetMetadata labels(Map<String, String> labels)
    {
        this.labels = labels;
        return this;
    }

    public DatasetMetadata putLabelsItem(String key, String labelsItem)
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

    public DatasetMetadata numTrainingSamples(Integer numTrainingSamples)
    {
        this.numTrainingSamples = numTrainingSamples;
        return this;
    }

    /**
     * Get numTrainingSamples
     * 
     * @return numTrainingSamples
     **/
    @Schema(required = true, description = "")
    public Integer getNumTrainingSamples()
    {
        return numTrainingSamples;
    }

    public void setNumTrainingSamples(Integer numTrainingSamples)
    {
        this.numTrainingSamples = numTrainingSamples;
    }

    public DatasetMetadata numTestSamples(Integer numTestSamples)
    {
        this.numTestSamples = numTestSamples;
        return this;
    }

    /**
     * Get numTestSamples
     * 
     * @return numTestSamples
     **/
    @Schema(required = true, description = "")
    public Integer getNumTestSamples()
    {
        return numTestSamples;
    }

    public void setNumTestSamples(Integer numTestSamples)
    {
        this.numTestSamples = numTestSamples;
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
        DatasetMetadata datasetMetadata = (DatasetMetadata) o;
        return Objects.equals(this.codebookName, datasetMetadata.codebookName)
                && Objects.equals(this.version, datasetMetadata.version)
                && Objects.equals(this.labels, datasetMetadata.labels)
                && Objects.equals(this.numTrainingSamples, datasetMetadata.numTrainingSamples)
                && Objects.equals(this.numTestSamples, datasetMetadata.numTestSamples);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codebookName, version, labels, numTrainingSamples, numTestSamples);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class DatasetMetadata {\n");

        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    labels: ").append(toIndentedString(labels)).append("\n");
        sb.append("    numTrainingSamples: ").append(toIndentedString(numTrainingSamples))
                .append("\n");
        sb.append("    numTestSamples: ").append(toIndentedString(numTestSamples)).append("\n");
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
