package core_module;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.aditya.takingnotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNote extends AppCompatActivity {

    //I created this Adapter class for purely debugging purposes so that I can check the error in my RecyclerView
    //Its not necessasary for the functioning of th application sinsce we've already created a Firestore Adapter for this application

    EditText editNoteEditText;
    EditText addNoteTitle;
    ImageView speech_to_text4;
    //now here we want to store the newly created note inside the database of google firebase
    //so we will start by calling firebase Store
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    //Setting up our Progress bar
    ProgressBar progressBar;
    TextView textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        user = FirebaseAuth.getInstance().getCurrentUser();//instanciating the user object It will get the current logger in user

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        speech_to_text4 = findViewById(R.id.speech_to_text4);
        speech_to_text4.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);
        textView5 = findViewById(R.id.textView5);
        textView5.setVisibility(View.INVISIBLE);

        //getting the instance of FirebaseFirestore
        //before performing any operation on google's Firebase Data base servers we need to create an instance of the firebase database
        //or any other firebase products in our android application at production level or during beta testing
        firebaseFirestore = FirebaseFirestore.getInstance();

        editNoteEditText = findViewById(R.id.editNoteEditText);
        addNoteTitle = findViewById(R.id.addNoteTitle);

        FloatingActionButton fab = findViewById(R.id.save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("save","Save button clicked");
                //extracting data from the editTexts
                String noteContent = editNoteEditText.getText().toString();
                String noteTitle = addNoteTitle.getText().toString();
                //checking if the input fields are empty or not
                if(noteContent.isEmpty() || noteTitle.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Input field can't be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                //saving datainto our firebase database

                textView5.setVisibility(View.VISIBLE);
                //setting up our progress bar visible while saving data into our database
                progressBar.setVisibility(View.VISIBLE);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                    //code to store the data inside the database if the input filed is not empty
                //notes is our collection of notes and notes is like a container here
                //each note will have multiple data like note title, date , owner of the note contents of the note etc.
                //specify te collection of notes using firebase store
                //.document(); its going to create the new empty document
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document();
                //now i will create a map object that will contain the title and the content of the note and we will insert that into the document
                //Map<String = Key,Object = value> note = new HashMap<>();
                Map<String,Object> note = new HashMap<>();
                note.put("title",noteTitle);
                note.put("content",noteContent);

                //now we can insert the note data
                documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                     //handle the condition when the note is added successfully here
                        Toast.makeText(AddNote.this,"Note saved!",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        textView5.setVisibility(View.INVISIBLE);
                        onBackPressed();
                        //adding animation when opening new activity
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this,"Failed!...Note could not be saved",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        textView5.setVisibility(View.INVISIBLE);
                        onBackPressed();
                        //adding animation when opening new activity
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    }
                });

            }
        });

        speech_to_text4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here speech to text engine code will come
                //here we will get the implicet intent to get the user input as a speech
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                //now setting up extra values for our intent here
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //getDefault() will point towards the local language of your android operating system
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                //making sure that our activity supports implicet intent
                if(intent.resolveActivity(getPackageManager()) != null){
                    //get the result
                    //10 is our request code  that we are passing to the on ActivityResult override method
                    startActivityForResult(intent,10);
                }
                else{
                    Toast.makeText(getApplicationContext(), "this feature is not supported by your android device",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //exit animation of currently active activity
    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
    }

    //now we will get the call back on our Activity by using onActivityCallBack result
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            //10 is our request code
            case  10:
                if(resultCode == RESULT_OK && data !=null){
                    //extract the result the data will an ArrayList of String
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editNoteEditText.setText(result.get(0));
                }
                break;
        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.speech_to_text, menu);
        return true;
    }

    //it will handl the back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home = default id for back button
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            //adding animation when opening new activity
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        else if(item.getItemId() ==R.id.enable_speech_to_text){
            speech_to_text4.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }
}