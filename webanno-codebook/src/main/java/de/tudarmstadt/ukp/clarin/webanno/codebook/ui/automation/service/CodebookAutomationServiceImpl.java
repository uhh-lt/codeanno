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

import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.updateDocumentTimestampAfterWrite;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.verifyAndUpdateDocumentTimestamp;
import static de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS;

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
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.codebook.CodebookConst;
import de.tudarmstadt.ukp.clarin.webanno.codebook.adapter.CodebookCasAdapter;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookFeature;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.GeneralApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.ModelApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api.PredictionApi;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.CodebookModel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.DocumentModel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.MultiDocumentPredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.MultiDocumentPredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.TagLabelMapping;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;

@Component(CodebookAutomationService.SERVICE_NAME)
public class CodebookAutomationServiceImpl
    implements CodebookAutomationService
{
    private final static Logger logger = LoggerFactory
            .getLogger(CodebookAutomationServiceImpl.class);

    private final PredictionApi predictionApi;
    private final ModelApi modelApi;
    private final GeneralApi generalApi;

    // CodebookName -> (MultiDoc)PredictionRequest
    private final ConcurrentHashMap<String, Object> predictionInProgress;
    private final Object lock;

    private final Map<Codebook, Boolean> availabilityCache;
    private final Map<Codebook, TagLabelMapping> tagLabelMappings;

    private final DocumentService documentService;
    private final ProjectService projectService;
    private final CodebookSchemaService codebookService;
    private final CorrectionDocumentService correctionDocumentService;
    private final UserDao userService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String DEFAULT_VERSION = "default";

    private boolean heartbeat;

    @Autowired
    public CodebookAutomationServiceImpl(DocumentService documentService,
            ProjectService projectService, CodebookSchemaService codebookService,
            CorrectionDocumentService correctionDocumentService, UserDao userService,
            ApplicationEventPublisher eventPublisher)
        throws MalformedURLException
    {
        predictionApi = new PredictionApi();
        predictionApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        modelApi = new ModelApi();
        modelApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        generalApi = new GeneralApi();
        generalApi.getApiClient().setBasePath(this.getApiBaseURL().toString());

        this.availabilityCache = new HashMap<>();
        this.tagLabelMappings = new HashMap<>();
        this.predictionInProgress = new ConcurrentHashMap<>();

        this.performHeartbeatCheck();
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
        String host = System.getProperty(CBA_API_HOST_ENV_VAR);
        int port = Integer.parseInt(System.getProperty(CBA_API_PORT_ENV_VAR));
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
    public void updateTagLabelMapping(Codebook cb, String tag, String label)
    {
        this.tagLabelMappings.putIfAbsent(cb, new TagLabelMapping());
        this.tagLabelMappings.computeIfPresent(cb, (codebook, tagLabelMapping) -> {
            tagLabelMapping.getMap().put(tag, label);
            return tagLabelMapping;
        });
    }

    @Override
    public boolean isPredictionInProgress(Codebook cb)
    {
        if (cb == null)
            return false;
        return this.predictionInProgress.get(cb.getUiName()) != null;
    }

    @Override
    public void addToPredictionInProgress(PredictionRequest req)
    {
        this.predictionInProgress.putIfAbsent(req.getCodebook().getName(), req);
    }

    @Override
    public void addToPredictionInProgress(MultiDocumentPredictionRequest req)
    {
        this.predictionInProgress.putIfAbsent(req.getCodebook().getName(), req);
    }

    @Override
    public void removeFromPredictionInProgress(PredictionResult result)
    {
        this.predictionInProgress.remove(result.getCodebookName());
    }

    @Override
    public void removeFromPredictionInProgress(MultiDocumentPredictionResult result)
    {
        this.predictionInProgress.remove(result.getCodebookName());
        eventPublisher.publishEvent(new PredictionFinishedEvent(this, result.getCodebookName()));
    }

    @Override
    public boolean isAutomationAvailable(Codebook cb, boolean updateCache) throws ApiException
    {
        if (!heartbeat || cb == null)
            return false;

        if (updateCache) {
            CodebookModel cbm = buildCodebookModel(cb);
            boolean available = modelApi.isAvailableModelIsAvailablePost(cbm,
                                                                         DEFAULT_VERSION).isValue();
            this.availabilityCache.put(cb, available);
        }
        if (this.availabilityCache.get(cb) == null)
            return false;
        else
            return this.availabilityCache.get(cb);
    }

    @Override
    public synchronized Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc,
            String userName)
        throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting asynchronous Codebook Tag prediction for " + cb.getName()
                + " of Document " + sdoc.getId());

        PredictionRequest req = buildPredictionRequest(cb, proj, sdoc);

        // add to to inProgress
        this.addToPredictionInProgress(req);

        return predictionApi.predictPredictionPredictPostAsync(req,
                new PersistPredResultToCasCallback(this, userName));
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, String userName) throws ApiException
    {
        return this.predictTagsAsync(cb, proj, documentService.listSourceDocuments(proj), userName);
    }

    @Override
    public Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> sdocs,
            String userName)
        throws ApiException
    {

        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting asynchronous Codebook Tag prediction for " + cb.getName()
                + " for Documents: "
                + sdocs.stream().map(SourceDocument::getId).collect(Collectors.toList()));

        MultiDocumentPredictionRequest req = buildMultiDocPredictionRequest(cb, proj, sdocs);

        // add to to inProgress
        this.addToPredictionInProgress(req);

        return predictionApi.predictMultiPredictionPredictMultiPostAsync(req,
                new PersistMultiPredResultToCasCallback(this, userName));
    }

    @Override
    public ModelMetadata getModelMetadata(Codebook cb) throws ApiException
    {
        if (!heartbeat)
            return null;

        CodebookModel cbm = buildCodebookModel(cb);
        return modelApi.getMetadataModelGetMetadataPost(cbm, DEFAULT_VERSION);
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
                CodebookConst.CODEBOOK_NAME_PREFIX + result.getCodebookName(), project);
    }

    private Codebook getCodebook(MultiDocumentPredictionResult result)
    {
        if (result == null || result.getCodebookName() == null
                || result.getCodebookName().isEmpty())
            return null;

        Project project = this.getProject(result);

        return codebookService.getCodeBook(
                CodebookConst.CODEBOOK_NAME_PREFIX + result.getCodebookName(), project);
    }

    private void writeCodebookTagToCorrectionCas(SourceDocument sdoc, Project project, Codebook cb,
            String predTag, String userName)
        throws IOException, UIMAException, AnnotationException
    {
        synchronized (lock) {

            AnnotatorState state = this.createAnnotatorState(sdoc, project, userName);
            CAS correctionCas = this.readOrCreateCorrectionCas(state, true);

            // FIXME we really need to get rid of the cb features to increase code
            // understandability a lot
            CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
            CodebookCasAdapter adapter = new CodebookCasAdapter(cb);
            AnnotationFS existingFs = adapter.getExistingFs(correctionCas);
            int annoId = existingFs != null ? WebAnnoCasUtil.getAddr(existingFs)
                    : adapter.add(correctionCas);
            adapter.setFeatureValue(correctionCas, feature, annoId, predTag);

            correctionDocumentService.writeCorrectionCas(correctionCas, state.getDocument());
            updateDocumentTimestampAfterWrite(state,
                    correctionDocumentService.getCorrectionCasTimestamp(state.getDocument()));

            logger.info("Successfully wrote predicted Codebook Tag for Codebook <" + cb.getName()
                    + "> and Document ID=<" + state.getDocument().getId() + "> to Correction CAS!");
        }
    }

    @Override
    public void writePredictedTagToCorrectionCas(PredictionResult result, String userName)
        throws IOException, UIMAException, AnnotationException
    {
        SourceDocument sdoc = this.getSourceDocument(result);
        Project project = this.getProject(result);
        Codebook cb = this.getCodebook(result);
        String predTag = result.getPredictedTag();

        this.writeCodebookTagToCorrectionCas(sdoc, project, cb, predTag, userName);

        this.removeFromPredictionInProgress(result);
    }

    @Override
    public void writePredictedTagsToCorrectionCas(MultiDocumentPredictionResult result,
            String userName)
    {
        Map<SourceDocument, String> predictedTags = this.getSourceDocumentsAndTags(result);
        Project project = this.getProject(result);
        Codebook cb = this.getCodebook(result);

        predictedTags.forEach((sdoc, predTag) -> {
            try {
                this.writeCodebookTagToCorrectionCas(sdoc, project, cb, predTag, userName);
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

    private AnnotatorState createAnnotatorState(SourceDocument doc, Project project,
            String userName)
        throws IOException
    {
        AnnotatorState state = new AnnotatorStateImpl(Mode.CORRECTION);
        state.setDocument(doc, Collections.singletonList(doc));
        state.setProject(project);

        // FIXME cannot get the current user name here because the Spring SecurityContext is
        // ThreadLocal only so we cant always access the current user (e.g. in async prediction)
        // state.setUser(userService.getCurrentUser());
        state.setUser(userService.get(userName));

        // If we have a timestamp, then use it to detect if there was a concurrent access
        verifyAndUpdateDocumentTimestamp(state, documentService
                .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername()));

        return state;
    }

    @Override
    public CAS readOrCreateCorrectionCas(AnnotatorState state, boolean upgrade)
        throws IOException, UIMAException
    {
        SourceDocument doc = state.getDocument();
        try {
            CAS correctionCas = correctionDocumentService.readCorrectionCas(doc);
            if (upgrade) {
                correctionDocumentService.upgradeCorrectionCas(correctionCas, doc);
                correctionDocumentService.writeCorrectionCas(correctionCas, doc);
                updateDocumentTimestampAfterWrite(state,
                        correctionDocumentService.getCorrectionCasTimestamp(doc));
            }
            return correctionCas;
        }
        catch (Exception e) {
            return initCorrectionCas(state);
        }
    }

    private CAS initCorrectionCas(AnnotatorState state) throws IOException, UIMAException
    {
        SourceDocument doc = state.getDocument();
        User user = state.getUser();
        if (user == null)
            try {
                user = userService.getCurrentUser();
            }
            catch (Exception e) {
                user = userService.get("admin");
            }
        // Check if there is an annotation document entry in the database. If there is none,
        // create one.
        AnnotationDocument annotationDocument = documentService.createOrGetAnnotationDocument(doc,
                user);

        // Change the state of the source document to in progress
        documentService.transitionSourceDocumentState(doc, NEW_TO_ANNOTATION_IN_PROGRESS);
        documentService.transitionAnnotationDocumentState(annotationDocument,
                AnnotationDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS);

        // Read the correction CAS - if it does not exist yet, from the initial CAS
        CAS correctionCas;
        if (correctionDocumentService.existsCorrectionCas(doc)) {
            correctionCas = correctionDocumentService.readCorrectionCas(doc);
        }
        else {
            correctionCas = documentService.readAnnotationCas(annotationDocument);
        }

        // upgrade and save the CorrectionCAS
        correctionDocumentService.upgradeCorrectionCas(correctionCas, doc);
        correctionDocumentService.writeCorrectionCas(correctionCas, doc);
        updateDocumentTimestampAfterWrite(state,
                correctionDocumentService.getCorrectionCasTimestamp(doc));

        return correctionCas;
    }

    private PredictionRequest buildPredictionRequest(Codebook cb, Project proj, SourceDocument sdoc)
    {
        PredictionRequest request = new PredictionRequest();

        CodebookModel cbm = buildCodebookModel(cb);
        DocumentModel docm = buildDocumentModel(proj, sdoc);
        TagLabelMapping mapping = tagLabelMappings.get(cb);

        return request.codebook(cbm).doc(docm).mapping(mapping);
    }

    private MultiDocumentPredictionRequest buildMultiDocPredictionRequest(Codebook cb, Project proj,
            List<SourceDocument> sdocs)
    {
        MultiDocumentPredictionRequest req = new MultiDocumentPredictionRequest();

        CodebookModel cbm = buildCodebookModel(cb);
        List<DocumentModel> docModels = sdocs.stream()
                .map(sourceDocument -> this.buildDocumentModel(proj, sourceDocument))
                .collect(Collectors.toList());
        TagLabelMapping mapping = tagLabelMappings.get(cb);

        return req.codebook(cbm).docs(docModels).mapping(mapping);
    }

}
