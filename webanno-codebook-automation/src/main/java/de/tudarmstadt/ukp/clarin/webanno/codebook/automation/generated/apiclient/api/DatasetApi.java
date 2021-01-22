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

import java.io.File;
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
import de.tudarmstadt.ukp.clarin.webanno.codebook.automation.generated.apiclient.model.DatasetMetadata;

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
     * Build call for getMetadataDatasetMetadataGet
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call getMetadataDatasetMetadataGetCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataset/metadata/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (datasetVersion != null)
            localVarQueryParams
                    .addAll(apiClient.parameterToPair("dataset_version", datasetVersion));

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
    private com.squareup.okhttp.Call getMetadataDatasetMetadataGetValidateBeforeCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling getMetadataDatasetMetadataGet(Async)");
        }
        // verify the required parameter 'datasetVersion' is set
        if (datasetVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'datasetVersion' when calling getMetadataDatasetMetadataGet(Async)");
        }

        com.squareup.okhttp.Call call = getMetadataDatasetMetadataGetCall(cbName, datasetVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Get Metadata
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return DatasetMetadata
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public DatasetMetadata getMetadataDatasetMetadataGet(String cbName, String datasetVersion)
        throws ApiException
    {
        ApiResponse<DatasetMetadata> resp = getMetadataDatasetMetadataGetWithHttpInfo(cbName,
                datasetVersion);
        return resp.getData();
    }

    /**
     * Get Metadata
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return ApiResponse&lt;DatasetMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<DatasetMetadata> getMetadataDatasetMetadataGetWithHttpInfo(String cbName,
            String datasetVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMetadataDatasetMetadataGetValidateBeforeCall(cbName,
                datasetVersion, null, null);
        Type localVarReturnType = new TypeToken<DatasetMetadata>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get Metadata (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call getMetadataDatasetMetadataGetAsync(String cbName,
            String datasetVersion, final ApiCallback<DatasetMetadata> callback)
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

        com.squareup.okhttp.Call call = getMetadataDatasetMetadataGetValidateBeforeCall(cbName,
                datasetVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<DatasetMetadata>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for isAvailableDatasetAvailableGet
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call isAvailableDatasetAvailableGetCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataset/available/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (datasetVersion != null)
            localVarQueryParams
                    .addAll(apiClient.parameterToPair("dataset_version", datasetVersion));

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
    private com.squareup.okhttp.Call isAvailableDatasetAvailableGetValidateBeforeCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling isAvailableDatasetAvailableGet(Async)");
        }
        // verify the required parameter 'datasetVersion' is set
        if (datasetVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'datasetVersion' when calling isAvailableDatasetAvailableGet(Async)");
        }

        com.squareup.okhttp.Call call = isAvailableDatasetAvailableGetCall(cbName, datasetVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Is Available
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse isAvailableDatasetAvailableGet(String cbName, String datasetVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = isAvailableDatasetAvailableGetWithHttpInfo(cbName,
                datasetVersion);
        return resp.getData();
    }

    /**
     * Is Available
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> isAvailableDatasetAvailableGetWithHttpInfo(String cbName,
            String datasetVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = isAvailableDatasetAvailableGetValidateBeforeCall(cbName,
                datasetVersion, null, null);
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
     * @param datasetVersion
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call isAvailableDatasetAvailableGetAsync(String cbName,
            String datasetVersion, final ApiCallback<BooleanResponse> callback)
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

        com.squareup.okhttp.Call call = isAvailableDatasetAvailableGetValidateBeforeCall(cbName,
                datasetVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for listDatasetsDatasetListGet
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
    public com.squareup.okhttp.Call listDatasetsDatasetListGetCall(String cbName,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataset/list/";

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
    private com.squareup.okhttp.Call listDatasetsDatasetListGetValidateBeforeCall(String cbName,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling listDatasetsDatasetListGet(Async)");
        }

        com.squareup.okhttp.Call call = listDatasetsDatasetListGetCall(cbName, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * List Datasets
     * 
     * @param cbName
     *            (required)
     * @return List&lt;DatasetMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public List<DatasetMetadata> listDatasetsDatasetListGet(String cbName) throws ApiException
    {
        ApiResponse<List<DatasetMetadata>> resp = listDatasetsDatasetListGetWithHttpInfo(cbName);
        return resp.getData();
    }

    /**
     * List Datasets
     * 
     * @param cbName
     *            (required)
     * @return ApiResponse&lt;List&lt;DatasetMetadata&gt;&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<List<DatasetMetadata>> listDatasetsDatasetListGetWithHttpInfo(String cbName)
        throws ApiException
    {
        com.squareup.okhttp.Call call = listDatasetsDatasetListGetValidateBeforeCall(cbName, null,
                null);
        Type localVarReturnType = new TypeToken<List<DatasetMetadata>>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * List Datasets (asynchronously)
     * 
     * @param cbName
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call listDatasetsDatasetListGetAsync(String cbName,
            final ApiCallback<List<DatasetMetadata>> callback)
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

        com.squareup.okhttp.Call call = listDatasetsDatasetListGetValidateBeforeCall(cbName,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<List<DatasetMetadata>>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for removeDatasetRemoveDelete
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param progressListener
     *            Progress listener
     * @param progressRequestListener
     *            Progress request listener
     * @return Call to execute
     * @throws ApiException
     *             If fail to serialize the request body object
     */
    public com.squareup.okhttp.Call removeDatasetRemoveDeleteCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/dataset/remove/";

        List<Pair> localVarQueryParams = new ArrayList<Pair>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<Pair>();
        if (cbName != null)
            localVarQueryParams.addAll(apiClient.parameterToPair("cb_name", cbName));
        if (datasetVersion != null)
            localVarQueryParams
                    .addAll(apiClient.parameterToPair("dataset_version", datasetVersion));

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
    private com.squareup.okhttp.Call removeDatasetRemoveDeleteValidateBeforeCall(String cbName,
            String datasetVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling removeDatasetRemoveDelete(Async)");
        }
        // verify the required parameter 'datasetVersion' is set
        if (datasetVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'datasetVersion' when calling removeDatasetRemoveDelete(Async)");
        }

        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteCall(cbName, datasetVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Remove
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return BooleanResponse
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public BooleanResponse removeDatasetRemoveDelete(String cbName, String datasetVersion)
        throws ApiException
    {
        ApiResponse<BooleanResponse> resp = removeDatasetRemoveDeleteWithHttpInfo(cbName,
                datasetVersion);
        return resp.getData();
    }

    /**
     * Remove
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @return ApiResponse&lt;BooleanResponse&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<BooleanResponse> removeDatasetRemoveDeleteWithHttpInfo(String cbName,
            String datasetVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteValidateBeforeCall(cbName,
                datasetVersion, null, null);
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
     * @param datasetVersion
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call removeDatasetRemoveDeleteAsync(String cbName,
            String datasetVersion, final ApiCallback<BooleanResponse> callback)
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

        com.squareup.okhttp.Call call = removeDatasetRemoveDeleteValidateBeforeCall(cbName,
                datasetVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<BooleanResponse>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for uploadDatasetUploadPut
     * 
     * @param cbName
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
    public com.squareup.okhttp.Call uploadDatasetUploadPutCall(String cbName, String datasetVersion,
            File datasetArchive, final ProgressResponseBody.ProgressListener progressListener,
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
        if (cbName != null)
            localVarFormParams.put("cb_name", cbName);
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
    private com.squareup.okhttp.Call uploadDatasetUploadPutValidateBeforeCall(String cbName,
            String datasetVersion, File datasetArchive,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling uploadDatasetUploadPut(Async)");
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

        com.squareup.okhttp.Call call = uploadDatasetUploadPutCall(cbName, datasetVersion,
                datasetArchive, progressListener, progressRequestListener);
        return call;

    }

    /**
     * Upload
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @return DatasetMetadata
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public DatasetMetadata uploadDatasetUploadPut(String cbName, String datasetVersion,
            File datasetArchive)
        throws ApiException
    {
        ApiResponse<DatasetMetadata> resp = uploadDatasetUploadPutWithHttpInfo(cbName,
                datasetVersion, datasetArchive);
        return resp.getData();
    }

    /**
     * Upload
     * 
     * @param cbName
     *            (required)
     * @param datasetVersion
     *            (required)
     * @param datasetArchive
     *            (required)
     * @return ApiResponse&lt;DatasetMetadata&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<DatasetMetadata> uploadDatasetUploadPutWithHttpInfo(String cbName,
            String datasetVersion, File datasetArchive)
        throws ApiException
    {
        com.squareup.okhttp.Call call = uploadDatasetUploadPutValidateBeforeCall(cbName,
                datasetVersion, datasetArchive, null, null);
        Type localVarReturnType = new TypeToken<DatasetMetadata>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Upload (asynchronously)
     * 
     * @param cbName
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
    public com.squareup.okhttp.Call uploadDatasetUploadPutAsync(String cbName,
            String datasetVersion, File datasetArchive, final ApiCallback<DatasetMetadata> callback)
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

        com.squareup.okhttp.Call call = uploadDatasetUploadPutValidateBeforeCall(cbName,
                datasetVersion, datasetArchive, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<DatasetMetadata>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
