package com.medicall.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medicall.capstone.MessageActivity;
import com.medicall.capstone.Model.Chats;
import com.medicall.capstone.Model.PatRC;
import com.medicall.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

    Context context;
    List<PatRC>  patRCList;
    boolean isChat;

    String friendid;
    String thelastmessage;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String type;


    public UserAdapter(Context context, List<PatRC> patRCList, boolean isChat,String type) {
        this.context = context;
        this.patRCList = patRCList;
        this.isChat = isChat;
        this.type=type;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.layoutofusers, parent, false);
        return new MyHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        PatRC patRC = patRCList.get(position);

        friendid = patRC.getUserId();


        holder.username.setText(patRC.getLastName()+", "+patRC.getFirstName());





        if (isChat) {

            LastMessage(patRC.getUserId(), holder.last_msg);

        } else {

            holder.last_msg.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return patRCList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView username, last_msg;


        public MyHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username_userfrag);
            last_msg = itemView.findViewById(R.id.lastMessage);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            PatRC patRC = patRCList.get(getAdapterPosition());

            friendid = patRC.getUserId();

            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("friendid", friendid);
            intent.putExtra("name", patRC.getLastName()+", "+patRC.getFirstName());
            intent.putExtra("usertype", "Patients");
            intent.putExtra("type", type);
            context.startActivity(intent);



        }
    }

    private void LastMessage(final String friendid, final TextView last_msg) {

        thelastmessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chats chats = ds.getValue(Chats.class);

                    if (firebaseUser!=null &&  chats!=null) {


                        if (chats.getSender().equals(friendid) && chats.getReciever().equals(firebaseUser.getUid()) ||
                                chats.getSender().equals(firebaseUser.getUid()) && chats.getReciever().equals(friendid)) {


                            thelastmessage = chats.getMessage();
                        }




                    }

                }


                switch (thelastmessage) {

                    case "default":
                        last_msg.setText("No message");
                        break;

                    default:
                        last_msg.setText(thelastmessage);

                }


                thelastmessage = "default";


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










    }
}