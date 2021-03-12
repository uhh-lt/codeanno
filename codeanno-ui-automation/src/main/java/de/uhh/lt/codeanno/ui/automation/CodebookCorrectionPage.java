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
package de.uhh.lt.codeanno.ui.automation;

import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_DOCUMENT_ID;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_FOCUS;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PAGE_PARAM_PROJECT_ID;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PROJECT_TYPE_AUTOMATION;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.paging.FocusPosition.TOP;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.copyDocumentMetadata;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.createSentence;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.createToken;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.exists;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getRealCas;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectSentences;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectTokens;
import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;
import static org.apache.uima.cas.impl.Serialization.deserializeCASComplete;
import static org.apache.uima.cas.impl.Serialization.serializeCASComplete;
import static org.apache.uima.fit.util.CasUtil.getType;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.CASCompleteSerializer;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.factory.CasFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;
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
import org.wicketstuff.annotation.mount.MountPath;
import org.wicketstuff.event.annotation.OnEvent;

import de.tudarmstadt.ukp.clarin.webanno.api.CorrectionDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.SessionMetaData;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.actionbar.ActionBar;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.page.AnnotationPageBase;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.paging.SentenceOrientedPagingStrategy;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.rendering.event.SelectionChangedEvent;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.constraints.ConstraintsService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.DecoratedObject;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.WicketUtil;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.ui.automation.tree.CodebookCorrectionTreePanel;

@MountPath("/codebookcorrection.html")
public class CodebookCorrectionPage
    extends AnnotationPageBase
{

    private static final long serialVersionUID = 7357170335776542934L;
    private final static Logger LOG = LoggerFactory.getLogger(CodebookCorrectionPage.class);

    private @SpringBean ProjectService projectService;
    private @SpringBean UserDao userRepository;
    private @SpringBean DocumentService documentService;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CorrectionDocumentService correctionDocumentService;
    private @SpringBean ConstraintsService constraintsService;

    private long currentProjectId;

    private WebMarkupContainer actionBar;
    private WebMarkupContainer leftPanelContainer;
    private WebMarkupContainer rightPanelContainer;
    private CodebookCorrectionTreePanel codebookCorrectionTreePanel;
    private boolean firstLoad;

    public CodebookCorrectionPage()
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

    public CodebookCorrectionPage(final PageParameters aPageParameters)
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
        codebookCorrectionTreePanel = new CodebookCorrectionTreePanel("codebookCorrectionTreePanel",
                this);
        codebookCorrectionTreePanel.setOutputMarkupPlaceholderTag(true);
        codebookCorrectionTreePanel.initTree();

        leftPanelContainer.add(codebookCorrectionTreePanel);
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

        // return the correction cas!
        return correctionDocumentService.readCorrectionCas(state.getDocument());
    }

    @Override
    public void writeEditorCas(CAS aCas) throws IOException, AnnotationException
    {
        // TODO do we need this methods or better: do we really want to extend AnnotationPage?!
        ensureIsEditable();

        AnnotatorState state = getModelObject();
        correctionDocumentService.writeCorrectionCas(aCas, state.getDocument());
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

        User user = userRepository.getCurrentUser();
        state.setUser(user);
        state.setDocument(state.getDocument(), getListOfDocs());

        // re-init tree since now the current project is set
        codebookCorrectionTreePanel.initTree();
        aTarget.add(codebookCorrectionTreePanel);

        // FIXME what do we actually need here?!
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
            // Update the CASes
            correctionDocumentService.upgradeCorrectionCas(correctionCas, state.getDocument());
            // After upgrading the CAS, we need to save it
            correctionDocumentService.writeCorrectionCas(correctionCas, state.getDocument());

            // Initialize timestamp in state
            AnnotatorStateUtils.updateDocumentTimestampAfterWrite(state, documentService
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
                        SourceDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS);
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

    public CAS clearAnnotations(CAS aCas) throws IOException
    {
        CAS target;
        try {
            target = CasFactory.createCas((TypeSystemDescription) null);
        }
        catch (UIMAException e) {
            throw new IOException(e);
        }

        // Copy the CAS - basically we do this just to keep the full type system information
        CASCompleteSerializer serializer = serializeCASComplete((CASImpl) getRealCas(aCas));
        deserializeCASComplete(serializer, (CASImpl) getRealCas(target));

        // Remove all annotations from the target CAS but we keep the type system!
        target.reset();

        // Copy over essential information
        if (exists(aCas, getType(aCas, DocumentMetaData.class))) {
            copyDocumentMetadata(aCas, target);
        }
        else {
            WebAnnoCasUtil.createDocumentMetadata(aCas);
        }
        target.setDocumentLanguage(aCas.getDocumentLanguage()); // DKPro Core Issue 435
        target.setDocumentText(aCas.getDocumentText());

        // Transfer token boundaries
        for (AnnotationFS t : selectTokens(aCas)) {
            target.addFsToIndexes(createToken(target, t.getBegin(), t.getEnd()));
        }

        // Transfer sentence boundaries
        for (AnnotationFS s : selectSentences(aCas)) {
            target.addFsToIndexes(createSentence(target, s.getBegin(), s.getEnd()));
        }

        return target;
    }
}
