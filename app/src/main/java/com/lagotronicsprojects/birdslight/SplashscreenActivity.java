package com.lagotronicsprojects.birdslight;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

public class SplashscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);



        View img = findViewById(R.id.heart_vector);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RichPathView img = findViewById(R.id.heart_vector);
                RichPath outline = img.findRichPathByName("outline");
                RichPathAnimator.animate(outline)
                        .interpolator(new AccelerateDecelerateInterpolator())
                        .trimPathEnd(0,1)
                        .duration(4000)
                        .andAnimate(outline)
                        .strokeColor(Color.rgb(0x5D,0x5D,0x5D),Color.blue(255), Color.rgb(0x5D,0x5D,0x5D))
                        .duration(4000)
                        .startDelay(50)

                        .start();
            }
        });

    }

}
