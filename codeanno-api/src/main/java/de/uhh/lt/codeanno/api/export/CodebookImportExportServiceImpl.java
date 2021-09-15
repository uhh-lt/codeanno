/*
 * Copyright 2021
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
package de.uhh.lt.codeanno.api.export;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportRequest;
import de.tudarmstadt.ukp.clarin.webanno.export.model.ExportedProject;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocumentState;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.uhh.lt.codeanno.api.CodebookConstants;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;
import de.uhh.lt.codeanno.csv.WebannoCsvWriter;
import de.uhh.lt.codeanno.model.Codebook;
import de.uhh.lt.codeanno.model.CodebookFeature;
import de.uhh.lt.codeanno.model.CodebookTag;
import de.uhh.lt.codeanno.tree.CodebookTreeProvider;
import de.uhh.lt.codeanno.tree.model.CodebookNode;

@Component
public class CodebookImportExportServiceImpl
    implements CodebookImportExportService
{

    private @Autowired AnnotationSchemaService annotationService;
    private @Autowired CodebookSchemaService codebookService;
    private @Autowired @Lazy DocumentService documentService;
    private @Autowired UserDao userRepository;
    private @Autowired CasStorageService casStorageService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String CODEBOOKS_FOLDER = "/codebooks/";

    @Override
    public void exportCodebookAnnotations(ProjectExportRequest aRequest, ExportedProject aExProject,
            File aStage)
        throws IOException, UIMAException
    {
        Project project = aRequest.getProject();

        File codebookDir = new File(aStage.getAbsolutePath() + CODEBOOKS_FOLDER);

        FileUtils.forceMkdir(codebookDir);
        appendCodebooks(project, codebookDir);
    }

    private File appendCodebooks(Project project, File codebookDir)
        throws IOException, UIMAException
    {

        List<SourceDocument> documents = documentService.listSourceDocuments(project);
        List<Codebook> codebooks = codebookService.listCodebook(project);

        File codebookFile = new File(codebookDir,
                project.getName() + CodebookConstants.CODEBOOK_EXT);

        boolean withHeader = true;// for the first doc we need the headers in the CSV

        for (SourceDocument sourceDocument : documents) {
            boolean withText = false;// do not write the text for each annotation document
            // there is one anno doc per user plus CURATION, MERGE, AGREEMENT anno docs
            for (AnnotationDocument annotationDocument : documentService
                    .listAnnotationDocuments(sourceDocument)) {
                if (userRepository.get(annotationDocument.getUser()) != null
                        && !annotationDocument.getState().equals(AnnotationDocumentState.NEW)
                        && !annotationDocument.getState().equals(AnnotationDocumentState.IGNORE)) {

                    // TODO Flo: I think we can skip this check since every anno doc MUST HAVE a CAS
                    // would increase performance...
                    File annotationFileAsSerialisedCas = documentService.getCasFile(sourceDocument,
                            annotationDocument.getUser());
                    if (annotationFileAsSerialisedCas.exists()) {
                        codebookFile = exportCodebookDocument(sourceDocument,
                                annotationDocument.getUser(), codebookFile.getAbsolutePath(),
                                Mode.ANNOTATION, codebookDir, withHeader, withText, codebooks);
                        withHeader = false;// only for the first doc we need the headers
                    }

                    log.info("Appending codebook annotation for user ["
                            + annotationDocument.getUser() + "] for source document ["
                            + sourceDocument.getId() + "] in project [" + project.getName()
                            + "] with id [" + project.getId() + "]");
                }
            }
        }
        return codebookFile;
    }

    private ExportedCodebook createExportedCodebook(Codebook cb, ExportedCodebook parent)
    {
        ExportedCodebook exCB = new ExportedCodebook();

        // basic attributes
        exCB.setDescription(cb.getDescription());
        exCB.setName(cb.getName());
        exCB.setUiName(cb.getUiName());
        exCB.setProjectName(cb.getProject().getName());
        exCB.setOrder(cb.getOrdering());
        exCB.setParent(parent);

        // features
        List<ExportedCodebookFeature> exFeatures = new ArrayList<>();
        for (CodebookFeature feature : codebookService.listCodebookFeature(cb)) {
            ExportedCodebookFeature exF = new ExportedCodebookFeature();
            exF.setDescription(feature.getDescription());
            exF.setName(feature.getName());
            exF.setProjectName(feature.getProject().getName());
            exF.setType(feature.getType());
            exF.setUiName(feature.getUiName());
            exFeatures.add(exF);
        }
        exCB.setFeatures(exFeatures);

        // tags
        List<ExportedCodebookTag> exTags = new ArrayList<>();
        for (CodebookTag tag : codebookService.listTags(cb)) {
            ExportedCodebookTag exTag = new ExportedCodebookTag();
            exTag.setDescription(tag.getDescription());
            exTag.setName(tag.getName());

            if (parent != null) {
                for (ExportedCodebookTag t : parent.getTags()) {
                    if (tag.getParent() != null && (tag.getParent().getName().equals(t.getName())))
                        exTag.setParent(t);
                }
            }
            exTags.add(exTag);
        }
        exCB.setTags(exTags);

        return exCB;
    }

    private List<ExportedCodebook> createExportedCodebooks(CodebookTreeProvider tree)
    {
        List<ExportedCodebook> exportedCodebooks = new ArrayList<>();

        // create root ExCBs
        for (CodebookNode root : tree.getRootNodes()) {
            ExportedCodebook rootExCB = createExportedCodebook(tree.getCodebook(root), null);
            // exportedCodebooks.add(rootExCB);

            // create children recursively
            for (Codebook child : tree.getChildren(tree.getCodebook(root)))
                createExportedCodebookRecursively(child, rootExCB, exportedCodebooks, tree);
        }

        return exportedCodebooks;
    }

    private void createExportedCodebookRecursively(Codebook child, ExportedCodebook parent,
            List<ExportedCodebook> exCBs, CodebookTreeProvider tree)
    {

        ExportedCodebook childExCB = createExportedCodebook(child, parent);
        if (tree.getCodebookNode(child).isLeaf())
            exCBs.add(childExCB);

        // create children recursively
        for (Codebook childrenChild : tree.getChildren(child))
            createExportedCodebookRecursively(childrenChild, childExCB, exCBs, tree);
    }

    @Override
    public List<ExportedCodebook> exportCodebooks(List<Codebook> codebooks)
    {
        CodebookTreeProvider tree = new CodebookTreeProvider(codebooks);
        return createExportedCodebooks(tree);
    }

    private Codebook createCodebooksRecursively(ExportedCodebook exCB, Project project,
            List<Codebook> importedCodebooks)
    {
        Codebook cb = new Codebook();

        cb.setName(exCB.getName());
        cb.setUiName(exCB.getUiName());
        cb.setOrdering(exCB.getOrder());
        cb.setDescription(exCB.getDescription());
        cb.setProject(project);

        if (exCB.getParent() != null) {
            if (codebookService.existsCodebook(exCB.getParent().getName(), project)) {
                cb.setParent(codebookService.getCodeBook(exCB.getParent().getName(), project));
            }
            else {
                cb.setParent(
                        createCodebooksRecursively(exCB.getParent(), project, importedCodebooks));
            }
        }

        // we have to persist the codebook before importing features and tags
        codebookService.createOrUpdateCodebook(cb);

        // TODO import features and tags
        for (ExportedCodebookFeature exFeature : exCB.getFeatures())
            importExportedCodebookFeature(exFeature, cb);

        for (ExportedCodebookTag exTag : exCB.getTags())
            importExportedCodebookTagsRecursively(exTag, cb);

        importedCodebooks.add(cb);
        return cb;
    }

    private void importExportedCodebookTagsRecursively(ExportedCodebookTag exTag, Codebook cb)
    {
        CodebookTag tag = new CodebookTag();
        tag.setDescription(exTag.getDescription());
        tag.setName(exTag.getName());
        tag.setCodebook(cb);

        if (tag.getTagOrdering() < 1) {
            int lastIndex = codebookService.listTags(cb).size();
            tag.setTagOrdering(lastIndex + 1);
        }

        if (cb.getParent() != null && exTag.getParent() != null) {
            for (CodebookTag pTag : codebookService.listTags(cb.getParent()))
                if (exTag.getParent().getName().equals(pTag.getName()))
                    tag.setParent(pTag);
        }
        else
            tag.setParent(null); // TODO

        CodebookFeature feature = codebookService.listCodebookFeature(cb).get(0);
        if (codebookService.existsCodebookTag(exTag.getName(), feature.getCodebook())) {
            return;
        }

        codebookService.createOrUpdateCodebookTag(tag);
    }

    private void importExportedCodebookFeature(ExportedCodebookFeature exFeature, Codebook cb)
    {
        CodebookFeature feature = new CodebookFeature();
        feature.setUiName(exFeature.getUiName());
        feature.setName(exFeature.getName());
        feature.setDescription(exFeature.getDescription());
        feature.setType(exFeature.getType());
        feature.setCodebook(cb);
        feature.setProject(cb.getProject());

        codebookService.createOrUpdateCodebookFeature(feature);
    }

    @Override
    public void importCodebooks(List<ExportedCodebook> exportedCodebooks, Project aProject)
    {
        /*
         * all of the ExportedCodebook in the list should be leafs (if they were exported with the
         * exportCodebooks() function!
         */
        List<Codebook> importedCodebooks = new ArrayList<>();

        for (ExportedCodebook leafExCB : exportedCodebooks) {
            createCodebooksRecursively(leafExCB, aProject, importedCodebooks);
        }
    }

    @Override
    public File exportCodebookDocument(SourceDocument aDocument, String aUser, String aFileName,
            Mode aMode, File aExportDir, boolean aWithHeaders, boolean aWithText,
            List<Codebook> aCodebooks)
        throws UIMAException, IOException
    {

        File annotationFolder = casStorageService.getAnnotationFolder(aDocument);
        String serializedCasFileName;
        // for Correction, it will export the corrected document (of the logged in user)
        // (CORRECTION_USER.ser is the automated result displayed for the user to
        // correct it, not
        // the final result) for automation, it will export either the corrected
        // document
        // (Annotated) or the automated document
        if (aMode.equals(Mode.ANNOTATION) || aMode.equals(Mode.AUTOMATION)
                || aMode.equals(Mode.CORRECTION)) {
            serializedCasFileName = aUser + ".ser";
        }
        // The merge result will be exported
        else {
            serializedCasFileName = WebAnnoConst.CURATION_USER + ".ser";
        }

        // Read file
        File serializedCasFile = new File(annotationFolder, serializedCasFileName);
        if (!serializedCasFile.exists()) {
            throw new FileNotFoundException("CAS file [" + serializedCasFileName
                    + "] not found in [" + annotationFolder + "]");
        }

        CAS cas = CasCreationUtils.createCas((TypeSystemDescription) null, null, null);
        CasPersistenceUtils.readSerializedCas(cas, serializedCasFile);

        // Update type system the CAS
        // quick fix works by simply commenting out the line below. First tests also work properly
        // annotationService.upgradeCas(cas, aDocument, aUser);

        String documentName = aDocument.getName();
        File exportFile = exportCodebooksToFile(cas, aDocument, aFileName, aExportDir, aWithHeaders,
                aWithText, aCodebooks, aUser, documentName);

        return exportFile;
    }

    @Override
    public File exportCodebooksToFile(CAS cas, SourceDocument aDocument, String aFileName,
            File aExportDir, boolean aWithHeaders, boolean aWithText, List<Codebook> aCodebooks,
            String aAnnotator, String aDocumentName)
        throws IOException, UIMAException
    {

        AnalysisEngineDescription writer = createEngineDescription(WebannoCsvWriter.class,
                JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION, aExportDir,
                WebannoCsvWriter.PARAM_FILENAME, aFileName, WebannoCsvWriter.PARAM_WITH_HEADERS,
                aWithHeaders, WebannoCsvWriter.PARAM_WITH_TEXT, aWithText,
                WebannoCsvWriter.PARAM_ANNOTATOR, aAnnotator, WebannoCsvWriter.PARAM_DOCUMENT_NAME,
                aDocumentName);

        runPipeline(cas, writer);

        File exportFile = new File(aFileName);
        // FileUtils.copyFile(aExportDir.listFiles()[0], exportFile);
        return exportFile;
    }

}
