package core_module;

import android.content.Intent;
import android.os.Bundle;

import com.aditya.takingnotes.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class NoteDetails extends AppCompatActivity {

    TextView contentTextView;
    TextView titleTextView;
    Intent intent;
   static String docId;

    //setting up text to speech listener
    TextToSpeech mtts;
    int everythingIsOKmttsIsGoodToGo = 0;

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