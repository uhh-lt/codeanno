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
import de.tudarmstadt.ukp.clarin.webanno.codebook.ui.automation.generated.apiclient.model.DatasetRequest;

public class DatasetApi
{
    private ApiClient apiClient;

    public DatasetApi()
    {
        this(Configuration.getDefaultApiClient());
    }

    public DatasetApi(ApiClient apiClient)
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
     * Build call for isAvailableDatasetAvailablePost
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
    public com.squareup.okhttp.Call isAvailableDatasetAvailablePostCall(DatasetRequest body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/dataset/available/";

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
    private com.squareup.okhttp.Call isAvailableDatasetAvailablePostValidateBeforeCall(
            DatasetRequest body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling isAvailableDatasetAvailablePost(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableDatasetAvailablePostCall(body, progressListener,
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
    public BooleanResponse isAvailableDatasetAvailablePost(DatasetRequest body) throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableDatasetAvailablePostWithHttpInfo(body);
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
    public ApiResponse<BooleanResponse> isAvailableDatasetAvailablePostWithHttpInfo(
            DatasetRequest body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableDatasetAvailablePostValidateBeforeCall(body,
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
    public com.squareup.okhttp.Call isAvailableDatasetAvailablePostAsync(DatasetRequest body,
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

        com.squareup.okhttp.Call call = isAvailableDatasetAvailablePostValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for removeDatasetRemoveDelete
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
    public com.squareup.okhttp.Call removeDatasetRemoveDeleteCall(DatasetRequest body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/dataset/remove/";

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
        return apiClient.buildCall(localVarPath, "DELETE", localVarQueryParams,
                localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams,
                localVarFormParams, localVarAuthNames, progressRequestListener);
    }

    @SuppressWarnings("rawtypes")
    private com.squareup.okhttp.Call removeDatasetRemoveDeleteValidateBeforeCall(
            DatasetRequest body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling removeDatasetRemoveDelete(Async)");
        }

        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Remove
     * 
     * @param body
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse removeDatasetRemoveDelete(DatasetRequest body) throws ApiException
    {
        ApiResponse<BooleanResponse> resp = removeDatasetRemoveDeleteWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Remove
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> removeDatasetRemoveDeleteWithHttpInfo(DatasetRequest body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteValidateBeforeCall(body, null,
                null);
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
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removeDatasetRemoveDeleteAsync(DatasetRequest body,
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

        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for uploadDatasetUploadPut
     * 
     * @param codebookName
     *            (required)
     * @param codebookTagList
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call uploadDatasetUploadPutCall(String codebookName,
            String codebookTagList, String datasetVersion, File datasetArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataset/upload/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();

        Map<String, String> localVarHeaderParams = new HashMap<String, String>();

        Map<String, Object> localVarFormParams = new HashMap<String, Object>();
        if (codebookName != null)
            localVarFormParams.put("codebook_name", codebookName);
        if (codebookTagList != null)
            localVarFormParams.put("codebook_tag_list", codebookTagList);
        if (datasetVersion != null)
            localVarFormParams.put("dataset_version", datasetVersion);
        if (datasetArchive != null)
            localVarFormParams.put("dataset_archive", datasetArchive);

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
    private com.squareup.okhttp.Call uploadDatasetUploadPutValidateBeforeCall(String codebookName,
            String codebookTagList, String datasetVersion, File datasetArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'codebookName' is set
        if (codebookName == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookName' when calling uploadDatasetUploadPut(Async)");
        }
        // verify the required parameter 'codebookTagList' is set
        if (codebookTagList == null) {
            throw new ApiException(
                    "Missing the required parameter 'codebookTagList' when calling uploadDatasetUploadPut(Async)");
        }
        // verify the required parameter 'datasetVersion' is set
        if (datasetVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'datasetVersion' when calling uploadDatasetUploadPut(Async)");
        }
        // verify the required parameter 'datasetArchive' is set
        if (datasetArchive == null) {
            throw new ApiException(
                    "Missing the required parameter 'datasetArchive' when calling uploadDatasetUploadPut(Async)");
        }

        com.squareup.okhttp.Call call = uploadDatasetUploadPutCall(codebookName, codebookTagList,
                datasetVersion, datasetArchive, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param codebookTagList
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse uploadDatasetUploadPut(String codebookName, String codebookTagList,
            String datasetVersion, File datasetArchive)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = uploadDatasetUploadPutWithHttpInfo(codebookName,
                codebookTagList, datasetVersion, datasetArchive);
        return resp.getData();
    }

    /**
     * Upload
     * 
     * @param codebookName
     *            (required)
     * @param codebookTagList
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> uploadDatasetUploadPutWithHttpInfo(String codebookName,
            String codebookTagList, String datasetVersion, File datasetArchive)
        throws ApiException
    {
        com.squareup.okhttp.Call call = uploadDatasetUploadPutValidateBeforeCall(codebookName,
                codebookTagList, datasetVersion, datasetArchive, null, null);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
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
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call uploadDatasetUploadPutAsync(String codebookName,
            String codebookTagList, String datasetVersion, File datasetArchive,
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

        com.squareup.okhttp.Call call = uploadDatasetUploadPutValidateBeforeCall(codebookName,
                codebookTagList, datasetVersion, datasetArchive, progressListener,
                progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
