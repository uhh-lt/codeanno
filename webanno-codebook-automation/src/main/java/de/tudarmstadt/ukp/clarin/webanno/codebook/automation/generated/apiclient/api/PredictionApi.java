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
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.MultiDocumentPredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.MultiDocumentPredictionResult;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.PredictionRequest;
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.PredictionResult;

public class PredictionApi
{
    private ApiClient apiClient;

    public PredictionApi()
    {
        this(Configuration.getDefaultApiClient());
    }

    public PredictionApi(ApiClient apiClient)
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
     * Build call for predictMultiPredictionMultiplePost
     * 
     * @param body
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call predictMultiPredictionMultiplePostCall(
            MultiDocumentPredictionRequest body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/prediction/multiple";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null)
            localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = { "application/json" };
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
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call predictMultiPredictionMultiplePostValidateBeforeCall(
            MultiDocumentPredictionRequest body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling predictMultiPredictionMultiplePost(Async)");
        }

        com.squareup.okhttp.Call call = predictMultiPredictionMultiplePostCall(body,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Predict Multi
     * 
     * @param body
     *            (required)
     * @return MultiDocumentPredictionResult
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public MultiDocumentPredictionResult predictMultiPredictionMultiplePost(
            MultiDocumentPredictionRequest body)
        throws ApiException
    {
        ApiResponse<MultiDocumentPredictionResult> resp = predictMultiPredictionMultiplePostWithHttpInfo(
                body);
        return resp.getData();
    }

    /**
     * Predict Multi
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;MultiDocumentPredictionResult&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<MultiDocumentPredictionResult> predictMultiPredictionMultiplePostWithHttpInfo(
            MultiDocumentPredictionRequest body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = predictMultiPredictionMultiplePostValidateBeforeCall(body,
                null, null);
        Type localVarReturnType = new TypeToken<MultiDocumentPredictionResult>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Predict Multi (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call predictMultiPredictionMultiplePostAsync(
            MultiDocumentPredictionRequest body,
            final ApiCallback<MultiDocumentPredictionResult> callback)
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

        com.squareup.okhttp.Call call = predictMultiPredictionMultiplePostValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<MultiDocumentPredictionResult>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for predictPredictionSinglePost
     * 
     * @param body
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call predictPredictionSinglePostCall(PredictionRequest body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/prediction/single";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null)
            localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = { "application/json" };
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
        return apiClient.buildCall(localVarPath, "POST", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call predictPredictionSinglePostValidateBeforeCall(
            PredictionRequest body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling predictPredictionSinglePost(Async)");
        }

        com.squareup.okhttp.Call call = predictPredictionSinglePostCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Predict
     * 
     * @param body
     *            (required)
     * @return PredictionResult
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public PredictionResult predictPredictionSinglePost(PredictionRequest body) throws ApiException
    {
        ApiResponse<PredictionResult> resp = predictPredictionSinglePostWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Predict
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;PredictionResult&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<PredictionResult> predictPredictionSinglePostWithHttpInfo(
            PredictionRequest body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = predictPredictionSinglePostValidateBeforeCall(body, null,
                null);
        Type localVarReturnType = new TypeToken<PredictionResult>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Predict (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call predictPredictionSinglePostAsync(PredictionRequest body,
            final ApiCallback<PredictionResult> callback)
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

        com.squareup.okhttp.Call call = predictPredictionSinglePostValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<PredictionResult>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
