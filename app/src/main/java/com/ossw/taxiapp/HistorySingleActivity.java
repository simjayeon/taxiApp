package com.ossw.taxiapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistorySingleActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private String rideID, currentUserID, customerId, driverId, userDriverOrCustomer;

    private TextView locationRide, distanceRide, dateRide, nameUser;
    private ImageView imageUser;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private DatabaseReference historyRideInfoDb;
    private LatLng destinationLatLng, pickupLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_single);

        polylines = new ArrayList<>();
        rideID = getIntent().getExtras().getString("rideID");

        mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        locationRide = (TextView)findViewById(R.id.rideLocation);
        distanceRide = (TextView)findViewById(R.id.rideDistance);
        dateRide = (TextView)findViewById(R.id.rideDate);
        nameUser = (TextView)findViewById(R.id.userName);
        imageUser = (ImageView)findViewById(R.id.userImage);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        historyRideInfoDb = FirebaseDatabase.getInstance().getReference().child("history").child(rideID);
        getRideInformation();
    }

    private void getRideInformation() {
        historyRideInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        if(child.getKey().equals("customer")){
                            customerId = child.getValue().toString();
                            if(!customerId.equals(currentUserID)){
                                userDriverOrCustomer = "Drivers";
                                getUserInformation("Customers", customerId);
                            }
                        }

                        if(child.getKey().equals("driver")) {
                            driverId = child.getValue().toString();
                            if (!driverId.equals(currentUserID)) {
                                userDriverOrCustomer = "Customers";
                                getUserInformation("Drivers", driverId);
                            }
                        }

                        if(child.getKey().equals("timestamp")) {
                            dateRide.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if(child.getKey().equals("destination")) {
                            locationRide.setText(getDate(Long.valueOf(child.getValue().toString())));

                        }
                        if(child.getKey().equals("location")) {
                            locationRide.setText(getDate(Long.valueOf(child.getValue().toString())));
                            pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()),Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            destinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()),Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            if(destinationLatLng != new LatLng(0,0)){
                                getRouteToMarker();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInformation(String otherUserDriverOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserDriverOrCustomer).child(otherUserId);
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        nameUser.setText(map.get("name").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(imageUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private String getDate(Long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(timestamp*1000);
        String date = DateFormat.format("dd-MM-yyyy hh:mm", cal).toString();
        return  date;
    }

    //경로 마커
    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(pickupLatLng, destinationLatLng)
                .build();
        routing.execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    //라우팅리스너
    private List<Polyline> polylines;
    private static final int [] COLORS = new int[]{
            R.color.primary_dark_material_light};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Someting went wrong, try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width*0.2);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("pickup location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("destination"));
        if(polylines.size()>0){
            for(Polyline poly : polylines){
                poly.remove();
            }
        }
        polylines = new ArrayList<>();

        for(int i=0; i<route.size(); i++){

            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10+i*3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route "+(i+1) +": distance - "+route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
