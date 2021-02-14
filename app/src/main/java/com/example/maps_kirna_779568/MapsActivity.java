package com.example.maps_kirna_779568;

import androidx.fragment.app.FragmentActivity;



import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import com.example.maps_kirna_779568.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import static android.graphics.Bitmap.Config.ARGB_8888;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;
    private Marker firstMarker;
    private Marker destMarker;

    private List<LatLng> latLngList = new ArrayList<>();
    List<Polyline> polylineList = new ArrayList<>();
    List<Marker> markers = new ArrayList();


    Polyline line;
    Polygon shape;
    private static final int POLYGON_SIDES = 4;


    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();


// apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
            }
        } );

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                setMarker(latLng);
            }
        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                setMarker(marker.getPosition());

            }
        });

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Log.d(TAG, "onPolygonClick: " +polygon.getPoints());
                float[] results = new float[1];
                double distance = 0.0;

                for (int i = 0; i<POLYGON_SIDES; i++) {

                    Location.distanceBetween(polygon.getPoints().get(i).latitude,polygon.getPoints().get(i).longitude,polygon.getPoints().get(i+1).latitude,polygon.getPoints().get(i+1).longitude,results);
                    distance += ((float) results[0])/1000;


                }

                Toast.makeText(MapsActivity.this, "Total Distance is: " +(String.format (Locale.CANADA,"%.2f KM",distance)) , Toast.LENGTH_SHORT).show();

            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

//// Toast.makeText(MapsActivity.this, "onPolylineClick", Toast.LENGTH_SHORT).show();
                LatLng location1 = polyline.getPoints().get(0);
                LatLng location2 = polyline.getPoints().get(1);


//// double distance = getDistanceMeters(location1.latitude,location1.longitude,location2.latitude,location2.latitude);

                LatLng midValue = midPoint(location1.latitude, location1.longitude, location2.latitude, location2.longitude);

                float[] results = new float[1];

                Location.distanceBetween(location1.latitude, location1.longitude, location2.latitude, location2.longitude, results);

                float distance = (results[0]) / 1000;

                Toast.makeText(MapsActivity.this, "Distance is " + (String.format (Locale.CANADA,"%.2f KM",distance)), Toast.LENGTH_SHORT).show();

//// distMarker(midValue,( (float) results[0]),null);
            }

            private void distMarker(LatLng latLng, double distance, String snippet)
            {

// BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.mipmap.transparent);
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(String.format(Locale.CANADA,"%.2f Km", distance))
                        .snippet(snippet)
//.icon(transparent)
                        .anchor((float) 0.5, (float) 0.5);

                Marker marker = mMap.addMarker(options);

//open the marker's info window
                marker.showInfoWindow();
            }

            public LatLng midPoint(double lat1,double lon1,double lat2,double lon2){



                double dLon = Math.toRadians(lon2 - lon1);

//convert to radians
                lat1 = Math.toRadians(lat1);
                lat2 = Math.toRadians(lat2);
                lon1 = Math.toRadians(lon1);

                double Bx = Math.cos(lat2) * Math.cos(dLon);
                double By = Math.cos(lat2) * Math.sin(dLon);
                double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
                double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

                return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));

            }
        });

    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }
