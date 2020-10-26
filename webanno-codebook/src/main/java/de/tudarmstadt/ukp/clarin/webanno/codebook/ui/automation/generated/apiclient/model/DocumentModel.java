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
 * DocumentModel
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-10-26T16:49:00.408Z[GMT]")
public class DocumentModel
    implements Serializable
{
    @SerializedName("doc_id")
    private Integer docId = null;

    @SerializedName("proj_id")
    private Integer projId = null;

    @SerializedName("text")
    private String text = null;

    public DocumentModel docId(Integer docId)
    {
        this.docId = docId;
        return this;
    }

    /**
     * Get docId
     * 
     * @return docId
     **/
    @Schema(description = "")
    public Integer getDocId()
    {
        return docId;
    }

    public void setDocId(Integer docId)
    {
        this.docId = docId;
    }

    public DocumentModel projId(Integer projId)
    {
        this.projId = projId;
        return this;
    }

    /**
     * Get projId
     * 
     * @return projId
     **/
    @Schema(description = "")
    public Integer getProjId()
    {
        return projId;
    }

    public void setProjId(Integer projId)
    {
        this.projId = projId;
    }

    public DocumentModel text(String text)
    {
        this.text = text;
        return this;
    }

    /**
     * Get text
     * 
     * @return text
     **/
    @Schema(description = "")
    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
        DocumentModel documentModel = (DocumentModel) o;
        return Objects.equals(this.docId, documentModel.docId)
                && Objects.equals(this.projId, documentModel.projId)
                && Objects.equals(this.text, documentModel.text);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(docId, projId, text);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class DocumentModel {\n");

        sb.append("    docId: ").append(toIndentedString(docId)).append("\n");
        sb.append("    projId: ").append(toIndentedString(projId)).append("\n");
        sb.append("    text: ").append(toIndentedString(text)).append("\n");
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
