package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component(PredictionFinishedEventListener.SERVICE_NAME)
public class PredictionFinishedEventListener
    implements ApplicationListener<ApplicationEvent>
{
    public static final String SERVICE_NAME = "predictionFinishedEventListener";

    @Override
    public void onApplicationEvent(ApplicationEvent aEvent)
    {
        if (aEvent instanceof PredictionFinishedEvent) {
            PredictionFinishedEvent event = (PredictionFinishedEvent) aEvent;
            // AutomationSettingsPanel.this.predictionInProgress = false;
        }
    }
}
