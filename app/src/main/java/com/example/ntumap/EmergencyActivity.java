package com.example.ntumap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private GoogleMap mMap;
    private TextView emergencyStatusText;
    private ListView emergencyListView;
    private Button evacuationRouteButton;
    private Button contactSecurityButton;
    private Button safeSpacesButton;
    
    private List<EmergencyAlert> emergencyAlerts;
    private EmergencyAlertAdapter alertAdapter;
    
    // NTU Clifton Campus coordinates
    private static final LatLng NTU_CLIFTON = new LatLng(52.9068, -1.1878);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        setupMap();
        loadEmergencyAlerts();
    }
    
    private void initializeViews() {
        emergencyStatusText = findViewById(R.id.emergencyStatusText);
        emergencyListView = findViewById(R.id.emergencyListView);
        evacuationRouteButton = findViewById(R.id.evacuationRouteButton);
        contactSecurityButton = findViewById(R.id.contactSecurityButton);
        safeSpacesButton = findViewById(R.id.safeSpacesButton);
    }
    
    private void setupClickListeners() {
        evacuationRouteButton.setOnClickListener(v -> showEvacuationRoute());
        contactSecurityButton.setOnClickListener(v -> contactSecurity());
        safeSpacesButton.setOnClickListener(v -> showSafeSpaces());
    }
    
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.emergencyMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Add NTU campus marker
        mMap.addMarker(new MarkerOptions()
                .position(NTU_CLIFTON)
                .title("NTU Clifton Campus")
                .snippet("Current Location"));
        
        // Move camera to NTU
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTU_CLIFTON, 15));
        
        // Add emergency exit markers
        addEmergencyExits();
    }
    
    private void addEmergencyExits() {
        // Add emergency exit locations around campus
        LatLng[] emergencyExits = {
            new LatLng(52.9070, -1.1880), // Library exit
            new LatLng(52.9066, -1.1876), // Main building exit
            new LatLng(52.9072, -1.1882), // Cafeteria exit
            new LatLng(52.9064, -1.1874), // Gym exit
        };
        
        String[] exitNames = {"Library Emergency Exit", "Main Building Exit", "Cafeteria Exit", "Gym Exit"};
        
        for (int i = 0; i < emergencyExits.length; i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(emergencyExits[i])
                    .title(exitNames[i])
                    .snippet("Emergency Exit"));
        }
    }
    
    private void loadEmergencyAlerts() {
        emergencyAlerts = new ArrayList<>();
        
        // Simulate emergency alerts
        emergencyAlerts.add(new EmergencyAlert("Fire Drill", "Scheduled fire drill in Main Building", "Low", "10 minutes ago"));
        emergencyAlerts.add(new EmergencyAlert("Medical Emergency", "Medical assistance needed in Library", "Medium", "5 minutes ago"));
        emergencyAlerts.add(new EmergencyAlert("Weather Warning", "Severe weather approaching campus", "High", "2 minutes ago"));
        
        // Create adapter
        alertAdapter = new EmergencyAlertAdapter(this, emergencyAlerts);
        emergencyListView.setAdapter(alertAdapter);
        
        // Update status
        updateEmergencyStatus();
    }
    
    private void updateEmergencyStatus() {
        if (emergencyAlerts.isEmpty()) {
            emergencyStatusText.setText("No Active Emergencies");
            emergencyStatusText.setBackgroundColor(android.graphics.Color.GREEN);
        } else {
            EmergencyAlert highestPriority = getHighestPriorityAlert();
            emergencyStatusText.setText("Active Emergency: " + highestPriority.getTitle());
            
            switch (highestPriority.getPriority()) {
                case "Low":
                    emergencyStatusText.setBackgroundColor(android.graphics.Color.YELLOW);
                    break;
                case "Medium":
                    emergencyStatusText.setBackgroundColor(android.graphics.Color.rgb(255, 165, 0)); // Orange
                    break;
                case "High":
                    emergencyStatusText.setBackgroundColor(android.graphics.Color.RED);
                    break;
            }
        }
    }
    
    private EmergencyAlert getHighestPriorityAlert() {
        EmergencyAlert highest = emergencyAlerts.get(0);
        for (EmergencyAlert alert : emergencyAlerts) {
            if (getPriorityLevel(alert.getPriority()) > getPriorityLevel(highest.getPriority())) {
                highest = alert;
            }
        }
        return highest;
    }
    
    private int getPriorityLevel(String priority) {
        switch (priority) {
            case "Low": return 1;
            case "Medium": return 2;
            case "High": return 3;
            default: return 0;
        }
    }
    
    private void showEvacuationRoute() {
        // Show evacuation route on map
        if (mMap != null) {
            // Clear existing routes
            mMap.clear();
            
            // Add current location
            mMap.addMarker(new MarkerOptions()
                    .position(NTU_CLIFTON)
                    .title("Current Location")
                    .snippet("You are here"));
            
            // Add nearest emergency exit
            LatLng nearestExit = new LatLng(52.9070, -1.1880); // Library exit
            mMap.addMarker(new MarkerOptions()
                    .position(nearestExit)
                    .title("Nearest Emergency Exit")
                    .snippet("Follow this route"));
            
            // Draw evacuation route
            List<LatLng> routePoints = new ArrayList<>();
            routePoints.add(NTU_CLIFTON);
            routePoints.add(nearestExit);
            
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(0xFFFF0000); // Red color for emergency route
            
            mMap.addPolyline(polylineOptions);
            
            // Move camera to show route
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTU_CLIFTON, 16));
            
            Toast.makeText(this, "Evacuation route displayed. Follow the red line to the nearest exit.", 
                          Toast.LENGTH_LONG).show();
        }
    }
    
    private void contactSecurity() {
        // Simulate contacting security
        Toast.makeText(this, "Contacting campus security...", Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // - Call campus security number
        // - Send location data
        // - Open emergency contact interface
        
        new android.app.AlertDialog.Builder(this)
                .setTitle("Contact Security")
                .setMessage("Campus Security: +44 115 848 8888\n\nWould you like to call now?")
                .setPositiveButton("Call", (dialog, which) -> {
                    Toast.makeText(EmergencyActivity.this, "Calling campus security...", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void showSafeSpaces() {
        // Show safe spaces on campus
        if (mMap != null) {
            mMap.clear();
            
            // Add safe space locations
            LatLng[] safeSpaces = {
                new LatLng(52.9070, -1.1880), // Library
                new LatLng(52.9066, -1.1876), // Main building lobby
                new LatLng(52.9072, -1.1882), // Student union
            };
            
            String[] safeSpaceNames = {"Library Safe Space", "Main Building Lobby", "Student Union"};
            
            for (int i = 0; i < safeSpaces.length; i++) {
                mMap.addMarker(new MarkerOptions()
                        .position(safeSpaces[i])
                        .title(safeSpaceNames[i])
                        .snippet("Safe Space - Staff Available"));
            }
            
            // Move camera to show safe spaces
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NTU_CLIFTON, 15));
            
            Toast.makeText(this, "Safe spaces marked on map. These locations have staff available for assistance.", 
                          Toast.LENGTH_LONG).show();
        }
    }
    
    // Emergency Alert data class
    public static class EmergencyAlert {
        private String title;
        private String description;
        private String priority;
        private String timeAgo;
        
        public EmergencyAlert(String title, String description, String priority, String timeAgo) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.timeAgo = timeAgo;
        }
        
        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getPriority() { return priority; }
        public String getTimeAgo() { return timeAgo; }
        
        @Override
        public String toString() {
            return String.format("%s (%s) - %s", title, priority, timeAgo);
        }
    }
    
    // Custom adapter for emergency alerts
    private static class EmergencyAlertAdapter extends android.widget.ArrayAdapter<EmergencyAlert> {
        private final List<EmergencyAlert> alerts;
        
        public EmergencyAlertAdapter(EmergencyActivity context, List<EmergencyAlert> alerts) {
            super(context, android.R.layout.simple_list_item_1, alerts);
            this.alerts = alerts;
        }
        
        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            android.view.View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            
            EmergencyAlert alert = alerts.get(position);
            textView.setText(alert.toString());
            
            // Color code based on priority
            switch (alert.getPriority()) {
                case "Low":
                    textView.setTextColor(android.graphics.Color.GREEN);
                    break;
                case "Medium":
                    textView.setTextColor(android.graphics.Color.rgb(255, 165, 0)); // Orange
                    break;
                case "High":
                    textView.setTextColor(android.graphics.Color.RED);
                    break;
            }
            
            return view;
        }
    }
} 