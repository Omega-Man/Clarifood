package com.awfa96.recipeme.recipeme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Tremaine on 10/16/2016.
 */

public class CameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static Set<String> whiteList = new HashSet<String>();
    private static boolean whiteListReady = false;
    private static final Set<String> blackList = new HashSet<String>();
    public static final String TAG = "CameraActivity";
    static {
        String[] actualBlackList = {"vegetable"};
        for (String item : actualBlackList) {
            blackList.add(item);
        }

        //String[] actualWhiteList = {"orange","cupcake","pizza","tomato","spaghetti","apple","milk","avocado",
        //        "spaghetti","water","banana","egg","flour","shrimp","sugar","salt","pepper","olive","cheese","meat"}

    }

    private static Lock mapLock = new ReentrantLock();
    protected static Map<String,Bitmap> taggedPictures = new HashMap<>();
    private boolean isSurfaceReady;
    private CameraManager manager;
    private ImageProcessor imageProcessor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (!whiteListReady) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OnlineIngredientFinder whiteListGetter = new OnlineIngredientFinder();
                        String jsonWhiteList = whiteListGetter.getIngredientsList();
                        jsonWhiteList = jsonWhiteList.substring(1, jsonWhiteList.length() - 1);
                        String[] whiteArray = jsonWhiteList.split(",");

                        for (String item : whiteArray) {
                            whiteList.add(item.substring(1, item.length() - 1));
                        }
                    } catch (Exception e) {
                        String[] actualWhiteList = {"orange", "cupcake", "pizza", "tomato", "spaghetti", "apple", "milk", "avocado"
                                , "water", "banana", "egg", "flour", "shrimp", "sugar", "salt", "pepper", "olive", "cheese", "meat", "emu"};
                        for (String item : actualWhiteList) {
                            whiteList.add(item);
                        }
                        Log.e(TAG, "Couldn't load whitelist from api. Resetting to default - " + e.toString());
                    }
                    whiteListReady = true;

                    return null;
                }
            }.execute();
        }

        Log.d(TAG, "Whitelist: " + whiteList.toString());
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.apikeys));

        try {
            imageProcessor = new ClarifaiImageProcessor(scanner.nextLine(), scanner.nextLine());
        } catch (Exception e) {
            Log.e(TAG, "noo clarifai is down");
        } finally {
            scanner.close();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        String[] cameraIds;

        Log.d(TAG, "Whitelist: " + whiteList.toString());
        try {
            cameraIds = manager.getCameraIdList();
            for (String ids : cameraIds) {
                Log.d(TAG, "Camera ids " + ids);
            }
        } catch (Exception e) {
            Log.e(TAG, "Sucky");
            return;
        }

        TextureView view = ((TextureView) findViewById(R.id.cameraView));
        view.setSurfaceTextureListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "User responded to permission request " + requestCode + " result: " + grantResults[0]);
        setupCameraToSurface();
    }

    public void takePicture(View view) {
        Log.d(TAG, "BUTTON PRESSED");

        Log.d(TAG, "Whitelist: " + whiteList.toString());
        Canvas canvas = new Canvas();
        TextureView textureView = ((TextureView) findViewById(R.id.cameraView));

        Bitmap bitmap = textureView.getBitmap();

        ImageView preview = (ImageView) findViewById(R.id.imagePreview);
        preview.setImageBitmap(bitmap);

        new AsyncTask<Bitmap, Integer, String>() {

            @Override
            protected String doInBackground(Bitmap... params) {
                Log.d(TAG, "Start to compress image");
                ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
                params[0].compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOS);

                if (!whiteListReady) {
                    Log.d(TAG, "Waiting for whitelist");
                    while (!whiteListReady) {
                    }
                }

                Log.d(TAG, "Sending image to clarifai");
                imageProcessor.addImage(byteArrayOS.toByteArray());
                final Map<String, Float> tags = imageProcessor.getTags();
                final SortedSet<String> sortedTags = new TreeSet<String>(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return -Float.compare(tags.get(o1), tags.get(o2));
                    }
                });

                sortedTags.addAll(tags.keySet());

                String finalTag = null;
                Log.d(TAG, "Looking at tags: " + sortedTags);
                for (String possibleTag: sortedTags) {
                    if(whiteList.contains(possibleTag)) {
                        finalTag = possibleTag;
                        break;
                    }
                    Log.e(TAG, "SKIPPING TAG: " + possibleTag);
                }

                if (finalTag != null) {
                    mapLock.lock();
                    taggedPictures.put(finalTag, makeThumbnail(params[0]));
                    mapLock.unlock();

                    final String reallyFinalTag = finalTag;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(CameraActivity.this, reallyFinalTag + " " + tags.get(reallyFinalTag) * 100 + "%", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(CameraActivity.this, "Couldn't recognize the ingredient", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }



                Log.d(TAG, "" + finalTag);
                return imageProcessor.getTags().toString();
            }
        }.execute(bitmap);
    }

    private Bitmap makeThumbnail(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOS);
        byte[] bytes = byteArrayOS.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private CameraDevice camera;
    public void setupCameraToSurface() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No camera permissions :(");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
            return;
        }
        try {
            manager.openCamera("0", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    CameraActivity.this.camera = camera;
                    Log.d(TAG, "Camera opened!");
                    final List<Surface> surfaces = new ArrayList<>();
                    surfaces.add(new Surface(((TextureView) findViewById(R.id.cameraView)).getSurfaceTexture()));

                    try {
                        camera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                Log.d(TAG, "Configured to surface view");
                                try {
                                    CaptureRequest.Builder builder = session.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                                    builder.addTarget(surfaces.get(0));
                                    session.setRepeatingRequest(builder.build(), null, null);
                                    Log.d(TAG, "Capture request sent");
                                    isSurfaceReady = true;
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                Log.d(TAG, "Failed to configure to surface view");
                            }
                        }, null);

                    } catch (Exception e) {
                        Log.e(TAG, "Fail to connect to surface view " + e.toString());
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.d(TAG, "Camera disconnected!");
                    throw new NullPointerException("AHHH");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.e(TAG, "Camera error!");
                }
            }, null);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.close();
            camera = null;
        }
    }

    public void finishCamera(View view) {
        Intent i = new Intent(this, ResultsActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "No camera permissions :(");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);
            return;
        }
        setupCameraToSurface();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
