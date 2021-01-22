/*
 * Copyright 2021
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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.project.tree;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.CodebookTreeProvider;
import de.tudarmstadt.ukp.clarin.webanno.codebook.tree.model.CodebookNode;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;

public class OrderingCodebookFolder
    extends Folder<CodebookNode>
{
    private static final long serialVersionUID = -7043149283348752066L;

    private CodebookSchemaService codebookSchemaService;

    private void move(CodebookNode node, CodebookTreeProvider provider, boolean up)
    {
        List<CodebookNode> sibs = provider.getSiblings(node);
        int myIdx = sibs.indexOf(node);
        // first cant move up and last cant move down
        if ((up && myIdx == 0) || (!up && myIdx == sibs.size() - 1))
            return;

        // swap orderings (simple inc/dec wont do the job when changing tree structure, i.e.
        // change a parent)
        int tmp = node.getOrdering();
        CodebookNode swapSib = sibs.get(myIdx + (up ? -1 : 1)); // 0 is highest leN
        node.setOrdering(swapSib.getOrdering());
        swapSib.setOrdering(tmp);

        // change ordering in both Codebooks
        Codebook cb = node.getCodebook();
        cb.setOrdering(node.getOrdering());
        this.codebookSchemaService.createOrUpdateCodebook(cb);

        cb = swapSib.getCodebook();
        cb.setOrdering(swapSib.getOrdering());
        this.codebookSchemaService.createOrUpdateCodebook(cb);

        provider.sortNodes();
    }

    public OrderingCodebookFolder(String id, AbstractTree<CodebookNode> tree,
            IModel<CodebookNode> model, ProjectCodebookTreePanel parentPanel,
            CodebookSchemaService codebookSchemaService)
    {
        super(id, tree, model);

        this.codebookSchemaService = codebookSchemaService;

        add(new LambdaAjaxLink("up", (target) -> {
            if (tree.getProvider() instanceof CodebookTreeProvider) {
                CodebookTreeProvider provider = (CodebookTreeProvider) tree.getProvider();
                this.move(model.getObject(), provider, true);

                // we have to think of a better way to update a tree. this way we init the tree way
                // too often...
                parentPanel.initTree();
                target.add(parentPanel);
            }
        })
        {
            private static final long serialVersionUID = 5243294213092651657L;
        });

        add(new LambdaAjaxLink("down", (target) -> {
            if (tree.getProvider() instanceof CodebookTreeProvider) {
                CodebookTreeProvider provider = (CodebookTreeProvider) tree.getProvider();
                this.move(model.getObject(), provider, false);
                // we have to think of a better way to update a tree. this way we init the tree way
                // too often...
                parentPanel.initTree();
                target.add(parentPanel);
            }
        })
        {
            private static final long serialVersionUID = 5243294213092651657L;
        });
    }
}
