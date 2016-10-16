package com.awfa96.recipeme.recipeme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Tremaine on 10/15/2016.
 */

public class OnlineRecipeFinder {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String REQUEST_URL = "http://40.112.189.205/recipes";
    private OkHttpClient client;
    public OnlineRecipeFinder() {
        client = new OkHttpClient();
    }

    public static void main(String[] args) throws IOException {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("tomatoes");
        ingredients.add("olive");
        ingredients.add("heuvos rancheros");
        System.out.println(tagsToJson(ingredients));
        OnlineRecipeFinder myFinder = new OnlineRecipeFinder();
        System.out.println(myFinder.getRecipes(ingredients));
    }


    public String getRecipes(List<String> ingredients) throws IOException {
        String jsonTags = tagsToJson(ingredients);
        RequestBody body = RequestBody.create(JSON, jsonTags);
        Request request = new Request.Builder()
                .url(REQUEST_URL)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private static String tagsToJson(List<String> tags) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"ingredients\": [");
        for (int tagNum = 0; tagNum < tags.size() - 1; tagNum++) {
            sb.append("\"").append(tags.get(tagNum) + "\", ");
        }
        if(tags.size() > 0) {
            sb.append("\"").append(tags.get(tags.size() - 1));
        }
        sb.append("\"] }");
        return sb.toString();
    }
}
