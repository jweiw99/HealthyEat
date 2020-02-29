package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.models.RecipeTemp;
import my.edu.utar.uccd3223.util.hideKeyboard;

public class Ingredient extends Fragment {

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeAdapter recipeAdapter;
    private RequestQueue requestQueue;

    private int numberInput = 0;
    private List<String> searchIngredient = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.activity_ingredient, container, false);

        recipeList = view.findViewById(R.id.lv_recipe_frag);

        // set it up to get user inputs
        EditText ingredientInputArea = view.findViewById(R.id.ingredientinputBox);
        Button searchButton = view.findViewById(R.id.searchIngredientButton);
        ImageButton addButton = view.findViewById(R.id.addButton);

        RelativeLayout canvas = view.findViewById(R.id.inputLayoutR);

        // What happens when search button is clicked.
        searchButton.setOnClickListener(v -> {
            String ingredientinput = ingredientInputArea.getText().toString();
            for (int i = 0; i <= numberInput; i++) {
                if (i == 0 && !ingredientinput.equals("")) {
                    ingredientInputArea.clearFocus();
                    searchIngredient.add(ingredientinput.toLowerCase());
                } else {
                    final EditText ingredientinputtext = view.findViewById(i);
                    final String inputtext = ingredientinputtext.getText().toString();
                    ingredientinputtext.clearFocus();
                    if (!inputtext.equals("")) {
                        searchIngredient.add(inputtext.toLowerCase());
                    }
                }
            }
            if (searchIngredient.size() > 0) {
                new hideKeyboard(getContext(),v);
                Toast.makeText(context,
                        "Hold up, fetching some delicious recipes!",
                        Toast.LENGTH_SHORT).show();
                retrieveRecipesWithIngredients();
            }
        });

        addButton.setOnClickListener(v -> {
            final EditText inputtext = new EditText(getContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(650, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (numberInput == 0) params.addRule(RelativeLayout.BELOW, R.id.inputLayout);
            else params.addRule(RelativeLayout.BELOW, numberInput);
            inputtext.setLayoutParams(params);
            inputtext.setId(++numberInput);
            inputtext.setInputType(InputType.TYPE_CLASS_TEXT);
            inputtext.setMaxLines(1);
            inputtext.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
            inputtext.setHint("Ingredient Name");
            canvas.addView(inputtext);
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
    private void retrieveRecipesWithIngredients() {
        requestQueue = Volley.newRequestQueue(getActivity());

        final SpoonAPI spoon = new SpoonAPI();

        String cuisine = "";

        String url = spoon.getRecipeComplexURL(searchIngredient, 10, 1500, cuisine);

        System.out.println(url);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        spoon.getRecipeComplexHelper(new JSONObject(response));
                        recipeTempList = spoon.getRecipeComplex();
                        handleRecipeFragmentAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        });
        requestQueue.add(req);

    }
}
