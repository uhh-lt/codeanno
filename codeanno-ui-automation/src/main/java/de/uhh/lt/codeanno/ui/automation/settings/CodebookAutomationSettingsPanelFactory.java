/*
 * Copyright 2019
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
package de.uhh.lt.codeanno.ui.automation.settings;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelFactory;

@Component
@Order(251)
public class CodebookAutomationSettingsPanelFactory
    implements ProjectSettingsPanelFactory
{

    @Override
    public String getPath()
    {
        return "/codebook_automation";
    }

    @Override
    public String getLabel()
    {
        return "Codebook Automation";
    }

    @Override
    public Panel createSettingsPanel(String aID, final IModel<Project> aProjectModel)
    {
        return new CodebookAutomationSettingsPanel(aID, aProjectModel);
    }

    @Override
    public boolean applies(Project aProject)
    {
        return WebAnnoConst.PROJECT_TYPE_AUTOMATION.equals(aProject.getMode())
                || WebAnnoConst.PROJECT_TYPE_ANNOTATION.equals(aProject.getMode());
    }
}
