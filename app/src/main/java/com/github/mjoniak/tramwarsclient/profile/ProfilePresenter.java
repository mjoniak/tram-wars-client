package com.github.mjoniak.tramwarsclient.profile;

import com.github.mjoniak.tramwarsclient.ApplicationState;
import com.github.mjoniak.tramwarsclient.datasource.ApiClient;
import com.github.mjoniak.tramwarsclient.datasource.IContinuation;
import com.github.mjoniak.tramwarsclient.datasource.dto.AuthorisationTokenDTO;
import com.github.mjoniak.tramwarsclient.domain.UserProfile;

class ProfilePresenter implements ProfileContract.Presenter {
    private final ProfileContract.View view;
    private final ApiClient apiClient;

    ProfilePresenter(ProfileContract.View view, ApiClient apiClient) {

        this.view = view;
        this.apiClient = apiClient;
    }

    @Override
    public void start() {
        ApplicationState state = ApplicationState.getInstance();
        apiClient.getProfile(
                state.getAccessToken(),
                state.getUserName(),
                new IContinuation<UserProfile>() {
                    @Override
                    public void continueWith(UserProfile profile) {
                        view.displayProfile(profile);
                    }
                });
    }

    @Override
    public void saveProfile(final UserProfile userProfile, final String currentPassword) {
        ApplicationState state = ApplicationState.getInstance();
        apiClient.putProfile(
                state.getAccessToken(),
                state.getUserName(),
                currentPassword,
                userProfile,
                new IContinuation<Object>() {
                    @Override
                    public void continueWith(Object o) {
                        view.displayProfileUpdatedMessage();
                        ApplicationState state = ApplicationState.getInstance();
                        state.setUserName(userProfile.getName());

                        apiClient.authorise(userProfile.getName(), currentPassword, new IContinuation<AuthorisationTokenDTO>() {
                            @Override
                            public void continueWith(AuthorisationTokenDTO response) {
                                ApplicationState state = ApplicationState.getInstance();
                                state.setAccessToken(response.getAccessToken());
                            }
                        });
                    }
                });
    }
}
