package com.aditya.takingnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {

    /*
    writing the code below into our fireBase database console will change the parameter of accessing our database from
    Testing Beta version to Production version and it will not allow any unauthorized person from accessing our app database
    rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth!=null;
    }
  }
}
     */

    //Adding FireBase Authentication class to our splash Activity
    FirebaseAuth authentication;

    ProgressBar SplashprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //check the internet connection
        if(!isNetworkAvailable()){
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Internet Connection DETECTED!", Toast.LENGTH_SHORT).show();
        }

        //creating an Instance of Firebase Authentication System
        authentication = FirebaseAuth.getInstance();

        SplashprogressBar = findViewById(R.id.SplashprogressBar);
       // SplashprogressBar.setVisibility(View.INVISIBLE);

         //setting up our progress bar visible while saving data into our database
                SplashprogressBar.setVisibility(View.VISIBLE);
                //for setting up the colour in progress bar
        SplashprogressBar.getProgressDrawable().setColorFilter(
                Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(SplashprogressBar, "progress", 0, 100);
                progressAnimator.setDuration(2000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                //the code below will open the main Activity after 2 seconds after the splash screen is deployed
        Handler handler = new Handler();
        //now the handler class have the method post delayed that will delay the launching of our main activity
        //here Runnable is the part of the thread this is going to this is going to run for the given seconds
        /*
        So in a nutShell the code below will open our main Activity after 2 seconds
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //checking Whether the user is already Logged in or not
                //authenticated user will have his/her username and password attached to this application
                //anonymous user will have not registered his/her username and password onto this application
                // here username is emailAddress
                if(authentication.getCurrentUser()==null){

                    //create a new anonymous account
                    authentication.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(getApplicationContext(),"Logging you in with \n the Temporary Account\n Your notes will be lost\n when you close the app\n you need to sign in \n if you want to store data permanently",Toast.LENGTH_SHORT).show();
                            //here we will write the code that will open our main Activity
                            //the code here will only execute after 2 seconds i.e = delayMillis: 2000 have passed
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                            //adding animation when opening new activity
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Error! "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            finish();
                            //adding animation when opening new activity
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        }
                    });
                }
                else{
                    //here we will write the code that will open our main Activity
                    //the code here will only execute after 2 seconds i.e = delayMillis: 2000 have passed
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                    //adding animation when opening new activity
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }
            }
        },2000);

    }

    //the code below will hide the system navigation bar and system status bar for IMMERSIVE EXPERIENCE
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    //check if the device is connected or not
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        //  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        //  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        //   | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}