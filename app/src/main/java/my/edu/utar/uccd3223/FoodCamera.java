package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import my.edu.utar.uccd3223.util.ImageFilePath;

public class FoodCamera extends AppCompatActivity {

    private ImageView takePictureButton, flash;

    TextureView textureView;
    private LifecycleRegistry lifecycleRegistry;

    ImageView upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_camera);
        textureView = (TextureView) findViewById(R.id.texture);

        takePictureButton = findViewById(R.id.btn_takepicture);

        assert takePictureButton != null;
        upload = findViewById(R.id.btn_upload);

        //To predict from images in Gallery
        upload.setOnClickListener(v -> {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, 1);
        });

        startCamera();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null && data.getData() != null && resultCode == RESULT_OK) {
            String realPath = ImageFilePath.getPath(FoodCamera.this, data.getData());
            if (!realPath.equals("")) {
                Intent predictionIntent = new Intent(FoodCamera.this, FoodCamDetection.class);
                predictionIntent.putExtra("method", "gallery");
                predictionIntent.putExtra("image", realPath);
                startActivity(predictionIntent);
            }else{
                Toast.makeText(getApplicationContext(), "Invalid Image", Toast.LENGTH_SHORT).show();
            }
        }
        return;
    }

    private void startCamera() {

        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight()); //size of the screen


        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        //to update the surface texture we  have to destroy it first then re-add it
        preview.setOnPreviewOutputUpdateListener(
                output -> {
                    ViewGroup parent = (ViewGroup) textureView.getParent();
                    parent.removeView(textureView);
                    parent.addView(textureView, 0);

                    textureView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                });


        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        takePictureButton.setOnClickListener(v -> {

            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + "IMG" + System.currentTimeMillis() + ".png");
            imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                @Override
                public void onImageSaved(@NonNull File file) {
                    Intent predictionIntent = new Intent(FoodCamera.this, FoodCamDetection.class);
                    predictionIntent.putExtra("method", "camera");
                    predictionIntent.putExtra("image", file.getAbsolutePath());
                    startActivity(predictionIntent);
                }

                @Override
                public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                    if (cause != null) {
                        //cause.printStackTrace();
                    }
                }
            });

        });

        //bind to lifecycle:
        CameraX.bindToLifecycle(this, preview, imgCap);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cX, cY);
        textureView.setTransform(mx);
    }
}
