package my.edu.utar.uccd3223;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long doubleBackToExit;
    private static final int TIME_INTERVAL = 2000;

    private BottomNavigationView.OnNavigationItemSelectedListener buttomnavListener =
            menuItem -> {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_recipes:
                        selectedFragment = new Recipe();
                        break;

                    case R.id.nav_fridge:
                        selectedFragment = new Ingredient();
                        break;

                    case R.id.nav_mealplan:
                        selectedFragment = new MealPlan();
                        break;

                    case R.id.nav_dashboard:
                        selectedFragment = new Dashboard();
                        break;

                }

                FragmentManager fm = getSupportFragmentManager();

                fm.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            };

    private NavigationView.OnNavigationItemSelectedListener navListener = menuitem -> {

        Fragment selectedFragment = null;
        switch (menuitem.getItemId()) {

            case R.id.nav_profile: {
                selectedFragment = new MyAccount();
                break;
            }
        }
        //close navigation drawer
        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        unCheckAll(findViewById(R.id.menu));

        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.menu);
        bottomNav.setOnNavigationItemSelectedListener(buttomnavListener);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,0,1) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        actionBarDrawerToggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navListener);

        // start up recipeFragment with fragmentManager
        FragmentManager fm = this.getSupportFragmentManager();

        // create arguments to be sent to recipe fragment
        Bundle args = new Bundle();

        Bundle bundleExtras = getIntent().getExtras();
        if (bundleExtras != null) {
            if(getIntent().hasExtra("activity")){
                MyAccount myAccountFrag = new MyAccount();
                args.putString("food", bundleExtras.getString("activity"));
                myAccountFrag.setArguments(args);
                // start transaction
                fm.beginTransaction().replace(R.id.fragment_container, myAccountFrag).commit();
                unCheckAll(findViewById(R.id.menu));
            }else{
                Recipe recipeFrag = new Recipe();
                args.putString("food", bundleExtras.getString("food"));
                recipeFrag.setArguments(args);
                // start transaction
                fm.beginTransaction().replace(R.id.fragment_container, recipeFrag).commit();
            }
        }else{
            Recipe recipeFrag = new Recipe();
            // start transaction
            fm.beginTransaction().replace(R.id.fragment_container, recipeFrag).commit();
        }
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

    public static void unCheckAll(BottomNavigationView view) {
        int size = view.getMenu().size();
        for (int i = 0; i < size; i++) {
            view.getMenu().getItem(i).setChecked(false);
        }
    }

}
