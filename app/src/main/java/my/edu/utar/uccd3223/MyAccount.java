package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import my.edu.utar.uccd3223.Database.DatabaseQuery;
import my.edu.utar.uccd3223.models.Calories;
import my.edu.utar.uccd3223.models.User;

public class MyAccount extends Fragment implements View.OnClickListener {

    private DatabaseQuery databaseQuery = new DatabaseQuery(getActivity());

    private User userRec ;
    private Calories caloriesRec;

    int age = 0, gender = 0, weight_goal = 0, activity_level = 0;
    double weight = 0.0, height = 0.0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_my_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getView().findViewById(R.id.save_button).setOnClickListener(this);
        EditText temp;
        TextView temp1;
        Spinner temp2;

        userRec = databaseQuery.getUser();
        if(userRec == null){
            databaseQuery.insertUser(new User());
            userRec = databaseQuery.getUser();
        }

        caloriesRec = databaseQuery.getTodayCalories();
        if(caloriesRec == null) {
            databaseQuery.insertCalories(new Calories());
            caloriesRec = databaseQuery.getTodayCalories();
        }

        // Set age text field to age from database
        temp = getView().findViewById(R.id.editAge);
        temp.setText(String.valueOf(userRec.getAge()));

        // Set weight text field to weight from database
        temp = getView().findViewById(R.id.editWeight);
        temp.setText(String.valueOf(userRec.getWeight()));

        // Set height text field to height from database
        temp = getView().findViewById(R.id.editHeight);
        temp.setText(String.valueOf(userRec.getHeight()));

        // Set calories text field to calories from database
        temp1 = getView().findViewById(R.id.calories);
        temp1.setText(String.valueOf(caloriesRec.getMax_calories()));

        // Set gender spinner field to gender from database
        temp2 = getView().findViewById(R.id.editGender);
        int gen = userRec.getGender();
        temp2.setSelection(gen);

        // Set weight_goal spinner field to weight_gaol from database
        temp2 = getView().findViewById(R.id.editWeightGoal);
        int goal = userRec.getWeight_goal();
        temp2.setSelection(goal);

        // Set activity_level spinner field to activity_level from database
        temp2 = getView().findViewById(R.id.editActivityLevel);
        int level = userRec.getActivity_level();
        temp2.setSelection(level);

    }

    // Check which listener was called
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // save button was clicked
            case R.id.save_button:


                EditText _temp = getView().findViewById(R.id.editAge);
                String _age = _temp.getText().toString();

                _temp = getView().findViewById(R.id.editWeight);
                String _weight = _temp.getText().toString();

                _temp = getView().findViewById(R.id.editHeight);
                String _height = _temp.getText().toString();

                if((!_age.isEmpty() && !_weight.isEmpty() && !_height.isEmpty())){
                    // save age in class variable
                    age = Integer.parseInt(_age);

                    // save weight in class variable
                    weight = Double.parseDouble(_weight);

                    // save height in class variable
                    height = Double.parseDouble(_height);
                }else{
                    Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    return;
                }

                // save gender in class variable
                Spinner temp2 = getView().findViewById(R.id.editGender);
                gender = (int) temp2.getSelectedItemId();

                // save weight_goal in class variable
                temp2 = getView().findViewById(R.id.editWeightGoal);
                weight_goal = (int) temp2.getSelectedItemId();


                Spinner spin_act = getView().findViewById(R.id.editActivityLevel);
                activity_level = (int) spin_act.getSelectedItemId();

                if(age > 0 && weight > 0 && height > 0) {
                    int TDEE = (int) calculateTDEE(weight, height, age, gender, activity_level);

                    databaseQuery.updateUserInfo(new User(age,weight,height,gender,weight_goal,activity_level));
                    databaseQuery.updateCaloriesInfo(new Calories(TDEE,TDEE));

                    TextView temp1 = getView().findViewById(R.id.calories);
                    temp1.setText(String.valueOf(TDEE));

                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }

        }
    }

    private double calculateTDEE(double weight, double height, int age, int gender, int activity_level) {


        double weight_kg = weight * 0.453592;
        double height_cm = height * 2.54;
        double BMR = 0;
        double TDEE = 0;

        if (gender == 0) {
            BMR = 9.99 * weight_kg + 6.25 * height_cm - 4.92 * age + 5;

            if (activity_level == 0) {
                TDEE = BMR * 1.2;
            } else if (activity_level == 1) {
                TDEE = BMR * 1.375;
            } else if (activity_level == 2) {
                TDEE = BMR * 1.55;
            } else if (activity_level == 3) {
                TDEE = BMR * 1.725;
            } else if (activity_level == 4) {
                TDEE = BMR * 1.9;
            }
        } else if (gender == 1) {
            BMR = 9.99 * weight_kg + 6.25 * height_cm - 5 - 4.92 * age - 161;
            if (activity_level == 0) {
                TDEE = BMR * 1.2;
            } else if (activity_level == 1) {
                TDEE = BMR * 1.375;
            } else if (activity_level == 2) {
                TDEE = BMR * 1.55;
            } else if (activity_level == 3) {
                TDEE = BMR * 1.725;
            } else if (activity_level == 4) {
                TDEE = BMR * 1.9;
            }
        }

        if (this.weight_goal == 0) {
            return TDEE * 1.1;
        }
        if (this.weight_goal == 1) {
            return TDEE * 0.9;
        }
        else {
            return TDEE;
        }

    }
}
