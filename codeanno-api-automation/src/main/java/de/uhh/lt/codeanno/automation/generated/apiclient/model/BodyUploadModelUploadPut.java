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
 * BodyUploadModelUploadPut
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-27T14:25:41.880Z[GMT]")
public class BodyUploadModelUploadPut
    implements Serializable
{
    @SerializedName("codebook_name")
    private String codebookName = null;

    @SerializedName("model_version")
    private String modelVersion = null;

    @SerializedName("model_archive")
    private File modelArchive = null;

    public BodyUploadModelUploadPut codebookName(String codebookName)
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

    public BodyUploadModelUploadPut modelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
        return this;
    }

    /**
     * Optional version tag of the model. If a tag is not provided and if there is already an
     * existing model with the same (default) tag, the model gets overwritten. E.g. v1
     * 
     * @return modelVersion
     **/
    @Schema(required = true, description = "Optional version tag of the model. If a tag is not provided and if there is already an existing model with the same (default) tag, the model gets overwritten.  E.g. v1")
    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    public BodyUploadModelUploadPut modelArchive(File modelArchive)
    {
        this.modelArchive = modelArchive;
        return this;
    }

    /**
     * Zip-archive of the model.
     * 
     * @return modelArchive
     **/
    @Schema(required = true, description = "Zip-archive of the model.")
    public File getModelArchive()
    {
        return modelArchive;
    }

    public void setModelArchive(File modelArchive)
    {
        this.modelArchive = modelArchive;
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
        BodyUploadModelUploadPut bodyUploadModelUploadPut = (BodyUploadModelUploadPut) o;
        return Objects.equals(this.codebookName, bodyUploadModelUploadPut.codebookName)
                && Objects.equals(this.modelVersion, bodyUploadModelUploadPut.modelVersion)
                && Objects.equals(this.modelArchive, bodyUploadModelUploadPut.modelArchive);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(codebookName, modelVersion, Objects.hashCode(modelArchive));
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class BodyUploadModelUploadPut {\n");

        sb.append("    codebookName: ").append(toIndentedString(codebookName)).append("\n");
        sb.append("    modelVersion: ").append(toIndentedString(modelVersion)).append("\n");
        sb.append("    modelArchive: ").append(toIndentedString(modelArchive)).append("\n");
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
