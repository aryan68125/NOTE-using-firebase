package authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aditya.takingnotes.MainActivity;
import com.aditya.takingnotes.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import core_module.Pop;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText email,lPassword;
    Button loginBtn;
    TextView forgotPasword,createAccount;
    ProgressBar progressBar3;
    FirebaseAuth authentication;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //setting up our drawer
        toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        email = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginBtn = findViewById(R.id.loginBtn);
        forgotPasword = findViewById(R.id.forgotPasword);
        createAccount = findViewById(R.id.createAccount);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar3.setVisibility(View.INVISIBLE);

        //creating a new instance of FirebaseAuth
        authentication = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.setVisibility(View.VISIBLE);
                //for setting up the colour in progress bar
                progressBar3.getProgressDrawable().setColorFilter(
                        Color.WHITE, PorterDuff.Mode.SRC_IN);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar3, "progress", 0, 100);
                progressAnimator.setDuration(2000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                String emailString = email.getText().toString();
                //removing all the black spaces from the email where \\s is a single space in unicode
                emailString = emailString.replaceAll("\\s","");
                String lpasswordString = lPassword.getText().toString();
                if (emailString.isEmpty() || lpasswordString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Input Fields can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //now we will sign in the user with their email and passoword
                authentication.signInWithEmailAndPassword(emailString,lpasswordString).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        //creating a new alert dialog box to ask the user weather they truely want to delete the note or not
                        new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.danger).setTitle("Are you sure you want to Login?")
                                .setMessage("You are in a temporary account and if you try to login all your notes will be lost FOREVER!")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(),"Login Success!",Toast.LENGTH_SHORT).show();

                                        //here we will delete the temporary user if the login is a success other wise our app will crash
                                        //before loggin the user in our application we need to delete the anonymous user
                                        // deleting notes created by the anonymous user
                                        if (authentication.getCurrentUser().isAnonymous()) {
                                            //if the user is anonymous we need to delete the user from the firebase cloud
                                            FirebaseUser user = authentication.getCurrentUser();
                                            //now we will delete the whole document created by the anonymous user
                                            //now creating the fireStore
                                            firestore.collection("notes").document(user.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "All temorary notes are removed", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Oops someting went wrong!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            //now we will delete the temporary user
                                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Temporary Account Deleted!", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Oops something went wrong!\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        //now we will send the user to the main activity
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        //adding animation when opening new activity
                                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);


                                    }
                                }).setNegativeButton("No",null).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Oops something went wrong!\n"+e.getMessage()+"\n Try again!",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        //now setting up the create new account button
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
                //adding animation when opening new activity
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
            }
        });

    }

    //it will handle the back button in the tool bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home = default id for back button
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(this.getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            //adding animation when opening new activity
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);
    }
}
