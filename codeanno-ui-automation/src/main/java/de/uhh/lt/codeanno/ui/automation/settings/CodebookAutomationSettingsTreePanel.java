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
package de.uhh.lt.codeanno.ui.automation.settings;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.tree.CodebookTreeProvider;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.model.CodebookNodeExpansion;
import de.uhh.lt.codeanno.tree.ui.CodebookTreePanel;

public class CodebookAutomationSettingsTreePanel
    extends CodebookTreePanel
{

    private static final long serialVersionUID = -4880127845755999769L;

    private final CodebookAutomationSettingsPanel codebookAutomationSettingsPanel;
    private final CodebookAutomationSettingsPanel.CodebookSelectionForm codebookSelectionForm;

    private @SpringBean CodebookSchemaService codebookSchemaService;

    public CodebookAutomationSettingsTreePanel(String aId, IModel<?> aModel,
            CodebookAutomationSettingsPanel codebookAutomationSettingsPanel,
            CodebookAutomationSettingsPanel.CodebookSelectionForm codebookSelectionForm)
    {
        super(aId, aModel);
        this.codebookAutomationSettingsPanel = codebookAutomationSettingsPanel;
        this.codebookSelectionForm = codebookSelectionForm;
    }

    @Override
    public void initCodebookTreeProvider()
    {
        Project project = (Project) this.getDefaultModelObject();
        // get all codebooks and init the provider
        List<Codebook> codebooks = this.codebookSchemaService.listCodebook(project);
        this.provider = new CodebookTreeProvider(codebooks);
    }

    private Folder<CodebookNode> buildFolderComponent(String id, IModel<CodebookNode> model)
    {
        Folder<CodebookNode> folder = new Folder<CodebookNode>(id, tree, model)
        {

            private static final long serialVersionUID = 1L;

            /**
             * Always clickable.
             */
            @Override
            protected boolean isClickable()
            {
                return true;
            }

            @Override
            protected void onClick(Optional<AjaxRequestTarget> targetOptional)
            {
                targetOptional.ifPresent(this::showCodebookDetails);

                if (!CodebookNodeExpansion.get().contains(this.getModelObject())) {
                    CodebookNodeExpansion.get().add(this.getModelObject());
                    tree.expand(this.getModelObject());
                }
                else {
                    CodebookNodeExpansion.get().remove(this.getModelObject());
                    tree.collapse(this.getModelObject());
                }
            }

            private void showCodebookDetails(AjaxRequestTarget target)
            {
                codebookSelectionForm.setModelObject(getModelObject().getCodebook());
                target.add(codebookAutomationSettingsPanel);
                target.add(codebookSelectionForm);
            }
        };

        // remove tree theme specific styling of the labels
        folder.streamChildren().forEach(
                component -> component.add(new AttributeModifier("class", new Model<>("tree-label"))
                {
                    private static final long serialVersionUID = -3206327021544384435L;

                    @Override
                    protected String newValue(String currentValue, String valueToRemove)
                    {
                        return currentValue.replaceAll(valueToRemove, "");
                    }
                }));

        return folder;
    }

    @Override
    public void initTree()
    {
        this.initCodebookTreeProvider();
        tree = new NestedTree<CodebookNode>("codebookAutomationSettingsTreePanel", this.provider,
                new CodebookNodeExpansionModel())
        {
            private static final long serialVersionUID = 2285250157811357702L;

            @Override
            protected Component newContentComponent(String id, IModel<CodebookNode> model)
            {
                return buildFolderComponent(id, model);
            }
        };

        this.applyTheme();
        tree.setOutputMarkupId(true);
        this.addOrReplace(tree);
    }

    // package private by intention
    void expandAll()
    {
        CodebookNodeExpansion.get().expandAll();
    }

    // package private by intention
    void collapseAll()
    {
        CodebookNodeExpansion.get().collapseAll();
    }
}
