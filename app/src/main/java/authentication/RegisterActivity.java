package authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText userName,userEmail,password,passwordConfirm;
    Button createAccount;
    TextView login;
    ProgressBar progressBar;
    FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //setting up our drawer
        toolbar = findViewById(R.id.toolbarregister);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        password = findViewById(R.id.password);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        createAccount = findViewById(R.id.createAccount);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.INVISIBLE);

        //new instance of firebase authentication class
        authentication = FirebaseAuth.getInstance();

        //we will set on click Listener on our Sync now button
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                //for setting up the colour in progress bar
                progressBar.getProgressDrawable().setColorFilter(
                        Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);

                //animating our progress bar
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
                progressAnimator.setDuration(2000);
                progressAnimator.setInterpolator(new LinearInterpolator());
                progressAnimator.start();

                //Extracting information from the editText fields
                final String userNameString = userName.getText().toString();
                String userEmailString = userEmail.getText().toString();
                //removing all spaces from the string where \\s is a single space in unicode
                userEmailString = userEmailString.replaceAll("\\s","");
                String passwordString = password.getText().toString();
                String passwordConfirmString = passwordConfirm.getText().toString();

                //now we will check if any of the field is empty if empty then we will show the error message to the user that we need all the fields
                if (userNameString.isEmpty() || userEmailString.isEmpty() || passwordString.isEmpty() || passwordConfirmString.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Input Fields can't be Empty", Toast.LENGTH_SHORT).show();
                } else if (!passwordString.equals(passwordConfirmString)) {
                    String error = "Password not matching";
                    //this will set the error message using i animations
                    passwordConfirm.setError(error);
                } else {
                    //merging the anonymous account with the real account
                    Log.i("ok", "all input fields are filled");
                    //creating an authentication credentials
                    //linking real user with the anonymous account
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmailString, passwordString);
                    //now we can use authentication.getCurrentUser(); which is Anonymous in our case
                    //linkWithCredential(here we will pass the credential that we just created ) will merge the anonymous user account with the real user account
                    authentication.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //saving the user name inside the user profile of the user account during registration
                            FirebaseUser firebaseUser = authentication.getCurrentUser(); //this is the user that has just registered his email into our app
                           //now to be able to save the user name of the particular user into the firebase authentication profile object
                            //.Builder() will add the new data and also dont forget to write .build(); after your request
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(userNameString).build();
                            //and now we will update our user profile
                            firebaseUser.updateProfile(request);

                            //if the  account are linked then display the message successful
                            Toast.makeText(getApplicationContext(), "Sync successful!", Toast.LENGTH_SHORT).show();
                            //now we will send the user to the main activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                            //adding animation when opening new activity
                            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Sync Failed!\nTry again!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                //adding animation when opening new activity
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
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

    //it will handle the back button in the tool bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //android.R.id.home = default id for back button
        if(item.getItemId()==android.R.id.home){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            //adding animation when opening new activity
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);
    }
}