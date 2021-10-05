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
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TrainingRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-27T14:25:41.880Z[GMT]")
public class TrainingRequest
    implements Serializable
{
    @SerializedName("cb_name")
    private String cbName = null;

    @SerializedName("model_config")
    private ModelConfig modelConfig = null;

    @SerializedName("model_version")
    private String modelVersion = "default";

    @SerializedName("dataset_version")
    private String datasetVersion = "default";

    @SerializedName("batch_size_train")
    private Integer batchSizeTrain = 32;

    @SerializedName("batch_size_test")
    private Integer batchSizeTest = 32;

    @SerializedName("max_steps_train")
    private Integer maxStepsTrain = 100;

    @SerializedName("max_steps_test")
    private Integer maxStepsTest = 100;

    public TrainingRequest cbName(String cbName)
    {
        this.cbName = cbName;
        return this;
    }

    /**
     * The name of the Codebook for which the model gets trained!
     * 
     * @return cbName
     **/
    @Schema(required = true, description = "The name of the Codebook for which the model gets trained!")
    public String getCbName()
    {
        return cbName;
    }

    public void setCbName(String cbName)
    {
        this.cbName = cbName;
    }

    public TrainingRequest modelConfig(ModelConfig modelConfig)
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

    public TrainingRequest modelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
        return this;
    }

    /**
     * Get modelVersion
     * 
     * @return modelVersion
     **/
    @Schema(example = "default", description = "")
    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    public TrainingRequest datasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
        return this;
    }

    /**
     * Get datasetVersion
     * 
     * @return datasetVersion
     **/
    @Schema(example = "default", description = "")
    public String getDatasetVersion()
    {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
    }

    public TrainingRequest batchSizeTrain(Integer batchSizeTrain)
    {
        this.batchSizeTrain = batchSizeTrain;
        return this;
    }

    /**
     * Get batchSizeTrain
     * 
     * @return batchSizeTrain
     **/
    @Schema(example = "32", description = "")
    public Integer getBatchSizeTrain()
    {
        return batchSizeTrain;
    }

    public void setBatchSizeTrain(Integer batchSizeTrain)
    {
        this.batchSizeTrain = batchSizeTrain;
    }

    public TrainingRequest batchSizeTest(Integer batchSizeTest)
    {
        this.batchSizeTest = batchSizeTest;
        return this;
    }

    /**
     * Get batchSizeTest
     * 
     * @return batchSizeTest
     **/
    @Schema(example = "32", description = "")
    public Integer getBatchSizeTest()
    {
        return batchSizeTest;
    }

    public void setBatchSizeTest(Integer batchSizeTest)
    {
        this.batchSizeTest = batchSizeTest;
    }

    public TrainingRequest maxStepsTrain(Integer maxStepsTrain)
    {
        this.maxStepsTrain = maxStepsTrain;
        return this;
    }

    /**
     * Get maxStepsTrain
     * 
     * @return maxStepsTrain
     **/
    @Schema(example = "10000", description = "")
    public Integer getMaxStepsTrain()
    {
        return maxStepsTrain;
    }

    public void setMaxStepsTrain(Integer maxStepsTrain)
    {
        this.maxStepsTrain = maxStepsTrain;
    }

    public TrainingRequest maxStepsTest(Integer maxStepsTest)
    {
        this.maxStepsTest = maxStepsTest;
        return this;
    }

    /**
     * Get maxStepsTest
     * 
     * @return maxStepsTest
     **/
    @Schema(example = "1000", description = "")
    public Integer getMaxStepsTest()
    {
        return maxStepsTest;
    }

    public void setMaxStepsTest(Integer maxStepsTest)
    {
        this.maxStepsTest = maxStepsTest;
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
        TrainingRequest trainingRequest = (TrainingRequest) o;
        return Objects.equals(this.cbName, trainingRequest.cbName)
                && Objects.equals(this.modelConfig, trainingRequest.modelConfig)
                && Objects.equals(this.modelVersion, trainingRequest.modelVersion)
                && Objects.equals(this.datasetVersion, trainingRequest.datasetVersion)
                && Objects.equals(this.batchSizeTrain, trainingRequest.batchSizeTrain)
                && Objects.equals(this.batchSizeTest, trainingRequest.batchSizeTest)
                && Objects.equals(this.maxStepsTrain, trainingRequest.maxStepsTrain)
                && Objects.equals(this.maxStepsTest, trainingRequest.maxStepsTest);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cbName, modelConfig, modelVersion, datasetVersion, batchSizeTrain,
                batchSizeTest, maxStepsTrain, maxStepsTest);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class TrainingRequest {\n");

        sb.append("    cbName: ").append(toIndentedString(cbName)).append("\n");
        sb.append("    modelConfig: ").append(toIndentedString(modelConfig)).append("\n");
        sb.append("    modelVersion: ").append(toIndentedString(modelVersion)).append("\n");
        sb.append("    datasetVersion: ").append(toIndentedString(datasetVersion)).append("\n");
        sb.append("    batchSizeTrain: ").append(toIndentedString(batchSizeTrain)).append("\n");
        sb.append("    batchSizeTest: ").append(toIndentedString(batchSizeTest)).append("\n");
        sb.append("    maxStepsTrain: ").append(toIndentedString(maxStepsTrain)).append("\n");
        sb.append("    maxStepsTest: ").append(toIndentedString(maxStepsTest)).append("\n");
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
