package fr.difs.minijeu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomGridViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    List<CustomGridViewItem> levels;

    public CustomGridViewAdapter(Context applicationContext, List<CustomGridViewItem> levels) {
        this.context = applicationContext;
        this.levels = levels;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return levels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.single_level, null);


        TextView txtTitle = (TextView) view.findViewById(R.id.level_number); // number of the level
        txtTitle.setText(levels.get(position).getLevel() + "");

        TextView txtScore = (TextView) view.findViewById(R.id.level_score); // highscore of the level
        float score = levels.get(position).getScore();
        if (score > 0f)
            txtScore.setText(score + "s");

        ImageView frame = (ImageView) view.findViewById(R.id.level_frame); // setup frame image
        if(levels.get(position).isUnlocked()) {
            frame.setImageResource(R.drawable.woodenframe);
        }
        else {
            frame.setImageResource(R.drawable.locked_woodenframe);
            if (score == 0f)
                txtScore.setText("");
        }
        return view;
    }
}
