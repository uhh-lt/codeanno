package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation;

import java.io.Serializable;

import de.tudarmstadt.ukp.clarin.webanno.codebook.model.Codebook;
import de.tudarmstadt.ukp.clarin.webanno.model.SourceDocument;

public class CodebookAutomationSuggestion
    implements Serializable
{
    private static final long serialVersionUID = -6602347393267270679L;

    private final Codebook codebook;
    private SourceDocument document;
    private String value;
    private double confidence;

    public CodebookAutomationSuggestion(Codebook codebook, SourceDocument document, String value,
                                        double confidence)
    {
        this.codebook = codebook;
        this.document = document;
        this.value = value;
        this.confidence = confidence;
    }

    public Codebook getCodebook()
    {
        return codebook;
    }

    public String getValue()
    {
        return value;
    }

    public double getConfidence()
    {
        return confidence;
    }

    public SourceDocument getDocument() {
        return document;
    }
}
