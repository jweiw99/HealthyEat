package my.edu.utar.uccd3223.API;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.edu.utar.uccd3223.models.Ingredient;
import my.edu.utar.uccd3223.models.Nutrition;
import my.edu.utar.uccd3223.models.RecipeFull;
import my.edu.utar.uccd3223.models.RecipeTemp;

public class SpoonAPI {
    // urls for API calls
    private String url_complexRecipe = "https://api.spoonacular.com/recipes/searchComplex";
    private String url_Recipe = "https://api.spoonacular.com/recipes/search";
    private String url_getRecipeWithID1 = "https://api.spoonacular.com/recipes/";
    private String url_getRecipeWithID2 = "/information?includeNutrition=true";
    private String url_MealPlan = "https://api.spoonacular.com/mealplanner/generate";
    private String apiKey = "f31cc73187534697a9417c425e05366b";
    // API return variables
    private List<RecipeTemp> recipeComplex;
    private List<RecipeTemp> recipeSimple;
    private List<RecipeTemp> mealPlan;
    private RecipeFull recipeTaken;
    private RecipeFull recipeFull;


    // get recipeFull after helper function is finished
    public RecipeFull getRecipeFull() {
        return recipeFull;
    }

    // get receiptTaken after helper function is finished
    public RecipeFull getRecipeTaken() {
        return recipeTaken;
    }

    // get recipeSimple list of recipes when helper function is finished
    public List<RecipeTemp> getRecipe() {
        return recipeSimple;
    }

    // get mealPlan list of recipes when helper function is finished
    public List<RecipeTemp> getMeal() {
        return mealPlan;
    }

    // get recipeComplex list of recipes when helper function is finished
    public List<RecipeTemp> getRecipeComplex() {
        return recipeComplex;
    }

    // returns url query for getting a recipe's full information given ID
    public String getRecipeIDURL(String id) {
        recipeFull = new RecipeFull();
        String url_query = url_getRecipeWithID1 + id + url_getRecipeWithID2 + "&apiKey=";
        url_query += apiKey;
        return url_query;
    }

    public String getRecipeURL(String foods) {
        recipeSimple = new ArrayList<>();
        String url_query = url_Recipe + "?query=" + foods + "&apiKey=";
        url_query += apiKey;
        return url_query;
    }

    public String getMealPlanURL(String colories) {
        mealPlan = new ArrayList<>();
        String url_query = url_MealPlan + "?timeFrame=day&targetCalories=" + colories + "&apiKey=";
        url_query += apiKey;
        return url_query;
    }

    // return header key
    public String getAPIKey() {
        return apiKey;
    }

    /* returns url query for getting list of recipes (not full information, meant for displaying)
    given ingredients and number of results wanted
    url_query: adds on parameters
    result: returns a list of recipe objects
     */
    public String getRecipeComplexURL(List<String> ingredients, int number, int maxCalories, String cuisine) {
        recipeComplex = new ArrayList<>();
        String ranking = "0";
        String offset = "0";
        String limitLicense = "false";
        String instructionsRequired = "true";

        String minFat = "0";
        String minProtein = "0";
        String minCarbs = "0";
        String url_query = url_complexRecipe + "?number=" + Integer.toString(number) + "&minFat=" +
                minFat + "&maxCalories=" + maxCalories + "&minProtein=" + minProtein + "&minCarbs="
                + minCarbs + "&ranking=" + ranking + "&offset=" + offset + "&limitLicense="
                + limitLicense + "&instructionsRequired=" + instructionsRequired
                + "&cuisine=" + cuisine + "&includeIngredients=";

        // add on ingredient to url string
        if (ingredients.size() > 1) {
            url_query += ingredients.get(0);
            for (int i = 1; i < ingredients.size(); i++) {
                url_query += "%2C+" + ingredients.get(i);
            }
        } else if (ingredients.size() == 1) {
            url_query += ingredients.get(0);
        }

        url_query += "&apiKey=" + apiKey;

        return url_query;
    }

    // helper function to set up recipe object for getRecipeURL function
    public void getRecipeHelper(JSONObject response) {
        try {
            JSONArray json_recipes = response.getJSONArray("results");

            for (int j = 0; j < json_recipes.length(); ++j) {
                JSONObject recipe_object = json_recipes.getJSONObject(j);
                RecipeTemp recipe = new RecipeTemp();

                recipe.setId(Integer.toString(recipe_object.getInt("id")));
                recipe.setImage(recipe_object.getString("image"));
                recipe.setTitle(recipe_object.getString("title"));

                recipeSimple.add(recipe);
            }

        } catch (Exception e) {

        }
    }

