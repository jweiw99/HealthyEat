package my.edu.utar.uccd3223;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_recipes:
                            selectedFragment = new MyAccount();
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
                    // create arguments to be sent to recipeFragment
                    Bundle args = new Bundle();
                    selectedFragment.setArguments(args);
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
    }
}
