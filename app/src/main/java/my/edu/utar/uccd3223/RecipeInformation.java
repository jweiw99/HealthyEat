package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.models.Ingredient;
import my.edu.utar.uccd3223.models.RecipeFull;

public class RecipeInformation extends AppCompatActivity {

    private RecipeFull recipeFull;
    private String recipeId;
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
    private Button btn_exit, btn_eat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_information);

        recipeId = getIntent().getExtras().getString("recipeId");
        handleAPICall();
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

        tv_title.setText(recipeFull.getTitle());
        tv_cookingInMins.setText("Cooking time: " + recipeFull.getCookingMinutes());

        tv_calories.setText("Calories: " + recipeFull.getNutrition().getCalories());
        tv_protein.setText("Protein: " + recipeFull.getNutrition().getProtein());
        tv_fat.setText("Fat: " + recipeFull.getNutrition().getFat());
        tv_carbs.setText("Carbs: " + recipeFull.getNutrition().getCarbohydrates());
        tv_instructions.setText("Instructions:\n" + recipeFull.getInstructions());

        tv_servings.setText("Servings: " + Integer.toString(recipeFull.getServings()));

        List<Ingredient> ingredients_list = recipeFull.getIngredients_list();
        String tv_string = "Ingredients:\n";
        for (int i = 0; i < ingredients_list.size(); ++i) {
            Ingredient temp = ingredients_list.get(i);
            tv_string += temp.getName() + ": " + temp.getAmount() + " " + temp.getUnit() + "\n";
        }
        tv_ingredients.setText(tv_string);



        String photoURL = "https://spoonacular.com/recipeImages/" + recipeId + "-90x90.jpg";
        Picasso.with(getApplicationContext()).load(recipeFull.getImage()).fit().centerCrop()
                .placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv_recipeImage);
    }

    // handle API call to receive full information about recipe given id
    public void handleAPICall() {
        final SpoonAPI spoon = new SpoonAPI();

        String url = spoon.getRecipeIDURL(recipeId);

        requestQueue = Volley.newRequestQueue(this);
        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        spoon.getRecipeByIDHelper(new JSONObject(response));
                        recipeFull = spoon.getRecipeFull();
                        displayRecipeFull();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        });
        requestQueue.add(req);
    }

    private void changeCalories(View v) {

    }

    private void exit(View v){
        finish();
    }
}
