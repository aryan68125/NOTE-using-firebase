package core_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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

import com.aditya.takingnotes.MainActivity;
import com.aditya.takingnotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditNote extends AppCompatActivity {

    EditText editNoteTitleEditText,editNoteContentEditText;
    TextView textView6;
    ProgressBar progressBar2;

    //now here we want to store the newly created note inside the database of google firebase
    //so we will start by calling firebase Store
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    Intent intent2;
    String content;
    String title;
    String noteId;
    ImageView speech_to_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        user = FirebaseAuth.getInstance().getCurrentUser();//instanciating the user object It will get the current logger in user

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting up our editText
        editNoteContentEditText = findViewById(R.id.editNoteContentEditText);
        editNoteTitleEditText = findViewById(R.id.editNoteTitleEditText);
        speech_to_text = findViewById(R.id.speech_to_text);

        //getting the data from the Note Details activity
        intent2 = getIntent();
        content = intent2.getStringExtra("contentTextView");
        title = intent2.getStringExtra("titleTextView");
        noteId = NoteDetails.docId;

        int BackgroundColor = intent2.getIntExtra("BackgroundColor",0);
        editNoteTitleEditText.setText(title);
        editNoteContentEditText.setText(content);
        editNoteContentEditText.setBackgroundColor(BackgroundColor);

        //setting up the visibility of the progress bar to invisible initially
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.INVISIBLE);
        textView6 = findViewById(R.id.textView6);
        textView6.setVisibility(View.INVISIBLE);

        //getting the instance of FirebaseFirestore
        //before performing any operation on google's Firebase Data base servers we need to create an instance of the firebase database
        //or any other firebase products in our android application at production level or during beta testing
        firebaseFirestore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.EditedSaveNotes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("save","Save button clicked");
                //checking if the input fields are empty or not
                if(editNoteContentEditText.getText().toString().isEmpty() || editNoteTitleEditText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Input field can't be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                //saving datainto our firebase database

                //setting up our progress bar visible while saving data into our database
                progressBar2.setVisibility(View.VISIBLE);
                textView6.setVisibility(View.VISIBLE);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar2, "progress", 0, 100);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                String updatedNotesTitle = editNoteTitleEditText.getText().toString();
                String updatedNotesContent = editNoteContentEditText.getText().toString();

                //code to store the data inside the database if the input filed is not empty
                //notes is our collection of notes and notes is like a container here
                //each note will have multiple data like note title, date , owner of the note contents of the note etc.
                //specify te collection of notes using firebase store
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(noteId);
                //now i will create a map object that will contain the title and the content of the note and we will insert that into the document
                //Map<String = Key,Object = value> note = new HashMap<>();
                Map<String,Object> note = new HashMap<>();
                note.put("title",updatedNotesTitle);
                note.put("content",updatedNotesContent);

                //now we can Update the note data
                documentReference.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //handle the condition when the note is added successfully here
                        Toast.makeText(EditNote.this,"Note Updated!",Toast.LENGTH_SHORT).show();
                        progressBar2.setVisibility(View.INVISIBLE);
                        textView6.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        onBackPressed();
                        //adding animation when opening new activity
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this,"Failed!...Note could not be updated",Toast.LENGTH_SHORT).show();
                        progressBar2.setVisibility(View.INVISIBLE);
                        textView6.setVisibility(View.INVISIBLE);
                        onBackPressed();
                        //adding animation when opening new activity
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    }
                });

            }
        });

        speech_to_text.setOnClickListener(new View.OnClickListener() {
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
                    String old_text = editNoteContentEditText.getText().toString();
                    editNoteContentEditText.setText(old_text+" "+result.get(0));
                }
                break;
        }
    }

    //it will handle the back button in the tool bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home = default id for back button
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            //adding animation when opening new activity
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
          return super.onOptionsItemSelected(item);
    }
}