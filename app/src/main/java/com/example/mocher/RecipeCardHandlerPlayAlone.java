package com.example.mocher;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import org.json.JSONException;

import java.util.List;

@Layout(R.layout.movie_card_view)
public class RecipeCardHandlerPlayAlone {

    @View(R.id.movie_poster)
    private ImageView moviePoster;

    private Movie mMovie;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public RecipeCardHandlerPlayAlone(Context context, Movie movie, SwipePlaceHolderView swipeView) throws JSONException {
        this.mContext = context;
        this.mMovie = movie;
        this.mSwipeView = swipeView;
    }

    @Resolve
    private void onResolved() {
        Glide.with(mContext).load(mMovie.getUrl()).into(moviePoster);
    }

    @SwipeOut
    private void onSwipedOut() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < ActivityPlayAlone.mResolvers.size(); i++) {
            if (ActivityPlayAlone.mResolvers.get(i) == tmp.get(0)) {
                ActivityPlayAlone.mDislikedIDs.add(ActivityPlayAlone.mStackIDs.get(i));
            }
        }
    }

    @SwipeCancelState
    private void onSwipeCancelState() {

    }

    @SwipeIn
    private void onSwipeIn() {
        List<Object> tmp = mSwipeView.getAllResolvers();
        for (int i = 0; i < ActivityPlayAlone.mResolvers.size(); i++) {
            if (ActivityPlayAlone.mResolvers.get(i) == tmp.get(0)) {
                ActivityPlayAlone.mLikedIDs.add(ActivityPlayAlone.mStackIDs.get(i));
            }
        }
    }

    @SwipeInState
    private void onSwipeInState() {

    }

    @SwipeOutState
    private void onSwipeOutState() {
    }

    @Click(R.id.movie_card)
    public void onProfileImageViewClick() {
    }
}
