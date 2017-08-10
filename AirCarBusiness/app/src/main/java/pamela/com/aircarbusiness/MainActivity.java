package pamela.com.aircarbusiness;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import pamela.com.aircarbusiness.Adapters.MyAdapter;

/**
 * Created by pamela on 08/11/2016.
 * This Activity is the first to be loaded when the user runs the app.
 * This Activity handles the logging in, logging out and the fragments in the app.
 * This Activity stores the strings for the database - It is static therefore all classes can access it since we are fetching data.
 */

public class MainActivity extends AppCompatActivity {
    private MyAdapter myAdapter;
    private ViewPager viewPager;
    private static int NUM_ITEMS = 4;
    private SlidingTabLayout slidingTabLayout;
    public static int getNumItems() {
        return NUM_ITEMS;
    }

    //strings for database tables
    private static String CAR_POST_TABLE = "carPost";
    private static String NOTIFICATIONS_TABLE = "Notifications";
    private static String RESERVATION_TABLE = "Reservation";
    private static String LOCATION_TABLE = "Location";
    //getters for database tables string
    public static String getCAR_POST_TABLE(){return CAR_POST_TABLE;}
    public static String getNOTIFICATIONS_TABLE(){return NOTIFICATIONS_TABLE;}
    public static String getRESERVATION_TABLE(){return RESERVATION_TABLE;}
    public static String getLOCATION_TABLE(){return LOCATION_TABLE;};

    //strings for attributes in carPost table from database
    private static String OBJECT_ID = "objectId";
    private static String MAKE = "make";
    private static String MODEL = "model";
    private static String REGISTRATION = "reg";
    private static String ENGINE = "engine";
    private static String DESCRIPTION = "description";
    private static String FROM = "availableFrom";
    private static String TO = "availableTo";
    private static String PRICE = "priceDay";
    private static String CITY = "city";
    private static String POSTED = "posted";
    private static String PIC1 = "pic1";
    private static String PIC2 = "pic2";
    private static String PIC3 = "pic3";
    private static String USER_ID = "userId";
    //getters for carPost table strings
    public static String getOBJECT_ID(){return OBJECT_ID;}
    public static String getMAKE(){return MAKE;}
    public static String getMODEL(){return MODEL;}
    public static String getREGISTRATION(){return REGISTRATION;}
    public static String getENGINE(){return ENGINE;}
    public static String getDESCRIPTION(){return DESCRIPTION;}
    public static String getFROM(){return FROM;}
    public static String getTO(){return TO;}
    public static String getPRICE(){return PRICE;}
    public static String getCITY(){return CITY;}
    public static String getPOSTED(){return POSTED;}
    public static String getPIC1(){return PIC1;}
    public static String getPIC2(){return PIC2;}
    public static String getPIC3(){return PIC3;}
    public static String getUSER_ID(){return USER_ID;}

    //strings for attributes in Notifications table from database
    private static String POSTER_ID = "posterId";
    private static String CUSTOMER_ID = "customerId";
    private static String MESSAGE = "message";
    private static String POST_ID = "postId";
    private static String EMAIL = "email";
    //getters for Notifications table strings
    public static String getPOSTER_ID(){return POSTER_ID;}
    public static String getCUSTOMER_ID(){return CUSTOMER_ID;}
    public static String getMESSAGE(){return MESSAGE;}
    public static String getPOST_ID(){return POST_ID;}
    public static String getEMAIL(){return EMAIL;}

    //strings for attributes in Location table from database
    private static String RESERVATION_ID = "reservationId";
    private static String LOCATION = "location";
    //getters for Location table strings
    public static String getRESERVATION_ID(){return RESERVATION_ID;}
    public static String getLOCATION(){return LOCATION;}

    //strings for pic url
    private static String URL1 = "url1";
    private static String URL2 = "url2";
    private static String URL3 = "url3";
    //getters for pic url strings
    public static String getURL1(){return URL1;}
    public static String getURL2(){return URL2;}
    public static String getURL3(){return URL3;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logIn();
        setTabs();
    }

    //create options for menu on the top toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //handing item in menu click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //This method checks if the user is already logged in. If not, then it goes to LoginActivity.
    private void logIn(){
        //If the user is anonymous then the app goes to LoginActivity.
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        } else {
            //If the user is not anonymous then it gets the user data from Parse.
            ParseUser user = ParseUser.getCurrentUser();
            //If the user data is not null then the user is logged in to the app.
            if (user != null) {
                Toast.makeText(this, "Hello" + user.getUsername(), Toast.LENGTH_SHORT).show();
            } else {
                //If the user data is null then the app goes to LoginActivity.
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    //This method initializes all the tabs on the toolbars
    public void setTabs() {
        myAdapter = new MyAdapter(getSupportFragmentManager(),getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(myAdapter);
        //viewPager.setOffscreenPageLimit(3);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);

        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorloginEditText));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorloginEditText));
        // https://www.youtube.com/watch?v=f6jh64jJDrk
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.white);
            }
        });
        slidingTabLayout.setViewPager(viewPager);

    }

    //This method logs out the user if the log out option is clicked
    private void logOut() {
        ParseUser user = ParseUser.getCurrentUser();
        user.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    //override the back button. I am overriding it here because I don't want the back button to do something when it is pressed in the fragments
    @Override
    public void onBackPressed() {
        Log.i("Back", "Back button pressed");
    }
}

