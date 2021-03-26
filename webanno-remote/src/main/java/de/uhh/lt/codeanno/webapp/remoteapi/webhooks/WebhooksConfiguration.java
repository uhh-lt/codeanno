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
package de.uhh.lt.codeanno.webapp.remoteapi.webhooks;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = WebhooksConfiguration.PROPERTY_PREFIX)
public class WebhooksConfiguration
{
    public static final String PROPERTY_PREFIX = "webhooks";

    private List<Webhook> globalHooks = new ArrayList<>();

    public List<Webhook> getGlobalHooks()
    {
        return globalHooks;
    }

    public void setGlobalHooks(List<Webhook> aWebhooks)
    {
        globalHooks = aWebhooks;
    }
}
