/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.ui.project.documents;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.fileinput.BootstrapFileInputField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.select.BootstrapSelect;
import de.tudarmstadt.ukp.clarin.webanno.api.AnnotationSchemaService;
import de.tudarmstadt.ukp.clarin.webanno.api.DocumentService;
import de.tudarmstadt.ukp.clarin.webanno.api.ImportExportService;
import de.tudarmstadt.ukp.clarin.webanno.api.format.FormatSupport;
import de.tudarmstadt.ukp.clarin.webanno.model.AnnotationDocument;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;
import de.tudarmstadt.ukp.clarin.webanno.security.model.User;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaAjaxButton;
import de.tudarmstadt.ukp.clarin.webanno.support.lambda.LambdaModel;
import de.tudarmstadt.ukp.clarin.webanno.support.wicket.WicketUtil;
import de.uhh.lt.codeanno.api.CodebookConstants;
import de.uhh.lt.codeanno.csv.WebAnnoCsvFormatSupport;
import de.uhh.lt.codeanno.csv.WebAnnoExcelFormatSupport;
import de.uhh.lt.codeanno.ui.project.CodebookAnnotationDocument;
import de.uhh.lt.codeanno.ui.project.CodebookDocumentUtil;

public class ImportDocumentsPanel
    extends Panel
{
    private static final long serialVersionUID = 4927011191395114886L;

    private final static Logger LOG = LoggerFactory.getLogger(ImportDocumentsPanel.class);

    private @SpringBean DocumentService documentService;
    private @SpringBean ImportExportService importExportService;
    private @SpringBean UserDao userRepository;
    private @SpringBean AnnotationSchemaService annotationService;

    private BootstrapFileInputField fileUpload;

    private IModel<String> format;

    private IModel<Project> projectModel;

    public ImportDocumentsPanel(String aId, IModel<Project> aProject)
    {
        super(aId);

        projectModel = aProject;

        Form<Void> form = new Form<>("form");
        add(form);

        format = Model.of();
        List<String> readableFormats = listReadableFormats();
        if (!readableFormats.isEmpty()) {
            if (readableFormats.contains("Plain text")) {
                format.setObject("Plain text");
            }
            else {
                format.setObject(readableFormats.get(0));
            }
        }

        form.add(fileUpload = new BootstrapFileInputField("documents"));
        fileUpload.getConfig().showPreview(false);
        fileUpload.getConfig().showUpload(false);
        fileUpload.getConfig().showRemove(false);
        fileUpload.setRequired(true);

        DropDownChoice<String> formats = new BootstrapSelect<>("format");
        formats.setModel(format);
        formats.setChoices(LambdaModel.of(this::listReadableFormats));
        form.add(formats);

        form.add(new LambdaAjaxButton<>("import", this::actionImport));
    }

    private List<String> listReadableFormats()
    {
        return importExportService.getReadableFormats().stream() //
                .map(FormatSupport::getName) //
                .sorted() //
                .collect(toList());
    }

    private void actionImport(AjaxRequestTarget aTarget, Form<Void> aForm)
    {
        aTarget.addChildren(getPage(), IFeedback.class);

        List<FileUpload> uploadedFiles = fileUpload.getFileUploads();
        Project project = projectModel.getObject();
        if (isEmpty(uploadedFiles)) {
            error("No document is selected to upload, please select a document first");
            return;
        }
        if (isNull(project.getId())) {
            error("Project not yet created, please save project details!");
            return;
        }

        FormatSupport documentFormat = importExportService.getFormatByName(format.getObject())
                .get();
        if (documentFormat.getClass().getName().equals(WebAnnoCsvFormatSupport.class.getName())) {
            readCSV(uploadedFiles, project, documentFormat);
        }
        else if (documentFormat.getClass().getName()
                .equals(WebAnnoExcelFormatSupport.class.getName())) {
            readExcel(uploadedFiles, project, documentFormat);
        }

        else {

            TypeSystemDescription fullProjectTypeSystem;
            try {
                fullProjectTypeSystem = annotationService.getFullProjectTypeSystem(project);
            }
            catch (Exception e) {
                error("Unable to acquire the type system for project: " + getRootCauseMessage(e));
                LOG.error("Unable to acquire the type system for project [{}]({})",
                        project.getName(), project.getId(), e);
                return;
            }

            // Fetching all documents at once here is faster than calling
            // existsSourceDocument() for
            // every imported document
            Set<String> existingDocuments = documentService.listSourceDocuments(project).stream()//
                    .map(SourceDocument::getName)//
                    .collect(toCollection(HashSet::new));

            for (FileUpload documentToUpload : uploadedFiles) {
                String fileName = documentToUpload.getClientFileName();

                if (existingDocuments.contains(fileName)) {
                    error("Document [" + fileName + "] already uploaded ! Delete "
                            + "the document if you want to upload again");
                    continue;
                }

                // Add the imported document to the set of existing documents just in case the
                // user
                // somehow manages to upload two files with the same name...
                existingDocuments.add(fileName);

                try {
                    SourceDocument document = new SourceDocument();
                    document.setName(fileName);
                    document.setProject(project);
                    document.setFormat(
                            importExportService.getFormatByName(format.getObject()).get().getId());

                    try (InputStream is = documentToUpload.getInputStream()) {
                        documentService.uploadSourceDocument(is, document, fullProjectTypeSystem);
                    }
                    info("Document [" + fileName + "] has been imported successfully!");
                }
                catch (Exception e) {
                    error("Error while uploading document " + fileName + ": "
                            + getRootCauseMessage(e));
                    LOG.error(fileName + ": " + e.getMessage(), e);
                }
            }
        }

        WicketUtil.refreshPage(aTarget, getPage());
    }

    private void readCSV(List<FileUpload> uploadedFiles, Project project,
            FormatSupport documentFormat)
    {
        for (FileUpload documentToUpload : uploadedFiles) {
            String fileName = documentToUpload.getClientFileName();
            try {
                List<CodebookAnnotationDocument> codebookDocuments = CodebookDocumentUtil
                        .getCodebookAnnotations(documentToUpload);
                for (CodebookAnnotationDocument cD : codebookDocuments) {
                    SourceDocument document = new SourceDocument();
                    document.setName(cD.getDocumentName());
                    document.setProject(project);
                    document.setFormat(documentFormat.getId());
                    InputStream is = CodebookDocumentUtil.getStream(cD);
                    documentService.uploadSourceDocument(is, document);
                    for (int u = 0; u < cD.getAnnotators().size(); u++) {

                        try {
                            User user = userRepository.get(cD.getAnnotators().get(u));

                            AnnotationDocument annotationDocument = documentService
                                    .createOrGetAnnotationDocument(document, user);
                            CAS editorCas = documentService.readAnnotationCas(annotationDocument);
                            annotationService.upgradeCas(editorCas, annotationDocument);
                            if (cD.getCodebooks().get(u).isEmpty()) {
                                continue;
                            }
                            for (int h = 2; h < cD.getHeaders().size() - 1; h++) {
                                String annotation = cD.getCodebooks().get(u).get(h - 2);
                                String[] splits = cD.getHeaders().get(h).split("\\.");
                                String codebook = splits[0] + "." + splits[1] + "."
                                        + splits[splits.length - 1];
                                Type type = editorCas.getTypeSystem().getType(codebook);
                                Feature f = type.getFeatureByBaseName(
                                        CodebookConstants.CODEBOOK_FEATURE_NAME);
                                AnnotationFS fs = editorCas.createAnnotation(type, 0, 0);
                                fs.setFeatureValueFromString(f, annotation);
                                editorCas.addFsToIndexes(fs);

                            }
                            documentService.writeAnnotationCas(editorCas, annotationDocument,
                                    false);
                        }
                        catch (Exception e) {
                            error("Unable to create Annotation CAS for the user "
                                    + cD.getAnnotators().get(u) + " Cause: " + e.getMessage());
                        }
                    }

                }
                info("File [" + fileName + "] has been imported successfully!");

            }
            catch (IOException | UIMAException e) {
                error("Error while uploading document " + fileName + ": "
                        + ExceptionUtils.getRootCauseMessage(e));
                LOG.error(fileName + ": " + e.getMessage(), e);
            }
        }
    }

    private void readExcel(List<FileUpload> uploadedFiles, Project project,
            FormatSupport documentFormat)
    {
        for (FileUpload documentToUpload : uploadedFiles) {
            String fileName = documentToUpload.getClientFileName();
            try {
                List<CodebookAnnotationDocument> codebookDocuments = CodebookDocumentUtil
                        .readExcelData(documentToUpload);
                for (CodebookAnnotationDocument cD : codebookDocuments) {
                    SourceDocument document = new SourceDocument();
                    document.setName(cD.getDocumentName());
                    document.setProject(project);
                    document.setFormat(documentFormat.getId());
                    InputStream is = CodebookDocumentUtil.getExcelStream(cD);
                    documentService.uploadSourceDocument(is, document);
                }
                info("File [" + fileName + "] has been imported successfully!");

            }
            catch (IOException | UIMAException e) {
                error("Error while uploading document " + fileName + ": "
                        + ExceptionUtils.getRootCauseMessage(e));
                LOG.error(fileName + ": " + e.getMessage(), e);
            }
            catch (Exception e) {
                error("Error while uploading document " + fileName + ": "
                        + ExceptionUtils.getRootCauseMessage(e));
                LOG.error(fileName + ": " + e.getMessage(), e);
            }
        }
    }

}
