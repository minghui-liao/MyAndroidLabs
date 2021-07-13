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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageListFragment extends Fragment {
    ArrayList<MessageListFragment.ChatMessage> messages = new ArrayList<>();;
    MessageListFragment.MyChatAdapter adt;
    SQLiteDatabase db;
    Button send;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View chatLayout = inflater.inflate(R.layout.chatlayout, container, false);
        send = chatLayout.findViewById(R.id.sendbtn);

        RecyclerView chatlist = chatLayout.findViewById(R.id.myrecycler);
        Button send = chatLayout.findViewById(R.id.sendbtn);
        Button receive = chatLayout.findViewById(R.id.receivebtn);
        EditText messageText = chatLayout.findViewById(R.id.editText);

        //adapter
        adt = new MyChatAdapter();
        chatlist.setAdapter(adt);
        chatlist.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        MyOpenHelper opener = new MyOpenHelper(getContext());
        db = opener.getWritableDatabase();

        Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);

        int _idCol = results.getColumnIndex("_id");
        int messageCol = results.getColumnIndex( MyOpenHelper.col_message);
        int sendCol = results.getColumnIndex( MyOpenHelper.col_send_receive);
        int timeCol = results.getColumnIndex( MyOpenHelper.col_time_sent);



        SimpleDateFormat sdf = new SimpleDateFormat("EE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
        String timeSent = sdf.format(new Date());

        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String message = results.getString( messageCol);
            String time = results.getString( timeCol);
            int sendOrReceive = results.getInt( sendCol);
            try {
                messages.add( new ChatMessage(message, sendOrReceive, sdf.parse(time), id) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //send click listener to add message
        send.setOnClickListener( clk -> {
            ChatMessage thisMessage = new ChatMessage(messageText.getText().toString(), 1, new Date());

            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
            newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, sdf.format(thisMessage.getTimeSent()));
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            thisMessage.setId(newId);

            messages.add(thisMessage);
            adt.notifyItemInserted(messages.size() - 1);
            messageText.setText("");
        });

        //send click listener to add message
        receive.setOnClickListener( clk -> {
            ChatMessage thisMessage = new ChatMessage(messageText.getText().toString(), 2, new Date());

            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, thisMessage.getMessage());
            newRow.put(MyOpenHelper.col_send_receive, thisMessage.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, sdf.format(thisMessage.getTimeSent()));
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            thisMessage.setId(newId);

            messages.add(thisMessage);
            adt.notifyItemInserted(messages.size() - 1);
            messageText.setText("");
        });

        return chatLayout;
    }

    public void notifyMessageDeleted(ChatMessage chosenMessage, int chosenPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setMessage("are you sure you want to delete the message: " + chosenMessage.getMessage())
                .setTitle("Danger!")
                .setNegativeButton("Cancel", (dialog, cl) -> { })
                .setPositiveButton("Delete", (dialog, cl) -> {

                    //position = getAbsoluteAdapterPosition();

                    ChatMessage removedMessage = messages.get(chosenPosition);
                    messages.remove(chosenPosition);
                    adt.notifyItemRemoved(chosenPosition);

                    db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] { Long.toString(removedMessage.getId()) });


                    Snackbar.make(send, "You deleted message #" + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("Undo", clk -> {

                                messages.add(chosenPosition,  removedMessage);
                                adt.notifyItemInserted(chosenPosition);
                                db.execSQL("Insert into " + MyOpenHelper.TABLE_NAME + " values('" + removedMessage.getId() +
                                        "','" + removedMessage.getMessage() +
                                        "','" + removedMessage.getSendOrReceive() +
                                        "','" + removedMessage.getTimeSent() + "');");
                            })
                            .show();
                })
                .create().show();
    }


    private class MyRowViews extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {

                MyRowViews newRow = adt.onCreateViewHolder(null, adt.getItemViewType(position));

                ChatRoom parentActivity = (ChatRoom)getContext();
                int position = getAbsoluteAdapterPosition();
                parentActivity.userClickedMessage( messages.get(position), position );

                /*
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
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
                        .create().show();*/
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
            holder.timeText.setText(messages.get(position).getTimeSent().toString());
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

    class ChatMessage
    {
        String message;
        int sendOrReceive;
        Date timeSent;
        long id;

        public ChatMessage(String message, int sendOrReceive, Date timeSent, long id) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
            setId(id);
        }

        public ChatMessage(String message, int sendOrReceive, Date timeSent) {
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

        public Date getTimeSent() {
            return timeSent;
        }
    }
}
