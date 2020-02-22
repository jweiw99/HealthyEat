package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Recipe extends Fragment {

    //set up Camera properties
    private static final int CAMERA_REQUEST = 1888;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int PERMISSION_CODE = 101;


    private ListView recipeList;
    private int numberRecipesShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.activity_recipe, container, false);

        // set it up to get user inputs
        final EditText foodInputArea = (EditText) view.findViewById(R.id.inputBox);
        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        ImageButton photoButton = (ImageButton) view.findViewById(R.id.insertPhoto);


        photoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMISSIONS, PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent(context.getApplicationContext(),
                        FoodCamera.class);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        // What happens when search button is clicked.
        searchButton.setOnClickListener(v -> {

            Toast.makeText(context,
                    "Hold up, put on thy aprons because we're fetching some delicious recipes!",
                    Toast.LENGTH_LONG).show();


        });
        recipeList = (ListView) view.findViewById(R.id.lv_recipe_frag);
        // hardcoded value for now
        numberRecipesShow = 10;

        return view;
    }


    // Handles the response after when the user presses the camera button
    // allowed the app to access the camera
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Context context = getActivity();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, getString(R.string.camera_permission_ok), Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(context.getApplicationContext(),
                        FoodCamera.class);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(context, getString(R.string.camera_permission_denied), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
