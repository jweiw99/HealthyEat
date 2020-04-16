package my.edu.utar.uccd3223.util;

public class TDEECalculate {
    public static double calculateTDEE(double weight, double height, int age, int gender, int activity_level, int weight_goal) {

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

        if (weight_goal == 0) {
            return TDEE * 1.1;
        }
        if (weight_goal == 1) {
            return TDEE * 0.9;
        } else {
            return TDEE;
        }

    }
}
