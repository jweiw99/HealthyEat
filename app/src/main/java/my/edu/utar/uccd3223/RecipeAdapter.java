package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import my.edu.utar.uccd3223.models.RecipeTemp;

public class RecipeAdapter extends ArrayAdapter<RecipeTemp> {

    private List<RecipeTemp> recipeList;
    private Context context;
    private LayoutInflater mInflater;

    // Constructors
    public RecipeAdapter(Context context, List<RecipeTemp> objects) {
        super(context, 0, objects);
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        recipeList = objects;
    }

    @Override
    public RecipeTemp getItem(int position) {
        return recipeList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final RecipeAdapter.ViewHolder vh;
        if (convertView == null) {
            View view = mInflater.inflate(R.layout.arrayadapter_recipe_row, parent, false);
            vh = RecipeAdapter.ViewHolder.create((RelativeLayout) view);
            view.setTag(vh);
        } else {
            vh = (RecipeAdapter.ViewHolder) convertView.getTag();
        }

        RecipeTemp recipe = getItem(position);

        if (recipe.getCalories() != null && recipe.getCarbs() != null && recipe.getProtein() != null) {
            String info = "Calories: " + recipe.getCalories() + " Carbs: " + recipe.getCarbs() +
                    " Protein: " + recipe.getProtein() + " Fat: " + recipe.getFat();
            vh.textViewInformation.setText(info);
        } else if (recipe.getCalories() != null) {
            String info = "Calories: " + recipe.getCalories();
            vh.textViewInformation.setText(info);
        }

        vh.textViewTitle.setText(recipe.getTitle());

        if (recipe.getId() != "999999999") {
            String photoURL = "https://spoonacular.com/recipeImages/" + recipe.getId() + "-90x90.jpg";
            Picasso.with(context).load(photoURL).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(vh.imageView);
        } else {
            Picasso.with(context).load(R.mipmap.ic_launcher).fit().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(vh.imageView);
        }
        return vh.rootView;
    }

    private static class ViewHolder {
        public final RelativeLayout rootView;
        public final ImageView imageView;
        public final TextView textViewTitle;
        public final TextView textViewInformation;

        private ViewHolder(RelativeLayout rootView, ImageView imageView, TextView textViewName,
                           TextView textViewEmail) {
            this.rootView = rootView;
            this.imageView = imageView;
            this.textViewTitle = textViewName;
            this.textViewInformation = textViewEmail;
        }

        public static RecipeAdapter.ViewHolder create(RelativeLayout rootView) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_recipe_results);
            TextView textViewName = (TextView) rootView.findViewById(R.id.tv_recipeFrag_title);
            TextView textViewEmail = (TextView) rootView.findViewById(R.id.tv_recipeFrag_info);
            return new RecipeAdapter.ViewHolder(rootView, imageView, textViewName, textViewEmail);
        }
    }
}
