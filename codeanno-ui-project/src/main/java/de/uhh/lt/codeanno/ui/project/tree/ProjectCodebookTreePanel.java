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
package de.uhh.lt.codeanno.ui.project.tree;

import java.util.List;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.tree.CodebookTreeProvider;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.model.CodebookNodeExpansion;
import de.uhh.lt.codeanno.tree.ui.CodebookTreePanel;
import de.uhh.lt.codeanno.ui.project.CodebookTagEditorPanel;
import de.uhh.lt.codeanno.ui.project.CodebookTagSelectionPanel;
import de.uhh.lt.codeanno.ui.project.ProjectCodebookPanel;

public class ProjectCodebookTreePanel
    extends CodebookTreePanel
{

    private static final long serialVersionUID = -4880127845755999769L;

    private final ProjectCodebookPanel projectCodebookPanel;
    private final ProjectCodebookPanel.CodebookDetailForm codebookDetailForm;
    private final CodebookTagSelectionPanel tagSelectionPanel;
    private final CodebookTagEditorPanel tagEditorPanel;

    @SpringBean
    CodebookSchemaService codebookSchemaService;

    public ProjectCodebookTreePanel(String aId, IModel<?> aModel,
            ProjectCodebookPanel projectCodebookPanel,
            ProjectCodebookPanel.CodebookDetailForm codebookDetailForm,
            CodebookTagSelectionPanel tagSelectionPanel, CodebookTagEditorPanel tagEditorPanel)
    {
        super(aId, aModel);
        this.projectCodebookPanel = projectCodebookPanel;
        this.codebookDetailForm = codebookDetailForm;
        this.tagSelectionPanel = tagSelectionPanel;
        this.tagEditorPanel = tagEditorPanel;
    }

    @Override
    public void initCodebookTreeProvider()
    {
        Project project = (Project) this.getDefaultModelObject();
        // get all codebooks and init the provider
        List<Codebook> codebooks = this.codebookSchemaService.listCodebook(project);
        this.provider = new CodebookTreeProvider(codebooks);
    }

    private OrderingCodebookFolder buildFolderComponent(String id, IModel<CodebookNode> model)
    {
        OrderingCodebookFolder folder = new OrderingCodebookFolder(id, tree, model, this,
                codebookSchemaService)
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
                // onclick toggle expand / collapse
                AjaxRequestTarget _target = targetOptional.get();
                this.showCodebookEditors(_target);
                if (!CodebookNodeExpansion.get().contains(this.getModelObject())) {
                    CodebookNodeExpansion.get().add(this.getModelObject());
                    tree.expand(this.getModelObject());
                }
                else {
                    CodebookNodeExpansion.get().remove(this.getModelObject());
                    tree.collapse(this.getModelObject());
                }
            }

            @Override
            protected Component newLabelComponent(String id, IModel<CodebookNode> model)
            {
                return super.newLabelComponent(id, model);
            }

            private void showCodebookEditors(AjaxRequestTarget _target)
            {
                codebookDetailForm.setModelObject(getModelObject().getCodebook());
                // remove current codebook from parent selection
                // (not working in codebookDetailForm.onModelChanged()..?!)
                codebookDetailForm
                        .updateParentChoicesForCodebook(this.getModelObject().getCodebook());
                // ProjectCodebookPanel.CodebookSelectionForm.this.setVisible(true);

                tagSelectionPanel.setDefaultModelObject(getModelObject().getCodebook());
                tagSelectionPanel.setVisible(true);
                tagEditorPanel.setVisible(true);
                // TODO check if available
                _target.add(codebookDetailForm);
                _target.add(tagSelectionPanel);
                _target.add(tagEditorPanel);
            }
        };

        return folder;
    }

    @Override
    public void initTree()
    {
        this.initCodebookTreeProvider();
        tree = new NestedTree<CodebookNode>("projectCodebookTree", this.provider,
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

    public void expandAll()
    {
        CodebookNodeExpansion.get().expandAll();
    }

    public void collapseAll()
    {
        CodebookNodeExpansion.get().collapseAll();
    }
}
