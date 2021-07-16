package algonquin.cst2335.liao0026;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button forecastBtn = findViewById(R.id.forecastButton);
        EditText cityText = findViewById(R.id.cityTextField);

        forecastBtn.setOnClickListener( (click) ->{
            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute( () -> {
                /* This runs in a separate thread */
                try {
                    String cityName = cityText.getText().toString();
                    //connect to the server:
                    stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                            + URLEncoder.encode(cityName, "UTF-8")
                            + "&appid=7e943c97096a9784391a981c4d878b22&Units=Metric";
                    //on other cpu:
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    JSONObject theDocument = new JSONObject( text );

                    JSONArray weatherArray = theDocument.getJSONArray ( "weather" );
                    JSONObject position0 = weatherArray.getJSONObject(0);

                    String description = position0.getString("description");
                    String iconName = position0.getString("icon");

                    JSONObject mainObject = theDocument.getJSONObject( "main" );
                    double current = mainObject.getDouble("temp");
                    double min = mainObject.getDouble("temp_min");
                    double max = mainObject.getDouble("temp_max");
                    int humidity = mainObject.getInt("humidity");

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

                    runOnUiThread( (  )  -> {
                        TextView tv = findViewById(R.id.temp);
                        tv.setText("The current tempearture is " + current);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.minTemp);
                        tv.setText("The min temperature is " + min);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.maxTemp);
                        tv.setText("The max temperature is " + max);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.humidity);
                        tv.setText("The humidity is " + humidity + "%");
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.description);
                        tv.setText(description);
                        tv.setVisibility(View.VISIBLE);

                        ImageView iv = findViewById(R.id.icon);
                        iv.setImageBitmap(image);
                        iv.setVisibility(View.VISIBLE);
                    });


                } catch (IOException | JSONException ioe) {
                    Log.e("Connection error: ", ioe.getMessage());
                }
            } );
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