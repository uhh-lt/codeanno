/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt  
 *  and Language Technology Group  Universität Hamburg 
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
package de.uhh.lt.codeanno.api.service;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.feature.FeatureSupportRegistry;
import de.tudarmstadt.ukp.clarin.webanno.model.TagSet;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;
import de.uhh.lt.codeanno.model.CodebookFeature;

public class UimaStringCodebookTraitsEditor
    extends Panel
{
    private static final long serialVersionUID = 2129000875921279514L;

    private @SpringBean AnnotationSchemaService annotationService;
    private @SpringBean FeatureSupportRegistry featureSupportRegistry;
    
    public UimaStringCodebookTraitsEditor(String aId, IModel<CodebookFeature> aFeature)
    {
        super(aId);
        
        add(new DropDownChoice<TagSet>("tagset")
        {
            private static final long serialVersionUID = -6705445053442011120L;
            {
                setOutputMarkupPlaceholderTag(true);
                setOutputMarkupId(true);
                setChoiceRenderer(new ChoiceRenderer<>("name"));
                setNullValid(true);
                setModel(PropertyModel.of(aFeature, "tagset"));
                setChoices(LambdaModel.of(() -> annotationService
                        .listTagSets(aFeature.getObject().getProject())));
            }
        });
    }
}
