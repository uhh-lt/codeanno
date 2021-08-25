/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.automation.page;

import static de.tudarmstadt.ukp.clarin.webanno.api.CasUpgradeMode.FORCE_CAS_UPGRADE;
import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.PROJECT_TYPE_AUTOMATION;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.updateDocumentTimestampAfterWrite;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.verifyAndUpdateDocumentTimestamp;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectAnnotationByAddr;
import static de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS;
import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.CasUtil;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.annotation.mount.MountPath;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.CorrectionDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectType;
import de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.AnnotationEditorBase;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.actionbar.ActionBar;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateImpl;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.page.AnnotationPageBase;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.paging.SentenceOrientedPagingStrategy;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.rendering.event.RenderAnnotationsEvent;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.automation.service.AutomationService;
import de.tudarmstadt.ukp.clarin.webanno.brat.annotation.BratAnnotationEditor;
import de.tudarmstadt.ukp.clarin.webanno.constraints.ConstraintsService;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentStateTransition;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationFeature;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationLayer;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.Tag;
import de.tudarmstadt.ukp.clarin.webanno.model.TagSet;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.DecoratedObject;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.component.DocumentNamePanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.annotation.detail.AnnotationDetailEditorPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.automation.util.AutomationUtil;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.SuggestionViewPanel;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.AnnotationSelection;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.CurationContainer;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.SourceListView;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.SuggestionBuilder;
import de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model.UserAnnotationSegment;

/**
 * This is the main class for the Automation page. Displays in the lower panel the Automatically
 * annotated document and in the upper panel the annotation pane to trigger automation on the lower
 * pane.
 */
