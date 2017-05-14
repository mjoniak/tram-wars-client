package com.github.mjoniak.tramwarsclient.map;

interface MapContract {
    interface View {
        void showProfileForm();
    }

    interface Presenter {
        void editProfile();
    }
}
