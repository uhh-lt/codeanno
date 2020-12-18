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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * MultiDocumentPredictionRequest
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-18T10:42:36.085Z[GMT]")
public class MultiDocumentPredictionRequest implements Serializable
{
    @SerializedName("docs")
    private List<DocumentDTO> docs = new ArrayList<DocumentDTO>();

    @SerializedName("codebook")
    private CodebookDTO codebook = null;

    @SerializedName("mapping")
    private TagLabelMapping mapping = null;

    @SerializedName("model_version")
    private String modelVersion = "default";

    public MultiDocumentPredictionRequest docs(List<DocumentDTO> docs)
    {
        this.docs = docs;
        return this;
    }

    public MultiDocumentPredictionRequest addDocsItem(DocumentDTO docsItem)
    {
        this.docs.add(docsItem);
        return this;
    }

    /**
     * Get docs
     * 
     * @return docs
     **/
    @Schema(required = true, description = "")
    public List<DocumentDTO> getDocs()
    {
        return docs;
    }

    public void setDocs(List<DocumentDTO> docs)
    {
        this.docs = docs;
    }

    public MultiDocumentPredictionRequest codebook(CodebookDTO codebook)
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
    public CodebookDTO getCodebook()
    {
        return codebook;
    }

    public void setCodebook(CodebookDTO codebook)
    {
        this.codebook = codebook;
    }

    public MultiDocumentPredictionRequest mapping(TagLabelMapping mapping)
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

    public MultiDocumentPredictionRequest modelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
        return this;
    }

    /**
     * Get modelVersion
     * 
     * @return modelVersion
     **/
    @Schema(description = "")
    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
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
        MultiDocumentPredictionRequest multiDocumentPredictionRequest = (MultiDocumentPredictionRequest) o;
        return Objects.equals(this.docs, multiDocumentPredictionRequest.docs)
                && Objects.equals(this.codebook, multiDocumentPredictionRequest.codebook)
                && Objects.equals(this.mapping, multiDocumentPredictionRequest.mapping)
                && Objects.equals(this.modelVersion, multiDocumentPredictionRequest.modelVersion);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(docs, codebook, mapping, modelVersion);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class MultiDocumentPredictionRequest {\n");

        sb.append("    docs: ").append(toIndentedString(docs)).append("\n");
        sb.append("    codebook: ").append(toIndentedString(codebook)).append("\n");
        sb.append("    mapping: ").append(toIndentedString(mapping)).append("\n");
        sb.append("    modelVersion: ").append(toIndentedString(modelVersion)).append("\n");
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
