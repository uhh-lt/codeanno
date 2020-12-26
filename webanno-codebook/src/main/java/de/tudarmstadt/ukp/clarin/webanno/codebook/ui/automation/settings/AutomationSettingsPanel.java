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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTag;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service.CodebookAutomationService;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior;

public class AutomationSettingsPanel
    extends Panel
{
    private static final long serialVersionUID = -685340838123105638L;

    public static final String MID = "automationSettingsPanel";
    public static final String METADATA_PANEL_MID = "modelMetadataPanel";
    public static final String TAG_LABEL_MAPPING_PANEL_MID = "tagLabelMappingPanel";
    public static final String TAG_LABEL_MAPPING_FORM_MID = "tagLabelMappingForm";

    private static final String AUTOMATION_AVAILABLE = "alert-success";
    private static final String AUTOMATION_UNAVAILABLE = "alert-danger";

    private WebMarkupContainer modelVersionSelectionPanel;
    private WebMarkupContainer metadataPanel;
    private WebMarkupContainer tagLabelMappingPanel;
    private Form<TagLabelMappingFormModel> tagLabelMappingForm;
    private LambdaAjaxLink startPredictionsButton;

    private @SpringBean UserDao userService;
    private @SpringBean CodebookSchemaService codebookSchemaService;
    private @SpringBean CodebookAutomationService codebookAutomationService;

    public AutomationSettingsPanel(Project aProject)
    {
        super(MID, Model.of(new AutomationSettingsPanelModel(aProject)));

        // model version selection
        createOrUpdateModelVersionSelection();

        // automation available message
        createOrUpdateAutomationAvailableAlert();

        // model metadata
        createOrUpdateModelMetadataPanel();

        // tag label mapping
        createOrUpdateTagLabelMappingPanel();

        // start predictions button
        createOrUpdateStartPredictionsButton();

        this.setVisible(getModelObject().getCodebook() != null);
        this.setOutputMarkupPlaceholderTag(true);
    }

    private void createOrUpdateModelVersionSelection()
    {

        this.modelVersionSelectionPanel = new WebMarkupContainer("modelVersionSelectionPanel");

        // get the available models for the Codebook
        List<String> modelVersionChoices = null;
        try {
            List<ModelMetadata> availableModels = this
                    .getAvailableModels(getModelObject().getCodebook() != null);
            modelVersionChoices = availableModels.stream().map(ModelMetadata::getVersion)
                    .collect(Collectors.toList());
            if (modelVersionChoices.size() == 1)
                getModelObject().setModelVersion(modelVersionChoices.get(0));
        }
        catch (ApiException e) {
            e.printStackTrace();
        }

        // provide the choices in the selection
        DropDownChoice<String> modelVersionSelection = new BootstrapSelect<>(
                "modelVersionSelection", Model.of(), modelVersionChoices);
        modelVersionSelection.add(new FormComponentUpdatingBehavior()
        {

            private static final long serialVersionUID = -7017410239431970221L;

            @Override
            protected void onUpdate()
            {
                super.onUpdate();
                // set model version
                getModelObject()
                        .setModelVersion(this.getFormComponent().getDefaultModelObjectAsString());

                createOrUpdateStartPredictionsButton();
            }
        });

        modelVersionSelection.add(new FormComponentUpdatingBehavior()
        {
            private static final long serialVersionUID = 1943677201058272469L;

            @Override
            protected void onUpdate()
            {
                super.onUpdate();
                AutomationSettingsPanel.this
                        .setModelVersion(modelVersionSelection.getModelObject());
            }
        });

        this.modelVersionSelectionPanel.add(modelVersionSelection);
        this.addOrReplace(modelVersionSelectionPanel);
    }

    private List<ModelMetadata> getAvailableModels(boolean updateCache) throws ApiException
    {
        if (updateCache) {
            List<ModelMetadata> availableModels = this.codebookAutomationService
                    .getAvailableModels(getModelObject().getCodebook());
            // safe for later use
            this.getModelObject().setAvailableModels(availableModels);
        }
        return new ArrayList<>(this.getModelObject().getAvailableModels().values());
    }

    private void createOrUpdateStartPredictionsButton()
    {
        startPredictionsButton = new LambdaAjaxLink("startPredictionsButton",
                this::actionStartPredictions);

        startPredictionsButton.setOutputMarkupId(true);

        // update the button (enabled flag handled by the lambda behaviour)
        boolean predictionInProgress = this.getModelObject().isPredictionInProgress();
        String resKey = predictionInProgress ? "startPredictionsButtonLabel.started"
                : "startPredictionsButtonLabel.start";
        Label label = new Label("startPredictionsButtonLabel", new StringResourceModel(resKey));
        startPredictionsButton.addOrReplace(label);

        startPredictionsButton.setVisible(this.isAutomationAvailable());
        startPredictionsButton.add(LambdaBehavior.enabledWhen(
                () -> !predictionInProgress && this.getModelObject().getModelMetadata() != null));

        tagLabelMappingPanel.addOrReplace(startPredictionsButton);
    }

    protected void actionStartPredictions(AjaxRequestTarget aTarget)
    {
        Codebook cb = this.getModelObject().getCodebook();
        Project project = this.getModelObject().getProject();
        String userName = userService.getCurrentUsername();
        String modelVersion = this.getModelObject().getModelVersion();

        // start async prediction for all docs in the project
        try {
            codebookAutomationService.predictTagsAsync(cb, project, userName, modelVersion, this);
            // disable the buttons
            this.createOrUpdateStartPredictionsButton();
            aTarget.add(this.startPredictionsButton);
        }
        catch (ApiException exception) {
            exception.printStackTrace();
        }

    }

    private void createOrUpdateModelMetadataPanel()
    {
        ModelMetadata metadata = null;
        if (metadataPanel == null) {
            metadataPanel = new WebMarkupContainer(METADATA_PANEL_MID);

            TextField<String> codebookName = new TextField<>("codebookName");
            codebookName.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(codebookName);

            TextField<String> modelVersion = new TextField<>("version");
            modelVersion.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(modelVersion);

            TextField<String> datasetVersion = new TextField<>("datasetVersion");
            datasetVersion.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(datasetVersion);

            TextField<String> modelType = new TextField<>("modelType");
            modelType.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(modelType);

            TextField<String> embeddingType = new TextField<>("modelConfig.embeddingType");
            embeddingType.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(embeddingType);

            TextField<String> dropout = new TextField<>("modelConfig.dropout");
            dropout.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(dropout);

            TextField<String> activationFn = new TextField<>("modelConfig.activationFn");
            activationFn.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(activationFn);

            TextField<String> optimizer = new TextField<>("modelConfig.optimizer");
            optimizer.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(optimizer);

            TextField<String> earlyStopping = new TextField<>("modelConfig.earlyStopping");
            earlyStopping.setEnabled(false).setOutputMarkupId(true);
            metadataPanel.add(earlyStopping);

            metadataPanel.setOutputMarkupPlaceholderTag(true);
            metadataPanel.add(LambdaBehavior.visibleWhen(this::isAutomationAvailable));
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
        metadataPanel.addOrReplace(new PropertyListView<>("labelsListView", labels)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("key", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        }.setReuseItems(true));

        // hidden units
        List<PairPojo> hiddenUnits = new ArrayList<>();
        if (metadata != null) {
            List<Integer> hidden = metadata.getModelConfig().getHiddenUnits();
            for (int i = 0; i < hidden.size(); i++)
                hiddenUnits.add(new PairPojo(Integer.toString(i), hidden.get(i).toString()));
        }

        metadataPanel.addOrReplace(new PropertyListView<>("hiddenUnitsListView", hiddenUnits)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("layerNumber", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        }.setReuseItems(true));

        // evaluation
        List<PairPojo> eval = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, Double> e : metadata.getEvaluation().entrySet())
                eval.add(new PairPojo(e.getKey(), e.getValue().toString()));
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
        this.getAvailableModels(false);
        return getModelObject().getModelMetadata();
        // if (getModelObject().getAvailableModels().isEmpty()) {
        //
        // Codebook cb = this.getModelObject().getCodebook();
        // String modelVersion = this.getModelObject().getModelVersion();
        // if (cb == null || !codebookAutomationService.isAutomationAvailable(cb, modelVersion,
        // false)) return null;
        //
        // ModelMetadata metadata = null;
        // try {
        // metadata = codebookAutomationService.getModelMetadata(cb, modelVersion);
        // } catch (ApiException e) {
        // e.printStackTrace();
        // }
        // return metadata;
        // }
    }

    private void createOrUpdateTagLabelMappingPanel()
    {
        if (tagLabelMappingPanel == null) {
            tagLabelMappingPanel = new WebMarkupContainer(TAG_LABEL_MAPPING_PANEL_MID);
            tagLabelMappingForm = new Form<>(TAG_LABEL_MAPPING_FORM_MID);

            tagLabelMappingForm.setOutputMarkupId(true);
            tagLabelMappingPanel.add(tagLabelMappingForm);
            tagLabelMappingPanel.setOutputMarkupId(true);
            tagLabelMappingPanel.add(LambdaBehavior.visibleWhen(this::isAutomationAvailable));
        }

        ModelMetadata metadata = this.getModelObject().getModelMetadata();
        Codebook cb = this.getModelObject().getCodebook();

        List<String> labelChoices = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, String> e : metadata.getLabels().entrySet())
                labelChoices.add(e.getKey());

        List<CodebookTag> tags = codebookSchemaService.listTags(cb);
        tagLabelMappingForm.addOrReplace(new ListView<>("tagsListView", tags)
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
                        codebookAutomationService.updateTagLabelMapping(cb,
                                item.getModelObject().getName(), ddChoice.getModelObject());
                    }
                });
                item.add(ddChoice);
            }
        }.setReuseItems(true));

        createOrUpdateStartPredictionsButton();

        this.addOrReplace(tagLabelMappingPanel);
    }

    private boolean isAutomationAvailable()
    {
        return this.isAutomationAvailable(false);
    }

    private boolean isAutomationAvailable(boolean updateCache)
    {
        Codebook cb = this.getModelObject().getCodebook();
        try {
            return !codebookSchemaService.listTags(cb).isEmpty()
                    && !this.getAvailableModels(updateCache).isEmpty();
        }
        catch (ApiException e) {
            e.printStackTrace();
            return false;
        }

        // boolean available = false;
        // try {
        // String modelVersion = this.getModelObject().getModelVersion();
        // if (cb != null)
        // available = codebookAutomationService.isAutomationAvailable(cb, modelVersion,
        // updateCache);
        // }
        // catch (ApiException e) {
        // e.printStackTrace();
        // }
        // return available;
    }

    private void createOrUpdateAutomationAvailableAlert()
    {
        boolean automationAvailable = isAutomationAvailable(true);
        Label label = new Label("automationAvailableAlert", new StringResourceModel(
                automationAvailable ? "automation.available" : "automation.unavailable"));
        label.add(new AttributeAppender("class",
                automationAvailable ? AUTOMATION_AVAILABLE : AUTOMATION_UNAVAILABLE, " "));
        label.setVisible(true);
        this.addOrReplace(label);
    }

    private AutomationSettingsPanelModel getModelObject()
    {
        return (AutomationSettingsPanelModel) this.getDefaultModelObject();
    }

    public void setCodebook(Codebook cb)
    {
        this.getModelObject().resetData();
        this.getModelObject().setCodebook(cb);

        this.createOrUpdateAutomationAvailableAlert();
        this.createOrUpdateModelVersionSelection();
        this.createOrUpdateModelMetadataPanel();
        this.createOrUpdateTagLabelMappingPanel();

        this.setVisible(this.getModelObject().getCodebook() != null);

    }

    private void setModelVersion(String modelVersion)
    {
        this.getModelObject().setModelVersion(modelVersion);
        createOrUpdateModelMetadataPanel();
        createOrUpdateTagLabelMappingPanel();
    }

    public void setPredictionInProgress(boolean inProgress)
    {
        /* This should only get called from outside! */
        this.getModelObject().setPredictionInProgress(inProgress);
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
