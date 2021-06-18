package algonquin.cst2335.liao0026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        et = findViewById(R.id.editText);
        btn = findViewById(R.id.button);

        btn.setOnClickListener( clk -> {
            String password = et.getText().toString();
            if (checkPasswordComplexity( password )) {
                tv.setText("Your password meets the requirements");
            } else {
                tv.setText("You shall not pass!");
            }

        });
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