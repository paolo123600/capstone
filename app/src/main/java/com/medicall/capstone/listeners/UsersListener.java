package com.medicall.capstone.listeners;

import com.medicall.capstone.models.User;

public interface UsersListener {

    Void initiateVideoMeeting(User user);

    Void initiateAudioMeeting(User user);

}
