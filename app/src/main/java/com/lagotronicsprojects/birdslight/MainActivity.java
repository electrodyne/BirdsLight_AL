package com.lagotronicsprojects.birdslight;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

public class MainActivity extends AppCompatActivity {

    EditText num_1, num_2, num_3, num_4, num_5, num_6;
    InputMethodManager im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VectorMasterView heartVector = findViewById(R.id.heart_vector);

        PathModel outline = heartVector.getPathModelByName("outline");

        //outline.setStrokeColor(Color.parseColor("#ED4337"));

        //outline.setFillColor(Color.parseColor("#ED4337"));

        /*
        num_1 = findViewById(R.id.num_1);
        num_2 = findViewById(R.id.num_2);
        num_1.requestFocus();
        showKeyboard(this);
        num_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ( count == 1 ) {
                    //num_2.requestFocus();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            num_2.requestFocus();
                        }
                    },150);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        */

    }

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

}
