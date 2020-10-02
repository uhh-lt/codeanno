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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.GeneralApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.ModelApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.PredictionApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

public abstract class CodebookAutomationService
{
    public static final String SERVICE_NAME = "codebookAutomationService";

    private static final String CBA_API_HOST_ENV_VAR = "CBA_API_HOST";
    private static final String CBA_API_PORT_ENV_VAR = "CBA_API_PORT";

    protected final PredictionApi predictionApi;
    protected final ModelApi modelApi;
    protected final GeneralApi generalApi;

    protected boolean heartbeat;

    protected Map<Codebook, Boolean> availabilityCache;

    protected @Autowired DocumentService documentService;
    protected @Autowired ProjectService projectService;
    protected @Autowired CodebookSchemaService codebookService;

    public CodebookAutomationService() throws MalformedURLException
    {
        predictionApi = new PredictionApi();
        predictionApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        modelApi = new ModelApi();
        modelApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        generalApi = new GeneralApi();
        generalApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        this.availabilityCache = new HashMap<>();

        this.performHeartbeatCheck();
    }

    private URL getApiBaseURL() throws MalformedURLException
    {
        String host = System.getProperty(CBA_API_HOST_ENV_VAR);
        int port = Integer.parseInt(System.getProperty(CBA_API_PORT_ENV_VAR));
        return new URL("http", host, port, "");
    }

    public boolean performHeartbeatCheck()
    {
        try {
            this.heartbeat = generalApi.heartbeatHeartbeatGet().isValue();
        }
        catch (ApiException exception) {
            this.heartbeat = false;
        }
        return this.heartbeat;
    }

    protected abstract PredictionRequest buildPredictionRequest(Codebook cb, Project proj,
            SourceDocument doc);

    public abstract boolean automationIsAvailable(Codebook cb, boolean updateCache)
        throws ApiException;

    public abstract PredictionResult predictTag(Codebook cb, Project proj, SourceDocument doc)
        throws ApiException;

    public abstract ModelMetadata getModelMetadata(Codebook cb) throws ApiException;
}
