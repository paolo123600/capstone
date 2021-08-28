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
import com.medicall.capstone.Model.SecRC;
import com.medicall.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SecAdapter extends RecyclerView.Adapter<SecAdapter.MyHolder> {

    Context context;
    List<SecRC> secRCList ;
    boolean isChat;

    String friendid;
    String thelastmessage;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String type;


    public SecAdapter(Context context, List<SecRC> secRCList, boolean isChat,String type) {
        this.context = context;
        this.secRCList = secRCList;
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

        SecRC secRC = secRCList.get(position);

        friendid = secRC.getUserId();


        holder.username.setText(secRC.getClinicName());





        if (isChat) {

            LastMessage(secRC.getUserId(), holder.last_msg);

        } else {

            holder.last_msg.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return secRCList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView username, last_msg;
        CircleImageView imageView, image_on, image_off;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username_userfrag);
            last_msg = itemView.findViewById(R.id.lastMessage);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            SecRC secRC = secRCList.get(getAdapterPosition());

            friendid = secRC.getUserId();

            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("friendid", friendid);
            intent.putExtra("name", secRC.getClinicName());
            intent.putExtra("usertype", "Secretary");
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