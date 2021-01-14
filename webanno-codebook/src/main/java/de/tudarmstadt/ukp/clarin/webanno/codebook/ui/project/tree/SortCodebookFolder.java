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

import org.apache.wicket.extensions.markup.html.repeater.tree.AbstractTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.model.IModel;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookNode;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTreeProvider;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;

public class SortCodebookFolder
    extends Folder<CodebookNode>
{
    private static final long serialVersionUID = -7043149283348752066L;

    public SortCodebookFolder(String id, AbstractTree<CodebookNode> tree,
            IModel<CodebookNode> model)
    {
        super(id, tree, model);
        add(new LambdaAjaxLink("up", (target) -> {
            if (tree.getProvider() instanceof CodebookTreeProvider)
                ((CodebookTreeProvider) tree.getProvider()).move(model.getObject(), true);
            // FIXME the tree doesn't refresh for notes starting with level 3...
            // not even by adding tree.getParent() or tree.getPage()!
            target.add(tree);
        })
        {
            private static final long serialVersionUID = 5243294213092651657L;
        });

        add(new LambdaAjaxLink("down", (target) -> {
            if (tree.getProvider() instanceof CodebookTreeProvider)
                ((CodebookTreeProvider) tree.getProvider()).move(model.getObject(), false);
            // FIXME the tree doesn't refresh for notes starting with level 3...
            // not even by adding tree.getParent() or tree.getPage()!
            target.add(tree);
        })
        {
            private static final long serialVersionUID = 5243294213092651657L;
        });
    }
}
