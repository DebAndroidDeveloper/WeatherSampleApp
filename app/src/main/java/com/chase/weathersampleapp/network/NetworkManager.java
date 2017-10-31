package com.chase.weathersampleapp.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chase.weathersampleapp.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkManager {
    private static final String TAG = NetworkManager.class.getCanonicalName();

    private static final MediaType JSON = MediaType.parse("application/json");

    private Context mContext;

    public NetworkManager(Context currentContext) {
        this.mContext = currentContext;
    }

    public String httpGet(String url) {

        String responseString = "";


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            responseString = response.body().string();

            JSONObject jsonObject = new JSONObject(responseString);
            Log.d(TAG, "HTTP response " + jsonObject.toString(4));
            broadcast(Constants.IntentActions.ACTION_SUCCESS, responseString, null);
            if (!response.isSuccessful()) {
                handleError(response.code(), request.url().uri() + "", responseString);
            }
        } catch (IOException e) {
            broadcast(Constants.IntentActions.ACTION_ERROR, TAG + " " + e.getMessage(), null);
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (JSONException e) {
            broadcast(Constants.IntentActions.ACTION_ERROR, TAG + " " + e.getMessage(), null);
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            ex.printStackTrace();
        }

        return responseString;
    }

    private void handleError(int responseCode, String uri, String responseString) {
        Log.e(TAG, " Error executing " + uri + " \nresponse code " + responseCode + " \nMessage: " + responseString);

        broadcast(Constants.IntentActions.ACTION_ERROR, " <p><b>Error executing:</b> " + uri + " </p><p><b>Response code:</b> " + responseCode + " </p><b>Message:</b> " + responseString, null);
    }

    /**
     * Util method to broadcast the result
     *
     * @param action
     * @param message
     * @param jsonResponse
     */
    protected void broadcast(String action, String message, JSONObject jsonResponse) {

        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(Constants.IntentExtras.MESSAGE, message);

        if (jsonResponse != null)
            intent.putExtra(Constants.IntentExtras.JSON_RESPONSE, jsonResponse.toString());

        //intent.putExtra(Constants.IntentExtras.REQUEST_ID, getRequestId());
        mContext.sendBroadcast(intent);
    }
}