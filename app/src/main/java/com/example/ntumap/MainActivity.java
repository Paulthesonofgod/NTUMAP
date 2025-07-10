package com.example.ntumap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1002;
    
    // UI Components
    private EditText searchEditText;
    private Button voiceButton;
    private Button openaiButton;
    private ImageButton menuButton;
    private ImageButton notificationButton;
    private ImageButton filterButton;
    private TextView campusName;
    
    // Google Maps
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    
    // Occupancy data (simulated for demo)
    private Map<String, RoomOccupancy> roomOccupancyMap;
    private Map<Marker, String> markerToRoomMap;
    
    // NTU Clifton Campus coordinates
    private static final LatLng NTU_CLIFTON = new LatLng(52.9068, -1.1878);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Check if this is the first launch
        checkFirstLaunch();
        
        // Initialize UI components
        initializeViews();
        setupClickListeners();
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        // Initialize occupancy data
        initializeOccupancyData();
        
        // Setup map
        setupMap();
        
        // Request permissions
        requestPermissions();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void checkFirstLaunch() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("is_first_launch", true);
        
        if (isFirstLaunch) {
            // Show onboarding
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
            
            // Mark as not first launch
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("is_first_launch", false);
            editor.apply();
        }
    }
    
    private void initializeViews() {
        searchEditText = findViewById(R.id.searchEditText);
        voiceButton = findViewById(R.id.voiceButton);
        openaiButton = findViewById(R.id.openaiButton);
        menuButton = findViewById(R.id.menuButton);
        notificationButton = findViewById(R.id.notificationButton);
        filterButton = findViewById(R.id.filterButton);
        campusName = findViewById(R.id.campusName);
    }
    
    private void setupClickListeners() {
        // Menu button - opens accessibility settings
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AccessibilityActivity.class);
            startActivity(intent);
        });
        
        // Notification button - shows emergency notifications
        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EmergencyActivity.class);
            startActivity(intent);
        });
        
        // Voice button - voice search functionality
        voiceButton.setOnClickListener(v -> {
            // TODO: Implement voice search
            Toast.makeText(this, "Voice search coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        // OpenAI button - AI-powered navigation
        openaiButton.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString();
            if (!searchQuery.isEmpty()) {
                // Use AI to find the best route
                performAISearch(searchQuery);
            } else {
                Toast.makeText(this, "Please enter a destination", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Filter button - filter room types
        filterButton.setOnClickListener(v -> {
            showFilterDialog();
        });
        
        // Search functionality
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString();
            if (!query.isEmpty()) {
                searchLocation(query);
            }
            return true;
        });
    }
    
    private void initializeOccupancyData() {
        roomOccupancyMap = new HashMap<>();
        markerToRoomMap = new HashMap<>();
        
        // Simulate real-time occupancy data for NTU buildings
        roomOccupancyMap.put("Room A", new RoomOccupancy("Room A", 12, 20, "Lecture Hall"));
        roomOccupancyMap.put("Room B", new RoomOccupancy("Room B", 3, 15, "Study Room"));
        roomOccupancyMap.put("Library", new RoomOccupancy("Library", 45, 100, "Study Space"));
        roomOccupancyMap.put("Computer Lab", new RoomOccupancy("Computer Lab", 8, 25, "Lab"));
        roomOccupancyMap.put("Cafeteria", new RoomOccupancy("Cafeteria", 67, 80, "Dining"));
        roomOccupancyMap.put("Gym", new RoomOccupancy("Gym", 12, 30, "Recreation"));
        
        // Update occupancy display
        updateOccupancyDisplay();
    }
    
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Add NTU Clifton campus marker
        mMap.addMarker(new MarkerOptions()
                .position(NTU_CLIFTON)
                .title("NTU Clifton Campus")
                .snippet("Nottingham Trent University"));
        
        // Move camera to NTU
        mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(NTU_CLIFTON, 15));
        
        // Enable location if permission granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        
        // Add room markers with occupancy data
        addRoomMarkers();
        
        // Set marker click listener for navigation
        mMap.setOnMarkerClickListener(marker -> {
            String roomName = markerToRoomMap.get(marker);
            if (roomName != null) {
                showRoomDetails(roomName);
                return true; // Consume the event
            }
            return false; // Let default behavior handle it
        });
    }
    
    private void addRoomMarkers() {
        // Add markers for different buildings/rooms on campus with larger tap areas
        LatLng[] roomLocations = {
            new LatLng(52.9068, -1.1878), // Main building
            new LatLng(52.9070, -1.1880), // Library
            new LatLng(52.9066, -1.1876), // Computer lab
            new LatLng(52.9072, -1.1882), // Cafeteria
            new LatLng(52.9064, -1.1874), // Gym
        };
        
        String[] roomNames = {"Main Building", "Library", "Computer Lab", "Cafeteria", "Gym"};
        
        for (int i = 0; i < roomLocations.length; i++) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(roomLocations[i])
                    .title(roomNames[i])
                    .snippet("Tap for details and navigation"));
            
            if (marker != null) {
                markerToRoomMap.put(marker, roomNames[i]);
            }
        }
    }
    
    private void showRoomDetails(String roomName) {
        // Show room details and navigation options
        RoomOccupancy occupancy = roomOccupancyMap.get(roomName);
        if (occupancy != null) {
            String message = String.format("%s\nOccupancy: %d/%d (%.1f%%)\nType: %s", 
                roomName, 
                occupancy.getCurrentOccupancy(), 
                occupancy.getMaxCapacity(),
                occupancy.getOccupancyPercentage(),
                occupancy.getRoomType());
            
            // Show dialog with navigation option
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Room Details")
                .setMessage(message)
                .setPositiveButton("Navigate", (dialog, which) -> {
                    startNavigation(roomName);
                })
                .setNegativeButton("Close", null)
                .show();
        }
    }
    
    private void startNavigation(String destination) {
        Intent intent = new Intent(this,
                NavigationActivity.class);
        intent.putExtra("destination", destination);
        intent.putExtra("ai_route", false);
        startActivity(intent);
    }
    
    private void requestPermissions() {
        // Request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                LOCATION_PERMISSION_REQUEST_CODE);
        }
        
        // Request Bluetooth permissions for occupancy monitoring
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 
                BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }
    
    private void searchLocation(String query) {
        // Implement search functionality with visual feedback
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
        
        // Highlight matching markers on the map
        if (mMap != null) {
            // Clear previous highlights
            mMap.clear();
            
            // Re-add all markers
            addRoomMarkers();
            
            // Find and highlight matching markers
            for (Map.Entry<Marker, String> entry : markerToRoomMap.entrySet()) {
                Marker marker = entry.getKey();
                String roomName = entry.getValue();
                
                if (roomName.toLowerCase().contains(query.toLowerCase())) {
                    // Highlight the matching marker
                    marker.setTitle("📍 " + roomName + " (Found!)");
                    marker.showInfoWindow();
                    
                    // Move camera to the found location
                    mMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                        marker.getPosition(), 17));
                    
                    // Show room details
                    showRoomDetails(roomName);
                    return;
                }
            }
            
            // If no exact match found, show suggestions
            showSearchSuggestions(query);
        }
    }
    
    private void showSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        
        for (String roomName : roomOccupancyMap.keySet()) {
            if (roomName.toLowerCase().contains(query.toLowerCase())) {
                suggestions.add(roomName);
            }
        }
        
        if (!suggestions.isEmpty()) {
            String[] suggestionArray = suggestions.toArray(new String[0]);
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Search Results")
                .setItems(suggestionArray, (dialog, which) -> {
                    String selectedRoom = suggestionArray[which];
                    searchEditText.setText(selectedRoom);
                    searchLocation(selectedRoom);
                })
                .setNegativeButton("Cancel", null)
                .show();
        } else {
            Toast.makeText(this, "No locations found matching: " + query, Toast.LENGTH_SHORT).show();
        }
    }
    
    private void performAISearch(String query) {
        // TODO: Integrate with OpenAI API for intelligent navigation
        Toast.makeText(this, "AI analyzing best route to: " + query, Toast.LENGTH_SHORT).show();
        
        // Simulate AI processing
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate AI processing time
                runOnUiThread(() -> {
                    // Show AI-powered route
                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    intent.putExtra("destination", query);
                    intent.putExtra("ai_route", true);
                    startActivity(intent);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void showFilterDialog() {
        // Implement filter dialog for room types
        String[] roomTypes = {"All", "Lecture Hall", "Study Room", "Lab", "Dining", "Recreation"};
        boolean[] checkedItems = {true, false, false, false, false, false};
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Filter by Room Type")
            .setMultiChoiceItems(roomTypes, checkedItems, (dialog, which, isChecked) -> {
                // Handle filter selection
                if (which == 0) { // "All" option
                    // If "All" is selected, uncheck others
                    if (isChecked) {
                        for (int i = 1; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                        }
                    }
                } else {
                    // If specific type is selected, uncheck "All"
                    checkedItems[0] = false;
                }
            })
            .setPositiveButton("Apply", (dialog, which) -> {
                applyFilters(roomTypes, checkedItems);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void applyFilters(String[] roomTypes, boolean[] checkedItems) {
        // Apply selected filters to the map
        if (mMap != null) {
            mMap.clear();
            
            // Re-add markers based on filters
            for (Map.Entry<Marker, String> entry : markerToRoomMap.entrySet()) {
                Marker marker = entry.getKey();
                String roomName = entry.getValue();
                RoomOccupancy occupancy = roomOccupancyMap.get(roomName);
                
                if (occupancy != null) {
                    boolean shouldShow = false;
                    
                    if (checkedItems[0]) { // "All" is selected
                        shouldShow = true;
                    } else {
                        // Check if room type matches any selected filter
                        for (int i = 1; i < checkedItems.length; i++) {
                            if (checkedItems[i] && occupancy.getRoomType().equals(roomTypes[i])) {
                                shouldShow = true;
                                break;
                            }
                        }
                    }
                    
                    if (shouldShow) {
                        // Re-add the marker
                        Marker newMarker = mMap.addMarker(new MarkerOptions()
                                .position(marker.getPosition())
                                .title(roomName)
                                .snippet("Tap for details and navigation"));
                        
                        if (newMarker != null) {
                            markerToRoomMap.put(newMarker, roomName);
                        }
                    }
                }
            }
            
            Toast.makeText(this, "Filters applied successfully", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateOccupancyDisplay() {
        // Update the visual occupancy indicators on the map
        // This would update the colored overlays showing room availability
        runOnUiThread(() -> {
            // Update UI elements with real-time occupancy data
            // TODO: Update map overlays with current occupancy
        });
    }
    
    // Room occupancy data class
    private static class RoomOccupancy {
        private final String name;
        private final int currentOccupancy;
        private final int maxCapacity;
        private final String roomType;
        
        public RoomOccupancy(String name, int currentOccupancy, int maxCapacity, String roomType) {
            this.name = name;
            this.currentOccupancy = currentOccupancy;
            this.maxCapacity = maxCapacity;
            this.roomType = roomType;
        }
        
        public String getName() { return name; }
        public int getCurrentOccupancy() { return currentOccupancy; }
        public int getMaxCapacity() { return maxCapacity; }
        public String getRoomType() { return roomType; }
        public double getOccupancyPercentage() { 
            return (double) currentOccupancy / maxCapacity * 100; 
        }
    }
}