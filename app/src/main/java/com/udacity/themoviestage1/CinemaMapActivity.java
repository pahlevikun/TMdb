package com.udacity.themoviestage1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.udacity.themoviestage1.config.GPSTracker;
import com.udacity.themoviestage1.pojo.Markers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CinemaMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mGoogleMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private Location mLastLocation, location;
    private Marker marker, mCurrLocationMarker;
    private ProgressDialog loading;
    private Double dLat, dLng;
    private boolean load = false;
    private boolean hide = true;
    private Button btDirect, btWeb;
    private TextView textNama;
    private LinearLayout linearLayout;

    private GPSTracker gps;

    private List<Markers> markerList = new ArrayList<Markers>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Cinema Map");

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btDirect = (Button) findViewById(R.id.buttonArahkan);
        btWeb = (Button) findViewById(R.id.buttonWeb);
        textNama = (TextView) findViewById(R.id.namaBioskop);
        linearLayout.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(CinemaMapActivity.this);

        gps = new GPSTracker(CinemaMapActivity.this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(CinemaMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CinemaMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        getMarker();

        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        dLat = gps.getLatitude();
        dLng = gps.getLongitude();

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(CinemaMapActivity.this, NavigasiActivity.class);
                intent.putExtra("markerNama", markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getNama());
                intent.putExtra("markerLat", markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getLat());
                intent.putExtra("markerLng", markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getLng());
                intent.putExtra("locLat", dLat);
                intent.putExtra("locLng", dLng);
                startActivity(intent);
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (hide){
                    linearLayout.setVisibility(View.VISIBLE);
                    hide = false;
                }
                textNama.setText(markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getNama());
                btDirect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+dLat+","+dLng+"&daddr="+markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getLat()+","+markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getLng();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                    }
                });

                btWeb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(markerList.get(Integer.parseInt(marker.getId().replace("m", ""))).getUrl()));
                        startActivity(i);
                    }
                });
                return false;
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(CinemaMapActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(CinemaMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        dLat = location.getLatitude();
        dLng = location.getLongitude();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }


    public void getMarker() {

        markerList.add(new Markers(1, "Ciputra Cibubur XXI", "-6.383407", "106.925625","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSCICI&find_by=2"));
        markerList.add(new Markers(2, "Grand Mall Bekasi XXI", "-6.227866", "106.983707","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSGRMA&find_by=2"));
        markerList.add(new Markers(3, "Metropolitan XXI", "-6.248572", "106.990634","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSMETL&find_by=2"));
        markerList.add(new Markers(4, "Mega Bekasi XXI", "-6.250078", "106.993535","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSMEBE&find_by=2"));
        markerList.add(new Markers(5, "Plasa Cibubur XXI", "-6.376393", "106.915048","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSPLCI&find_by=2"));

        for (int i = 0; i < markerList.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(markerList.get(i).getLat()), Double.parseDouble(markerList.get(i).getLng()));
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(markerList.get(i).getNama())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dest)))
                    .hideInfoWindow();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}