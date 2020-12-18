package core_module;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aditya.takingnotes.R;

public class DevActivity extends AppCompatActivity {
    ListView listView;
    TextView textView4;
    MediaPlayer mediaPlayer; //will play sound when something is selected from the listView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //calling get support action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView4 = findViewById(R.id.textView4);
        listView = findViewById(R.id.listView);

        String [] info ={"Name","Branch","College Name", "college code", "Roll number", "course"};
        ListAdapter items = new ArrayAdapter<String>(this, R.layout.row, info);
        listView.setAdapter(items);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                {
                    textView4.setText("Aditya Kumar");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.name);
                    mediaPlayer.start();
                }
                else if(position==1)
                {
                    textView4.setText("Computer science");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.branch);
                    mediaPlayer.start();
                }
                else if(position==2)
                {
                    textView4.setText("Saroj Institute of Technology");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.college_name);
                    mediaPlayer.start();
                }
                else if(position == 3)
                {
                    textView4.setText("123");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.college_code);
                    mediaPlayer.start();
                }
                else if(position==4)
                {
                    textView4.setText("1901230100001");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.roll_number);
                    mediaPlayer.start();
                }
                else if(position==5)
                {
                    textView4.setText("B.tech");
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.course);
                    mediaPlayer.start();
                }
            }
        });
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