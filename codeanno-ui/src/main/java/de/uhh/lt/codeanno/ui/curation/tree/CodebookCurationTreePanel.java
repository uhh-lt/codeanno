/*
 * Copyright 2021
 * Language Technology (LT) Universität Hamburg
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
package de.uhh.lt.codeanno.ui.curation.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.kendo.ui.markup.html.link.Link;

import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.tree.CodebookTreeProvider;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.model.CodebookNodeExpansion;
import de.uhh.lt.codeanno.tree.ui.CodebookTreePanel;
import de.uhh.lt.codeanno.ui.curation.CodebookCurationPage;
import de.uhh.lt.codeanno.ui.curation.CodebookUserSuggestion;

public class CodebookCurationTreePanel
    extends CodebookTreePanel
{
    private static final long serialVersionUID = -8329270688665288003L;

    private final CodebookCurationPage parentPage;
    private final Map<CodebookNode, CodebookCurationNodePanel> nodePanels;
    private transient Map<Codebook, List<CodebookUserSuggestion>> userSuggestions;

    private @SpringBean CodebookSchemaService codebookSchemaService;

    public CodebookCurationTreePanel(String aId, CodebookCurationPage parentPage)
    {
        super(aId, new Model<>(null));

        // create and add expand and collapse links
        this.add(new Link<Void>("expandAll")
        {
            private static final long serialVersionUID = -2081711094768955973L;

            public void onClick()
            {
                CodebookNodeExpansion.get().expandAll();
            }
        });
        this.add(new Link<Void>("collapseAll")
        {
            private static final long serialVersionUID = -4576757597732733009L;

            public void onClick()
            {
                CodebookNodeExpansion.get().collapseAll();
            }
        });

        this.nodePanels = new HashMap<>();
        this.parentPage = parentPage;
    }

    @Override
    public void initCodebookTreeProvider()
    {
        // get all codebooks and init the provider
        List<Codebook> codebooks = this.codebookSchemaService
                .listCodebook(parentPage.getModelObject().getProject());
        this.provider = new CodebookTreeProvider(codebooks);
    }

    @Override
    public void initTree()
    {
        this.initCodebookTreeProvider();
        this.userSuggestions = this.parentPage.getUserSuggestionsOfCurrentDocument();

        tree = new NestedTree<CodebookNode>("codebookCurationTree", this.provider,
                new CodebookNodeExpansionModel())
        {
            private static final long serialVersionUID = 2285250157811357702L;

            @Override
            protected Component newContentComponent(String id, IModel<CodebookNode> model)
            {
                // we save the nodes and their panels to get 'easy' access to the panels since
                // we need them later
                CodebookCurationNodePanel nodePanel = new CodebookCurationNodePanel(id, model,
                        CodebookCurationTreePanel.this,
                        userSuggestions.get(provider.getCodebook(model.getObject())));
                CodebookCurationTreePanel.this.nodePanels.put(model.getObject(), nodePanel);
                return nodePanel;
            }
        };

        this.applyTheme();

        tree.setOutputMarkupId(true);
        this.addOrReplace(tree);
    }

    public Map<CodebookNode, CodebookCurationNodePanel> getNodePanels()
    {
        return nodePanels;
    }

    public CodebookCurationPage getParentPage()
    {
        return parentPage;
    }

    public void expandNode(CodebookNode n)
    {
        tree.expand(n);
    }
}
