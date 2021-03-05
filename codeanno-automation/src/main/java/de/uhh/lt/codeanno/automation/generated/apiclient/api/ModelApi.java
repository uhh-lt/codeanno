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
package de.uhh.lt.codeanno.automation.generated.apiclient.api;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.reflect.TypeToken;

import de.uhh.lt.codeanno.automation.generated.apiclient.ApiCallback;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiClient;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiException;
import de.uhh.lt.codeanno.automation.generated.apiclient.ApiResponse;
import de.uhh.lt.codeanno.automation.generated.apiclient.Configuration;
import de.uhh.lt.codeanno.automation.generated.apiclient.Pair;
import de.uhh.lt.codeanno.automation.generated.apiclient.ProgressRequestBody;
import de.uhh.lt.codeanno.automation.generated.apiclient.ProgressResponseBody;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.BooleanResponse;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.ModelMetadata;
import de.uhh.lt.codeanno.automation.generated.apiclient.model.StringResponse;

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
     * Build call for getMetadataModelMetadataGet
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getMetadataModelMetadataGetCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/metadata/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
    private com.squareup.okhttp.Call getMetadataModelMetadataGetValidateBeforeCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling getMetadataModelMetadataGet(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling getMetadataModelMetadataGet(Async)");
        }

        com.squareup.okhttp.Call call = getMetadataModelMetadataGetCall(cbName, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Get Metadata
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return ModelMetadata
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ModelMetadata getMetadataModelMetadataGet(String cbName, String modelVersion)
        throws ApiException
    {
        ApiResponse<ModelMetadata> resp = getMetadataModelMetadataGetWithHttpInfo(cbName,
                modelVersion);
        return resp.getData();
    }

    /**
     * Get Metadata
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return ApiResponse&lt;ModelMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<ModelMetadata> getMetadataModelMetadataGetWithHttpInfo(String cbName,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMetadataModelMetadataGetValidateBeforeCall(cbName,
                modelVersion, null, null);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get Metadata (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getMetadataModelMetadataGetAsync(String cbName,
            String modelVersion, final ApiCallback<ModelMetadata> callback)
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

        com.squareup.okhttp.Call call = getMetadataModelMetadataGetValidateBeforeCall(cbName,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for isAvailableModelAvailableGet
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call isAvailableModelAvailableGetCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/available/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
    private com.squareup.okhttp.Call isAvailableModelAvailableGetValidateBeforeCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling isAvailableModelAvailableGet(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling isAvailableModelAvailableGet(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableModelAvailableGetCall(cbName, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Is Available
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse isAvailableModelAvailableGet(String cbName, String modelVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableModelAvailableGetWithHttpInfo(cbName,
                modelVersion);
        return resp.getData();
    }

    /**
     * Is Available
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> isAvailableModelAvailableGetWithHttpInfo(String cbName,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableModelAvailableGetValidateBeforeCall(cbName,
                modelVersion, null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Is Available (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call isAvailableModelAvailableGetAsync(String cbName,
            String modelVersion, final ApiCallback<BooleanResponse> callback)
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

        com.squareup.okhttp.Call call = isAvailableModelAvailableGetValidateBeforeCall(cbName,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for listModelsModelListGet
     * 
     * @param cbName
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call listModelsModelListGetCall(String cbName,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/list/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));

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
    private com.squareup.okhttp.Call listModelsModelListGetValidateBeforeCall(String cbName,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling listModelsModelListGet(Async)");
        }

        com.squareup.okhttp.Call call = listModelsModelListGetCall(cbName, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * List Models
     * 
     * @param cbName
     *            (required)
     * @return List&lt;ModelMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public List<ModelMetadata> listModelsModelListGet(String cbName) throws ApiException
    {
        ApiResponse<List<ModelMetadata>> resp = listModelsModelListGetWithHttpInfo(cbName);
        return resp.getData();
    }

    /**
     * List Models
     * 
     * @param cbName
     *            (required)
     * @return ApiResponse&lt;List&lt;ModelMetadata&gt;&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<List<ModelMetadata>> listModelsModelListGetWithHttpInfo(String cbName)
        throws ApiException
    {
        com.squareup.okhttp.Call call = listModelsModelListGetValidateBeforeCall(cbName, null,
                null);
        Type localVarReturnType = new TypeToken<List<ModelMetadata>>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * List Models (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call listModelsModelListGetAsync(String cbName,
            final ApiCallback<List<ModelMetadata>> callback)
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

        com.squareup.okhttp.Call call = listModelsModelListGetValidateBeforeCall(cbName,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<ModelMetadata>>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for removeModelRemoveDelete
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call removeModelRemoveDeleteCall(String cbName, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/remove/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call removeModelRemoveDeleteValidateBeforeCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling removeModelRemoveDelete(Async)");
        }

        com.squareup.okhttp.Call call = removeModelRemoveDeleteCall(cbName, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Remove
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse removeModelRemoveDelete(String cbName, String modelVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = removeModelRemoveDeleteWithHttpInfo(cbName,
                modelVersion);
        return resp.getData();
    }

    /**
     * Remove
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> removeModelRemoveDeleteWithHttpInfo(String cbName,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = removeModelRemoveDeleteValidateBeforeCall(cbName,
                modelVersion, null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Remove (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removeModelRemoveDeleteAsync(String cbName, String modelVersion,
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

        com.squareup.okhttp.Call call = removeModelRemoveDeleteValidateBeforeCall(cbName,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for uploadModelUploadPut
     * 
     * @param codebookName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param modelArchive
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call uploadModelUploadPutCall(String codebookName,
            String modelVersion, File modelArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/upload/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();
        if (codebookName != null)
            localVarFormParams.put("codebook_name", codebookName);
        if (modelVersion != null)
            localVarFormParams.put("model_version", modelVersion);
        if (modelArchive != null)
            localVarFormParams.put("model_archive", modelArchive);

        final String[] localVarAccepts = { "application/json" };
        final String localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null)
            localVarHeaderParams.put("Accept", localVarAccept);

        final String[] localVarContentTypes = { "multipart/form-data" };
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
    private com.squareup.okhttp.Call uploadModelUploadPutValidateBeforeCall(String codebookName,
            String modelVersion, File modelArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'codebookName' is set
        if (codebookName == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookName' when calling uploadModelUploadPut(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling uploadModelUploadPut(Async)");
        }
        // verify the required parameter 'modelArchive' is set
        if (modelArchive == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelArchive' when calling uploadModelUploadPut(Async)");
        }

        com.squareup.okhttp.Call call = uploadModelUploadPutCall(codebookName, modelVersion,
                modelArchive, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param modelArchive
     *            (required)
     * @return StringResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public StringResponse uploadModelUploadPut(String codebookName, String modelVersion,
            File modelArchive)
        throws ApiException
    {
        ApiResponse<StringResponse> resp = uploadModelUploadPutWithHttpInfo(codebookName,
                modelVersion, modelArchive);
        return resp.getData();
    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param modelArchive
     *            (required)
     * @return ApiResponse&lt;StringResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<StringResponse> uploadModelUploadPutWithHttpInfo(String codebookName,
            String modelVersion, File modelArchive)
        throws ApiException
    {
        com.squareup.okhttp.Call call = uploadModelUploadPutValidateBeforeCall(codebookName,
                modelVersion, modelArchive, null, null);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Upload (asynchronously)
     * 
     * @param codebookName
     *            (required)
     * @param modelVersion
     *            (required)
     * @param modelArchive
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call uploadModelUploadPutAsync(String codebookName,
            String modelVersion, File modelArchive, final ApiCallback<StringResponse> callback)
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

        com.squareup.okhttp.Call call = uploadModelUploadPutValidateBeforeCall(codebookName,
                modelVersion, modelArchive, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