    // helper function to set up recipe object for getRecipeIDURL function
    public void getRecipeIDHelper(JSONObject response) {
        try {
            recipeTaken = new RecipeFull();
            if (response.has("id")) {
                recipeTaken.setId(response.getInt("id"));
            }
            if (response.has("image")) {
                recipeTaken.setImage(response.getString("image"));
            }
            if (response.has("title")) {
                recipeTaken.setTitle(response.getString("title"));
            }
        } catch (Exception e) {

        }
    }

    // helper function to set up recipe object for getRecipeURL function
    public void getMealPlanHelper(JSONObject response) {
        try {
            JSONArray json_recipes = response.getJSONArray("meals");

            for (int j = 0; j < json_recipes.length(); ++j) {
                JSONObject recipe_object = json_recipes.getJSONObject(j);
                RecipeTemp recipe = new RecipeTemp();

                recipe.setId(Integer.toString(recipe_object.getInt("id")));
                recipe.setImage(recipe_object.getString("image"));
                recipe.setTitle(recipe_object.getString("title"));

                mealPlan.add(recipe);
            }

        } catch (Exception e) {

        }
    }

    // helper function to set up recipe object for getRecipeComplex function
    public void getRecipeComplexHelper(JSONObject response) {
        recipeComplex = new ArrayList<>();
        try {
            JSONArray json_recipes = response.getJSONArray("results");

            for (int j = 0; j < json_recipes.length(); ++j) {
                JSONObject recipe_object = json_recipes.getJSONObject(j);
                RecipeTemp recipe = new RecipeTemp();

                recipe.setId(Integer.toString(recipe_object.getInt("id")));
                recipe.setImage(recipe_object.getString("image"));
                Log.d("ERROR_CHECKER: ", "What's the image URL? " +
                        recipe.getImage());
                recipe.setMissedIngredientCount(recipe_object
                        .getString("missedIngredientCount"));
                recipe.setUsedIngredientCount(recipe_object
                        .getString("usedIngredientCount"));
                recipe.setTitle(recipe_object.getString("title"));

                // only included if min/max macros are set in API request call
                if (recipe_object.has("calories")) {
                    recipe.setCalories(recipe_object.getString("calories"));
                }
                if (recipe_object.has("carbs")) {
                    recipe.setCarbs(recipe_object.getString("carbs"));
                }
                if (recipe_object.has("fat")) {
                    recipe.setFat(recipe_object.getString("fat"));
                }
                if (recipe_object.has("protein")) {
                    recipe.setProtein(recipe_object.getString("protein"));
                }

                recipeComplex.add(recipe);
                Log.d("ERROR_CHECKER: ", "Were adding the result now with size: " +
                        Integer.toString(recipeComplex.size()));
            }
            Log.d("ERROR_CHECKER", "Were returning the result now with size: " +
                    Integer.toString(recipeComplex.size()));

        } catch (Exception e) {
            Log.d("ERROR_CHECKER", "Unfortunately, we've come across an error and that is: ");
            Log.d("ERROR_CHECKER", e.getMessage());
        }
    }

