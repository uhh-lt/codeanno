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

import java.io.IOException;
import java.io.Serializable;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Possible activation functions
 */
@JsonAdapter(ActivationFunctionIdentifier.Adapter.class)
public enum ActivationFunctionIdentifier
    implements
    Serializable
{
    RELU("relu"), SIGMOID("sigmoid"), TANH("tanh"), EXPONENTIAL("exponential");

    private final String value;

    ActivationFunctionIdentifier(String value)
    {
        this.value = value;
    }

    public static ActivationFunctionIdentifier fromValue(String text)
    {
        for (ActivationFunctionIdentifier b : ActivationFunctionIdentifier.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    public static class Adapter
        extends TypeAdapter<ActivationFunctionIdentifier>
    {
        @Override
        public void write(final JsonWriter jsonWriter,
                final ActivationFunctionIdentifier enumeration)
            throws IOException
        {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public ActivationFunctionIdentifier read(final JsonReader jsonReader) throws IOException
        {
            String value = jsonReader.nextString();
            return ActivationFunctionIdentifier.fromValue(String.valueOf(value));
        }
    }
}
