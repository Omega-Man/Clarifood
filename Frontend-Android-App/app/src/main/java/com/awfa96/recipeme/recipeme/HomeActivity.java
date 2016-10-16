package com.awfa96.recipeme.recipeme;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Tremaine on 10/16/2016.
 */

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private static int textPaddingInRows = 0;
    private Map<String,Float> ingredients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ingredients = new TreeMap<>();
        setContentView(R.layout.activity_home);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        textPaddingInRows = (int) Math.ceil(10 * logicalDensity);
    }

    protected void getCameraView(View view) {
        Log.d(TAG, "am at camera");
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
    }


    @Override
    public void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final TableLayout recipeTable = (TableLayout)findViewById(R.id.recipes);
                final TableRow.LayoutParams lp = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1.f
                );
                OnlineRecipeFinder finder = new OnlineRecipeFinder();
                try {
                    String jsonRecipes = finder.getRecipes(new ArrayList(CameraActivity.taggedPictures.keySet()));

                    jsonRecipes = jsonRecipes.substring(1, jsonRecipes.length() - 1);
                    final String[] recipeArray = jsonRecipes.split(",");
                    runOnUiThread(
                        new Runnable(){

                            @Override
                            public void run() {
                                recipeTable.removeAllViews();

                                if (CameraActivity.taggedPictures.keySet().size() > 0) {
                                    TextView title = (TextView) findViewById(R.id.topTextResults);
                                    title.setText(getText(R.string.make_foods));
                                    for (String recipe : recipeArray) {
                                        if (recipe.length() > 3) {
                                            recipe = recipe.substring(1, recipe.length() - 1);
                                            final TableRow row = new TableRow(HomeActivity.this);
                                            row.setLayoutParams(lp);

                                            TextView label = new TextView(HomeActivity.this);
                                            label.setText(recipe);
                                            label.setGravity(Gravity.CENTER);
                                            label.setPadding(textPaddingInRows, textPaddingInRows, textPaddingInRows, textPaddingInRows);
                                            row.addView(label, new TableRow.LayoutParams(recipeTable.getWidth(), TableRow.LayoutParams.MATCH_PARENT));
                                            final String reallyFinalRecipe = recipe;
                                            label.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent i = new Intent(Intent.ACTION_WEB_SEARCH);
                                                    i.putExtra(SearchManager.QUERY, reallyFinalRecipe);
                                                    if (i.resolveActivity(getPackageManager()) != null) {
                                                        startActivity(i);
                                                    }
                                                }
                                            });
                                            recipeTable.addView(row);
                                        }
                                    }
                                } else {
                                    TextView title = (TextView) findViewById(R.id.topTextResults);
                                    title.setText(getText(R.string.no_recipe_available));
                                }
                            }
                        }
                    );
                } catch (IOException e) {
                    Log.d("HomeActivity", "Couldn't get recipes - " + e.toString());
                }
                return null;
            }
        }.execute();
    }

    public void editMode(View view) {
        Intent i = new Intent(this, ResultsActivity.class);
        startActivity(i);
        finish();
    }
}
