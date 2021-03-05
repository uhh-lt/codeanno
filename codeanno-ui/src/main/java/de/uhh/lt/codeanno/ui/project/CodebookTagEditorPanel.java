/*
 * Copyright 2019
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
package de.uhh.lt.codeanno.ui.project;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxButton;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookTag;

public class CodebookTagEditorPanel
    extends Panel
{
    private static final long serialVersionUID = -3356173821217898824L;

    private @SpringBean CodebookSchemaService codebookSchemaService;

    private final IModel<Codebook> selectedCodebook;
    private final IModel<CodebookTag> selectedTag;

    private final ParentSelectionWrapper<CodebookTag> codebookTagParentSelection;
    private final CodebookTagSelectionPanel tagSelectionPanel;

    private LambdaAjaxLink upBtn;
    private LambdaAjaxLink downBtn;


    public CodebookTagEditorPanel(String aId,
                                  IModel<Codebook> aCodebook,
                                  IModel<CodebookTag> aTag,
                                  CodebookTagSelectionPanel aTagSelectionPanel)
    {
        super(aId, aTag);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        selectedCodebook = aCodebook;
        selectedTag = aTag;
        tagSelectionPanel = aTagSelectionPanel;

        Form<CodebookTag> form = new Form<>("form", CompoundPropertyModel.of(aTag));

        // TODO maybe we have to change TagExistsValidator
        // since there could be tag that only differ by parent?!
        form.add(new TextField<String>("name").add(new TagExistsValidator()).setRequired(true));
        form.add(new TextArea<String>("description"));

        // add parent tag selection
        // TODO
        // 1) get parent codebook!
        // 2) get tags from parent codebook as possible parent tags
        // 3) display them in the selection
        // 4) safe the chosen tag as parent tag!#
        List<CodebookTag> parentTags = new ArrayList<>();
        if (selectedCodebook.getObject() != null) {
            // TODO is this correct now?
            parentTags = codebookSchemaService.listTags(selectedCodebook.getObject().getParent());
        }
        this.codebookTagParentSelection = new ParentSelectionWrapper<>("parent", "name",
                parentTags);
        form.add(this.codebookTagParentSelection.getDropdown().setOutputMarkupPlaceholderTag(true));
        form.add(new LambdaAjaxButton<>("save", this::actionSave));
        form.add(new LambdaAjaxLink("delete", this::actionDelete)
                .onConfigure(_this -> _this.setVisible(form.getModelObject().getId() != null)));
        form.add(new LambdaAjaxLink("cancel", this::actionCancel));

        // buttons to sort
        upBtn = new LambdaAjaxLink("up", (target) -> {
            moveTag(true);
            this.setVisible(tagIsMovable(true));
            target.add(tagSelectionPanel, this, CodebookTagEditorPanel.this);
        });
        upBtn.setOutputMarkupId(true);
        upBtn.setVisible(tagIsMovable(true));
        form.add(upBtn);

        downBtn = new LambdaAjaxLink("down", (target) -> {
            moveTag(false);
            this.setVisible(tagIsMovable(false));
            target.add(tagSelectionPanel, this, CodebookTagEditorPanel.this);
        });
        downBtn.setOutputMarkupId(true);
        downBtn.setVisible(tagIsMovable(false));
        form.add(downBtn);

        add(form);
    }

    @Override
    protected void onConfigure()
    {
        super.onConfigure();
        this.setVisible(selectedTag != null && selectedTag.getObject() != null);
        List<CodebookTag> parentTags = new ArrayList<>();
        if (selectedCodebook.getObject() != null) {
            parentTags = codebookSchemaService.listTags(selectedCodebook.getObject().getParent());
        }
        this.codebookTagParentSelection.updateParents(parentTags);

        upBtn.setVisible(tagIsMovable(true));
        downBtn.setVisible(tagIsMovable(false));
    }

    private void actionSave(AjaxRequestTarget aTarget, Form<CodebookTag> aForm)
    {
        selectedTag.getObject().setCodebook(selectedCodebook.getObject());

        if (selectedTag.getObject().getTagOrdering() < 1) {
            int lastIndex = codebookSchemaService.listTags(selectedCodebook.getObject()).size();
            selectedTag.getObject().setTagOrdering(lastIndex + 1);
        }
        codebookSchemaService.createOrUpdateCodebookTag(selectedTag.getObject());

        // Reload whole page because master panel also needs to be reloaded.
        aTarget.add(getPage());
    }

    private void actionDelete(AjaxRequestTarget aTarget)
    {
        codebookSchemaService.removeCodebookTag(selectedTag.getObject());
        actionCancel(aTarget);
    }

    private void actionCancel(AjaxRequestTarget aTarget)
    {
        selectedTag.setObject(null);

        // Reload whole page because master panel also needs to be reloaded.
        aTarget.add(getPage());
    }

    private void moveTag(boolean up)
    {
        CodebookTag tag = selectedTag.getObject();
        List<CodebookTag> tags = codebookSchemaService.listTags(tag.getCodebook());
        int tagIdx = tags.indexOf(tag);
        if (tagIsMovable(up)) {
            int oldOrdering = tag.getTagOrdering();
            CodebookTag swapTag = tags.get(tagIdx + (up ? -1 : 1));
            tag.setTagOrdering(swapTag.getTagOrdering());
            swapTag.setTagOrdering(oldOrdering);
            codebookSchemaService.createOrUpdateCodebookTag(tag);
            codebookSchemaService.createOrUpdateCodebookTag(swapTag);
        }
    }

    private boolean tagIsMovable(boolean up) {
        CodebookTag tag = selectedTag.getObject();
        List<CodebookTag> tags = Collections.emptyList();
        if (tag != null)
            tags = codebookSchemaService.listTags(tag.getCodebook());
        int tagIdx = tags.indexOf(tag);

        if (tagIdx == -1) // tag is not yet saved and therefore not in the Tag list
            return false;
        // first cant move up and last cant move down
        else return (tagIdx != 0 || !up) && (tagIdx != tags.size() - 1 || up);
    }

    private class TagExistsValidator
        implements IValidator<String>
    {
        private static final long serialVersionUID = 6697292531559511021L;

        @Override
        public void validate(IValidatable<String> aValidatable)
        {
            String newName = aValidatable.getValue();
            String oldName = aValidatable.getModel().getObject();
            if (!StringUtils.equals(newName, oldName) && isNotBlank(newName)
                    && codebookSchemaService.existsCodebookTag(newName,
                            selectedCodebook.getObject())) {
                aValidatable.error(
                        new ValidationError(new StringResourceModel("tagExistsError").getString()));
            }
        }
    }
}
