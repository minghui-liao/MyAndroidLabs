package algonquin.cst2335.liao0026;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author  Minghui Liao
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    /** This holds the text at the center of the screen */
    private TextView tv = null;

    /** This holds the edittext below the textview and centered horizontally */
    private EditText et = null;

    /** This holds the button at the bottom */
    private Button btn = null;

    String stringURL;
    Bitmap image = null;


    String description = null;
    String iconName = null;
    String current = null;
    String min = null;
    String max = null;
    String humidity = null;

    TextView currentTemp;
    TextView minTemp;
    TextView maxTemp;
    TextView humidityView;
    TextView descriptionView;
    ImageView icon;
    EditText cityText;

    float oldSize = 14;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.hide_views:
                currentTemp.setVisibility(View.INVISIBLE);
                minTemp.setVisibility(View.INVISIBLE);
                maxTemp.setVisibility(View.INVISIBLE);
                humidityView.setVisibility(View.INVISIBLE);
                descriptionView.setVisibility(View.INVISIBLE);
                icon.setVisibility(View.INVISIBLE);
                cityText.setText("");
                break;
            case R.id.id_increase:
                oldSize++;
                currentTemp.setTextSize(oldSize);
                minTemp.setTextSize(oldSize);
                maxTemp.setTextSize(oldSize);
                humidityView.setTextSize(oldSize);
                descriptionView.setTextSize(oldSize);
                cityText.setTextSize(oldSize);
                break;
            case R.id.id_decrease:
                oldSize = Float.max(oldSize-1, 5);
                currentTemp.setTextSize(oldSize);
                minTemp.setTextSize(oldSize);
                maxTemp.setTextSize(oldSize);
                humidityView.setTextSize(oldSize);
                descriptionView.setTextSize(oldSize);
                cityText.setTextSize(oldSize);
                break;
            case 5:
                String cityName = item.getTitle().toString();
                runForecast(cityName);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void runForecast(String cityName) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Getting forecast")
                .setMessage("We're calling people in " + cityName + " to look outside their windows and tell us what's the weather like over there.")
                .setView( new ProgressBar(MainActivity.this))
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            /* This runs in a separate thread */
            try {
                //String cityName = cityText.getText().toString();
                //connect to the server:
                stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                        + URLEncoder.encode(cityName, "UTF-8")
                        + "&appid=7e943c97096a9784391a981c4d878b22&units=metric&mode=xml";
                //on other cpu:
                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                //This code creates a pull parser from the inputStream in that you got from the server:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( in  , "UTF-8");

                while( xpp.next() != XmlPullParser.END_DOCUMENT )
                {
                    switch ( xpp.getEventType())
                    {
                        case XmlPullParser.START_TAG:
                            if (xpp.getName().equals("temperature"))
                            {
                                current = xpp.getAttributeValue(null, "value");  //this gets the current temperature
                                min = xpp.getAttributeValue(null, "min"); //this gets the min temperature
                                max = xpp.getAttributeValue(null, "max"); //this gets the max temperature
                            }
                            else if (xpp.getName().equals("weather"))
                            {
                                description = xpp.getAttributeValue(null, "value");  //this gets the weather description
                                iconName = xpp.getAttributeValue(null, "icon"); //this gets the icon name
                            }
                            else if (xpp.getName().equals("humidity"))
                            {
                                humidity = xpp.getAttributeValue(null, "value");
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            break;
                    }
                }

//                    String text = (new BufferedReader(
//                            new InputStreamReader(in, StandardCharsets.UTF_8)))
//                            .lines()
//                            .collect(Collectors.joining("\n"));
//
//                    JSONObject theDocument = new JSONObject( text );
//
//                    JSONArray weatherArray = theDocument.getJSONArray ( "weather" );
//                    JSONObject position0 = weatherArray.getJSONObject(0);
//
//                    String description = position0.getString("description");
//                    String iconName = position0.getString("icon");
//
//                    JSONObject mainObject = theDocument.getJSONObject( "main" );
//                    double current = mainObject.getDouble("temp");
//                    double min = mainObject.getDouble("temp_min");
//                    double max = mainObject.getDouble("temp_max");
//                    int humidity = mainObject.getInt("humidity");
//
                File file = new File(getFilesDir(), iconName + ".png");
                if(file.exists()) {
                    image = BitmapFactory.decodeFile(getFilesDir() + "/" + iconName + ".png");
                }
                else {
                    URL imgUrl = new URL( "https://openweathermap.org/img/w/" + iconName + ".png" );
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());
                        image.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput(iconName+".png", Activity.MODE_PRIVATE));
                    }
                }
