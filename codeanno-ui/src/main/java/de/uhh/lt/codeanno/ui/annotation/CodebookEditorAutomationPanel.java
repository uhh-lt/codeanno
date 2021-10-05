/*
 * Copyright 2021
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
package de.uhh.lt.codeanno.ui.annotation;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.automation.CodebookAutomationService;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiException;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionResult;
import de.uhh.lt.codeanno.model.Codebook;

public class CodebookEditorAutomationPanel
    extends Panel
{
    private static final long serialVersionUID = 6802667883209136405L;

    private @SpringBean CodebookAutomationService codebookAutomationService;
    private @SpringBean CodebookSchemaService codebookSchemaService;
    private @SpringBean DocumentService documentService;

    private final CodebookEditorPanel parentEditorPanel;

    public CodebookEditorAutomationPanel(String mid, CodebookEditorPanel parentPanel)
    {
        super(mid);

        this.parentEditorPanel = parentPanel;

        LambdaAjaxLink automateCodebooksButton = new LambdaAjaxLink("automateCodebooksButton",
                this::actionAutomateCodebooks);
        this.add(automateCodebooksButton);
        this.setOutputMarkupId(true);
        this.setVisible(isAnyCodebookAutomatable());
    }

    private boolean isAnyCodebookAutomatable()
    {
        Project proj = parentEditorPanel.getModelObject().getProject();
        for (Codebook cb : codebookSchemaService.listCodebook(proj)) {
            try {
                if (!codebookAutomationService.isAutomationAvailable(cb, false))
                    return false;
            }
            catch (ApiException e) {
                return false;
            }
        }

        return true;
    }

    private void actionAutomateCodebooks(AjaxRequestTarget ajaxRequestTarget)
    {
        Project proj = parentEditorPanel.getModelObject().getProject();
        SourceDocument sdoc = parentEditorPanel.getModelObject().getDocument();
        List<Codebook> codebooks = codebookSchemaService.listCodebook(proj);

        codebooks.forEach((cb) -> {
            try {
                if (codebookAutomationService.isAutomationAvailable(cb, false)) {
                    PredictionResult res = codebookAutomationService.predictTag(cb, proj, sdoc);

                    codebookAutomationService.writePredictedTagToCorrectionCas(res);
                }
            }
            catch (IOException | UIMAException | AnnotationException | ApiException e) {
                // TODO what to do
                e.printStackTrace();
            }
        });
        ajaxRequestTarget.add(parentEditorPanel);
    }
}
