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

import static de.tudarmstadt.ukp.clarin.webanno.api.CasUpgradeMode.AUTO_CAS_UPGRADE;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorStateUtils.updateDocumentTimestampAfterWrite;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.copyDocumentMetadata;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.createSentence;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.createToken;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.exists;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.getAddr;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectSentences;
import static de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil.selectTokens;
import static de.tudarmstadt.ukp.clarin.webanno.api.casstorage.CasAccessMode.SHARED_READ_ONLY_ACCESS;
import static de.tudarmstadt.ukp.clarin.webanno.api.casstorage.CasAccessMode.UNMANAGED_ACCESS;
import static de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState.FINISHED;
import static org.apache.uima.cas.impl.Serialization.deserializeCASComplete;
import static org.apache.uima.cas.impl.Serialization.serializeCASComplete;
import static org.apache.uima.fit.util.CasUtil.getType;
import static org.apache.uima.fit.util.CasUtil.selectAt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASCompleteSerializer;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.factory.CasFactory;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.event.BulkAnnotationEvent;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.util.WebAnnoCasUtil;
import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.CasDiff;
import de.tudarmstadt.ukp.clarin.webanno.curation.casdiff.api.Position;
import de.tudarmstadt.ukp.clarin.webanno.curation.casmerge.AlreadyMergedException;
import de.tudarmstadt.ukp.clarin.webanno.curation.casmerge.CasMergeOperationResult;
import de.tudarmstadt.ukp.clarin.webanno.curation.storage.CurationDocumentService;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.StopWatch;
import de.tudarmstadt.ukp.clarin.webanno.support.logging.LogMessage;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.uhh.lt.codeanno.api.adapter.CodebookCasAdapter;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;