//
                runOnUiThread( (  )  -> {

                    currentTemp.setText("The current tempearture is " + current);
                    currentTemp.setVisibility(View.VISIBLE);

                    minTemp.setText("The min temperature is " + min);
                    minTemp.setVisibility(View.VISIBLE);

                    maxTemp.setText("The max temperature is " + max);
                    maxTemp.setVisibility(View.VISIBLE);

                    humidityView.setText("The humidity is " + humidity + "%");
                    humidityView.setVisibility(View.VISIBLE);

                    descriptionView.setText(description);
                    descriptionView.setVisibility(View.VISIBLE);

                    icon.setImageBitmap(image);
                    icon.setVisibility(View.VISIBLE);

                    dialog.hide();
                });


            } catch (IOException | XmlPullParserException ioe) {
                Log.e("Connection error: ", ioe.getMessage());
            }
        } );




//        tv = findViewById(R.id.textView);
//        et = findViewById(R.id.editText);
//        btn = findViewById(R.id.button);

//        btn.setOnClickListener( clk -> {
//            String password = et.getText().toString();
//            if (checkPasswordComplexity( password )) {
//                tv.setText("Your password meets the requirements");
//            } else {
//                tv.setText("You shall not pass!");
//            }
//
//        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentTemp = findViewById(R.id.temp);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);
        humidityView = findViewById(R.id.humidity);
        descriptionView = findViewById(R.id.description);
        icon = findViewById(R.id.icon);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item)-> {
            onOptionsItemSelected(item);
            drawer.closeDrawer(GravityCompat.START);
            return false;
        });

        Button forecastBtn = findViewById(R.id.forecastButton);
        cityText = findViewById(R.id.cityTextField);

        forecastBtn.setOnClickListener( (click) ->{
            String cityName = cityText.getText().toString();

            myToolbar.getMenu().add( 0, 5, 0, cityName).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            runForecast(cityName);

//            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("Getting forecast")
//                    .setMessage("We're calling people in " + cityName + " to look outside their windows and tell us what's the weather like over there.")
//                    .setView( new ProgressBar(MainActivity.this))
//                    .show();
//
//            Executor newThread = Executors.newSingleThreadExecutor();
//            newThread.execute( () -> {
//                /* This runs in a separate thread */
//                try {
//                    //String cityName = cityText.getText().toString();
//                    //connect to the server:
//                    stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
//                            + URLEncoder.encode(cityName, "UTF-8")
//                            + "&appid=7e943c97096a9784391a981c4d878b22&units=metric&mode=xml";
//                    //on other cpu:
//                    URL url = new URL(stringURL);
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//                    //This code creates a pull parser from the inputStream in that you got from the server:
//                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//                    factory.setNamespaceAware(false);
//                    XmlPullParser xpp = factory.newPullParser();
//                    xpp.setInput( in  , "UTF-8");
//
//                    while( xpp.next() != XmlPullParser.END_DOCUMENT )
//                    {
//                        switch ( xpp.getEventType())
//                        {
//                            case XmlPullParser.START_TAG:
//                                if (xpp.getName().equals("temperature"))
//                                {
//                                    current = xpp.getAttributeValue(null, "value");  //this gets the current temperature
//                                    min = xpp.getAttributeValue(null, "min"); //this gets the min temperature
//                                    max = xpp.getAttributeValue(null, "max"); //this gets the max temperature
//                                }
//                                else if (xpp.getName().equals("weather"))
//                                {
//                                    description = xpp.getAttributeValue(null, "value");  //this gets the weather description
//                                    iconName = xpp.getAttributeValue(null, "icon"); //this gets the icon name
//                                }
//                                else if (xpp.getName().equals("humidity"))
//                                {
//                                    humidity = xpp.getAttributeValue(null, "value");
//                                }
//                                break;
//                            case XmlPullParser.END_TAG:
//                                break;
//                            case XmlPullParser.TEXT:
//                                break;
//                        }
//                    }
//
////                    String text = (new BufferedReader(
////                            new InputStreamReader(in, StandardCharsets.UTF_8)))
////                            .lines()
////                            .collect(Collectors.joining("\n"));
////
////                    JSONObject theDocument = new JSONObject( text );
////
////                    JSONArray weatherArray = theDocument.getJSONArray ( "weather" );
////                    JSONObject position0 = weatherArray.getJSONObject(0);
////
////                    String description = position0.getString("description");
////                    String iconName = position0.getString("icon");
////
////                    JSONObject mainObject = theDocument.getJSONObject( "main" );
////                    double current = mainObject.getDouble("temp");
////                    double min = mainObject.getDouble("temp_min");
////                    double max = mainObject.getDouble("temp_max");
////                    int humidity = mainObject.getInt("humidity");
////
//                    File file = new File(getFilesDir(), iconName + ".png");
//                    if(file.exists()) {
//                        image = BitmapFactory.decodeFile(getFilesDir() + "/" + iconName + ".png");
//                    }
//                    else {
//                        URL imgUrl = new URL( "https://openweathermap.org/img/w/" + iconName + ".png" );
//                        HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
//                        connection.connect();
//                        int responseCode = connection.getResponseCode();
//                        if (responseCode == 200) {
//                            image = BitmapFactory.decodeStream(connection.getInputStream());
//                            image.compress(Bitmap.CompressFormat.PNG, 100, openFileOutput(iconName+".png", Activity.MODE_PRIVATE));
//                        }
//                    }
////
//                    runOnUiThread( (  )  -> {
//                        currentTemp = findViewById(R.id.temp);
//                        currentTemp.setText("The current tempearture is " + current);
//                        currentTemp.setVisibility(View.VISIBLE);
//
//                        minTemp = findViewById(R.id.minTemp);
//                        minTemp.setText("The min temperature is " + min);
//                        minTemp.setVisibility(View.VISIBLE);
//
//                        maxTemp = findViewById(R.id.maxTemp);
//                        maxTemp.setText("The max temperature is " + max);
//                        maxTemp.setVisibility(View.VISIBLE);
//
//                        humidityView = findViewById(R.id.humidity);
//                        humidityView.setText("The humidity is " + humidity + "%");
//                        humidityView.setVisibility(View.VISIBLE);
//
//                        descriptionView = findViewById(R.id.description);
//                        descriptionView.setText(description);
//                        descriptionView.setVisibility(View.VISIBLE);
//
//                        icon = findViewById(R.id.icon);
//                        icon.setImageBitmap(image);
//                        icon.setVisibility(View.VISIBLE);
//
//                        dialog.hide();
//                    });
//
//
//                } catch (IOException | XmlPullParserException ioe) {
//                    Log.e("Connection error: ", ioe.getMessage());
//                }
//            } );
        });



