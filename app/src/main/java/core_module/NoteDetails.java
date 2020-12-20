package core_module;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.aditya.takingnotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class NoteDetails extends AppCompatActivity {

    TextView contentTextView;
    TextView titleTextView;
    Intent intent;
   static String docId;

    //setting up text to speech listener
    TextToSpeech mtts;
    int everythingIsOKmttsIsGoodToGo = 0;

    //creating a variable to store data while writing
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    String save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentTextView = findViewById(R.id.noteDetailsContent);
        // setMovementMethod(new ScrollingMovementMethod()); it will enable the scrolling of our textView
        contentTextView.setMovementMethod(new ScrollingMovementMethod());
        titleTextView = findViewById(R.id.noteDetailsTitle);

        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting the data passed from the main activity to the note details class activity
        intent = getIntent();

        contentTextView.setText(intent.getStringExtra("content"));
        titleTextView.setText(intent.getStringExtra("title"));
        contentTextView.setBackgroundColor(intent.getIntExtra("color",0));
        docId = intent.getStringExtra("docId");
        Log.i("notedetailsnoteId", docId);

        FloatingActionButton fab = findViewById(R.id.save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent2 = new Intent(getApplicationContext(),EditNote.class);
               intent2.putExtra("contentTextView",contentTextView.getText().toString());
               intent2.putExtra("titleTextView",titleTextView.getText().toString());
               //storing the color code in integer format so that it cn be passed on to the editNote Activity
                int colorCode = intent.getIntExtra("color",0);
                intent2.putExtra("BackgroundColor",colorCode);
               startActivity(intent2);
                //adding animation when opening new activity
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }
        });

        //code related to text to speech engine
        //setting up text to speech engine
        mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //checking if this set language method was successfull
                    int result = mtts.setLanguage(Locale.ENGLISH); //passing language to our text to speech engine if its initializaton is a success
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //if there is a missing data or language not supported by the device then we will show an error message
                        Toast.makeText(getApplicationContext(), "Either the language is not supported by your device or the input field is empty", Toast.LENGTH_LONG).show();
                    } else {
                        //if there is no error and text to speech is successfully loaded then button is enabled
                        everythingIsOKmttsIsGoodToGo = 1;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Initialization of text to speech engine failed!!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_speak, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        //stopping mtts when the app is closed
        if(mtts!=null){
            mtts.stop();
            mtts.shutdown();
        }
        super.onDestroy();
    }

    //it will handle the back button in the tool bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home = default id for back button
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        if (item.getItemId() == R.id.speak) {
            if (everythingIsOKmttsIsGoodToGo == 1) {
                String text = contentTextView.getText().toString();
                mtts.setPitch(1.1f); //setting up the pitch and speed of the speech in text to speech engine
                mtts.setSpeechRate(1.1f);
                //making text to speech engine to speek our entered text
                //TextToSpeech.QUEUE_FLUSH = current txt is cancled to speak a new one
                //TextToSpeech.QUEUE_ADD the next text is spoken after the previous text is finished
                //mtts.speak(Passing the content of our editText, TextToSpeech.QUEUE_FLUSH,null);
                mtts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        if(item.getItemId()==R.id.export){
            scorestore();
        }
        return super.onOptionsItemSelected(item);
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
                          | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                           | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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


    //storing high score int he file system you need to go to android manifest xml file
    //there inside the application tag write (android:requestLegacyExternalStorage="true")
    //permission denied problem will be solved
    public void scorestore() //it will store the high score
    {
        save = contentTextView.getText().toString();
        //data entry will take place
            //android version above marshmallow will require to ask for user permission to access files
            //go to manifest file and type the following under the package name
            //<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>

            if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)  //checking our bhuild version of the os greater than marshmallow version
            {
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                {
                    String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                    //show popup for runtime permissions
                    requestPermissions(permissions, WRITE_EXTERNAL_STORAGE_CODE);
                }
                else{
                    //permission already granted; hence save data
                    saveToTxtFile(save);
                }
            }

        }


    //checking permission and requesting permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case WRITE_EXTERNAL_STORAGE_CODE:{
                // if request is canceled then result arrays are empty
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    saveToTxtFile(save);
                }
                else
                {
                    Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //actually saving in game data onto the file system by creating a directory and a txt file
    private void saveToTxtFile(String save) {
        //get current Time for file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());

        try{
            //path to storage in the file system
            //File path = Environment.getExternalStorageDirectory();
            File path = Environment.getExternalStorageDirectory();
            //create folder name "Brain_Trainer_game"
            File dir = new File(path+"/Taking_Notes/");
            dir.mkdirs();

            //File name
            String Filename = titleTextView.getText().toString() + timeStamp + ".txt";

            //creating new file
            File file = new File(dir, Filename);

            //FileWriter class s used to store characters in file
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(save);
            bw.close();

            Toast.makeText(this,Filename + "is saved\n" +dir, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            //if anything goes wrong
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}