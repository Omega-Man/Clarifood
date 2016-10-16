package com.awfa96.recipeme.recipeme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void loginGuest(View view) {
        Log.d(TAG, "yes");
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
    }
}
