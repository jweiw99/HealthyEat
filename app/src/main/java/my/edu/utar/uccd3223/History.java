package my.edu.utar.uccd3223;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.edu.utar.uccd3223.Database.DatabaseQuery;
import my.edu.utar.uccd3223.models.Calories;
import my.edu.utar.uccd3223.models.Food;
import my.edu.utar.uccd3223.models.RecipeTemp;
import my.edu.utar.uccd3223.models.User;
import my.edu.utar.uccd3223.util.TDEECalculate;

public class History extends Fragment {

    private DatabaseQuery databaseQuery = new DatabaseQuery(getActivity());
    private User userRec;
    private Calories caloriesRec;
    private List<Food> foodRec;
    String year;
    String month;
    String day;

    private List<RecipeTemp> recipeTempList;
    private ListView recipeList;
    private RecipeAdapter recipeAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_history, container, false);

        recipeList = view.findViewById(R.id.lv_recipe_frag);

        if (this.getArguments() != null) {
            if (!this.getArguments().getString("year").isEmpty()) {
                year = this.getArguments().getString("year");
                month = this.getArguments().getString("month");
                day = this.getArguments().getString("day");
                TextView dateView = view.findViewById(R.id.searchText);
                dateView.setText(year + "-" + month + "-" + day);
            } else {
                year = new SimpleDateFormat("yyyy").format(new Date().getTime());
                month = new SimpleDateFormat("MM").format(new Date().getTime());
                day = new SimpleDateFormat("dd").format(new Date().getTime());
            }
        } else {
            year = new SimpleDateFormat("yyyy").format(new Date().getTime());
            month = new SimpleDateFormat("MM").format(new Date().getTime());
            day = new SimpleDateFormat("dd").format(new Date().getTime());
        }

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        TextView temp;

        // set it up to get user inputs
        ImageButton searchButton = view.findViewById(R.id.searchButton);

        userRec = databaseQuery.getUser();
        if (userRec == null) {
            databaseQuery.insertUser(new User());
            userRec = databaseQuery.getUser();
        }

        caloriesRec = databaseQuery.getHistoryCalories(year, month, day);

        if (caloriesRec == null) {
            caloriesRec = new Calories();
            caloriesRec.setMax_calories(0);
            caloriesRec.setCalories_taken(0);
        }

        // Set calories text field to calories from database
        temp = getView().findViewById(R.id.totalcaloriesNo);
        temp.setText(String.valueOf(caloriesRec.getMax_calories()));

        temp = getView().findViewById(R.id.caloriestakenNo);
        temp.setText(String.valueOf(caloriesRec.getCalories_taken()));

        temp = getView().findViewById(R.id.caloriesneedNo);
        temp.setText(String.valueOf(caloriesRec.getMax_calories() - caloriesRec.getCalories_taken()));

        searchButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    myDateListener, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
            datePickerDialog.show();
        });
        retrieveRecipesWithColories(year, month, day);
    }

    private DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
        // TODO Auto-generated method stub
        // arg1 = year
        // arg2 = month
        // arg3 = day
        String _month;
        if ((arg2 + 1) < 10) {
            _month = "0" + String.valueOf(arg2 + 1);
        } else {
            _month = String.valueOf(arg2 + 1);
        }
        Bundle args = new Bundle();
        args.putString("year", String.valueOf(arg1));
        args.putString("month", _month);
        args.putString("day", String.valueOf(arg3));

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        currentFragment.setArguments(args);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(currentFragment).attach(currentFragment).commit();
    };


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
                if(!recipeId.contains("999999999")){
                    Bundle args = new Bundle();
                    args.putString("recipeId", recipeId);
                    startActivity(new Intent(getActivity(), RecipeInformation.class)
                            .putExtras(args));
                }else{
                    Toast.makeText(context, "Custom Food, no Information", Toast.LENGTH_SHORT).show();
                }

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
    private void retrieveRecipesWithColories(String year, String month, String day) {
        recipeTempList = new ArrayList<>();

        foodRec = databaseQuery.getHistoryFood(year, month, day);

        for (int i = 0; i < foodRec.size(); i++) {
            RecipeTemp recipeTemp = new RecipeTemp();
            recipeTemp.setdbId(String.valueOf(foodRec.get(i).getFood_id()));
            recipeTemp.setId(String.valueOf(foodRec.get(i).getFood_api_id()));
            recipeTemp.setImage(foodRec.get(i).getFood_image());
            recipeTemp.setTitle(foodRec.get(i).getFood_title());
            recipeTemp.setCalories(String.valueOf(foodRec.get(i).getFood_calories()));
            recipeTempList.add(recipeTemp);
            handleRecipeFragmentAdapter();
        }
    }
}