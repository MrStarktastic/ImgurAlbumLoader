package com.example.company.imguralbumloader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Glide;

public class AlbumFragment extends Fragment implements View.OnClickListener, AlbumImageLinksFetcher.OnLinksFetchedListener {
    private static final String KEY_LAYOUT_MANAGER_STATE = "LayoutManager state";

    private RecyclerView albumList;
    private String[] imageLinks;
    private boolean showAlbumButtonClicked;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        showAlbumButtonClicked = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_album, container, false);

        final Button showAlbumButton = view.findViewById(R.id.show_album_button);
        albumList = view.findViewById(R.id.album_list_view);
        albumList.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.grid_column_count));
        albumList.setLayoutManager(layoutManager);

        if (showAlbumButtonClicked) {
            showAlbumButton.setVisibility(View.GONE);
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(KEY_LAYOUT_MANAGER_STATE));
            if (imageLinks != null) populateList(imageLinks);
        } else {
            showAlbumButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the state of the RecyclerView's LayoutManager so that the scrolling position is retained
        outState.putParcelable(KEY_LAYOUT_MANAGER_STATE, albumList.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onClick(final View v) {
        showAlbumButtonClicked = true;
        // Fade the button out while moving it down and make it "gone" after the animation ends
        v.animate().alpha(0).translationY(v.getHeight()).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                v.setVisibility(View.GONE);
            }
        });
        new AlbumImageLinksFetcher(this).fetchLinks();
    }

    @Override
    public void onLinksFetched(String[] links) {
        populateList(imageLinks = links);
    }

    private void populateList(String[] links) {
        albumList.setAdapter(new ImageAdapter(links, Glide.with(this)));
    }
}
