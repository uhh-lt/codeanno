/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uhh.lt.codeanno.webapp.remoteapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("remote-api")
public class RemoteApiProperties
{
    private boolean enabled = false;

    public boolean isEnabled()
    {
        boolean enabledViaLegacySystemProperty = "true"
                .equals(System.getProperty("webanno.remote-api.enable"));

        return enabled || enabledViaLegacySystemProperty;
    }

    public void setEnabled(boolean aRemoteApiEnabled)
    {
        enabled = aRemoteApiEnabled;
    }
}
