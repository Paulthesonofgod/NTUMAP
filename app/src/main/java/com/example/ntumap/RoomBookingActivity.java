package com.example.ntumap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class RoomBookingActivity extends AppCompatActivity {
    
    private String searchQuery;
    private TextView searchResultsText;
    private ListView roomListView;
    private Button filterButton;
    private Button accessibilityButton;
    
    private List<Room> availableRooms;
    private ArrayAdapter<Room> roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_booking);
        
        // Get search query from intent
        Intent intent = getIntent();
        searchQuery = intent.getStringExtra("search_query");
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        
        // Load available rooms
        loadAvailableRooms();
    }
    
    private void initializeViews() {
        searchResultsText = findViewById(R.id.searchResultsText);
        roomListView = findViewById(R.id.roomListView);
        filterButton = findViewById(R.id.filterButton);
        accessibilityButton = findViewById(R.id.accessibilityButton);
        
        // Set search results text
        if (searchQuery != null) {
            searchResultsText.setText("Available rooms for: " + searchQuery);
        }
    }
    
    private void setupClickListeners() {
        filterButton.setOnClickListener(v -> showFilterDialog());
        accessibilityButton.setOnClickListener(v -> enableAccessibilityMode());
        
        roomListView.setOnItemClickListener((parent, view, position, id) -> {
            Room selectedRoom = availableRooms.get(position);
            showRoomDetails(selectedRoom);
        });
    }
    
    private void loadAvailableRooms() {
        availableRooms = new ArrayList<>();
        
        // Simulate real-time room availability data
        // In a real app, this would come from a backend API
        availableRooms.add(new Room("Study Room A", "Library - Floor 1", 15, 8, "Study Space", "Quiet study area with individual desks"));
        availableRooms.add(new Room("Computer Lab B", "Technology Building - Floor 2", 25, 12, "Computer Lab", "Windows and Mac computers available"));
        availableRooms.add(new Room("Group Study Room C", "Library - Floor 2", 20, 15, "Group Study", "Large table for group projects"));
        availableRooms.add(new Room("Silent Study Room D", "Library - Floor 3", 10, 3, "Silent Study", "Completely silent study environment"));
        availableRooms.add(new Room("Presentation Room E", "Main Building - Floor 1", 30, 0, "Presentation", "Projector and whiteboard available"));
        availableRooms.add(new Room("Meeting Room F", "Business School - Floor 2", 12, 5, "Meeting", "Professional meeting space"));
        
        // Create custom adapter
        roomAdapter = new RoomArrayAdapter(this, availableRooms);
        roomListView.setAdapter(roomAdapter);
    }
    
    private void showFilterDialog() {
        // TODO: Implement filter dialog for room types, capacity, etc.
        Toast.makeText(this, "Filter options: Study Space, Computer Lab, Group Study, Silent Study, Presentation, Meeting", 
                      Toast.LENGTH_LONG).show();
    }
    
    private void enableAccessibilityMode() {
        // Enable accessibility features
        Toast.makeText(this, "Accessibility mode enabled. Larger text and voice guidance activated.", 
                      Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // - Increase text size
        // - Enable screen reader support
        // - Add voice guidance for room selection
    }
    
    private void showRoomDetails(Room room) {
        // Show detailed room information and booking options
        StringBuilder details = new StringBuilder();
        details.append("Room: ").append(room.getName()).append("\n");
        details.append("Location: ").append(room.getLocation()).append("\n");
        details.append("Capacity: ").append(room.getCurrentOccupancy()).append("/").append(room.getMaxCapacity()).append("\n");
        details.append("Type: ").append(room.getType()).append("\n");
        details.append("Description: ").append(room.getDescription()).append("\n\n");
        
        if (room.getCurrentOccupancy() < room.getMaxCapacity()) {
            details.append("Available for booking!");
            showBookingDialog(room);
        } else {
            details.append("Room is currently full.");
        }
        
        Toast.makeText(this, details.toString(), Toast.LENGTH_LONG).show();
    }
    
    private void showBookingDialog(Room room) {
        // Show booking confirmation dialog
        new android.app.AlertDialog.Builder(this)
                .setTitle("Book Room")
                .setMessage("Would you like to book " + room.getName() + "?")
                .setPositiveButton("Book Now", (dialog, which) -> {
                    bookRoom(room);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void bookRoom(Room room) {
        // Simulate room booking process
        Toast.makeText(this, "Booking " + room.getName() + "...", Toast.LENGTH_SHORT).show();
        
        // In a real app, this would:
        // - Send booking request to backend
        // - Update room occupancy
        // - Send confirmation email
        // - Add to user's schedule
        
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate booking process
                runOnUiThread(() -> {
                    Toast.makeText(RoomBookingActivity.this, 
                                  "Successfully booked " + room.getName() + "! Check your email for confirmation.", 
                                  Toast.LENGTH_LONG).show();
                    
                    // Update room occupancy
                    room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
                    roomAdapter.notifyDataSetChanged();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // Room data class
    public static class Room {
        private String name;
        private String location;
        private int maxCapacity;
        private int currentOccupancy;
        private String type;
        private String description;
        
        public Room(String name, String location, int maxCapacity, int currentOccupancy, String type, String description) {
            this.name = name;
            this.location = location;
            this.maxCapacity = maxCapacity;
            this.currentOccupancy = currentOccupancy;
            this.type = type;
            this.description = description;
        }
        
        // Getters
        public String getName() { return name; }
        public String getLocation() { return location; }
        public int getMaxCapacity() { return maxCapacity; }
        public int getCurrentOccupancy() { return currentOccupancy; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        
        // Setters
        public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }
        
        public double getOccupancyPercentage() {
            return (double) currentOccupancy / maxCapacity * 100;
        }
        
        public boolean isAvailable() {
            return currentOccupancy < maxCapacity;
        }
        
        @Override
        public String toString() {
            return String.format("%s (%d/%d) - %s", name, currentOccupancy, maxCapacity, type);
        }
    }
    
    // Custom adapter for room list
    private static class RoomArrayAdapter extends ArrayAdapter<Room> {
        private final List<Room> rooms;
        
        public RoomArrayAdapter(RoomBookingActivity context, List<Room> rooms) {
            super(context, android.R.layout.simple_list_item_1, rooms);
            this.rooms = rooms;
        }
        
        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            
            Room room = rooms.get(position);
            textView.setText(room.toString());
            
            // Color code based on availability
            if (room.isAvailable()) {
                textView.setTextColor(android.graphics.Color.GREEN);
            } else {
                textView.setTextColor(android.graphics.Color.RED);
            }
            
            return view;
        }
    }
} 