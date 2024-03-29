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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipFile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportRequest;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExportTaskMonitor;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectExporter;
import de.tudarmstadt.ukp.clarin.webanno.api.export.ProjectImportRequest;
import de.tudarmstadt.ukp.clarin.webanno.export.model.ExportedProject;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.uhh.lt.codeanno.api.service.CodebookSchemaService;

@Component
public class CodebookExporter
    implements ProjectExporter
{
    private static final String CODEBOOKS = "codebooks";

    private @Autowired CodebookImportExportService codebookImportExportService;
    private @Autowired CodebookSchemaService codebookSchemaService;

    @Override
    public void exportData(ProjectExportRequest aRequest, ProjectExportTaskMonitor aMonitor,
            ExportedProject aExProject, File aStage)
        throws Exception
    {
        // export the codebooks (w/o values)
        List<ExportedCodebook> exportedCodebooks = codebookImportExportService
                .exportCodebooks(codebookSchemaService.listCodebook(aRequest.getProject()));

        aExProject.setProperty(CODEBOOKS, exportedCodebooks);
    }

    @Override
    public void importData(ProjectImportRequest aRequest, Project aProject,
            ExportedProject aExProject, ZipFile aZip)
        throws Exception
    {
        // create the codebooks and associate with the project
        Optional<ExportedCodebook[]> exportedCodebooksArray = aExProject.getProperty(CODEBOOKS,
                ExportedCodebook[].class);
        if (exportedCodebooksArray.isPresent()) {
            List<ExportedCodebook> exportedCodebooks = Arrays.asList(exportedCodebooksArray.get());
            codebookImportExportService.importCodebooks(exportedCodebooks, aProject);
        }
    }

}
