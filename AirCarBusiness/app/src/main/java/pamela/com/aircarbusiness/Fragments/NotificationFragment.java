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
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pamela.com.aircarbusiness.Adapters.CustomBaseAdapter;
import pamela.com.aircarbusiness.MainActivity;
import pamela.com.aircarbusiness.NotificationActivity;
import pamela.com.aircarbusiness.R;

/**
 * Created by pamela on 13/11/2016.
 * This class extends Fragment and is called when the second tab on the bottom toolbar is clicked.
 * This fragment shows notifications when customers want to reserve the user's car in a ListView.
 * When the user clicks a notification from the ListView then it goes to NotificationActivity and shows more information.
 */

public class NotificationFragment extends Fragment {
    private HashMap<String, String> notifications;
    private ListView listView;
    private static List<HashMap<String,String>> notifications_list = new ArrayList<HashMap<String, String>>();
    public static List getnotifications_list(){return notifications_list;}

    //getter for the instance of the class
    public static NotificationFragment getInstance() {
        NotificationFragment notificationFragment = new NotificationFragment();
        return notificationFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notifications_list.clear();
        listView = (ListView) view.findViewById(R.id.notification_listView);
        getNotifications();
    }

    //fetching all the data from the Notifications table in the database
    private void getNotifications(){
        final ParseQuery<ParseObject> notifications_query = new ParseQuery<ParseObject>(MainActivity.getNOTIFICATIONS_TABLE()); //make queries from Notifications table
        ParseUser user = ParseUser.getCurrentUser(); //get the current user
        notifications_query.whereEqualTo(MainActivity.getPOSTER_ID(),user.getObjectId()); //get notifications of user
        notifications_query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) { //execute query
                if(e == null){
                    if(objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            notifications = new HashMap<>();
                            notifications.put(MainActivity.getOBJECT_ID(), objects.get(i).getObjectId());
                            notifications.put(MainActivity.getEMAIL(), objects.get(i).get(MainActivity.getEMAIL()).toString());
                            notifications.put(MainActivity.getMESSAGE(), objects.get(i).get(MainActivity.getMESSAGE()).toString());
                            notifications.put(MainActivity.getPOST_ID(), objects.get(i).get(MainActivity.getPOST_ID()).toString());
                            notifications.put(MainActivity.getCUSTOMER_ID(), objects.get(i).get(MainActivity.getCUSTOMER_ID()).toString());

                            notifications_list.add(i, notifications); //add the fetched data to notifications_list ArrayList
                            getCarPosts(i, objects.size()); //call getCarposts() parameters passed: index and size of objects
                        }//end for loop
                    }else{
                        Log.i("NotificationFragment", "There are no posts");
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }//end getNofifications()

    //fetch data from the carPost table where the objectId int carPost table is equal to postId in Notifications table
    private void getCarPosts(final int i, final int finalSize){
        ParseQuery<ParseObject> car_query = ParseQuery.getQuery(MainActivity.getCAR_POST_TABLE());
        car_query.whereEqualTo(MainActivity.getOBJECT_ID(),notifications_list.get(i).get(MainActivity.getPOST_ID()));
        car_query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    if(i<= notifications_list.size()){
                        ParseFile file = object.getParseFile(MainActivity.getPIC1());
                        ParseFile file2 = object.getParseFile(MainActivity.getPIC2());
                        ParseFile file3 = object.getParseFile(MainActivity.getPIC2());

                        notifications_list.get(i).put(MainActivity.getURL1(),file.getUrl());
                        notifications_list.get(i).put(MainActivity.getURL2(), file2.getUrl());
                        notifications_list.get(i).put(MainActivity.getURL3(),file3.getUrl());
                        notifications_list.get(i).put(MainActivity.getMAKE(), object.get(MainActivity.getMAKE()).toString());
                        notifications_list.get(i).put(MainActivity.getMODEL(), object.get(MainActivity.getMODEL()).toString());
                        notifications_list.get(i).put(MainActivity.getDESCRIPTION(),object.get(MainActivity.getDESCRIPTION()).toString());

                        // if all the data is in the memory, it can me manipulated
                        if(i == finalSize - 1){
                            //call the CustomBaseAdapter to display data on a ListView parameters passed: context, ArrayList and fragment number.
                            CustomBaseAdapter adapter = new CustomBaseAdapter(getContext(), notifications_list, 1);
                            if (listView != null) {
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(getContext(), NotificationActivity.class);
                                        i.putExtra("position", position); //pass the position of the clicked item
                                        startActivity(i);
                                    }
                                });
                            }else {
                                Log.i("NotificationsFragment", "ListView is null");
                            }
                        }
                    }else{
                        // means that threads are executing in different time
                        // to avoid null pointer exception
                        getCarPosts(i,finalSize); // recursive algorithm
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }//end getCarPosts()
}
