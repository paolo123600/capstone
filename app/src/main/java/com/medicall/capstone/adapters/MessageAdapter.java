package com.medicall.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicall.capstone.Model.Chats;
import com.medicall.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {


    Context context;
    List<Chats> chatslist;


    public static final int MESSAGE_RIGHT = 0; // FOR ME (
    public static final int MESSAGE_LEFT = 1; // FOR FRIEND



    public MessageAdapter(Context context, List<Chats> chatslist) {
        this.context = context;
        this.chatslist = chatslist;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MESSAGE_RIGHT) {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);


        } else {

            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);

        }



    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Chats chats  = chatslist.get(position);

        holder.messagetext.setText(chats.getMessage());





        if (position == chatslist.size() -1) {

            if (chats.isIsseen()) {

                holder.seen.setText("seen");


            } else {
                holder.seen.setText("Delivered");
            }

        } else {
            holder.seen.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return chatslist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView messagetext, seen;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messagetext = itemView.findViewById(R.id.show_message);

            seen = itemView.findViewById(R.id.text_Seen);
        }
    }


    @Override
    public int getItemViewType(int position) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (chatslist.get(position).getSender().equals(user.getUid())) {


            return MESSAGE_RIGHT;
        } else {

            return MESSAGE_LEFT;


        }
    }
}