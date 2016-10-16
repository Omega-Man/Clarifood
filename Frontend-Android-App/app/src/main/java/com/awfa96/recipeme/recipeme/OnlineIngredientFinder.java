package com.awfa96.recipeme.recipeme;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tremaine on 10/16/2016.
 */

public class OnlineIngredientFinder {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String REQUEST_URL = "http://40.112.189.205/ingredients";
    private OkHttpClient client;
    public OnlineIngredientFinder() { client = new OkHttpClient(); }
    public static void main(String[] args) throws IOException {
        OnlineIngredientFinder oig = new OnlineIngredientFinder();
        System.out.println(oig.getIngredientsList());
    }

    // returns list of all ingredients from the server
    public String getIngredientsList() throws IOException {
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
