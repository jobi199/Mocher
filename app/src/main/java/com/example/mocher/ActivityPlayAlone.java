package com.example.mocher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityPlayAlone extends AppCompatActivity {

    //General
    public static final Integer MAX_RELEASE_YEAR = 2021;
    public static final Integer MIN_RELEASE_YEAR = 1980;
    public static final Integer MAX_DURATION = 200;
    public static final Integer MAX_RATING = 100;
    private static final String TAG = "PLAY_ALONE_ACTIVITY";
    private static final String MODE = "Offline";
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    //Public
    public static List<Integer> mDislikedIDs = new ArrayList<>();
    public static List<Integer> mLikedIDs = new ArrayList<>();
    public static List<Integer> mStackIDs = new ArrayList<>();
    public static List<Object> mResolvers = new ArrayList<>();

    //Classes
    private SwipePlaceHolderViewHandlerPlayAlone mSwipePlaceHolderViewHandler;
    private FilterImageViewHandler mFilterImageViewHandler;
    private FilterTextViewHandler mFilterTextViewHandler;
    private FilterSeekBarHandler mFilterSeekBarHandler;
    private FilterSpinnerHandler mFilterSpinnerHandler;
    private FilterApplier mFilterApplier;
    private SwipeHandler mSwipeHandler;

    //Pages
    private LinearLayout mLoadScreen;
    private LinearLayout mTutorial;
    private LinearLayout mPage1;
    private LinearLayout mPage2;
    private LinearLayout mPage3;

    //Filter
    private TextView mTextViewMovieHitCounter;

    private TextView mTextViewMaturity0;
    private TextView mTextViewMaturity6;
    private TextView mTextViewMaturity12;
    private TextView mTextViewMaturity16;
    private TextView mTextViewMaturity18;
    private List<TextView> mTextViewList;

    private ImageView mImageViewUSA;
    private ImageView mImageViewKorea;
    private ImageView mImageViewJapan;
    private ImageView mImageViewSpain;
    private ImageView mImageViewGermany;
    private ImageView mImageViewItaly;
    private ImageView mImageViewFrance;
    private List<ImageView> mImageViewList;

    private CrystalRangeSeekbar mSeekBarReleaseYear;
    private CrystalRangeSeekbar mSeekBarDuration;
    private CrystalRangeSeekbar mSeekBarRating;
    private TextView mTextViewReleaseYear;
    private TextView mTextViewDuration;
    private TextView mTextViewRating;

    private PowerSpinnerView mPowerSpinnerGenre;
    private PowerSpinnerView mPowerSpinnerActor;
    private PowerSpinnerView mPowerSpinnerActress;
    private PowerSpinnerView mPowerSpinnerDirector;
    private List<PowerSpinnerView> mSpinnerList;

    //Classes
    private PageHandler mPageHandler;

    //Views
    private BottomNavigationView mBottomNavigationView;
    private SwipePlaceHolderView mSwipePlaceHolderView;
    private TextView mTextViewRateMoviesButton;

    //Lists
    private List<Movie> mAllMoviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alone);

        setupEverything();
    }

    @Override
    protected void onPause() {
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveTextViewState(View v)  {
        mFilterTextViewHandler.saveTextViewState(v, mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveImageViewState(View v) {
        mFilterImageViewHandler.saveImageViewState(v, mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    public void resetFilter(View v) {
        mFilterTextViewHandler.resetTextViewStates(mTextViewList);
        mFilterImageViewHandler.resetImageViewStates(mImageViewList);
        mFilterSeekBarHandler.resetSeekBarStates(mSeekBarReleaseYear, mSeekBarDuration, mSeekBarRating);
        mFilterSpinnerHandler.resetSpinnerStates(mSpinnerList, mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
        mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        makeToast("Filter wurde zurückgesetzt");
    }

    public void resetGenreSpinner(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerGenre, "GenreSpinner", mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    public void resetActorSpinner(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerActor, "ActorSpinner", mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    public void resetActressSpinner(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerActress, "ActressSpinner", mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    public void resetDirectorSpinner(View v) {
        mFilterSpinnerHandler.resetSpinnerState(mPowerSpinnerDirector, "DirectorSpinner", mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        resetLikeDislikeList();
    }

    private void resetLikeDislikeList() {
        mLikedIDs = new ArrayList<>();
        mDislikedIDs = new ArrayList<>();
    }

    private void loadFilter() {
        mFilterTextViewHandler.loadTextViewStates(mTextViewList);
        mFilterImageViewHandler.loadImageViewStates(mImageViewList);
        mFilterSeekBarHandler.loadSeekBarStates(mSeekBarReleaseYear, mSeekBarDuration, mSeekBarRating);
        mFilterSpinnerHandler.loadSpinnerStates(mPowerSpinnerGenre, mPowerSpinnerActor, mPowerSpinnerActress, mPowerSpinnerDirector);
    }

    public void switchPage2() {
        mPageHandler.savePage(2);
        loadCorrectPage();
    }

    public void switchPage3() {
        mPageHandler.savePage(3);
        mSwipeHandler.saveLikedIndices(mLikedIDs);
        mSwipeHandler.saveDislikedIndices(mDislikedIDs);
        loadCorrectPage();
    }

    private void loadCorrectPage() {
        if (mPageHandler == null) {
            mPageHandler = new PageHandler(mPage1, mPage2, mPage3, mTutorial, mLoadScreen, mSharedPreferences, MODE);
        }
        mPageHandler.loadCorrectPage();
        Integer currentPage = mSharedPreferences.getInt("Page"+MODE, 1);
        if (currentPage == 1) {
            loadFilter();
            mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
        }
        else if (currentPage == 2) {
            mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
            setupSwipePlaceholderView();
        }
//        else {
//            loadResults();
//        }
    }

    private void setupEverything() {
        mSharedPreferences = getSharedPreferences("MocherData", Context.MODE_PRIVATE);
        mContext = getApplicationContext();
        resetSharedPreferences();

        setupPages();
        setupClasses();
        setupViews();
        setupSeekBars();
        setupSpinners();
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

    private void setupClasses() {
        mAllMoviesList = JsonLoader.loadMovies(mContext);

        mFilterTextViewHandler = new FilterTextViewHandler(mSharedPreferences, mContext, MODE);
        mFilterImageViewHandler = new FilterImageViewHandler(mSharedPreferences, mContext, MODE);
        mFilterSeekBarHandler = new FilterSeekBarHandler(mSharedPreferences, mContext, MODE);
        mFilterSpinnerHandler = new FilterSpinnerHandler(mSharedPreferences, mContext, MODE);
        mFilterApplier = new FilterApplier(mSharedPreferences, mAllMoviesList, mContext, MODE);

        mSwipeHandler = new SwipeHandler(mSharedPreferences, MODE);
        mSwipeHandler.loadLikedIndices();
        mSwipeHandler.loadDislikedIndices();
        mLikedIDs = mSwipeHandler.mLikedIDs;
        mDislikedIDs = mSwipeHandler.mDislikedIDs;
    }

    private void setupViews() {
        mTextViewMovieHitCounter = findViewById(R.id.movie_count);

        mTextViewMaturity0 = findViewById(R.id.maturity_0);
        mTextViewMaturity6 = findViewById(R.id.maturity_6);
        mTextViewMaturity12 = findViewById(R.id.maturity_12);
        mTextViewMaturity16 = findViewById(R.id.maturity_16);
        mTextViewMaturity18 = findViewById(R.id.maturity_18);
        mTextViewList = Arrays.asList(mTextViewMaturity0, mTextViewMaturity6, mTextViewMaturity12, mTextViewMaturity16, mTextViewMaturity18);

        mImageViewUSA = findViewById(R.id.united_states);
        mImageViewKorea = findViewById(R.id.korea);
        mImageViewJapan = findViewById(R.id.japan);
        mImageViewSpain = findViewById(R.id.spain);
        mImageViewGermany = findViewById(R.id.germany);
        mImageViewItaly = findViewById(R.id.italy);
        mImageViewFrance = findViewById(R.id.france);
        mImageViewList = Arrays.asList(mImageViewUSA, mImageViewKorea, mImageViewJapan, mImageViewSpain, mImageViewGermany, mImageViewItaly, mImageViewFrance);

        mTextViewRateMoviesButton = findViewById(R.id.text_view_create_group_button);
        mTextViewRateMoviesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPage2();
            }
        });
    }

    private void setupSeekBars() {
        mSeekBarReleaseYear = findViewById(R.id.release_date_seek_bar);
        mTextViewReleaseYear = findViewById(R.id.release_date_text_view);

        mSeekBarDuration = findViewById(R.id.duration_seek_bar);
        mTextViewDuration = findViewById(R.id.duration_text_view);

        mSeekBarRating = findViewById(R.id.rating_seek_bar);
        mTextViewRating = findViewById(R.id.rating_text_view);

        mSeekBarReleaseYear.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewReleaseYear.setText(minValue+" - "+maxValue));
        mSeekBarReleaseYear.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveSeekBarState("ReleaseSeekBar", mSeekBarReleaseYear, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton, MIN_RELEASE_YEAR, MAX_RELEASE_YEAR);
            resetLikeDislikeList();
        });


        mSeekBarDuration.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewDuration.setText(minValue+" - "+maxValue+" min"));
        mSeekBarDuration.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveSeekBarState("DurationSeekBar", mSeekBarDuration, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton, 0, MAX_DURATION);
            resetLikeDislikeList();
        });

        mSeekBarRating.setOnRangeSeekbarChangeListener((minValue, maxValue) -> mTextViewRating.setText(minValue+" - "+maxValue));
        mSeekBarRating.setOnRangeSeekbarFinalValueListener((minValue, maxValue) -> {
            mFilterSeekBarHandler.saveSeekBarState("RatingSeekBar", mSeekBarRating, minValue.intValue(), maxValue.intValue(), mFilterApplier, mTextViewMovieHitCounter, mTextViewRateMoviesButton, 0, MAX_RATING);
            resetLikeDislikeList();
        });
    }

    private void setupSpinners() {
        mPowerSpinnerGenre = findViewById(R.id.power_spinner_genre);
        mPowerSpinnerActor = findViewById(R.id.power_spinner_actor);
        mPowerSpinnerActress = findViewById(R.id.power_spinner_actress);
        mPowerSpinnerDirector = findViewById(R.id.power_spinner_director);
        mSpinnerList = Arrays.asList(mPowerSpinnerGenre, mPowerSpinnerActor, mPowerSpinnerActress, mPowerSpinnerDirector);

        mPowerSpinnerGenre.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("GenreSpinner", newItem, newIndex, mPowerSpinnerGenre);
            mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
            //resetLikeDislikeList();
        });

        mPowerSpinnerActor.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("ActorSpinner", newItem, newIndex, mPowerSpinnerActor);
            mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
            //resetLikeDislikeList();
        });

        mPowerSpinnerActress.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("ActressSpinner", newItem, newIndex, mPowerSpinnerActress);
            mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
            //resetLikeDislikeList();
        });

        mPowerSpinnerDirector.setOnSpinnerItemSelectedListener((OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            mFilterSpinnerHandler.saveSpinnerState("DirectorSpinner", newItem, newIndex, mPowerSpinnerDirector);
            mFilterApplier.applyFilter(mTextViewMovieHitCounter, mTextViewRateMoviesButton);
            //resetLikeDislikeList();
        });
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

    private void setupSwipePlaceholderView() {
        if (mSwipePlaceHolderViewHandler == null) {
            mSwipePlaceHolderViewHandler = new SwipePlaceHolderViewHandlerPlayAlone(mContext);
        }
        mSwipeHandler.loadSelectedIndices();
        mSwipePlaceHolderView = findViewById(R.id.swipeView);
        mSwipePlaceHolderViewHandler.setSwipePlaceHolderViewBuilder(mSwipePlaceHolderView);
        mSwipePlaceHolderViewHandler.loadSwipePlaceholderView(mSwipeHandler.mSelectedIDs, mAllMoviesList, mSwipePlaceHolderView);
        mStackIDs = mSwipePlaceHolderViewHandler.mStackIDs;
        mResolvers = mSwipePlaceHolderViewHandler.mResolvers;
        mSwipePlaceHolderView.addItemRemoveListener(new ItemRemovedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemRemoved(int count) {
                if (mSwipePlaceHolderView.getAllResolvers().size() == 0) {
                    switchPage3();
                }
            }
        });
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}