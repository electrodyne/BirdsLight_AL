package com.lagotronicsprojects.birdslight;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.richpath.RichPath;
import com.richpath.RichPathView;
import com.richpathanimator.RichPathAnimator;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

import static java.lang.System.currentTimeMillis;

public class SplashscreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        //LOAD EVERYTHING HERE. (GLOBALS AND SERVICES)
        //TRANSFER TO NEXT ACTIVITY WHEN DONE.

        AVLoadingIndicatorView avloading = findViewById(R.id.avi);
        requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,BirdsLightUtils.WRITE_PERMISSION);

        setupWindowAnimations();

        BirdsLightUtils bl = new BirdsLightUtils(SplashscreenActivity.this, "myKey", "jamesianfauni");

        /* MASTER'S CODE */
        //bl.addCode("123456", 255);
        bl.setCounter(3, 30);
        bl.setLock(false,30);
        Log.e("LOG",bl.getContent(1024));
        /* *********************** */

         //long current_time = System.currentTimeMillis();
        //Log.e("LOG", Objects.toString(current_time,null));

        if (bl.isLockedOut(30)) {


            avloading.setVisibility(View.INVISIBLE);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();

           alert.setCancelable(false);
           View dialogView = inflater.inflate(R.layout.dialog_lockout,null);
           alert.setView(dialogView);

            TextView dialog_notif = dialogView.findViewById(R.id.dialog_notif);
            dialog_notif.setText("Sorry, your application was locked out. Try again after " + bl.getRemainingTime(1024));

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Your action here
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            alert.show();
        }
        else {
            Intent i = new Intent(this, MainActivity.class);
            switchActivity(this,i,3000);
        }
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



    private void setupWindowAnimations() {
       // Slide slide = new Slide();
        Fade fade = new Fade();
        //slide.setDuration(2000);
        fade.setDuration(2000);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
    }

    @Override
    public void onBackPressed() {

    }

    private void requestPermission(Activity context, String permission, int requestCode) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    permission)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(context,
                        new String[]{permission},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            //return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case BirdsLightUtils.WRITE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}
