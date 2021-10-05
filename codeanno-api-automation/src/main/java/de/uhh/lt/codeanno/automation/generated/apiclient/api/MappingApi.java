/*
 * Copyright 2021
 * Language Technology (LT) Universität Hamburg
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
import de.uhh.lt.codeanno.automation.generated.apiclient.model.TagLabelMapping;

public class MappingApi
{
    private ApiClient apiClient;

    public MappingApi()
    {
        this(Configuration.getDefaultApiClient());
    }

    public MappingApi(ApiClient apiClient)
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
     * Build call for getMappingGetGet
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
    public com.squareup.okhttp.Call getMappingGetGetCall(String cbName, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/mapping/get/";

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
    private com.squareup.okhttp.Call getMappingGetGetValidateBeforeCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling getMappingGetGet(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling getMappingGetGet(Async)");
        }

        com.squareup.okhttp.Call call = getMappingGetGetCall(cbName, modelVersion, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Get
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return TagLabelMapping
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public TagLabelMapping getMappingGetGet(String cbName, String modelVersion) throws ApiException
    {
        ApiResponse<TagLabelMapping> resp = getMappingGetGetWithHttpInfo(cbName, modelVersion);
        return resp.getData();
    }

    /**
     * Get
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return ApiResponse&lt;TagLabelMapping&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<TagLabelMapping> getMappingGetGetWithHttpInfo(String cbName,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = getMappingGetGetValidateBeforeCall(cbName, modelVersion,
                null, null);
        Type localVarReturnType = new TypeToken<TagLabelMapping>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Get (asynchronously)
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
    public com.squareup.okhttp.Call getMappingGetGetAsync(String cbName, String modelVersion,
            final ApiCallback<TagLabelMapping> callback)
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

        com.squareup.okhttp.Call call = getMappingGetGetValidateBeforeCall(cbName, modelVersion,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<TagLabelMapping>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for registerMappingRegisterPut
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
    public com.squareup.okhttp.Call registerMappingRegisterPutCall(TagLabelMapping body,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/mapping/register/";

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
    private com.squareup.okhttp.Call registerMappingRegisterPutValidateBeforeCall(
            TagLabelMapping body, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling registerMappingRegisterPut(Async)");
        }

        com.squareup.okhttp.Call call = registerMappingRegisterPutCall(body, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Register
     * 
     * @param body
     *            (required)
     * @return Object
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public Object registerMappingRegisterPut(TagLabelMapping body) throws ApiException
    {
        ApiResponse<Object> resp = registerMappingRegisterPutWithHttpInfo(body);
        return resp.getData();
    }

    /**
     * Register
     * 
     * @param body
     *            (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<Object> registerMappingRegisterPutWithHttpInfo(TagLabelMapping body)
        throws ApiException
    {
        com.squareup.okhttp.Call call = registerMappingRegisterPutValidateBeforeCall(body, null,
                null);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Register (asynchronously)
     * 
     * @param body
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call registerMappingRegisterPutAsync(TagLabelMapping body,
            final ApiCallback<Object> callback)
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

        com.squareup.okhttp.Call call = registerMappingRegisterPutValidateBeforeCall(body,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for unregisterMappingUnregisterDelete
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
    public com.squareup.okhttp.Call unregisterMappingUnregisterDeleteCall(String cbName,
            String modelVersion, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/mapping/unregister/";

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
    private com.squareup.okhttp.Call unregisterMappingUnregisterDeleteValidateBeforeCall(
            String cbName, String modelVersion,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling unregisterMappingUnregisterDelete(Async)");
        }
        // verify the required parameter 'modelVersion' is set
        if (modelVersion == null) {
            throw new ApiException(
                    "Missing the required parameter 'modelVersion' when calling unregisterMappingUnregisterDelete(Async)");
        }

        com.squareup.okhttp.Call call = unregisterMappingUnregisterDeleteCall(cbName, modelVersion,
                progressListener, progressRequestListener);
        return call;

    }

    /**
     * Unregister
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return Object
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public Object unregisterMappingUnregisterDelete(String cbName, String modelVersion)
        throws ApiException
    {
        ApiResponse<Object> resp = unregisterMappingUnregisterDeleteWithHttpInfo(cbName,
                modelVersion);
        return resp.getData();
    }

    /**
     * Unregister
     * 
     * @param cbName
     *            (required)
     * @param modelVersion
     *            (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<Object> unregisterMappingUnregisterDeleteWithHttpInfo(String cbName,
            String modelVersion)
        throws ApiException
    {
        com.squareup.okhttp.Call call = unregisterMappingUnregisterDeleteValidateBeforeCall(cbName,
                modelVersion, null, null);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Unregister (asynchronously)
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
    public com.squareup.okhttp.Call unregisterMappingUnregisterDeleteAsync(String cbName,
            String modelVersion, final ApiCallback<Object> callback)
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

        com.squareup.okhttp.Call call = unregisterMappingUnregisterDeleteValidateBeforeCall(cbName,
                modelVersion, progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }

    /**
     * Build call for updateMappingUpdatePost
     * 
     * @param body
     *            (required)
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
    public com.squareup.okhttp.Call updateMappingUpdatePostCall(TagLabelMapping body, String cbName,
            final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        Object localVarPostBody = body;

        // create path and map variables
        String localVarPath = "/mapping/update/";

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
    private com.squareup.okhttp.Call updateMappingUpdatePostValidateBeforeCall(TagLabelMapping body,
            String cbName, final ProgressResponseBody.ProgressListener progressListener,
            final ProgressRequestBody.ProgressRequestListener progressRequestListener)
        throws ApiException
    {
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new ApiException(
                    "Missing the required parameter 'body' when calling updateMappingUpdatePost(Async)");
        }
        // verify the required parameter 'cbName' is set
        if (cbName == null) {
            throw new ApiException(
                    "Missing the required parameter 'cbName' when calling updateMappingUpdatePost(Async)");
        }

        com.squareup.okhttp.Call call = updateMappingUpdatePostCall(body, cbName, progressListener,
                progressRequestListener);
        return call;

    }

    /**
     * Update
     * 
     * @param body
     *            (required)
     * @param cbName
     *            (required)
     * @return Object
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public Object updateMappingUpdatePost(TagLabelMapping body, String cbName) throws ApiException
    {
        ApiResponse<Object> resp = updateMappingUpdatePostWithHttpInfo(body, cbName);
        return resp.getData();
    }

    /**
     * Update
     * 
     * @param body
     *            (required)
     * @param cbName
     *            (required)
     * @return ApiResponse&lt;Object&gt;
     * @throws ApiException
     *             If fail to call the API, e.g. server error or cannot deserialize the response
     *             body
     */
    public ApiResponse<Object> updateMappingUpdatePostWithHttpInfo(TagLabelMapping body,
            String cbName)
        throws ApiException
    {
        com.squareup.okhttp.Call call = updateMappingUpdatePostValidateBeforeCall(body, cbName,
                null, null);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        return apiClient.execute(call, localVarReturnType);
    }

    /**
     * Update (asynchronously)
     * 
     * @param body
     *            (required)
     * @param cbName
     *            (required)
     * @param callback
     *            The callback to be executed when the API call finishes
     * @return The request call
     * @throws ApiException
     *             If fail to process the API call, e.g. serializing the request body object
     */
    public com.squareup.okhttp.Call updateMappingUpdatePostAsync(TagLabelMapping body,
            String cbName, final ApiCallback<Object> callback)
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

        com.squareup.okhttp.Call call = updateMappingUpdatePostValidateBeforeCall(body, cbName,
                progressListener, progressRequestListener);
        Type localVarReturnType = new TypeToken<Object>()
        {
        }.getType();
        apiClient.executeAsync(call, localVarReturnType, callback);
        return call;
    }
}
