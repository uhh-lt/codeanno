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
package de.uhh.lt.codeanno.api.export;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;

import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportRequest;
import de.tudarmstadt.ukp.clarin.webanno.export.model.ExportedProject;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.uhh.lt.codeanno.model.Codebook;

public interface CodebookImportExportService
{
    String SERVICE_NAME = "codebookImportExportService";

    File exportCodebooksToFile(CAS cas, SourceDocument document, String fileName, File exportDir,
            boolean withHeaders, boolean withText, List<Codebook> codebooks, String annotator,
            String documentName)
        throws IOException, UIMAException;

    File exportCodebookDocument(SourceDocument document, String user, String fileName, Mode mode,
            File exportDir, boolean withHeaders, boolean withText, List<Codebook> codebooks)
        throws UIMAException, IOException, ClassNotFoundException;

    void exportCodebookAnnotations(ProjectExportRequest aRequest, ExportedProject aExProject,
            File aStage)
        throws IOException, UIMAException;

    List<ExportedCodebook> exportCodebooks(List<Codebook> codebooks);

    void importCodebooks(List<ExportedCodebook> exportedCodebooks, Project aProject);
}
