/*
 * Copyright 2020
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
 * and  Language Technology Universität Hamburg
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
package de.uhh.lt.codeanno.automation;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component(PredictionFinishedEventListener.SERVICE_NAME)
public class PredictionFinishedEventListener
    implements ApplicationListener<ApplicationEvent>
{
    public static final String SERVICE_NAME = "predictionFinishedEventListener";

    @Override
    public void onApplicationEvent(ApplicationEvent aEvent)
    {
        if (aEvent instanceof PredictionFinishedEvent) {
            PredictionFinishedEvent event = (PredictionFinishedEvent) aEvent;
            // AutomationSettingsPanel.this.predictionInProgress = false;
        }
    }
}
