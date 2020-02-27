package my.edu.utar.uccd3223;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.models.RecipeTemp;

public class Recipe extends Fragment {

    //set up Camera properties
    private static final int CAMERA_REQUEST = 1888;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int PERMISSION_CODE = 101;

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeAdapter recipeAdapter;
    private RequestQueue requestQueue;

    private String searchFoodName = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.activity_recipe, container, false);

        recipeList = (ListView) view.findViewById(R.id.lv_recipe_frag);

        // set it up to get user inputs
        EditText foodInputArea = view.findViewById(R.id.inputRecipeBox);
        ImageButton searchButton = view.findViewById(R.id.searchRecipeButton);
        ImageButton photoButton = view.findViewById(R.id.insertPhoto);


        foodInputArea.setOnFocusChangeListener((v, b) -> {
            if (!b) {
                hideKeyboard(getContext(), view);
            }
        });

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
            foodInputArea.clearFocus();
            searchFoodName = foodInputArea.getText().toString();
            if (!searchFoodName.isEmpty()) {
                Toast.makeText(context,
                        "Hold up, fetching some delicious recipes!",
                        Toast.LENGTH_SHORT).show();
                retrieveRecipesWithName();
            }
        });

        // Receive from camera - predict food
        Bundle args = this.getArguments();
        if (args != null) {
            searchFoodName = args.getString("food");
            foodInputArea.setText(searchFoodName);
            searchButton.performClick();
        }


        return view;
    }

    // create listview and display recipes
    private void handleRecipeFragmentAdapter() {
        Context context = getActivity().getApplicationContext();
        if(recipeTempList.size()>0) {
            recipeAdapter = new RecipeAdapter(context, recipeTempList);
            recipeList.setAdapter(recipeAdapter);
            recipeList.setOnItemClickListener((parent, view, position, id) -> {
                // information to send to recipeInformation activity
                RecipeTemp recipe = recipeTempList.get(position);
                String recipeId = recipe.getId();

                Bundle args = new Bundle();
                args.putString("recipeId", recipeId);
                args.putString("food", searchFoodName);
                startActivity(new Intent(getActivity(), RecipeInformation.class)
                        .putExtras(args));
            });
        }else{
            RecipeTemp recipe = new RecipeTemp();
            recipe.setTitle("Not Found");
            recipeTempList.add(recipe);
            recipeAdapter = new RecipeAdapter(context, recipeTempList);
            recipeList.setAdapter(recipeAdapter);
        }
    }

    // call api with given Name
    private void retrieveRecipesWithName() {
        requestQueue = Volley.newRequestQueue(getActivity());

        final SpoonAPI spoon = new SpoonAPI();

        String url = spoon.getRecipeURL(searchFoodName);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        spoon.getRecipeHelper(new JSONObject(response));
                        recipeTempList = spoon.getRecipe();
                        handleRecipeFragmentAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        });
        requestQueue.add(req);

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

    private void hideKeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