//        tv = findViewById(R.id.textView);
//        et = findViewById(R.id.editText);
//        btn = findViewById(R.id.button);

//        btn.setOnClickListener( clk -> {
//            String password = et.getText().toString();
//            if (checkPasswordComplexity( password )) {
//                tv.setText("Your password meets the requirements");
//            } else {
//                tv.setText("You shall not pass!");
//            }
//
//        });
    }

    /** This function checks the char has an special symbol.
     *
     * @param c The char object that we are checking
     * @return Returns true if the char c is a special symbol, or false if it is not a special symbol.
     */
    boolean isSpecialCharacter(char c)
    {
        switch (c)
        {
            case '#':
            case '$':
            case '%':
            case '^':
            case '&':
            case '!':
            case '@':
            case '?':
            case '*':
                return true; //return true if c is one of: #$%^&*!@?
            default:return false; //return false otherwise

        }
    }

    /** This function checks the complexity of the password.
     *  It checks if the password has an Upper Case letter, a lower case letter, a number, and a special symbol.
     *
     * @param pw The String object that we are checking
     * @return Return true if the password is complex enough, return false if is not complex enough.
     */
    boolean checkPasswordComplexity(String pw) {
        boolean foundUpperCase, foundLowerCase, foundNumber, foundSpecial;
        foundUpperCase = foundLowerCase = foundNumber = foundSpecial = false;

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        for (int i = 0; i < pw.length(); i++) {
            char c = pw.charAt(i);
            if (Character.isUpperCase(c)) foundUpperCase = true;
            if (Character.isLowerCase(c)) foundLowerCase = true;
            if (Character.isDigit(c)) foundNumber = true;
            if (isSpecialCharacter(c)) foundSpecial = true;
        }

        if(!foundUpperCase)
        {
            Toast.makeText(context, "The password is missing an upper case letter", duration) // Say that they are missing an upper case letter;
            .show();
            return false;
        }
        else if( ! foundLowerCase)
        {
            Toast.makeText(context, "The password is missing a lower case letter", duration) // Say that they are missing a lower case letter;
            .show();
            return false;
        }
        else if( ! foundNumber) {
            Toast.makeText(context, "The password is missing a number", duration) // Say that they are missing a number;
            .show();
            return false;
        }
        else if(! foundSpecial) {
            Toast.makeText(context, "The password is missing a special", duration) // Say that they are missing a special symbol;
            .show();
            return false;
        }
        else
            return true; //only get here if they're all true
    }
}