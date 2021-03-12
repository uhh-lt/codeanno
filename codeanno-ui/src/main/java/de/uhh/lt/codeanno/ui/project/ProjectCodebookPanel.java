/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt 
 * and Language Technology Lab Universität Hamburg
 * 
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
package de.uhh.lt.codeanno.ui.project;

import static de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst.CURATION_USER;
import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.wicket.util.string.Strings.escapeMarkup;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CAS;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.resource.IResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapRadioChoice;
import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.support.JSONUtil;
import de.tudarmstadt.ukp.clarin.webanno.support.dialog.ConfirmationDialog;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxButton;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxFormChoiceComponentUpdatingBehavior;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.support.spring.ApplicationEventPublisherHolder;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.AjaxDownloadLink;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.InputStreamResourceStream;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelBase;
import de.uhh.lt.codeanno.api.CodebookConstants;
import de.uhh.lt.codeanno.api.event.CodebookConfigurationChangedEvent;
import de.uhh.lt.codeanno.api.export.CodebookExporter;
import de.uhh.lt.codeanno.api.export.ExportedCodebook;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.tree.CodebookTreeProvider;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.model.CodebookNodeExpansion;
import de.uhh.lt.codeanno.ui.config.CodebookLayoutCssResourceBehavior;
import de.uhh.lt.codeanno.ui.project.tree.ProjectCodebookTreePanel;

/**
 * A Panel Used to add Codebooks to a selected {@link Project} in the project settings page
 */

