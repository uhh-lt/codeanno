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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;

import com.squareup.okhttp.Call;

import de.tudarmstadt.ukp.clarin.webanno.api.annotation.exception.AnnotationException;
import de.tudarmstadt.ukp.clarin.webanno.api.annotation.model.AnnotatorState;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.PredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

public interface CodebookAutomationService
{

    String SERVICE_NAME = "codebookAutomationService";

    String CBA_API_HOST_ENV_VAR = "CBA_API_HOST";
    String CBA_API_PORT_ENV_VAR = "CBA_API_PORT";

    boolean performHeartbeatCheck();

    void updateTagLabelMapping(Codebook cb, String tag, String label);

    boolean isPredictionInProgress(Codebook cb);

    Integer getPredictionInProgress(Codebook cb);

    Double getPredictionInProgressFraction(Codebook cb);

    void addToPredictionProgress(Codebook cb, SourceDocument sdoc);

    void removeFromPredictionProgress(PredictionResult result);

    boolean isAutomationAvailable(Codebook cb, boolean updateCache) throws ApiException;

    Call predictTagAsync(Codebook cb, Project proj, SourceDocument sdoc, String userName)
        throws ApiException;

    ModelMetadata getModelMetadata(Codebook cb) throws ApiException;

    void writePredictedTagToCorrectionCas(PredictionResult result, String userName)
        throws IOException, UIMAException, AnnotationException;

    CAS readOrCreateCorrectionCas(AnnotatorState state, boolean upgrade)
        throws IOException, UIMAException;
}
