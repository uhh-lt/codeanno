package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.service;

import de.tudarmstadt.ukp.clarin.webanno.api.casstorage.CasSessionException;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.casstorage.CasStorageSession;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiCallback;

public abstract class PersistResultToCasCallback<T> implements ApiCallback<T> {

    protected final CodebookAutomationService codebookAutomationService;
    protected final String userName;
    protected CasStorageSession casStorageSession;

    public PersistResultToCasCallback(
            CodebookAutomationService codebookAutomationService, String userName) {
        this.codebookAutomationService = codebookAutomationService;
        this.userName = userName;
    }

    public synchronized void initCasStorageSession()
    {
        try {
            if (this.casStorageSession == null)
                this.casStorageSession = CasStorageSession.open();
        }
        catch (CasSessionException e) {
            this.casStorageSession = CasStorageSession.get();
        }
    }

    public synchronized void closeCasStorageSession()
    {
        if (this.casStorageSession != null && !this.casStorageSession.isClosed())
            this.casStorageSession.close();

        this.casStorageSession = null;
    }
}
