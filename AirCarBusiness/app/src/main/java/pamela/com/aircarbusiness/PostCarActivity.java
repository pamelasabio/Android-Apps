package pamela.com.aircarbusiness;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.Reference;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pamela.com.aircarbusiness.Fragments.MyCarsFragment;

/**
 * Created by pamela on 13/11/2016.
 * This Activity is for posting cars and is called when the user clicked Post Car button from MyCarsFragment
 * The user enters all the details of the car. It is important that the user must enter all details for the car.
 * If the user doesn't enter all details then the car won't be posted.
 * If the car is succesfully posted then it is saved to the database.
 */

public class PostCarActivity extends AppCompatActivity {
    private Spinner make_spinner;
    private Spinner model_spinner;
    private EditText registrationNo_editText;
    private EditText engineSize_editText;
    private EditText description_editText;
    private Button from_button;
    private Button to_button;
    private EditText price_editText;
    private EditText address_editText;
    private EditText city_editText;
    private DatePickerDialog datePickerDialog;
    private ImageView car_image1;
    private ImageView car_image2;
    private ImageView car_image3;
    public int imagepos = 0;
    private static int RESULT_LOAD_IMG = 1;
    private String imgDecodableString;
    private LatLng latLng;

    private static int from_year;
    private static int from_month;
    private static int from_day;
    private static int to_year;
    private static int to_month;
    private static int to_day;

    //permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //getters for dates
    private static int getFromYear(){return from_year;}
    private static int getFromMonth(){return from_month;}
    private static int getFromDay(){return from_day;}
    private static int getToYear(){return to_year;}
    private static int getToMonth(){return to_month;}
    private static int getToDay(){return to_day;}

