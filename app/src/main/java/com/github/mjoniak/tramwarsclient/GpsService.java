//package com.github.mjoniak.tramwarsclient;
//
//import android.app.job.JobParameters;
//import android.app.job.JobService;
//
//import com.google.android.gms.maps.model.LatLng;
//
//
//public class GpsService extends JobService {
//
//    private Thread thread;
//
//    @Override
//    public boolean onStartJob(final JobParameters params) {
//        final GpsLocator locator = new GpsLocator(GpsService.this, new PermissionManager());
//        final ApiClient client = new ApiClient(getApplicationContext(), new IErrorHandler() {
//            @Override
//            public void handle(String message) {
//                System.err.println(message);
//            }
//        });
//        thread = new Thread() {
//            @Override
//            public void run() {
//                client.authorise("abc", "Test@123", new IContinuation<AuthorisationTokenDTO>() {
//                    @Override
//                    public void continueWith(AuthorisationTokenDTO response) {
//                        updatePosition(locator, client, response);
//                    }
//                });
//
//                jobFinished(params, false);
//            }
//        };
//        thread.start();
//
//        return true;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        if (thread != null && thread.isAlive()) {
//            thread.interrupt();
//        }
//
//        return true;
//    }
//
//    private static void updatePosition(GpsLocator locator, ApiClient client, AuthorisationTokenDTO response) {
//        try {
//            LatLng pos = locator.getCurrentPosition();
//            client.postPosition(pos, response.getAccessToken());
//        } catch (CantGetLocationException e) {
//            e.printStackTrace();
//        }
//    }
//}
