package com.example.mocher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityPlayAlone extends AppCompatActivity {

    //General
    private static final String TAG = "PLAY_ALONE_ACTIVITY";
    private static final String MODE = "Offline";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Pages
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Filter
    private CrystalRangeSeekbar mSeekBarReleaseYear;
    private CrystalRangeSeekbar mSeekBarRating;
    private TextView mTextViewReleaseYear;
    private TextView mTextViewRating;

    //Classes
    private PageHandler mPageHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        setupEverything();
    }

    private void loadCorrectPage() {
        if (mPageHandler == null) {
            mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, MODE);
        }
        mPageHandler.loadCorrectPage();
//        Integer currentPage = mSharedPreferences.getInt("Page"+MODE, 1);
//        if (currentPage == 1) {
//            loadFilter();
//            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewRateRecipesButton);
//        }
//        else if (currentPage == 2) {
//            mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
//            setupSwipePlaceholderView();
//        }
//        else {
//            loadResults();
//        }
    }

    private void setupEverything() {
        mSharedPreferences = getSharedPreferences("MocherData", Context.MODE_PRIVATE);
        mContext = getApplicationContext();

        setupPages();
        setupSeekBars();
        setupBottomNavigationBar();
        loadCorrectPage();
    }

    private void setupPages() {
        mPage1 = findViewById(R.id.page_1);
        mPage2 = findViewById(R.id.page_2);
        mPage3 = findViewById(R.id.page_3);
        mLoadScreen = findViewById(R.id.load_screen);
        mTutorial = findViewById(R.id.tutorial);
        mTutorial.setVisibility(View.GONE);
        mTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTutorial.setVisibility(View.GONE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean(MODE+"Tutorial", true);
                editor.commit();
            }
        });
    }

    private void setupSeekBars() {
        mSeekBarReleaseYear = findViewById(R.id.release_date_seek_bar);
        mTextViewReleaseYear = findViewById(R.id.release_date_text_view);

        mSeekBarRating = findViewById(R.id.rating_seek_bar);
        mTextViewRating = findViewById(R.id.rating_text_view);

        mSeekBarReleaseYear.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewReleaseYear.setText(minValue+" - "+maxValue));

        mSeekBarRating.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewRating.setText(minValue+" - "+maxValue));
    }

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.play_alone);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        return true;
                    case R.id.join_group:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityJoinGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityCreateGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }
}