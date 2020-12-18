package model;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import core_module.NoteDetails;
import com.aditya.takingnotes.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> titles;
    List<String> content;

    public Adapter(List<String> title, List<String> content){
        this.titles = title;
        this.content = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.noteTitle.setText(titles.get(position));
        holder.noteContent.setText(content.get(position));
        //creating an int variable to store the color code of the cardView
        final int colorCode = holder.view.getResources().getColor(getRandomeColour(),null);

        //binding the colours onto our cardView
        //holder.view.getResources().getColor(getRandomeColour(),null) this will select the colour from the colour.xml
        holder.mCardView.setCardBackgroundColor(colorCode);
        //setting up an onclickListener on our view over here
        holder.view.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("title",titles.get(position));
                intent.putExtra("content",content.get(position));
                //passing the color assign to the carView onto the noteDetails activity
                intent.putExtra("color",colorCode);
                v.getContext().startActivity(intent);
            }
        });
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

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle,noteContent;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.titles);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.noteCard);
            view = itemView;
        }
    }
}
