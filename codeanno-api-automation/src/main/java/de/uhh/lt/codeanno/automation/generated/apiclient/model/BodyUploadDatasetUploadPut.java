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

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BodyUploadDatasetUploadPut
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-27T14:25:41.880Z[GMT]")
public class BodyUploadDatasetUploadPut
    implements Serializable
{
    @SerializedName("cb_name")
    private String cbName = null;

    @SerializedName("dataset_version")
    private String datasetVersion = null;

    @SerializedName("dataset_archive")
    private File datasetArchive = null;

    public BodyUploadDatasetUploadPut cbName(String cbName)
    {
        this.cbName = cbName;
        return this;
    }

    /**
     * The name of the Codebook. Case-sensitive!
     * 
     * @return cbName
     **/
    @Schema(required = true, description = "The name of the Codebook. Case-sensitive!")
    public String getCbName()
    {
        return cbName;
    }

    public void setCbName(String cbName)
    {
        this.cbName = cbName;
    }

    public BodyUploadDatasetUploadPut datasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
        return this;
    }

    /**
     * Optional version of the dataset. If not provided and if there is already an existing dataset
     * with the same (default) version, the dataset gets overwritten. E.g. v1
     * 
     * @return datasetVersion
     **/
    @Schema(required = true, description = "Optional version of the dataset. If  not provided and if there is already an existing dataset with the same (default) version, the dataset gets overwritten.  E.g. v1")
    public String getDatasetVersion()
    {
        return datasetVersion;
    }

    public void setDatasetVersion(String datasetVersion)
    {
        this.datasetVersion = datasetVersion;
    }

    public BodyUploadDatasetUploadPut datasetArchive(File datasetArchive)
    {
        this.datasetArchive = datasetArchive;
        return this;
    }

    /**
     * CSV Dataset in a zip-archive.
     * 
     * @return datasetArchive
     **/
    @Schema(required = true, description = "CSV Dataset in a zip-archive.")
    public File getDatasetArchive()
    {
        return datasetArchive;
    }

    public void setDatasetArchive(File datasetArchive)
    {
        this.datasetArchive = datasetArchive;
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
        BodyUploadDatasetUploadPut bodyUploadDatasetUploadPut = (BodyUploadDatasetUploadPut) o;
        return Objects.equals(this.cbName, bodyUploadDatasetUploadPut.cbName)
                && Objects.equals(this.datasetVersion, bodyUploadDatasetUploadPut.datasetVersion)
                && Objects.equals(this.datasetArchive, bodyUploadDatasetUploadPut.datasetArchive);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cbName, datasetVersion, Objects.hashCode(datasetArchive));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class BodyUploadDatasetUploadPut {\n");

        sb.append("    cbName: ").append(toIndentedString(cbName)).append("\n");
        sb.append("    datasetVersion: ").append(toIndentedString(datasetVersion)).append("\n");
        sb.append("    datasetArchive: ").append(toIndentedString(datasetArchive)).append("\n");
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
