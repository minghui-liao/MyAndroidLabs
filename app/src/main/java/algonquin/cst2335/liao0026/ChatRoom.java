package algonquin.cst2335.liao0026;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        chatList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        MyOpenHelper opener = new MyOpenHelper();

        sendbtn.setOnClickListener( vw -> {
            ChatMessage thisMessage = new ChatMessage(messageTyped.getText().toString(),1, new Date());
            messageTyped.setText("");
            messages.add( thisMessage );
            adt.notifyItemInserted( messages.size() - 1 );
        } );

        receivebtn.setOnClickListener( vw -> {
            ChatMessage thisMessage = new ChatMessage(messageTyped.getText().toString(),2, new Date());
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
                    Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                            .setAction("Undo", clk -> {

                                messages.add(position,  removedMessage);
                                adt.notifyItemInserted(position);
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
            Date date = messages.get(position).getTimeSent();
            SimpleDateFormat sdf = new SimpleDateFormat("EE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());
            String currentDateandTime = sdf.format(date);
            holder.timeText.setText(currentDateandTime);
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
        Date timeSent;

        public ChatMessage(String message, int sendOrReceive, Date timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
        }

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
