package com.yog.androidarena;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yog.androidarena.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_libs, R.id.navigation_articles)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void setTitleAccordingToFragment(int fragmentPos)
    {
        switch (fragmentPos)
        {
            case 0:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.things_frag_title));
                break;
            case 1:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.lib_frag_title));
                break;
            case 2:
                activityMainBinding.includeToolbar.fragTitle.setText(getResources().getString(R.string.article_frag_title));
                break;

        }
    }

}
