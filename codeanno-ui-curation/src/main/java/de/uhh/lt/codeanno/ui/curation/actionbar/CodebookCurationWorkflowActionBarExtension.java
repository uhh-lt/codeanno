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
package de.uhh.lt.codeanno.ui.curation.actionbar;

import static java.lang.Integer.MAX_VALUE;

import org.apache.wicket.markup.html.panel.Panel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.actionbar.ActionBarExtension;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.page.AnnotationPageBase;
import de.uhh.lt.codeanno.ui.curation.CodebookCurationPage;

@Order(1000)
@Component
public class CodebookCurationWorkflowActionBarExtension
    implements ActionBarExtension
{
    @Override
    public String getRole()
    {
        // FIXME dirty hack in order to avoid circular dependency
        return "de.tudarmstadt.ukp.clarin.webanno.ui.annotation.DefaultWorkflowActionBarExtension";
        // DefaultWorkflowActionBarExtension.class.getName();
    }

    @Override
    public Panel createActionBarItem(String aId, AnnotationPageBase aPage)
    {
        return new CodebookCurationWorkflowActionBarItemGroup(aId, aPage);
    }

    @Override
    public int getPriority()
    {
        return MAX_VALUE;
    }

    @Override
    public boolean accepts(AnnotationPageBase aPage)
    {
        return aPage instanceof CodebookCurationPage;
    }
}
