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
 * PredictionResult
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-18T10:42:36.085Z[GMT]")
public class PredictionResult implements Serializable
{
    @SerializedName("doc_id")
    private Integer docId = null;

    @SerializedName("proj_id")
    private Integer projId = null;

    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("predicted_tag")
    private String predictedTag = null;

    @SerializedName("probabilities")
    private Map<String, BigDecimal> probabilities = new HashMap<String, BigDecimal>();

    public PredictionResult docId(Integer docId)
    {
        this.docId = docId;
        return this;
    }

    /**
     * Get docId
     * 
     * @return docId
     **/
    @Schema(required = true, description = "")
    public Integer getDocId()
    {
        return docId;
    }

    public void setDocId(Integer docId)
    {
        this.docId = docId;
    }

    public PredictionResult projId(Integer projId)
    {
        this.projId = projId;
        return this;
    }

    /**
     * Get projId
     * 
     * @return projId
     **/
    @Schema(required = true, description = "")
    public Integer getProjId()
    {
        return projId;
    }

    public void setProjId(Integer projId)
    {
        this.projId = projId;
    }

    public PredictionResult codebookName(String codebookName)
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

    public PredictionResult predictedTag(String predictedTag)
    {
        this.predictedTag = predictedTag;
        return this;
    }

    /**
     * Get predictedTag
     * 
     * @return predictedTag
     **/
    @Schema(required = true, description = "")
    public String getPredictedTag()
    {
        return predictedTag;
    }

    public void setPredictedTag(String predictedTag)
    {
        this.predictedTag = predictedTag;
    }

    public PredictionResult probabilities(Map<String, BigDecimal> probabilities)
    {
        this.probabilities = probabilities;
        return this;
    }

    public PredictionResult putProbabilitiesItem(String key, BigDecimal probabilitiesItem)
    {
        this.probabilities.put(key, probabilitiesItem);
        return this;
    }

    /**
     * Get probabilities
     * 
     * @return probabilities
     **/
    @Schema(required = true, description = "")
    public Map<String, BigDecimal> getProbabilities()
    {
        return probabilities;
    }

    public void setProbabilities(Map<String, BigDecimal> probabilities)
    {
        this.probabilities = probabilities;
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
        PredictionResult predictionResult = (PredictionResult) o;
        return Objects.equals(this.docId, predictionResult.docId)
                && Objects.equals(this.projId, predictionResult.projId)
                && Objects.equals(this.codebookName, predictionResult.codebookName)
                && Objects.equals(this.predictedTag, predictionResult.predictedTag)
                && Objects.equals(this.probabilities, predictionResult.probabilities);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(docId, projId, codebookName, predictedTag, probabilities);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class PredictionResult {\n");

        sb.append("    docId: ").append(toIndentedString(docId)).append("\n");
        sb.append("    projId: ").append(toIndentedString(projId)).append("\n");
        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    predictedTag: ").append(toIndentedString(predictedTag)).append("\n");
        sb.append("    probabilities: ").append(toIndentedString(probabilities)).append("\n");
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
