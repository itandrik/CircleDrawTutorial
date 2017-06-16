package com.example.tahajjudtutorial;

import android.animation.Animator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements
        View.OnLongClickListener, View.OnTouchListener{
    private SwitchCompat mTahajjudSwitch;
    private LinearLayout mLlSoundContainer;
    private View mSoundHider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTahajjudSwitch = (SwitchCompat) findViewById(R.id.switchTahajjudSound);
        mTahajjudSwitch.setOnLongClickListener(this);

        mLlSoundContainer = (LinearLayout) findViewById(R.id.llTahajjudSoundContainer);

        mSoundHider = findViewById(R.id.soundHiderView);
        mSoundHider.setOnTouchListener(this);
    }

    /**
     * Show container with sounds
     */
    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.switchTahajjudSound) {
            mLlSoundContainer.setAlpha(0.0f);
            mLlSoundContainer.setVisibility(View.VISIBLE);
            mSoundHider.setVisibility(View.VISIBLE);
            mLlSoundContainer.bringToFront();
            mLlSoundContainer.animate()
                    .alpha(1.0f)
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //Stub
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //Stub
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //Stub
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //Stub
                        }
                    });
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.soundHiderView){
            mLlSoundContainer.animate()
                    .alpha(0.0f)
                    .setDuration(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            //Stub
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLlSoundContainer.setVisibility(View.GONE);
                            mSoundHider.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            //Stub
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                            //Stub
                        }
                    });
        }
        return false;
    }
}
