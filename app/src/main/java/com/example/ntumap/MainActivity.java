package com.example.ntumap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
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
    
    // NTU Clifton Campus coordinates
    private static final LatLng NTU_CLIFTON = new LatLng(52.9068, -1.1878);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
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
    }
    
    private void addRoomMarkers() {
        // Add markers for different buildings/rooms on campus
        LatLng[] roomLocations = {
            new LatLng(52.9068, -1.1878), // Main building
            new LatLng(52.9070, -1.1880), // Library
            new LatLng(52.9066, -1.1876), // Computer lab
            new LatLng(52.9072, -1.1882), // Cafeteria
            new LatLng(52.9064, -1.1874), // Gym
        };
        
        String[] roomNames = {"Main Building", "Library", "Computer Lab", "Cafeteria", "Gym"};
        
        for (int i = 0; i < roomLocations.length; i++) {
            RoomOccupancy occupancy = roomOccupancyMap.get(roomNames[i]);
            if (occupancy != null) {
                String snippet = String.format("Occupancy: %d/%d (%s)", 
                    occupancy.getCurrentOccupancy(), 
                    occupancy.getMaxCapacity(),
                    occupancy.getRoomType());
                
                mMap.addMarker(new MarkerOptions()
                        .position(roomLocations[i])
                        .title(roomNames[i])
                        .snippet(snippet));
            }
        }
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
        // TODO: Implement location search with Google Places API
        Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
        
        // For now, just show a simple search result
        if (query.toLowerCase().contains("room") || query.toLowerCase().contains("classroom")) {
            Intent intent = new Intent(this, RoomBookingActivity.class);
            intent.putExtra("search_query", query);
            startActivity(intent);
        } else {
            // Start navigation
            Intent intent = new Intent(this, NavigationActivity.class);
            intent.putExtra("destination", query);
            startActivity(intent);
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
        // TODO: Implement filter dialog for room types
        Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show();
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
        private String name;
        private int currentOccupancy;
        private int maxCapacity;
        private String roomType;
        
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