/*
 * Copyright 2021
 * Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
 * and  Language Technology Universität Hamburg
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
package de.tudarmstadt.ukp.clarin.webanno.tsv;

import static de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.Escaping.escapeValue;
import static de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.model.TsvSchema.CHAIN_FIRST_FEAT;
import static de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.model.TsvSchema.CHAIN_NEXT_FEAT;
import static org.apache.commons.io.IOUtils.buffer;
import static org.apache.uima.fit.util.FSUtil.getFeature;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.dkpro.core.api.parameter.ComponentParameters;

import de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.Tsv3XCasSchemaAnalyzer;
import de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.model.LayerType;
import de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.model.TsvColumn;
import de.tudarmstadt.ukp.clarin.webanno.tsv.internal.tsv3x.model.TsvSchema;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

public class CodeAnnoHumanTsvWriter
    extends JCasFileWriter_ImplBase
{
    /**
     * The character encoding used by the input files.
     */
    public static final String PARAM_ENCODING = ComponentParameters.PARAM_TARGET_ENCODING;
    @ConfigurationParameter(name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
    private String encoding;

    /**
     * Use this filename extension.
     */
    public static final String PARAM_FILENAME_EXTENSION = ComponentParameters.PARAM_FILENAME_EXTENSION;
    @ConfigurationParameter(name = PARAM_FILENAME_EXTENSION, mandatory = true, defaultValue = ".tsv")
    private String filenameSuffix;

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException
    {
        TsvSchema schema = Tsv3XCasSchemaAnalyzer.analyze(aJCas.getTypeSystem());
        List<TsvColumn> cols = schema.getColumns();
        Map<Type, List<TsvColumn>> map = cols.stream()
                .collect(Collectors.groupingBy(TsvColumn::getUimaType));
        for (List<TsvColumn> list : map.values()) {
            List<TsvColumn> tmp = list.stream().filter(a -> a.uimaFeature != null)
                    .collect(Collectors.toList());
            list.clear();
            list.addAll(tmp);
        }

        final DocumentMetaData meta = DocumentMetaData.get(aJCas);
        final String fileName = meta.getDocumentTitle();

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(buffer(getOutputStream(aJCas, filenameSuffix)), encoding))) {

            int chainId = 0;
            for (Type headType : schema.getChainHeadTypes()) {
                for (FeatureStructure chainHead : aJCas.getCas().select(headType)) {
                    List<AnnotationFS> elements = new ArrayList<>();
                    AnnotationFS link = getFeature(chainHead, CHAIN_FIRST_FEAT, AnnotationFS.class);
                    while (link != null) {
                        elements.add(link);
                        link = getFeature(link, CHAIN_NEXT_FEAT, AnnotationFS.class);
                    }
                    if (!elements.isEmpty()) {
                        List<TsvColumn> chainCols = map.get(elements.get(0).getType());
                        chainId++;
                        Type elementType = headType.getFeatureByBaseName(CHAIN_FIRST_FEAT)
                                .getRange();
                        for (AnnotationFS a : elements)
                            writeAnnotation(writer, a, fileName, elementType.getShortName(),
                                    chainCols, chainId);
                    }
                }
            }

            for (Map.Entry<Type, List<TsvColumn>> entry : map.entrySet()) {
                if (entry.getValue().size() == 0
                        || entry.getValue().get(0).layerType == LayerType.CHAIN)
                    continue;
                Collection<AnnotationFS> annotations = CasUtil.select(aJCas.getCas(),
                        entry.getKey());
                if (annotations.isEmpty())
                    continue;
                String layer = entry.getKey().getShortName();
                for (AnnotationFS a : annotations)
                    writeAnnotation(writer, a, fileName, layer, entry.getValue(), 0);
            }
        }
        catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    private static void writeAnnotation(PrintWriter writer, AnnotationFS a, String docName,
            String layerName, List<TsvColumn> columns, int chainId)
    {
        writer.print(docName);
        writer.print('\t');
        writer.print(layerName);
        writer.print('\t');
        if (chainId != 0)
            writer.print(chainId);
        writer.print('\t');
        for (TsvColumn column : columns) {
            Object value = getFeature(a, column.uimaFeature, Object.class);
            if (value != null) {
                value = escapeValue(String.valueOf(value));
                writer.print(value);
            }
            writer.print('\t');
        }
        writer.print(a.getCoveredText());
        writer.print('\n');
    }
}
