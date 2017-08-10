package pamela.com.aircarbusiness.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pamela.com.aircarbusiness.MainActivity;
import pamela.com.aircarbusiness.R;

import static android.graphics.Typeface.BOLD;

/**
 * Created by pamela on 07/07/16.
 */
public class CustomBaseAdapter extends BaseAdapter {

    private Context context;
    private List<HashMap<String,String>> data;
    private int fragment;
    //private Holder holder;

    //constructor
    public CustomBaseAdapter(Context c, List<HashMap<String,String>> list, int fragment){
        this.context = c;
        this.data = new ArrayList<HashMap<String, String>>();
        this.data = list;
        this.fragment = fragment;
    }

    class Holder{
        ImageView list_imageView;
        TextView list_textView;

        Holder(View v){
            list_imageView = (ImageView) v.findViewById(R.id.imageView);
            list_textView = (TextView) v.findViewById(R.id.imageText);
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.advanced_list_row,parent,false);
            holder = new Holder(row);
            row.setTag(holder);
        }else{
            holder = (Holder) row.getTag();
        }

        if(data != null) {
            glide(data.get(position).get(MainActivity.getURL1()), holder.list_imageView);
            if(fragment == 0) {
                String car_make = data.get(position).get(MainActivity.getMAKE());
                String car_model = data.get(position).get(MainActivity.getMODEL());
                String description = data.get(position).get(MainActivity.getDESCRIPTION());
                holder.list_textView.setText(car_make + " " + car_model + "\n\n" + description);
            }else if(fragment == 1) {
                String customer = data.get(position).get(MainActivity.getEMAIL());
                String car_make = data.get(position).get(MainActivity.getMAKE());
                String car_model = data.get(position).get(MainActivity.getMODEL());
                String message = "is asking for a reservation of your car ";
                holder.list_textView.setText(customer + " " + message + car_make + " " + car_model);
            }else if(fragment == 2){
                String customer = data.get(position).get(MainActivity.getEMAIL());
                String car_make = data.get(position).get(MainActivity.getMAKE());
                String car_model = data.get(position).get(MainActivity.getMODEL());
                String message = "reserved your car ";
                holder.list_textView.setText(customer + " " + message + car_make + " " + car_model);
            }else if(fragment == 3){
                String car_make = data.get(position).get(MainActivity.getMAKE());
                String car_model = data.get(position).get(MainActivity.getMODEL());
                holder.list_textView.setText(car_make + " " + car_model);
            }
        }else{
            Log.i("Adapter3","NULL");
            holder.list_imageView.setImageResource(android.R.drawable.ic_dialog_alert);
        }
        return row;
    }


    // Method that takes picture URL and the imageView, and then put the picture in that ImageView
    private void glide(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .override(150,250)
                .into(imageView);
    }

}
