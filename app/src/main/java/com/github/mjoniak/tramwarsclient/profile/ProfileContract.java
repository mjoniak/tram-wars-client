package com.github.mjoniak.tramwarsclient.profile;

import com.github.mjoniak.tramwarsclient.domain.UserProfile;

interface ProfileContract {
    interface View {

        void displayProfile(UserProfile profile);

        void displayProfileUpdatedMessage();
    }

    interface Presenter {
        void start();

        void saveProfile(UserProfile userProfile, String currentPassword);
    }
}
