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

import java.io.File;
import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BodyUploadDatasetTrainingUploadDatasetPut
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen",
                            date = "2020-11-27T12:05:41.274Z[GMT]")
public class BodyUploadDatasetTrainingUploadDatasetPut
    implements Serializable
{
    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("codebook_tag_list")
    private String codebookTagList = null;

    @SerializedName("dataset_version")
    private String datasetVersion = null;

    @SerializedName("dataset_archive")
    private File datasetArchive = null;

    public BodyUploadDatasetTrainingUploadDatasetPut codebookName(String codebookName)
    {
        this.codebookName = codebookName;
        return this;
    }

    /**
     * The name of the Codebook. Case-sensitive!
     *
     * @return codebookName
     **/
    @Schema(required = true, description = "The name of the Codebook. Case-sensitive!")
    public String getCodebookName()
    {
        return codebookName;
    }

    public void setCodebookName(String codebookName)
    {
        this.codebookName = codebookName;
    }

    public BodyUploadDatasetTrainingUploadDatasetPut codebookTagList(String codebookTagList)
    {
        this.codebookTagList = codebookTagList;
        return this;
    }

    /**
     * Comma-separated list of tags. E.g. tag1,Tag2 ...
     *
     * @return codebookTagList
     **/
    @Schema(required = true, description = "Comma-separated list of tags. E.g. tag1,Tag2 ...")
    public String getCodebookTagList()
    {
        return codebookTagList;
    }

    public void setCodebookTagList(String codebookTagList)
    {
        this.codebookTagList = codebookTagList;
    }

    public BodyUploadDatasetTrainingUploadDatasetPut datasetVersion(String datasetVersion)
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

    public BodyUploadDatasetTrainingUploadDatasetPut datasetArchive(File datasetArchive)
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
        BodyUploadDatasetTrainingUploadDatasetPut bodyUploadDatasetTrainingUploadDatasetPut =
                (BodyUploadDatasetTrainingUploadDatasetPut) o;
        return Objects.equals(this.codebookName,
                bodyUploadDatasetTrainingUploadDatasetPut.codebookName)
                && Objects.equals(this.codebookTagList,
                        bodyUploadDatasetTrainingUploadDatasetPut.codebookTagList)
                && Objects.equals(this.datasetVersion,
                        bodyUploadDatasetTrainingUploadDatasetPut.datasetVersion)
                && Objects.equals(this.datasetArchive,
                        bodyUploadDatasetTrainingUploadDatasetPut.datasetArchive);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codebookName, codebookTagList, datasetVersion,
                Objects.hashCode(datasetArchive));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class BodyUploadDatasetTrainingUploadDatasetPut {\n");

        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    codebookTagList: ").append(toIndentedString(codebookTagList)).append("\n");
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