public class ProjectCodebookPanel
    extends ProjectSettingsPanelBase
{
    private static final Logger LOG = LoggerFactory.getLogger(ProjectCodebookPanel.class);
    private static final long serialVersionUID = -7870526462864489252L;

    private final String CFN = "code";
    private final CodebookExportMode exportMode = CodebookExportMode.ALL;

    private @SpringBean AnnotationSchemaService annotationService;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CodebookExporter codebookExporter;
    private @SpringBean ApplicationEventPublisherHolder applicationEventPublisherHolder;
    private @SpringBean CasStorageService casStorageService;
    private @SpringBean DocumentService documentService;

    private ConfirmationDialog confirmationDialog;

    private final CodebookSelectionForm codebookSelectionForm;
    private final CodebookDetailForm codebookDetailForm;

    private final CodebookTagSelectionPanel tagSelectionPanel;
    private final CodebookTagEditorPanel tagEditorPanel;

    private final ImportCodebookForm importCodebookForm;

    private final IModel<CodebookTag> selectedTag;

    private final ProjectCodebookTreePanel projectCodebookTreePanel;

    public ProjectCodebookPanel(String id, final IModel<Project> aProjectModel)
    {
        super(id, aProjectModel);
        setOutputMarkupId(true);
        add(CodebookLayoutCssResourceBehavior.get());

        selectedTag = Model.of();
        Model<Codebook> selectedCodebook = Model.of();

        // codebook selection form
        codebookSelectionForm = new CodebookSelectionForm("codebookSelectionForm",
                selectedCodebook);
        add(codebookSelectionForm);

        // codebook detail form
        codebookDetailForm = new CodebookDetailForm("codebookDetailForm", selectedCodebook);
        add(codebookDetailForm);

        // tag selection panel
        tagSelectionPanel = new CodebookTagSelectionPanel("codebookTagSelector", selectedCodebook,
                selectedTag);
        add(tagSelectionPanel);

        // tag editor panel
        tagEditorPanel = new CodebookTagEditorPanel("codebookTagEditor", selectedCodebook,
                selectedTag, tagSelectionPanel);
        add(tagEditorPanel);
        tagSelectionPanel.setChangeAction(target -> {
            target.add(tagEditorPanel);
        });

        // import form
        importCodebookForm = new ImportCodebookForm("importCodebookForm");
        codebookSelectionForm.add(importCodebookForm);

        // export panel
        // codebookSelectionForm.add(new ExportCodebooksForm());
        BootstrapRadioChoice<CodebookExportMode> exportModeChoice = new BootstrapRadioChoice<>(
                "exportMode", asList(CodebookExportMode.values()));
        exportModeChoice.setModel(new PropertyModel<CodebookExportMode>(this, "exportMode"));
        exportModeChoice.setChoiceRenderer(new EnumChoiceRenderer<>(this));
        exportModeChoice.add(new LambdaAjaxFormChoiceComponentUpdatingBehavior());
        codebookDetailForm.add(exportModeChoice);
        codebookDetailForm.add(new AjaxDownloadLink("export",
                new StringResourceModel("export.codebooks.filename", this),
                LoadableDetachableModel.of(this::exportCodebooks)));

        // add and init tree
        projectCodebookTreePanel = new ProjectCodebookTreePanel("projectCodebookTreePanel",
                aProjectModel, this, codebookDetailForm, tagSelectionPanel, tagEditorPanel);

        updateTree();

        projectCodebookTreePanel.setOutputMarkupId(true);
        // add tree to selection form
        codebookSelectionForm.add(projectCodebookTreePanel);
    }

    private void updateTree()
    {
        projectCodebookTreePanel.initTree();
        projectCodebookTreePanel.expandAll();
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();

        codebookDetailForm.setModelObject(null);
        tagSelectionPanel.setDefaultModelObject(null);
        tagEditorPanel.setDefaultModelObject(null);
        this.updateTree();
    }

    private List<Codebook> getConnectedCodebooks(Codebook codebook)
    {
        CodebookNode node = this.projectCodebookTreePanel.getProvider().getCodebookNode(codebook);

        List<CodebookNode> connected = new ArrayList<>(
                this.projectCodebookTreePanel.getProvider().getDescendants(node));
        connected.addAll(this.projectCodebookTreePanel.getProvider().getPrecedents(node));
        connected.add(node);

        return this.projectCodebookTreePanel.getProvider().getCodebooks(connected);
    }

    private IResourceStream exportCodebooks()
    {
        switch (exportMode) {
        case SELECTED:
            Codebook cb = codebookDetailForm.getModelObject();
            if (cb == null)
                throw new IllegalStateException("Selected Codebook is null!");

            return exportCodebooks(getConnectedCodebooks(cb));

        case ALL:
            return exportCodebooks(
                    codebookService.listCodebook(ProjectCodebookPanel.this.getModelObject()));
        default:
            throw new IllegalStateException("Unknown mode: [" + exportMode + "]");
        }
    }

    private IResourceStream exportCodebooks(List<Codebook> codebooks)
    {
        try {
            List<ExportedCodebook> exCBs = codebookExporter.exportCodebooks(codebooks);

            return new InputStreamResourceStream(new ByteArrayInputStream(
                    JSONUtil.toPrettyJsonString(exCBs).getBytes(StandardCharsets.UTF_8)));

        }
        catch (Exception e) {
            error("Unable to generate the JSON file: " + ExceptionUtils.getRootCauseMessage(e));
            LOG.error("Unable to generate the JSON file", e);
            RequestCycle.get().find(IPartialPageRequestHandler.class)
                    .ifPresent(handler -> handler.addChildren(getPage(), IFeedback.class));
            return null;
        }
    }

    private void saveCodebook(Codebook codebook)
    {
        final Project project = ProjectCodebookPanel.this.getModelObject();
        boolean isNewCodebook = isNull(codebook.getId());

        if (isNewCodebook) {
            String codebookName = StringUtils.capitalize(codebook.getUiName());

            codebookName = codebookName.replaceAll("\\W", "");

            if (codebookName.isEmpty()) {
                error("Unable to derive internal name from [" + codebook.getUiName()
                        + "]. Please choose a different initial name and rename after the "
                        + "codebook has been created.");
                return;
            }

            if (!Character.isJavaIdentifierStart(codebookName.charAt(0))) {
                error("Initial codebook name cannot start with [" + codebookName.charAt(0)
                        + "]. Please choose a different initial name and rename after the "
                        + "codebook has been created.");
                return;
            }
            String internalName = CodebookConstants.CODEBOOK_NAME_PREFIX + codebookName;
            if (codebookService.existsCodebook(internalName, project)) {
                error("A codebook with the name [" + internalName
                        + "] already exists in this project.");
                return;
            }

            codebook.setName(internalName);
        }

        if (codebook.getOrdering() < 1) {
            int lastIndex = codebookService.listCodebook(project).size();
            codebook.setOrdering(lastIndex + 1);
        }

        codebook.setProject(project);

        codebookService.createOrUpdateCodebook(codebook);
        if (!codebookService.existsFeature(CFN, codebook)) {
            CodebookFeature codebookFeature = new CodebookFeature();
            codebookFeature.setCodebook(codebook);
            codebookFeature.setProject(ProjectCodebookPanel.this.getModelObject());
            codebookFeature.setName(CFN);
            codebookFeature.setUiName("Code");// not visible for current implementation
            codebookFeature.setDescription("Specific code values for this codebook");
            codebookFeature.setType(CAS.TYPE_NAME_STRING);
            codebookService.createOrUpdateCodebookFeature(codebookFeature);
            tagSelectionPanel.setDefaultModelObject(codebook);
        }

        applicationEventPublisherHolder.get()
                .publishEvent(new CodebookConfigurationChangedEvent(this, project));
    }

    enum CodebookExportMode
    {
        SELECTED, ALL
    }

    private class CodebookSelectionForm
        extends Form<Codebook>
    {

        private static final long serialVersionUID = 9114612780349722472L;

        @SuppressWarnings({})
        public CodebookSelectionForm(String id, IModel<Codebook> aModel)
        {
            super(id, aModel);

            // add create button
            add(new AjaxButton("create", new StringResourceModel("label"))
            {
                private static final long serialVersionUID = -4482428496358679571L;

                @Override
                public void onSubmit(AjaxRequestTarget target)
                {
                    selectedTag.setObject(null);
                    tagSelectionPanel.setDefaultModelObject(null);
                    tagEditorPanel.setDefaultModelObject(null);
                    CodebookSelectionForm.this.setDefaultModelObject(null);
                    codebookDetailForm.setDefaultModelObject(new Codebook());

                    // Normally, using focusComponent should work, but it doesn't.
                    // Therefore, we manually add a JavaScript snippet..
                    // (cf. https://issues.apache.org/jira/browse/WICKET-5858)
                    // target.focusComponent(codebookDetailForm.uiName);
                    target.appendJavaScript("setTimeout(function() { document.getElementById('"
                            + codebookDetailForm.uiName.getMarkupId() + "').focus(); }, 100);");
                    target.add(ProjectCodebookPanel.this);
                }
            });

            // reset Ordering button
            add(new LambdaAjaxLink("resetOrdering", this::actionResetOrdering));

            // create and add expand and collapse buttons
            add(new Button("expandAll")
            {
                private static final long serialVersionUID = 2572198183232832062L;

                @Override
                public void onSubmit()
                {
                    actionExpand();
                }
            });

            add(new Button("collapseAll")
            {
                private static final long serialVersionUID = 2572198183232832062L;

                @Override
                public void onSubmit()
                {
                    actionCollapse();
                }
            });
        }

        private void actionExpand()
        {
            CodebookNodeExpansion.get().expandAll();
        }

        private void actionCollapse()
        {
            CodebookNodeExpansion.get().collapseAll();
        }

        private void actionResetOrdering(AjaxRequestTarget aTarget) throws Exception
        {
            Project proj = ProjectCodebookPanel.this.getModelObject();
            List<Codebook> codebooks = codebookService.listCodebook(proj);
            codebooks.sort(Comparator.comparing(Codebook::getName).reversed());
            codebooks.forEach(cb -> cb.setOrdering(codebooks.indexOf(cb)));
            codebooks.forEach(codebookService::createOrUpdateCodebook);
            projectCodebookTreePanel.initTree(); // we have to think of a better way to update a
            // tree. this way we init the tree way too often
            aTarget.add(codebookSelectionForm, projectCodebookTreePanel);
        }
    }

    private class ImportCodebookForm
        extends Form<String>
    {
        private static final long serialVersionUID = -7777616763931128598L;

        private final FileUploadField fileUpload;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public ImportCodebookForm(String id)
        {
            super(id);
            add(fileUpload = new FileUploadField("content", new Model()));
            add(new LambdaAjaxButton("import", this::actionImport));
        }

        private void actionImport(AjaxRequestTarget aTarget, Form<String> aForm)
        {
            List<FileUpload> uploadedFiles = fileUpload.getFileUploads();
            Project project = ProjectCodebookPanel.this.getModelObject();

            if (isEmpty(uploadedFiles)) {
                error("Please choose file with codebook category details before uploading");
                return;
            }
            else if (isNull(project.getId())) {
                error("Project not yet created, please save project details!");
                return;
            }
            for (FileUpload uploadedFile : uploadedFiles) {
                try (BufferedInputStream bis = IOUtils.buffer(uploadedFile.getInputStream())) {
                    byte[] buf = new byte[5];
                    bis.mark(buf.length + 1);
                    bis.read(buf, 0, buf.length);
                    bis.reset();

                    importCodebook(bis);
                }
                catch (Exception e) {
                    error("Error importing codebooks: " + ExceptionUtils.getRootCauseMessage(e));
                    aTarget.addChildren(getPage(), IFeedback.class);
                }
            }
            codebookDetailForm.setVisible(false);

            updateTree();

            aTarget.add(ProjectCodebookPanel.this);
            aTarget.addChildren(getPage(), IFeedback.class);
        }

        private void importCodebook(InputStream aIS) throws IOException
        {
            String text = IOUtils.toString(aIS, StandardCharsets.UTF_8);
            ExportedCodebook[] exCodebooks = JSONUtil.getObjectMapper().readValue(text,
                    ExportedCodebook[].class);
            codebookExporter.importCodebooks(Arrays.asList(exCodebooks),
                    ProjectCodebookPanel.this.getModelObject());
        }
    }

    public class CodebookDetailForm
        extends Form<Codebook>
    {

        private static final long serialVersionUID = 4032381828920667771L;

        private final ParentSelectionWrapper<Codebook> codebookParentSelection;

        private final TextField<String> uiName;

        public CodebookDetailForm(String id, IModel<Codebook> aSelectedCodebook)
        {
            super(id, CompoundPropertyModel.of(aSelectedCodebook));

            setOutputMarkupPlaceholderTag(true);
            uiName = new TextField<>("uiName");
            uiName.setOutputMarkupId(true);
            uiName.setRequired(true);
            add(uiName);
            add(new TextArea<String>("description").setOutputMarkupPlaceholderTag(true));

            add(new Label("name")
            {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onConfigure()
                {
                    super.onConfigure();
                    setVisible(StringUtils
                            .isNotBlank(CodebookDetailForm.this.getModelObject().getName()));
                }
            });

            // add Parent Selection
            List<Codebook> possibleParents = new ArrayList<>();
            if (aSelectedCodebook.getObject() != null) {
                possibleParents = projectCodebookTreePanel.getProvider()
                        .getPossibleParents(aSelectedCodebook.getObject());
            }
            this.codebookParentSelection = new ParentSelectionWrapper<>("parent", "uiName",
                    possibleParents);
            add(this.codebookParentSelection.getDropdown());

            // create tag checkbox
            CheckBox createTag = new CheckBox("createTag", Model.of(Boolean.TRUE));
            add(createTag);

            // add save and delete buttons
            add(new LambdaAjaxButton<>("save", this::actionSave));
            add(new LambdaAjaxButton<>("delete", this::actionDelete)
                    .add(visibleWhen(() -> !isNull(this.getModelObject().getId()))));
            add(new LambdaAjaxLink("close", this::actionCancel));

            confirmationDialog = new ConfirmationDialog("confirmationDialog");
            confirmationDialog
                    .setTitleModel(new StringResourceModel("DeleteCodebookDialog.title", this));
            add(confirmationDialog);

        }

        private void actionSave(AjaxRequestTarget aTarget, Form<?> aForm)
        {
            Codebook codebook = CodebookDetailForm.this.getModelObject();
            // make the codebook directly available for parent selection
            this.codebookParentSelection.addParent(codebook);

            saveCodebook(codebook);

            updateTree();

            applicationEventPublisherHolder.get().publishEvent(
                    new CodebookConfigurationChangedEvent(this, codebook.getProject()));

            aTarget.add(ProjectCodebookPanel.this);
            aTarget.addChildren(getPage(), IFeedback.class);
        }

        private void purgeCodebook(Codebook codebook)
        {
            // recursively purges all child codebooks and their respective tags
            CodebookTreeProvider codebookNodeProvider = projectCodebookTreePanel.getProvider();

            codebookNodeProvider.getChildren(codebook).forEach(this::purgeCodebook);

            List<CodebookTag> tags = codebookService.listTags(codebook);
            tags.forEach(codebookService::removeCodebookTag);

            codebookService.removeCodebook(codebook);
        }

        private void actionDelete(AjaxRequestTarget aTarget, Form aForm)
        {
            StringResourceModel model = new StringResourceModel("DeleteCodebookDialog.text",
                    this.getParent());
            CharSequence params = escapeMarkup(getModelObject().getName());
            confirmationDialog.setContentModel(model.setParameters(params));
            confirmationDialog.show(aTarget);

            confirmationDialog.setConfirmAction(_target -> {
                try {
                    Codebook codebook = codebookDetailForm.getModelObject();
                    this.purgeCodebook(codebook);
                }
                catch (Exception e) {
                    // TODO
                    // due to the limitations of ConfirmationDialog it's not possible to display
                    // a meaningful error of DataIntegrityViolationException
                }

                Project project = getModelObject().getProject();

                setModelObject(null);

                for (SourceDocument doc : documentService.listSourceDocuments(project)) {
                    for (AnnotationDocument ann : documentService.listAllAnnotationDocuments(doc)) {
                        try {
                            CAS cas = casStorageService.readCas(doc, ann.getUser());
                            annotationService.upgradeCas(cas, doc, ann.getUser());
                            casStorageService.writeCas(doc, cas, ann.getUser());
                        }
                        catch (FileNotFoundException e) {
                            // If there is no CAS file, we do not have to upgrade it. Ignoring.
                        }
                    }

                    // Also upgrade the curation CAS if it exists
                    try {
                        CAS cas = casStorageService.readCas(doc, CURATION_USER);
                        annotationService.upgradeCas(cas, doc, CURATION_USER);
                        casStorageService.writeCas(doc, cas, CURATION_USER);
                    }
                    catch (FileNotFoundException e) {
                        // If there is no CAS file, we do not have to upgrade it. Ignoring.
                    }
                }

                updateTree();

                _target.add(ProjectCodebookPanel.this);
                _target.addChildren(getPage(), IFeedback.class);
            });
        }

        private void actionCancel(AjaxRequestTarget aTarget)
        {
            codebookSelectionForm.setModelObject(null);
            codebookDetailForm.setModelObject(null);
            tagSelectionPanel.setDefaultModelObject(null);
            tagEditorPanel.setDefaultModelObject(null);

            aTarget.add(ProjectCodebookPanel.this);
            aTarget.addChildren(getPage(), IFeedback.class);
        }

        public void updateParentChoicesForCodebook(Codebook currentCodebook)
        {
            List<Codebook> possibleParents = projectCodebookTreePanel.getProvider()
                    .getPossibleParents(currentCodebook);
            this.codebookParentSelection.updateParents(possibleParents);
            this.codebookParentSelection.removeFromParentChoices(currentCodebook);
        }

        @Override
        protected void onConfigure()
        {
            super.onConfigure();
            setVisible(getDefaultModelObject() != null);
            // hide and reset tag editor panel when the selected codebook changed
            tagEditorPanel.setDefaultModelObject(null);
            // update parent selections
            this.updateParentChoicesForCodebook(getModelObject());
        }

        public TextField<String> getUiName()
        {
            return uiName;
        }
    }
}
