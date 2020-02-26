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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
            searchFoodName = foodInputArea.getText().toString();
            if (!searchFoodName.isEmpty()) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(getActivity().INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                Toast.makeText(context,
                        "Hold up, fetching some delicious recipes!",
                        Toast.LENGTH_SHORT).show();
                try {
                    retrieveRecipesWithName();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        recipeAdapter = new RecipeAdapter(context, recipeTempList);
        recipeList.setAdapter(recipeAdapter);
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
    private void retrieveRecipesWithName() throws JSONException {
        requestQueue = Volley.newRequestQueue(getActivity());

        final SpoonAPI spoon = new SpoonAPI();
        final String apikey = spoon.getAPIKey();

        String url = spoon.getRecipeURL(searchFoodName);

        String responedata = "{\"results\":[{\"id\":246916,\"title\":\"Bison Burger\",\"readyInMinutes\":45,\"servings\":6,\"image\":\"Buffalo-Burger-246916.jpg\",\"imageUrls\":[\"Buffalo-Burger-246916.jpg\"]},{\"id\":245166,\"title\":\"Hawaiian Pork Burger\",\"readyInMinutes\":40,\"servings\":4,\"image\":\"Hawaiian-Pork-Burger-245166.jpg\",\"imageUrls\":[\"Hawaiian-Pork-Burger-245166.jpg\"]},{\"id\":246009,\"title\":\"Blue Cheese Burgers\",\"readyInMinutes\":55,\"servings\":4,\"image\":\"Blue-Cheese-Burgers-246009.jpg\",\"imageUrls\":[\"Blue-Cheese-Burgers-246009.jpg\"]},{\"id\":219957,\"title\":\"Carrot & sesame burgers\",\"readyInMinutes\":50,\"servings\":6,\"image\":\"Carrot---sesame-burgers-219957.jpg\",\"imageUrls\":[\"Carrot---sesame-burgers-219957.jpg\"]},{\"id\":607109,\"title\":\"Turkey Zucchini Burger with Garlic Mayo\",\"readyInMinutes\":45,\"servings\":6,\"image\":\"Turkey-Zucchini-Burger-with-Garlic-Mayo-607109.jpg\",\"imageUrls\":[\"Turkey-Zucchini-Burger-with-Garlic-Mayo-607109.jpg\"]},{\"id\":864633,\"title\":\"Banh Mi Burgers with Spicy Sriracha Mayo\",\"readyInMinutes\":35,\"servings\":4,\"image\":\"banh-mi-burgers-with-spicy-sriracha-mayo-864633.jpg\",\"imageUrls\":[\"banh-mi-burgers-with-spicy-sriracha-mayo-864633.jpg\"]},{\"id\":219871,\"title\":\"Halloumi aubergine burgers with harissa relish\",\"readyInMinutes\":20,\"servings\":4,\"image\":\"Halloumi-aubergine-burgers-with-harissa-relish-219871.jpg\",\"imageUrls\":[\"Halloumi-aubergine-burgers-with-harissa-relish-219871.jpg\"]},{\"id\":246177,\"title\":\"Grilled Beef and Mushroom Burger\",\"readyInMinutes\":30,\"servings\":3,\"image\":\"Grilled-Beef-and-Mushroom-Burger-246177.jpg\",\"imageUrls\":[\"Grilled-Beef-and-Mushroom-Burger-246177.jpg\"]},{\"id\":245343,\"title\":\"Herbed Turkey Burger\",\"readyInMinutes\":30,\"servings\":8,\"image\":\"Herbed-Turkey-Burger-245343.jpg\",\"imageUrls\":[\"Herbed-Turkey-Burger-245343.jpg\"]},{\"id\":593801,\"title\":\"Turkey Burgers with Mango Chutney\",\"readyInMinutes\":30,\"servings\":12,\"image\":\"Ground-Turkey-Burger-Sliders-with-Mango-Chutney-593801.jpg\",\"imageUrls\":[\"Ground-Turkey-Burger-Sliders-with-Mango-Chutney-593801.jpg\"]}],\"baseUri\":\"https://spoonacular.com/recipeImages/\",\"offset\":0,\"number\":10,\"totalResults\":103,\"processingTimeMs\":272,\"expires\":1582920875140,\"isStale\":false}";

        spoon.getRecipeHelper(new JSONObject(responedata));
        recipeTempList = spoon.getRecipe();
        handleRecipeFragmentAdapter();

/*
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
*/


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
