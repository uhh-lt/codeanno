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

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service.CodebookAutomationService;

public class AutomationSettingsPanel
    extends Panel
{

    private static final long serialVersionUID = -685340838123105638L;

    public static final String MID = "automationSettingsPanel";
    public static final String METADATA_PANEL_MID = "modelMetadataPanel";

    private static final String AUTOMATION_AVAILABLE = "alert-success";
    private static final String AUTOMATION_UNAVAILABLE = "alert-danger";

    private boolean automationAvailable;

    private WebMarkupContainer metadataPanel;

    private @SpringBean CodebookAutomationService codebookAutomationService;

    public AutomationSettingsPanel()
    {
        super(MID);
    }

    public AutomationSettingsPanel(IModel<Codebook> model)
    {
        super(MID, model);

        // automation available message
        checkAutomationAvailable();
        createAutomationAvailableAlert();

        // model metadata
        createModelMetadataPanel();

        this.setVisible(model.getObject() != null);
        this.setOutputMarkupPlaceholderTag(true);
    }

    private void createModelMetadataPanel()
    {
        ModelMetadata metadata = null;

        try {
            if (this.getModelObject() != null)
                metadata = codebookAutomationService.getModelMetadata(getModelObject());
        }
        catch (ApiException e) {
            e.printStackTrace();
        }

        metadataPanel = new WebMarkupContainer(METADATA_PANEL_MID);
        metadataPanel.setDefaultModel(new CompoundPropertyModel<>(metadata));

        // labels
        List<PairPojo> labels = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, String> e : metadata.getLabels().entrySet())
                labels.add(new PairPojo(e.getKey(), e.getValue()));
        metadataPanel.add(new PropertyListView<PairPojo>("labelsListView", labels)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("key", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        });

        // evaluation
        List<PairPojo> eval = new ArrayList<>();
        if (metadata != null)
            for (Map.Entry<String, BigDecimal> e : metadata.getEvaluation().entrySet())
                eval.add(new PairPojo(e.getKey(), e.getValue().toPlainString()));
        metadataPanel.add(new PropertyListView<PairPojo>("evaluationListView", eval)
        {
            private static final long serialVersionUID = 9206487155367441591L;

            @Override
            protected void populateItem(ListItem<PairPojo> item)
            {
                item.add(new Label("key", item.getModelObject().key));
                item.add(new Label("value", item.getModelObject().value));
            }
        });

        metadataPanel
                .add(new TextField<String>("modelType").setEnabled(false).setOutputMarkupId(true));
        metadataPanel
                .add(new TextArea<String>("embeddings").setEnabled(false).setOutputMarkupId(true));
        metadataPanel
                .add(new TextField<String>("timestamp").setEnabled(false).setOutputMarkupId(true));

        metadataPanel.setOutputMarkupPlaceholderTag(true);
        metadataPanel.setVisible(metadata != null);
        this.addOrReplace(metadataPanel);
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();
        this.checkAutomationAvailable();
        this.createAutomationAvailableAlert();
        this.createModelMetadataPanel();
        this.setVisible(this.getModelObject() != null);
    }

    private void checkAutomationAvailable()
    {
        try {
            if (this.getModelObject() != null)
                this.automationAvailable = codebookAutomationService
                        .automationIsAvailable(this.getModelObject(), true);
            else
                this.automationAvailable = false;
        }
        catch (ApiException e) {
            this.automationAvailable = false;
            e.printStackTrace();
        }
    }

    private void createAutomationAvailableAlert()
    {
        Label label = new Label("automationAvailableAlert", new StringResourceModel(
                automationAvailable ? "automation.available" : "automation.unavailable"));
        label.add(new AttributeAppender("class",
                automationAvailable ? AUTOMATION_AVAILABLE : AUTOMATION_UNAVAILABLE, " "));
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
}
