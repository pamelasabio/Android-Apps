package pamela.com.aircarbusiness.Fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pamela.com.aircarbusiness.Adapters.CustomBaseAdapter;
import pamela.com.aircarbusiness.MainActivity;
import pamela.com.aircarbusiness.PostCarActivity;
import pamela.com.aircarbusiness.R;
import pamela.com.aircarbusiness.TrackMyCarActivity;

/**
 * Created by pamela on 13/11/2016.
 * This class extends Fragment and is called when the fourth tab on the bottom toolbar is clicked.
 * This fragment shows the reserved cars in a ListView.
 * When the user clicks a notification from the ListView then it goes to TrackMyCarActivity and shows the location of the car in the map.
 */

public class TrackMyCarFragment extends Fragment {
    private HashMap<String, String> trackmycar;
    private HashMap<String, ParseGeoPoint> geoPointHashMap;
    private ListView listView;
    private static List<HashMap<String,String>> trackmycar_list = new ArrayList<HashMap<String, String>>();
    public static List gettrackmycar_list(){return trackmycar_list;}
    private static List<HashMap<String,ParseGeoPoint>> geopoint_list = new ArrayList<HashMap<String, ParseGeoPoint>>();
    public static List getgeopoint_list(){return geopoint_list;}

    //getter for the instance of the class
    public static TrackMyCarFragment getInstance(){
        TrackMyCarFragment trackMyCarFragment = new TrackMyCarFragment();
        return trackMyCarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trackmycar_list.clear();
        listView = (ListView) view.findViewById(R.id.reservation_listView);
        getReservations();
    }

    //fetching all the data from the Reservation table in the database
    private void getReservations() {
        ParseQuery<ParseObject> reservation_query = new ParseQuery<ParseObject>(MainActivity.getRESERVATION_TABLE());
        ParseUser user = ParseUser.getCurrentUser();
        reservation_query.whereEqualTo(MainActivity.getPOSTER_ID(), user.getObjectId());
        reservation_query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        for (int i = 0; i < objects.size(); i++) {
                            trackmycar = new HashMap<>();
                            trackmycar.put(MainActivity.getRESERVATION_ID(), objects.get(i).getObjectId());
                            trackmycar.put(MainActivity.getPOST_ID(), objects.get(i).get(MainActivity.getPOST_ID()).toString());
                            trackmycar_list.add(i, trackmycar);
                            ParseObject object = new ParseObject(MainActivity.getLOCATION_TABLE());
                            ParseUser user = ParseUser.getCurrentUser();
                            object.put(MainActivity.getRESERVATION_ID(),objects.get(i).getObjectId());
                            ParseGeoPoint geoPoint = new ParseGeoPoint();
                            geoPoint.setLatitude(53.3568422);
                            geoPoint.setLongitude(-6.3780174);
                            object.put(MainActivity.getLOCATION(),geoPoint);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e == null) {
                                        Log.i("TrackMyCarFragment","Location added");
                                    }else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            getLocations(i);
                            getCarPosts(i, objects.size());
                        }
                    } else {
                        Log.i("TrackMyCarFragment", "There are no cars to track");
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }//end getReservations

    ////fetching the location of the reserved car from the Location table in the database where reservationId in the Location table is equal to objectId from Reservation table
    private void getLocations(final int i){
        ParseQuery<ParseObject> locations_query = ParseQuery.getQuery(MainActivity.getLOCATION_TABLE());
        locations_query.whereEqualTo(MainActivity.getRESERVATION_ID(),trackmycar_list.get(i).get(MainActivity.getRESERVATION_ID()));
        locations_query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if (i <= trackmycar_list.size()) {
                        geoPointHashMap = new HashMap<>();
                        geoPointHashMap.put(MainActivity.getLOCATION(), (ParseGeoPoint) object.get(MainActivity.getLOCATION()));
                        geopoint_list.add(i, geoPointHashMap);
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }//end getLocations()

    //fetch data from the carPost table where the objectId int carPost table is equal to postId in Reservation table.
    private void getCarPosts(final int i, final int finalSize){
        ParseQuery<ParseObject> car_query = ParseQuery.getQuery(MainActivity.getCAR_POST_TABLE());
        car_query.whereEqualTo(MainActivity.getOBJECT_ID(),trackmycar_list.get(i).get(MainActivity.getPOST_ID()));
        car_query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    if(i<= trackmycar_list.size()){
                        trackmycar_list.get(i).put(MainActivity.getMAKE(), object.get(MainActivity.getMAKE()).toString());
                        trackmycar_list.get(i).put(MainActivity.getMODEL(), object.get(MainActivity.getMODEL()).toString());
                        ParseFile file = object.getParseFile(MainActivity.getPIC1());
                        ParseFile file2 = object.getParseFile(MainActivity.getPIC2());
                        ParseFile file3 = object.getParseFile(MainActivity.getPIC2());

                        trackmycar_list.get(i).put(MainActivity.getURL1(),file.getUrl());
                        trackmycar_list.get(i).put(MainActivity.getURL2(), file2.getUrl());
                        trackmycar_list.get(i).put(MainActivity.getURL3(),file3.getUrl());
                        if(i == finalSize - 1){  // means all the data is in the memory, it can me manipulated
                            CustomBaseAdapter adapter = new CustomBaseAdapter(getContext(), trackmycar_list, 3);
                            if (listView != null) {
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(getContext(), TrackMyCarActivity.class);
                                        i.putExtra("position", position);
                                        startActivity(i);
                                    }
                                });
                            }else {
                                Log.i("TrackMyCarFragment", "ListView is null");
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
