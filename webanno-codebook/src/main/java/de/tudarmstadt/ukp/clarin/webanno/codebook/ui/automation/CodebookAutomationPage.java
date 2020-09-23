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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation;

import static de.tudarmstadt.ukp.clarin.webanno.api.CasUpgradeMode.FORCE_CAS_UPGRADE;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_DOCUMENT_ID;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_FOCUS;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_PROJECT_ID;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PROJECT_TYPE_AUTOMATION;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.updateDocumentTimestampAfterWrite;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.verifyAndUpdateDocumentTimestamp;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.paging.FocusPosition.TOP;
import static de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS;
import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.uima.cas.CAS;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.event.annotation.OnEvent;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.CorrectionDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.SessionMetaData;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.AnnotationEditorExtensionRegistry;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.actionbar.ActionBar;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.page.AnnotationPageBase;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.paging.SentenceOrientedPagingStrategy;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.rendering.event.SelectionChangedEvent;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.tree.CodebookAutomationTreePanel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.curation.DocumentNamePanel;
import de.tudarmstadt.ukp.clarin.webanno.constraints.ConstraintsService;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.DecoratedObject;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.WicketUtil;

@MountPath("/codebookautomation.html")
public class CodebookAutomationPage
    extends AnnotationPageBase
{

    private static final long serialVersionUID = 7357170335776542934L;
    private final static Logger LOG = LoggerFactory.getLogger(CodebookAutomationPage.class);

    private @SpringBean ProjectService projectService;
    private @SpringBean UserDao userRepository;
    private @SpringBean DocumentService documentService;
    private @SpringBean CurationDocumentService curationDocumentService;
    private @SpringBean AnnotationSchemaService annotationService;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CorrectionDocumentService correctionDocumentService;
    private @SpringBean AnnotationEditorExtensionRegistry extensionRegistry;
    private @SpringBean ConstraintsService constraintsService;
    private @SpringBean CasStorageService casStorageService;

    private long currentProjectId;

    private WebMarkupContainer actionBar;
    private WebMarkupContainer leftPanelContainer;
    private WebMarkupContainer rightPanelContainer;
    private CodebookAutomationTreePanel codebookAutomationTreePanel;
    private boolean firstLoad;

    public CodebookAutomationPage()
    {
        super();
        LOG.debug("Setting up codebook automation page without parameters");
        commonInit();

        Map<String, StringValue> fragmentParameters = Session.get()
                .getMetaData(SessionMetaData.LOGIN_URL_FRAGMENT_PARAMS);
        if (fragmentParameters != null) {
            // Clear the URL fragment parameters - we only use them once!
            Session.get().setMetaData(SessionMetaData.LOGIN_URL_FRAGMENT_PARAMS, null);

            StringValue project = fragmentParameters.get(PAGE_PARAM_PROJECT_ID);
            StringValue document = fragmentParameters.get(PAGE_PARAM_DOCUMENT_ID);
            StringValue focus = fragmentParameters.get(PAGE_PARAM_FOCUS);

            handleParameters(null, project, document, focus, false);
        }
    }

    public CodebookAutomationPage(final PageParameters aPageParameters)
    {
        super(aPageParameters);
        LOG.debug("Setting up codebook automation page with parameters: {}", aPageParameters);

        commonInit();

        StringValue project = aPageParameters.get(PAGE_PARAM_PROJECT_ID);
        StringValue document = aPageParameters.get(PAGE_PARAM_DOCUMENT_ID);
        StringValue focus = aPageParameters.get(PAGE_PARAM_FOCUS);

        handleParameters(null, project, document, focus, true);
    }

    private void commonInit()
    {
        firstLoad = false;

        setModel(Model.of(new AnnotatorStateImpl(Mode.AUTOMATION)));

        // init left panel -> codebook tree
        leftPanelContainer = new WebMarkupContainer("leftPanelContainer");
        leftPanelContainer.setOutputMarkupPlaceholderTag(true);
        codebookAutomationTreePanel = new CodebookAutomationTreePanel("codebookAutomationTreePanel",
                this);
        codebookAutomationTreePanel.setOutputMarkupPlaceholderTag(true);
        codebookAutomationTreePanel.initTree();

        leftPanelContainer.add(codebookAutomationTreePanel);
        leftPanelContainer.add(visibleWhen(() -> getModelObject().getDocument() != null));
        add(leftPanelContainer);

        // init right panel -> actionbar + document content
        rightPanelContainer = new WebMarkupContainer("rightPanelContainer");
        rightPanelContainer.add(visibleWhen(() -> getModelObject().getDocument() != null));
        rightPanelContainer.setOutputMarkupPlaceholderTag(true);
        rightPanelContainer.add(new DocumentNamePanel("documentNamePanel", getModel()));

        // action bar
        actionBar = new ActionBar("actionBar");
        rightPanelContainer.add(actionBar);

        // document content
        getModelObject().setPagingStrategy(new SentenceOrientedPagingStrategy());
        WebMarkupContainer documentContentContainer = new WebMarkupContainer(
                "documentContentContainer");
        documentContentContainer.setOutputMarkupId(true);

        // FIXME just a quick-n-dirty-hack since enabling the "webanno-way" of viewing a
        // doc
        // (like in annotation mode) requires a lot of boilerplate code (see
        // CurationPage.java)
        documentContentContainer
                .add(new MultiLineLabel("documentContent", new LoadableDetachableModel<String>()
                {

                    private static final long serialVersionUID = 3761758181939352279L;

                    @Override
                    protected String load()
                    {
                        try {
                            return new String(Files.readAllBytes(documentService
                                    .getSourceDocumentFile(getModelObject().getDocument())
                                    .toPath()));
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        return "Error loading document content!";
                    }
                }));
        documentContentContainer.add(visibleWhen(() -> getModelObject().getDocument() != null));
        rightPanelContainer.add(documentContentContainer);

        // Ensure that a user is set
        getModelObject().setUser(userRepository.getCurrentUser());

        add(rightPanelContainer);
    }

    private void handleParameters(AjaxRequestTarget aTarget, StringValue aProjectParameter,
            StringValue aDocumentParameter, StringValue aFocusParameter, boolean aLockIfPreset)
    {
        // Get current project from parameters
        Project project = null;
        try {
            project = getProjectFromParameters(aProjectParameter);
        }
        catch (NoResultException e) {
            error("Project [" + aProjectParameter + "] does not exist");
            return;
        }

        // Get current document from parameters
        SourceDocument document = null;
        if (project != null) {
            try {
                document = getDocumentFromParameters(project, aDocumentParameter);
            }
            catch (NoResultException e) {
                error("Document [" + aDocumentParameter + "] does not exist in project ["
                        + project.getId() + "]");
            }
        }

        // Get current focus unit from parameters
        int focus = 0;
        if (aFocusParameter != null) {
            focus = aFocusParameter.toInt(0);
        }

        // If there is no change in the current document, then there is nothing to do.
        // Mind
        // that document IDs are globally unique and a change in project does not happen
        // unless
        // there is also a document change.
        if (document != null && document.equals(getModelObject().getDocument())
                && focus == getModelObject().getFocusUnitIndex()) {
            return;
        }

        // Check access to project
        if (project != null && !projectService.isCurator(project, getModelObject().getUser())) {
            error("You have no permission to access project [" + project.getId() + "]");
            return;
        }

        // Update project in state
        // Mind that this is relevant if the project was specified as a query parameter
        // i.e. not only in the case that it was a URL fragment parameter.
        if (project != null) {
            getModelObject().setProject(project);
            if (aLockIfPreset) {
                getModelObject().setProjectLocked(true);
            }
        }

        if (document != null) {
            // If we arrive here and the document is not null, then we have a change of
            // document
            // or a change of focus (or both)
            if (!document.equals(getModelObject().getDocument())) {
                getModelObject().setDocument(document, getListOfDocs());
                actionLoadDocument(aTarget, focus);
            }
            else {
                try {
                    getModelObject().moveToUnit(getEditorCas(), focus, TOP);
                    actionRefreshDocument(aTarget);
                }
                catch (Exception e) {
                    aTarget.addChildren(getPage(), IFeedback.class);
                    LOG.info("Error reading CAS " + e.getMessage());
                    error("Error reading CAS " + e.getMessage());
                }
            }
        }
    }

    private Project getProjectFromParameters(StringValue projectParam)
    {
        Project project = null;
        if (projectParam != null && !projectParam.isEmpty()) {
            long projectId = projectParam.toLong();
            project = projectService.getProject(projectId);
        }
        return project;
    }

    private SourceDocument getDocumentFromParameters(Project aProject, StringValue documentParam)
    {
        SourceDocument document = null;
        if (documentParam != null && !documentParam.isEmpty()) {
            long documentId = documentParam.toLong();
            document = documentService.getSourceDocument(aProject.getId(), documentId);
        }
        return document;
    }

    @Override
    public List<SourceDocument> getListOfDocs()
    {
        AnnotatorState state = getModelObject();
        return new ArrayList<>(documentService
                .listAnnotatableDocuments(state.getProject(), state.getUser()).keySet());
    }

    public List<DecoratedObject<SourceDocument>> listDocuments(Project aProject, User aUser)
    {
        List<DecoratedObject<SourceDocument>> allSourceDocuments = new ArrayList<>();
        List<SourceDocument> sdocs = new ArrayList<>(
                documentService.listAnnotatableDocuments(aProject, aUser).keySet());

        for (SourceDocument sourceDocument : sdocs) {
            DecoratedObject<SourceDocument> dsd = DecoratedObject.of(sourceDocument);
            dsd.setLabel("%s (%s)", sourceDocument.getName(), sourceDocument.getState());
            dsd.setColor(sourceDocument.getState().getColor());
            allSourceDocuments.add(dsd);
        }
        return allSourceDocuments;
    }

    @Override
    public CAS getEditorCas() throws IOException
    {
        AnnotatorState state = getModelObject();

        if (state.getDocument() == null) {
            throw new IllegalStateException("Please open a document first!");
        }

        // If we have a timestamp, then use it to detect if there was a concurrent access
        verifyAndUpdateDocumentTimestamp(state, documentService
                .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername()));

        return documentService.readAnnotationCas(getModelObject().getDocument(),
                state.getUser().getUsername());
    }

    @Override
    public void writeEditorCas(CAS aCas) throws IOException, AnnotationException
    {
        ensureIsEditable();

        AnnotatorState state = getModelObject();
        documentService.writeAnnotationCas(aCas, state.getDocument(), state.getUser(), true);

        // Update timestamp in state
        Optional<Long> diskTimestamp = documentService
                .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername());
        diskTimestamp.ifPresent(state::setAnnotationDocumentTimestamp);
    }

    @Override
    public void actionRefreshDocument(AjaxRequestTarget aTarget)
    {
        WicketUtil.refreshPage(aTarget, getPage());
    }

    @Override
    public IModel<List<DecoratedObject<Project>>> getAllowedProjects()
    {
        User user = userRepository.getCurrentUser();
        List<DecoratedObject<Project>> allowedProject = new ArrayList<>();
        for (Project project : projectService.listProjects()) {
            if (projectService.isAnnotator(project, user)
                    && PROJECT_TYPE_AUTOMATION.equals(project.getMode())
                    && !codebookService.listCodebook(project).isEmpty()) {
                allowedProject.add(DecoratedObject.of(project));
            }
        }
        return Model.ofList(allowedProject);
    }

    /**
     * Re-render the document when the selection has changed.
     */
    @OnEvent
    public void onSelectionChangedEvent(SelectionChangedEvent aEvent)
    {
        actionRefreshDocument(aEvent.getRequestHandler());
    }

    private void onDocumentSelected(AjaxRequestTarget aTarget)
    {
        AnnotatorState state = getModelObject();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        /*
         * Changed for #152, getDocument was returning null even after opening a document Also,
         * surrounded following code into if block to avoid error.
         */
        if (state.getProject() == null) {
            setResponsePage(getApplication().getHomePage());
            return;
        }
        if (state.getDocument() != null) {
            try {
                documentService.createSourceDocument(state.getDocument());
                upgradeCasAndSave(state.getDocument(), username);

                actionLoadDocument(aTarget);
            }
            catch (Exception e) {
                LOG.error("Unable to load data", e);
                error("Unable to load data: " + ExceptionUtils.getRootCauseMessage(e));
            }
        }
    }

    public void upgradeCasAndSave(SourceDocument aDocument, String aUsername) throws IOException
    {
        User user = userRepository.get(aUsername);
        if (documentService.existsAnnotationDocument(aDocument, user)) {
            AnnotationDocument annotationDocument = documentService.getAnnotationDocument(aDocument,
                    user);
            try {
                CAS cas = documentService.readAnnotationCas(annotationDocument);
                annotationService.upgradeCas(cas, annotationDocument);
                documentService.writeAnnotationCas(cas, annotationDocument, false);
            }
            catch (Exception e) {
                // no need to catch, it is acceptable that no curation document
                // exists to be upgraded while there are annotation documents
            }
        }
    }

    @Override
    public void actionLoadDocument(AjaxRequestTarget aTarget)
    {
        actionLoadDocument(aTarget, 0);
    }

    /**
     * Open a document or to a different document. This method should be used only the first time
     * that a document is accessed. It reset the annotator state and upgrades the CAS.
     */
    private void actionLoadDocument(AjaxRequestTarget aTarget, int aFocus)
    {
        LOG.info("BEGIN LOAD_DOCUMENT_ACTION at focus " + aFocus);

        AnnotatorState state = getModelObject();

        state.setUser(userRepository.getCurrentUser());
        state.setDocument(state.getDocument(), getListOfDocs());

        // re-init tree since now the current project is set
        codebookAutomationTreePanel.initTree();
        aTarget.add(codebookAutomationTreePanel);

        try {
            // Check if there is an annotation document entry in the database. If there is none,
            // create one.
            AnnotationDocument annotationDocument = documentService
                    .createOrGetAnnotationDocument(state.getDocument(), state.getUser());

            // Read the correction CAS - if it does not exist yet, from the initial CAS
            CAS correctionCas;
            if (correctionDocumentService.existsCorrectionCas(state.getDocument())) {
                correctionCas = correctionDocumentService.readCorrectionCas(state.getDocument());
            }
            else {
                correctionCas = documentService.createOrReadInitialCas(state.getDocument());
            }

            // Read the annotation CAS or create an annotation CAS from the initial CAS by stripping
            // annotations
            CAS editorCas = documentService.readAnnotationCas(annotationDocument,
                    FORCE_CAS_UPGRADE);

            // Update the CASes
            correctionDocumentService.upgradeCorrectionCas(correctionCas, state.getDocument());

            // After creating an new CAS or upgrading the CAS, we need to save it
            documentService.writeAnnotationCas(editorCas, annotationDocument.getDocument(),
                    state.getUser(), false);
            correctionDocumentService.writeCorrectionCas(correctionCas, state.getDocument());

            // Initialize timestamp in state
            updateDocumentTimestampAfterWrite(state, documentService
                    .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername()));

            // Load constraints
            state.setConstraints(constraintsService.loadConstraints(state.getProject()));

            // Load user preferences
            loadPreferences();

            // if project is changed, reset some project specific settings
            if (currentProjectId != state.getProject().getId()) {
                state.clearRememberedFeatures();
            }
            currentProjectId = state.getProject().getId();

            // Re-render the whole page because the font size
            WicketUtil.refreshPage(aTarget, this);

            // Update document state
            if (state.getDocument().getState().equals(SourceDocumentState.NEW)) {
                documentService.transitionSourceDocumentState(state.getDocument(),
                        NEW_TO_ANNOTATION_IN_PROGRESS);
            }

            if (AnnotationDocumentState.NEW.equals(annotationDocument.getState())) {
                documentService.transitionAnnotationDocumentState(annotationDocument,
                        AnnotationDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS);
            }
        }
        catch (Exception e) {
            handleException(aTarget, e);
        }

        LOG.info("END LOAD_DOCUMENT_ACTION");
    }

    /**
     * for the first time, open the <b>open document dialog</b>
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        super.renderHead(response);

        String jQueryString = "";
        if (firstLoad) {
            jQueryString += "jQuery('#showOpenDocumentModal').trigger('click');";
            firstLoad = false;
        }
        response.render(OnLoadHeaderItem.forScript(jQueryString));
    }
}
