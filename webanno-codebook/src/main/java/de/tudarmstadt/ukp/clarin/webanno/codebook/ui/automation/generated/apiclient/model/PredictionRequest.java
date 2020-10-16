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
 * PredictionRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-10-02T09:35:39.209Z[GMT]")
public class PredictionRequest
    implements Serializable
{
    @SerializedName("doc")
    private DocumentModel doc = null;

    @SerializedName("codebook")
    private CodebookModel codebook = null;

    @SerializedName("mapping")
    private TagLabelMapping mapping = null;

    public PredictionRequest doc(DocumentModel doc)
    {
        this.doc = doc;
        return this;
    }

    /**
     * Get doc
     * 
     * @return doc
     **/
    @Schema(required = true, description = "")
    public DocumentModel getDoc()
    {
        return doc;
    }

    public void setDoc(DocumentModel doc)
    {
        this.doc = doc;
    }

    public PredictionRequest codebook(CodebookModel codebook)
    {
        this.codebook = codebook;
        return this;
    }

    /**
     * Get codebook
     * 
     * @return codebook
     **/
    @Schema(required = true, description = "")
    public CodebookModel getCodebook()
    {
        return codebook;
    }

    public void setCodebook(CodebookModel codebook)
    {
        this.codebook = codebook;
    }

    public PredictionRequest mapping(TagLabelMapping mapping)
    {
        this.mapping = mapping;
        return this;
    }

    /**
     * Get mapping
     * 
     * @return mapping
     **/
    @Schema(description = "")
    public TagLabelMapping getMapping()
    {
        return mapping;
    }

    public void setMapping(TagLabelMapping mapping)
    {
        this.mapping = mapping;
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
        PredictionRequest predictionRequest = (PredictionRequest) o;
        return Objects.equals(this.doc, predictionRequest.doc)
                && Objects.equals(this.codebook, predictionRequest.codebook)
                && Objects.equals(this.mapping, predictionRequest.mapping);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(doc, codebook, mapping);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class PredictionRequest {\n");

        sb.append("    doc: ").append(toIndentedString(doc)).append("\n");
        sb.append("    codebook: ").append(toIndentedString(codebook)).append("\n");
        sb.append("    mapping: ").append(toIndentedString(mapping)).append("\n");
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
