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

package de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiCallback;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiClient;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiException;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ApiResponse;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.Configuration;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.Pair;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ProgressRequestBody;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.ProgressResponseBody;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.BooleanResponse;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.CodebookModel;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.ModelMetadata;
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.StringResponse;

public class ModelApi
{
    private ApiClient apiClient;

    public ModelApi()
    {
        this(Configuration.getDefaultApiClient());
    }

    public ModelApi(ApiClient apiClient)
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
     * Build call for computeModelIdModelComputeModelIdPost
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
    public com.squareup.okhttp.Call computeModelIdModelComputeModelIdPostCall(CodebookModel body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/compute_model_id/";

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
    private com.squareup.okhttp.Call computeModelIdModelComputeModelIdPostValidateBeforeCall(
            CodebookModel body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling computeModelIdModelComputeModelIdPost(Async)");
        }

        com.squareup.okhttp.Call call = computeModelIdModelComputeModelIdPostCall(body,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Compute Model Id
     * 
     * @param body
     *            (required)
     * @return StringResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public StringResponse computeModelIdModelComputeModelIdPost(CodebookModel body)
        throws ApiException
    {
        ApiResponse<StringResponse> resp = computeModelIdModelComputeModelIdPostWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Compute Model Id
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;StringResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<StringResponse> computeModelIdModelComputeModelIdPostWithHttpInfo(
            CodebookModel body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = computeModelIdModelComputeModelIdPostValidateBeforeCall(
                body, null, null);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Compute Model Id (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call computeModelIdModelComputeModelIdPostAsync(CodebookModel body,
            final ApiCallback<StringResponse> callback)
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

        com.squareup.okhttp.Call call = computeModelIdModelComputeModelIdPostValidateBeforeCall(
                body, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for getMetadataModelGetMetadataPost
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
    public com.squareup.okhttp.Call getMetadataModelGetMetadataPostCall(CodebookModel body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/get_metadata/";

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
    private com.squareup.okhttp.Call getMetadataModelGetMetadataPostValidateBeforeCall(
            CodebookModel body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling getMetadataModelGetMetadataPost(Async)");
        }

        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Get Metadata
     * 
     * @param body
     *            (required)
     * @return ModelMetadata
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ModelMetadata getMetadataModelGetMetadataPost(CodebookModel body) throws ApiException
    {
        ApiResponse<ModelMetadata> resp = getMetadataModelGetMetadataPostWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Get Metadata
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;ModelMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<ModelMetadata> getMetadataModelGetMetadataPostWithHttpInfo(
            CodebookModel body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostValidateBeforeCall(body,
                null, null);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get Metadata (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getMetadataModelGetMetadataPostAsync(CodebookModel body,
            final ApiCallback<ModelMetadata> callback)
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

        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for initModelModelInitModelPut
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
    public com.squareup.okhttp.Call initModelModelInitModelPutCall(CodebookModel body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/init_model/";

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
        return apiClient.buildCall(localVarPath, "PUT", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call initModelModelInitModelPutValidateBeforeCall(
            CodebookModel body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling initModelModelInitModelPut(Async)");
        }

        com.squareup.okhttp.Call call = initModelModelInitModelPutCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Init Model
     * 
     * @param body
     *            (required)
     * @return StringResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public StringResponse initModelModelInitModelPut(CodebookModel body) throws ApiException
    {
        ApiResponse<StringResponse> resp = initModelModelInitModelPutWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Init Model
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;StringResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<StringResponse> initModelModelInitModelPutWithHttpInfo(CodebookModel body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = initModelModelInitModelPutValidateBeforeCall(body, null,
                null);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Init Model (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call initModelModelInitModelPutAsync(CodebookModel body,
            final ApiCallback<StringResponse> callback)
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

        com.squareup.okhttp.Call call = initModelModelInitModelPutValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for isAvailableModelIsAvailablePost
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
    public com.squareup.okhttp.Call isAvailableModelIsAvailablePostCall(CodebookModel body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/is_available/";

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
    private com.squareup.okhttp.Call isAvailableModelIsAvailablePostValidateBeforeCall(
            CodebookModel body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling isAvailableModelIsAvailablePost(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Is Available
     * 
     * @param body
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse isAvailableModelIsAvailablePost(CodebookModel body) throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableModelIsAvailablePostWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Is Available
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> isAvailableModelIsAvailablePostWithHttpInfo(
            CodebookModel body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostValidateBeforeCall(body,
                null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Is Available (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call isAvailableModelIsAvailablePostAsync(CodebookModel body,
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

        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
