/*
 * Copyright 2020
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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.settings;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTag;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service.CodebookAutomationService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.tree.CodebookNodeExpansion;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.support.DescriptionTooltipBehavior;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelBase;

public class CodebookAutomationSettingsPanel
    extends ProjectSettingsPanelBase
{
    private static final long serialVersionUID = -7870526462864489252L;

    private static final String AUTOMATION_SERVICE_ALIVE = "btn-success";
    private static final String AUTOMATION_SERVICE_DEAD = "btn-danger";
    private final LambdaAjaxLink performHeartbeatCheckButton;
    private final Model<String> deadOrAliveModel;
    private final CodebookSelectionForm codebookSelectionForm;
    private final AutomationSettingsPanel automationSettingsPanel;
    private final CodebookAutomationSettingsTreePanel codebookAutomationSettingsTreePanel;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CodebookAutomationService codebookAutomationService;
    private WebMarkupContainer propertiesCard;
    private WebMarkupContainer tagsCard;

    CodebookAutomationSettingsPanel(String id, final IModel<Project> aProjectModel)
    {
        super(id, aProjectModel);
        setOutputMarkupId(true);

        // CB Selection Form
        codebookSelectionForm = new CodebookSelectionForm();
        add(codebookSelectionForm);

        // properties card
        createPropertiesCard(null);

        // add and init tree
        codebookAutomationSettingsTreePanel = new CodebookAutomationSettingsTreePanel(
                "codebookAutomationSettingsTreePanel", aProjectModel, this, codebookSelectionForm);
        updateTree();
        codebookAutomationSettingsTreePanel.setOutputMarkupId(true);
        // add tree to selection form
        codebookSelectionForm.add(codebookAutomationSettingsTreePanel);

        // cb automation settings panel
        this.automationSettingsPanel = new AutomationSettingsPanel(aProjectModel.getObject());
        this.addOrReplace(automationSettingsPanel);

        // heartbeat check button
        performHeartbeatCheckButton = new LambdaAjaxLink("performHeartbeatCheckButton",
                this::actionPerformHeartbeatCheck);
        deadOrAliveModel = new Model<>(
                codebookAutomationService.performHeartbeatCheck() ? AUTOMATION_SERVICE_ALIVE
                        : AUTOMATION_SERVICE_DEAD);
        performHeartbeatCheckButton.add(AttributeModifier.append("class", deadOrAliveModel));
        performHeartbeatCheckButton.setOutputMarkupId(true);
        add(performHeartbeatCheckButton);

    }

    private void actionPerformHeartbeatCheck(AjaxRequestTarget target)
    {
        boolean alive = this.codebookAutomationService.performHeartbeatCheck();
        this.deadOrAliveModel.setObject(alive ? AUTOMATION_SERVICE_ALIVE : AUTOMATION_SERVICE_DEAD);

        if (!alive)
            this.updateAutomationSettingsPanel(null);
        else
            this.updateAutomationSettingsPanel(codebookSelectionForm.getModelObject());

        target.add(automationSettingsPanel);
        target.add(performHeartbeatCheckButton);
    }

    private void createPropertiesCard(Codebook selectedCodebook)
    {
        String id = "propertiesCard";

        propertiesCard = new WebMarkupContainer(id);
        propertiesCard.setDefaultModel(new CompoundPropertyModel<>(selectedCodebook));

        propertiesCard
                .add(new TextField<String>("uiName").setEnabled(false).setOutputMarkupId(true));
        propertiesCard
                .add(new TextArea<String>("description").setEnabled(false).setOutputMarkupId(true));
        propertiesCard
                .add(new TextField<String>("parent").setEnabled(false).setOutputMarkupId(true));

        propertiesCard.setOutputMarkupPlaceholderTag(true);
        propertiesCard.setVisible(selectedCodebook != null);
        this.addOrReplace(propertiesCard);

        // tags card
        createTagsCard(selectedCodebook);
    }

    private void createTagsCard(Codebook selectedCodebook)
    {
        String id = "tagsCard";
        tagsCard = new WebMarkupContainer(id);

        List<CodebookTag> tags = null;
        if (selectedCodebook != null)
            tags = codebookService.listTags(selectedCodebook);
        else
            tags = Collections.emptyList();

        tagsCard.add(new PropertyListView<CodebookTag>("tagListView", tags)
        {
            private static final long serialVersionUID = 5328960765143797557L;

            protected void populateItem(ListItem item)
            {
                CodebookTag tag = (CodebookTag) item.getModelObject();
                item.add(new Label("name")
                        .add(new DescriptionTooltipBehavior(tag.getName(), tag.getDescription())));
            }
        }.setOutputMarkupId(true));

        tagsCard.setOutputMarkupPlaceholderTag(true);
        tagsCard.setVisible(selectedCodebook != null);
        propertiesCard.addOrReplace(tagsCard);
    }

    private void updateAutomationSettingsPanel(Codebook selectedCodebook)
    {
        this.automationSettingsPanel.setCodebook(selectedCodebook);
    }

    private void updateTree()
    {
        codebookAutomationSettingsTreePanel.initTree();
        codebookAutomationSettingsTreePanel.expandAll();
    }

    @Override
    protected void onModelChanged()
    {
        super.onModelChanged();

        codebookSelectionForm.setDefaultModel(null);
        createPropertiesCard(null);
        updateAutomationSettingsPanel(null);
    }

    class CodebookSelectionForm
        extends Form<Codebook>
    {

        private static final long serialVersionUID = -7757401460625924337L;

        @SuppressWarnings({})
        public CodebookSelectionForm()
        {
            super("codebookSelectionForm", Model.of());

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

        @Override
        protected void onModelChanged()
        {
            super.onModelChanged();

            createPropertiesCard(getModelObject());
            updateAutomationSettingsPanel(getModelObject());
        }
    }
}
