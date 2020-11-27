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
     * Build call for getMetadataModelGetMetadataPost
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
    public com.squareup.okhttp.Call getMetadataModelGetMetadataPostCall(CodebookModel body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/get_metadata/";

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
    private com.squareup.okhttp.Call getMetadataModelGetMetadataPostValidateBeforeCall(
            CodebookModel body, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling getMetadataModelGetMetadataPost(Async)");
        }

        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostCall(body, modelVersion,
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
    public ModelMetadata getMetadataModelGetMetadataPost(CodebookModel body, String modelVersion)
        throws ApiException
    {
        ApiResponse<ModelMetadata> resp = getMetadataModelGetMetadataPostWithHttpInfo(body,
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
    public ApiResponse<ModelMetadata> getMetadataModelGetMetadataPostWithHttpInfo(
            CodebookModel body, String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostValidateBeforeCall(body,
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
    public com.squareup.okhttp.Call getMetadataModelGetMetadataPostAsync(CodebookModel body,
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

        com.squareup.okhttp.Call call = getMetadataModelGetMetadataPostValidateBeforeCall(body,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<ModelMetadata>()
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
    public com.squareup.okhttp.Call isAvailableModelIsAvailablePostCall(CodebookModel body,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/model/is_available/";

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
    private com.squareup.okhttp.Call isAvailableModelIsAvailablePostValidateBeforeCall(
            CodebookModel body, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling isAvailableModelIsAvailablePost(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostCall(body, modelVersion,
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
    public BooleanResponse isAvailableModelIsAvailablePost(CodebookModel body, String modelVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableModelIsAvailablePostWithHttpInfo(body,
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
    public ApiResponse<BooleanResponse> isAvailableModelIsAvailablePostWithHttpInfo(
            CodebookModel body, String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostValidateBeforeCall(body,
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
    public com.squareup.okhttp.Call isAvailableModelIsAvailablePostAsync(CodebookModel body,
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

        com.squareup.okhttp.Call call = isAvailableModelIsAvailablePostValidateBeforeCall(body,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for uploadForCodebookModelUploadForCodebookPut
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
    public com.squareup.okhttp.Call uploadForCodebookModelUploadForCodebookPutCall(
            String codebookName, String codebookTagList, String modelVersion, File modelArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/model/upload_for_codebook/";

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
    private com.squareup.okhttp.Call uploadForCodebookModelUploadForCodebookPutValidateBeforeCall(
            String codebookName, String codebookTagList, String modelVersion, File modelArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'codebookName' is set
        if (codebookName == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookName' when calling uploadForCodebookModelUploadForCodebookPut(Async)");
        }
        // verify the required parameter 'codebookTagList' is set
        if (codebookTagList == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookTagList' when calling uploadForCodebookModelUploadForCodebookPut(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling uploadForCodebookModelUploadForCodebookPut(Async)");
        }
        // verify the required parameter 'modelArchive' is set
        if (modelArchive == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelArchive' when calling uploadForCodebookModelUploadForCodebookPut(Async)");
        }

        com.squareup.okhttp.Call call = uploadForCodebookModelUploadForCodebookPutCall(codebookName,
                codebookTagList, modelVersion, modelArchive, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Upload For Codebook
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
    public StringResponse uploadForCodebookModelUploadForCodebookPut(String codebookName,
            String codebookTagList, String modelVersion, File modelArchive)
        throws ApiException
    {
        ApiResponse<StringResponse> resp = uploadForCodebookModelUploadForCodebookPutWithHttpInfo(
                codebookName, codebookTagList, modelVersion, modelArchive);
        return resp.getData();
    }

    /**
     * Upload For Codebook
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
    public ApiResponse<StringResponse> uploadForCodebookModelUploadForCodebookPutWithHttpInfo(
            String codebookName, String codebookTagList, String modelVersion, File modelArchive)
        throws ApiException
    {
        com.squareup.okhttp.Call call =
                uploadForCodebookModelUploadForCodebookPutValidateBeforeCall(
                codebookName, codebookTagList, modelVersion, modelArchive, null, null);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Upload For Codebook (asynchronously)
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
    public com.squareup.okhttp.Call uploadForCodebookModelUploadForCodebookPutAsync(
            String codebookName, String codebookTagList, String modelVersion, File modelArchive,
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

        com.squareup.okhttp.Call call =
                uploadForCodebookModelUploadForCodebookPutValidateBeforeCall(
                codebookName, codebookTagList, modelVersion, modelArchive, progressListener,
                progressRequestListener);
        Type localVarReturnType = new TypeToken<StringResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
