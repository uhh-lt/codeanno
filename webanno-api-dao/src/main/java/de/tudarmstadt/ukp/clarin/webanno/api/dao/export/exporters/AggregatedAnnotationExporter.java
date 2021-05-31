/*
 * Copyright 2021
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
 * and Language Technology Universität Hamburg
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
package de.tudarmstadt.ukp.clarin.webanno.api.dao.export.exporters;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.CasStorageService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.WebAnnoConst;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportRequest;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportTaskMonitor;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExporter;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectImportRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.export.CasPersistenceUtils;
import de.tudarmstadt.ukp.clarin.webanno.export.model.ExportedProject;
import de.tudarmstadt.ukp.clarin.webanno.model.*;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.tsv.CodeAnnoHumanTsvWriter;

@Component
public class AggregatedAnnotationExporter
    implements ProjectExporter
{
    private static final String AGGREGATED_ANNOTATION_FOLDER = "/aggregated_annotations";

    private @Autowired @Lazy DocumentService documentService;
    private @Autowired UserDao userRepository;
    private @Autowired CasStorageService casStorageService;

    @Override
    public void exportData(ProjectExportRequest aRequest, ProjectExportTaskMonitor aMonitor,
            ExportedProject aExProject, File aStage)
        throws Exception
    {
        final Project project = aRequest.getProject();
        final File dir = new File(aStage.getAbsolutePath() + AGGREGATED_ANNOTATION_FOLDER);
        final File file = new File(dir, project.getName());
        FileUtils.forceMkdir(dir);
        final List<SourceDocument> documents = documentService.listSourceDocuments(project);

        for (SourceDocument sourceDocument : documents) {
            // there is one annotation doc per user plus CURATION, MERGE, AGREEMENT docs
            for (AnnotationDocument annotationDocument : documentService
                    .listAnnotationDocuments(sourceDocument)) {
                if (userRepository.get(annotationDocument.getUser()) != null
                        && !annotationDocument.getState().equals(AnnotationDocumentState.NEW)
                        && !annotationDocument.getState().equals(AnnotationDocumentState.IGNORE)) {
                    exportCodebookDocument(sourceDocument, annotationDocument.getUser(),
                            file.getAbsolutePath(), Mode.ANNOTATION, dir);
                }
            }
        }
    }

    public void exportCodebookDocument(SourceDocument aDocument, String aUser, String aFileName,
            Mode aMode, File aExportDir)
        throws UIMAException, IOException
    {

        final File annotationFolder = casStorageService.getAnnotationFolder(aDocument);
        final String serializedCasFileName;
        if (aMode.equals(Mode.ANNOTATION) || aMode.equals(Mode.AUTOMATION)
                || aMode.equals(Mode.CORRECTION)) {
            serializedCasFileName = aUser + ".ser";
        }
        else {
            serializedCasFileName = WebAnnoConst.CURATION_USER + ".ser";
        }

        final File serializedCasFile = new File(annotationFolder, serializedCasFileName);
        if (!serializedCasFile.exists()) {
            throw new FileNotFoundException("CAS file [" + serializedCasFileName
                    + "] not found in [" + annotationFolder + "]");
        }

        final CAS cas = CasCreationUtils.createCas((TypeSystemDescription) null, null, null);
        CasPersistenceUtils.readSerializedCas(cas, serializedCasFile);

        final AnalysisEngineDescription writer = createEngineDescription(
                CodeAnnoHumanTsvWriter.class, JCasFileWriter_ImplBase.PARAM_TARGET_LOCATION,
                aExportDir, CodeAnnoHumanTsvWriter.PARAM_FILENAME, aFileName,
                CodeAnnoHumanTsvWriter.PARAM_WITH_HEADERS, false,
                CodeAnnoHumanTsvWriter.PARAM_ANNOTATOR, aUser,
                CodeAnnoHumanTsvWriter.PARAM_DOCUMENT_NAME, aDocument.getName());

        runPipeline(cas, writer);
    }

    @Override
    public void importData(ProjectImportRequest aRequest, Project aProject,
            ExportedProject aExProject, ZipFile aZip)
    {
    }
}
