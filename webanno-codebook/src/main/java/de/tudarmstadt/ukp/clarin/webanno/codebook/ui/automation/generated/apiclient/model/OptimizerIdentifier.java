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

import java.io.IOException;
import java.io.Serializable;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Possible tf.optimizer check out https://www.tensorflow.org/api_docs/python/tf/keras/optimizers
 * for details!
 */
@JsonAdapter(OptimizerIdentifier.Adapter.class)
public enum OptimizerIdentifier implements Serializable
{
    ADAM("Adam"), SGD("SGD"), ADAGRAD("Adagrad"), RMSPROP("RMSProp"), FTRL("Ftrl");

    private final String value;

    OptimizerIdentifier(String value)
    {
        this.value = value;
    }

    public static OptimizerIdentifier fromValue(String text)
    {
        for (OptimizerIdentifier b : OptimizerIdentifier.values()) {
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
        extends TypeAdapter<OptimizerIdentifier>
    {
        @Override
        public void write(final JsonWriter jsonWriter, final OptimizerIdentifier enumeration)
            throws IOException
        {
            jsonWriter.value(enumeration.getValue());
        }

        @Override
        public OptimizerIdentifier read(final JsonReader jsonReader) throws IOException
        {
            String value = jsonReader.nextString();
            return OptimizerIdentifier.fromValue(String.valueOf(value));
        }
    }
}
