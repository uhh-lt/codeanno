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
package de.uhh.lt.codeanno.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.extensions.markup.html.repeater.tree.ITreeProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.tree.model.CodebookNode;

public class CodebookTreeProvider
    implements Serializable, ITreeProvider<CodebookNode>
{

    private static final long serialVersionUID = 7312208573006457875L;

    private final Map<String, CodebookNode> nameToNodes;
    private final Map<String, Codebook> nameToCodebooks;
    private final List<CodebookNode> roots;

    public CodebookTreeProvider(List<Codebook> allCodebooks)
    {

        this.nameToCodebooks = allCodebooks.stream()
                .collect(Collectors.toMap(Codebook::getName, o -> o));

        this.nameToNodes = allCodebooks.stream().map(CodebookNode::new)
                .collect(Collectors.toMap(CodebookNode::getName, o -> o));

        buildTreeStructures();

        this.roots = this.nameToNodes.values().stream().filter(CodebookNode::isRoot)
                .collect(Collectors.toList());

        this.sortNodes();
    }

    public void setParent(CodebookNode node)
    {
        Codebook book = this.nameToCodebooks.get(node.getName());
        if (book.getParent() == null)
            return;
        node.setParent(this.nameToNodes.get(book.getParent().getName()));
    }

    private void buildTreeStructures()
    {
        this.nameToNodes.values().forEach(this::setParent);
        this.nameToNodes.values().forEach(this::addChildrenRecursively);
    }

    public void addChildrenRecursively(CodebookNode node)
    {
        if (node != null && !isRoot(node) && node.getParent() != null) {
            CodebookNode parent = node.getParent();
            parent.addChild(node);
            this.addChildrenRecursively(parent);
        }
    }

    public boolean isRoot(CodebookNode node)
    {
        return this.nameToCodebooks.get(node.getName()).getParent() == null;
    }

    @Override
    public Iterator<CodebookNode> getRoots()
    {
        return this.roots.iterator();
    }

    public List<CodebookNode> getRootNodes()
    {
        return this.roots;
    }

    @Override
    public boolean hasChildren(CodebookNode node)
    {
        return !node.isLeaf();
    }

    @Override
    public Iterator<CodebookNode> getChildren(final CodebookNode node)
    {
        return node.getChildren().iterator();
    }

    /*
     * Mapping functions between CodebookNodes and Codebooks and vice versa
     */
    public Codebook getCodebook(final CodebookNode node)
    {
        return nameToCodebooks.get(node.getName());
    }

    public CodebookNode getCodebookNode(final Codebook book)
    {
        return nameToNodes.get(book.getName());
    }

    public List<Codebook> getCodebooks(final List<CodebookNode> nodes)
    {
        List<Codebook> books = new ArrayList<>();
        for (CodebookNode node : nodes)
            books.add(this.getCodebook(node));
        return books;
    }

    public List<CodebookNode> getCodebookNodes(final List<Codebook> books)
    {
        List<CodebookNode> nodes = new ArrayList<>();
        for (Codebook book : books)
            nodes.add(this.getCodebookNode(book));
        return nodes;
    }

    public List<Codebook> getChildren(final Codebook book)
    {
        if (this.getCodebookNode(book) == null) // This should never be true!
            return Collections.emptyList();
        return this.getCodebooks(this.getCodebookNode(book).getChildren());
    }

    public List<CodebookNode> getPrecedents(final CodebookNode node)
    {
        List<CodebookNode> parents = new ArrayList<>();
        CodebookNode parent = node.getParent();
        while (parent != null) {
            parents.add(parent);
            parent = parent.getParent();
        }
        return parents;
    }

    public List<CodebookNode> getDescendants(final CodebookNode node)
    {
        return getDescendants(node, null);
    }

    private List<CodebookNode> getDescendants(final CodebookNode node,
            List<CodebookNode> allChildren)
    {
        if (allChildren == null)
            allChildren = new ArrayList<>();

        for (CodebookNode child : node.getChildren()) {
            allChildren.add(child);
            getDescendants(child, allChildren);
        }

        return allChildren;
    }

    public List<CodebookNode> getSiblings(final CodebookNode node)
    {
        if (node.getParent() == null)
            return roots;
        // node is sibling of it self
        return node.getParent().getChildren();
    }

    public List<Codebook> getPossibleParents(final Codebook book)
    {
        if (book == null || book.getId() == null)
            return new ArrayList<>(this.nameToCodebooks.values());

        // all but own children
        List<Codebook> possibleParents = new ArrayList<>(this.nameToCodebooks.values());
        possibleParents.removeAll(this.getChildren(book));
        return possibleParents;
    }

    public void sortNodes()
    {
        Collections.sort(this.roots);
        roots.forEach(codebookNode -> Collections.sort(codebookNode.getChildren()));
    }

    // TreeProvider stuff
    @Override
    public void detach()
    {

    }

    @Override
    public IModel<CodebookNode> model(CodebookNode node)
    {
        return Model.of(node);
    }

}