    private ParseFile pic1file;
    private ParseFile pic2file;
    private ParseFile pic3file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcar);
        verifyStoragePermissions(PostCarActivity.this);
        setSpinners();
        setDates();
        setImages();


        Button post_button = (Button) findViewById(R.id.post_button);
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                make_spinner = (Spinner) findViewById(R.id.make_spinner);
                model_spinner = (Spinner) findViewById(R.id.model_spinner);
                registrationNo_editText = (EditText) findViewById(R.id.registrationNo_editText);
                engineSize_editText = (EditText) findViewById(R.id.engineSize_editText);
                description_editText = (EditText) findViewById(R.id.description_editText);
                from_button = (Button) findViewById(R.id.from_button);
                to_button = (Button) findViewById(R.id.to_button);
                price_editText = (EditText) findViewById(R.id.price_editText) ;
                address_editText = (EditText) findViewById(R.id.address_editText);
                city_editText = (EditText) findViewById(R.id.city_editText);

                //Check if the user entered all details
                if(make_spinner.getSelectedItem().equals("Any Make") || model_spinner.getSelectedItem().equals("Any Model") || TextUtils.isEmpty(registrationNo_editText.getText()) || TextUtils.isEmpty(engineSize_editText.getText()) || TextUtils.isEmpty(description_editText.getText()) || from_button.getText().equals("From") || to_button.getText().equals("To") || TextUtils.isEmpty(price_editText.getText()) || TextUtils.isEmpty(address_editText.getText()) || TextUtils.isEmpty(city_editText.getText())) {
                    Toast.makeText(PostCarActivity.this, "Please enter all details", Toast.LENGTH_LONG).show();
                    //check if the user didn't select a make
                    if(make_spinner.getSelectedItem().equals("Any Make")) {
                        TextView text = (TextView) make_spinner.getChildAt(0);
                        text.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't select a model
                    if(model_spinner.getSelectedItem().equals("Any Model"))    {
                        TextView text = (TextView) model_spinner.getChildAt(0);
                        text.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter registration number
                    if (TextUtils.isEmpty(registrationNo_editText.getText())) {
                        registrationNo_editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter engine size
                    if (TextUtils.isEmpty(engineSize_editText.getText())) {
                        engineSize_editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter description
                    if (TextUtils.isEmpty(description_editText.getText())) {
                        description_editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter from date
                    if(from_button.getText().equals("From")){
                        from_button.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter to date
                    if(to_button.getText().equals("To")){
                        to_button.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter price
                    if(TextUtils.isEmpty(price_editText.getText())){
                        price_editText.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter address
                    if(TextUtils.isEmpty(address_editText.getText())){
                        address_editText.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    //check if the user didn't enter city
                    if(TextUtils.isEmpty(city_editText.getText())){
                        city_editText.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                }else{
                    postCar();
                }
            }
        });

    }

    //This method sets values to the make_spinner dropdown and the model_spinner dropdown
    private void setSpinners() {
        make_spinner = (Spinner) findViewById(R.id.make_spinner);
        model_spinner = (Spinner) findViewById(R.id.model_spinner);
        final ArrayAdapter makeAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.makeArray, R.layout.support_simple_spinner_dropdown_item);
        ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.emptyArray, R.layout.support_simple_spinner_dropdown_item);
        model_spinner.setAdapter(modelAdapter);

        make_spinner.setAdapter(makeAdapter);
        make_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] makeArray = getResources().getStringArray(R.array.makeArray);
                if (make_spinner.getSelectedItem().equals(makeArray[0])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.emptyArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[1])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.alfaRomeoArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[2])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.audiArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[3])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.bmwArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[4])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.fiatArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[5])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.fordArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[6])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.hondaArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[7])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.hyundaiArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[8])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.lexusArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[9])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.mazdaArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[10])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.mercedesArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[11])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.nissanArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[12])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.openArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[13])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.peugeotArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[14])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.seatArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                } else if (make_spinner.getSelectedItem().equals(makeArray[15])) {
                    ArrayAdapter modelAdapter = ArrayAdapter.createFromResource(PostCarActivity.this, R.array.volvoArray, R.layout.support_simple_spinner_dropdown_item);
                    model_spinner.setAdapter(modelAdapter);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //This method gets the date from the date picker and sets the date in the database
    private void setDates() {
        from_button = (Button) findViewById(R.id.from_button);
        from_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                final int mYear = c.get(Calendar.YEAR); // current year
                final int mMonth = c.get(Calendar.MONTH); // current month
                final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(PostCarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {

                        if(year <= mYear && month <= mMonth && day < mDay){
                            Toast.makeText(PostCarActivity.this, "You have entered a date in the past. The date must be in the future", Toast.LENGTH_LONG).show();
                        }else {
                            // set day of month , month and year value in the edit text
                            from_year = year;
                            from_month = month;
                            from_day = day;
                            from_button.setText("From: " + day + "/" + (month + 1) + "/" + year);
                        }

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        to_button = (Button) findViewById(R.id.to_button);
        to_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(PostCarActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {
                        if(year <= getFromYear() && month <= getFromMonth() && day <= getFromDay()){
                            Toast.makeText(PostCarActivity.this, "The date must be after the From date ", Toast.LENGTH_LONG).show();
                        }else {
                            // set day of month , month and year value in the edit text
                            to_year = year;
                            to_month = month;
                            to_day = day;
                            to_button.setText("To: " + day + "/" + (month + 1) + "/" + year);
                        }

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    //This method sets the images for uploading in the post
    public void setImages() {
        car_image1 = (ImageView) findViewById(R.id.car_image1);
        car_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to Open Image applications like Gallery, Google Photos
                imagepos = 1;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        car_image2 = (ImageView) findViewById(R.id.car_image2);
        car_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagepos = 2;
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        car_image3 = (ImageView) findViewById(R.id.car_image3);
        car_image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagepos = 3;
                // Create intent to Open Image applications like Gallery, Google Photos
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });
    }

    //This is method called when a photo is picked from the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
                ByteArrayOutputStream bytearrayoutputstream  = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytearrayoutputstream);
                if(imagepos == 1) {
                    car_image1.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
                    pic1file = new ParseFile("hello.png",bytearrayoutputstream.toByteArray());
                    Toast.makeText(this, pic1file.toString(), Toast.LENGTH_SHORT).show();
                }else if(imagepos == 2){
                    car_image2.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
                    pic2file = new ParseFile("hello1.png",bytearrayoutputstream.toByteArray());
                }else if(imagepos == 3){
                    car_image3.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
                    pic3file = new ParseFile("hello2.png",bytearrayoutputstream.toByteArray());
                }

            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    //This method checks the storage permission in run time. If the app doesn't have permission then we have to ask the user for permission
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    //This method takes an address and gets the latitude and the longitude of the address
    //Reference : this code is from http://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latlng = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latlng = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return latlng;
    }
    //end of reference

    //This method puts the details of the car in the database so that it can be fetched and be shown in the My Cars tab
    private void postCar(){

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar1.set(Calendar.YEAR,getFromYear());
        calendar1.set(Calendar.MONTH,getFromMonth());
        calendar1.set(Calendar.DAY_OF_MONTH,getFromDay());
        Date date1 = calendar1.getTime();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar2.set(Calendar.YEAR,getToYear());
        calendar2.set(Calendar.MONTH,getToMonth());
        calendar2.set(Calendar.DAY_OF_MONTH,getToDay());
        Date date2 = calendar2.getTime();

        latLng = getLocationFromAddress(PostCarActivity.this, String.valueOf(address_editText.getText()));
        ParseGeoPoint geoPoint = new ParseGeoPoint();
        if(latLng != null) {
            geoPoint.setLatitude(latLng.latitude);
            geoPoint.setLongitude(latLng.longitude);
        }else{
            geoPoint.setLatitude(53.3568422);
            geoPoint.setLongitude(-6.3780174);
        }


        byte[] bytes = getResources().getResourceName(android.R.drawable.ic_menu_gallery).getBytes();
        if(pic1file == null){
            pic1file = new ParseFile("pic.png",bytes);
        }
        if(pic2file == null){
            pic2file = new ParseFile("pic.png",bytes);
        }
        if(pic3file == null){
            pic3file = new ParseFile("pic.png",bytes);
        }

        pic1file.saveInBackground();
        pic2file.saveInBackground();
        pic3file.saveInBackground();

        ParseObject object = new ParseObject(MainActivity.getCAR_POST_TABLE());
        ParseUser user = ParseUser.getCurrentUser();
        object.put(MainActivity.getMAKE(),make_spinner.getSelectedItem());
        object.put(MainActivity.getMODEL(),model_spinner.getSelectedItem());
        object.put(MainActivity.getREGISTRATION(),String.valueOf(registrationNo_editText.getText()));
        object.put(MainActivity.getENGINE(),String.valueOf(engineSize_editText.getText()));
        object.put(MainActivity.getDESCRIPTION(),String.valueOf(description_editText.getText()));
        object.put(MainActivity.getFROM(),date1);
        object.put(MainActivity.getTO(),date2);
        object.put(MainActivity.getPRICE(),Float.parseFloat(price_editText.getText().toString()));
        object.put(MainActivity.getLOCATION(),geoPoint);
        object.put(MainActivity.getCITY(),String.valueOf(city_editText.getText()).toLowerCase());
        object.put(MainActivity.getUSER_ID(),String.valueOf(user.getObjectId()));
        object.put(MainActivity.getPOSTED(),1);
        object.put(MainActivity.getPIC1(),pic1file);
        object.put(MainActivity.getPIC2(),pic2file);
        object.put(MainActivity.getPIC3(),pic3file);

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Toast.makeText(PostCarActivity.this, "done", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(PostCarActivity.this, MainActivity.class);
                    startActivity(i);
                }else {
                    e.printStackTrace();
                }
            }
        });
    }
}
