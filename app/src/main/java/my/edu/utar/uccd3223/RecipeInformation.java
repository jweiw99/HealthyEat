package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.models.Ingredient;
import my.edu.utar.uccd3223.models.RecipeFull;

public class RecipeInformation extends AppCompatActivity implements View.OnClickListener {

    private RecipeFull recipeFull;
    private String recipeId;
    private String email;
    private RequestQueue requestQueue;
    private TextView tv_carbs;
    private TextView tv_protein;
    private TextView tv_calories;
    private TextView tv_fat;
    private TextView tv_cookingInMins;
    private TextView tv_instructions;
    private TextView tv_ingredients;
    private TextView tv_servings;
    private TextView tv_title;
    private ImageView iv_recipeImage;
    private Button btn_exit;
    private FloatingActionButton favoriteFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recipeId = getIntent().getExtras().getString("recipeId");
        email = getIntent().getExtras().getString("email");
        handleAPICall();

        favoriteFab = findViewById(R.id.fab);
        btn_exit = findViewById(R.id.btn_recipeInfo_exit);
        btn_exit.setOnClickListener(this);
        favoriteFab.setOnClickListener(this);
    }

    // when a user clicks on the favorite fab, it will store information about the recipe to the
    // db (title, image, recipeID)
    private void saveFavoriteRecipeToDb() {
        Map<String, Object> user = new HashMap<>();
        Map<String, String> description = new HashMap<>();
        description.put("Title", recipeFull.getTitle());
        description.put("Image", recipeFull.getImage());

        user.put(recipeId, description);


    }

    // override onclicks
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // fab was clicked, favorite it
            case R.id.fab:
                saveFavoriteRecipeToDb();
                break;
            // exit button
            case R.id.btn_recipeInfo_exit:
                finish();
                break;
            case R.id.btn_eat:
                changeCalories();
        }
    }

    // display recipe to user by setting the text
    private void displayRecipeFull() {
        tv_carbs = findViewById(R.id.tv_recipeInfo_carbs);
        tv_protein = findViewById(R.id.tv_recipeInfo_protein);
        tv_calories = findViewById(R.id.tv_recipeInfo_calories);
        tv_fat = findViewById(R.id.tv_recipeInfo_fat);
        tv_cookingInMins = findViewById(R.id.tv_recipeInfo_cookingInMins);
        tv_instructions = findViewById(R.id.tv_recipeInfo_instructions);
        tv_ingredients = findViewById(R.id.tv_recipeInfo_ingredients);
        tv_servings = findViewById(R.id.tv_recipeInfo_servings);
        tv_title = findViewById(R.id.tv_recipeInfo_title);
        iv_recipeImage = findViewById(R.id.iv_recipeInfo_recipeImage);

        tv_carbs.setText("Carbs: " + recipeFull.getNutrition().getCarbohydrates());
        tv_protein.setText("Protein: " + recipeFull.getNutrition().getProtein());
        tv_calories.setText("Calories: " + recipeFull.getNutrition().getCalories());
        tv_fat.setText("Fat: " + recipeFull.getNutrition().getFat());
        tv_cookingInMins.setText("Cooking time: " + recipeFull.getCookingMinutes());
        tv_instructions.setText("Instructions: " + recipeFull.getInstructions());

        List<Ingredient> ingredients_list = recipeFull.getIngredients_list();
        String tv_string = "";
        for (int i = 0; i < ingredients_list.size(); ++i) {
            Ingredient temp = ingredients_list.get(i);
            tv_string += temp.getName() + ": " + temp.getAmount() + " " + temp.getUnit() + " ";
        }
        tv_ingredients.setText(tv_string);
        tv_servings.setText("Servings: " + Integer.toString(recipeFull.getServings()));
        tv_title.setText(recipeFull.getTitle());

        Picasso.with(getApplicationContext()).load(recipeFull.getImage())
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_recipeImage);
    }


    // handle API call to receive full information about recipe given id
    public void handleAPICall() {
        final SpoonAPI spoon = new SpoonAPI();

        String url = spoon.getRecipeIDURL(recipeId);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        spoon.getRecipeByIDHelper(response);
                        recipeFull = spoon.getRecipeFull();
                        displayRecipeFull();
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
                headers.put("X-RapidAPI-Key", spoon.getAPIKey());
                return headers;
            }
        };
        requestQueue.add(req);
    }

    private void changeCalories() {

    }
}
