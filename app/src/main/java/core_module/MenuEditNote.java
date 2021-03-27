package core_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
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

public class MenuEditNote extends AppCompatActivity {

    EditText MenueditNoteTitleEditText,MenueditNoteContentEditText;
    TextView MenutextView6;
    ProgressBar MenuprogressBar2;
    ImageView speech_to_text2;

    //now here we want to store the newly created note inside the database of google firebase
    //so we will start by calling firebase Store
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit_note);

        user = FirebaseAuth.getInstance().getCurrentUser();//instanciating the user object It will get the current logger in user

        Toolbar toolbar = findViewById(R.id.Menutoolbar);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        speech_to_text2 = findViewById(R.id.speech_to_text2);

        MenutextView6 = findViewById(R.id.MenutextView6);
        MenuprogressBar2 = findViewById(R.id.MenuprogressBar2);
        MenuprogressBar2.setVisibility(View.INVISIBLE);
        MenutextView6.setVisibility(View.INVISIBLE);

        //setting up our editText
        MenueditNoteContentEditText = findViewById(R.id.MenueditNoteContentEditText);
        MenueditNoteTitleEditText = findViewById(R.id.MenueditNoteTitleEditText);
        MenueditNoteTitleEditText.setText(Pop.stringTitle);
        MenueditNoteContentEditText.setText(Pop.stringContent);
        MenueditNoteContentEditText.setBackgroundColor(Pop.colourCodeINT);

        //getting the instance of FirebaseFirestore
        //before performing any operation on google's Firebase Data base servers we need to create an instance of the firebase database
        //or any other firebase products in our android application at production level or during beta testing
        firebaseFirestore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.MenuEditedSaveNotes);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("save","Save button clicked");
                //checking if the input fields are empty or not
                if(MenueditNoteContentEditText.getText().toString().isEmpty() || MenueditNoteTitleEditText.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Input field can't be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                //saving datainto our firebase database

                //setting up our progress bar visible while saving data into our database
                MenuprogressBar2.setVisibility(View.VISIBLE);
                MenutextView6.setVisibility(View.VISIBLE);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(MenuprogressBar2, "progress", 0, 100);
                progressAnimator.setDuration(1000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                String updatedNotesTitle = MenueditNoteTitleEditText.getText().toString();
                String updatedNotesContent = MenueditNoteContentEditText.getText().toString();

                //code to store the data inside the database if the input filed is not empty
                //notes is our collection of notes and notes is like a container here
                //each note will have multiple data like note title, date , owner of the note contents of the note etc.
                //specify te collection of notes using firebase store
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").document(Pop.docIdString);
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
                        Toast.makeText(MenuEditNote.this,"Note Updated!",Toast.LENGTH_SHORT).show();
                        MenuprogressBar2.setVisibility(View.INVISIBLE);
                        MenutextView6.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MenuEditNote.this,"Failed!...Note could not be updated",Toast.LENGTH_SHORT).show();
                        MenuprogressBar2.setVisibility(View.INVISIBLE);
                        MenutextView6.setVisibility(View.INVISIBLE);
                        onBackPressed();
                    }
                });

            }
        });

        speech_to_text2.setOnClickListener(new View.OnClickListener() {
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
                    String old_text = MenueditNoteContentEditText.getText().toString();
                    MenueditNoteContentEditText.setText(old_text+" "+result.get(0));
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
        }
        return super.onOptionsItemSelected(item);
    }
}