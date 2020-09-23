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

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTag;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.tree.CodebookTagSelectionComboBox;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.OverviewListChoice;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.ComponentPropertyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.tree.CodebookNodeExpansion;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.support.spring.ApplicationEventPublisherHolder;
import de.tudarmstadt.ukp.clarin.webanno.ui.core.settings.ProjectSettingsPanelBase;

import java.util.Collections;
import java.util.List;

public class CodebookAutomationSettingsPanel
    extends ProjectSettingsPanelBase
{
    private static final Logger LOG = LoggerFactory.getLogger(
            de.tudarmstadt.ukp.clarin.webanno
                    .codebook.ui.automation.settings.CodebookAutomationSettingsPanel.class);

    private static final long serialVersionUID = -7870526462864489252L;

    private @SpringBean ProjectService projectService;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean ApplicationEventPublisherHolder applicationEventPublisherHolder;
    private @SpringBean UserDao userRepository;


    private CodebookSelectionForm codebookSelectionForm;

    private WebMarkupContainer propertiesCard;
    private WebMarkupContainer tagsCard;

    private CodebookAutomationSettingsTreePanel codebookAutomationSettingsTreePanel;


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
        codebookAutomationSettingsTreePanel = new CodebookAutomationSettingsTreePanel("codebookAutomationSettingsTreePanel",
                aProjectModel, this, codebookSelectionForm);
        updateTree();
        codebookAutomationSettingsTreePanel.setOutputMarkupId(true);
        // add tree to selection form
        codebookSelectionForm.add(codebookAutomationSettingsTreePanel);

    }

    private void createPropertiesCard(Codebook selectedCodebook) {
        String id = "propertiesCard";

        propertiesCard = new WebMarkupContainer(id);
        propertiesCard.setDefaultModel(new CompoundPropertyModel<>(selectedCodebook));

        propertiesCard.add(new TextField<String>("uiName")
                .setEnabled(false).setOutputMarkupId(true));
        propertiesCard.add(new TextArea<String>("description")
                .setEnabled(false).setOutputMarkupId(true));
        propertiesCard.add(new TextField<String>("parent")
                .setEnabled(false).setOutputMarkupId(true));

        propertiesCard.setOutputMarkupPlaceholderTag(true);
        propertiesCard.setVisible(selectedCodebook != null);
        this.addOrReplace(propertiesCard);

        // tags card
        createTagsCard(null);
    }

    private void createTagsCard(Codebook selectedCodebook) {
        String id = "tagsCard";
        tagsCard = new WebMarkupContainer(id);

        List<CodebookTag> tags = null;
        if (selectedCodebook != null)
            tags = codebookService.listTags(selectedCodebook);
        else
            tags = Collections.emptyList();

        tagsCard.add(new PropertyListView<CodebookTag>("tagListView", tags) {
            private static final long serialVersionUID = 5328960765143797557L;

            protected void populateItem(ListItem item) {
                item.add(new Label("name"));
            }
        }.setOutputMarkupId(true));

        tagsCard.setOutputMarkupPlaceholderTag(true);
        tagsCard.setVisible(selectedCodebook != null);
        propertiesCard.addOrReplace(tagsCard);
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

        createPropertiesCard(null);
        updateTree();
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
        protected void onModelChanged() {
            super.onModelChanged();

            createPropertiesCard(getModelObject());
            createTagsCard(getModelObject());
        }
    }
}