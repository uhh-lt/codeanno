/*
 * Copyright 2020
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
package de.uhh.lt.codeanno.ui.analysis.codebookstats;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.ui.analysis.ListViewPanelFilterForm;
import de.uhh.lt.codeanno.ui.analysis.StatsPanel;

public class CodebookStatsPanel<T>
    extends StatsPanel<T>
{
    private static final long serialVersionUID = 6969135652333817611L;

    private @SpringBean CodebookSchemaService codebookSchemaService;
    private @SpringBean CodebookStatsFactory codebookStatsFactory;

    private final ListViewPanelFilterForm listViewPanelFilterForm;

    public CodebookStatsPanel(String id, T analysisTarget)
    {
        super(id, analysisTarget);
        this.listViewPanelFilterForm = new CodebookStatsFilterPanel("filterFormPanel",
                this::updateListView);
        this.add(listViewPanelFilterForm);
        this.setOutputMarkupId(true);
        this.updateListView();
    }

    private void updateListView()
    {
        this.updateListView(0, -1, "", "", null);
    }

    private void updateListView(Integer min, Integer max, String startWith, String contains,
            AjaxRequestTarget target)
    {
        List<Codebook> cbList = null;
        if (this.analysisTarget instanceof Project) {
            cbList = codebookSchemaService.listCodebook((Project) this.analysisTarget);
        }
        else if (this.analysisTarget instanceof SourceDocument) {
            cbList = codebookSchemaService
                    .listCodebook(((SourceDocument) this.analysisTarget).getProject());
        }
        CodebookStatsFactory.CodebookStats stats = this.createStats();
        this.createListView(cbList, stats, min, max, startWith, contains);

        if (target != null)
            target.add(this);
    }

    private void createListView(List<Codebook> cbList, CodebookStatsFactory.CodebookStats stats,
            Integer min, Integer max, String startWith, String contains)
    {
        ListView<Codebook> codebookListView = new ListView<Codebook>("codebooksListView", cbList)
        {
            private static final long serialVersionUID = -4707500638635391896L;

            @Override
            protected void populateItem(ListItem<Codebook> item)
            {

                item.add(new Label("cbName", item.getModelObject().getUiName()));

                // if the stats are empty dont show the list of tags!
                if (stats.getSortedFrequencies().isEmpty()) {
                    WebMarkupContainer tagListView = new WebMarkupContainer("tagsListView");
                    tagListView.add(new WebMarkupContainer("cbTag"),
                            new WebMarkupContainer("cbTagCnt"));
                    tagListView.setOutputMarkupPlaceholderTag(true);
                    tagListView.setVisible(false);
                    item.addOrReplace(tagListView);
                    return;
                }

                List<Pair<CodebookTag, Integer>> tags = stats.getFilteredFrequencies(
                        item.getModelObject(), min, max, startWith, contains);

                ListView<Pair<CodebookTag, Integer>> tagListView =
                        new ListView<Pair<CodebookTag, Integer>>("tagsListView", tags)
                {

                    private static final long serialVersionUID = 5393976460907614174L;

                    @Override
                    protected void populateItem(ListItem<Pair<CodebookTag, Integer>> item)
                    {
                        String tagName = "<EMPTY>";
                        if (item.getModelObject().getKey() != null)
                            tagName = item.getModelObject().getKey().getName();
                        item.add(new Label("cbTag", tagName));
                        item.add(new Label("cbTagCnt", item.getModelObject().getValue()));
                    }
                };

                tagListView.setOutputMarkupPlaceholderTag(true);
                item.addOrReplace(tagListView);
            }
        };

        codebookListView.setOutputMarkupPlaceholderTag(true);
        this.addOrReplace(codebookListView);
    }

    @Override
    public void update(T analysisTarget)
    {
        this.analysisTarget = analysisTarget;
        if (this.analysisTarget != null)
            this.updateListView();
    }

    @Override
    public CodebookStatsFactory.CodebookStats createStats()
    {
        boolean curators = ((CodebookStatsFilterPanel) this.listViewPanelFilterForm)
                .showFromCurators();
        boolean annotators = ((CodebookStatsFilterPanel) this.listViewPanelFilterForm)
                .showFromAnnotators();
        if (this.analysisTarget instanceof Project) {
            this.cachedStats.put(analysisTarget, codebookStatsFactory
                    .create((Project) this.analysisTarget, annotators, curators));
            return (CodebookStatsFactory.CodebookStats) this.cachedStats.get(this.analysisTarget);
        }
        else if (this.analysisTarget instanceof SourceDocument) {
            this.cachedStats.put(analysisTarget,
                    codebookStatsFactory.create(
                            Collections.singletonList((SourceDocument) this.analysisTarget),
                            annotators, curators));
            return (CodebookStatsFactory.CodebookStats) this.cachedStats.get(this.analysisTarget);
        }
        else
            return null; // TODO what to throw?
    }

    public CodebookStatsFactory.CodebookStats getCurrentStats()
    {
        return (CodebookStatsFactory.CodebookStats) this.cachedStats.get(this.analysisTarget);
    }
}
