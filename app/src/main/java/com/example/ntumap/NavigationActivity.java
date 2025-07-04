package com.example.ntumap;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private String destination;
    private boolean isAIRoute;
    
    // UI Components
    private TextView destinationText;
    private TextView distanceText;
    private TextView timeText;
    private Button startNavigationButton;
    private Button accessibilityButton;
    
    // Navigation data
    private LatLng currentLocation;
    private LatLng destinationLocation;
    private List<LatLng> routePoints;
    
    // NTU Clifton Campus coordinates
    private static final LatLng NTU_CLIFTON = new LatLng(52.9068, -1.1878);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        
        // Get intent data
        Intent intent = getIntent();
        destination = intent.getStringExtra("destination");
        isAIRoute = intent.getBooleanExtra("ai_route", false);
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Initialize UI
        initializeViews();
        setupMap();
        
        // Calculate route
        calculateRoute();
    }
    
    private void initializeViews() {
        destinationText = findViewById(R.id.destinationText);
        distanceText = findViewById(R.id.distanceText);
        timeText = findViewById(R.id.timeText);
        startNavigationButton = findViewById(R.id.startNavigationButton);
        accessibilityButton = findViewById(R.id.accessibilityButton);
        
        // Set destination text
        if (destination != null) {
            destinationText.setText("Destination: " + destination);
        }
        
        // Setup button listeners
        startNavigationButton.setOnClickListener(v -> startTurnByTurnNavigation());
        accessibilityButton.setOnClickListener(v -> enableAccessibilityMode());
    }
    
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigationMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Add destination marker
        if (destination != null) {
            destinationLocation = getDestinationCoordinates(destination);
            mMap.addMarker(new MarkerOptions()
                    .position(destinationLocation)
                    .title(destination)
                    .snippet("Your destination"));
        }
        
        // Move camera to show both current location and destination
        if (currentLocation != null && destinationLocation != null) {
            LatLng center = new LatLng(
                (currentLocation.latitude + destinationLocation.latitude) / 2,
                (currentLocation.longitude + destinationLocation.longitude) / 2
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTU_CLIFTON, 15));
        }
    }
    
    private void calculateRoute() {
        // Simulate route calculation
        if (destination != null) {
            destinationLocation = getDestinationCoordinates(destination);
            
            // For demo purposes, use a simple route
            routePoints = new ArrayList<>();
            routePoints.add(NTU_CLIFTON); // Start at NTU
            routePoints.add(destinationLocation); // End at destination
            
            // Calculate distance and time
            double distance = calculateDistance(NTU_CLIFTON, destinationLocation);
            int timeMinutes = (int) (distance / 1000 * 15); // Rough estimate: 15 min per km
            
            // Update UI
            distanceText.setText(String.format("Distance: %.1f km", distance / 1000));
            timeText.setText(String.format("Estimated time: %d minutes", timeMinutes));
            
            // Draw route on map
            if (mMap != null) {
                drawRoute();
            }
        }
    }
    
    private void drawRoute() {
        if (routePoints != null && routePoints.size() >= 2) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(0xFF2196F3) // Blue color
                    .startCap(new RoundCap())
                    .endCap(new RoundCap());
            
            mMap.addPolyline(polylineOptions);
        }
    }
    
    private LatLng getDestinationCoordinates(String destination) {
        // Simulate destination coordinates based on search query
        // In a real app, this would use Google Places API
        switch (destination.toLowerCase()) {
            case "library":
                return new LatLng(52.9070, -1.1880);
            case "cafeteria":
            case "canteen":
                return new LatLng(52.9072, -1.1882);
            case "gym":
            case "fitness":
                return new LatLng(52.9064, -1.1874);
            case "computer lab":
            case "lab":
                return new LatLng(52.9066, -1.1876);
            default:
                // Default to a location near NTU
                return new LatLng(52.9068, -1.1878);
        }
    }
    
    private double calculateDistance(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude, 
                                end.latitude, end.longitude, results);
        return results[0];
    }
    
    private void startTurnByTurnNavigation() {
        // Start turn-by-turn navigation
        Toast.makeText(this, "Starting turn-by-turn navigation to " + destination, 
                      Toast.LENGTH_SHORT).show();
        
        // In a real app, this would start Google Maps navigation
        // For now, just show a success message
        Toast.makeText(this, "Navigation started! Follow the route on the map.", 
                      Toast.LENGTH_LONG).show();
    }
    
    private void enableAccessibilityMode() {
        // Enable accessibility features for navigation
        Toast.makeText(this, "Accessibility mode enabled. Voice guidance activated.", 
                      Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // - Enable voice guidance
        // - Increase text size
        // - Enable high contrast mode
        // - Activate screen reader support
    }
    
    // AI-powered route optimization
    private void optimizeRouteWithAI() {
        if (isAIRoute) {
            Toast.makeText(this, "AI optimizing route for best accessibility and efficiency...", 
                          Toast.LENGTH_SHORT).show();
            
            // Simulate AI processing
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        Toast.makeText(NavigationActivity.this, 
                                      "AI route optimized! Considered accessibility, traffic, and energy efficiency.", 
                                      Toast.LENGTH_LONG).show();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
} 