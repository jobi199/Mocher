package com.example.mocher;

import android.content.SharedPreferences;

import com.example.mocher.Movie;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SwipeHandler {

    private SharedPreferences mSharedPreferences;
    private String mMode;

    public List<Integer> mSelectedIDs;
    public List<Integer> mLikedIDs;
    public List<Integer> mDislikedIDs;
    public ArrayList<Movie> mOfflineResults;
    public ArrayList<Movie> mOnlineResults;

    public SwipeHandler(SharedPreferences sharedPreferences, String mode) {
        this.mSharedPreferences = sharedPreferences;
        this.mMode = mode;
    }

    public void loadSelectedIndices() {
        mSelectedIDs = new ArrayList<>();
        String tmp1 = mSharedPreferences.getString("Selected" + mMode + "IDs", "");
        if (!tmp1.equals("")) {
            String[] tmp2 = tmp1.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mSelectedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void loadLikedIndices() {
        mLikedIDs = new ArrayList<>();
        String tmp = mSharedPreferences.getString("Liked" + mMode + "IDs", "");
        if (!tmp.equals("")) {
            String[] tmp2 = tmp.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mLikedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void loadDislikedIndices() {
        mDislikedIDs = new ArrayList<>();
        String tmp = mSharedPreferences.getString("Disliked" + mMode + "IDs", "");
        if (!tmp.equals("")) {
            String[] tmp2 = tmp.split("#");
            for (int i = 0; i < tmp2.length; i++) {
                mDislikedIDs.add(Integer.parseInt(tmp2[i]));
            }
        }
    }

    public void saveLikedIndices(List<Integer> liked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String tmp = "";
        if (liked != null && liked.size() > 0) {
            tmp = String.valueOf(liked.get(0));
            for (int i = 1; i < liked.size(); i++) {
                tmp = tmp + "#" + liked.get(i);
            }
        }
        editor.putString("Liked" + mMode + "IDs", tmp);
        editor.commit();
    }

    public void saveDislikedIndices(List<Integer> disliked) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String tmp = "";
        if (disliked != null && disliked.size() > 0) {
            tmp = String.valueOf(disliked.get(0));
            for (int i = 1; i < disliked.size(); i++) {
                tmp = tmp + "#" + disliked.get(i);
            }
        }
        editor.putString("Disliked" + mMode + "IDs", tmp);
        editor.commit();
    }

    public void loadOfflineResults(List<Movie> movies) {
        mOfflineResults = new ArrayList<>();
        loadLikedIndices();
        loadDislikedIndices();
        for (int i = 0; i < mLikedIDs.size(); i++) {
            Movie movie = movies.get(mLikedIDs.get(i));
            movie.setScore(1);
            mOfflineResults.add(movie);
        }
        for (int i = 0; i < mDislikedIDs.size(); i++) {
            Movie movie = movies.get(mDislikedIDs.get(i));
            movie.setScore(0);
            mOfflineResults.add(movie);
        }
    }

    public void loadOnlineResults(DataSnapshot dataSnapshot, List<Movie> movies, String prefCode) {
        String code = mSharedPreferences.getString(prefCode, "");
        List<String> counter = (List<String>) dataSnapshot.child(code).child("counter").getValue();
        List<String> selectedIDs = (List<String>) dataSnapshot.child(code).child("selected_ids").getValue();
        String peopleNumber = (String) dataSnapshot.child(code).child("people_number").getValue();
        calculateOnlineStandings(counter, selectedIDs, peopleNumber, movies);
    }

    private void calculateOnlineStandings(List<String> count, List<String> ids, String maxVotes, List<Movie> movies) {
        mOnlineResults = new ArrayList<>();
        for (int i = Integer.parseInt(maxVotes); i > -1; i--) {
            for (int y = 0; y < ids.size(); y++) {
                if (Integer.parseInt(count.get(y)) == i) {
                    Movie movie = movies.get(Integer.parseInt(ids.get(y)));
                    movie.setScore(i);
                    mOnlineResults.add(movie);
                }
            }
        }
    }
}
