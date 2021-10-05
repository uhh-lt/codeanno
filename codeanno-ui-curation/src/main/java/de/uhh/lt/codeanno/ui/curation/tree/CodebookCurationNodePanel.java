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

import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getAddr;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.context.event.EventListener;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.event.DocumentStateChangedEvent;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.support.DescriptionTooltipBehavior;
import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.ui.CodebookNodePanel;
import de.uhh.lt.codeanno.ui.annotation.CodebookTagSelectionComboBox;
import de.uhh.lt.codeanno.ui.curation.CodebookUserSuggestion;

public class CodebookCurationNodePanel
    extends CodebookNodePanel
{
    private static final long serialVersionUID = 5875644822389693657L;

    private static final String HAS_DIFF = "bg-danger";
    private static final String HAS_NO_DIFF = "bg-success";
    private final CodebookTagSelectionComboBox codebookTagSelectionComboBox;
    private final CodebookCurationTreePanel parentTreePanel;
    private final Form<CodebookTag> codebookCurationForm;
    private final List<CodebookUserSuggestion> codebookUserSuggestions;
    private final WebMarkupContainer codebookCurationPanelHeader;
    private final WebMarkupContainer codebookCurationPanelFooter;
    private final WebMarkupContainer codebookCurationPanel;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CurationDocumentService curationDocumentService;

    public CodebookCurationNodePanel(String id, IModel<CodebookNode> node,
            CodebookCurationTreePanel parentTreePanel,
            List<CodebookUserSuggestion> codebookUserSuggestions)
    {
        super(id, new CompoundPropertyModel<>(node));

        this.node = node.getObject();
        this.parentTreePanel = parentTreePanel;
        this.codebookUserSuggestions = codebookUserSuggestions;

        this.codebookCurationPanel = new WebMarkupContainer("codebookCurationPanel");
        this.codebookCurationPanel.setOutputMarkupPlaceholderTag(true);
        boolean hasDiff = codebookUserSuggestions.get(0).hasDiff();

        // header
        this.codebookCurationPanelHeader = new WebMarkupContainer("codebookCurationPanelHeader");
        this.codebookCurationPanelHeader.setOutputMarkupPlaceholderTag(true);
        this.codebookCurationPanelHeader
                .add(AttributeModifier.append("class", hasDiff ? HAS_DIFF : HAS_NO_DIFF));
        // name of the CB
        this.codebookCurationPanelHeader.add(new Label("codebookName", this.node.getUiName()));

        this.codebookCurationPanel.add(codebookCurationPanelHeader);

        // suggestions list view
        this.codebookCurationPanel.add(
                new ListView<CodebookUserSuggestion>("suggestionsListView", codebookUserSuggestions)
                {
                    private static final long serialVersionUID = -3459331980449938289L;

                    @Override
                    protected void populateItem(ListItem item)
                    {
                        CodebookUserSuggestion userSuggestion = (CodebookUserSuggestion) item
                                .getModelObject();

                        item.add(new Label("userName", userSuggestion.getUser()));
                        String tag = userSuggestion.getValue();
                        if (tag == null)
                            tag = "<NULL>";
                        else if (tag.isEmpty())
                            tag = "<EMPTY>";
                        item.add(new Label("codebookTag", tag));

                        // item.add(AttributeModifier.append("class",
                        // userSuggestion.hasDiff() ? HAS_DIFF : HAS_NO_DIFF));
                    }
                });

        this.codebookCurationPanelFooter = new WebMarkupContainer("codebookCurationPanelFooter");
        this.codebookCurationPanelFooter.setOutputMarkupPlaceholderTag(true);

        // codebook curation form
        IModel<CodebookTag> selectedTag = Model.of();
        this.codebookCurationForm = new Form<>("codebookCurationForm",
                CompoundPropertyModel.of(selectedTag));
        this.codebookCurationForm.setOutputMarkupId(true);

        // codebook curation ComboBox
        this.codebookTagSelectionComboBox = createCurationComboBox();
        this.codebookCurationForm.addOrReplace(this.codebookTagSelectionComboBox);

        // tooltip for the codebooks
        Codebook codebook = this.node.getCodebook();
        this.codebookCurationPanel.add(
                new DescriptionTooltipBehavior(codebook.getUiName(), codebook.getDescription()));

        this.codebookCurationPanelFooter.add(this.codebookCurationForm);

        this.codebookCurationPanel.add(codebookCurationPanelFooter);
        this.add(codebookCurationPanel);
    }

    private CodebookTagSelectionComboBox createCurationComboBox()
    {
        CAS curationCas = null;
        try {
            curationCas = curationDocumentService
                    .readCurationCas(codebookUserSuggestions.get(0).getDocument());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Codebook codebook = this.node.getCodebook();
        CodebookFeature feature = codebookService.listCodebookFeature(codebook).get(0);
        CodebookCasAdapter adapter = new CodebookCasAdapter(feature.getCodebook());

        String existingValue = (String) adapter.getExistingCodeValue(curationCas, feature);
        List<CodebookTag> tagChoices = this.getPossibleTagChoices();

        CodebookTagSelectionComboBox comboBox = new CodebookTagSelectionComboBox(this,
                "codebookTagSelectionComboBox", Model.of(existingValue), tagChoices, node);

        // only enable if curation in progress
        comboBox.setEnabled(codebookUserSuggestions.get(0).getDocument().getState()
                .equals(SourceDocumentState.CURATION_IN_PROGRESS));

        comboBox.add(new AjaxFormComponentUpdatingBehavior("change")
        {
            private static final long serialVersionUID = -6052685304352686750L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                // persist changes in curation cas
                try {
                    CAS curationCas = curationDocumentService
                            .readCurationCas(codebookUserSuggestions.get(0).getDocument());

                    if (comboBox.getModelObject() == null) {
                        // combo box got cleared or NONE was selected
                        CodebookCasAdapter adapter = new CodebookCasAdapter(codebook);
                        adapter.delete(curationCas, feature);
                        writeCodebookCas(curationCas);
                    }
                    else {
                        saveCodebookAnnotation(feature, comboBox.getModelObject(), curationCas);
                    }
                }
                catch (IOException | AnnotationException e) {
                    error("Unable to update" + e.getMessage());
                }
                parentTreePanel.expandNode(node);
            }
        });

        comboBox.setOutputMarkupId(true);
        return comboBox;
    }

    private void saveCodebookAnnotation(CodebookFeature feature, String value, CAS curationCas)
        throws AnnotationException, IOException
    {

        CodebookCasAdapter adapter = new CodebookCasAdapter(feature.getCodebook());
        if (value == null) {
            adapter.delete(curationCas, feature);
            writeCodebookCas(curationCas);
            return;
        }

        writeCodebookToCas(adapter, feature, value, curationCas);

        // persist changes
        writeCodebookCas(curationCas);

    }

    private void writeCodebookToCas(CodebookCasAdapter aAdapter, CodebookFeature feature,
            String tagValue, CAS aJCas)
        throws IOException, AnnotationException
    {
        AnnotationFS existingFs = aAdapter.getExistingFs(aJCas);
        int annoId = existingFs != null ? getAddr(existingFs) :  aAdapter.add(aJCas);
        aAdapter.setFeatureValue(aJCas, feature, annoId, tagValue);
    }

    private void writeCodebookCas(CAS aCas) throws IOException
    {
        SourceDocument document = this.codebookUserSuggestions.get(0).getDocument();
        curationDocumentService.writeCurationCas(aCas, document, true);

        // Update timestamp in state
        Optional<Long> diskTimestamp = curationDocumentService.getCurationCasTimestamp(document);
        diskTimestamp.ifPresent(aLong -> parentTreePanel.getParentPage().getModelObject()
                .setAnnotationDocumentTimestamp(aLong));
    }

    private List<CodebookTag> getPossibleTagChoices()
    {
        // get the possible tag choices for the current node
        CodebookCurationNodePanel parentPanel = this.parentTreePanel.getNodePanels()
                .get(this.node.getParent());
        if (parentPanel == null)
            return codebookService.listTags(this.node.getCodebook());
        // TODO also check parents of parent
        CodebookTag parentTag = parentPanel.getCurrentlySelectedTag();
        if (parentTag == null) // TODO why is this null for leafs ?!?!?!?
            return codebookService.listTags(this.node.getCodebook());

        // only tags that have parentTag as parent
        List<CodebookTag> validTags = codebookService.listTags(this.node.getCodebook()).stream()
                .filter(codebookTag -> {
                    if (codebookTag.getParent() == null)
                        return false;
                    return codebookTag.getParent().equals(parentTag);
                }).collect(Collectors.toList());
        return validTags;
    }

    public CodebookTag getCurrentlySelectedTag()
    {
        String tagString = this.codebookTagSelectionComboBox.getModelObject();
        if (tagString == null || tagString.isEmpty())
            return null;
        List<CodebookTag> tags = codebookService.listTags(this.node.getCodebook());
        Set<CodebookTag> tag = tags.stream().filter(t -> t.getName().equals(tagString))
                .collect(Collectors.toSet());
        assert tag.size() == 1; // TODO what to throw?
        return tag.iterator().next();
    }

    @Override
    public CodebookNodePanel getParentNodePanel()
    {
        // TODO
        return null;
    }

    @EventListener
    public void onDocumentStateChangedEvent(DocumentStateChangedEvent changedEvent)
    {
        // TODO how to update the combo boxes without an AjaxRequestTarget?!
        if (changedEvent.getNewState().equals(SourceDocumentState.CURATION_FINISHED)) {
            this.codebookTagSelectionComboBox.setEnabled(false);
        }
    }
}
