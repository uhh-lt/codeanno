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
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DatasetRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-18T10:42:36.085Z[GMT]")
public class DatasetRequest implements Serializable
{
    @SerializedName("cb")
    private CodebookDTO cb = null;

    @SerializedName("dataset_version")
    private String datasetVersion = null;

    public DatasetRequest cb(CodebookDTO cb)
    {
        this.cb = cb;
        return this;
    }

    /**
     * Get cb
     * 
     * @return cb
     **/
    @Schema(required = true, description = "")
    public CodebookDTO getCb()
    {
        return cb;
    }

    public void setCb(CodebookDTO cb)
    {
        this.cb = cb;
    }

    public DatasetRequest datasetVersion(String datasetVersion)
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

    @Override
    public boolean equals(java.lang.Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatasetRequest datasetRequest = (DatasetRequest) o;
        return Objects.equals(this.cb, datasetRequest.cb)
                && Objects.equals(this.datasetVersion, datasetRequest.datasetVersion);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cb, datasetVersion);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class DatasetRequest {\n");

        sb.append("    cb: ").append(toIndentedString(cb)).append("\n");
        sb.append("    datasetVersion: ").append(toIndentedString(datasetVersion)).append("\n");
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
