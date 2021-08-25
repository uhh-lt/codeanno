/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt  
 *  and Language Technology Group  Universität Hamburg 
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
package de.uhh.lt.codeanno.api.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;

import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.model.Codebook;

public class CurationUtil
{

    public final static String CURATION_USER = "CURATION_USER";

    public static List<Type> getCodebookTypes(CAS mergeJCas, List<Codebook> aCodebooks)
    {
        List<Type> entryTypes = new LinkedList<>();

        for (Codebook codebook : aCodebooks) {
            CodebookCasAdapter cA = new CodebookCasAdapter(codebook);
            entryTypes.add(cA.getAnnotationType(mergeJCas));
        }
        return entryTypes;
    }

}
