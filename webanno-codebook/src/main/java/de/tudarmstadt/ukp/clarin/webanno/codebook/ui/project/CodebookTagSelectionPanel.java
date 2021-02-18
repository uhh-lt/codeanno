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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.project;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.CodebookTag;
import de.tudarmstadt.ukp.clarin.webanno.codebook.service.CodebookSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxFormComponentUpdatingBehavior;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxLink;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.ListPanel_ImplBase;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.OverviewListChoice;

public class CodebookTagSelectionPanel
    extends ListPanel_ImplBase
{
    private static final long serialVersionUID = -1L;

    private @SpringBean CodebookSchemaService codebookSchemaService;

    private final OverviewListChoice<CodebookTag> overviewList;
    private final IModel<Codebook> selectedCodebook;
    private final IModel<CodebookTag> selectedTag;

    public CodebookTagSelectionPanel(String id, IModel<Codebook> aCodebook,
            IModel<CodebookTag> aTag)
    {
        super(id, aCodebook);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        selectedCodebook = aCodebook;
        selectedTag = aTag;

        overviewList = new OverviewListChoice<>("tag");
        overviewList.setChoiceRenderer(new ChoiceRenderer<>("name"));
        overviewList.setModel(selectedTag);
        overviewList.setChoices(LambdaModel.of(this::listTags));
        overviewList.add(new LambdaAjaxFormComponentUpdatingBehavior("change", this::onChange));
        add(overviewList);

        add(new LambdaAjaxLink("create", this::actionCreate));
        add(new LambdaAjaxLink("resetOrdering", this::actionResetOrdering));
    }

    private List<CodebookTag> listTags()
    {
        if (selectedCodebook.getObject() != null) {
            List<CodebookTag> tags = codebookSchemaService.listTags(selectedCodebook.getObject());
            // init the ordering if necessary
            boolean initNecessary =
                    tags.stream().filter(tag -> tag.getTagOrdering() == 0).count() > 1;
            if (initNecessary)
                actionResetOrdering(null);
            return tags;
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    protected void onConfigure()
    {
        super.onConfigure();
        this.setVisible(selectedCodebook.getObject() != null
                && selectedCodebook.getObject().getId() != null);
    }

    @Override
    protected void actionCreate(AjaxRequestTarget aTarget) throws Exception
    {
        super.actionCreate(aTarget);
        selectedTag.setObject(new CodebookTag());
    }

    protected void actionResetOrdering(AjaxRequestTarget aTarget)
    {
        List<CodebookTag> tags = selectedCodebook.getObject() != null ?
                                 codebookSchemaService.listTags(selectedCodebook.getObject()) :
                                 Collections.emptyList();
        tags.sort(Comparator.comparing(CodebookTag::getName));
        tags.forEach(t -> t.setTagOrdering(tags.indexOf(t)));
        tags.forEach(codebookSchemaService::createOrUpdateCodebookTag);

        if (aTarget != null)
            aTarget.add(this, overviewList);
    }
}
