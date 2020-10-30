package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.MultiDocumentPredictionResult;

public class PersistMultiPredResultToCasCallback
    extends PersistResultToCasCallback<MultiDocumentPredictionResult>
{

    private final static Logger logger = LoggerFactory
            .getLogger(PersistMultiPredResultToCasCallback.class);

    public PersistMultiPredResultToCasCallback(CodebookAutomationService codebookAutomationService,
            String userName)
    {
        super(codebookAutomationService, userName);
    }

    @Override
    public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders)
    {
        // TODO should we delete the existing Codebook Tag from the correction CAS? not really!
        logger.warn("Codebook Tag prediction FAILED! Reason: " + statusCode + " "
                + e.getResponseBody());

        // TODO remove prediction from progress list
        this.closeCasStorageSession();

    }

    @Override
    public void onSuccess(MultiDocumentPredictionResult result, int statusCode,
            Map<String, List<String>> responseHeaders)
    {
        try {
            // TODO save the predictions in the DB to get probabilities of all predictions
            String sb = "Successfully received Codebook Automation Results" + " for <"
                    + result.getCodebookName() + "> Codebook and Document with IDs=<"
                    + result.getPredictedTags().keySet().toString() + ">!";
            logger.info(sb);

            this.initCasStorageSession();

            codebookAutomationService.writePredictedTagsToCorrectionCas(result, userName);
        }
        finally {
            this.closeCasStorageSession();
        }
    }

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done)
    {

    }

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done)
    {

    }
}
