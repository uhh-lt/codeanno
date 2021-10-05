/*
 * Copyright 2021
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
 * and  Language Technology Universität Hamburg
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
package de.uhh.lt.codeanno.ui.annotation;

import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getAddr;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.wicket.kendo.ui.form.combobox.ComboBox;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;
import de.uhh.lt.codeanno.tree.model.CodebookNode;

public abstract class CodebookEditorPanel
    extends Panel
{
    /**
     * Function to return tooltip using jquery Docs for the JQuery tooltip widget that we configure
     * below: https://api.jqueryui.com/tooltip/
     */
    protected static final String FUNCTION_FOR_TOOLTIP = "function() { return "
            + "'<div class=\"tooltip-title\">'+($(this).text() "
            + "? $(this).text() : 'no title')+'</div>"
            + "<div class=\"tooltip-content tooltip-pre\">'+($(this).attr('title') "
            + "? $(this).attr('title') : 'no description' )+'</div>' }";

    private static final long serialVersionUID = -9151455840010092452L;
    private static final Logger LOG = LoggerFactory.getLogger(CodebookEditorPanel.class);

    private @SpringBean DocumentService documentService;
    private @SpringBean CodebookSchemaService codebookService;

    private CodebookEditorTreePanel codebookEditorTreePanel;
    private CodebookEditorAutomationPanel codebookEditorAutomationPanel;

    public CodebookEditorPanel(String id, IModel<CodebookEditorModel> aModel)
    {
        super(id, aModel);

        setOutputMarkupId(true);

        this.setDefaultModel(aModel);
        this.createOrUpdateCodebookTree(aModel);

        codebookEditorAutomationPanel = new CodebookEditorAutomationPanel(
                "codebookEditorAutomationPanel", this);
        addOrReplace(codebookEditorAutomationPanel);
    }

    public void createOrUpdateCodebookTree(IModel<CodebookEditorModel> aModel)
    {
        // add but don't init the tree
        codebookEditorTreePanel = new CodebookEditorTreePanel("codebookEditorTreePanel", aModel,
                this);
        if (aModel.getObject() != null)
            codebookEditorTreePanel.initTree();
        codebookEditorTreePanel.setOutputMarkupId(true);
        addOrReplace(codebookEditorTreePanel);
    }

    public CodebookEditorModel getModelObject()
    {
        return (CodebookEditorModel) getDefaultModelObject();
    }

    public String getExistingCodeFromUserCas(Codebook codebook)
    {
        CodebookCasAdapter adapter = new CodebookCasAdapter(codebook);
        CodebookFeature feature = codebookService.listCodebookFeature(codebook).get(0);
        CAS cas = null;
        try {
            cas = getUserCas();
        }
        catch (IOException e) {
            return null;
        }

        return (String) adapter.getExistingCodeValue(cas, feature);
    }

    public AjaxFormComponentUpdatingBehavior createOnChangeSaveUpdatingBehavior(
            ComboBox<?> comboBox, Codebook codebook)
    {
        return new AjaxFormComponentUpdatingBehavior("change")
        {
            private static final long serialVersionUID = 5179816588460867471L;

            @Override
            public void onUpdate(AjaxRequestTarget aTarget)
            {
                persistCodebookAnnotationInUserCas(comboBox.getModelObject(), codebook);

                // update the tag selection combo boxes of the child nodes so that they offer
                // only
                // the valid tags based on the currently selected tag..

                // fist clear the child combo boxes selections // TODO only for invalid?!
                CodebookNode currentNode = CodebookEditorPanel.this.codebookEditorTreePanel
                        .getProvider().getCodebookNode(codebook);
                Set<CodebookEditorNodePanel> childNodePanels = getChildNodePanels(currentNode);

                // secondly update the possible tag selection combo boxes of all the child nodes
                childNodePanels.forEach(CodebookEditorNodePanel::updateTagSelectionCombobox);

                // TODO this doesn't work and I have no clue how to get this working...
                childNodePanels.forEach(CodebookEditorNodePanel::clearSelection);
                // add the node panels to the ajax target for re-rendering
                childNodePanels.forEach(aTarget::add);
                aTarget.add(CodebookEditorPanel.this);
            }
        };
    }

    public void persistCodebookAnnotationInUserCas(String codebookTagValue, Codebook cb)
    {
        try { // to persist the changes made to the codebook
            CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
            CAS userCas = getUserCas();
            CodebookCasAdapter adapter = new CodebookCasAdapter(cb);

            if (codebookTagValue == null) {
                // combo box got cleared or NONE was selected
                adapter.delete(userCas, feature);
            }
            else {
                // store the value in the CAS
                AnnotationFS existingFs = adapter.getExistingFs(userCas);
                int annoId = existingFs != null ? getAddr(existingFs) : adapter.add(userCas);
                adapter.setFeatureValue(userCas, feature, annoId, codebookTagValue);

            }
            // persist changes
            writeUserCas();
        }
        catch (IOException | AnnotationException e) {
            error("Unable to update" + e.getMessage());
            LOG.error("Unable to update" + e.getMessage());
        }
    }

    public void setModel(AjaxRequestTarget aTarget, CodebookEditorModel aState)
    {
        setDefaultModelObject(aState);
        setDefaultModel(Model.of(aState));

        // initialize the tree with the project's codebooks
        codebookEditorTreePanel.setDefaultModelObject(aState);
        codebookEditorTreePanel.initTree();
        if (aTarget != null) {
            aTarget.add(codebookEditorTreePanel);
            aTarget.add(this);
        }
    }

    /* package private */ CAS getUserCas() throws IOException
    {
        CodebookEditorModel state = getModelObject();

        if (state.getDocument() == null) {
            throw new IllegalStateException("Please open a document first!");
        }
        return (onGetJCas());
    }

    /* package private */ void writeUserCas() throws IOException
    {
        CAS userCas = getUserCas();

        CodebookEditorModel state = getModelObject();
        documentService.writeAnnotationCas(userCas, state.getDocument(), state.getUser(), true);

        // Update timestamp in state
        Optional<Long> diskTimestamp = documentService
                .getAnnotationCasTimestamp(state.getDocument(), state.getUser().getUsername());
        diskTimestamp.ifPresent(this::onJCasUpdate);
    }

    /* package private */ Map<CodebookNode, CodebookEditorNodePanel> getNodePanels()
    {
        return this.codebookEditorTreePanel.getNodePanels();
    }

    public CodebookEditorNodePanel getParentNodePanel(CodebookNode node)
    {
        return this.codebookEditorTreePanel.getNodePanels().get(node.getParent());
    }

    private Set<CodebookEditorNodePanel> getChildNodePanels(CodebookNode node)
    {
        Set<CodebookEditorNodePanel> childNodePanels = new HashSet<>();
        Map<CodebookNode, CodebookEditorNodePanel> nodePanels = this.codebookEditorTreePanel
                .getNodePanels();
        List<CodebookNode> allChildren = this.codebookEditorTreePanel.getProvider()
                .getDescendants(node);
        allChildren.forEach(child -> {
            if (null != child && null != nodePanels.get(child)) {
                childNodePanels.add(nodePanels.get(child));
            }
        });
        return childNodePanels;
    }

    // Overridden in AnnotationPage
    protected abstract void onJCasUpdate(Long aTimeStamp);

    // Overridden in AnnotationPage
    protected abstract CAS onGetJCas() throws IOException;
}
