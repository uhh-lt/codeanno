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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.CodebookModel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.DocumentModel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

@Component(CodebookAutomationService.SERVICE_NAME)
public class CodebookAutomationServiceImpl
    extends CodebookAutomationService
{

    public CodebookAutomationServiceImpl() throws MalformedURLException
    {
        super();
    }

    @Override
    protected PredictionRequest buildPredictionRequest(Codebook cb, Project proj,
            SourceDocument sdoc)
    {
        PredictionRequest request = new PredictionRequest();

        CodebookModel cbm = buildCodebookModel(cb);
        DocumentModel docm = buildDocumentModel(proj, sdoc);

        return request.codebook(cbm).doc(docm);
    }

    @Override
    public boolean automationIsAvailable(Codebook cb, boolean updateCache) throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return false;

        if (updateCache) {
            CodebookModel cbm = buildCodebookModel(cb);
            boolean available = modelApi.isAvailableModelIsAvailablePost(cbm).isValue();
            this.availabilityCache.put(cb, available);
        }
        if (this.availabilityCache.get(cb) == null)
            return false;
        else
            return this.availabilityCache.get(cb);
    }

    @Override
    public PredictionResult predictTag(Codebook cb, Project proj, SourceDocument sdoc)
        throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        PredictionRequest req = buildPredictionRequest(cb, proj, sdoc);
        return predictionApi.predictPredictionPredictPost(req);
    }

    @Override
    public ModelMetadata getModelMetadata(Codebook cb) throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        CodebookModel cbm = buildCodebookModel(cb);
        return modelApi.getMetadataModelGetMetadataPost(cbm);
    }

    private DocumentModel buildDocumentModel(Project proj, SourceDocument sdoc)
    {
        DocumentModel docm = new DocumentModel().docId(sdoc.getId().intValue())
                .projId(proj.getId().intValue());
        try {
            docm.text(new String(
                    Files.readAllBytes(documentService.getSourceDocumentFile(sdoc).toPath())));
        }
        catch (IOException e) {
            // TODO what to throw?!
            e.printStackTrace();
        }
        return docm;
    }

    private CodebookModel buildCodebookModel(Codebook cb)
    {
        CodebookModel cbm = new CodebookModel().name(cb.getUiName());
        this.codebookService.listTags(cb).forEach(t -> cbm.addTagsItem(t.getName()));
        return cbm;
    }
}