    // helper function for getting recipe information by ID
    public void getRecipeByIDHelper(JSONObject response) {
        try {
            if (response.has("vegetarian")) {
                recipeFull.setVegetarian(response.getBoolean("vegetarian"));
            }
            if (response.has("vegan")) {
                recipeFull.setVegan(response.getBoolean("vegan"));
            }
            if (response.has("glutenFree")) {
                recipeFull.setGlutenFree(response.getBoolean("glutenFree"));
            }
            if (response.has("dairyFree")) {
                recipeFull.setDairyFree(response.getBoolean("dairyFree"));
            }
            if (response.has("veryHealthy")) {
                recipeFull.setVeryHealthy(response.getBoolean("veryHealthy"));
            }
            if (response.has("veryPopular")) {
                recipeFull.setVeryHealthy(response.getBoolean("veryPopular"));
            }
            if (response.has("lowFodMap")) {
                recipeFull.setLowFodmap(response.getBoolean("lowFodMap"));
            }
            if (response.has("ketogenic")) {
                recipeFull.setKetogenic(response.getBoolean("ketogenic"));
            }
            if (response.has("whole30")) {
                recipeFull.setWhole30(response.getBoolean("whole30"));
            }
            if (response.has("preparationMinutes")) {
                recipeFull.setPreparationMinutes(response.getInt("preparationMinutes"));
            }
            if (response.has("cookingMinutes")) {
                recipeFull.setCookingMinutes(response.getInt("cookingMinutes"));
            }
            if (response.has("sourceUrl")) {
                recipeFull.setSourceUrl(response.getString("sourceUrl"));
            }
            if (response.has("healthScore")) {
                recipeFull.setHealthScore(response.getString("healthScore"));
            }
            if (response.has("pricePerServing")) {
                recipeFull.setPricePerServing(response.getInt("pricePerServing"));
            }
            if (response.has("title")) {
                recipeFull.setTitle(response.getString("title"));
            }
            if (response.has("readyInMinutes")) {
                recipeFull.setReadyInMinutes(response.getInt("readyInMinutes"));
            }
            if (response.has("image")) {
                recipeFull.setImage(response.getString("image"));
            }
            if (response.has("servings")) {
                recipeFull.setServings(response.getInt("servings"));
            }
            if (response.has("instructions")) {
                recipeFull.setInstructions(response.getString("instructions"));
            }
            // add cuisines, if recipe has them
            if (response.has("cuisine")) {
                JSONArray cuisines = response.getJSONArray("cuisine");

                List<String> recipeCuisines = new ArrayList<>();
                for (int i = 0; i < cuisines.length(); ++i) {
                    recipeCuisines.add(cuisines.getString(i));
                }
                recipeFull.setCuisines(recipeCuisines);
            }

            JSONArray ingredients_json = response.getJSONArray("extendedIngredients");
            List<Ingredient> fullIngredients = new ArrayList<>();
            for (int i = 0; i < ingredients_json.length(); ++i) {
                Ingredient tempIngredient = new Ingredient();
                JSONObject numIngredient = ingredients_json.getJSONObject(i);
                tempIngredient.setAmount(numIngredient.getInt("amount"));
                tempIngredient.setName(numIngredient.getString("name"));
                tempIngredient.setUnit(numIngredient.getString("unit"));

                fullIngredients.add(tempIngredient);
            }
            recipeFull.setIngredients_list(fullIngredients);


            JSONArray instructions_json = response.getJSONArray("analyzedInstructions");
            List<String> fullInstructions = new ArrayList<>();
            String tempInstructions = new String();
            instructions_json = instructions_json.getJSONObject(0).getJSONArray("steps");
            for (int i = 0; i < instructions_json.length(); ++i) {
                JSONObject stepInstructions = instructions_json.getJSONObject(i);
                tempInstructions = stepInstructions.getString("step");
                fullInstructions.add(tempInstructions);
            }
            recipeFull.setAnalyzedInstructions(fullInstructions);

            JSONObject nutritions = response.getJSONObject("nutrition");
            JSONArray nutrients = nutritions.getJSONArray("nutrients");
            Nutrition tempNutrition = new Nutrition();
            for (int i = 0; i < nutrients.length(); ++i) {
                JSONObject tempNutrient = nutrients.getJSONObject(i);

                switch (tempNutrient.getString("title")) {
                    case "Calories":
                        tempNutrition.setCalories(tempNutrient.getInt("amount"));
                    case "Fat":
                        tempNutrition.setFat(tempNutrient.getInt("amount"));
                    case "Saturated Fat":
                        tempNutrition.setSaturatedFat(tempNutrient.getInt("amount"));
                    case "Carbohydrates":
                        tempNutrition.setCarbohydrates(tempNutrient.getInt("amount"));
                    case "Sugar":
                        tempNutrition.setSugar(tempNutrient.getInt("amount"));
                    case "Cholesterol":
                        tempNutrition.setCholesterol(tempNutrient.getInt("amount"));
                    case "Protein":
                        tempNutrition.setProtein(tempNutrient.getInt("amount"));
                    case "Sodium":
                        tempNutrition.setSodium(tempNutrient.getInt("amount"));
                }
            }
            recipeFull.setNutrition(tempNutrition);
        } catch (Exception e) {
            Log.d("ERROR_CHECKER", e.getMessage());
        }
    }
}
