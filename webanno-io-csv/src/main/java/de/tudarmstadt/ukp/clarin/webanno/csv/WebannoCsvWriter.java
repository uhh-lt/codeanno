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
package de.tudarmstadt.ukp.clarin.webanno.csv;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.dkpro.core.api.parameter.ComponentParameters;

public class WebannoCsvWriter
    extends JCasFileWriter_ImplBase
{
    public static final String PARAM_ENCODING = ComponentParameters.PARAM_TARGET_ENCODING;
    @ConfigurationParameter(name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
    private String encoding;

    public static final String PARAM_WITH_HEADERS = "withHeaders";
    @ConfigurationParameter(name = PARAM_WITH_HEADERS, mandatory = true, defaultValue = "true")
    private boolean withHeaders;

    public static final String PARAM_WITH_TEXT = "withText";
    @ConfigurationParameter(name = PARAM_WITH_TEXT, mandatory = true, defaultValue = "true")
    private boolean withText;

    public static final String PARAM_FILENAME = "filename";
    @ConfigurationParameter(name = PARAM_FILENAME, mandatory = true, defaultValue = "codebooks")
    private String filename;

    public static final String PARAM_DOCUMENT_NAME = "documentName";
    @ConfigurationParameter(name = PARAM_DOCUMENT_NAME, mandatory = true,
                            defaultValue = "defaultDocName.txt")
    private String documentName;

    public static final String PARAM_ANNOTATOR = "annotator";
    @ConfigurationParameter(name = PARAM_ANNOTATOR,
                            mandatory = true,
                            defaultValue = "defaultAnnotator")
    private String annotator;

    private static final String NEW_LINE_SEPARATOR = "\n";

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException
    {
        OutputStreamWriter writer = null;
        CSVPrinter csvFileWriter = null;
        try {
            CSVFormat csvFileFormat = CSVFormat.RFC4180.withRecordSeparator(NEW_LINE_SEPARATOR);
            File file = new File(
                    this.getTargetLocation() + File.separator + new File(filename).getName());
            // smy: No We do not delete it here, the caller should take care of cleaning tmp
            // files,or we do not need at all.
            // Files.deleteIfExists(file.toPath()); // TODO delete the file if its already
            // there?!
            file.getParentFile().mkdirs();
            writer = new OutputStreamWriter(new FileOutputStream(file, true), encoding);
            csvFileWriter = new CSVPrinter(writer, csvFileFormat);
            writeCsv(aJCas, csvFileWriter);
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
        finally {
            try {
                csvFileWriter.flush();
                csvFileWriter.close();
            }
            catch (IOException e) {
                throw new AnalysisEngineProcessException(e);
            }
            closeQuietly(writer);
        }

    }

    private void writeCsv(JCas aJCas, CSVPrinter aCsvFilePrinter)
        throws IOException, CASRuntimeException
    {

        Set<Type> codebookTypes = new LinkedHashSet<>();
        List<String> headers = new ArrayList<>();

        headers.add(PARAM_DOCUMENT_NAME);
        headers.add(PARAM_ANNOTATOR);

        // find codebook types
        aJCas.getTypeSystem().getTypeIterator().forEachRemaining(type -> {
            if (type.getName().startsWith("webanno.codebook")) {
                codebookTypes.add(type);
                headers.add(type.getName());
            }
        });

        if (codebookTypes.isEmpty()) {
            return;
        }
        if (withText) {
            headers.add("Text");
        }

        // write headers
        aCsvFilePrinter.printRecord(headers.toArray());

        List<String> cellValues = new ArrayList<>();
        cellValues.add(documentName);
        cellValues.add(annotator);

        for (Type codebookType : codebookTypes) {
            for (Feature feature : codebookType.getFeatures()) {
                if (feature.getName().equals("uima.cas.AnnotationBase:sofa")
                        || feature.getName().equals("uima.tcas.Annotation:begin")
                        || feature.getName().equals("uima.tcas.Annotation:end")) {
                    continue;
                }
                if (CasUtil.select(aJCas.getCas(), codebookType).isEmpty()) {
                    cellValues.add("");
                }
                else {
                    cellValues.add(CasUtil.select(aJCas.getCas(), codebookType).iterator().next()
                            .getFeatureValueAsString(feature));
                }
            }
        }
        if (withText) {
            cellValues.add(aJCas.getDocumentText());
        }
        aCsvFilePrinter.printRecord(cellValues);
    }

}
