/*
 * Copyright 2020
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
package de.uhh.lt.codeanno.automation;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;

import com.squareup.okhttp.Call;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiException;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.ModelMetadata;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.MultiDocumentPredictionRequest;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.MultiDocumentPredictionResult;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionRequest;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.PredictionResult;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.TagLabelMapping;
import de.uhh.lt.codeanno.model.Codebook;

public interface CodebookAutomationService
{

    String SERVICE_NAME = "codebookAutomationService";

    boolean performHeartbeatCheck();

    TagLabelMapping loadTagLabelMapping(Codebook cb, String modelVersion);

    void updateTagLabelMapping(Codebook cb, String modelVersion, String tag, String label)
        throws ApiException;

    void registerTagLabelMapping(TagLabelMapping mapping) throws ApiException;

    void unregisterTagLabelMapping(Codebook cb, String modelVersion) throws ApiException;

    boolean isPredictionInProgress(Codebook cb, Object caller);

    void addToPredictionInProgress(PredictionRequest req, Object caller);

    void addToPredictionInProgress(MultiDocumentPredictionRequest req, Object caller);

    void removeFromPredictionInProgress(PredictionResult result);

    void removeFromPredictionInProgress(MultiDocumentPredictionResult result);

    boolean isAutomationAvailable(Codebook cb, String modelVersion, boolean updateCache)
        throws ApiException;

    boolean isAutomationAvailable(Codebook cb, boolean updateCache) throws ApiException;

    PredictionResult predictTag(Codebook cb, Project proj, SourceDocument sdoc) throws ApiException;

    PredictionResult predictTag(Codebook cb, Project proj, SourceDocument sdoc, String modelVersion)
        throws ApiException;

    Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc) throws ApiException;

    Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc, String modelVersion,
            Object caller)
        throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj) throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj, String modelVersion) throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj, String modelVersion, Object caller)
        throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> docs) throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> docs, String modelVersion)
        throws ApiException;

    Call predictTagsAsync(Codebook cb, Project proj, List<SourceDocument> docs, String modelVersion,
            Object caller)
        throws ApiException;

    ModelMetadata getModelMetadata(Codebook cb) throws ApiException;

    ModelMetadata getModelMetadata(Codebook cb, String modelVersion) throws ApiException;

    List<ModelMetadata> getAvailableModels(Codebook cb) throws ApiException;

    void writePredictedTagToCorrectionCas(PredictionResult result)
        throws IOException, UIMAException, AnnotationException;

    void writePredictedTagsToCorrectionCas(MultiDocumentPredictionResult result);

    CAS readOrCreateCorrectionCas(SourceDocument sdoc, boolean upgrade)
        throws IOException, UIMAException;

    String readPredictedTagValueFromCorrectionCas(Codebook cb, SourceDocument sdoc)
        throws IOException, AnnotationException;
}
