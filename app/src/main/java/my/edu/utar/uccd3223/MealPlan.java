package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import my.edu.utar.uccd3223.util.hideKeyboard;

public class MealPlan extends Fragment {

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeAdapter recipeAdapter;
    private RequestQueue requestQueue;

    private String targetCalories = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.activity_meal_plan, container, false);

        recipeList = (ListView) view.findViewById(R.id.lv_recipe_frag);

        // set it up to get user inputs
        EditText coloriesInput = view.findViewById(R.id.inputCaloriesBox);
        ImageButton searchButton = view.findViewById(R.id.searchCaloriesButton);

        coloriesInput.setOnFocusChangeListener((v, b) -> {
            if (!b) {
                new hideKeyboard(getContext(), view);
            }
        });

        // What happens when search button is clicked.
        searchButton.setOnClickListener(v -> {
            coloriesInput.clearFocus();
            targetCalories = coloriesInput.getText().toString();
            if (!targetCalories.isEmpty()) {
                Toast.makeText(context,
                        "Hold up, fetching some delicious recipes!",
                        Toast.LENGTH_SHORT).show();
                retrieveRecipesWithColories();
            }
        });

        return view;
    }

    // create listview and display recipes
    private void handleRecipeFragmentAdapter() {
        Context context = getActivity().getApplicationContext();
        if (recipeTempList.size() > 0) {
            recipeAdapter = new RecipeAdapter(context, recipeTempList);
            recipeList.setAdapter(recipeAdapter);
            recipeList.setOnItemClickListener((parent, view, position, id) -> {
                // information to send to recipeInformation activity
                RecipeTemp recipe = recipeTempList.get(position);
                String recipeId = recipe.getId();

                Bundle args = new Bundle();
                args.putString("recipeId", recipeId);
                startActivity(new Intent(getActivity(), RecipeInformation.class)
                        .putExtras(args));
            });
        } else {
            RecipeTemp recipe = new RecipeTemp();
            recipe.setTitle("Not Found");
            recipeTempList.add(recipe);
            recipeAdapter = new RecipeAdapter(context, recipeTempList);
            recipeList.setAdapter(recipeAdapter);
        }
    }

    // call api with given Name
    private void retrieveRecipesWithColories() {
        requestQueue = Volley.newRequestQueue(getActivity());

        final SpoonAPI spoon = new SpoonAPI();

        String url = spoon.getMealPlanURL(targetCalories);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        spoon.getMealPlanHelper(new JSONObject(response));
                        recipeTempList = spoon.getMeal();
                        handleRecipeFragmentAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        });
        requestQueue.add(req);

    }
}
