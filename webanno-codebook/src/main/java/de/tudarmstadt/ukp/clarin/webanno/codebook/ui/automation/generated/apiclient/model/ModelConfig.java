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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ModelConfig
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-18T10:42:36.085Z[GMT]")
public class ModelConfig implements Serializable
{
    @SerializedName("embedding_type")
    private String embeddingType = "https://tfhub.dev/google/universal-sentence-encoder/2";

    @SerializedName("hidden_units")
    private List<Integer> hiddenUnits = null;

    @SerializedName("dropout")
    private Double dropout = 0.2;

    @SerializedName("optimizer")
    private OptimizerIdentifier optimizer = null;

    @SerializedName("early_stopping")
    private Boolean earlyStopping = false;

    @SerializedName("activation_fn")
    private ActivationFunctionIdentifier activationFn = null;

    public ModelConfig embeddingType(String embeddingType)
    {
        this.embeddingType = embeddingType;
        return this;
    }

    /**
     * Get embeddingType
     * 
     * @return embeddingType
     **/
    @Schema(example = "https://tfhub.dev/google/universal-sentence-encoder/2", description = "")
    public String getEmbeddingType()
    {
        return embeddingType;
    }

    public void setEmbeddingType(String embeddingType)
    {
        this.embeddingType = embeddingType;
    }

    public ModelConfig hiddenUnits(List<Integer> hiddenUnits)
    {
        this.hiddenUnits = hiddenUnits;
        return this;
    }

    public ModelConfig addHiddenUnitsItem(Integer hiddenUnitsItem)
    {
        if (this.hiddenUnits == null) {
            this.hiddenUnits = new ArrayList<Integer>();
        }
        this.hiddenUnits.add(hiddenUnitsItem);
        return this;
    }

    /**
     * Get hiddenUnits
     * 
     * @return hiddenUnits
     **/
    @Schema(example = "[1024,1024,512,64]", description = "")
    public List<Integer> getHiddenUnits()
    {
        return hiddenUnits;
    }

    public void setHiddenUnits(List<Integer> hiddenUnits)
    {
        this.hiddenUnits = hiddenUnits;
    }

    public ModelConfig dropout(Double dropout)
    {
        this.dropout = dropout;
        return this;
    }

    /**
     * Get dropout
     * 
     * @return dropout
     **/
    @Schema(example = "0.2", description = "")
    public Double getDropout()
    {
        return dropout;
    }

    public void setDropout(Double dropout)
    {
        this.dropout = dropout;
    }

    public ModelConfig optimizer(OptimizerIdentifier optimizer)
    {
        this.optimizer = optimizer;
        return this;
    }

    /**
     * Get optimizer
     * 
     * @return optimizer
     **/
    @Schema(description = "")
    public OptimizerIdentifier getOptimizer()
    {
        return optimizer;
    }

    public void setOptimizer(OptimizerIdentifier optimizer)
    {
        this.optimizer = optimizer;
    }

    public ModelConfig earlyStopping(Boolean earlyStopping)
    {
        this.earlyStopping = earlyStopping;
        return this;
    }

    /**
     * Get earlyStopping
     * 
     * @return earlyStopping
     **/
    @Schema(example = "false", description = "")
    public Boolean isEarlyStopping()
    {
        return earlyStopping;
    }

    public void setEarlyStopping(Boolean earlyStopping)
    {
        this.earlyStopping = earlyStopping;
    }

    public ModelConfig activationFn(ActivationFunctionIdentifier activationFn)
    {
        this.activationFn = activationFn;
        return this;
    }

    /**
     * Get activationFn
     * 
     * @return activationFn
     **/
    @Schema(description = "")
    public ActivationFunctionIdentifier getActivationFn()
    {
        return activationFn;
    }

    public void setActivationFn(ActivationFunctionIdentifier activationFn)
    {
        this.activationFn = activationFn;
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
        ModelConfig modelConfig = (ModelConfig) o;
        return Objects.equals(this.embeddingType, modelConfig.embeddingType)
                && Objects.equals(this.hiddenUnits, modelConfig.hiddenUnits)
                && Objects.equals(this.dropout, modelConfig.dropout)
                && Objects.equals(this.optimizer, modelConfig.optimizer)
                && Objects.equals(this.earlyStopping, modelConfig.earlyStopping)
                && Objects.equals(this.activationFn, modelConfig.activationFn);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(embeddingType, hiddenUnits, dropout, optimizer, earlyStopping,
                activationFn);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ModelConfig {\n");

        sb.append("    embeddingType: ").append(toIndentedString(embeddingType)).append("\n");
        sb.append("    hiddenUnits: ").append(toIndentedString(hiddenUnits)).append("\n");
        sb.append("    dropout: ").append(toIndentedString(dropout)).append("\n");
        sb.append("    optimizer: ").append(toIndentedString(optimizer)).append("\n");
        sb.append("    earlyStopping: ").append(toIndentedString(earlyStopping)).append("\n");
        sb.append("    activationFn: ").append(toIndentedString(activationFn)).append("\n");
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
