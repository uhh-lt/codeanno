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

import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.LinkCompareBehavior;
import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.api.Position;
import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.api.Position_ImplBase;

/**
 * 
 * Codebooks will have no specific begin/end position. Lets keep the begin/end
 * position, if in the future we implement codebooks based on regions such as
 * paragraphs
 *
 */
public class CodebookPosition extends Position_ImplBase {
    private final int begin;
    private final int end;
    private final String text;

    public CodebookPosition(String aCollectionId, String aDocumentId, int aCasId, String aType,
            int aBegin, int aEnd, String aText, String aFeature, String aRole, int aLinkTargetBegin,
            int aLinkTargetEnd, String aLinkTargetText, LinkCompareBehavior aLinkCompareBehavior) {
        super(aCollectionId, aDocumentId, aCasId, aType, aFeature, aRole, aLinkTargetBegin,
                aLinkTargetEnd, aLinkTargetText, aLinkCompareBehavior);
        begin = aBegin;
        end = aEnd;
        text = aText;
    }

    /**
     * @return the begin offset.
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @return the end offset.
     */
    public int getEnd() {
        return end;
    }

    @Override
    public int compareTo(Position aOther) {
        int superCompare = super.compareTo(aOther);
        if (superCompare != 0) {
            return superCompare;
        } else {
            CodebookPosition otherSpan = (CodebookPosition) aOther;
            if (begin == otherSpan.begin) {
                return otherSpan.end - end;
            } else {
                return begin - otherSpan.begin;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Codebook [");
        toStringFragment(builder);
        builder.append(", Codebook=(").append(begin).append('-').append(end).append(')');
        builder.append('[').append(text).append(']');
        builder.append(']');
        return builder.toString();
    }

    @Override
    public String toMinimalString() {
        StringBuilder builder = new StringBuilder();
        builder.append(begin).append('-').append(end).append(" [").append(text).append(']');
        return builder.toString();
    }
}