@MountPath("/automation.html")
@ProjectType(id = WebAnnoConst.PROJECT_TYPE_AUTOMATION, prio = 110)
public class AutomationPage
    extends AnnotationPageBase
{
    private static final String MID_NUMBER_OF_PAGES = "numberOfPages";

    private static final Logger LOG = LoggerFactory.getLogger(AutomationPage.class);

    private static final long serialVersionUID = 1378872465851908515L;

    private @SpringBean CasStorageService casStorageService;
    private @SpringBean DocumentService documentService;
    private @SpringBean ProjectService projectService;
    private @SpringBean ConstraintsService constraintsService;
    private @SpringBean AnnotationSchemaService annotationService;
    private @SpringBean UserDao userRepository;
    private @SpringBean CurationDocumentService curationDocumentService;
    private @SpringBean CorrectionDocumentService correctionDocumentService;
    private @SpringBean AutomationService automationService;

    private long currentprojectId;

    private WebMarkupContainer centerArea;
    private WebMarkupContainer actionBar;
    private AnnotationEditorBase annotationEditor;
    private AnnotationDetailEditorPanel detailEditor;
    private SuggestionViewPanel suggestionView;

    private final Map<String, Map<Integer, AnnotationSelection>> annotationSelectionByUsernameAndAddress = //
            new HashMap<>();

    private final SourceListView curationSegment = new SourceListView();

    private CurationContainer curationContainer;

    public AutomationPage()
    {
        commonInit();
    }

    private void commonInit()
    {
        setVersioned(false);

        setModel(Model.of(new AnnotatorStateImpl(Mode.AUTOMATION)));

        WebMarkupContainer rightSidebar = new WebMarkupContainer("rightSidebar");
        // Override sidebar width from preferencesa
        rightSidebar.add(new AttributeModifier("style", LambdaModel.of(() -> String
                .format("flex-basis: %d%%;", getModelObject().getPreferences().getSidebarSize()))));
        rightSidebar.setOutputMarkupId(true);
        add(rightSidebar);

        centerArea = new WebMarkupContainer("centerArea");
        centerArea.add(visibleWhen(() -> getModelObject().getDocument() != null));
        centerArea.setOutputMarkupPlaceholderTag(true);

        centerArea.add(new DocumentNamePanel("documentNamePanel", getModel()));

        actionBar = new ActionBar("actionBar");
        centerArea.add(actionBar);

        rightSidebar.add(detailEditor = createDetailEditor());

        annotationEditor = new BratAnnotationEditor("mergeView", getModel(), detailEditor,
                this::getEditorCas);
        centerArea.add(annotationEditor);
        add(centerArea);

        getModelObject().setPagingStrategy(new SentenceOrientedPagingStrategy());
        centerArea.add(getModelObject().getPagingStrategy()
                .createPositionLabel(MID_NUMBER_OF_PAGES, getModel())
                .add(visibleWhen(() -> getModelObject().getDocument() != null))
                .add(LambdaBehavior.onEvent(RenderAnnotationsEvent.class,
                        (c, e) -> e.getRequestHandler().add(c))));

        List<UserAnnotationSegment> segments = new LinkedList<>();
        UserAnnotationSegment userAnnotationSegment = new UserAnnotationSegment();
        if (getModelObject().getDocument() != null) {
            userAnnotationSegment
                    .setSelectionByUsernameAndAddress(annotationSelectionByUsernameAndAddress);
            userAnnotationSegment.setAnnotatorState(getModelObject());
            segments.add(userAnnotationSegment);
        }

        suggestionView = new SuggestionViewPanel("automateView", new ListModel<>(segments))
        {
            private static final long serialVersionUID = 2583509126979792202L;

            @Override
            public void onChange(AjaxRequestTarget aTarget)
            {
                try {
                    // update begin/end of the curation segment based on bratAnnotatorModel changes
                    // (like sentence change in auto-scroll mode,....
                    aTarget.addChildren(getPage(), IFeedback.class);
                    AnnotatorState state = AutomationPage.this.getModelObject();
                    curationContainer.setState(state);
                    CAS editorCas = getEditorCas();
                    setCurationSegmentBeginEnd(editorCas);

                    suggestionView.requestUpdate(aTarget, curationContainer,
                            annotationSelectionByUsernameAndAddress, curationSegment);

                    annotationEditor.requestRender(aTarget);
                    update(aTarget);
                }
                catch (Exception e) {
                    handleException(aTarget, e);
                }
            }
        };
        centerArea.add(suggestionView);

        curationContainer = new CurationContainer();
        curationContainer.setState(getModelObject());
    }

    @Override
    public IModel<List<DecoratedObject<Project>>> getAllowedProjects()
    {
        return LambdaModel.of(() -> {
            User user = userRepository.getCurrentUser();
            List<DecoratedObject<Project>> allowedProject = new ArrayList<>();
            for (Project project : projectService.listProjects()) {
                if (projectService.isAnnotator(project, user)
                        && PROJECT_TYPE_AUTOMATION.equals(project.getMode())) {
                    allowedProject.add(DecoratedObject.of(project));
                }
            }
            return allowedProject;
        });
    }

    private AnnotationDetailEditorPanel createDetailEditor()
    {
        return new AnnotationDetailEditorPanel("annotationDetailEditorPanel", this, getModel())
        {
            private static final long serialVersionUID = 2857345299480098279L;

            @Override
            protected void onChange(AjaxRequestTarget aTarget)
            {
                AnnotatorState state = getModelObject();

                aTarget.addChildren(getPage(), IFeedback.class);

                try {
                    annotationEditor.requestRender(aTarget);
                }
                catch (Exception e) {
                    handleException(this, aTarget, e);
                    return;
                }

                try {
                    SuggestionBuilder builder = new SuggestionBuilder(casStorageService,
                            documentService, correctionDocumentService, curationDocumentService,
                            annotationService, userRepository);
                    curationContainer = builder.buildCurationContainer(state);
                    setCurationSegmentBeginEnd(getEditorCas());
                    curationContainer.setState(state);

                    suggestionView.requestUpdate(aTarget, curationContainer,
                            annotationSelectionByUsernameAndAddress, curationSegment);

                    update(aTarget);
                }
                catch (Exception e) {
                    handleException(this, aTarget, e);
                }
            }

            @Override
            public void onAnnotate(AjaxRequestTarget aTarget)
            {
                AnnotatorState state = getModelObject();

                if (state.isForwardAnnotation()) {
                    return;
                }
                AnnotationLayer layer = state.getSelectedAnnotationLayer();
                int address = state.getSelection().getAnnotation().getId();
                try {
                    CAS cas = getEditorCas();
                    AnnotationFS fs = selectAnnotationByAddr(cas, address);

                    for (AnnotationFeature f : annotationService.listAnnotationFeature(layer)) {
                        Type type = CasUtil.getType(fs.getCAS(), layer.getName());
                        Feature feat = type.getFeatureByBaseName(f.getName());
                        if (!automationService.existsMiraTemplate(f)) {
                            continue;
                        }
                        if (!automationService.getMiraTemplate(f).isAnnotateAndRepeat()) {
                            continue;
                        }
                        TagSet tagSet = f.getTagset();
                        boolean isRepeatable = false;
                        // repeat only if the value is in the tagset
                        for (Tag tag : annotationService.listTags(tagSet)) {
                            if (fs.getFeatureValueAsString(feat) == null) {
                                break; // this is new annotation without values
                            }
                            if (fs.getFeatureValueAsString(feat).equals(tag.getName())) {
                                isRepeatable = true;
                                break;
                            }
                        }
                        if (automationService.getMiraTemplate(f) != null && isRepeatable) {

                            if (layer.getType().endsWith(WebAnnoConst.RELATION_TYPE)) {
                                AutomationUtil.repeatRelationAnnotation(state, documentService,
                                        correctionDocumentService, annotationService, fs, f,
                                        fs.getFeatureValueAsString(feat));
                                update(aTarget);
                                break;
                            }
                            else if (layer.getType().endsWith(WebAnnoConst.SPAN_TYPE)) {
                                AutomationUtil.repeatSpanAnnotation(state, documentService,
                                        correctionDocumentService, annotationService, fs.getBegin(),
                                        fs.getEnd(), f, fs.getFeatureValueAsString(feat));
                                update(aTarget);
                                break;
                            }

                        }
                    }
                }
                catch (Exception e) {
                    handleException(this, aTarget, e);
                }
            }

            @Override
            protected void onAutoForward(AjaxRequestTarget aTarget)
            {
                annotationEditor.requestRender(aTarget);
            }

            @Override
            public void onDelete(AjaxRequestTarget aTarget, AnnotationFS aFS)
            {
                AnnotatorState state = getModelObject();
                AnnotationLayer layer = state.getSelectedAnnotationLayer();
                for (AnnotationFeature f : annotationService.listAnnotationFeature(layer)) {
                    if (!automationService.existsMiraTemplate(f)) {
                        continue;
                    }
                    if (!automationService.getMiraTemplate(f).isAnnotateAndRepeat()) {
                        continue;
                    }
                    try {
                        Type type = CasUtil.getType(aFS.getCAS(), layer.getName());
                        Feature feat = type.getFeatureByBaseName(f.getName());
                        if (layer.getType().endsWith(WebAnnoConst.RELATION_TYPE)) {
                            AutomationUtil.deleteRelationAnnotation(state, documentService,
                                    correctionDocumentService, annotationService, aFS, f,
                                    aFS.getFeatureValueAsString(feat));
                        }
                        else {
                            AutomationUtil.deleteSpanAnnotation(state, documentService,
                                    correctionDocumentService, annotationService, aFS.getBegin(),
                                    aFS.getEnd(), f, aFS.getFeatureValueAsString(feat));
                        }
                        update(aTarget);
                    }
                    catch (Exception e) {
                        handleException(this, aTarget, e);
                    }
                }
            }

            @Override
            public CAS getEditorCas() throws IOException
            {
                return AutomationPage.this.getEditorCas();
            }
        };
    }

    @Override
    public List<SourceDocument> getListOfDocs()
    {
        AnnotatorState state = getModelObject();
        return new ArrayList<>(documentService
                .listAnnotatableDocuments(state.getProject(), state.getUser()).keySet());
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
        if (diskTimestamp.isPresent()) {
            state.setAnnotationDocumentTimestamp(diskTimestamp.get());
        }
    }

    private void setCurationSegmentBeginEnd(CAS aEditorCas)
        throws UIMAException, ClassNotFoundException, IOException
    {
        AnnotatorState state = getModelObject();
        curationSegment.setBegin(state.getWindowBeginOffset());
        curationSegment.setEnd(state.getWindowEndOffset());
    }

    private void update(AjaxRequestTarget target)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {
        suggestionView.requestUpdate(target, curationContainer,
                annotationSelectionByUsernameAndAddress, curationSegment);
    }

    @Override
    public void actionLoadDocument(AjaxRequestTarget aTarget)
    {
        LOG.info("BEGIN LOAD_DOCUMENT_ACTION");

        AnnotatorState state = getModelObject();

        state.setUser(userRepository.getCurrentUser());
        state.setDocument(state.getDocument(), getListOfDocs());

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

            // (Re)initialize brat model after potential creating / upgrading CAS
            state.reset();

            // Initialize timestamp in state
            updateDocumentTimestampAfterWrite(state, documentService
                    .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername()));

            // Load constraints
            state.setConstraints(constraintsService.loadConstraints(state.getProject()));

            // Load user preferences
            loadPreferences();

            // Initialize the visible content
            state.setFirstVisibleUnit(WebAnnoCasUtil.getFirstSentence(editorCas));

            // if project is changed, reset some project specific settings
            if (currentprojectId != state.getProject().getId()) {
                state.clearRememberedFeatures();
            }

            currentprojectId = state.getProject().getId();

            setCurationSegmentBeginEnd(editorCas);
            suggestionView.init(aTarget, curationContainer, annotationSelectionByUsernameAndAddress,
                    curationSegment);
            update(aTarget);

            // Re-render the whole page because the font size
            if (aTarget != null) {
                aTarget.add(this);
            }

            // Update document state
            if (state.getDocument().getState().equals(SourceDocumentState.NEW)) {
                documentService.transitionSourceDocumentState(state.getDocument(),
                        NEW_TO_ANNOTATION_IN_PROGRESS);
            }

            if (AnnotationDocumentState.NEW.equals(annotationDocument.getState())) {
                documentService.transitionAnnotationDocumentState(annotationDocument,
                        AnnotationDocumentStateTransition.NEW_TO_ANNOTATION_IN_PROGRESS);
            }

            // Reset the editor
            detailEditor.reset(aTarget);
        }
        catch (Exception e) {
            handleException(aTarget, e);
        }

        LOG.info("END LOAD_DOCUMENT_ACTION");
    }

    @Override
    public void actionRefreshDocument(AjaxRequestTarget aTarget)
    {
        try {
            AnnotatorState state = getModelObject();
            SuggestionBuilder builder = new SuggestionBuilder(casStorageService, documentService,
                    correctionDocumentService, curationDocumentService, annotationService,
                    userRepository);
            curationContainer = builder.buildCurationContainer(state);
            setCurationSegmentBeginEnd(getEditorCas());
            curationContainer.setState(state);
            update(aTarget);
            annotationEditor.requestRender(aTarget);
            aTarget.add(centerArea.get(MID_NUMBER_OF_PAGES));
        }
        catch (Exception e) {
            handleException(aTarget, e);
        }
    }
}
