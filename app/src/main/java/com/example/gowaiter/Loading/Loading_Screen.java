package com.example.gowaiter.Loading;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class Loading_Screen {
    private View loadingView;

    public void show(Activity activity) {
        FrameLayout layout = new FrameLayout(activity);
        layout.setBackgroundColor(Color.parseColor("#80000000")); // 50% opacity black
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        layout.setLayoutParams(layoutParams);

        ProgressBar progressBar = new ProgressBar(activity);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        pbParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(pbParams);

        layout.addView(progressBar);
        activity.addContentView(layout, layoutParams);
        loadingView = layout;
    }

    public void hide() {
        if (loadingView != null) {
            ViewGroup parent = (ViewGroup) loadingView.getParent();
            if (parent != null) {
                parent.removeView(loadingView);
            }
            loadingView = null;
        }
    }
}
