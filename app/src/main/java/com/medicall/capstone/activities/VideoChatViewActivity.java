package com.medicall.capstone.activities;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Rational;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.medicall.capstone.GlobalVariables;
import com.medicall.capstone.Login;
import com.medicall.capstone.MessageActivity;
import com.medicall.capstone.NoteActivity;
import com.medicall.capstone.R;
import com.medicall.capstone.doctor.E_Prescription_Template;
import com.medicall.capstone.utilities.Constants;
import com.medicall.capstone.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoChatViewActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private RtcEngine mRtcEngine;
    private boolean mCallEnd;
    private boolean mMuted;
    private  String scheddocu = "";
    private  String name = "";
    private  String friendid = "";
    private FrameLayout mLocalContainer;
    private RelativeLayout mRemoteContainer;
    private VideoCanvas mLocalVideo;
    private VideoCanvas mRemoteVideo;
    private RelativeLayout smalllayout;
    private RelativeLayout chtlayout;

    private boolean chatmode = false;
    private boolean onStopCalled = false;
    private ViewGroup.LayoutParams params2;

    ImageButton docprescription;

    private ImageView mCallBtn;
    private ImageView mMuteBtn;
    private ImageView mSwitchCameraBtn;
    private ImageButton mChatButton;
    private ImageButton mAddNote;
    private PreferenceManager preferenceManager;
    private SurfaceView mLocalView;
    private  String usertype="";
    private SurfaceView mRemoteView;
    FirebaseFirestore db;
   private String Channel1 = "";
