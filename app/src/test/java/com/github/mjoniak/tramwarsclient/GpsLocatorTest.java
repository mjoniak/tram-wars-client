package com.github.mjoniak.tramwarsclient;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class GpsLocatorTest {
    @SuppressWarnings("MissingPermission")
    @Test
    public void whenPermissionOkThenReturnsLocation() throws Exception {
        Context context = mock(Context.class);
        LocationManager locationManager = mock(LocationManager.class);
        PermissionManager permissionManager = mock(PermissionManager.class);
        Location location = mock(Location.class);

        when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager);
        when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(location);
        when(permissionManager.checkGpsPermissions(refEq(context))).thenReturn(true);
        when(location.getLatitude()).thenReturn(50.0);
        when(location.getLongitude()).thenReturn(20.0);

        GpsLocator locator = new GpsLocator(context, permissionManager);
        LatLng pos = locator.getCurrentPosition();
        assertEquals(50.0, pos.latitude, 0.1);
        assertEquals(20.0, pos.longitude, 0.1);
    }

    @SuppressWarnings("MissingPermission")
    @Test
    public void whenNoLocationThenThrowException() {
        Context context = mock(Context.class);
        LocationManager locationManager = mock(LocationManager.class);
        PermissionManager permissionManager = mock(PermissionManager.class);

        when(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager);
        when(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)).thenReturn(null);
        when(permissionManager.checkGpsPermissions(refEq(context))).thenReturn(true);

        GpsLocator locator = new GpsLocator(context, permissionManager);
        try {
            locator.getCurrentPosition();
            fail();
        } catch (CantGetLocationException ignored) {}
    }
}
