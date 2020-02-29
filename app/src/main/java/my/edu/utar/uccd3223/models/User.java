package my.edu.utar.uccd3223.models;

public class User {
    private int age;
    private double weight;
    private double height;
    private int gender;
    private int weight_goal;
    private int activity_level;

    public User() {
        this.age = 0;
        this.weight = 0.0;
        this.height = 0.0;
        this.gender = 0;
        this.weight_goal = 0;
        this.activity_level = 0;
    }

    public User(int age, double weight, double height, int gender, int weight_goal, int activity_level) {
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.weight_goal = weight_goal;
        this.activity_level = activity_level;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getWeight_goal() {
        return weight_goal;
    }

    public void setWeight_goal(int weight_goal) {
        this.weight_goal = weight_goal;
    }

    public int getActivity_level() {
        return activity_level;
    }

    public void setActivity_level(int activity_level) {
        this.activity_level = activity_level;
    }
}
