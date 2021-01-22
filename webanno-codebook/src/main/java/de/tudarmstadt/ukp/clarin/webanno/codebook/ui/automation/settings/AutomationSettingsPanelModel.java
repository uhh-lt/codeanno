/*
 * Copyright 2020
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
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
package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.settings;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;

class AutomationSettingsPanelModel
    implements Serializable
{

    private static final long serialVersionUID = 5975706194753510777L;

    private boolean predictionInProgress;
    private String modelVersion;
    private Map<String, ModelMetadata> availableModels;
    private Project project;
    private Codebook codebook;

    public AutomationSettingsPanelModel()
    {
        this.resetData();
    }

    public AutomationSettingsPanelModel(Project project)
    {
        this.resetData();
        this.project = project;
        this.codebook = null;
    }

    public void resetData()
    {
        this.predictionInProgress = false;
        this.modelVersion = "";
        this.availableModels = Collections.emptyMap();
    }

    public Codebook getCodebook()
    {
        return codebook;
    }

    public void setCodebook(Codebook codebook)
    {
        this.codebook = codebook;
    }

    public boolean isPredictionInProgress()
    {
        return predictionInProgress;
    }

    public void setPredictionInProgress(boolean predictionInProgress)
    {
        this.predictionInProgress = predictionInProgress;
    }

    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    public ModelMetadata getModelMetadata()
    {
        return this.availableModels.get(modelVersion);
    }

    public Project getProject()
    {
        return project;
    }

    public void setProject(Project project)
    {
        this.project = project;
    }

    public void setAvailableModels(List<ModelMetadata> availableModels)
    {
        this.availableModels = availableModels.stream()
                .collect(Collectors.toMap(ModelMetadata::getVersion, md -> md));
    }

    public Map<String, ModelMetadata> getAvailableModels()
    {
        return availableModels;
    }
}
