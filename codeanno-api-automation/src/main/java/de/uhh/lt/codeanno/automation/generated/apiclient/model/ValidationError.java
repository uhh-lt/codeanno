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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ValidationError
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-23T17:53:55.514Z[GMT]")
public class ValidationError
    implements Serializable
{
    @SerializedName("loc")
    private List<String> loc = new ArrayList<String>();

    @SerializedName("msg")
    private String msg = null;

    @SerializedName("type")
    private String type = null;

    public ValidationError loc(List<String> loc)
    {
        this.loc = loc;
        return this;
    }

    public ValidationError addLocItem(String locItem)
    {
        this.loc.add(locItem);
        return this;
    }

    /**
     * Get loc
     * 
     * @return loc
     **/
    @Schema(required = true, description = "")
    public List<String> getLoc()
    {
        return loc;
    }

    public void setLoc(List<String> loc)
    {
        this.loc = loc;
    }

    public ValidationError msg(String msg)
    {
        this.msg = msg;
        return this;
    }

    /**
     * Get msg
     * 
     * @return msg
     **/
    @Schema(required = true, description = "")
    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public ValidationError type(String type)
    {
        this.type = type;
        return this;
    }

    /**
     * Get type
     * 
     * @return type
     **/
    @Schema(required = true, description = "")
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
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
        ValidationError validationError = (ValidationError) o;
        return Objects.equals(this.loc, validationError.loc)
                && Objects.equals(this.msg, validationError.msg)
                && Objects.equals(this.type, validationError.type);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(loc, msg, type);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class ValidationError {\n");

        sb.append("    loc: ").append(toIndentedString(loc)).append("\n");
        sb.append("    msg: ").append(toIndentedString(msg)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
