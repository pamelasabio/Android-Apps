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
import pamela.com.aircarbusiness.ReservationActivity;

/**
 * Created by pamela on 13/11/2016.
 * This class extends Fragment and is called when the third tab on the bottom toolbar is clicked.
 * This fragment shows car reservations (when user accepted the reservation request) in a ListView.
 * When the user clicks a reservation from the ListView then it goes to ReservationsActivity and shows more information.
 */

public class ReservationFragment extends Fragment {
    private HashMap<String, String> reservations;
    private ListView listView;
    private static List<HashMap<String,String>> reservations_list = new ArrayList<HashMap<String, String>>();
    public static List getreservations_list(){return reservations_list;}

    //getter for the instance of the class
    public static ReservationFragment getInstance(){
        ReservationFragment reservationFragment = new ReservationFragment();
        return reservationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reservations_list.clear();
        listView = (ListView) view.findViewById(R.id.reservation_listView);
        getReservations();
    }

    //fetching all the data from the Reservation table in the database
    private void getReservations() {
        ParseQuery<ParseObject> reservation_query = new ParseQuery<ParseObject>(MainActivity.getRESERVATION_TABLE()); //make queries from Reservation table
        ParseUser user = ParseUser.getCurrentUser(); //get current user
        reservation_query.whereEqualTo(MainActivity.getPOSTER_ID(), user.getObjectId()); //get reservations
        reservation_query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) { //execute query
                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            reservations = new HashMap<>();
                            reservations.put(MainActivity.getPOST_ID(), objects.get(i).get(MainActivity.getPOST_ID()).toString());
                            reservations.put(MainActivity.getEMAIL(), objects.get(i).get(MainActivity.getEMAIL()).toString());
                            reservations_list.add(i, reservations);
                            getCarPosts(i, objects.size());
                        }
                    } else {
                        Log.i("ReservationFragment", "There are no reservations");
                    }
                } else{
                    e.printStackTrace();
                }
            }
        });
    }//end getReservations()


    //fetch data from the carPost table where the objectId int carPost table is equal to postId in Reservation table.
    private void getCarPosts(final int i, final int finalSize){
        ParseQuery<ParseObject> car_query = ParseQuery.getQuery(MainActivity.getCAR_POST_TABLE());
        car_query.whereEqualTo(MainActivity.getOBJECT_ID(),reservations_list.get(i).get(MainActivity.getPOST_ID()));
        car_query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    if(i<= reservations_list.size()){
                        ParseFile file = object.getParseFile(MainActivity.getPIC1());
                        ParseFile file2 = object.getParseFile(MainActivity.getPIC2());
                        ParseFile file3 = object.getParseFile(MainActivity.getPIC2());

                        reservations_list.get(i).put(MainActivity.getURL1(),file.getUrl());
                        reservations_list.get(i).put(MainActivity.getURL2(), file2.getUrl());
                        reservations_list.get(i).put(MainActivity.getURL3(),file3.getUrl());
                        reservations_list.get(i).put(MainActivity.getMAKE(), object.get(MainActivity.getMAKE()).toString());
                        reservations_list.get(i).put(MainActivity.getMODEL(), object.get(MainActivity.getMODEL()).toString());
                        reservations_list.get(i).put(MainActivity.getDESCRIPTION(),object.get(MainActivity.getDESCRIPTION()).toString());
                        reservations_list.get(i).put(MainActivity.getREGISTRATION(), object.get(MainActivity.getREGISTRATION()).toString());
                        reservations_list.get(i).put(MainActivity.getENGINE(), object.get(MainActivity.getENGINE()).toString());

                        if(i == finalSize - 1){  // means all the data is in the memory, it can me manipulated
                            //call the CustomBaseAdapter to display data on a ListView parameters passed: context, ArrayList and fragment number.
                            CustomBaseAdapter adapter = new CustomBaseAdapter(getContext(), reservations_list, 2);
                            if (listView != null) {
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(getContext(), ReservationActivity.class);
                                        i.putExtra("position", position); ////pass the position of the clicked item
                                        startActivity(i);
                                    }
                                });
                            }else {
                                Log.i("ReservationFragment", "ListView is null");
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
