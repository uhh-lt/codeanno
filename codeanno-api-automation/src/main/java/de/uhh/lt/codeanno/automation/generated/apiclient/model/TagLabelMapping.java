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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * TagLabelMapping
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-27T14:25:41.880Z[GMT]")
public class TagLabelMapping
    implements Serializable
{
    @SerializedName("cb_name")
    private String cbName = null;

    @SerializedName("version")
    private String version = "default";

    @SerializedName("map")
    private Map<String, String> map = new HashMap<String, String>();

    public TagLabelMapping cbName(String cbName)
    {
        this.cbName = cbName;
        return this;
    }

    /**
     * The name of the related Codebook
     * 
     * @return cbName
     **/
    @Schema(required = true, description = "The name of the related Codebook")
    public String getCbName()
    {
        return cbName;
    }

    public void setCbName(String cbName)
    {
        this.cbName = cbName;
    }

    public TagLabelMapping version(String version)
    {
        this.version = version;
        return this;
    }

    /**
     * The version of the related model
     * 
     * @return version
     **/
    @Schema(example = "default", description = "The version of the related model")
    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public TagLabelMapping map(Map<String, String> map)
    {
        this.map = map;
        return this;
    }

    public TagLabelMapping putMapItem(String key, String mapItem)
    {
        this.map.put(key, mapItem);
        return this;
    }

    /**
     * Get map
     * 
     * @return map
     **/
    @Schema(required = true, description = "")
    public Map<String, String> getMap()
    {
        return map;
    }

    public void setMap(Map<String, String> map)
    {
        this.map = map;
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
        TagLabelMapping tagLabelMapping = (TagLabelMapping) o;
        return Objects.equals(this.cbName, tagLabelMapping.cbName)
                && Objects.equals(this.version, tagLabelMapping.version)
                && Objects.equals(this.map, tagLabelMapping.map);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(cbName, version, map);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("class TagLabelMapping {\n");

        sb.append("    cbName: ").append(toIndentedString(cbName)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    map: ").append(toIndentedString(map)).append("\n");
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
