package com.example.capstone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstone.MessageActivity;
import com.example.capstone.Model.Chats;
import com.example.capstone.Model.DocRC;
import com.example.capstone.Model.PatRC;
import com.example.capstone.Model.Users;
import com.example.capstone.R;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.MyHolder> {

    Context context;
    List<DocRC> docRCList ;
    boolean isChat;

    String friendid;
    String thelastmessage;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    String type;


    public DocAdapter(Context context, List<DocRC> docRcList, boolean isChat,String type) {
        this.context = context;
        this.docRCList = docRcList;
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

        DocRC docRC = docRCList.get(position);

        friendid = docRC.getUserId();


        holder.username.setText("Doc"+". "+docRC.getLastName());
        holder.clinicname.setText(docRC.getClinicName());


        if (isChat) {

            LastMessage(docRC.getUserId(), holder.last_msg);

        } else {

            holder.last_msg.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return docRCList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView username, last_msg, clinicname;


        public MyHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username_userfrag);
            last_msg = itemView.findViewById(R.id.lastMessage);
            clinicname = itemView.findViewById(R.id.tv_clinic);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            DocRC docRC = docRCList.get(getAdapterPosition());

            friendid = docRC.getUserId();

            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("friendid", friendid);
            intent.putExtra("name", "Doc."+" "+docRC.getLastName());
            intent.putExtra("usertype", "Doctors");
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