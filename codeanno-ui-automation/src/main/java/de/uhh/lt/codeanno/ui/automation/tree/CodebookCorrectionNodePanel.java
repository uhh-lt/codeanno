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
package de.uhh.lt.codeanno.ui.automation.tree;

import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.updateDocumentTimestampAfterWrite;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getAddr;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

import de.tudarmstadt.ukp.clarin.webanno.api.CorrectionDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.event.DocumentStateChangedEvent;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.DescriptionTooltipBehavior;
import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.automation.CodebookAutomationService;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiException;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionResult;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.tree.model.CodebookNode;
import de.uhh.lt.codeanno.tree.ui.CodebookNodePanel;
import de.uhh.lt.codeanno.ui.annotation.CodebookTagSelectionComboBox;

public class CodebookCorrectionNodePanel
    extends CodebookNodePanel
{

    private static final long serialVersionUID = 5875644822389693657L;
    private static final String AUTOMATION_AVAILABLE = "bg-primary";
    private static final String AUTOMATION_NOT_AVAILABLE = "bg-warning";
    private static final String AUTOMATION_ACCEPTED = "bg-success";
    private final CodebookTagSelectionComboBox codebookCorrectionComboBox;
    private final CodebookCorrectionTreePanel parentTreePanel;
    private final Form<CodebookTag> codebookCorrectionForm;
    private final CodebookNode node;
    private final PredictionResult automationSuggestions;
    private final WebMarkupContainer codebookCorrectionPanel;
    private final WebMarkupContainer codebookCorrectionPanelHeader;
    private final WebMarkupContainer codebookCorrectionPanelFooter;
    private @SpringBean CodebookSchemaService codebookService;
    private @SpringBean DocumentService documentService;
    private @SpringBean UserDao userRepository;
    private @SpringBean CodebookAutomationService automationService;
    private @SpringBean CorrectionDocumentService correctionDocumentService;

    public CodebookCorrectionNodePanel(String id, IModel<CodebookNode> node,
            PredictionResult automationSuggestions, CodebookCorrectionTreePanel parentTreePanel)
    {
        super(id, new CompoundPropertyModel<>(node));

        this.node = node.getObject();
        this.parentTreePanel = parentTreePanel;
        this.automationSuggestions = automationSuggestions;

        this.codebookCorrectionPanel = new WebMarkupContainer("codebookCorrectionPanel");
        this.codebookCorrectionPanel.setOutputMarkupPlaceholderTag(true);

        // header
        this.codebookCorrectionPanelHeader = new WebMarkupContainer(
                "codebookCorrectionPanelHeader");
        this.codebookCorrectionPanelHeader.setOutputMarkupPlaceholderTag(true);
        try {
            this.codebookCorrectionPanelHeader.add(AttributeModifier.append("class",
                    automationService.isAutomationAvailable(this.node.getCodebook(), false)
                            ? AUTOMATION_AVAILABLE
                            : AUTOMATION_NOT_AVAILABLE));
        }
        catch (ApiException exception) {
            // TODO what to throw?!
            exception.printStackTrace();
        }

        // name of the CB
        this.codebookCorrectionPanelHeader.add(new Label("codebookName", this.node.getUiName()));

        this.codebookCorrectionPanel.add(codebookCorrectionPanelHeader);

        // suggestions list view
        List<PredictionPojo> preds = buildPredictionList();
        this.codebookCorrectionPanel.add(new ListView<PredictionPojo>("suggestionsListView", preds)
        {
            private static final long serialVersionUID = -3459331980449938289L;

            @Override
            protected void populateItem(ListItem<PredictionPojo> item)
            {
                PredictionPojo pred = item.getModelObject();

                item.add(new Label("tag", pred.tag));
                item.add(new Label("probability", String.format("%.6f", pred.prob)));
            }
        });

        this.codebookCorrectionPanelFooter = new WebMarkupContainer(
                "codebookCorrectionPanelFooter");
        this.codebookCorrectionPanelFooter.setOutputMarkupPlaceholderTag(true);

        // codebook automation form
        IModel<CodebookTag> selectedTag = Model.of();
        this.codebookCorrectionForm = new Form<>("codebookCorrectionForm",
                CompoundPropertyModel.of(selectedTag));
        this.codebookCorrectionForm.setOutputMarkupId(true);

        // codebook automation ComboBox
        this.codebookCorrectionComboBox = createAutomationComboBox();
        this.codebookCorrectionForm.addOrReplace(this.codebookCorrectionComboBox);

        // tooltip for the codebooks
        Codebook codebook = this.node.getCodebook();
        this.codebookCorrectionPanel.add(
                new DescriptionTooltipBehavior(codebook.getUiName(), codebook.getDescription()));

        this.codebookCorrectionPanelFooter.add(this.codebookCorrectionForm);

        this.codebookCorrectionPanel.add(codebookCorrectionPanelFooter);
        this.add(codebookCorrectionPanel);
    }

    private List<PredictionPojo> buildPredictionList()
    {
        List<PredictionPojo> preds = new ArrayList<>();
        if (automationSuggestions == null)
            return preds;

        for (Map.Entry<String, Double> e : automationSuggestions.getProbabilities().entrySet()) {
            preds.add(new PredictionPojo(e.getKey(), e.getValue()));
        }
        return preds;
    }

    private CodebookTagSelectionComboBox createAutomationComboBox()
    {
        CAS correctionCas = null;
        try {
            // returns the correction CAS
            correctionCas = parentTreePanel.getParentPage().getEditorCas();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // get the predicted CodebookTag from the correction CAS
        Codebook codebook = this.node.getCodebook();
        CodebookFeature feature = codebookService.listCodebookFeature(codebook).get(0);
        CodebookCasAdapter adapter = new CodebookCasAdapter(feature.getCodebook());
        User user = userRepository.getCurrentUser();
        String existingValue = (String) adapter.getExistingCodeValue(correctionCas, feature);

        List<CodebookTag> tagChoices = this.getPossibleTagChoices();
        CodebookTagSelectionComboBox comboBox = new CodebookTagSelectionComboBox(this,
                "codebookTagSelectionComboBox", Model.of(existingValue), tagChoices, node);

        SourceDocument doc = this.parentTreePanel.getParentPage().getModelObject().getDocument();
        comboBox.setEnabled(
                doc != null && !doc.getState().equals(SourceDocumentState.CURATION_FINISHED));

        comboBox.add(new AjaxFormComponentUpdatingBehavior("change")
        {
            private static final long serialVersionUID = -6052685304352686750L;

            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
                // persist changes in correction cas
                try {
                    CAS cas = parentTreePanel.getParentPage().getEditorCas();
                    saveCodebookAnnotation(feature, comboBox.getModelObject(), cas);
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

    private void saveCodebookAnnotation(CodebookFeature feature, String value, CAS correctionCas)
        throws AnnotationException, IOException
    {
        CodebookCasAdapter adapter = new CodebookCasAdapter(feature.getCodebook());
        // combo box got cleared or NONE was selected remove the value from the cas
        if (value == null) {
            adapter.delete(correctionCas, feature);
            // persist changes
            writeCorrectionCas(correctionCas);
            return;
        }

        // write (add) the Codebook annotation to the editor CAS (which is the correction cas)
        writeCodebookToCas(adapter, feature, value, correctionCas);

        // persist changes
        writeCorrectionCas(correctionCas);
    }

    private void writeCodebookToCas(CodebookCasAdapter aAdapter, CodebookFeature feature,
            String value, CAS correctionCas)
        throws IOException, AnnotationException
    {
        AnnotationFS existingFs = aAdapter.getExistingFs(correctionCas);
        int annoId;

        if (existingFs != null) {
            annoId = getAddr(existingFs);
        }
        else {
            annoId = aAdapter.add(correctionCas);
        }
        aAdapter.setFeatureValue(correctionCas, feature, annoId, value);
    }

    private void writeCorrectionCas(CAS correctionCas) throws IOException, AnnotationException
    {
        AnnotatorState state = parentTreePanel.getParentPage().getModelObject();
        correctionDocumentService.writeCorrectionCas(correctionCas, state.getDocument());
        Optional<Long> diskTimestamp = correctionDocumentService
                .getCorrectionCasTimestamp(state.getDocument());
        updateDocumentTimestampAfterWrite(state, diskTimestamp);
    }

    private List<CodebookTag> getPossibleTagChoices()
    {
        // get the possible tag choices for the current node
        CodebookCorrectionNodePanel parentPanel = this.parentTreePanel.getNodePanels()
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
        String tagString = this.codebookCorrectionComboBox.getModelObject();
        if (tagString == null || tagString.isEmpty())
            return null;
        List<CodebookTag> tags = codebookService.listTags(this.node.getCodebook());
        Set<CodebookTag> tag = tags.stream().filter(t -> {
            return t.getName().equals(tagString);

        }).collect(Collectors.toSet());
        assert tag.size() == 1; // TODO what to throw?
        return tag.iterator().next();
    }

    @Override
    public CodebookNodePanel getParentNodePanel()
    {
        return null;
    }

    @EventListener
    public void onDocumentStateChangedEvent(DocumentStateChangedEvent changedEvent)
    {
        // TODO how to update the combo boxes without an AjaxRequestTarget?!
        if (changedEvent.getNewState().equals(SourceDocumentState.CURATION_FINISHED)) {
            this.codebookCorrectionComboBox.setEnabled(false);
        }
    }

    private static class PredictionPojo
        implements Serializable
    {
        private static final long serialVersionUID = -7298275007124902612L;

        public String tag;
        public Double prob;

        public PredictionPojo(String tag, double prob)
        {
            this.tag = tag;
            this.prob = prob;
        }
    }
}
