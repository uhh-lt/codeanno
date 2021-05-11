package de.tudarmstadt.ukp.clarin.webanno.tsv;

import de.tudarmstadt.ukp.clarin.webanno.api.format.FormatSupport;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.springframework.stereotype.Component;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

@Component
public class CodeAnnoHumanTsvFormatSupport implements FormatSupport {
    public static final String ID = "humantsv";
    public static final String NAME = "CodeAnno human-readable TSV";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public AnalysisEngineDescription getWriterDescription(Project aProject,
                                                          TypeSystemDescription aTSD, CAS aCAS)
            throws ResourceInitializationException {
        return createEngineDescription(CodeAnnoHumanTsvWriter.class, aTSD);
    }
}
