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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityCreateGroup extends AppCompatActivity {

    //General
    private static final String TAG = "CREATE_GROUP_ACTIVITY";
    private static final String MODE = "Online";
    private static final String PLACEHOLDER_1_TEXT = "Du hast alle Filme bewertet und deine Daten wurden erfolgreich hochgeladen.\nDie Abstimmung läuft aktuell noch. Du kannst sie jederzeit durch einen Klick auf 'Abstimmung beenden' schließen.\nDu solltest das jedoch erst dann machen, wenn alle Teilnehmer ihre Stimme abgegeben haben.\nAnschließend werden die Ergebnisse hier veröffentlicht.";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Pages
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Classes
    private PageHandler mPageHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

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
//            mFilterApplier.applyFilter(mTextViewRecipeHitCounter, mTextViewCreateGroupButton);
//        }
//        else if (currentPage == 2) {
//            mSwipePlaceHolderViewHandlerCreateGroup = new SwipePlaceHolderViewHandlerCreateGroup(mContext);
//            setupSwipePlaceholderView();
//        }
    }

    private void setupEverything() {
        mSharedPreferences = getSharedPreferences("MocherData", Context.MODE_PRIVATE);
        mContext = getApplicationContext();

        setupPages();
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

    private void setupBottomNavigationBar() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setSelectedItemId(R.id.create_group);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.play_alone:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityPlayAlone.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.join_group:
                        mPageHandler.showLoadScreen();
                        startActivity(new Intent(getApplicationContext(), ActivityJoinGroup.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.create_group:
                        return true;
                }
                return false;
            }
        });
    }
}