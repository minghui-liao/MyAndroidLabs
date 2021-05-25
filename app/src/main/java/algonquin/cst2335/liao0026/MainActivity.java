package algonquin.cst2335.liao0026;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mytext = findViewById(R.id.textview);

        Button btn = findViewById(R.id.mybutton);

        CheckBox mycheck = findViewById(R.id.mycb);
        mycheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Context context = getApplicationContext();
            CharSequence text = "You clicked on the checkbox and it is now:" + isChecked;
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });


        Switch myswitch = findViewById(R.id.myswitch);
        myswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Context context = getApplicationContext();
            CharSequence text = "You clicked on the switch and it is now:" + isChecked;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });


        RadioButton myradio = findViewById(R.id.myradio);
        myradio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Context context = getApplicationContext();
            CharSequence text = "You clicked on the radio button and it is now:" + isChecked;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });

        ImageButton imgbtn = findViewById(R.id.myimagebutton);
        imgbtn.setOnClickListener(vw -> {
            Context context = getApplicationContext();
            CharSequence text = "The width = " + imgbtn.getWidth() + " and height = " + imgbtn.getHeight();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        });

        ImageView myimage = findViewById(R.id.logo_algonquin);

        EditText myedit = findViewById(R.id.myedittext);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String editString = myedit.getText().toString();
                mytext.setText( "Your edit text has: " + editString);
            }
        });
    }
}