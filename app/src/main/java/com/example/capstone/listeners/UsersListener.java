package com.example.capstone.listeners;

import com.example.capstone.models.User;

public interface UsersListener {

    Void initiateVideoMeeting(User user);

    Void initiateAudioMeeting(User user);

}
