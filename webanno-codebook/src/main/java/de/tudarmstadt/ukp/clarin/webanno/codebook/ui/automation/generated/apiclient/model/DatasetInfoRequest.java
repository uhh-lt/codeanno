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
 * DatasetInfoRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-11-27T12:05:41.274Z[GMT]")
public class DatasetInfoRequest
    implements Serializable
{
    @SerializedName("cb")
    private CodebookModel cb = null;

    @SerializedName("dataset_version_tag")
    private String datasetVersionTag = null;

    public DatasetInfoRequest cb(CodebookModel cb)
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
    public CodebookModel getCb()
    {
        return cb;
    }

    public void setCb(CodebookModel cb)
    {
        this.cb = cb;
    }

    public DatasetInfoRequest datasetVersionTag(String datasetVersionTag)
    {
        this.datasetVersionTag = datasetVersionTag;
        return this;
    }

    /**
     * Get datasetVersionTag
     *
     * @return datasetVersionTag
     **/
    @Schema(required = true, description = "")
    public String getDatasetVersionTag()
    {
        return datasetVersionTag;
    }

    public void setDatasetVersionTag(String datasetVersionTag)
    {
        this.datasetVersionTag = datasetVersionTag;
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
        DatasetInfoRequest datasetInfoRequest = (DatasetInfoRequest) o;
        return Objects.equals(this.cb, datasetInfoRequest.cb)
                && Objects.equals(this.datasetVersionTag, datasetInfoRequest.datasetVersionTag);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cb, datasetVersionTag);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class DatasetInfoRequest {\n");

        sb.append("    cb: ").append(toIndentedString(cb)).append("\n");
        sb.append("    datasetVersionTag: ").append(toIndentedString(datasetVersionTag))
                .append("\n");
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
