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
package de.uhh.lt.codeanno.ui.annotation;

import static de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaBehavior.visibleWhen;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.support.DescriptionTooltipBehavior;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.automation.CodebookAutomationService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.ui.CodebookNodePanel;

public class CodebookEditorNodePanel
    extends CodebookNodePanel
{
    private static final long serialVersionUID = 5875644822389693657L;
    protected @SpringBean DocumentService documentService;
    private final CodebookTagSelectionComboBox tagSelectionComboBox;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean CodebookAutomationService automationService;
    private final CodebookEditorPanel parentEditor;
    private final Form<CodebookTag> tagSelectionForm;
    private String automatedTag;
    private String userTag;

    public CodebookEditorNodePanel(String id, IModel<CodebookNode> node,
            CodebookEditorPanel parentEditor)
    {
        super(id, new CompoundPropertyModel<>(node));

        this.node = node.getObject();
        this.parentEditor = parentEditor;

        // get the automated tag if there is one
        this.setAutomatedTag();
        // set the user tag if there is one
        this.setUserTag();

        // form
        IModel<CodebookTag> selectedTag = Model.of();
        this.tagSelectionForm = new Form<>("codebookTagSelectionForm",
                CompoundPropertyModel.of(selectedTag));
        this.tagSelectionForm.setOutputMarkupId(true);

        // combobox
        this.tagSelectionComboBox = createTagSelectionComboBox();
        this.tagSelectionForm.addOrReplace(this.tagSelectionComboBox);

        // label for the combobox
        this.tagSelectionForm.add(new Label("codebookNameLabel", this.node.getUiName()));

        // tooltip for the codebooks
        Codebook codebook = this.node.getCodebook();
        this.add(new DescriptionTooltipBehavior(codebook.getUiName(), codebook.getDescription()));

        // automatedCodebookButtonPanel
        WebMarkupContainer automatedCodebookButtonPanel = new WebMarkupContainer(
                "automatedCodebookButtonPanel");
        automatedCodebookButtonPanel.setOutputMarkupId(true);
        automatedCodebookButtonPanel
                .add(visibleWhen(() -> this.automatedTag != null && this.userTag == null));
        // automatedCodebookAcceptButton
        automatedCodebookButtonPanel.add(new LambdaAjaxLink("automatedCodebookAcceptButton",
                this::actionAcceptAutomatedCodebook));
        this.tagSelectionForm.add(automatedCodebookButtonPanel);
        // automatedCodebookButtonPanel
        WebMarkupContainer automatedCodebookHintPanel = new WebMarkupContainer(
                "automatedCodebookHintPanel");
        automatedCodebookHintPanel.setOutputMarkupId(true);
        automatedCodebookHintPanel
                .add(visibleWhen(() -> this.automatedTag != null && this.userTag == null));
        this.tagSelectionForm.add(automatedCodebookHintPanel);

        this.add(this.tagSelectionForm);
    }

    private void setAutomatedTag()
    {
        try {
            // check if there is a Codebook Annotation in the correction CAS (which is the CAS in
            // which predicted i.e. automated Codebook Annotations from CBA are stored)
            Codebook cb = this.node.getCodebook();
            SourceDocument sdoc = this.parentEditor.getModelObject().getDocument();
            this.automatedTag = automationService.readPredictedTagValueFromCorrectionCas(cb, sdoc);
        }
        catch (IOException | AnnotationException e) {
            this.automatedTag = null;
        }
    }

    private void setUserTag()
    {
        this.userTag = this.parentEditor.getExistingCode(this.node.getCodebook());
    }

    private void actionAcceptAutomatedCodebook(AjaxRequestTarget ajaxRequestTarget)
    {
        Codebook cb = this.node.getCodebook();
        this.parentEditor.persistCodebookAnnotationInUserCas(this.automatedTag, cb);
        this.userTag = this.automatedTag;
        // write the automated value in the USER CAS!
        // set the automatedTag to null
        ajaxRequestTarget.add(this.parentEditor);
    }

    private CodebookTagSelectionComboBox createTagSelectionComboBox()
    {
        List<CodebookTag> tagChoices = this.getPossibleTagChoices();
        String existingCode = null;
        if (this.userTag != null)
            existingCode = this.userTag;
        else if (this.automatedTag != null)
            existingCode = this.automatedTag;

        CodebookTagSelectionComboBox tagSelection = new CodebookTagSelectionComboBox(this,
                "codebookTagBox", new Model<>(existingCode), tagChoices, node);

        SourceDocument doc = parentEditor.getModelObject().getDocument();

        tagSelection.setEnabled(doc != null && !documentService.isAnnotationFinished(
                parentEditor.getModelObject().getDocument(),
                parentEditor.getModelObject().getUser()));

        Codebook codebook = this.node.getCodebook();
        AjaxFormComponentUpdatingBehavior updatingBehavior = parentEditor
                .createOnChangeSaveUpdatingBehavior(tagSelection, codebook);
        tagSelection.add(updatingBehavior);
        tagSelection.setOutputMarkupId(true);
        return tagSelection;
    }

    private List<CodebookTag> getPossibleTagChoices()
    {

        // get the possible tag choices for the current node
        CodebookEditorNodePanel parentPanel = this.parentEditor.getNodePanels()
                .get(this.node.getParent());

        if (parentPanel == null)
            return codebookService.listTags(this.node.getCodebook());

        // TODO also check parents of parent
        CodebookTag parentTag = parentPanel.getCurrentlySelectedTag();
        if (parentTag == null) // TODO why is this null for street ?!?!?!?
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
        String tagString = this.tagSelectionComboBox.getModelObject();
        if (tagString == null || tagString.isEmpty())
            return null;
        List<CodebookTag> tags = codebookService.listTags(this.node.getCodebook());
        Set<CodebookTag> tag = tags.stream().filter(t -> t.getName().equals(tagString))
                .collect(Collectors.toSet());
        assert tag.size() == 1; // TODO what to throw?
        // FIXME this happens only if the tagString is not persisted in the DB, i.e., there was an
        // error while persisting. should be checked before!
        return tag.iterator().next();
    }

    // package private by intention
    void clearSelection()
    {
        this.tagSelectionComboBox.setModelObject(null);
    }

    // package private by intention
    void updateTagSelectionCombobox()
    {
        this.tagSelectionForm.addOrReplace(createTagSelectionComboBox());
    }

    public CodebookSchemaService getCodebookService()
    {
        return codebookService;
    }

    public CodebookEditorPanel getParentEditor()
    {
        return parentEditor;
    }

    @Override
    public CodebookNodePanel getParentNodePanel()
    {
        return parentEditor.getParentNodePanel(node);
    }
}
