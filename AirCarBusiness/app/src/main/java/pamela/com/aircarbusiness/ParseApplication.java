package pamela.com.aircarbusiness;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by pamela on 08/11/2016.
 * This class extends Application and this class is for initialising the parse server.
 */

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("aircar28521Wdsaj42") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explic// itly configured on Parse server
                .server("https://aircar.herokuapp.com/parse/").build());

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
