package algonquin.cst2335.liao0026;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoom extends AppCompatActivity {

    Button sendbtn;
    Button receivebtn;
    EditText messageTyped;
    RecyclerView chatList;
    ArrayList<ChatMessage> messages = new ArrayList<>();;
    MyChatAdapter adt;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.chatlayout );

        chatList = findViewById(R.id.myrecycler);
        chatList.setAdapter(new MyChatAdapter());

        messageTyped = findViewById(R.id.editText);

        sendbtn = findViewById(R.id.sendbtn);
        receivebtn = findViewById(R.id.receivebtn);
        adt = new MyChatAdapter();
        chatList.setAdapter(adt);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatList.setLayoutManager(layoutManager);

        MyOpenHelper opener = new MyOpenHelper(this);
        db = opener.getWritableDatabase();

        Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);
        
        int _idCol = results.getColumnIndex("_id");
        int messageCol = results.getColumnIndex( MyOpenHelper.col_message);
        int sendCol = results.getColumnIndex( MyOpenHelper.col_send_receive);
        int timeCol = results.getColumnIndex( MyOpenHelper.col_time_sent);

        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String message = results.getString( messageCol);
            String time = results.getString( timeCol);
            int sendOrReceive = results.getInt( sendCol);
            messages.add( new ChatMessage(message, sendOrReceive, time, id) );
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
        String timeSent = sdf.format(new Date());
        sendbtn.setOnClickListener( vw -> {
            ChatMessage thisMessage = new ChatMessage(messageTyped.getText().toString(),1, timeSent);
            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
            newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, thisMessage.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            thisMessage.setId(newId);

            messageTyped.setText("");
            messages.add( thisMessage );
            adt.notifyItemInserted( messages.size() - 1 );
        } );

        receivebtn.setOnClickListener( vw -> {
            ChatMessage thisMessage = new ChatMessage(messageTyped.getText().toString(),2, timeSent);
            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
            newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, thisMessage.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            thisMessage.setId(newId);

            messageTyped.setText("");
            messages.add( thisMessage );
            adt.notifyItemInserted( messages.size() - 1 );
        } );

    }

    private class MyRowViews extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {

                MyRowViews newRow = adt.onCreateViewHolder(null, adt.getItemViewType(position));

                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message: " + messageText.getText())
                .setTitle("Question:")
                .setNegativeButton("No", (dialog, cl) -> { })
                .setPositiveButton("Yes", (dialog, cl) -> {

                    position = getAbsoluteAdapterPosition();
                    
                    ChatMessage removedMessage = messages.get(position);
                    messages.remove(position);
                    adt.notifyItemRemoved(position);

                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString(removedMessage.getId()) });


                    Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", clk -> {

                                messages.add(position,  removedMessage);
                                adt.notifyItemInserted(position);
                                db.execSQL("Insert into " + MyOpenHelper.TABLE_NAME + " values('" + removedMessage.getId() +
                                        "','" + removedMessage.getMessage() +
                                        "','" + removedMessage.getSendOrReceive() +
                                        "','" + removedMessage.getTimeSent() + "');");
                            })
                            .show();
                })
                .create().show();
            });

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }

        public void setPosition(int p) { position = p; }
    }

    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews>{
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID;
            if(viewType == 1) layoutID = R.layout.send_message;
            else layoutID = R.layout.receive_message;
            View loadedRow = inflater.inflate(layoutID, parent, false);
            return new MyRowViews(loadedRow);
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.messageText.setText(messages.get(position).getMessage() );
            //Date date = messages.get(position).getTimeSent();
//            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
//            String currentDateandTime = sdf.format(date);
            holder.timeText.setText(messages.get(position).getTimeSent());
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).sendOrReceive;
        }
    }

    private class ChatMessage
    {
        String message;
        int sendOrReceive;
        String timeSent;
        long id;

        public ChatMessage(String message, int sendOrReceive, String timeSent, long id) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
            setId(id);
        }

        public ChatMessage(String message, int sendOrReceive, String timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
        }

        public void setId( long l ) { id = l; }

        public long getId() { return id;}

        public ChatMessage(String s) {
            message = s;
        }

        public String getMessage() {
            return message;
        }

        public int getSendOrReceive() {
            return sendOrReceive;
        }

        public String getTimeSent() {
            return timeSent;
        }
    }
}
