package algonquin.cst2335.liao0026;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.w( "MainActivity", "In onCreate() - Loading Widgets" );

        Log.w( TAG, "In onCreate() - Loading Widgets");

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String emailAddress = prefs.getString("LoginName", "");

        EditText emailEditText = findViewById(R.id.editTextEmailAddress);
        emailEditText.setText(emailAddress);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(clk -> {
            Intent nextPage = new Intent( MainActivity.this, SecondActivity.class);

            String eml = emailEditText.getText().toString();
            nextPage.putExtra( "EmailAddress", eml);
            SharedPreferences.Editor  editor = prefs.edit();
            editor.putString("LoginName", eml);
            editor.apply();
            startActivity(nextPage);
//            startActivity(call);


        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w( TAG, "The application is now visible on screen");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w( TAG, "The application is now responding to user input");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w( TAG, "The application no longer responds to user input");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w( TAG, "The application is no longer visible.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w( TAG, "Any memory used by the application is freed.");
    }
}