//// public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {
////
//// double l1 = toRadians(lat1);
//// double l2 = toRadians(lat2);
//// double g1 = toRadians(lng1);
//// double g2 = toRadians(lng2);
////
//// double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
//// if(dist < 0) {
//// dist = dist + Math.PI;
//// }
//// return ;
//// }

    private LatLng getPolygonCenterPoint(ArrayList<LatLng> polygonPointsList){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygonPointsList.size() ; i++)
        {
            builder.include(polygonPointsList.get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng = bounds.getCenter();

        return centerLatLng;
    }

    private void setMarker(LatLng latLng) {

        HashMap<String, String> markerPoint = new HashMap<>();
        markerPoint = geoCoder(latLng);

        Log.d(TAG, "setMarker: " +markerPoint.get("postalCode"));

        String letters = "A";
        if (markers.size() ==0) {
            letters = "A";
        }
        if (markers.size() ==1) {
            letters = "B";
        }
        if (markers.size() ==2) {
            letters = "C";
        }
        if (markers.size() ==3) {
            letters = "D";
        }

        MarkerOptions options = new MarkerOptions().position(latLng)
                .draggable(true)
                .title(markerPoint.get("thoroughfare") + "," + markerPoint.get("subThoroughfare" )+ "," + markerPoint.get("postalCode"))
// .icon(BitmapDescriptorFactory.fromBitmap(makeBitmap(this,letters)))
                .snippet(markerPoint.get("locality") + "," + markerPoint.get("adminArea"));


        if(markers.size() == POLYGON_SIDES){
            firstMarker = null;
            mMap.clear();
            clearMap(); }

        Marker marker = mMap.addMarker(options);
        drawLine(marker);
        firstMarker = marker;
        markers.add(marker);
        if(markers.size() == POLYGON_SIDES){
            drawShape(); }

    }

    private void drawShape() {

        PolygonOptions options = new PolygonOptions()
                .clickable(true)
                .fillColor(0x3500FF00)
                .strokeColor(Color.RED)
                .strokeWidth(10);

        for(int i = 0; i < POLYGON_SIDES; i++){
            Marker marker = markers.get(i);
            latLngList.add(marker.getPosition());
        }

        for (Polyline polyline : polylineList){
            polyline.remove();
        }
        polylineList.clear();


        for (int i = 0; i < POLYGON_SIDES; i++) {

            int last;
            if (i+1 == POLYGON_SIDES){last = 0; }
            else {last = i+1;}

            drawLine(latLngList.get(i), latLngList.get(last));
        }

        for (LatLng latLng : latLngList)
        {
            options.add(latLng);
        }

        shape = mMap.addPolygon(options);
    }

    private void clearMap() {

        polylineList.clear();
        latLngList.clear();

        for (Marker marker : markers) {
            marker.remove();
        }

        markers.clear();
        shape.remove();
        line.remove();
        shape = null;
        line = null;

    }

    private void drawLine(LatLng latLng1, LatLng latLng2) {
        PolylineOptions options = new PolylineOptions()
                .clickable(true)
                .color(Color.RED)
                .width(10)
                .add(latLng1,latLng2);
        line = mMap.addPolyline(options);
    }

    private void drawLine(Marker marker)
    {
        if (firstMarker != null) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .clickable(true)
                    .color(Color.RED)
                    .width(10)
                    .add(marker.getPosition(), firstMarker.getPosition());
            polylineList.add(mMap.addPolyline(polylineOptions));
        }
    }

    public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK); // Text color
        paint.setTextSize(20 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.GRAY); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width() - 15 ; // 10 for padding from right
        int y = bounds.height();
        canvas.drawText(text, x, y, paint);

        return bitmap;
    }

    private void startUpdateLocations() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// TODO: Consider calling
// ActivityCompat#requestPermissions
// here to request the missing permissions, and then overriding
// public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
// to handle the case where the user grants the permission. See the documentation
// for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

//// Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//// setHomeMarker(lastKnownLocation);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }


    public HashMap<String, String> geoCoder(LatLng latLng) {

        HashMap<String, String> markerPoint = new HashMap<>();

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                if (addresses.get(0).getAdminArea() != null)
                    markerPoint.put("adminArea", addresses.get(0).getAdminArea());

                if (addresses.get(0).getLocality() != null)
                    markerPoint.put("locality", addresses.get(0).getLocality());

                if (addresses.get(0).getPostalCode() != null)
                    markerPoint.put("postalCode", addresses.get(0).getPostalCode());

                if (addresses.get(0).getThoroughfare() != null)
                    markerPoint.put("thoroughfare", addresses.get(0).getThoroughfare());

                if (addresses.get(0).getSubThoroughfare() != null)
                    markerPoint.put("subThoroughfare", addresses.get(0).getSubThoroughfare());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "geoCoder: " +markerPoint.get("postalCode"));

        return markerPoint;
    }
}


