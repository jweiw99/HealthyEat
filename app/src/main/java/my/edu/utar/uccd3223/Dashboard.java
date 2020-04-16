package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.edu.utar.uccd3223.API.SpoonAPI;
import my.edu.utar.uccd3223.Database.DatabaseQuery;
import my.edu.utar.uccd3223.models.Calories;
import my.edu.utar.uccd3223.models.Food;
import my.edu.utar.uccd3223.models.RecipeFull;
import my.edu.utar.uccd3223.models.RecipeTemp;
import my.edu.utar.uccd3223.models.User;
import my.edu.utar.uccd3223.util.TDEECalculate;

public class Dashboard extends Fragment {

    private DatabaseQuery databaseQuery = new DatabaseQuery(getActivity());
    private User userRec;
    private Calories caloriesRec;
    private List<Food> foodRec;

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeAdapter recipeAdapter;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = getActivity();
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        recipeList = view.findViewById(R.id.lv_recipe_frag);

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        TextView temp;

        userRec = databaseQuery.getUser();
        if (userRec == null) {
            databaseQuery.insertUser(new User());
            userRec = databaseQuery.getUser();
        }

        caloriesRec = databaseQuery.getTodayCalories();

        if (caloriesRec == null) {
            Calories _yesdaycalories = databaseQuery.getYesterdayCalories();
            if (_yesdaycalories == null) {
                _yesdaycalories = new Calories();
                _yesdaycalories.setMax_calories((int) TDEECalculate.calculateTDEE(userRec.getWeight(), userRec.getHeight(), userRec.getAge(), userRec.getGender(), userRec.getActivity_level(), userRec.getWeight_goal()));
            } else {
                _yesdaycalories.setCalories_date(Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date().getTime())));
                _yesdaycalories.setCalories_taken(0);
            }
            databaseQuery.insertCalories(_yesdaycalories);
            caloriesRec = databaseQuery.getTodayCalories();
        }

        // Set calories text field to calories from database
        temp = getView().findViewById(R.id.totalcaloriesNo);
        temp.setText(String.valueOf(caloriesRec.getMax_calories()));

        temp = getView().findViewById(R.id.caloriestakenNo);
        temp.setText(String.valueOf(caloriesRec.getCalories_taken()));

        temp = getView().findViewById(R.id.caloriesneedNo);
        temp.setText(String.valueOf(caloriesRec.getMax_calories() - caloriesRec.getCalories_taken()));

        retrieveRecipesWithColories();

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
                String calories = recipe.getCalories();
                Integer caloriestaken = caloriesRec.getCalories_taken() - Integer.parseInt(calories);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this?");

                builder.setPositiveButton("YES", (dialog, which) -> {
                    // Do nothing but close the dialog
                    databaseQuery.deleteFoodByid(Integer.parseInt(recipeId),caloriestaken);
                    dialog.dismiss();

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    if (Build.VERSION.SDK_INT >= 26) {
                        ft.setReorderingAllowed(false);
                    }
                    ft.detach(this).attach(this).commit();
                });

                builder.setNegativeButton("NO", (dialog, which) -> {
                    // Do nothing
                    dialog.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
            });
        } else {
            RecipeTemp recipe = new RecipeTemp();
            recipe.setTitle("You din't take any food");
            recipeTempList.add(recipe);
            recipeAdapter = new RecipeAdapter(context, recipeTempList);
            recipeList.setAdapter(recipeAdapter);
        }
    }

    // call api with given id
    private void retrieveRecipesWithColories() {
        recipeTempList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());

        final SpoonAPI spoon = new SpoonAPI();

        foodRec = databaseQuery.getTodayFood();

        for (int i = 0; i < foodRec.size(); i++) {
            String url = spoon.getRecipeIDURL(String.valueOf(foodRec.get(i).getFood_api_id()));
            StringRequest req = new StringRequest(Request.Method.GET, url,
                    response -> {
                        try {
                            spoon.getRecipeIDHelper(new JSONObject(response));
                            RecipeTemp recipeTemp = new RecipeTemp();
                            RecipeFull recipeReceived = spoon.getRecipeTaken();
                            recipeTemp.setId(String.valueOf(recipeReceived.getId()));
                            recipeTemp.setImage(recipeReceived.getImage());
                            recipeTemp.setTitle(recipeReceived.getTitle());
                            recipeTemp.setCalories(String.valueOf(recipeReceived.getNutrition().getCalories()));
                            recipeTempList.add(recipeTemp);
                            handleRecipeFragmentAdapter();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {

            });
            requestQueue.add(req);
        }
    }
}
