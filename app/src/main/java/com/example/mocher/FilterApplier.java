package com.example.mocher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FilterApplier {

    private static final String[] MATURITIES = {"0", "6", "12", "16", "18"};
    private static final String[] COUNTRIES = {"Englisch", "Koreanisch", "Japanisch", "Spanisch", "Deutsch", "Italienisch", "Französisch"};
    public static final Integer MAX_MOVIES = 10;
    public static final Integer MAX_RELEASE_YEAR = 2021;
    public static final Integer MIN_RELEASE_YEAR = 1980;
    public static final Integer MAX_DURATION = 200;
    public static final Integer MAX_RATING = 100;

    private SharedPreferences mSharedPreferences;
    private List<Movie> mAllMoviesList;
    private Context mContext;
    private String mMode;

    private List<Integer> mFilteredIDs;
    private List<Integer> mSelectedIDs;

    public FilterApplier(SharedPreferences sharedPreferences, List<Movie> allMoviesList, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mAllMoviesList = allMoviesList;
        this.mContext = context;
        this.mMode = mode;
    }

    public void applyFilter(TextView textView, TextView button) {

        mFilteredIDs = new ArrayList<>();
        List<String> mMaturityFilter = getFilterValues(MATURITIES);
        List<String> mCountryFilter = getFilterValues(COUNTRIES);

        for (int i = 0; i < mAllMoviesList.size(); i++) {

            Movie movie = mAllMoviesList.get(i);
            Boolean status = true;

            if (mMaturityFilter.size() > 0) {
                status = checkMaturity(movie, mMaturityFilter);
            }
            if (status && mCountryFilter.size() > 0) {
                status = checkCountry(movie, mCountryFilter);
            }
            if (status && mSharedPreferences.getInt("GenreSpinner" + mMode + "Index", -1) != -1) {
                status = checkGenre(movie);
            }
            if (status && mSharedPreferences.getInt("ActorSpinner" + mMode + "Index", -1) != -1) {
                status = checkActor(movie);
            }
            if (status && mSharedPreferences.getInt("ActressSpinner" + mMode + "Index", -1) != -1) {
                status = checkActress(movie);
            }
            if (status && mSharedPreferences.getInt("DirectorSpinner" + mMode + "Index", -1) != -1) {
                status = checkDirector(movie);
            }
            if (status) {
                status = checkReleaseYear(movie);
            }
            if (status) {
                status = checkDuration(movie);
            }
            if (status) {
                status = checkRating(movie);
            }

            if (status) {
                mFilteredIDs.add(movie.getIndex());
            }
        }
        saveFilteredIDs();
        if (mSharedPreferences.getBoolean("ChangeStatus"+mMode, false) || mSharedPreferences.getString("Selected"+mMode+"IDs","").equals("")) {
            selectIndices(mFilteredIDs);
            deleteLikedIDs();
            deleteDislikedIDs();
        }
        int size = mFilteredIDs.size();
        if (size < 3) {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            button.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            button.setText("Filter anpassen");
            if (mMode.equals("Online")) {
                makeToast("Damit eine Gruppe erstellt werden kann muss es mindestens 3 Filter Treffer geben");
            }
            else {
                makeToast("Es muss mindestens 3 Filter Treffer geben, damit du swipen kannst");
            }
            button.setClickable(false);
        }
        else {
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            if (mMode.equals("Online")) {
                button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                button.setText("Gruppe erstellen");
            }
            else {
                button.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                button.setText("Swipen");
            }
            button.setClickable(true);
        }
        textView.setText(size + " Filme gefunden");
    }

    private Boolean checkMaturity(Movie movie, List<String> values) {
        String maturity = movie.getMaturity();
        if (!values.contains(maturity)) {
            return false;
        }
        return true;
    }

    private Boolean checkCountry(Movie movie, List<String> values) {
        String country = movie.getCountry();
        if (!values.contains(country)) {
            return false;
        }
        return true;
    }

    private Boolean checkGenre(Movie movie) {
        String[] genres = movie.getGenres();
        if (!Arrays.asList(genres).contains(mSharedPreferences.getString("GenreSpinner" + mMode + "Value", ""))) {
            return false;
        }
        return true;
    }

    private Boolean checkActor(Movie movie) {
        String[] genres = movie.getActors();
        if (!Arrays.asList(genres).contains(mSharedPreferences.getString("ActorSpinner" + mMode + "Value", ""))) {
            return false;
        }
        return true;
    }

    private Boolean checkActress(Movie movie) {
        String[] genres = movie.getActors();
        if (!Arrays.asList(genres).contains(mSharedPreferences.getString("ActressSpinner" + mMode + "Value", ""))) {
            return false;
        }
        return true;
    }

    private Boolean checkDirector(Movie movie) {
        String[] genres = movie.getDirectors();
        if (!Arrays.asList(genres).contains(mSharedPreferences.getString("DirectorSpinner" + mMode + "Value", ""))) {
            return false;
        }
        return true;
    }

    private Boolean checkReleaseYear(Movie movie) {
        int releaseSeekBarMin = mSharedPreferences.getInt("ReleaseSeekBar" + mMode + "Min", MIN_RELEASE_YEAR);
        int releaseSeekBarMax = mSharedPreferences.getInt("ReleaseSeekBar" + mMode + "Max", MAX_RELEASE_YEAR);
        int release_year = movie.getRelease2();
        if (release_year < releaseSeekBarMin || release_year > releaseSeekBarMax) {
            return false;
        }
        return true;
    }

    private Boolean checkDuration(Movie movie) {
        int durationSeekBarMin = mSharedPreferences.getInt("DurationSeekBar" + mMode + "Min", 0);
        int durationSeekBarMax = mSharedPreferences.getInt("DurationSeekBar" + mMode + "Max", MAX_DURATION);
        int duration = movie.getDuration2();
        if (duration < durationSeekBarMin || duration > durationSeekBarMax) {
            return false;
        }
        return true;
    }

    private Boolean checkRating(Movie movie) {
        int ratingSeekBarMin = mSharedPreferences.getInt("RatingSeekBar" + mMode + "Min", 0);
        int ratingSeekBarMax = mSharedPreferences.getInt("RatingSeekBar" + mMode + "Max", MAX_RATING);
        int rating = movie.getRating();
        if (rating < ratingSeekBarMin || rating > ratingSeekBarMax) {
            return false;
        }
        return true;
    }

    private List<String> getFilterValues(String[] values) {
        List<String> tmp = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            if (mSharedPreferences.getBoolean(values[i]+mMode, false)) {
                tmp.add(values[i]);
            }
        }
        return tmp;
    }

    private void saveFilteredIDs() {
        String tmp = "";
        if (mFilteredIDs.size() > 0) {
            tmp = String.valueOf(mFilteredIDs.get(0));
            for (int i = 1; i < mFilteredIDs.size(); i++) {
                tmp = tmp + "#" + mFilteredIDs.get(i);
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Filtered" + mMode + "IDs", tmp);
        editor.commit();
    }

    private void selectIndices(List<Integer> filteredIDs) {
        mSelectedIDs = new ArrayList<>();
        if (MAX_MOVIES < filteredIDs.size()) {
            Random random = new Random();
            while (mSelectedIDs.size() < MAX_MOVIES) {
                int randomInt = random.nextInt(filteredIDs.size());
                if (!mSelectedIDs.contains(filteredIDs.get(randomInt))) {
                    mSelectedIDs.add(filteredIDs.get(randomInt));
                }
            }
        }
        else {
            for (int i = 0; i < filteredIDs.size(); i++) {
                mSelectedIDs.add(filteredIDs.get(i));
            }
        }
        saveSelectedIndices();
        setChangeStatusFalse();
    }

    private void saveSelectedIndices() {
        String tmp = "";
        if (mSelectedIDs.size() > 0) {
            tmp = String.valueOf(mSelectedIDs.get(0));
            for (int i = 1; i < mSelectedIDs.size(); i++) {
                tmp = tmp + "#" + mSelectedIDs.get(i);
            }
        }
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Selected"+mMode+"IDs", tmp);
        editor.commit();
    }

    private void setChangeStatusFalse() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean("ChangeStatus"+mMode, false);
        editor.commit();
    }

    private void deleteLikedIDs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Liked"+mMode+"IDs", "");
        editor.commit();
    }

    private void deleteDislikedIDs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("Disliked"+mMode+"IDs", "");
        editor.commit();
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setGravity(Gravity.CENTER);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
