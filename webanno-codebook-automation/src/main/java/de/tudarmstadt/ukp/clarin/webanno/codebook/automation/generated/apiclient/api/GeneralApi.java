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
package de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ApiCallback;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ApiClient;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ApiResponse;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.Configuration;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.Pair;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ProgressRequestBody;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.ProgressResponseBody;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.BooleanResponse;

public class GeneralApi
{
    private ApiClient apiClient;

    public GeneralApi()
    {
        this(Configuration.getDefaultApiClient());
    }

    public GeneralApi(ApiClient apiClient)
    {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient()
    {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient)
    {
        this.apiClient = apiClient;
    }

    /**
     * Build call for heartbeatHeartbeatGet
     * 
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call heartbeatHeartbeatGetCall(
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/heartbeat";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null)
            localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if (progressListener != null) {
            apiClient.getHttpClient().networkInterceptors()
                    .add(new com.squareup.okhttp.Interceptor()
                    {
                        @Override
                        public com.squareup.okhttp.Response intercept(
                                com.squareup.okhttp.Interceptor.Chain chain)
                            throws IOException
                        {
                            com.squareup.okhttp.Response originalResponse = chain
                                    .proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new ProgressResponseBody(originalResponse.body(),
                                            progressListener))
                                    .build();
                        }
                    });
        }

        String[] localVarAuthNames = new String[] {};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call heartbeatHeartbeatGetValidateBeforeCall(
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {

        com.squareup.okhttp.Call call = heartbeatHeartbeatGetCall(progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Heartbeat
     * 
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse heartbeatHeartbeatGet() throws ApiException
    {
        ApiResponse<BooleanResponse> resp = heartbeatHeartbeatGetWithHttpInfo();
        return resp.getData();
    }

    /**
     * Heartbeat
     * 
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> heartbeatHeartbeatGetWithHttpInfo() throws ApiException
    {
        com.squareup.okhttp.Call call = heartbeatHeartbeatGetValidateBeforeCall(null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Heartbeat (asynchronously)
     * 
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call heartbeatHeartbeatGetAsync(
            final ApiCallback<BooleanResponse> callback)
        throws ApiException
    {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener()
            {
                @Override
                public void update(long bytesRead, long contentLength, boolean done)
                {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener()
            {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done)
                {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = heartbeatHeartbeatGetValidateBeforeCall(progressListener,
                progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for rootToDocsGet
     * 
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call rootToDocsGetCall(
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null)
            localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = {

        };
        final String localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        if (progressListener != null) {
            apiClient.getHttpClient().networkInterceptors()
                    .add(new com.squareup.okhttp.Interceptor()
                    {
                        @Override
                        public com.squareup.okhttp.Response intercept(
                                com.squareup.okhttp.Interceptor.Chain chain)
                            throws IOException
                        {
                            com.squareup.okhttp.Response originalResponse = chain
                                    .proceed(chain.request());
                            return originalResponse.newBuilder()
                                    .body(new ProgressResponseBody(originalResponse.body(),
                                            progressListener))
                                    .build();
                        }
                    });
        }

        String[] localVarAuthNames = new String[] {};
        return apiClient.buildCall(localVarPath, "GET", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call rootToDocsGetValidateBeforeCall(
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {

        com.squareup.okhttp.Call call = rootToDocsGetCall(progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Root To Docs Redirection to /docs
     * 
     * @return Object
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public Object rootToDocsGet() throws ApiException
    {
        ApiResponse<Object> resp = rootToDocsGetWithHttpInfo();
        return resp.getData();
    }

    /**
     * Root To Docs Redirection to /docs
     * 
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<Object> rootToDocsGetWithHttpInfo() throws ApiException
    {
        com.squareup.okhttp.Call call = rootToDocsGetValidateBeforeCall(null, null);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Root To Docs (asynchronously) Redirection to /docs
     * 
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call rootToDocsGetAsync(final ApiCallback<Object> callback)
        throws ApiException
    {

        ProgressResponseBody.ProgressListener progressListener = null;
        ProgressRequestBody.ProgressRequestListener progressRequestListener = null;

        if (callback != null) {
            progressListener = new ProgressResponseBody.ProgressListener()
            {
                @Override
                public void update(long bytesRead, long contentLength, boolean done)
                {
                    callback.onDownloadProgress(bytesRead, contentLength, done);
                }
            };

            progressRequestListener = new ProgressRequestBody.ProgressRequestListener()
            {
                @Override
                public void onRequestProgress(long bytesWritten, long contentLength, boolean done)
                {
                    callback.onUploadProgress(bytesWritten, contentLength, done);
                }
            };
        }

        com.squareup.okhttp.Call call = rootToDocsGetValidateBeforeCall(progressListener,
                progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
