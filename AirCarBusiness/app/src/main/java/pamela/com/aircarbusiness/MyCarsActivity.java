package pamela.com.aircarbusiness;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;

import pamela.com.aircarbusiness.Fragments.MyCarsFragment;

/**
 * Created by pamela on 27/11/2016.
 * This Activity shows the details of the car that the user has posted.
 * It takes the position of the clicked item from the ListView in MyCarsFragment.
 * It takes the Hashmap from the posts_lists at the position that is passed.
 */

public class MyCarsActivity extends AppCompatActivity {
    private ImageView big_imageView;
    private HashMap<String, String> postHashmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycars);
        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("position");
        postHashmap = (HashMap<String, String>) MyCarsFragment.getposts_list().get(position);
        setImages();
        setTexts();
    }

    //set the imageViews to the fetched data from the carPosts
    private void setImages(){
        big_imageView = (ImageView) findViewById(R.id.big_imageView);
        ImageView small1_imageView = (ImageView) findViewById(R.id.small1_imageView);
        ImageView small2_imageView = (ImageView) findViewById(R.id.small2_imageView);
        ImageView small3_imageView = (ImageView) findViewById(R.id.small3_imageView);
        glide(postHashmap.get(MainActivity.getURL1()),big_imageView);
        glide(postHashmap.get(MainActivity.getURL1()),small1_imageView);
        glide(postHashmap.get(MainActivity.getURL2()),small2_imageView);
        glide(postHashmap.get(MainActivity.getURL3()),small3_imageView);
        small1_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(postHashmap.get(MainActivity.getURL1()),big_imageView);
            }
        });
        small2_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(postHashmap.get(MainActivity.getURL2()),big_imageView);
            }
        });
        small3_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glide(postHashmap.get(MainActivity.getURL3()),big_imageView);
            }
        });
    }

    //set textViews to the fetched data from the carPosts
    private void setTexts(){
        TextView makemodel_textView = (TextView) findViewById(R.id.makemodel_textView);
        makemodel_textView.setText("Make: " + postHashmap.get(MainActivity.getMAKE()) + "   " + "Model: " + postHashmap.get(MainActivity.getMODEL()));

        TextView registration_textView = (TextView) findViewById(R.id.registration_textView);
        registration_textView.setText("Registration Number: " + postHashmap.get(MainActivity.getREGISTRATION()));

        TextView engine_textView = (TextView) findViewById(R.id.engine_textView);
        engine_textView.setText("Engine Size " + postHashmap.get(MainActivity.getENGINE()));

        TextView message_textView = (TextView) findViewById(R.id.message_textView);
        message_textView.setText(postHashmap.get(MainActivity.getDESCRIPTION()));

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

}
