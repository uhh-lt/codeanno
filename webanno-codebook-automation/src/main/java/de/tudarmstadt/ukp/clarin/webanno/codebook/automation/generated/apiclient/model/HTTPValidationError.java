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
package de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * HTTPValidationError
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-23T17:53:55.514Z[GMT]")
public class HTTPValidationError
    implements Serializable
{
    @SerializedName("detail")
    private List<ValidationError> detail = null;

    public HTTPValidationError detail(List<ValidationError> detail)
    {
        this.detail = detail;
        return this;
    }

    public HTTPValidationError addDetailItem(ValidationError detailItem)
    {
        if (this.detail == null) {
            this.detail = new ArrayList<ValidationError>();
        }
        this.detail.add(detailItem);
        return this;
    }

    /**
     * Get detail
     * 
     * @return detail
     **/
    @Schema(description = "")
    public List<ValidationError> getDetail()
    {
        return detail;
    }

    public void setDetail(List<ValidationError> detail)
    {
        this.detail = detail;
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
        HTTPValidationError htTPValidationError = (HTTPValidationError) o;
        return Objects.equals(this.detail, htTPValidationError.detail);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(detail);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class HTTPValidationError {\n");

        sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
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
