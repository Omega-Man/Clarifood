package com.awfa96.recipeme.recipeme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tremaine on 10/16/2016.
 */

public class ResultsActivity extends AppCompatActivity {
    private Map<String,Bitmap> ingredientMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        ingredientMap = CameraActivity.taggedPictures;
        Log.d("ResultsActiviteeee", ingredientMap.values().toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        final TableLayout thumbnails = (TableLayout)findViewById(R.id.thumbnails);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT, 0.1f
        );
        for (final String tag : ingredientMap.keySet()) {
            final TableRow row = new TableRow(this);
            row.setLayoutParams(lp);

            ImageView view = new ImageView(this);

            view.setImageBitmap(ingredientMap.get(tag));
            double newWidth = ingredientMap.get(tag).getWidth() * 250.0 / ingredientMap.get(tag).getHeight();
            Log.d("ResultsActiviteeee", newWidth + " is width");
            view.setLayoutParams(new TableRow.LayoutParams((int) (newWidth), 250));
            row.addView(view);

            TextView label2 = new TextView(this);
            label2.setText(tag);
            label2.setGravity(Gravity.CENTER);
            label2.setPadding(10, 10, 10, 10);
            row.addView(label2, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 250));

            ImageView delete = new ImageView(this);
            delete.setImageResource(R.mipmap.ic_delete_48px);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    thumbnails.removeView(row);
                    ingredientMap.remove(tag);
                    Log.d("ResultsActiviteeee", "Removed, new map: " + ingredientMap.toString());
                }
            });
            row.addView(delete, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 250));

            thumbnails.addView(row);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        TableLayout thumbnails = (TableLayout)findViewById(R.id.thumbnails);
        thumbnails.removeAllViews();
    }

    public void goBack(View view) {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
        finish();
    }

    public void goHome(View view) {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
