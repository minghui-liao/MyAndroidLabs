package algonquin.cst2335.liao0026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.BreakIterator;

public class SecondActivity extends AppCompatActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String phoneNumber = prefs.getString("Phone Number", "");

        Intent fromPrevious = getIntent();
        String emailAddress = fromPrevious.getStringExtra("EmailAddress");
        TextView textView = findViewById(R.id.textView);
        textView.setText("Welcome back " + emailAddress);

        EditText editTextPhone = findViewById(R.id.editTextPhone);
        editTextPhone.setText(phoneNumber);

        Button callBtn = findViewById(R.id.phoneNumberbutton);
        callBtn.setBackgroundColor(getResources().getColor(R.color.my_button_blue));

        callBtn.setOnClickListener( clk -> {
//            Intent callIntent = new Intent();
//            callIntent.putExtra( "Phone Number", editTextPhone.getText().toString() );
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + editTextPhone.getText().toString()));
            //setResult(345, callIntent);
            startActivityForResult( callIntent, 5432);

            //finish();
        });

        Button changePictureButton = findViewById(R.id.changePictureButton);
        changePictureButton.setBackgroundColor(getResources().getColor(R.color.my_button_blue));
        changePictureButton.setOnClickListener(clk -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult( cameraIntent, 3456);
        });

        File file = new File("/data/user/0/algonquin.cst2335.liao0026/files");
        if(file.exists())
        {
            Bitmap theImage = BitmapFactory.decodeFile("/data/user/0/algonquin.cst2335.liao0026/files");
            ImageView profileImage = findViewById(R.id.profileImage);
            profileImage.setImageBitmap( theImage );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = getFilesDir().getPath();
        if(requestCode == 3456) 
        {
            if(resultCode == RESULT_OK) {
                Bitmap thumbnail = data.getParcelableExtra("data");
                FileOutputStream fOut = null;
                try {
                    fOut = openFileOutput( "Picture.png", Context.MODE_PRIVATE);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                ImageView profileImage = findViewById(R.id.profileImage);
                profileImage.setImageBitmap(thumbnail);
            }
        }
//        if (requestCode == 5432) {
//            Log.w( "Second Activity", String.format("Call 5432 resultCode %d\n", resultCode));
//            if (resultCode == RESULT_OK) {
//                Log.w( "Second Activity", "Call 5432 success\n");
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor  editor = prefs.edit();
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        String phoneNumber = editTextPhone.getText().toString();
        editor.putString("Phone Number", phoneNumber);
        editor.apply();
    }
}