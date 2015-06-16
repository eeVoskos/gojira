package com.gojira.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.gojira.R;
import com.gojira.app.GojiraApp;
import com.gojira.data.api.JiraService;
import com.gojira.data.io.SessionResponse;
import com.gojira.util.ViewUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author Stratos Theodorou
 * @version 1.0
 * @since 14/05/2015
 */
public class SplashActivity extends BaseActivity implements Callback<SessionResponse> {

    private static final long START_DELAY = 2500;
    private static final long STOP_DELAY = 2000;

    @Inject
    JiraService mService;

    @InjectView(R.id.logo)
    ImageView mLogo;

    @InjectView(R.id.description)
    TextView mDescription;

    @InjectView(R.id.root)
    View mRoot;

    AnimatorSet mStartAnimations;
    AnimatorSet mStopAnimations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GojiraApp.get(this).getGraph().inject(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        // Start animations, after views are laid out
        ViewUtils.addOnGlobalLayoutListener(mRoot, new Runnable() {
            @Override
            public void run() {
                mStartAnimations = new AnimatorSet();
                mStartAnimations.playTogether(
                        ObjectAnimator
                                .ofFloat(mDescription, "alpha", 0.0f, 1.0f)
                                .setDuration(START_DELAY),
                        ObjectAnimator
                                .ofFloat(mLogo, "alpha", 0.0f, 1.0f)
                                .setDuration(START_DELAY),
                        ObjectAnimator
                                .ofFloat(mLogo, "translationY", -(mLogo.getTop() + mLogo.getBottom()), 0)
                                .setDuration(START_DELAY / 3)
                );
                mStartAnimations.setInterpolator(new AccelerateDecelerateInterpolator());
                mStartAnimations.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mService.session(SplashActivity.this);
                    }
                });
                mStartAnimations.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mStartAnimations != null) {
            mStartAnimations.cancel();
        }
        if (mStopAnimations != null) {
            mStopAnimations.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void success(SessionResponse sessionResponse, Response response) {
        // We already have a session, start main app flow
        mStopAnimations = new AnimatorSet();
        mStopAnimations.setDuration(STOP_DELAY);
        mStopAnimations.setInterpolator(new DecelerateInterpolator());
        mStopAnimations.playTogether(
                ObjectAnimator.ofFloat(mDescription, "alpha", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(mLogo, "alpha", 1.0f, 0.0f)
        );
        mStopAnimations.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
        mStopAnimations.start();
    }

    @Override
    public void failure(RetrofitError error) {
        Timber.d(error, "Error trying to get session.");

        // Unexpected error typically means invalid endpoint, so we need to login
        if (error.getKind() == RetrofitError.Kind.UNEXPECTED) {
            mStopAnimations = new AnimatorSet();
            mStopAnimations.setDuration(STOP_DELAY);
            mStopAnimations.setInterpolator(new DecelerateInterpolator());
            mStopAnimations.playTogether(
                    ObjectAnimator.ofFloat(mDescription, "alpha", 1.0f, 0.0f),
                    ObjectAnimator.ofFloat(mLogo, "alpha", 1.0f, 0.0f)
            );
            mStopAnimations.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
            mStopAnimations.start();
        }

        // Login required in case of 401 error
        else if (error.getKind() == RetrofitError.Kind.HTTP) {
            mStopAnimations = new AnimatorSet();
            mStopAnimations.setDuration(STOP_DELAY);
            mStopAnimations.setInterpolator(new DecelerateInterpolator());
            mStopAnimations.playTogether(
                    ObjectAnimator.ofFloat(mDescription, "alpha", 1.0f, 0.0f),
                    ObjectAnimator.ofFloat(mLogo, "alpha", 1.0f, 0.0f)
            );
            mStopAnimations.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            });
            mStopAnimations.start();
        }


        // Show retry prompt in case of network error
        else if (error.getKind() == RetrofitError.Kind.NETWORK) {
            Snackbar.make(mRoot, R.string.error_network, Snackbar.LENGTH_LONG)
                    .setAction(R.string.action_retry, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mService.session(SplashActivity.this);
                        }
                    })
                    .show();
        }

        // Non-recoverable error?
        else {
            Snackbar.make(mRoot, R.string.error_unknown, Snackbar.LENGTH_LONG).show();
        }
    }
}
