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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.squareup.okhttp.Call;

import de.tudarmstadt.ukp.clarin.webanno.api.CorrectionDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.uhh.lt.codeanno.api.CodebookConstants;
import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiException;
import de.uhh.lt.codeanno.automation.generated.apiclient.api.GeneralApi;
import de.uhh.lt.codeanno.automation.generated.apiclient.api.MappingApi;
import de.uhh.lt.codeanno.automation.generated.apiclient.api.ModelApi;
import de.uhh.lt.codeanno.automation.generated.apiclient.api.PredictionApi;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.DocumentDTO;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.ModelMetadata;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.MultiDocumentPredictionRequest;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.MultiDocumentPredictionResult;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionRequest;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionResult;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.TagLabelMapping;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;

@Component(CodebookAutomationService.SERVICE_NAME)
public class CodebookAutomationServiceImpl
    implements CodebookAutomationService
{
    private final static Logger logger = LoggerFactory
            .getLogger(CodebookAutomationServiceImpl.class);
    private static final String DEFAULT_VERSION = "default";
    // 1 minute time out.. just in case (could take longer when deployed)
    private static final Integer API_CALL_TIMEOUT_S = 60 * 1000;
    private static final String CBA_API_HOST_ENV_VAR = "CBA_API_HOST";
    private static final String CBA_API_PORT_ENV_VAR = "CBA_API_PORT";
    private final PredictionApi predictionApi;
    private final ModelApi modelApi;
    private final GeneralApi generalApi;
    private final MappingApi mappingApi;
    // CodebookName -> Caller (AutomationSettingsPanel only for now)
    private final ConcurrentHashMap<String, Object> predictionInProgress;
    private final Object lock;
    private final Map<Codebook, Boolean> availabilityCache;
    private final DocumentService documentService;
    private final ProjectService projectService;
    private final CodebookSchemaService codebookService;
    private final CorrectionDocumentService correctionDocumentService;
    private final UserDao userService;
    private final ApplicationEventPublisher eventPublisher;
    private boolean heartbeat;

    @Autowired
    public CodebookAutomationServiceImpl(DocumentService documentService,
            ProjectService projectService, CodebookSchemaService codebookService,
            CorrectionDocumentService correctionDocumentService, UserDao userService,
            ApplicationEventPublisher eventPublisher)
        throws MalformedURLException
    {
        String baseUrl = this.getApiBaseURL().toString();
        logger.info("Using CBA API base URL: " + baseUrl);

        predictionApi = new PredictionApi();
        predictionApi.getApiClient().setBasePath(baseUrl);
        predictionApi.getApiClient().setConnectTimeout(API_CALL_TIMEOUT_S);
        predictionApi.getApiClient().setReadTimeout(API_CALL_TIMEOUT_S);
        predictionApi.getApiClient().setWriteTimeout(API_CALL_TIMEOUT_S);

        modelApi = new ModelApi();
        modelApi.getApiClient().setBasePath(baseUrl);
        modelApi.getApiClient().setConnectTimeout(API_CALL_TIMEOUT_S);
        modelApi.getApiClient().setReadTimeout(API_CALL_TIMEOUT_S);
        modelApi.getApiClient().setWriteTimeout(API_CALL_TIMEOUT_S);

        generalApi = new GeneralApi();
        generalApi.getApiClient().setBasePath(baseUrl);
        generalApi.getApiClient().setConnectTimeout(API_CALL_TIMEOUT_S);
        generalApi.getApiClient().setReadTimeout(API_CALL_TIMEOUT_S);
        generalApi.getApiClient().setWriteTimeout(API_CALL_TIMEOUT_S);

        mappingApi = new MappingApi();
        mappingApi.getApiClient().setBasePath(baseUrl);
        mappingApi.getApiClient().setConnectTimeout(API_CALL_TIMEOUT_S);
        mappingApi.getApiClient().setReadTimeout(API_CALL_TIMEOUT_S);
        mappingApi.getApiClient().setWriteTimeout(API_CALL_TIMEOUT_S);

        this.availabilityCache = new HashMap<>();
        this.predictionInProgress = new ConcurrentHashMap<>();

        if (!this.performHeartbeatCheck())
            logger.warn("Cannot reach Codebook Automation API");
        this.documentService = documentService;
        this.projectService = projectService;
        this.codebookService = codebookService;
        this.correctionDocumentService = correctionDocumentService;
        this.userService = userService;
        this.eventPublisher = eventPublisher;

        this.lock = new Object();
    }

    private URL getApiBaseURL() throws MalformedURLException
    {
        String host = System.getenv(CBA_API_HOST_ENV_VAR);
        int port = Integer.parseInt(System.getenv(CBA_API_PORT_ENV_VAR));
        return new URL("http", host, port, "");
    }

    @Override
    public synchronized boolean performHeartbeatCheck()
    {
        try {
            this.heartbeat = generalApi.heartbeatHeartbeatGet().isValue();
        }
        catch (ApiException exception) {
            this.heartbeat = false;
        }
        return this.heartbeat;
    }

    @Override
    public TagLabelMapping loadTagLabelMapping(Codebook cb, String modelVersion)
    {
        if (cb == null || modelVersion.equals(""))
            return null;
        try {
            return this.mappingApi.getMappingGetGet(cb.getUiName(), modelVersion);
        }
        catch (ApiException e) {
            e.printStackTrace();
            // if the mapping cannot be retrieved (most probably due to 404 by CBA API), create a
            // new one
            return new TagLabelMapping().cbName(cb.getUiName()).version(modelVersion);
        }
    }

    @Override
    public void updateTagLabelMapping(Codebook cb, String modelVersion, String tag, String label)
        throws ApiException
    {
        TagLabelMapping mapping = this.loadTagLabelMapping(cb, modelVersion);
        mapping.getMap().put(tag, label);
        this.mappingApi.updateMappingUpdatePost(mapping, cb.getUiName());
    }

    @Override
    public void registerTagLabelMapping(TagLabelMapping mapping) throws ApiException
    {
        this.mappingApi.registerMappingRegisterPut(mapping);
    }

    @Override
    public void unregisterTagLabelMapping(Codebook cb, String modelVersion) throws ApiException
    {
        this.mappingApi.unregisterMappingUnregisterDelete(cb.getUiName(), modelVersion);
    }

    @Override
    public boolean isPredictionInProgress(Codebook cb, Object caller)
    {
        if (cb == null)
            return false;
        return this.predictionInProgress.get(cb.getUiName()) != null;
    }

    @Override
    public void addToPredictionInProgress(PredictionRequest req, Object caller)
    {
        this.predictionInProgress.putIfAbsent(req.getCbName(), caller);
    }

    @Override
    public void addToPredictionInProgress(MultiDocumentPredictionRequest req, Object caller)
    {
        this.predictionInProgress.putIfAbsent(req.getCbName(), caller);
    }

    @Override
    public void removeFromPredictionInProgress(MultiDocumentPredictionResult result)
    {
        Object caller = this.predictionInProgress.remove(result.getCodebookName());
        // FIXME event doesn't update WicketPage..
        eventPublisher.publishEvent(new PredictionFinishedEvent(this, result.getCodebookName()));
    }

    @Override
    public boolean isAutomationAvailable(Codebook cb, boolean updateCache) throws ApiException
    {
        return this.isAutomationAvailable(cb, DEFAULT_VERSION, updateCache);
    }

    @Override
    public void removeFromPredictionInProgress(PredictionResult result)
    {
        Object caller = this.predictionInProgress.remove(result.getCodebookName());
    }

    @Override
    public boolean isAutomationAvailable(Codebook cb, String modelVersion, boolean updateCache)
        throws ApiException
    {
        if (!heartbeat || cb == null)
            return false;

        if (updateCache) {
            boolean available = modelApi.isAvailableModelAvailableGet(cb.getUiName(), modelVersion)
                    .isValue();
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
        return this.predictTag(cb, proj, sdoc, DEFAULT_VERSION);
    }

    @Override
    public PredictionResult predictTag(Codebook cb, Project proj, SourceDocument sdoc,
            String modelVersion)
        throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting synchronous Codebook Tag prediction for " + cb.getName()
                + " of Document " + sdoc.getId());

        PredictionRequest req = buildPredictionRequest(cb, proj, sdoc, modelVersion);
        return this.predictionApi.predictPredictionSinglePost(req);
    }

    @Override
    public synchronized Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc)
        throws ApiException
    {
        return predictTagAsync(cb, proj, sdoc, DEFAULT_VERSION, null);
    }

    @Override
    public synchronized Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc,
            String modelVersion, Object caller)
        throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting asynchronous Codebook Tag prediction for " + cb.getName()
                + " of Document " + sdoc.getId());

        PredictionRequest req = buildPredictionRequest(cb, proj, sdoc, modelVersion);

        // add to to inProgress TODO add caller
        // this.addToPredictionInProgress(cb, caller);

        return predictionApi.predictPredictionSinglePostAsync(req,
                new PersistPredResultToCasCallback(this));
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj) throws ApiException
    {
        return this.predictTagsAsync(cb, proj, DEFAULT_VERSION);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, String modelVersion) throws ApiException
    {
        return this.predictTagsAsync(cb, proj, modelVersion, null);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, String modelVersion, Object caller)
        throws ApiException
    {
        return this.predictTagsAsync(cb, proj, documentService.listSourceDocuments(proj),
                modelVersion, caller);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> sdocs)
        throws ApiException
    {
        return predictTagsAsync(cb, proj, sdocs, DEFAULT_VERSION, null);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> docs,
            String modelVersion)
        throws ApiException
    {
        return predictTagsAsync(cb, proj, docs, modelVersion, null);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> sdocs,
            String modelVersion, Object caller)
        throws ApiException
    {

        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting asynchronous Codebook Tag prediction for " + cb.getName()
                + " for Documents: "
                + sdocs.stream().map(SourceDocument::getId).collect(Collectors.toList()));

        MultiDocumentPredictionRequest req = buildMultiDocPredictionRequest(cb, proj, sdocs,
                modelVersion);

        // add to to inProgress
        this.addToPredictionInProgress(req, caller);

        return predictionApi.predictMultiPredictionMultiplePostAsync(req,
                new PersistMultiPredResultToCasCallback(this));
    }

    @Override
    public ModelMetadata getModelMetadata(Codebook cb) throws ApiException
    {
        return this.getModelMetadata(cb, DEFAULT_VERSION);
    }

    @Override
    public ModelMetadata getModelMetadata(Codebook cb, String modelVersion) throws ApiException
    {
        if (!heartbeat)
            return null;

        return modelApi.getMetadataModelMetadataGet(cb.getUiName(), modelVersion);
    }

    @Override
    public List<ModelMetadata> getAvailableModels(Codebook cb) throws ApiException
    {
        if (!heartbeat || cb == null)
            return Collections.emptyList();
        return modelApi.listModelsModelListGet(cb.getUiName());
    }

    private DocumentDTO buildDocumentModel(Project proj, SourceDocument sdoc)
    {
        DocumentDTO docm = new DocumentDTO().docId(sdoc.getId().intValue())
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

    private Project getProject(PredictionResult result)
    {
        if (result == null || result.getProjId() == null)
            return null;

        return projectService.getProject(result.getProjId());
    }

    private Project getProject(MultiDocumentPredictionResult result)
    {
        if (result == null || result.getProjId() == null)
            return null;

        return projectService.getProject(result.getProjId());
    }

    private SourceDocument getSourceDocument(PredictionResult result)
    {
        if (result == null || result.getDocId() == null || result.getProjId() == null)
            return null;

        return documentService.getSourceDocument(result.getProjId(), result.getDocId());
    }

    private Map<SourceDocument, String> getSourceDocumentsAndTags(
            MultiDocumentPredictionResult result)
    {
        if (result == null || result.getProjId() == null)
            return null;

        Map<SourceDocument, String> predictions = new HashMap<>();

        result.getPredictedTags().forEach((docId, predTag) -> {
            SourceDocument sdoc = documentService.getSourceDocument(result.getProjId(),
                    Long.parseLong(docId));
            predictions.put(sdoc, predTag);
        });

        return predictions;
    }

    private Codebook getCodebook(PredictionResult result)
    {
        if (result == null || result.getCodebookName() == null
                || result.getCodebookName().isEmpty())
            return null;

        Project project = this.getProject(result);

        return codebookService.getCodeBook(
                CodebookConstants.CODEBOOK_NAME_PREFIX + result.getCodebookName(), project);
    }

    private Codebook getCodebook(MultiDocumentPredictionResult result)
    {
        if (result == null || result.getCodebookName() == null
                || result.getCodebookName().isEmpty())
            return null;

        Project project = this.getProject(result);

        return codebookService.getCodeBook(
                CodebookConstants.CODEBOOK_NAME_PREFIX + result.getCodebookName(), project);
    }

    private void writeCodebookTagToCorrectionCas(SourceDocument sdoc, Codebook cb, String tagValue)
        throws IOException, UIMAException, AnnotationException
    {
        synchronized (lock) {
            CAS correctionCas = this.readOrCreateCorrectionCas(sdoc, true);

            // FIXME we really need to get rid of the cb features to increase code
            // understandability a lot
            // write to CAS
            CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
            CodebookCasAdapter adapter = new CodebookCasAdapter(cb);
            AnnotationFS existingFs = adapter.getExistingFs(correctionCas);
            int annoId = existingFs != null ? WebAnnoCasUtil.getAddr(existingFs)
                    : adapter.add(correctionCas);
            adapter.setFeatureValue(correctionCas, feature, annoId, tagValue);

            // persist the changes in the CAS
            correctionDocumentService.upgradeCorrectionCas(correctionCas, sdoc);
            correctionDocumentService.writeCorrectionCas(correctionCas, sdoc);

            logger.info("Successfully wrote predicted Codebook Tag for Codebook <" + cb.getName()
                    + "> and Document ID=<" + sdoc.getId() + "> to Correction CAS!");
        }
    }

    @Override
    public void writePredictedTagToCorrectionCas(PredictionResult result)
        throws IOException, UIMAException, AnnotationException
    {
        SourceDocument sdoc = this.getSourceDocument(result);
        Codebook cb = this.getCodebook(result);
        String predTag = result.getPredictedTag();

        this.writeCodebookTagToCorrectionCas(sdoc, cb, predTag);

        this.removeFromPredictionInProgress(result);
    }

    @Override
    public void writePredictedTagsToCorrectionCas(MultiDocumentPredictionResult result)
    {
        Map<SourceDocument, String> predictedTags = this.getSourceDocumentsAndTags(result);
        Codebook cb = this.getCodebook(result);

        predictedTags.forEach((sdoc, predTag) -> {
            try {
                this.writeCodebookTagToCorrectionCas(sdoc, cb, predTag);
            }
            catch (IOException | UIMAException | AnnotationException e) {
                logger.error("Could not persist predicted tag of Codebook <" + cb.getName() + "> "
                        + "to CAS");
                // TODO what to throw?!
                e.printStackTrace();
            }
        });

        this.removeFromPredictionInProgress(result);
    }

    @Override
    public String readPredictedTagValueFromCorrectionCas(Codebook cb, SourceDocument sdoc)
        throws IOException
    {
        CAS correctionCas = correctionDocumentService.readCorrectionCas(sdoc);

        CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
        CodebookCasAdapter adapter = new CodebookCasAdapter(cb);
        return (String) adapter.getExistingCodeValue(correctionCas, feature);
    }

    @Override
    public CAS readOrCreateCorrectionCas(SourceDocument sdoc, boolean upgrade)
        throws IOException, UIMAException
    {
        // Read the correction CAS - if it does not exist yet, from the initial CAS
        CAS correctionCas;
        if (correctionDocumentService.existsCorrectionCas(sdoc)) {
            correctionCas = correctionDocumentService.readCorrectionCas(sdoc);
        }
        else {
            correctionCas = documentService.createOrReadInitialCas(sdoc);
        }

        // Update the CAS
        correctionDocumentService.upgradeCorrectionCas(correctionCas, sdoc);
        // save it
        correctionDocumentService.writeCorrectionCas(correctionCas, sdoc);

        return correctionCas;
    }

    private PredictionRequest buildPredictionRequest(Codebook cb, Project proj, SourceDocument sdoc,
            String modelVersion)
    {
        PredictionRequest request = new PredictionRequest();

        DocumentDTO docm = buildDocumentModel(proj, sdoc);

        TagLabelMapping mapping = this.loadTagLabelMapping(cb, modelVersion);

        return request.cbName(cb.getUiName()).doc(docm).mapping(mapping).modelVersion(modelVersion);
    }

    private MultiDocumentPredictionRequest buildMultiDocPredictionRequest(Codebook cb, Project proj,
            List<SourceDocument> sdocs, String modelVersion)
    {
        MultiDocumentPredictionRequest req = new MultiDocumentPredictionRequest();

        List<DocumentDTO> docModels = sdocs.stream()
                .map(sourceDocument -> this.buildDocumentModel(proj, sourceDocument))
                .collect(Collectors.toList());

        TagLabelMapping mapping = this.loadTagLabelMapping(cb, modelVersion);

        return req.cbName(cb.getUiName()).docs(docModels).mapping(mapping)
                .modelVersion(modelVersion);
    }

}
