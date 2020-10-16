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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.wicket.util.collections.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import de.tudarmstadt.ukp.clarin.webanno.codebook.adapter.CodebookAdapter;
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

    private final Map<Codebook, Boolean> availabilityCache;
    private final Map<Codebook, TagLabelMapping> tagLabelMappings;
    private final ConcurrentHashMap<Codebook, ConcurrentHashSet<SourceDocument>> predictionProgress;
    private final Object lock;
    private final DocumentService documentService;
    private final ProjectService projectService;
    private final CodebookSchemaService codebookService;
    private final CorrectionDocumentService correctionDocumentService;
    private final UserDao userService;
    private boolean heartbeat;

    @Autowired
    public CodebookAutomationServiceImpl(DocumentService documentService,
            ProjectService projectService, CodebookSchemaService codebookService,
            CorrectionDocumentService correctionDocumentService, UserDao userService)
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
        this.predictionProgress = new ConcurrentHashMap<>();

        this.performHeartbeatCheck();
        this.documentService = documentService;
        this.projectService = projectService;
        this.codebookService = codebookService;
        this.correctionDocumentService = correctionDocumentService;
        this.userService = userService;

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
        Set<SourceDocument> inProgress = this.predictionProgress.get(cb);
        return inProgress != null && !inProgress.isEmpty();
    }

    @Override
    public Integer getPredictionInProgress(Codebook cb)
    {
        if (this.predictionProgress.get(cb) == null)
            return 0;
        return this.predictionProgress.get(cb).size();
    }

    @Override
    public Double getPredictionInProgressFraction(Codebook cb)
    {
        if (this.predictionProgress.get(cb) == null || cb.getProject() == null)
            return 0.0;

        int inProgress = this.predictionProgress.get(cb).size();
        int allDocs = documentService.listSourceDocuments(cb.getProject()).size();

        return inProgress / (double) allDocs;
    }

    @Override
    public void addToPredictionProgress(Codebook cb, SourceDocument sdoc)
    {
        this.predictionProgress.putIfAbsent(cb, new ConcurrentHashSet<>());
        this.predictionProgress.get(cb).add(sdoc);
    }

    @Override
    public void removeFromPredictionProgress(PredictionResult result)
    {
        Codebook cb = this.getCodebook(result);
        SourceDocument sdoc = this.getSourceDocument(result);
        if (this.predictionProgress.get(cb) == null)
            return;
        this.predictionProgress.get(cb).remove(sdoc);
    }

    private PredictionRequest buildPredictionRequest(Codebook cb, Project proj, SourceDocument sdoc)
    {
        PredictionRequest request = new PredictionRequest();

        CodebookModel cbm = buildCodebookModel(cb);
        DocumentModel docm = buildDocumentModel(proj, sdoc);
        TagLabelMapping map = tagLabelMappings.get(cb);

        return request.codebook(cbm).doc(docm).mapping(map);
    }

    @Override
    public boolean isAutomationAvailable(Codebook cb, boolean updateCache) throws ApiException
    {
        if (!heartbeat || cb == null)
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
    public synchronized Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc,
            String userName)
        throws ApiException
    {
        if (!this.performHeartbeatCheck())
            return null;

        logger.info("Starting asynchronous Codebook Tag prediction for " + cb.getName()
                + " of Document " + sdoc.getId());

        PredictionRequest req = buildPredictionRequest(cb, proj, sdoc);
        this.addToPredictionProgress(cb, sdoc);
        return predictionApi.predictPredictionPredictPostAsync(req,
                new PersistToCasCallback(this, userName));
    }

    @Override
    public ModelMetadata getModelMetadata(Codebook cb) throws ApiException
    {
        if (!heartbeat)
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

    private Project getProject(PredictionResult result)
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

    private Codebook getCodebook(PredictionResult result)
    {
        if (result == null || result.getCodebookName() == null
                || result.getCodebookName().isEmpty())
            return null;

        Project project = this.getProject(result);

        return codebookService.getCodeBook(
                CodebookConst.CODEBOOK_NAME_PREFIX + result.getCodebookName(), project);
    }

    @Override
    public void writePredictedTagToCorrectionCas(PredictionResult result, String userName)
        throws IOException, UIMAException, AnnotationException
    {
        synchronized (lock) {
            logger.info("Started...");

            AnnotatorState state = this.createAnnotatorState(result, userName);
            CAS correctionCas = this.readOrCreateCorrectionCas(state, true);

            // FIXME we really need to get rid of the cb features to increase code
            // understandability a lot
            Codebook cb = this.getCodebook(result);
            CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
            CodebookAdapter adapter = new CodebookAdapter(cb);
            AnnotationFS existingFs = adapter.getExistingFs(correctionCas);
            int annoId = existingFs != null ? WebAnnoCasUtil.getAddr(existingFs)
                    : adapter.add(correctionCas);
            adapter.setFeatureValue(correctionCas, feature, annoId, result.getPredictedTag());

            correctionDocumentService.writeCorrectionCas(correctionCas, state.getDocument());
            updateDocumentTimestampAfterWrite(state,
                    correctionDocumentService.getCorrectionCasTimestamp(state.getDocument()));

            logger.info("Successfully wrote predicted Codebook Tag for Codebook <" + cb.getName()
                    + "> and Document ID=<" + state.getDocument().getId() + "> to Correction CAS!");
        }
    }

    private AnnotatorState createAnnotatorState(PredictionResult result, String userName)
        throws IOException
    {
        AnnotatorState state = new AnnotatorStateImpl(Mode.CORRECTION);

        SourceDocument doc = this.getSourceDocument(result);
        state.setDocument(doc, Collections.singletonList(doc));

        Project project = this.getProject(result);
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

}
