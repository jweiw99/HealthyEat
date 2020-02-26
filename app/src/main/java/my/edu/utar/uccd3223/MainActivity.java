package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long doubleBackToExit;
    private static final int TIME_INTERVAL = 2000;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_recipes:
                            selectedFragment = new Recipe();
                            break;

                        case R.id.nav_fridge:
                            selectedFragment = new MyAccount();
                            break;

                        case R.id.nav_account:
                            selectedFragment = new MyAccount();
                            break;

                        case R.id.nav_favorites:
                            selectedFragment = new MyAccount();
                    }

                    FragmentManager fm = getSupportFragmentManager();

                    fm.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        // start up recipeFragment with fragmentManager
        FragmentManager fm = this.getSupportFragmentManager();

        Recipe recipeFrag = new Recipe();
        // create arguments to be sent to recipe fragment
        Bundle args = new Bundle();

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            args.putString("food", bundleExtras.getString("food"));
            recipeFrag.setArguments(args);
        }
        // start transaction
        fm.beginTransaction().replace(R.id.fragment_container, recipeFrag).commit();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExit + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast toast = Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        doubleBackToExit = System.currentTimeMillis();

    }
}
