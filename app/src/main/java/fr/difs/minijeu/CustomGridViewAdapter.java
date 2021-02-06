package fr.difs.minijeu;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        ImageView icon = (ImageView) view.findViewById(R.id.item_image); // get the reference of ImageView
        icon.setImageResource(R.drawable.woodenframe); // set logo images
        TextView txtTitle = (TextView) view.findViewById(R.id.item_text);
        txtTitle.setText(levels.get(position).getLevel()+"");
        return view;




//        RecordHolder holder = null;
//        if (row == null) {
//            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
//            row = inflater.inflate(resourceId, parent, false);
//
//            holder = new RecordHolder();
//            holder.txtTitle = (TextView) row.findViewById(R.id.item_text);
//            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
//            row.setTag(holder);
//        } else {
//            holder = (RecordHolder) row.getTag();
//        }
//
//        CustomGridViewItem item = levels.get(position);
//        holder.txtTitle.setText(item.getLevel());
//        holder.imageItem.setImageBitmap(item.getImage());
//        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        ImageView imageItem;

    }
}
