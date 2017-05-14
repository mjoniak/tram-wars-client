package com.github.mjoniak.tramwarsclient.map;

class MapPresenter implements MapContract.Presenter {
    private MapContract.View view;

    MapPresenter(MapContract.View view) {
        this.view = view;
    }

    @Override
    public void editProfile() {
        view.showProfileForm();
    }
}
