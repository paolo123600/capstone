package com.example.capstone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.GlobalVariables;
import com.example.capstone.R;
import com.example.capstone.adapters.UsersAdapter;
import com.example.capstone.listeners.UsersListener;
import com.example.capstone.models.User;
import com.example.capstone.utilities.Constants;
import com.example.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class VideoCall_Main extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private List<User> users;
    private UsersAdapter usersAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call__main);
        GlobalVariables gv = (GlobalVariables) getApplicationContext();

        db = FirebaseFirestore.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());


        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(String.format(
                "%S %S",
                preferenceManager.getString(Constants.KEY_FIRST_NAME),
                preferenceManager.getString(Constants.KEY_LAST_NAME)
        ));


        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users, this);
        userRecyclerView.setAdapter(usersAdapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers);

        getUsers();

    }


    private void getUsers() {
        swipeRefreshLayout.setRefreshing(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        swipeRefreshLayout.setRefreshing(false);
                        String myUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            users.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                if (myUserId.equals(documentSnapshot.getId())){
                                    continue;
                                }
                                User user = new User();
                                user.firstName = documentSnapshot.getString(Constants.KEY_FIRST_NAME);
                                user.lastName = documentSnapshot.getString(Constants.KEY_LAST_NAME);
                                user.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                                user.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                                users.add(user);
                            }
                            if (users.size() > 0) {
                                usersAdapter.notifyDataSetChanged();
                            }else {
                                textErrorMessage.setText(String.format("%s", "No users available"));
                                textErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }else {
                            textErrorMessage.setText(String.format("%s", "No users available"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }

                    }

                });
    }

    @Override
    public Void initiateVideoMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + "is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        }else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
        return null;
    }

    @Override
    public Void initiateAudioMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    user.firstName + " " + user.lastName + "is not available for meeting",
                    Toast.LENGTH_SHORT
            ).show();
        }else {
            Toast.makeText(
                    this,
                    "Audio meeting with " + user.firstName + " " + user.lastName,
                    Toast.LENGTH_SHORT
            ).show();
        }
        return null;
    }
}
