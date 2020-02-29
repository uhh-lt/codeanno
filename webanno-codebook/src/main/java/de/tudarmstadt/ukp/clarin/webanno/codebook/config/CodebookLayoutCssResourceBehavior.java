/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt  
 * and Language Technology Group  Universität Hamburg 
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
package de.tudarmstadt.ukp.clarin.webanno.codebook.config;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;

public class CodebookLayoutCssResourceBehavior
    extends Behavior
{
    private static final long serialVersionUID = -2794888027435574063L;
    private static final CodebookLayoutCssResourceBehavior INSTANCE = 
            new CodebookLayoutCssResourceBehavior();

    public static CodebookLayoutCssResourceBehavior get()
    {
        return INSTANCE;
    }

    @Override
    public void renderHead(Component aComponent, IHeaderResponse aResponse)
    {
        aResponse.render(CssHeaderItem.forReference(CodebookLayoutCssReference.get()));
    }
}