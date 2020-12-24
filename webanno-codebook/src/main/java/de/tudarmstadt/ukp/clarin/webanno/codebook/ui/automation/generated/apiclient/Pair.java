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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2020-12-23T17:53:55.514Z[GMT]")
public class Pair
{
    private String name = "";
    private String value = "";

    public Pair(String name, String value)
    {
        setName(name);
        setValue(value);
    }

    public String getName()
    {
        return this.name;
    }

    private void setName(String name)
    {
        if (!isValidString(name))
            return;

        this.name = name;
    }

    public String getValue()
    {
        return this.value;
    }

    private void setValue(String value)
    {
        if (!isValidString(value))
            return;

        this.value = value;
    }

    private boolean isValidString(String arg)
    {
        if (arg == null)
            return false;
        if (arg.trim().isEmpty())
            return false;

        return true;
    }
}
