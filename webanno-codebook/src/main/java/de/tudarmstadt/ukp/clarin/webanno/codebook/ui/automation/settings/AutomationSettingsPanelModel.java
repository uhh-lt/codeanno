package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.settings;


import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;

import java.io.Serializable;

class AutomationSettingsPanelModel implements Serializable {

    private static final long serialVersionUID = 5975706194753510777L;

    private boolean predictionInProgress;
    private String modelVersion;
    private ModelMetadata modelMetadata;
    private Project project;
    private Codebook codebook;

    public AutomationSettingsPanelModel() {
        this.resetData();
    }

    public AutomationSettingsPanelModel(Project project) {
        this.resetData();
        this.project = project;
        this.codebook = null;
    }

    public void resetData() {
        this.predictionInProgress = false;
        this.modelVersion = "default";
        this.modelMetadata = null;
    }

    public Codebook getCodebook() {
        return codebook;
    }

    public void setCodebook(Codebook codebook) {
        this.codebook = codebook;
    }

    public boolean isPredictionInProgress() {
        return predictionInProgress;
    }

    public void setPredictionInProgress(boolean predictionInProgress) {
        this.predictionInProgress = predictionInProgress;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public ModelMetadata getModelMetadata() {
        return modelMetadata;
    }

    public void setModelMetadata(
            ModelMetadata modelMetadata
    ) {
        this.modelMetadata = modelMetadata;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}