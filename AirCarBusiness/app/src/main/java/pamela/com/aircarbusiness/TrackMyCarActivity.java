package pamela.com.aircarbusiness;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;

import java.util.HashMap;

import pamela.com.aircarbusiness.Fragments.TrackMyCarFragment;

/**
 * Created by pamela on 27/11/2016.
 * This Activity shows the location of the chosen car in the map.
 * It takes the position of the clicked item from the ListView in MyCarsFragment.
 * It takes the Hashmap from the geopoint at the position that is passed which store the locations.
 * It adds a marker to the map where the car is located.
 */

public class TrackMyCarActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, ParseGeoPoint> locationHashmap;
    private HashMap<String, String> carHasmap;
    private ParseGeoPoint location;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_my_car);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("position");
        locationHashmap = (HashMap<String, ParseGeoPoint>) TrackMyCarFragment.getgeopoint_list().get(position);
        carHasmap = (HashMap<String, String>) TrackMyCarFragment.gettrackmycar_list().get(position);
        location = locationHashmap.get((MainActivity.getLOCATION()));
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(latLng.latitude, latLng.longitude);
        mMap.addMarker(new MarkerOptions().position(location).title(carHasmap.get(MainActivity.getMAKE() + " " + carHasmap.get(MainActivity.getMODEL()))));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        pointToPosition(location);
    }

    private void pointToPosition(LatLng position) {
        //Build camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(17).build();
        //Zoom in and animate the camera.
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