private  boolean inpip = false;
   private PictureInPictureParams.Builder pictureInPictureParams;

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        /**
         * Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         *
         * @param channel Channel name.
         * @param uid User ID.
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered.
         */
        @Override
        public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }




        /**
         * Occurs when the first remote video frame is received and decoded.
         * This callback is triggered in either of the following scenarios:
         *
         *     The remote user joins the channel and sends the video stream.
         *     The remote user stops sending the video stream and re-sends it after 15 seconds. Possible reasons include:
         *         The remote user leaves channel.
         *         The remote user drops offline.
         *         The remote user calls the muteLocalVideoStream method.
         *         The remote user calls the disableVideo method.
         *
         * @param uid User ID of the remote user sending the video streams.
         * @param width Width (pixels) of the video stream.
         * @param height Height (pixels) of the video stream.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until this callback is triggered.
         */
        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a
         *     goodbye message. When this message is received, the SDK determines that the
         *     user/host leaves the channel.
         *
         *     Drop offline: When no data packet of the user or host is received for a certain
         *     period of time (20 seconds for the communication profile, and more for the live
         *     broadcast profile), the SDK assumes that the user/host drops offline. A poor
         *     network connection may lead to false detections, so we recommend using the
         *     Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoChatViewActivity.this, "The call has been ended", Toast.LENGTH_LONG).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        updatepat();
                        endCall();
                        RtcEngine.destroy();
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAndRemoveTask();
                    }
                }
            });
        }
    };

    private void onRemoteUserLeft(int uid) {
        if (mRemoteVideo != null && mRemoteVideo.uid == uid) {
            removeFromParent(mRemoteVideo);
            // Destroys remote view
            mRemoteVideo = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(pictureInPictureParams.build());

        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat_view);
         mChatButton=(ImageButton) findViewById(R.id.videochathome_chat);
        mAddNote= (ImageButton) findViewById(R.id.videochathome_addnote);
        preferenceManager = new PreferenceManager(getApplicationContext());
        usertype=preferenceManager.getString(Constants.USERTYPE);
        smalllayout = findViewById(R.id.smallvclayout);
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        chtlayout = findViewById(R.id.chatlayout);
        db= FirebaseFirestore.getInstance();
        docprescription = findViewById(R.id.videochathome_prescription);

        Intent intent1 = getIntent();
        String patid = intent1.getStringExtra("patid");
        String docid = intent1.getStringExtra("docid");
        String clname = intent1.getStringExtra("clname");
        String ddate = intent1.getStringExtra("ddate");

        docprescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(usertype.equals("Patient")){
                        Rational rational = new Rational(2,
                                3);
                        PictureInPictureParams params3 =
                                new PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build();
                        setPictureInPictureParams(params3);
                        enterPictureInPictureMode(params3);
                        Intent intent = new Intent(VideoChatViewActivity.this, E_Prescription_Template.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("patid",patid);
                        intent.putExtra("docid", docid);
                        intent.putExtra("clname", clname);
                        intent.putExtra("ddate", ddate);
                        startActivity(intent);
                    }
                    else{
                        Rational rational = new Rational(2,
                                3);
                        PictureInPictureParams params3 =
                                new PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build();
                        setPictureInPictureParams(params3);
                        enterPictureInPictureMode(params3);

                        Intent intent = new Intent(VideoChatViewActivity.this, E_Prescription_Template.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("patid",patid);
                        intent.putExtra("docid", docid);
                        intent.putExtra("clname", clname);
                        intent.putExtra("ddate", ddate);
                        startActivity(intent);


                    }




                } else {

                }


            }

        });



        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(usertype.equals("Patient")){
                        Rational rational = new Rational(2,  
                                3);
                        PictureInPictureParams params3 =
                                new PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build();
                        setPictureInPictureParams(params3);
                        enterPictureInPictureMode(params3);
                        Intent intent = new Intent(VideoChatViewActivity.this, MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("friendid", friendid);
                        intent.putExtra("name", name);
                        intent.putExtra("usertype", "Doctors");
                        intent.putExtra("type", "Patients");
                        intent.putExtra("FromCall", "true");
                        startActivity(intent);
                    }
                    else{
                        Rational rational = new Rational(2,
                                3);
                        PictureInPictureParams params3 =
                                new PictureInPictureParams.Builder()
                                        .setAspectRatio(rational)
                                        .build();
                        setPictureInPictureParams(params3);
                        enterPictureInPictureMode(params3);

                        Intent intent = new Intent(VideoChatViewActivity.this, MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("friendid", gv.getSDPatUId());
                        intent.putExtra("name", name);
                        intent.putExtra("usertype", "Patients");
                        intent.putExtra("type", "Doctors");
                        intent.putExtra("FromCall", "true");
                        startActivity(intent);


                    }




                } else {

                }


            }
        });

        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoChatViewActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });
        initUI();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pictureInPictureParams = new PictureInPictureParams.Builder();
        }



       Channel1 = gv.getChannel_Name().toString();

        String usertype = preferenceManager.getString(Constants.USERTYPE);
        if(usertype.equals("Patient")){
            mAddNote.setVisibility(View.GONE);
            docprescription.setVisibility(View.GONE);
        }
        Intent intented = getIntent();
        name=intented.getStringExtra("name");
        friendid=intented.getStringExtra("friendid");


        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID)) {
            initEngineAndJoinChannel();


        }
    }

    private void gotoChat() {
        GlobalVariables gv = (GlobalVariables) getApplicationContext();
        if(usertype.equals("Patient")){
            Intent intent = new Intent(VideoChatViewActivity.this, MessageActivity.class);
            intent.putExtra("friendid", friendid);
            intent.putExtra("name", name);
            intent.putExtra("usertype", "Doctors");
            intent.putExtra("type", "Patients");
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(VideoChatViewActivity.this, MessageActivity.class);
            intent.putExtra("friendid", gv.getSDPatUId());
            intent.putExtra("name", name);
            intent.putExtra("usertype", "Patients");
            intent.putExtra("type", "Doctors");
            startActivity(intent);


        }
    }

    private void initUI() {
        mLocalContainer = findViewById(R.id.local_video_view_container);
        mRemoteContainer = findViewById(R.id.remote_video_view_container);

        mCallBtn = findViewById(R.id.btn_call);
        mMuteBtn = findViewById(R.id.btn_mute);
        mSwitchCameraBtn = findViewById(R.id.btn_switch_camera);


    }

    private void showButtons(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        mMuteBtn.setVisibility(visibility);
        mSwitchCameraBtn.setVisibility(visibility);
        mAddNote.setVisibility(visibility);
        mChatButton.setVisibility(visibility);
    }

    public void onCallClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(VideoChatViewActivity.this)
                .setTitle("End the call")
                .setMessage("Are you sure you want to end the call?")

                .setPositiveButton("End", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            updatepat();
                            endCall();
                            RtcEngine.destroy();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finishAndRemoveTask();
                        } else {

                        }


                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();




    }

    private void updatepat() {

        if (usertype.equals("Doctor")){
            GlobalVariables gv = (GlobalVariables) getApplicationContext();
            String patui= gv.getSDPatUId();
            String docui = preferenceManager.getString(Constants.KEY_USER_ID);

            db.collection("Schedule").whereEqualTo("PatientUId",patui).whereEqualTo("TimeStart",gv.getSDtimestart()).whereEqualTo("DoctorUId",docui).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            scheddocu=document.getId();

                            db.collection("Schedule").document(scheddocu).update("Status","Completed").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(VideoChatViewActivity.this, "Errrrrr", Toast.LENGTH_SHORT).show();
                    }
                }
            });




        }
    }

    private ViewGroup removeFromParent(VideoCanvas canvas) {
        if (canvas != null) {
            ViewParent parent = canvas.view.getParent();
            if (parent != null) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(canvas.view);
                return group;
            }
        }
        return null;
    }

    private void endCall() {
        removeFromParent(mLocalVideo);
        mLocalVideo = null;
        removeFromParent(mRemoteVideo);
        mRemoteVideo = null;

    }

    public void onLocalAudioMuteClicked(View view) {
        mMuted = !mMuted;
        // Stops/Resumes sending the local audio stream.
        mRtcEngine.muteLocalAudioStream(mMuted);
        int res = mMuted ? R.drawable.btn_mute : R.drawable.btn_unmute;
        mMuteBtn.setImageResource(res);
    }

    public void onSwitchCameraClicked(View view) {
        // Switches between front and rear cameras.
        mRtcEngine.switchCamera();
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }

        return true;
    }


    public void onUserJoined(final int uid, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("agora", "Remote user joined, uid: " + (uid & 0xFFFFFFFFL));
                setupRemoteVideo(uid);
            }
        });

    }

    private void initEngineAndJoinChannel() {
        initializeEngine();
        setupVideoConfig();
        setupLocalVideo();
        joinChannel();


    
    }

    private void setupVideoConfig() {
        // In simple use cases, we only need to enable video capturing
        // and rendering once at the initialization step.
        // Note: audio recording and playing is enabled by default.
        mRtcEngine.enableVideo();

        // Please go to this page for detailed explanation
        // https://docs.agora.io/en/Video/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_rtc_engine.html#af5f4de754e2c1f493096641c5c5c1d8f
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }


    private void initializeEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupLocalVideo() {

        // Enable the video module.
        mRtcEngine.enableVideo();

        // Create a SurfaceView object.
        SurfaceView view = RtcEngine.CreateRendererView(getBaseContext());
        view.setZOrderMediaOverlay(true);

        mLocalView = RtcEngine.CreateRendererView(getBaseContext());
        mLocalView.setZOrderMediaOverlay(true);
        mLocalContainer.addView(mLocalView);
        // Set the local video view.
        VideoCanvas localVideoCanvas = new VideoCanvas(mLocalView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(localVideoCanvas);
    }


    private void joinChannel() {
        // 1. Users can only see each other after they join the
        // same channel successfully using the same app id.
        // 2. One token is only valid for the channel name that
        // you use to generate this token.
        String token = getString(R.string.agora_access_token);
        if (TextUtils.isEmpty(token) || TextUtils.equals(token, "#YOUR ACCESS TOKEN#")) {
            token = null; // default, no token
        }
        mRtcEngine.joinChannel(token,Channel1, "Extra Optional Data", 0);
    }

    private void startCall() {
        setupLocalVideo();
        joinChannel();
    }


    private void setupRemoteVideo(int uid) {

        // Create a SurfaceView object.


        mRemoteView = RtcEngine.CreateRendererView(getBaseContext());
        mRemoteContainer.addView(mRemoteView);
        // Set the remote video view.
        mRtcEngine.setupRemoteVideo(new VideoCanvas(mRemoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));


    }

    private void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                showLongToast("Need permissions " + Manifest.permission.RECORD_AUDIO +
                        "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE);
                finish();
                return;
            }

            // Here we continue only if all permissions are granted.
            // The permissions can also be granted in the system settings manually.
            initEngineAndJoinChannel();
        }

    }

    @Override
    protected void onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational rational = new Rational(2,
                    3);
            PictureInPictureParams params3 =
                    new PictureInPictureParams.Builder()
                            .setAspectRatio(rational)
                            .build();
            setPictureInPictureParams(params3);
            enterPictureInPictureMode(params3);



        } else {

        }


        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        onStopCalled = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        onStopCalled = false;
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

        if (isInPictureInPictureMode) {
            inpip = true;
            mCallBtn.setVisibility(View.GONE);
            mMuteBtn.setVisibility(View.GONE);
            docprescription.setVisibility(View.GONE);
            mSwitchCameraBtn.setVisibility(View.GONE);
            mLocalContainer.setVisibility(View.GONE);
            mChatButton.setVisibility(View.GONE);
            mAddNote.setVisibility(View.GONE);

      mLocalView.setVisibility(View.GONE);

            params2 = mLocalContainer.getLayoutParams();
            ViewGroup.LayoutParams params = mLocalContainer.getLayoutParams();
            params.height = 100;

            mLocalContainer.setLayoutParams(params);
        } else {
            inpip = false;
            mCallBtn.setVisibility(View.VISIBLE);
            mMuteBtn.setVisibility(View.VISIBLE);
            mSwitchCameraBtn.setVisibility(View.VISIBLE);
            mLocalContainer.setVisibility(View.VISIBLE);
            mChatButton.setVisibility(View.VISIBLE);
            if (!usertype.equals("Patient")){
                mAddNote.setVisibility(View.VISIBLE);
                docprescription.setVisibility(View.VISIBLE);
            }

            mLocalView.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = mLocalContainer.getLayoutParams();
            params.height = (int) getResources().getDimension(R.dimen.local_preview_height);
            mLocalContainer.setLayoutParams(params);

            if (onStopCalled) {


                Intent intent = new Intent(getApplicationContext(), VideoChatViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivityIfNeeded(intent, 0);

                if (onStopCalled){
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoChatViewActivity.this)
                        .setTitle("End the call")
                        .setMessage("Are you sure you want to end the call?")

                        .setPositiveButton("End", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    updatepat();
                                    endCall();
                                    RtcEngine.destroy();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finishAndRemoveTask();
                                } else {

                                }


                            }
                        });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ViewGroup.LayoutParams params2 = mLocalContainer.getLayoutParams();
                        params2.height = (int) getResources().getDimension(R.dimen.local_preview_height);
                        mLocalContainer.setLayoutParams(params2);
                        dialogInterface.dismiss();
                    }
                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }}




        }
    }
}

