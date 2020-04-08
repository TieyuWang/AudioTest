package com.yezi.audiotest;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.yezi.audioinfo.AudioInfoSearcher;
import com.yezi.audiotest.databinding.ActivityMainBinding;
import com.yezi.audiotest.viewmodel.UiShareViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;

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
        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        mUiShareViewModel = getAppViewModelProvider().get(UiShareViewModel.class);
        DrawerLayout drawerLayout = activityMainBinding.activityDrawerLayout;
        // drawerLayout.openDrawer(GravityCompat.START);


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mainFragment,R.id.nav_mixerTestFragment,R.id.nav_effectTestFragment
                ,R.id.nav_bluetoothTestFragment)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationView navigationView = activityMainBinding.activityLeftNavigationView;
        NavController navController = Navigation.findNavController(this,R.id.activity_fragment_first);
        NavigationUI.setupActionBarWithNavController(this,navController,mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView,navController);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,R.id.activity_fragment_first);
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
