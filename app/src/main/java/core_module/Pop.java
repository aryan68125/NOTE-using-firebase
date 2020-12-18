package core_module;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.aditya.takingnotes.MainActivity;
import com.aditya.takingnotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;

public class Pop extends Activity {
    Button editButton;
    static int editButtonFlag;
    static String stringTitle;
    static String stringContent;
    static Integer colourCodeINT;
    static String docIdString;

    Button deleteButton;
    static int deleteButtonStatus;

    //now here we want to store the newly created note inside the database of google firebase
    //so we will start by calling firebase Store
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_window);

        editButtonFlag = 0;
        deleteButtonStatus = 0;

        //now inorder to make this activity a pop up window we will set the size to be a percetage of the devices screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //if we want our activity to be 50% of our sceen size just multiply the width and height with 0.5
        getWindow().setLayout((int)(width*0.9),(int)(height*0.3));
        //but right now when our pop up activity shows up the background just goes black and we don't need that
        /*
        Add the following lines inside the Styles.xml
         <style name="AppTheme.CustomeThemeForPopActivity">
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowCloseOnTouchOutside">true</item>
    </style>

       And then go to the Android manifest file and the the code below
       <activity android:name=".Pop"
            android:theme="@style/AppTheme.CustomeThemeForPopActivity"></activity>

            this should fix the issue that we just discussed above

         */

        Intent intent = getIntent();
        stringTitle = intent.getStringExtra("title");
        stringContent = intent.getStringExtra("content");
       docIdString= intent.getStringExtra("docId");
        colourCodeINT = intent.getIntExtra("color",0);

        //setting up the edit note button that will open the edit note activity
        editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButtonFlag =1;
                //informing the editText to open the Activity
                Intent editButtonStatus = new Intent(Pop.this,MenuEditNote.class);
                startActivity(editButtonStatus);
            }
        });

        //getting the instance of FirebaseFirestore
        //before performing any operation on google's Firebase Data base servers we need to create an instance of the firebase database
        //or any other firebase products in our android application at production level or during beta testing
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();//instanciating the user object It will get the current logger in user

        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //creating a new alert dialog box to ask the user weather they truely want to delete the note or not
                new AlertDialog.Builder(Pop.this).setIcon(R.drawable.danger).setTitle("Are you sure you want to delete the note?")
                        .setMessage("The deleted notes cant be recovered!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //the docIdis returned from the main activity so we can use it to reference our note using document
                                //code to store the data inside the database if the input filed is not empty
                                //notes is our collection of notes and notes is like a container here
                                //each note will have multiple data like note title, date , owner of the note contents of the note etc.
                                //specify te collection of notes using firebase store
                                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(Pop.docIdString);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),"Note DELETED!",Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent1);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"ERROR in deleting Note!",Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent1);
                                    }
                                });
                            }
                        }).setNegativeButton("No",null).show();

            }
        });

    }

    //the code below will hide the system navigation bar and system status bar for IMMERSIVE EXPERIENCE
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
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
                        //  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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
