package com.awfa96.recipeme.recipeme;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void onlineRecipeTest() throws IOException {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("meat");
        ingredients.add("asparagus");
        ingredients.add("tomato");
        OnlineRecipeFinder myFinder = new OnlineRecipeFinder();
        assertTrue(myFinder.getRecipes(ingredients).length() > 0);
    }
}