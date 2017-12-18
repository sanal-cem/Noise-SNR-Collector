package com.snrc.cell;

/**
 * Created by Cem on 1.11.2017.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.snrc.lib.GPSTracker;
import com.snrc.lib.Global;
import com.snrc.lib.PermissionUtils;
import com.snrc.R;
import com.snrc.lib.SNRDataPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * This demo shows how GMS Location can be used to check for changes to the users location.  The
 * "My Location" button uses GMS Location to set the blue dot representing the users location.
 * Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run
 * time. If the permission has not been granted, the Activity is finished with an error message.
 */
public class cMapsActivity extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private SNRDataPoint cSnrData;
    double lat;
    double lon;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = findViewById(R.id.backButMap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNaviActivity();
            }
        });

        // BANNER ad
        loadAdView(R.id.adView_maps);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        if(Global.jsonText != null)
            addMarkers();
        addRoute();
    }
    // listens users taps and adds route accordingly.
    private void addRoute() {
        GPSTracker gpsTObj = new GPSTracker(this);
        lat = gpsTObj.getLatitude();
        lon = gpsTObj.getLongitude();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            // listens map clicks
            @Override
            public void onMapClick(LatLng arg0) {

                // get location of SNR data.
                LatLng here = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(here).title("Here"));

                LatLng dest = new LatLng(arg0.latitude, arg0.longitude);
                mMap.addMarker(new MarkerOptions().position(dest).title("Destination"));

                //Define list to get all latlng for the route
                List<LatLng> path = new ArrayList<>();

                //Execute Directions API request
                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey("seperatedgoogleMapsAPIKey")
                        .build();
                DirectionsApiRequest req = DirectionsApi.getDirections(context, "lat, lon", "arg0.latitude, arg0.longitude");
                try {
                    DirectionsResult res = req.await();

                    //Loop through legs and steps to get encoded polylines of each step
                    if (res.routes != null && res.routes.length > 0) {
                        DirectionsRoute route = res.routes[0];

                        if (route.legs !=null) {
                            for(int i=0; i<route.legs.length; i++) {
                                DirectionsLeg leg = route.legs[i];
                                if (leg.steps != null) {
                                    for (int j=0; j<leg.steps.length;j++){
                                        DirectionsStep step = leg.steps[j];
                                        if (step.steps != null && step.steps.length >0) {
                                            for (int k=0; k<step.steps.length;k++){
                                                DirectionsStep step1 = step.steps[k];
                                                EncodedPolyline points1 = step1.polyline;
                                                if (points1 != null) {
                                                    //Decode polyline and add points to list of route coordinates
                                                    List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                    for (com.google.maps.model.LatLng coord1 : coords1) {
                                                        path.add(new LatLng(coord1.lat, coord1.lng));
                                                    }
                                                }
                                            }
                                        } else {
                                            EncodedPolyline points = step.polyline;
                                            if (points != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords = points.decodePath();
                                                for (com.google.maps.model.LatLng coord : coords) {
                                                    path.add(new LatLng(coord.lat, coord.lng));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch(Exception ex) {
                    ex.getLocalizedMessage();
                }

                //Draw the polyline
                if (path.size() > 0) {
                    PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
                    mMap.addPolyline(opts);
                }

                mMap.getUiSettings().setZoomControlsEnabled(true);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 6));
            }
        });
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }
    private void addMarkers() {
        cSnrData = new SNRDataPoint();
        cSnrData.fetchSNRDataDb();

        int rNum = cSnrData.routeNum[0];
        for (int i = 0; i < cSnrData.jsonArrLength; i++) {

            if(cSnrData.routeNum[i] != rNum)
                rNum = cSnrData.routeNum[i];

            LatLng point = new LatLng(cSnrData.loc_x[i], cSnrData.loc_y[i]);
            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(String.valueOf(cSnrData.SNR_all[i])));
        }
    }
    private void gotoNaviActivity() {
        Intent i = new Intent(this, cNaviActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }
    private void loadAdView(int adInt) {
        AdView adView = this.findViewById(adInt);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}