@Component(CodebookCasMergeService.SERVICE_NAME)
public class CodebookCasMergeServiceImpl
    implements CodebookCasMergeService
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CodebookSchemaService codebookSchemaService;
    private final ApplicationEventPublisher eventPublisher;
    private final DocumentService documentService;
    private final CurationDocumentService curationDocumentService;
    private final UserDao userRepository;
    private final AnnotationSchemaService annotationSchemaService;

    private boolean mergeIncompleteAnnotations = false;

    @Autowired
    public CodebookCasMergeServiceImpl(CodebookSchemaService aSchemaService,
            DocumentService documentService, CurationDocumentService curationDocumentService,
            UserDao userRepository, AnnotationSchemaService annotationSchemaService,
            ApplicationEventPublisher aEventPublisher)
    {
        this.codebookSchemaService = aSchemaService;
        this.documentService = documentService;
        this.curationDocumentService = curationDocumentService;
        this.userRepository = userRepository;
        this.annotationSchemaService = annotationSchemaService;
        this.eventPublisher = aEventPublisher;
    }

    private static void clearAnnotations(CAS aCas) throws UIMAException
    {
        CAS backup = CasFactory.createCas((TypeSystemDescription) null);
        // Copy the CAS - basically we do this just to keep the full type system
        // information
        CASCompleteSerializer serializer = serializeCASComplete((CASImpl) aCas);
        deserializeCASComplete(serializer, (CASImpl) backup);

        // Remove all annotations from the target CAS but we keep the type system!
        aCas.reset();

        // Copy over essential information
        if (exists(backup, getType(backup, DocumentMetaData.class))) {
            copyDocumentMetadata(backup, aCas);
        }
        else {
            WebAnnoCasUtil.createDocumentMetadata(aCas);
        }
        aCas.setDocumentLanguage(backup.getDocumentLanguage()); // DKPro Core Issue 435
        aCas.setDocumentText(backup.getDocumentText());

        // Transfer token boundaries
        for (AnnotationFS t : selectTokens(backup)) {
            aCas.addFsToIndexes(createToken(aCas, t.getBegin(), t.getEnd()));
        }

        // Transfer sentence boundaries
        for (AnnotationFS s : selectSentences(backup)) {
            aCas.addFsToIndexes(createSentence(aCas, s.getBegin(), s.getEnd()));
        }
    }

    /**
     * Do not check on agreement on Position and SOfa feature - already checked
     */
    private static boolean isBasicFeature(Feature aFeature)
    {
        // FIXME The two parts of this OR statement seem to be redundant. Also the order
        // of the check should be changes such that equals is called on the constant.
        return aFeature.getName().equals(CAS.FEATURE_FULL_NAME_SOFA)
                || aFeature.toString().equals("uima.cas.AnnotationBase:sofa");
    }

    /**
     * Return true if these two annotations agree on every non slot features
     */
    private static boolean isSameAnno(AnnotationFS aFs1, AnnotationFS aFs2)
    {
        // Check offsets (because they are excluded by shouldIgnoreFeatureOnMerge())
        if (aFs1.getBegin() != aFs2.getBegin() || aFs1.getEnd() != aFs2.getEnd()) {
            return false;
        }

        // Check the features (basically limiting to the primitive features)
        for (Feature f : aFs1.getType().getFeatures()) {
            if (shouldIgnoreFeatureOnMerge(aFs1, f)) {
                continue;
            }

            Object value1 = getFeatureValue(aFs1, f);
            Object value2 = getFeatureValue(aFs2, f);

            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the feature value of this {@code Feature} on this annotation
     */
    private static Object getFeatureValue(FeatureStructure aFS, Feature aFeature)
    {
        switch (aFeature.getRange().getName()) {
        case CAS.TYPE_NAME_STRING:
            return aFS.getFeatureValueAsString(aFeature);
        case CAS.TYPE_NAME_BOOLEAN:
            return aFS.getBooleanValue(aFeature);
        case CAS.TYPE_NAME_FLOAT:
            return aFS.getFloatValue(aFeature);
        case CAS.TYPE_NAME_INTEGER:
            return aFS.getIntValue(aFeature);
        case CAS.TYPE_NAME_BYTE:
            return aFS.getByteValue(aFeature);
        case CAS.TYPE_NAME_DOUBLE:
            return aFS.getDoubleValue(aFeature);
        case CAS.TYPE_NAME_LONG:
            aFS.getLongValue(aFeature);
        case CAS.TYPE_NAME_SHORT:
            aFS.getShortValue(aFeature);
        default:
            return null;
        // return aFS.getFeatureValue(aFeature);
        }
    }

    private static boolean existsSameAt(CAS aCas, AnnotationFS aFs)
    {
        return selectAt(aCas, aFs.getType(), aFs.getBegin(), aFs.getEnd()).stream()
                .filter(cand -> isSameAnno(aFs, cand)).findAny().isPresent();
    }

    private static boolean shouldIgnoreFeatureOnMerge(FeatureStructure aFS, Feature aFeature)
    {
        return !WebAnnoCasUtil.isPrimitiveType(aFeature.getRange()) || isBasicFeature(aFeature)
                || aFeature.getName().equals(CAS.FEATURE_FULL_NAME_BEGIN)
                || aFeature.getName().equals(CAS.FEATURE_FULL_NAME_END);
    }

    @Override
    public boolean isMergeIncompleteAnnotations()
    {
        return mergeIncompleteAnnotations;
    }

    @Override
    public void setMergeIncompleteAnnotations(boolean aMergeIncompleteAnnotations)
    {
        mergeIncompleteAnnotations = aMergeIncompleteAnnotations;
    }

    private boolean shouldMerge(CasDiff.DiffResult aDiff, CasDiff.ConfigurationSet cfgs)
    {
        boolean stacked = cfgs.getConfigurations().stream()
                .anyMatch(CasDiff.Configuration::isStacked);
        if (stacked) {
            log.trace(" `-> Not merging stacked annotation");
            return false;
        }

        if (!aDiff.isComplete(cfgs) && !isMergeIncompleteAnnotations()) {
            log.trace(" `-> Not merging incomplete annotation");
            return false;
        }

        if (!aDiff.isAgreement(cfgs)) {
            log.trace(" `-> Not merging annotation with disagreement");
            return false;
        }

        return true;
    }

    @Override
    public void reMergeCas(CasDiff.DiffResult aDiff, SourceDocument aTargetDocument,
            String aTargetUsername, CAS aTargetCas, Map<String, CAS> aCases)
        throws AnnotationException, UIMAException
    {

        List<LogMessage> messages = new ArrayList<>();

        // Remove any annotations from the target CAS - keep type system, sentences and tokens
        clearAnnotations(aTargetCas);

        // If there is nothing to merge, bail out
        if (aCases.isEmpty()) {
            return;
        }

        // Set up a cache for resolving type to layer to avoid hammering the DB as we
        // process each position
        Map<String, Codebook> type2code = aDiff.getPositions().stream().map(Position::getType)
                .distinct()
                .map(type -> codebookSchemaService.getCodeBook(type, aTargetDocument.getProject()))
                .collect(Collectors.toMap(Codebook::getName, Function.identity()));

        List<String> codeNames = new ArrayList<>(type2code.keySet());

        for (String codeName : codeNames) {
            List<CodebookPosition> positions = aDiff.getPositions().stream()
                    .filter(pos -> codeName.equals(pos.getType()))
                    .filter(pos -> pos instanceof CodebookPosition)
                    .map(pos -> (CodebookPosition) pos).filter(pos -> pos.getFeature() == null)
                    .collect(Collectors.toList());

            if (positions.isEmpty()) {
                continue;
            }

            log.trace("Processing {} codebook positions on layer {}", positions.size(), codeName);
            for (CodebookPosition position : positions) {
                log.trace(" |   processing {}", position);
                CasDiff.ConfigurationSet cfgs = aDiff.getConfigurtionSet(position);

                if (!shouldMerge(aDiff, cfgs)) {
                    continue;
                }

                try {

                    Map<String, List<CAS>> casMap = new LinkedHashMap<>();
                    for (Map.Entry<String, CAS> e : aCases.entrySet()) {
                        casMap.put(e.getKey(), Collections.singletonList(e.getValue()));
                    }
                    AnnotationFS sourceFS = (AnnotationFS) cfgs.getConfigurations().get(0)
                            .getRepresentative(casMap);
                    mergeCodebookAnnotation(aTargetDocument, aTargetUsername,
                            type2code.get(position.getType()), aTargetCas, sourceFS, false);
                    log.trace(" `-> merged annotation with agreement");
                }
                catch (AnnotationException e) {
                    log.trace(" `-> not merged annotation: {}", e.getMessage());
                    messages.add(LogMessage.error(this, "%s", e.getMessage()));
                }
            }
        }

        if (eventPublisher != null) {
            eventPublisher.publishEvent(
                    new BulkAnnotationEvent(this, aTargetDocument, aTargetUsername, null));
        }

    }

    private void copyFeatures(SourceDocument aDocument, String aUsername,
            CodebookCasAdapter aAdapter, Codebook aCodebook, FeatureStructure aTargetFS,
            FeatureStructure aSourceFs)
    {
        CodebookFeature feature = codebookSchemaService.listCodebookFeature(aCodebook).get(0);

        Type sourceFsType = aAdapter.getAnnotationType(aSourceFs.getCAS());
        Feature sourceFeature = sourceFsType.getFeatureByBaseName(feature.getName());

        if (sourceFeature == null) {
            throw new IllegalStateException("Target CAS type [" + sourceFsType.getName()
                    + "] does not define a feature named [" + feature.getName() + "]");
        }

        if (shouldIgnoreFeatureOnMerge(aSourceFs, sourceFeature)) {
            return;
        }

        Object value = aAdapter.getExistingCodeValue(aSourceFs.getCAS(), feature);
        aAdapter.setFeatureValue(aTargetFS.getCAS(), feature, getAddr(aTargetFS), value);

    }

    @Override
    public CasMergeOperationResult.ResultState mergeCodebookAnnotation(SourceDocument aDocument,
            String aUsername, Codebook aCodebook, CAS aTargetCas, AnnotationFS aSourceFs,
            boolean aAllowStacking)
        throws AnnotationException
    {
        if (existsSameAt(aTargetCas, aSourceFs)) {
            throw new AlreadyMergedException(
                    "The annotation already exists in the target document.");
        }

        CodebookCasAdapter adapter = new CodebookCasAdapter(aCodebook);
        // AnnotationFS existingAnnos = CasUtil.selectSingleAt(aTargetCas,
        // aSourceFs.getType(),
        // aSourceFs.getBegin(), aSourceFs.getEnd());

        List<AnnotationFS> existingAnnos = selectAt(aTargetCas, aSourceFs.getType(),
                aSourceFs.getBegin(), aSourceFs.getEnd());
        if (existingAnnos.isEmpty()) {
            adapter.add(aTargetCas);

            AnnotationFS mergedCode = adapter.getExistingFs(aTargetCas);
            copyFeatures(aDocument, aUsername, adapter, aCodebook, mergedCode, aSourceFs);
            return CasMergeOperationResult.ResultState.CREATED;
        }

        else {
            copyFeatures(aDocument, aUsername, adapter, aCodebook, existingAnnos.get(0), aSourceFs);
        }
        return CasMergeOperationResult.ResultState.UPDATED;

    }

    @Override
    public List<Type> getCodebookTypes(CAS mergeJCas, List<Codebook> aCodebooks)
    {
        List<Type> entryTypes = new LinkedList<>();

        for (Codebook codebook : aCodebooks) {
            CodebookCasAdapter cA = new CodebookCasAdapter(codebook);
            entryTypes.add(cA.getAnnotationType(mergeJCas));
        }
        return entryTypes;
    }

    @Override
    public CAS createMergeCas(AnnotatorState aState, Map<String, CAS> aCasses,
            AnnotationDocument randomAnnotationDocument, boolean aUpgrade,
            boolean aMergeIncompleteAnnotations, boolean aForceRecreateCas)
        throws UIMAException, ClassNotFoundException, IOException, AnnotationException
    {

        // Upgrading should be an explicit action during the opening of a document at
        // the
        // end of the open dialog - it must not happen during editing because the CAS
        // addresses are used as IDs in the UI
        CAS mergeCas = null;
        try {
            mergeCas = curationDocumentService.readCurationCas(aState.getDocument());
            if (aUpgrade) {
                curationDocumentService.upgradeCurationCas(mergeCas, aState.getDocument());
                curationDocumentService.writeCurationCas(mergeCas, aState.getDocument(), true);
                updateDocumentTimestampAfterWrite(aState,
                        curationDocumentService.getCurationCasTimestamp(aState.getDocument()));
            }
        }
        // Create JCas, if it could not be loaded from the file system
        catch (Exception e) {
            mergeCas = createCurationCas(aState, randomAnnotationDocument, aCasses,
                    aMergeIncompleteAnnotations);
            updateDocumentTimestampAfterWrite(aState,
                    curationDocumentService.getCurationCasTimestamp(aState.getDocument()));
        }

        return mergeCas;
    }

    @Override
    public void prepareMergeCAS(AnnotatorState state, boolean aMergeIncompleteAnnotations,
            boolean aForceRecreateCas)
        throws IOException, UIMAException, ClassNotFoundException, AnnotationException
    {

        List<AnnotationDocument> finishedAnnotationDocuments = new ArrayList<>();

        // FIXME: This is slow and should be done via a proper SQL query
        for (AnnotationDocument annotationDocument : documentService
                .listAnnotationDocuments(state.getDocument())) {
            if (annotationDocument.getState().equals(FINISHED)) {
                finishedAnnotationDocuments.add(annotationDocument);
            }
        }

        if (finishedAnnotationDocuments.isEmpty()) {
            throw new IllegalStateException("This document has the state "
                    + state.getDocument().getState() + " but "
                    + "there are no finished annotation documents! This "
                    + "can for example happen when curation on a document has already started "
                    + "and afterwards all annotators have been remove "
                    + "from the project, have been " + "disabled or if all were put back into "
                    + AnnotationDocumentState.IN_PROGRESS + " mode. It can "
                    + "also happen after importing a project when the users and/or permissions "
                    + "were not imported (only admins can do this via the projects page in the) "
                    + "administration dashboard and if none of the imported users have been "
                    + "enabled via the users management page after the import (also something "
                    + "that only administrators can do).");
        }

        AnnotationDocument randomAnnotationDocument = finishedAnnotationDocuments.get(0);

        // upgrade CASes for each user
        // TODO what if new type is added once the user finished
        // annotation
        for (AnnotationDocument ad : finishedAnnotationDocuments) {
            upgradeCasAndSave(ad.getDocument(), ad.getUser());
        }

        Map<String, CAS> curationCASes = listCASesForCuration(finishedAnnotationDocuments);

        createMergeCas(state, curationCASes, randomAnnotationDocument, true, aForceRecreateCas,
                aMergeIncompleteAnnotations);
    }

    @Override
    public void upgradeCasAndSave(SourceDocument aDocument, String aUsername)
    {
        User user = userRepository.get(aUsername);
        if (documentService.existsAnnotationDocument(aDocument, user)) {
            AnnotationDocument annotationDocument = documentService.getAnnotationDocument(aDocument,
                    user);
            try {
                CAS cas = documentService.readAnnotationCas(annotationDocument);
                annotationSchemaService.upgradeCas(cas, annotationDocument);
                documentService.writeAnnotationCas(cas, annotationDocument, false);
            }
            catch (Exception e) {
                // no need to catch, it is acceptable that no curation document
                // exists to be upgraded while there are annotation documents
            }
        }
    }

    @Override
    public CAS createCurationCas(AnnotatorState aState,
            AnnotationDocument aRandomAnnotationDocument, Map<String, CAS> aCasses,
            boolean aMergeIncompleteAnnotations)
        throws IOException, UIMAException, AnnotationException
    {
        Validate.notNull(aState, "State must be specified");
        Validate.notNull(aRandomAnnotationDocument, "Annotation document must be specified");

        // We need a modifiable copy of some annotation document which we can use to initialize
        // the curation CAS. This is an exceptional case where BYPASS is the correct choice
        CAS mergeCas = documentService.readAnnotationCas(aRandomAnnotationDocument,
                UNMANAGED_ACCESS);
        CasDiff.DiffResult diff;
        try (StopWatch watch = new StopWatch(log, "CasDiff (codebook curation)")) {
            // codebook cas diff
            diff = CodebookDiff.doCodebookDiff(codebookSchemaService, aState.getProject(), null,
                    aCasses, 0, 0);
        }
        try (StopWatch watch = new StopWatch(log, "CasMerge (codebook curation)")) {
            // cas merge
            setMergeIncompleteAnnotations(aMergeIncompleteAnnotations);
            reMergeCas(diff, aState.getDocument(), aState.getUser().getUsername(), mergeCas,
                    aCasses);
        }

        curationDocumentService.writeCurationCas(mergeCas, aRandomAnnotationDocument.getDocument(),
                false);

        return mergeCas;
    }

    @Override
    public Map<String, CAS> listCASesForCuration(List<AnnotationDocument> annotationDocuments)
        throws IOException
    {
        Map<String, CAS> casses = new HashMap<>();
        for (AnnotationDocument annotationDocument : annotationDocuments) {
            String username = annotationDocument.getUser();

            if (!annotationDocument.getState().equals(AnnotationDocumentState.FINISHED)) {
                continue;
            }

            CAS cas = documentService.readAnnotationCas(annotationDocument.getDocument(),
                    annotationDocument.getUser(), AUTO_CAS_UPGRADE, SHARED_READ_ONLY_ACCESS);
            casses.put(username, cas);
        }
        return casses;
    }

    /**
     * @return a map of all users and their corresponding CASes for the given document )
     */
    public Map<String, CAS> getUserCASes(SourceDocument doc)
    {
        Map<String, CAS> curationCASes = new HashMap<>();

        // get all annotation documents of all users of the current doc
        List<AnnotationDocument> annotationDocuments = documentService.listAnnotationDocuments(doc);

        for (AnnotationDocument annotationDocument : annotationDocuments) {
            String username = annotationDocument.getUser();
            if (annotationDocument.getState().equals(AnnotationDocumentState.FINISHED)) {
                CAS cas;
                try {
                    cas = documentService.readAnnotationCas(annotationDocument);
                    curationCASes.put(username, cas);
                }
                catch (IOException e) {
                    log.error("Unable to load the curation CASes: " + e.getMessage());
                }
            }
        }

        return curationCASes;
    }
}
