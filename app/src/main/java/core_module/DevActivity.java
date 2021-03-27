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
            onBackPressed();
            //adding animation when opening new activity
            overridePendingTransition(R.anim.slide_up,R.anim.slide_down);
        }
        return super.onOptionsItemSelected(item);
    }

}