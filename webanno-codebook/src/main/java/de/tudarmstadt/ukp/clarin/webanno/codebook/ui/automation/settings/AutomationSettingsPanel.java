/*
 * Copyright 2020
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.settings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import com.googlecode.wicket.jquery.ui.widget.progressbar.ProgressBar;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTag;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service.CodebookAutomationService;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior;

public class AutomationSettingsPanel
    extends Panel
{

    public static final String MID = "automationSettingsPanel";
    public static final String METADATA_PANEL_MID = "modelMetadataPanel";
    public static final String TAG_LABEL_MAPPING_PANEL_MID = "tagLabelMappingPanel";
    public static final String TAG_LABEL_MAPPING_FORM_MID = "tagLabelMappingForm";
    private static final long serialVersionUID = -685340838123105638L;
    private static final String AUTOMATION_AVAILABLE = "alert-success";
    private static final String AUTOMATION_UNAVAILABLE = "alert-danger";
    private final IModel<Project> projectModel;
    private boolean automationAvailable;
    private boolean predictionStarted;
    private WebMarkupContainer metadataPanel;
    private WebMarkupContainer tagLabelMappingPanel;
    private Form<TagLabelMappingFormModel> tagLabelMappingForm;
    private LambdaAjaxLink startPredictionsButton;
    private Form<Void> predictionProgressBarForm;
    private ProgressBar predictionProgressBar;
    private AbstractAjaxTimerBehavior progressPollTimer;
    private IModel<ModelMetadata> metadata;
    private @SpringBean UserDao userService;
    private @SpringBean DocumentService documentService;
    private @SpringBean CodebookSchemaService codebookSchemaService;
    private @SpringBean CodebookAutomationService codebookAutomationService;

    public AutomationSettingsPanel(IModel<Codebook> model, IModel<Project> aProjectModel)
    {
        super(MID, model);
        this.projectModel = aProjectModel;

        this.predictionStarted = false;

        // automation available message
        checkAutomationAvailable();
        createOrUpdateAutomationAvailableAlert();

        // model metadata
        createOrUpdateModelMetadataPanel();

        // tag label mapping
        createOrUpdateTagLabelMappingPanel();

        // start predictions button
        createOrUpdateStartPredictionsButton();

        // prediction progress bar
        createPredictionProgressBar();

        this.setVisible(model.getObject() != null);
        this.setOutputMarkupPlaceholderTag(true);
    }

    private void createOrUpdateStartPredictionsButton()
    {
        startPredictionsButton = new LambdaAjaxLink("startPredictionsButton",
                this::actionStartPredictions);

        startPredictionsButton.addOrReplace(new Label("startPredictionsButtonLabel",
                new StringResourceModel("startPredictionsButtonLabel.start")));

        startPredictionsButton.setOutputMarkupId(true);

        Codebook cb = getModelObject();
        startPredictionsButton
                .add(LambdaBehavior.enabledWhen(() -> automationAvailable && !predictionStarted));
        startPredictionsButton.add(LambdaBehavior.visibleWhen(() -> automationAvailable));

        this.addOrReplace(startPredictionsButton);
    }

    private void createPredictionProgressBar()
    {
        // poll timer
        progressPollTimer = new AbstractAjaxTimerBehavior(Duration.ONE_SECOND)
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onTimer(AjaxRequestTarget target)
            {
                // get the progress from the service
                if (!codebookAutomationService.isPredictionInProgress(getModelObject())) {
                    predictionProgressBar.forward(target, 100);
                    this.stop(target);
                }

                predictionProgressBar.forward(target,
                        100 * (int) Math.floor(1 - codebookAutomationService
                                .getPredictionInProgressFraction(getModelObject())));
            }
        };
        progressPollTimer.stop(null);

        // ProgressBar
        this.predictionProgressBar = new ProgressBar("predictionProgressBar", Model.of(0))
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onValueChanged(IPartialPageRequestHandler handler)
            {
                super.onValueChanged(handler);
            }

            @Override
            protected void onComplete(AjaxRequestTarget target)
            {
                super.onComplete(target);
                // stop timer and flag
                progressPollTimer.stop(target);
                predictionStarted = false;

                // update the button text and enable it
                startPredictionsButton.addOrReplace(new Label("startPredictionsButtonLabel",
                        new StringResourceModel("startPredictionsButtonLabel.start")));
                startPredictionsButton.setEnabled(true);

                target.add(startPredictionsButton, predictionProgressBarForm,
                        predictionProgressBar);
            }
        };
        predictionProgressBar.setOutputMarkupId(true);

        predictionProgressBarForm = new Form<>("predictionProgressBarForm");
        predictionProgressBarForm.add(progressPollTimer);
        predictionProgressBarForm.addOrReplace(predictionProgressBar);
        predictionProgressBarForm.add(LambdaBehavior.visibleWhen(() -> predictionStarted));

        this.addOrReplace(predictionProgressBarForm);
    }

    protected void actionStartPredictions(AjaxRequestTarget aTarget)
    {
        Codebook cb = getModelObject();
        Project project = this.projectModel.getObject();
        String userName = userService.getCurrentUsername();

        // start async prediction for all docs in the project
        for (SourceDocument sdoc : documentService.listSourceDocuments(project)) {
            try {
                // FIXME
                Thread.sleep(5);
                codebookAutomationService.predictTagAsync(cb, project, sdoc, userName);
            }
            catch (ApiException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        // start the poll timer
        progressPollTimer.restart(aTarget);

        // update the button (enabled flag handled by the lambda behaviour)
        startPredictionsButton.addOrReplace(new Label("startPredictionsButtonLabel",
                new StringResourceModel("startPredictionsButtonLabel.started")));

        aTarget.add(startPredictionsButton, predictionProgressBarForm);
    }

    private void createOrUpdateModelMetadataPanel()
    {
        ModelMetadata metadata = null;
        if (metadataPanel == null) {
            metadataPanel = new WebMarkupContainer(METADATA_PANEL_MID);

            TextField<String> modelType = new TextField<>("modelType");
            modelType.setEnabled(false).setOutputMarkupId(true);

            TextField<String> embeddings = new TextField<>("embeddings");
            embeddings.setEnabled(false).setOutputMarkupId(true);

            TextField<String> timestamp = new TextField<>("timestamp");
            timestamp.setEnabled(false).setOutputMarkupId(true);

            metadataPanel.add(modelType);
            metadataPanel.add(embeddings);
            metadataPanel.add(timestamp);

            metadataPanel.setOutputMarkupPlaceholderTag(true);
            metadataPanel.add(LambdaBehavior.visibleWhen(() -> automationAvailable));
            this.addOrReplace(metadataPanel);
        }
        else {
            try {
                metadata = getModelMetadata();
            }
            catch (ApiException exception) {
                // TODO what to do?
                exception.printStackTrace();
            }
        }

        metadataPanel.setDefaultModel(new CompoundPropertyModel<>(metadata));

        // labels
        List<PairPojo> labels = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, String> e : metadata.getLabels().entrySet())
                labels.add(new PairPojo(e.getKey(), e.getValue()));
        metadataPanel.addOrReplace(new PropertyListView<PairPojo>("labelsListView", labels)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("key", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        }.setReuseItems(true));

        // evaluation
        List<PairPojo> eval = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, BigDecimal> e : metadata.getEvaluation().entrySet())
                eval.add(new PairPojo(e.getKey(), e.getValue().toPlainString()));
        metadataPanel.addOrReplace(new PropertyListView<PairPojo>("evaluationListView", eval)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("key", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        }.setReuseItems(true));
    }

    private ModelMetadata getModelMetadata() throws ApiException
    {
        Codebook cb = getModelObject();
        if (cb == null || !codebookAutomationService.isAutomationAvailable(cb, true))
            return null;

        ModelMetadata data = null;
        try {
            data = codebookAutomationService.getModelMetadata(getModelObject());
        }
        catch (ApiException e) {
            e.printStackTrace();
        }
        this.metadata = Model.of(data);
        return data;
    }

    private void createOrUpdateTagLabelMappingPanel()
    {
        if (tagLabelMappingPanel == null) {
            tagLabelMappingPanel = new WebMarkupContainer(TAG_LABEL_MAPPING_PANEL_MID);
            tagLabelMappingForm = new Form<>(TAG_LABEL_MAPPING_FORM_MID);

            tagLabelMappingForm.setOutputMarkupId(true);
            tagLabelMappingPanel.add(tagLabelMappingForm);
            tagLabelMappingPanel.setOutputMarkupId(true);
            tagLabelMappingPanel.add(LambdaBehavior.visibleWhen(() -> automationAvailable));
        }

        List<String> labelChoices = new ArrayList<>();
        if (metadata != null && metadata.getObject() != null)
            for (Map.Entry<String, String> e : metadata.getObject().getLabels().entrySet())
                labelChoices.add(e.getKey());

        List<CodebookTag> tags = codebookSchemaService.listTags(this.getModelObject());
        tagLabelMappingForm.addOrReplace(new ListView<CodebookTag>("tagsListView", tags)
        {
            private static final long serialVersionUID = 7639470004828914942L;

            @Override
            protected void populateItem(ListItem<CodebookTag> item)
            {
                item.add(new Label("name", item.getModelObject().getName()));
                DropDownChoice<String> ddChoice = new BootstrapSelect<>("labelSelection",
                        Model.of(), labelChoices);
                ddChoice.add(new FormComponentUpdatingBehavior()
                {

                    private static final long serialVersionUID = -7017410239431970221L;

                    @Override
                    protected void onUpdate()
                    {
                        super.onUpdate();
                        codebookAutomationService.updateTagLabelMapping(
                                AutomationSettingsPanel.this.getModelObject(),
                                item.getModelObject().getName(), ddChoice.getModelObject());
                    }
                });
                item.add(ddChoice);
            }
        }.setReuseItems(true));

        this.addOrReplace(tagLabelMappingPanel);
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();
        this.checkAutomationAvailable();
        this.createOrUpdateAutomationAvailableAlert();
        this.createOrUpdateModelMetadataPanel();
        this.createOrUpdateTagLabelMappingPanel();
        this.createOrUpdateStartPredictionsButton();

        this.setVisible(this.getModelObject() != null);
    }

    private void checkAutomationAvailable()
    {
        try {
            if (this.getModelObject() != null)
                this.automationAvailable = codebookAutomationService
                        .isAutomationAvailable(this.getModelObject(), true);
            else
                this.automationAvailable = false;
        }
        catch (ApiException e) {
            this.automationAvailable = false;
            e.printStackTrace();
        }
    }

    private void createOrUpdateAutomationAvailableAlert()
    {
        Label label = new Label("automationAvailableAlert", new StringResourceModel(
                automationAvailable ? "automation.available" : "automation.unavailable"));
        label.add(new AttributeAppender("class",
                automationAvailable ? AUTOMATION_AVAILABLE : AUTOMATION_UNAVAILABLE, " "));
        label.setVisible(true);
        this.addOrReplace(label);
    }

    public Codebook getModelObject()
    {
        return (Codebook) this.getDefaultModelObject();
    }

    private static class PairPojo
        implements Serializable
    {
        private static final long serialVersionUID = 1294588462693119953L;
        public String key;
        public String value;

        public PairPojo(String key, String value)
        {
            this.key = key;
            this.value = value;
        }
    }

    private static class TagLabelMappingFormModel
        implements Serializable
    {
        private static final long serialVersionUID = -2729971443377393469L;
        public List<PairPojo> mappings;

        public TagLabelMappingFormModel()
        {
            this.mappings = new ArrayList<>();
        }
    }
}
