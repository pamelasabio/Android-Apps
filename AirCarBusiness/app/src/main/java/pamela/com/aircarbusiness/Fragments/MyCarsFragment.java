package pamela.com.aircarbusiness.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pamela.com.aircarbusiness.Adapters.CustomBaseAdapter;
import pamela.com.aircarbusiness.MainActivity;
import pamela.com.aircarbusiness.MyCarsActivity;
import pamela.com.aircarbusiness.PostCarActivity;
import pamela.com.aircarbusiness.R;

/**
 * Created by pamela on 13/11/2016.
 * This class extends Fragment and is called when the first tab on the bottom toolbar is clicked.
 * This fragment shows the cars that the user has posted in a ListView.
 * There is a "Post a car" button and when it is clicked, then it goes to PostCarActivity.
 */

public class MyCarsFragment extends Fragment {
    private HashMap<String, String> posts;
    private ListView listView;
    private static List<HashMap<String, String>> posts_list = new ArrayList<HashMap<String, String>>();

    public static List getposts_list() {
        return posts_list;
    }

    //getter for the instance of the class
    public static MyCarsFragment getInstance() {
        MyCarsFragment myCarsFragmentFragment = new MyCarsFragment();
        return myCarsFragmentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mycars, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        posts_list.clear();
        listView = (ListView) view.findViewById(R.id.mycars_listView);
        getCarPosts();
        Button post_car = (Button) view.findViewById(R.id.post_car_button);
        post_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PostCarActivity.class);
                startActivity(i);
            }
        });
    }

    //fetching all the data from the carPost table in the database
    private void getCarPosts() {
        ParseQuery<ParseObject> posts_query = new ParseQuery<ParseObject>(MainActivity.getCAR_POST_TABLE()); //make queries from carPost table
        ParseUser user = ParseUser.getCurrentUser(); //get the current user
        posts_query.whereEqualTo(MainActivity.getUSER_ID(), user.getObjectId());  //find posts of user
        posts_query.findInBackground(new FindCallback<ParseObject>() { //execute query
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) { // Check if there was no errors
                    if (objects.size() > 0) {
                        //fetch data from post table and save into an ArrayList to pass to CustomBaseAdapter
                        for (int i = 0; i < objects.size(); i++) {
                            posts = new HashMap<String, String>();
                            posts.put(MainActivity.getOBJECT_ID(), objects.get(i).getObjectId());
                            posts.put(MainActivity.getMAKE(), objects.get(i).get(MainActivity.getMAKE()).toString());
                            posts.put(MainActivity.getMODEL(), objects.get(i).get(MainActivity.getMODEL()).toString());
                            posts.put(MainActivity.getREGISTRATION(), objects.get(i).get(MainActivity.getREGISTRATION()).toString());
                            posts.put(MainActivity.getENGINE(), objects.get(i).get(MainActivity.getENGINE()).toString());
                            posts.put(MainActivity.getDESCRIPTION(), objects.get(i).get(MainActivity.getDESCRIPTION()).toString());
                            posts.put(MainActivity.getURL1(), objects.get(i).getParseFile(MainActivity.getPIC1()).getUrl());
                            posts.put(MainActivity.getURL2(), objects.get(i).getParseFile(MainActivity.getPIC2()).getUrl());
                            posts.put(MainActivity.getURL3(), objects.get(i).getParseFile(MainActivity.getPIC3()).getUrl());

                            posts_list.add(i, posts); //add the fetched data to posts_list ArrayList

                            //call the CustomBaseAdapter to display data on a ListView parameters passed: context, arraylist and fragment number.
                            CustomBaseAdapter adapter = new CustomBaseAdapter(getContext(), posts_list, 0);
                            if (listView != null) {
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(getContext(), MyCarsActivity.class);
                                        i.putExtra("position", position); //pass the position of the clicked item
                                        startActivity(i);
                                    }
                                });
                            } else {
                                Log.i("MyCarsFragment", "ListView is null");
                            }
                        }//end for loop
                    } else {
                        Log.i("MyCarsFragment", "There are no posts");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }//end getCarPosts()
}
