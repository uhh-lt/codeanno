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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.tree;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.kendo.ui.markup.html.link.Link;

import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.PredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.service.CodebookAutomationService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.CodebookTreeProvider;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.model.CodebookNode;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.model.CodebookNodeExpansion;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.ui.CodebookTreePanel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.CodebookCorrectionPage;

public class CodebookCorrectionTreePanel
    extends CodebookTreePanel
{
    private static final long serialVersionUID = -8329270688665288003L;

    private transient final CodebookCorrectionPage parentPage;
    private transient final Map<CodebookNode, CodebookCorrectionNodePanel> nodePanels;
    private transient Map<Codebook, PredictionResult> automationSuggestions;

    private @SpringBean CodebookAutomationService codebookAutomationService;
    private @SpringBean CodebookSchemaService codebookSchemaService;

    public CodebookCorrectionTreePanel(String aId, CodebookCorrectionPage parentPage)
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
        this.automationSuggestions = new HashMap<>();
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

        this.automationSuggestions = Collections.emptyMap(); // this.fetchSuggestions();

        tree = new NestedTree<CodebookNode>("codebookCorrectionTree", this.provider,
                new CodebookNodeExpansionModel())
        {
            private static final long serialVersionUID = 2285250157811357702L;

            @Override
            protected Component newContentComponent(String id, IModel<CodebookNode> model)
            {
                // we save the nodes and their panels to get 'easy' access to the panels since
                // we need them later
                CodebookCorrectionNodePanel nodePanel = new CodebookCorrectionNodePanel(id, model,
                        automationSuggestions.get(provider.getCodebook(model.getObject())),
                        CodebookCorrectionTreePanel.this);
                CodebookCorrectionTreePanel.this.nodePanels.put(model.getObject(), nodePanel);
                return nodePanel;
            }
        };

        this.applyTheme();

        tree.setOutputMarkupId(true);
        this.addOrReplace(tree);
    }

    public Map<CodebookNode, CodebookCorrectionNodePanel> getNodePanels()
    {
        return nodePanels;
    }

    public CodebookCorrectionPage getParentPage()
    {
        return parentPage;
    }

    public void expandNode(CodebookNode n)
    {
        tree.expand(n);
    }
}
