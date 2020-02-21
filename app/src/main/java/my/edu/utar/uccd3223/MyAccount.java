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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyAccount extends Fragment implements View.OnClickListener {

    String age, weight, height, gender, weight_goal, activity_level;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();

        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.activity_my_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        getView().findViewById(R.id.save_button).setOnClickListener(this);


    }

    // Check which listener was called
    @Override
    public void onClick(View v) {
        System.out.println("CLICK TRIGGERED");
        switch (v.getId()) {
            // save button was clicked
            case R.id.save_button:
                EditText temp = getView().findViewById(R.id.editAge);
                age = temp.getText().toString();

                // weight age in class variable
                temp = getView().findViewById(R.id.editWeight);
                weight = temp.getText().toString();

                // save height in class variable
                temp = getView().findViewById(R.id.editHeight);
                height = temp.getText().toString();

                // save gender in class variable
                Spinner temp2 = getView().findViewById(R.id.editGender);
                gender = temp2.getSelectedItem().toString();

                // save weight_goal in class variable
                temp2 = getView().findViewById(R.id.editWeightGoal);
                weight_goal = temp2.getSelectedItem().toString();


                Spinner spin_act = getView().findViewById(R.id.editActivityLevel);
                activity_level = spin_act.getSelectedItem().toString();

                System.out.println("Changing values:" +
                        "\nAge = " + age +
                        "\nWeight = " + weight +
                        "\nHeight = " + height +
                        "\nGender = " + gender +
                        "\nWeight Goal = " + weight_goal +
                        "\nActivity Level = " + activity_level);

                String TDEE = Double.toString(calculateTDEE(weight, height, age, gender, activity_level));

                Map<String, Object> userMap = new HashMap<>();
                userMap.put("age", age);
                userMap.put("weight", weight);
                userMap.put("height", height);
                userMap.put("gender", gender);
                userMap.put("weight_goal", weight_goal);
                userMap.put("TDEE", TDEE);

                // TODO: get the variable email, similar to RegistrationInfo.java
                //getActivity().getIntent().getStringExtra("email_from_reg"); DOESNT WORK
                // get bundle arguments
                String email = "";
                Bundle bundle = this.getArguments();

        }
    }

    private double calculateTDEE(String weight, String height, String age, String gender, String activity_level) {


        double weight_kg = Integer.parseInt(weight) * 0.453592;
        double height_cm = Integer.parseInt(height) * 2.54;
        double BMR = 0;
        double TDEE = 0;

        if (gender.equals("Male")) {
            BMR = 9.99 * weight_kg + 6.25 * height_cm - 4.92 * Integer.parseInt(age) + 5;

            if (activity_level.equals("Sedentary")) {
                TDEE = BMR * 1.2;
            } else if (activity_level.equals("Lightly Active")) {
                TDEE = BMR * 1.375;
            } else if (activity_level.equals("Moderately Active")) {
                TDEE = BMR * 1.55;
            } else if (activity_level.equals("Very Active")) {
                TDEE = BMR * 1.725;
            } else if (activity_level.equals("Highly Active")) {
                TDEE = BMR * 1.9;
            }
        } else if (gender.equals("Female")) {
            BMR = 9.99 * weight_kg + 6.25 * height_cm - 5 - 4.92 * Integer.parseInt(age) - 161;
            if (activity_level.equals("Sedentary")) {
                TDEE = BMR * 1.2;
            } else if (activity_level.equals("Lightly Active")) {
                TDEE = BMR * 1.375;
            } else if (activity_level.equals("Moderately Active")) {
                TDEE = BMR * 1.55;
            } else if (activity_level.equals("Very Active")) {
                TDEE = BMR * 1.725;
            } else if (activity_level.equals("Highly Active")) {
                TDEE = BMR * 1.9;
            }
        }

        if (this.weight_goal.contains("Bulk up!")) {
            return TDEE * 1.1;
        }
        if (this.weight_goal.contains("Cut fat!")) {
            return TDEE * 0.9;
        }
        else {
            return TDEE;
        }

    }
}
