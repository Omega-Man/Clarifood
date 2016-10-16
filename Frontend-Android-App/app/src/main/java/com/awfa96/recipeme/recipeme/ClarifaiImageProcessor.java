package com.awfa96.recipeme.recipeme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by Tremaine on 10/15/2016.
 */

public class ClarifaiImageProcessor implements ImageProcessor {
    // test main
    /*
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClarifaiImageProcessor iProcess = new ClarifaiImageProcessor();
        iProcess.addImage(null);
    }
    */
    private final float MINIMUM_MATCH = .5f;
    private final ClarifaiClient client;
    private Map<String, Float> conceptAndAccuracy;

    public ClarifaiImageProcessor(String appId, String appSecret) throws InterruptedException, ExecutionException {
        client = new ClarifaiBuilder(appId, appSecret).build().get();
        conceptAndAccuracy = new HashMap<>();
    }

    @Override
    public boolean addImage(byte[] image) {
        // grab output from clarifai with given image
        final List<ClarifaiOutput<Concept>> predictionResults =
                client.getDefaultModels().foodModel()
                .predict().withInputs(
                        ClarifaiInput.forImage(ClarifaiImage.of(image))
                        //ClarifaiInput.forImage(ClarifaiImage.of("https://samples.clarifai.com/metro-north.jpg"))
                )
                .executeSync()
                .get();
        // DEBUG print all names / values
        /*
        for (Concept i : predictionResults.get(0).data()) {
            System.out.println(i.name() + " : " + i.value());
        }
        */

        // Add names/values to concept
        conceptAndAccuracy.clear();
        float highestMatch = 0.f;
        for (Concept i: predictionResults.get(0).data()) {
            conceptAndAccuracy.put(i.name(),i.value());
            if (highestMatch < i.value()) {
                highestMatch = i.value();
            }
        }

        return highestMatch >= MINIMUM_MATCH;
    }

    @Override
    public Map<String, Float> getTags() {
        return conceptAndAccuracy;
    }
}
