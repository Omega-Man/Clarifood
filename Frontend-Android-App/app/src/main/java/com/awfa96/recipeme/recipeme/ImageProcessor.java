package com.awfa96.recipeme.recipeme;

import java.util.Map;

/**
 * Created by Tremaine on 10/15/2016.
 */

public interface ImageProcessor {
    public boolean addImage(byte[] image);  // sends image to api
    public Map<String,Float> getTags();

}
