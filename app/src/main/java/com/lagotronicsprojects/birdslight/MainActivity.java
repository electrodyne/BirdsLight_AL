package com.lagotronicsprojects.birdslight;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.JsonReader;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.security.Key;
import java.security.KeyStore;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    PinLockView mPinLockView;
    //private Realm realm;
    private BirdsLightUtils bl = new BirdsLightUtils(this,"myKey","jamesianfauni");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //realm = Realm.getDefaultInstance();



        setupWindowAnimations();

        mPinLockView = findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        mIndicatorDots.setPinLength(6);
        mPinLockView.attachIndicatorDots(mIndicatorDots);



    }

    private void switchActivity(Activity context, final Intent intent, long delay) {
        class OneShotTask implements Runnable {
            Intent intent;               //Private Var
            Activity context;            //Private var
            OneShotTask(Activity c,Intent i) {    //Public var
                context = c;             //Copying Public Var to Private
                intent = i;              //Copying Public Var to Private
            }
            public void run() {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
            }
        }
        //runOnUiThread(new OneShotTask(intent));
        final Handler handler = new Handler();
        handler.postDelayed(new OneShotTask(context,intent),delay);

    }
    /*
    private void delayedExecution(Void proceedure, int delay) {
        class OneShotTask implements Runnable {
            Callable<Void> proc;
            OneShotTask(Callable<Void> p) {
                proc = p;
            }
            @Override
            public void run() {
                try {
                    proc.call();
                } catch (Exception e) {
                    Log.e("DELAYCALL",e.toString());
                }
            }
        }
        final Handler handler = new Handler();
        handler.postDelayed(new OneShotTask(proceedure),delay);
    }*/

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (bl.isCodeValid(pin,1024)) {
                bl.resetCounter(50);
                Intent i = new Intent(MainActivity.this, WifiActivity.class);
                switchActivity(MainActivity.this, i, 1000);

            }
            else {
                int currentTrials = bl.decrementCounter(40);
                mPinLockView.resetPinLockView();

                if (currentTrials > 0)
                    Toast.makeText(MainActivity.this, "Wrong Pin. You have " + currentTrials + " trials left.", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "You just hit 3 consecutive mistakes. This application shall be locked for 3 days.", Toast.LENGTH_LONG).show();
                    bl.setLock(true, 1000);
                    //delayedExecution((Callable<Void>)mPinLockView.resetPinLockView(), 3000);
                    bl.tripTimer(1024);
                    Intent i = new Intent(MainActivity.this, SplashscreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    switchActivity(MainActivity.this, i,1000);
                }
            }
        }

        @Override
        public void onEmpty() {
           // Log.e(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };

    private void setupWindowAnimations(){
        Fade fade = new Fade();
        fade.setDuration(2000);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
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


    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Do you want to exit");
        // alert.setMessage("Message");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();

    }


}
