package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class FoodCamDetection extends AppCompatActivity {

    private static final String HOSTED_MODEL_NAME = "foodDetection";
    private static final String LOCAL_MODEL_ASSET = "foodmodel.tflite";
    private static final String LABEL_PATH = "labels.txt";

    private FirebaseModelInterpreter mInterpreter;
    private FirebaseModelInputOutputOptions mDataOptions;

    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;

    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    private List<String> mLabelList;
    String foodLabel = "";

    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    1,
                    (o1, o2) -> (o1.getValue()).compareTo(o2.getValue()));

    private ImageView mImageView;
    private Button retakeBtn, continueBtn;
    private ProgressBar loadingIndicator;
    private Bitmap mSelectedImage;

    TextView foodP;
    RelativeLayout rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cam_detection);

        mImageView = findViewById(R.id.image_view);
        retakeBtn = findViewById(R.id.retakeBtn);
        continueBtn = findViewById(R.id.continueBtn);
        loadingIndicator = findViewById(R.id.progressBar_cyclic);

        foodP = findViewById(R.id.predictfoodName);

        rl = findViewById(R.id.foodCamBody);

        Bundle bundleExtras = getIntent().getExtras();
        String method = bundleExtras.getString("method");
        String path = bundleExtras.getString("image");

        mSelectedImage = BitmapFactory.decodeFile(path);

        if (method.equals("gallery")) {
            try {
                mSelectedImage = convertToPNG(mSelectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mImageView.setImageBitmap(mSelectedImage);

        initCustomModel();

        retakeBtn.setOnClickListener(v -> finish());

        continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FoodCamDetection.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("food", foodP.getText().toString().toLowerCase());
            startActivity(intent);
        });
    }

    private void initCustomModel() {
        loadingIndicator.setVisibility(View.VISIBLE);
        mLabelList = loadLabelList(this);

        int[] inputDims = {DIM_BATCH_SIZE, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y, DIM_PIXEL_SIZE};
        int[] outputDims = {DIM_BATCH_SIZE, mLabelList.size()};
        try {
            mDataOptions =
                    new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, inputDims)
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, outputDims)
                            .build();
            FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions
                    .Builder()
                    .requireWifi()
                    .build();
            FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder
                    (HOSTED_MODEL_NAME)
                    .enableModelUpdates(true)
                    .setInitialDownloadConditions(conditions)
                    .setUpdatesDownloadConditions(conditions)
                    .build();
            FirebaseLocalModel localModel =
                    new FirebaseLocalModel.Builder("asset")
                            .setAssetFilePath(LOCAL_MODEL_ASSET).build();
            FirebaseModelManager manager = FirebaseModelManager.getInstance();
            //manager.registerRemoteModel(remoteModel);
            manager.registerLocalModel(localModel);
            FirebaseModelOptions modelOptions =
                    new FirebaseModelOptions.Builder()
                            //.setRemoteModelName(HOSTED_MODEL_NAME)
                            .setLocalModelName("asset")
                            .build();
            mInterpreter = FirebaseModelInterpreter.getInstance(modelOptions);
            runModelInference();
        } catch (FirebaseMLException e) {
            Toast.makeText(getApplicationContext(), "Error while setting up the model", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void runModelInference() {
        if (mInterpreter == null) {
            Toast.makeText(getApplicationContext(), "Error model not ready", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create input data.
        ByteBuffer imgData = convertBitmapToByteBuffer(mSelectedImage, mSelectedImage.getWidth(),
                mSelectedImage.getHeight());

        try {
            FirebaseModelInputs inputs = new FirebaseModelInputs.Builder().add(imgData).build();
            mInterpreter
                    .run(inputs, mDataOptions)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error running model inference", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .continueWith(
                            new Continuation<FirebaseModelOutputs, List<String>>() {
                                @Override
                                public List<String> then(Task<FirebaseModelOutputs> task) {
                                    float[][] labelProbArray = task.getResult()
                                            .<float[][]>getOutput(0);
                                    List<String> topLabels = getTopLabels(labelProbArray);

                                    foodLabel = topLabels.get(0).split(":")[0];
                                    foodLabel = foodLabel.substring(0, 1).toUpperCase() + foodLabel.substring(1);

                                    if (!foodLabel.equals("")) {
                                        loadingIndicator.setVisibility(View.GONE);
                                        foodP.setVisibility(View.VISIBLE);
                                        foodP.setText(topLabels.get(0));
                                        continueBtn.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Label error", Toast.LENGTH_SHORT).show();
                                    }

                                    return topLabels;
                                }
                            });
        } catch (FirebaseMLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error running model inference", Toast.LENGTH_SHORT).show();
        }

    }

    private List<String> loadLabelList(Activity activity) {
        List<String> labelList = new ArrayList<>();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(activity.getAssets().open
                             (LABEL_PATH)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
        }
        return labelList;
    }

    private synchronized List<String> getTopLabels(float[][] labelProbArray) {
        for (int i = 0; i < mLabelList.size(); ++i) {
            sortedLabels.add(
                    new AbstractMap.SimpleEntry<>(mLabelList.get(i), labelProbArray[0][i]));
            if (sortedLabels.size() > 1) {
                sortedLabels.poll();
            }
        }
        List<String> result = new ArrayList<>();
        final int size = sortedLabels.size();
        for (int i = 0; i < size; ++i) {
            Map.Entry<String, Float> label = sortedLabels.poll();
            //result.add(label.getKey() + ":" + round(label.getValue() * 100, 2) + "%");
            result.add(label.getKey());
        }
        Collections.reverse(result);
        return result;
    }

    private synchronized ByteBuffer convertBitmapToByteBuffer(
            Bitmap bitmap, int width, int height) {
        ByteBuffer imgData =
                ByteBuffer.allocateDirect(
                        DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE * 4);
        imgData.order(ByteOrder.nativeOrder());
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, DIM_IMG_SIZE_X, DIM_IMG_SIZE_Y,
                true);
        imgData.rewind();
        scaledBitmap.getPixels(intValues, 0, scaledBitmap.getWidth(), 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight());
        // Convert the image to int points.
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                imgData.putFloat(((val >> 16) & 0xFF) / 255.f);
                imgData.putFloat(((val >> 8) & 0xFF) / 255.f);
                imgData.putFloat((val & 0xFF) / 255.f);
            }
        }
        return imgData;
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static Bitmap convertToPNG(Bitmap image) throws IOException {

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                "gallery_photo",  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(imageFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }
}
