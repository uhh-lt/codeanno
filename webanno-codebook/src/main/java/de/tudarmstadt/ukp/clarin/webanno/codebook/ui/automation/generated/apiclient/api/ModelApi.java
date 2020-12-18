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

import java.io.File;
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
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.CodebookDTO;
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
     * Build call for getMetadataModelMetadataPost
     * 
     * @param body
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
    public com.squareup.okhttp.Call getMetadataModelMetadataPostCall(CodebookDTO body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/metadata/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
    private com.squareup.okhttp.Call getMetadataModelMetadataPostValidateBeforeCall(
            CodebookDTO body, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling getMetadataModelMetadataPost(Async)");
        }

        com.squareup.okhttp.Call call = getMetadataModelMetadataPostCall(body, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Get Metadata
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return ModelMetadata
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ModelMetadata getMetadataModelMetadataPost(CodebookDTO body, String modelVersion)
        throws ApiException
    {
        ApiResponse<ModelMetadata> resp = getMetadataModelMetadataPostWithHttpInfo(body,
                modelVersion);
        return resp.getData();
    }

    /**
     * Get Metadata
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return ApiResponse&lt;ModelMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<ModelMetadata> getMetadataModelMetadataPostWithHttpInfo(CodebookDTO body,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMetadataModelMetadataPostValidateBeforeCall(body,
                modelVersion, null, null);
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
     * @param modelVersion
     *            (optional, default to default)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getMetadataModelMetadataPostAsync(CodebookDTO body,
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

        com.squareup.okhttp.Call call = getMetadataModelMetadataPostValidateBeforeCall(body,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for isAvailableModelAvailablePost
     * 
     * @param body
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
    public com.squareup.okhttp.Call isAvailableModelAvailablePostCall(CodebookDTO body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/available/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
    private com.squareup.okhttp.Call isAvailableModelAvailablePostValidateBeforeCall(
            CodebookDTO body, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling isAvailableModelAvailablePost(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableModelAvailablePostCall(body, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Is Available
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse isAvailableModelAvailablePost(CodebookDTO body, String modelVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableModelAvailablePostWithHttpInfo(body,
                modelVersion);
        return resp.getData();
    }

    /**
     * Is Available
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> isAvailableModelAvailablePostWithHttpInfo(CodebookDTO body,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableModelAvailablePostValidateBeforeCall(body,
                modelVersion, null, null);
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
     * @param modelVersion
     *            (optional, default to default)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call isAvailableModelAvailablePostAsync(CodebookDTO body,
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

        com.squareup.okhttp.Call call = isAvailableModelAvailablePostValidateBeforeCall(body,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for removeModelRemoveDelete
     * 
     * @param body
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
    public com.squareup.okhttp.Call removeModelRemoveDeleteCall(CodebookDTO body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/remove/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (modelVersion != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("model_version", modelVersion));

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
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call removeModelRemoveDeleteValidateBeforeCall(CodebookDTO body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling removeModelRemoveDelete(Async)");
        }

        com.squareup.okhttp.Call call = removeModelRemoveDeleteCall(body, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Remove
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse removeModelRemoveDelete(CodebookDTO body, String modelVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = removeModelRemoveDeleteWithHttpInfo(body, modelVersion);
        return resp.getData();
    }

    /**
     * Remove
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> removeModelRemoveDeleteWithHttpInfo(CodebookDTO body,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = removeModelRemoveDeleteValidateBeforeCall(body,
                modelVersion, null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Remove (asynchronously)
     * 
     * @param body
     *            (required)
     * @param modelVersion
     *            (optional, default to default)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removeModelRemoveDeleteAsync(CodebookDTO body,
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

        com.squareup.okhttp.Call call = removeModelRemoveDeleteValidateBeforeCall(body,
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
     * @param codebookTagList
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
            String codebookTagList, String modelVersion, File modelArchive,
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
        if (codebookTagList != null)
            localVarFormParams.put("codebook_tag_list", codebookTagList);
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
            String codebookTagList, String modelVersion, File modelArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'codebookName' is set
        if (codebookName == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookName' when calling uploadModelUploadPut(Async)");
        }
        // verify the required parameter 'codebookTagList' is set
        if (codebookTagList == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookTagList' when calling uploadModelUploadPut(Async)");
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

        com.squareup.okhttp.Call call = uploadModelUploadPutCall(codebookName, codebookTagList,
                modelVersion, modelArchive, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param codebookTagList
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
    public StringResponse uploadModelUploadPut(String codebookName, String codebookTagList,
            String modelVersion, File modelArchive)
        throws ApiException
    {
        ApiResponse<StringResponse> resp = uploadModelUploadPutWithHttpInfo(codebookName,
                codebookTagList, modelVersion, modelArchive);
        return resp.getData();
    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param codebookTagList
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
            String codebookTagList, String modelVersion, File modelArchive)
        throws ApiException
    {
        com.squareup.okhttp.Call call = uploadModelUploadPutValidateBeforeCall(codebookName,
                codebookTagList, modelVersion, modelArchive, null, null);
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
     * @param codebookTagList
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
            String codebookTagList, String modelVersion, File modelArchive,
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

        com.squareup.okhttp.Call call = uploadModelUploadPutValidateBeforeCall(codebookName,
                codebookTagList, modelVersion, modelArchive, progressListener,
                progressRequestListener);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
