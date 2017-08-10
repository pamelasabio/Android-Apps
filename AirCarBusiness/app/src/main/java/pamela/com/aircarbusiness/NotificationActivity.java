package pamela.com.aircarbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import pamela.com.aircarbusiness.Fragments.MyCarsFragment;
import pamela.com.aircarbusiness.Fragments.NotificationFragment;

/**
 * Created by pamela on 24/11/2016.
 * This Activity shows details of the notifications which is the reservation request.
 * It takes the position of the clicked item from the ListView in NotificationsFragment.
 * It takes the Hashmap from the notifications_list at the position that is passed.
 */

public class NotificationActivity extends AppCompatActivity {
    private ImageView big_imageView;
    private HashMap<String, String> notifHashmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Bundle bundle = getIntent().getExtras();
        final int position = bundle.getInt("position");
        notifHashmap = (HashMap<String, String>) NotificationFragment.getnotifications_list().get(position);
        setImages();
        TextView message_textView = (TextView) findViewById(R.id.message_textView);
        message_textView.setText(notifHashmap.get(MainActivity.getMESSAGE()));

        Button accept_button = (Button) findViewById(R.id.accept_button);
        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveReservation();
                deleteNotification();
            }
        });

        Button reject_button = (Button) findViewById(R.id.reject_button);
        reject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNotification();
            }
        });
    }

    //set the imageViews to the fetched data from the carPosts
    private void setImages(){
        big_imageView = (ImageView) findViewById(R.id.big_imageView);
        ImageView small1_imageView = (ImageView) findViewById(R.id.small1_imageView);
        ImageView small2_imageView = (ImageView) findViewById(R.id.small2_imageView);
        ImageView small3_imageView = (ImageView) findViewById(R.id.small3_imageView);
        glide(notifHashmap.get(MainActivity.getURL1()),big_imageView);
        glide(notifHashmap.get(MainActivity.getURL1()),small1_imageView);
        glide(notifHashmap.get(MainActivity.getURL2()),small2_imageView);
        glide(notifHashmap.get(MainActivity.getURL3()),small3_imageView);
        small1_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(notifHashmap.get(MainActivity.getURL1()),big_imageView);
            }
        });
        small2_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(notifHashmap.get(MainActivity.getURL2()),big_imageView);
            }
        });
        small3_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(notifHashmap.get(MainActivity.getURL3()),big_imageView);
            }
        });
    }

    private void glide(String url, ImageView imageView) {
        Log.i("Adapter3","Gliding");
        Glide.with(getApplicationContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageView);
    }

    //This method inserts the values into the saveReservation database.
    private void saveReservation(){
        final ParseObject object = new ParseObject(MainActivity.getRESERVATION_TABLE());
        ParseUser user = ParseUser.getCurrentUser();
        object.put(MainActivity.getPOSTER_ID(),user.getObjectId());
        object.put(MainActivity.getCUSTOMER_ID(),notifHashmap.get(MainActivity.getCUSTOMER_ID()));
        object.put(MainActivity.getPOST_ID(),notifHashmap.get(MainActivity.getPOST_ID()));
        object.put(MainActivity.getEMAIL(),notifHashmap.get(MainActivity.getEMAIL()));
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Toast.makeText(NotificationActivity.this, "Car reserved", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(NotificationActivity.this,MainActivity.class);
                    NotificationFragment.getnotifications_list().clear();
                    startActivity(i);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //This method deletes the notification the notification table.
    private void deleteNotification(){
        final ParseQuery<ParseObject> notifications_query = new ParseQuery<ParseObject>(MainActivity.getNOTIFICATIONS_TABLE());
        notifications_query.whereEqualTo(MainActivity.getOBJECT_ID(),notifHashmap.get(MainActivity.getOBJECT_ID()));
        notifications_query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    try {
                        object.delete();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
}
