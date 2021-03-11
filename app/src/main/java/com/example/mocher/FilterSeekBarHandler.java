package com.example.mocher;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

public class FilterSeekBarHandler {

    public static final Integer MAX_RELEASE_YEAR = 2021;
    public static final Integer MIN_RELEASE_YEAR = 1980;
    public static final Integer MAX_DURATION = 200;
    public static final Integer MAX_RATING = 100;

    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private String mMode;

    public FilterSeekBarHandler(SharedPreferences sharedPreferences, Context context, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mContext = context;
        this.mMode = mode;
    }

    public void saveSeekBarState(String key, CrystalRangeSeekbar seekBar, int min, int max, FilterApplier filterApplier, TextView textView, TextView button, int default_min, int default_max) {
        if (!(mSharedPreferences.getInt(key + mMode + "Min", default_min) == min) || !(mSharedPreferences.getInt(key + mMode + "Max", default_max) == max)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putInt(key + mMode + "Min", min);
            editor.putInt(key + mMode + "Max", max);
            editor.putBoolean("ChangeStatus" + mMode, true);
            editor.commit();
        }
        setSeekBarUnselected(seekBar);
        if (min > default_min || max < default_max) {
            setSeekBarSelected(seekBar);
        }
        filterApplier.applyFilter(textView, button);
    }

    public void resetSeekBarStates(CrystalRangeSeekbar seekBarRelease, CrystalRangeSeekbar seekBarDuration, CrystalRangeSeekbar seekBarRating) {
        resetSeekBarSharedPreferences();
        loadSeekBarStates(seekBarRelease, seekBarDuration, seekBarRating);
    }

    public void resetSeekBarSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt("ReleaseSeekBar" + mMode + "Min", MIN_RELEASE_YEAR);
        editor.putInt("ReleaseSeekBar" + mMode + "Max", MAX_RELEASE_YEAR);
        editor.putInt("DurationSeekBar" + mMode + "Min", 0);
        editor.putInt("DurationSeekBar" + mMode + "Max", MAX_DURATION);
        editor.putInt("RatingSeekBar" + mMode + "Min", 0);
        editor.putInt("RatingSeekBar" + mMode + "Max", MAX_RATING);
        editor.commit();
    }

    public void loadSeekBarStates(CrystalRangeSeekbar seekBarRelease, CrystalRangeSeekbar seekBarDuration, CrystalRangeSeekbar seekBarRating) {
        int min_release = mSharedPreferences.getInt("ReleaseSeekBar" + mMode + "Min", MIN_RELEASE_YEAR);
        int max_release = mSharedPreferences.getInt("ReleaseSeekBar" + mMode + "Max", MAX_RELEASE_YEAR);
        seekBarRelease.setMinStartValue(min_release);
        seekBarRelease.setMaxStartValue(max_release);
        seekBarRelease.apply();

        int min_duration = mSharedPreferences.getInt("DurationSeekBar" + mMode + "Min",0);
        int max_duration = mSharedPreferences.getInt("DurationSeekBar" + mMode + "Max", MAX_DURATION);
        seekBarDuration.setMinStartValue(min_duration);
        seekBarDuration.setMaxStartValue(max_duration);
        seekBarDuration.apply();

        int min_rating = mSharedPreferences.getInt("RatingSeekBar" + mMode + "Min",0);
        int max_rating = mSharedPreferences.getInt("RatingSeekBar" + mMode + "Max", MAX_RATING);
        seekBarRating.setMinStartValue(min_rating);
        seekBarRating.setMaxStartValue(max_rating);
        seekBarRating.apply();

        setSeekBarUnselected(seekBarRelease);
        if (min_release > MIN_RELEASE_YEAR || max_release < MAX_RELEASE_YEAR) {
            setSeekBarSelected(seekBarRelease);
        }

        setSeekBarUnselected(seekBarDuration);
        if (min_duration > 0 || max_duration < MAX_DURATION) {
            setSeekBarSelected(seekBarDuration);
        }

        setSeekBarUnselected(seekBarRating);
        if (min_rating > 0 || max_rating < MAX_RATING) {
            setSeekBarSelected(seekBarRating);
        }
    }

    private void setSeekBarSelected(CrystalRangeSeekbar seekBar) {
        seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.red));
        seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.red));
        seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.red));
    }

    private void setSeekBarUnselected(CrystalRangeSeekbar seekBar) {
        seekBar.setBarHighlightColor(ContextCompat.getColor(mContext, R.color.seek_bar_color));
        seekBar.setLeftThumbColor(ContextCompat.getColor(mContext, R.color.seek_bar_color));
        seekBar.setRightThumbColor(ContextCompat.getColor(mContext, R.color.seek_bar_color));
    }

}
