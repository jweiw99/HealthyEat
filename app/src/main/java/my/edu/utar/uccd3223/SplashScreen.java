package my.edu.utar.uccd3223;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import my.edu.utar.uccd3223.Database.DatabaseQuery;
import my.edu.utar.uccd3223.models.User;

public class SplashScreen extends FragmentActivity {

    private DatabaseQuery databaseQuery = new DatabaseQuery(this);

    private View firstView;
    private View secondView;
    private View thirdView;
    private View fourthView;
    private View fifthView;
    private View sixthView;
    private TextView title;
    private TextView sub_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firstView = (View) findViewById(R.id.first_line);
        secondView = (View) findViewById(R.id.second_line);
        thirdView = (View) findViewById(R.id.third_line);
        fourthView = (View) findViewById(R.id.fourth_line);
        fifthView = (View) findViewById(R.id.fifth_line);
        sixthView = (View) findViewById(R.id.sixth_line);
        title = (TextView) findViewById(R.id.title);
        sub_title = (TextView) findViewById(R.id.sub_title);

        // Animation
        Animation fromTopAnimantion1 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_1);
        Animation fromTopAnimantion2 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_2);
        Animation fromTopAnimantion3 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_3);
        Animation fromTopAnimantion4 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_4);
        Animation fromTopAnimantion5 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_5);
        Animation fromTopAnimantion6 = AnimationUtils.loadAnimation(this, R.anim.from_top_animation_6);
        Animation middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        Animation bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Setting Animations to the elements of Splash Screen
        firstView.setAnimation(fromTopAnimantion1);
        secondView.setAnimation(fromTopAnimantion2);
        thirdView.setAnimation(fromTopAnimantion3);
        fourthView.setAnimation(fromTopAnimantion4);
        fifthView.setAnimation(fromTopAnimantion5);
        sixthView.setAnimation(fromTopAnimantion6);

        title.setAnimation(middleAnimation);
        sub_title.setAnimation(bottomAnimation);

        /// Go to Main Screen
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                User userRec = databaseQuery.getUser();
                Intent intent;
                if (userRec == null) {
                    intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                    intent.putExtra("activity", "MyAccount");
                } else {
                    intent = new Intent(getApplicationContext(),
                            MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
