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
package de.uhh.lt.codeanno.automation.generated.apiclient.model;

import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TrainingStatus
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-23T17:53:55.514Z[GMT]")
public class TrainingStatus
    implements Serializable
{
    @SerializedName("state")
    private String state = "unknown";

    @SerializedName("process_status")
    private String processStatus = "unknown";

    public TrainingStatus state(String state)
    {
        this.state = state;
        return this;
    }

    /**
     * Get state
     * 
     * @return state
     **/
    @Schema(description = "")
    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public TrainingStatus processStatus(String processStatus)
    {
        this.processStatus = processStatus;
        return this;
    }

    /**
     * Get processStatus
     * 
     * @return processStatus
     **/
    @Schema(description = "")
    public String getProcessStatus()
    {
        return processStatus;
    }

    public void setProcessStatus(String processStatus)
    {
        this.processStatus = processStatus;
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
        TrainingStatus trainingStatus = (TrainingStatus) o;
        return Objects.equals(this.state, trainingStatus.state)
                && Objects.equals(this.processStatus, trainingStatus.processStatus);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(state, processStatus);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class TrainingStatus {\n");

        sb.append("    state: ").append(toIndentedString(state)).append("\n");
        sb.append("    processStatus: ").append(toIndentedString(processStatus)).append("\n");
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
