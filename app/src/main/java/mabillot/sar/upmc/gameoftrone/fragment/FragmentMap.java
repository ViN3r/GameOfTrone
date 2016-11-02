package mabillot.sar.upmc.gameoftrone.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import mabillot.sar.upmc.gameoftrone.MainActivity;
import mabillot.sar.upmc.gameoftrone.R;
import mabillot.sar.upmc.gameoftrone.metier.Arbitre;
import mabillot.sar.upmc.gameoftrone.metier.Localisation;
import mabillot.sar.upmc.gameoftrone.metier.Toilette;
import mabillot.sar.upmc.gameoftrone.outils.Util;

/**
 * Created by paulo on 09/01/2016.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,GoogleMap.OnMarkerClickListener {
    private MapView mapView;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Marker myMarker,toiletteMarker;
    private Circle previousCircle;

    private static final int COLOR = 0x7500ff00;

    private Arbitre arbitre;
    private Toilette selectedToilette;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_map, container, false);
        mapView = (MapView) view.findViewById(R.id.viewMap);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        arbitre = ((MainActivity) getActivity()).getArbitre();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setOnMarkerClickListener(this);

            for(Toilette t : arbitre.getToilettes()){
                Double.parseDouble(new Float(t.getLocalisation().getX()).toString());
                doToiletteMarker(t, googleMap);
            }
            mapView.onResume();
        }else{
            Toast.makeText(this.getActivity(), "This application need to access at location", Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng latlng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());

                this.iconPlayer(latlng);

                //Center and zoom
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(Util.convertCenterAndRadiusToBounds(latlng, 1000), 20));

                arbitre.getJoueur().setLocalisation(new Localisation(Float.parseFloat(String.valueOf(mLastLocation.getLatitude())),Float.parseFloat(String.valueOf(mLastLocation.getLatitude()))));
            }
            startLocationUpdates();
        }else{
            Toast.makeText(this.getActivity(), "This application need to access at location", Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    protected void startLocationUpdates() {
        //TODO Version 23 (voir MainActivity)

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else{
            Toast.makeText(this.getActivity(), "This application need to access at location", Toast.LENGTH_SHORT).show();
            this.getActivity().finish();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        this.iconPlayer(latlng);
        arbitre.getJoueur().setLocalisation(new Localisation(Float.parseFloat(String.valueOf(location.getLatitude())), Float.parseFloat(String.valueOf(location.getLatitude()))));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        toiletteMarker.hideInfoWindow();
        toiletteMarker = marker;

        selectedToilette = arbitre.findToilettes(toiletteMarker);
        toiletteMarker.showInfoWindow();

        return true;
    }

    public boolean attackToilette(){
        /* Check distance */
        if(toiletteMarker == null || myMarker == null || Util.distance(myMarker.getPosition(), toiletteMarker.getPosition()) >= arbitre.getJoueur().getArme().getPortee()) {
            return false;
        }

        if(!arbitre.getJoueur().attacks(selectedToilette)){
            return false;
        }

        if( selectedToilette != null) {
            if (selectedToilette.isConquis()) {
                toiletteMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.toilette_conquise));
                toiletteMarker.setTitle(arbitre.getJoueur().getNom());
            } else {
                toiletteMarker.setTitle("Toilette: " + selectedToilette.getPv());
            }
            toiletteMarker.showInfoWindow();
        }
        return true;
    }


    /*
     * Delete previous marker and circle for player
     * Add a new Marker and circle
     * set icon Marker
     */
    private void iconPlayer(LatLng latlng){
        if(myMarker != null){
            myMarker.remove();
        }
        myMarker = map.addMarker(new MarkerOptions().position(latlng));
        myMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.sword));

        if(previousCircle != null){
            previousCircle.remove();
        }
        previousCircle = map.addCircle(new CircleOptions().center(latlng).radius(arbitre.getJoueur().getArme().getPortee()).fillColor(COLOR));
    }

    /*
     * Add the correct marker for the selected toilette
     * if not controlled: white
     * else: black
     */
    private void doToiletteMarker(Toilette toilette, GoogleMap googleMap){
        LatLng latlng = new LatLng(Double.parseDouble(new Float(toilette.getLocalisation().getX()).toString()),Double.parseDouble(new Float(toilette.getLocalisation().getY()).toString()));


        if(toilette.isConquis()) {
            toiletteMarker = googleMap.addMarker(new MarkerOptions().position(latlng).title(arbitre.getJoueur().getNom()));
            toiletteMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.toilette_conquise));
        }else{
            toiletteMarker = googleMap.addMarker(new MarkerOptions().position(latlng).title("Toilette: " + toilette.getPv()));
            toiletteMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.toilette));
        }
    }
}
