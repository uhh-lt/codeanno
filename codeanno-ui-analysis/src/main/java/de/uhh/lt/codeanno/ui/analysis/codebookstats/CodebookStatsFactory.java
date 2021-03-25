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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.ui.analysis.Stats;

public interface CodebookStatsFactory
{

    CodebookStats create(Project project, boolean annotators, boolean curators);

    CodebookStats create(List<SourceDocument> docs, boolean annotators, boolean curators);

    class CodebookStats
        extends Stats<Codebook, CodebookTag>
    {

        private static final long serialVersionUID = 1627935552528272517L;

        public CodebookStats(Map<Codebook, List<Pair<CodebookTag, Integer>>> sortedFrequencies)
        {
            super(sortedFrequencies);
        }
    }

}
