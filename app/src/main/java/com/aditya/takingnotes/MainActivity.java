package com.aditya.takingnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import authentication.LoginActivity;
import authentication.RegisterActivity;
import core_module.AddNote;
import core_module.DevActivity;
import core_module.NoteDetails;
import core_module.Pop;
import model.Note;

//implementing onclickliserner on drawer menu
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /*
    after connecting the application with the firebase modify these lines in the gradel build module app
    implementation 'com.google.firebase:firebase-auth:19.2.0'
    implementation 'com.google.firebase:firebase-firestore:21.4.0'
     */

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView nav_VIew;
    Toolbar toolbar;

    RecyclerView recyclerView;

    //in order to access the firestore database we need the instance of firestore here
    FirebaseFirestore firebaseFirestore;

    //creating a separate adapter for FirebaseFirestore so that we can pull the data from the firebase database and show that data on the RecyclerView
    //FirestoreRecyclerAdapter<Model class = Note,ViewHolder>
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;

    //creating firebase user object here
    FirebaseUser user;
    FirebaseAuth authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //geting the instance of our fireStore
        firebaseFirestore = FirebaseFirestore.getInstance();
        authentication = FirebaseAuth.getInstance();
        //userID object
        user = authentication.getCurrentUser();

        //now we need to query the data base
        //orderBy("paramerter on which the order will be decided", Direction of ordering Acending or Decending)
         /*
        here we will Query the notes collection and inside that we will have the document identified by the uid of the user
        and inside that we will have my notes and then we will query my notes and inside that we will have all the notes

        user.getUid() = will give us the id of the user
         */
        Query query = firebaseFirestore.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);

        //executing the created query
        //FirestoreRecyclerOptions<here will come the class model name using which we will extract the data from the firebase> it will execute this query that we just created
        //once this build() method is called its going to query the data from the notes collection in the fireStore database
        //and finally we can use this allNotes to get the particular document
        FirestoreRecyclerOptions<Note> allNotes= new FirestoreRecyclerOptions.Builder<Note>().
                setQuery(query,Note.class).build();

        //creating the object of the noteAdapter
        // noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(HERE WE NEED TO PASS THE QUERY = allNotes)
        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull final Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                //creating an int variable to store the color code of the cardView
                final int colorCode = noteViewHolder.view.getResources().getColor(getRandomeColour(),null);

                //binding the colours onto our cardView
                //holder.view.getResources().getColor(getRandomeColour(),null) this will select the colour from the colour.xml
                noteViewHolder.mCardView.setCardBackgroundColor(colorCode);
                //DOcument ID
                final String docId = noteAdapter.getSnapshots().getSnapshot(i).getId();
                Log.i("noteId",docId);

                //setting up an onclickListener on our view over here
                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("clicked", "item clicked in the card View");
                        //now we will start the new Activity here in this case we will open noteDetails Activity
                        //View v is the current context here and NoteDetails.class is the activity that we want to open
                        Intent intent = new Intent(v.getContext(), NoteDetails.class);
                /*
                if the user selects the first note on the recyclerView then the position will return 0
                and the title and the content of the note will be returned from the zero'th position of the ArrayList
                 */

                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        //passing the color assign to the carView onto the noteDetails activity
                        intent.putExtra("color",colorCode);
                        //passing the noteID onto the EditNote Activity
                        intent.putExtra("docId",docId);
                        v.getContext().startActivity(intent);
                    }
                });

                //menuIcon is the imageView in the carView that is displaying the text Information in the RecyclerView
                //noteViewHolder will be used to get the xml resources inside the cardView
                //view is the variable that is created inside the noteViewHolder and this view contains the actual view of oor layout
                //usint this view we can get the resources
                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                //setting up the onClick Listener for our menue icon
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("carView", "menu icon clicked");
                        Intent intent = new Intent(v.getContext(), Pop.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        //passing the color assign to the carView onto the noteDetails activity
                        intent.putExtra("color",colorCode);
                        //passing the noteID onto the EditNote Activity
                        intent.putExtra("docId",docId);
                        v.getContext().startActivity(intent);
                        //adding animation when opening new activity
                        overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                    }
                });

            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        //setting up our recyclerView
        recyclerView = findViewById(R.id.noteList);

        //setting up our drawer
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer);
        nav_VIew = findViewById(R.id.nav_View);
        //telling the NavigationItemSelectedListener onto the sav_View
        nav_VIew.setNavigationItemSelectedListener(this);
        //it will specify that if the nacigation bar is open or close
        /*
        creating the object for Actionbar Drawer toggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(where we want to have this actionbar toggle,the layout that we want to toggle,where we want to show the toggle button,R.string.open,R.string.close);
         */
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        //setting up the drawer listner for our drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        //its going to tell the action bar toggle weather the drawer is open or not
        actionBarDrawerToggle.syncState();

        //seting up the layout manager for our recycler View
        //StaggeredGridLayoutManager(number of columns that you want to show,Oreientation of the gridView )
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        //now setting up the adapter for the recyclerView
        recyclerView.setAdapter(noteAdapter);

        //here we will set the current user name and email address in the drawer title
        //creating object of View class so that we can access the elements of our drawer header layout using View
        //now we need to use the nav_View to get the header
        //by using this custome view i will bind the xml resources of the drawer menu to the textView variable
        View headerView = nav_VIew.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userNameDisplay);
        TextView useremail = headerView.findViewById(R.id.userEmailDisplay);
        if(user.isAnonymous())
        {
            username.setText("Temporary Account");
        }
        else {
            useremail.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }

    }


    //now we need our Recycler View to Listen for the data change all the time in our FireStore database when the main Activity is active
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
   //We need to stop Listening for new data from the database for our RecyclerView when the Main Activity is stopped or killed
    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null){
            noteAdapter.stopListening();
        }
    }

    //handeling click on the drawer menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START); //this will close the drawer when an menu item is clicked
        switch(item.getItemId())
        {
            case R.id.add_notes:
                Intent intent = new Intent(getApplicationContext(), AddNote.class);
                startActivity(intent);
                //adding animation when opening new activity
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.developer:
                Intent intent2 = new Intent(getApplicationContext(), DevActivity.class);
                startActivity(intent2);
                //adding animation when opening new activity
                overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                break;
            case R.id.log_out:
                //if the user is registered on our application using anonymous account then we will not save their data after they have signed out and delete their user account
                //but if the authenticated user that has signed up with his user name and password then we will save their data and not delete their user account
                checkUser();
                break;
            case R.id.log_in:
                //here log in page will be implemented in the drawer menu
                if(user.isAnonymous()) {
                    Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent1);
                    finish();
                    //adding animation when opening new activity
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are already Logged in",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                //if the user is already logged in and he presses the sync note button then we will not allow him to go to login or register page
                //we will only allow anonymous user to go to register page activity
                if(user.isAnonymous()) {
                    //opening the register page for the anonymous user
                    Intent intent3 = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent3);
                    finish();
                    //adding animation when opening new activity
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are already Logged in",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Sync_note:
                //if the user is already logged in and he presses the sync note button then we will not allow him to go to login or register page
                //we will only allow anonymous user to go to register page activity
                if(user.isAnonymous()) {
                    //opening the register page for the anonymous user
                    Intent intent4 = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent4);
                    finish();
                    //adding animation when opening new activity
                    overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
                }
                else{
                    Toast.makeText(getApplicationContext(),"You are already Logged in",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Log.i("menu","Item selected");
        }
        return false;
    }

    //this method is responsible for checking if they are real user or anonymous user
    private void checkUser() {
        //checking if the user is real or not
        if(user.isAnonymous()){
            //PERFORM THIS IF BLOCK WHEN THE USER IS ANONYMOUS

            //creating a new alert dialog box to ask the user weather they truely want to delete the note or not
            new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.danger).setTitle("Are you sure you want to Logout?")
                    .setMessage("You din't register your email address and password. All your notes will be Lost FOREVER!")
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //first we need to delete all the notes of the ANonymous user

                    //delete the user anonymous
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //when the user is deleted successfully then we will send him to the splash screen
                            Intent intent = new Intent(getApplicationContext(), Splash.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }).show();
        }
        else{
            //IF THE USER IS ALREADY REGISTERED WITH HIS EMAIL ADDRESS AND PASSWORD
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(),Splash.class);
            startActivity(intent);
            finish();
        }
    }

    //this method will generate randome colours using out colors xml file inside the values folder
    private int getRandomeColour() {
        List<Integer> colourCode = new ArrayList<>();
        colourCode.add(R.color.darkred);
        colourCode.add(R.color.colorPrimary);
        colourCode.add(R.color.colorPrimaryDark);
        colourCode.add(R.color.colorAccent);
        colourCode.add(R.color.yellow);
        colourCode.add(R.color.lightGreen);
        colourCode.add(R.color.pink);
        colourCode.add(R.color.lightPurple);
        colourCode.add(R.color.skyblue);
        colourCode.add(R.color.gray);
        colourCode.add(R.color.red);
        colourCode.add(R.color.blue);
        colourCode.add(R.color.greenlight);
        colourCode.add(R.color.notgreen);
        //creating a random generator so that we can generate random colours
        Random random = new Random();
        int number = random.nextInt(colourCode.size());
        return colourCode.get(number);
    }

    //NOTEVIEWHOLDER MODEL CLASS
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
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