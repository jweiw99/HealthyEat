package my.edu.utar.uccd3223;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.models.RecipeTemp;

public class Recipe extends Fragment {

    //set up Camera properties
    private static final int CAMERA_REQUEST = 1888;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int PERMISSION_CODE = 101;

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeComplexAdapter recipeComplexAdapter;
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
        EditText foodInputArea = view.findViewById(R.id.inputBox);
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        ImageButton photoButton = view.findViewById(R.id.insertPhoto);


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
            searchFoodName = foodInputArea.getText().toString();
            retrieveRecipesWithName();
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
        recipeComplexAdapter = new RecipeComplexAdapter(context, recipeTempList);
        recipeList.setAdapter(recipeComplexAdapter);
        recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // information to send to recipeInformation activity
                RecipeTemp recipe = recipeTempList.get(position);
                String recipeId = recipe.getId();

                Bundle args = new Bundle();
                args.putString("recipeId", recipeId);
                startActivity(new Intent(getActivity(), RecipeInformation.class)
                        .putExtras(args));
            }
        });
    }

    // call api with given Name
    private void retrieveRecipesWithName() {
        requestQueue = Volley.newRequestQueue(getActivity());

        int max = 0;
        String cuisine = "";
        int numberRecipesToShow = 10;

        List<String> cuisinesList = new ArrayList<>(
                Arrays.asList("african", "chinese", "korean", "japanese", "vietnamese",
                        "thai", "irish", "italian", "spanish", "british", "indian",
                        "mexican", "french", "eastern", "middle", "american", "jewish",
                        "southern", "caribbean", "cajun", "greek", "nordic", "german",
                        "european", "eastern")
        );

        cuisine = "";

        final SpoonAPI spoon = new SpoonAPI();
        final String apikey = spoon.getAPIKey();

        String url = spoon.getRecipeComplexURL(searchFoodName, numberRecipesToShow,1500, cuisine);

        Log.d("RECIPEFRAGMENT", "ABOUT TO DO THE API CALL");
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spoon.getRecipeComplexHelper(response);
                        Log.d("RECIPEFRAGMENT", "THIS IS THE SPOON RESULT: "
                                + spoon.getRecipeComplex());
                        recipeTempList = spoon.getRecipeComplex();
                        handleRecipeFragmentAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("X-RapidAPI-Key", apikey);
                return headers;
            }
        };
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
}
