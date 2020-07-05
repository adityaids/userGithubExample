package com.aditya.usergithub.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.aditya.usergithub.R;
import com.aditya.usergithub.broadcast.ReminderBroadcast;
import com.aditya.usergithub.model.FavoritModel;
import com.aditya.usergithub.model.User;
import com.aditya.usergithub.model.UserDetail;
import com.aditya.usergithub.preference.AppPreference;
import com.aditya.usergithub.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;
import com.takusemba.spotlight.CustomTarget;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv_empty;
    private ProgressBar pgsBar;
    private RecyclerView rv;
    private MainViewModel mainViewModel;
    private UserAdapter userAdapter;
    private RecyclerTouchListener onTouchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchView searchView = findViewById(R.id.search_user);
        rv = findViewById(R.id.rv_user);
        tv_empty = findViewById(R.id.tv_empty);
        pgsBar = findViewById(R.id.progress_bar);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        checkOnFirst();

        userAdapter = new UserAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        userAdapter.notifyDataSetChanged();
        rv.setAdapter(userAdapter);

        mainViewModel.getFavorit().observe(this, new Observer<List<FavoritModel>>() {
            @Override
            public void onChanged(List<FavoritModel> favoritModels) {
                mainViewModel.setFavoritModel(favoritModels);
            }
        });

        onTouchListener = new RecyclerTouchListener(this, rv);
        onTouchListener
                .setSwipeOptionViews(R.id.swipe_favorit)
                .setSwipeable(R.id.fg, R.id.bg, new RecyclerTouchListener.OnSwipeOptionsClickListener() {
                    @Override
                    public void onSwipeOptionClicked(int viewID, int position) {
                        if (viewID == R.id.swipe_favorit) {
                            boolean isFavorited = mainViewModel.checkFavorit(position);
                            if (!isFavorited) {
                                mainViewModel.insert(position);
                                showSnackBarMessage(getString(R.string.is_added));
                            } else {
                                mainViewModel.delete(position);
                                showSnackBarMessage(getString(R.string.is_remove));
                            }
                        }
                    }
                });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tv_empty.setVisibility(View.GONE);
                showLoading(true);
                mainViewModel.setSearchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mainViewModel.getListUser().observe(this, new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                if (users == null) {
                    showLoading(false);
                    tv_empty.setText(R.string.user_not_found);
                } else {
                    showLoading(false);
                    tv_empty.setVisibility(View.GONE);
                    userAdapter.setData(users);
                    userAdapter.notifyDataSetChanged();
                    rv.animate().alpha(1).setDuration(900).setStartDelay(300);
                }
            }
        });

        userAdapter.setOnItemClickCallBack(new UserAdapter.OnItemClickCallBack() {
            @Override
            public void onItemClicked(User data) {
                showLoading(true);
                mainViewModel.setDataUser(data.getDetailUrl());
            }
        });

        mainViewModel.getDetailUser().observe(this, new Observer<UserDetail>() {
            @Override
            public void onChanged(UserDetail userDetail) {
                showLoading(false);
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra(DetailActivity.EXTRA_USER_DETAIL, userDetail);
                startActivity(detailIntent);
            }
        });
    }

    private void checkOnFirst() {
        AppPreference appPreference = new AppPreference(this);
        Boolean firstRun = appPreference.getFirstRun();

        if (firstRun) {
            ReminderBroadcast reminderBroadcast = new ReminderBroadcast();
            reminderBroadcast.setReminder(this, getString(R.string.app_name), getString(R.string.check_new));
            appPreference.setFirstRun(false);
            showSpotLight();
        }
    }

    private void showSpotLight() {

        SimpleTarget simpleTarget = new SimpleTarget.Builder(this)
                .setPoint(535, 280) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(640f) // radius of the Target
                .setTitle(getString(R.string.added_new_feature)) // title
                .setDescription(getString(R.string.let_me_show_you)) // description
                .build();

        SimpleTarget simpleTarget1 = new SimpleTarget.Builder(this)
                .setPoint(535, 280) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(640f) // radius of the Target
                .setTitle("Its easy") // title
                .setDescription("Search Someone and swipe left to see a button favorit") // description
                .build();

        SimpleTarget simpleTarget2 = new SimpleTarget.Builder(this)
                .setPoint(535, 280) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(640f) // radius of the Target
                .setTitle("Favorit") // title
                .setDescription("Saat anda melihat tombol love anda dapat menambahkan user ke favorit") // description
                .build();

        SimpleTarget simpleTarget3 = new SimpleTarget.Builder(this)
                .setPoint(900, 130) // position of the Target. setPoint(Point point), setPoint(View view) will work too.
                .setRadius(50f) // radius of the Target
                .setTitle("Favorit") // title
                .setDescription("Setelah Menambahkan user anda dapat melihat nya dengan menekan menu favorit") // description
                .build();

        Spotlight.with(MainActivity.this)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(simpleTarget, simpleTarget1, simpleTarget2, simpleTarget3)
                .start();
    }

    private void showLoading(Boolean state) {
        if (state) {
            pgsBar.setVisibility(View.VISIBLE);
        } else {
            pgsBar.setVisibility(View.GONE);
        }
    }

    private void showSnackBarMessage(String message){
        Snackbar.make(rv, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting_menu:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.favorit_menu:
                intent = new Intent(MainActivity.this, FavoritActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        rv.removeOnItemTouchListener(onTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        rv.addOnItemTouchListener(onTouchListener);
    }
}
