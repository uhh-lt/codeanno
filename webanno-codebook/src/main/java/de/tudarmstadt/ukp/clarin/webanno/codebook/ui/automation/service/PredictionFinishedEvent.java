package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import org.springframework.context.ApplicationEvent;

public class PredictionFinishedEvent
    extends ApplicationEvent
{

    private static final long serialVersionUID = -4924287447631696926L;
    private final String codebookUiName;

    public PredictionFinishedEvent(Object source, String cbUiName)
    {
        super(source);
        this.codebookUiName = cbUiName;
    }

    public String getCodebookUiName()
    {
        return codebookUiName;
    }
}
