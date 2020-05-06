package com.yezi.audiotest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.yezi.audiotest.databinding.ActivityMainBinding;
import com.yezi.audiotest.viewmodel.UiShareViewModel;

import java.util.Arrays;


/**
 * @author : yezi
 * @date : 2020/3/26 16:36
 * desc   :
 * version: 1.0
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private UiShareViewModel mUiShareViewModel;
    private AppBarConfiguration mAppBarConfiguration;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        mUiShareViewModel = getAppViewModelProvider().get(UiShareViewModel.class);
        DrawerLayout drawerLayout = activityMainBinding.activityDrawerLayout;
        // drawerLayout.openDrawer(GravityCompat.START);
        

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_homeFragment,R.id.nav_mixerTestFragment,R.id.nav_effectTestFragment
                ,R.id.nav_bluetoothTestFragment,R.id.nav_volumeTestFragment)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationView navigationView = activityMainBinding.activityLeftNavigationView;
        navigationView.setSaveEnabled(true);
        NavController navController = Navigation.findNavController(this,R.id.fragment_layout);
        NavigationUI.setupActionBarWithNavController(this,navController,mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView,navController);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,R.id.fragment_layout);
        return NavigationUI.navigateUp(navController,mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public ViewModelProvider getAppViewModelProvider(){
        return ((AudioTestApplication)getApplication()).getAppViewModelProvider(this);
    }


    private void showLongToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void showShortToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
