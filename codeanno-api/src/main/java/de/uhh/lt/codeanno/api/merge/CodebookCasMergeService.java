/*
 * Copyright 2021 Language Technology Group (LT) Universität Hamburg
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
package de.uhh.lt.codeanno.api.merge;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.CasDiff;
import de.tudarmstadt.ukp.clarin.webanno.curation.casmerge.CasMergeOperationResult;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.uhh.lt.codeanno.model.Codebook;

public interface CodebookCasMergeService
{
    String SERVICE_NAME = "CodebookCasMergeService";

    String CURATION_USER = "CURATION_USER";
    String CORRECTION_USER = "CORRECTION_USER";

    CasMergeOperationResult.ResultState mergeCodebookAnnotation(SourceDocument aDocument,
            String aUsername, Codebook aCodebook, CAS aTargetCas, AnnotationFS aSourceFs,
            boolean aAllowStacking)
        throws AnnotationException;

    /**
     * Using {@code DiffResult}, determine the annotations to be deleted from the randomly generated
     * MergeCase. The initial Merge CAs is stored under a name {@code CurationPanel#CURATION_USER}.
     * <p>
     * Any similar annotations stacked in a {@code CasDiff2.Position} will be assumed a difference
     * <p>
     * Any two annotation with different value will be assumed a difference
     *
     * @param aDiff
     *            the {@link CasDiff.DiffResult}
     * @param aCases
     *            a map of {@code CAS}s for each users and the random merge
     */
    void reMergeCas(CasDiff.DiffResult aDiff, SourceDocument aTargetDocument,
            String aTargetUsername, CAS aTargetCas, Map<String, CAS> aCases)
        throws AnnotationException, UIMAException;

    boolean isMergeIncompleteAnnotations();

    void setMergeIncompleteAnnotations(boolean aMergeIncompleteAnnotations);

    List<Type> getCodebookTypes(CAS mergeJCas, List<Codebook> aCodebooks);

    void prepareMergeCAS(AnnotatorState state, boolean aMergeIncompleteAnnotations,
            boolean aForceRecreateCas)
        throws IOException, UIMAException, ClassNotFoundException, AnnotationException;

    void upgradeCasAndSave(SourceDocument aDocument, String aUsername);

    /**
     * For the first time a curation page is opened, create a MergeCas that contains only agreeing
     * annotations Using the CAS of the curator user.
     *
     * @param aState
     *            the annotator state
     * @param aRandomAnnotationDocument
     *            an annotation document.
     * @param aCasses
     *            the CASes
     * @return the CAS.
     * @throws IOException
     *             if an I/O error occurs.
     */
    CAS createCurationCas(AnnotatorState aState, AnnotationDocument aRandomAnnotationDocument,
            Map<String, CAS> aCasses, boolean aMergeIncompleteAnnotations)
        throws IOException, UIMAException, AnnotationException;

    /**
     * Fetches the CAS that the user will be able to edit. In AUTOMATION/CORRECTION mode, this is
     * the CAS for the CORRECTION_USER and in CURATION mode it is the CAS for the CURATION user.
     *
     * @param aState
     *            the state containing the model and metadata like timestamps
     * @param aCasses
     *            the CASes.
     * @return the CAS.
     * @throws UIMAException
     *             hum?
     * @throws ClassNotFoundException
     *             hum?
     * @throws IOException
     *             if an I/O error occurs.
     * @throws AnnotationException
     *             hum?
     */
    CAS createMergeCas(AnnotatorState aState, Map<String, CAS> aCasses,
            AnnotationDocument randomAnnotationDocument, boolean aUpgrade,
            boolean aMergeIncompleteAnnotations, boolean aForceRecreateCas)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException;

    Map<String, CAS> listCASesForCuration(List<AnnotationDocument> annotationDocuments)
            throws IOException;

    Map<String, CAS> getUserCASes(SourceDocument doc);
}
