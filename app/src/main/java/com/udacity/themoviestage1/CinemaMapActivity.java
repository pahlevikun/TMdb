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
        markerList.add(new Markers(6, "Pondok Gede XXI", "-6.284277", "106.911660","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSPOGD&find_by=2"));
        markerList.add(new Markers(7, "Revo Town XXI", "-6.254522", "106.989705","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSBESQ&find_by=2"));
        markerList.add(new Markers(8, "Summarecon Mall Bekasi XXI", "-6.226174", "107.001008","https://m.21cineplex.com/gui.schedule.php?cinema_id=BKSSUBE&find_by=2"));
        markerList.add(new Markers(9, "Bellanova XXI", "-6.566350", "106.844423","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRBELL&find_by=2"));
        markerList.add(new Markers(10, "Botani XXI", "-6.602416", "106.806795","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRBOTA&find_by=2"));
        markerList.add(new Markers(11, "Bogor Trade Mall XXI", "-6.605047", "106.795818","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRBTM&find_by=2"));
        markerList.add(new Markers(12, "Cibinong City XXI", "-6.484337", "106.842334","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRCICI&find_by=2"));
        markerList.add(new Markers(13, "Cinere XXI", "-6.323615", "106.783947","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRCINE&find_by=2"));
        markerList.add(new Markers(14, "Depok XXI", "-6.392399", "106.825963","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRDEPK&find_by=2"));
        markerList.add(new Markers(15, "Ekalokasari XXI", "-6.621726", "106.817273","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGREKAL&find_by=2"));
        markerList.add(new Markers(16, "Margo XXI", "-6.373389", "106.834548","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRMAPL&find_by=2"));
        markerList.add(new Markers(17, "Metmall Cileungsi XXI", "-6.398475", "106.975307","https://m.21cineplex.com/gui.schedule.php?cinema_id=BGRMECI&find_by=2"));
        markerList.add(new Markers(18, "AEON Mall BSD City XXI", "-6.303792", "106.644362","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRAEBS&find_by=2"));
        markerList.add(new Markers(19, "Alam Sutera XXI", "-6.222351", "106.653451","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRALSU&find_by=2"));
        markerList.add(new Markers(20, "Bale Kota XXI", "-6.179036", "106.643002","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRBAKO&find_by=2"));
        markerList.add(new Markers(21, "Bintaro Xchange XXI", "-6.284835", "106.728112","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRBIXC&find_by=2"));
        markerList.add(new Markers(22, "Bintaro XXI", "-6.272100", "106.741267","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRBINT&find_by=2"));
        markerList.add(new Markers(23, "BSD XXI", "-6.279341", "106.662935","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRSERP&find_by=2"));
        markerList.add(new Markers(24, "CBD Ciledug XXI", "-6.223627", "106.708639","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRCBCI&find_by=2"));
        markerList.add(new Markers(25, "Living World XXI", "-6.244481", "106.653529","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRLIWO&find_by=2"));
        markerList.add(new Markers(26, "Lotte Bintaro XXI", "-6.274408", "106.723833","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRLOBI&find_by=2"));
        markerList.add(new Markers(27, "Summarecon Mall Serpong XXI", "-6.239387", "106.628132","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRSERO&find_by=2"));
        markerList.add(new Markers(28, "Supermal Karawaci XXI", "-6.226550", "106.606555","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRKARA&find_by=2"));
        markerList.add(new Markers(29, "Tangcity XXI", "-6.193996", "106.633957","https://m.21cineplex.com/gui.schedule.php?cinema_id=TGRTACI&find_by=2"));
        markerList.add(new Markers(30, "Anggrek XXI", "-6.178258", "106.791978","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTANGG&find_by=2"));
        markerList.add(new Markers(31, "Arion XXI", "-6.194176", "106.890828","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTARIO&find_by=2"));
        markerList.add(new Markers(32, "Artha Gading XXI", "-6.146793", "106.892222","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTARGA&find_by=2"));
        markerList.add(new Markers(33, "Atrium XXI", "-6.177370", "106.841381","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTATRI&find_by=2"));
        markerList.add(new Markers(34, "Bassura XXI", "-6.224459", "106.877934","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTBASS&find_by=2"));
        markerList.add(new Markers(35, "Baywalk Pluit XXI", "-6.107966", "106.779059","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTBAPL&find_by=2"));
        markerList.add(new Markers(36, "Blok M Plaza XXI", "-6.244191", "106.797532","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTBLOM&find_by=2"));
        markerList.add(new Markers(37, "Blok M Square XXI", "-6.244260", "106.800938","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTBMSQ&find_by=2"));
        markerList.add(new Markers(38, "Cijantung XXI", "-6.312778", "106.861929","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTCIJA&find_by=2"));
        markerList.add(new Markers(39, "Cipinang XXI", "-6.238687", "106.894086","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTCIPI&find_by=2"));
        markerList.add(new Markers(40, "Citra XXI", "-6.168473", "106.786862","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTCITR&find_by=2"));
        markerList.add(new Markers(41, "Daan Mogot XXI", "-6.150874", "106.714405","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTDAMG&find_by=2"));
        markerList.add(new Markers(42, "Djakarta XXI", "-6.186760", "106.824014","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTDJAR&find_by=2"));
        markerList.add(new Markers(43, "Emporium Pluit XXI", "-6.127525", "106.790962","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTEMPL&find_by=2"));
        markerList.add(new Markers(44, "Epicentrum XXI", "-6.216657", "106.834716","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTEPIC&find_by=2"));
        markerList.add(new Markers(45, "Gading XXI", "-6.158835", "106.907045","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTGADN&find_by=2"));
        markerList.add(new Markers(46, "Gandaria City XXI", "-6.243824", "106.783395","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTGAND&find_by=2"));
        markerList.add(new Markers(47, "Grand Paragon XXI", "-6.151765", "106.816189","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTGRPA&find_by=2"));
        markerList.add(new Markers(48, "Hollywood XXI", "-6.226890", "106.818571","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTHOKC&find_by=2"));
        markerList.add(new Markers(49, "Kalibata XXI", "-6.257085", "106.855920","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKALI&find_by=2"));
        markerList.add(new Markers(50, "Kasablanka XXI", "-6.223836", "106.842632","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKASA&find_by=2"));
        markerList.add(new Markers(52, "Kemang Village XXI", "-6.260762", "106.812536","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKEVI&find_by=2"));
        markerList.add(new Markers(52, "Kramat Jati XXI", "-6.270668", "106.869006","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKRJT&find_by=2"));
        markerList.add(new Markers(53, "Koja Trade Mall XXI", "-6.121018", "106.916104","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKTM&find_by=2"));
        markerList.add(new Markers(54, "Kuningan City XXI", "-6.224773", "106.829311","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTKUCI&find_by=2"));
        markerList.add(new Markers(55, "Lotte Shopping Avenue XXI", "-6.224025", "106.822720","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTLOSA&find_by=2"));
        markerList.add(new Markers(56, "Metropole XXI", "-6.199926", "106.843777","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTMETR&find_by=2"));
        markerList.add(new Markers(57, "One Bel Park XXI", "-6.304457", "106.795025","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTONBE&find_by=2"));
        markerList.add(new Markers(58, "Pejaten Village XXI", "-6.280634", "106.829207","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPEVI&find_by=2"));
        markerList.add(new Markers(59, "Plaza Indonesia XXI", "-6.192956", "106.822412","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSTUX&find_by=2"));
        markerList.add(new Markers(60, "Plaza Senayan XXI", "-6.225492", "106.799171","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPLSE&find_by=2"));
        markerList.add(new Markers(61, "Pluit Junction XXI", "-6.126078", "106.791283","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPLJU&find_by=2"));
        markerList.add(new Markers(62, "Pondok Indah 1 XXI", "-6.264692", "106.784394","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPOIN&find_by=2"));
        markerList.add(new Markers(63, "Pondok Indah 2 XXI", "-6.265872", "106.782480","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPOID&find_by=2"));
        markerList.add(new Markers(64, "Puri XXI", "-6.187357", "106.734361","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTPURI&find_by=2"));
        markerList.add(new Markers(65, "Seasons City XXI", "-6.153622", "106.796444","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSACI&find_by=2"));
        markerList.add(new Markers(65, "Senayan City XXI", "-6.226983", "106.797420","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSECI&find_by=2"));
        markerList.add(new Markers(66, "Setiabudi XXI", "-6.214909", "106.830196","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSETI&find_by=2"));
        markerList.add(new Markers(67, "ST. Moritz XXI", "-6.187594", "106.739605","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSTMO&find_by=2"));
        markerList.add(new Markers(68, "Sunter XXI", "-6.138258", "106.870906","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTSUNT&find_by=2"));
        markerList.add(new Markers(69, "TIM XXI", "-6.188820", "106.839717","https://m.21cineplex.com/gui.schedule.php?cinema_id=JKTTIM&find_by=2"));

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