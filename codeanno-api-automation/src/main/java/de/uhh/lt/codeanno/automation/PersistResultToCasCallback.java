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

import de.tudarmstadt.ukp.clarin.webanno.api.casstorage.CasSessionException;
import de.tudarmstadt.ukp.clarin.webanno.api.dao.casstorage.CasStorageSession;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiCallback;

public abstract class PersistResultToCasCallback<T>
    implements ApiCallback<T>
{

    protected final CodebookAutomationService codebookAutomationService;
    protected final String userName;
    protected CasStorageSession casStorageSession;

    public PersistResultToCasCallback(CodebookAutomationService codebookAutomationService,
            String userName)
    {
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
