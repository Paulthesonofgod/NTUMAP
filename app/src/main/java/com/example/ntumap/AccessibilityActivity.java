package com.example.ntumap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccessibilityActivity extends AppCompatActivity {
    
    private CheckBox voiceGuidanceCheckBox;
    private CheckBox highContrastCheckBox;
    private CheckBox screenReaderCheckBox;
    private CheckBox largeTextCheckBox;
    private CheckBox vibrationFeedbackCheckBox;
    private SeekBar textSizeSeekBar;
    private TextView textSizePreview;
    private Button saveButton;
    private Button resetButton;
    
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);
        
        // Initialize SharedPreferences
        preferences = getSharedPreferences("AccessibilitySettings", MODE_PRIVATE);
        
        // Initialize UI
        initializeViews();
        setupClickListeners();
        loadCurrentSettings();
    }
    
    private void initializeViews() {
        voiceGuidanceCheckBox = findViewById(R.id.voiceGuidanceCheckBox);
        highContrastCheckBox = findViewById(R.id.highContrastCheckBox);
        screenReaderCheckBox = findViewById(R.id.screenReaderCheckBox);
        largeTextCheckBox = findViewById(R.id.largeTextCheckBox);
        vibrationFeedbackCheckBox = findViewById(R.id.vibrationFeedbackCheckBox);
        textSizeSeekBar = findViewById(R.id.textSizeSeekBar);
        textSizePreview = findViewById(R.id.textSizePreview);
        saveButton = findViewById(R.id.saveButton);
        resetButton = findViewById(R.id.resetButton);
    }
    
    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveSettings());
        resetButton.setOnClickListener(v -> resetToDefaults());
        
        // Text size seekbar listener
        textSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextSizePreview(progress);
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Checkbox listeners for immediate feedback
        voiceGuidanceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Voice guidance enabled", Toast.LENGTH_SHORT).show();
            }
        });
        
        highContrastCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "High contrast mode enabled", Toast.LENGTH_SHORT).show();
                applyHighContrastMode();
            } else {
                applyNormalMode();
            }
        });
        
        screenReaderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Screen reader support enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadCurrentSettings() {
        // Load saved settings from SharedPreferences
        voiceGuidanceCheckBox.setChecked(preferences.getBoolean("voice_guidance", false));
        highContrastCheckBox.setChecked(preferences.getBoolean("high_contrast", false));
        screenReaderCheckBox.setChecked(preferences.getBoolean("screen_reader", false));
        largeTextCheckBox.setChecked(preferences.getBoolean("large_text", false));
        vibrationFeedbackCheckBox.setChecked(preferences.getBoolean("vibration_feedback", false));
        
        int textSize = preferences.getInt("text_size", 16);
        textSizeSeekBar.setProgress(textSize - 12); // Min size is 12, max is 24
        updateTextSizePreview(textSize - 12);
    }
    
    private void updateTextSizePreview(int progress) {
        int textSize = 12 + progress; // Range from 12 to 24
        textSizePreview.setText("Preview Text Size: " + textSize + "sp");
        textSizePreview.setTextSize(textSize);
    }
    
    private void applyHighContrastMode() {
        // Apply high contrast colors
        findViewById(android.R.id.content).setBackgroundColor(android.graphics.Color.BLACK);
        // In a real app, this would change all text colors to white and backgrounds to black
    }
    
    private void applyNormalMode() {
        // Apply normal colors
        findViewById(android.R.id.content).setBackgroundColor(android.graphics.Color.WHITE);
        // In a real app, this would restore normal color scheme
    }
    
    private void saveSettings() {
        // Save all settings to SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        
        editor.putBoolean("voice_guidance", voiceGuidanceCheckBox.isChecked());
        editor.putBoolean("high_contrast", highContrastCheckBox.isChecked());
        editor.putBoolean("screen_reader", screenReaderCheckBox.isChecked());
        editor.putBoolean("large_text", largeTextCheckBox.isChecked());
        editor.putBoolean("vibration_feedback", vibrationFeedbackCheckBox.isChecked());
        
        int textSize = 12 + textSizeSeekBar.getProgress();
        editor.putInt("text_size", textSize);
        
        editor.apply();
        
        Toast.makeText(this, "Accessibility settings saved successfully!", Toast.LENGTH_SHORT).show();
        
        // Apply settings immediately
        applyAccessibilitySettings();
    }
    
    private void resetToDefaults() {
        // Reset all settings to default values
        voiceGuidanceCheckBox.setChecked(false);
        highContrastCheckBox.setChecked(false);
        screenReaderCheckBox.setChecked(false);
        largeTextCheckBox.setChecked(false);
        vibrationFeedbackCheckBox.setChecked(false);
        textSizeSeekBar.setProgress(4); // Default to 16sp (12 + 4)
        updateTextSizePreview(4);
        
        Toast.makeText(this, "Settings reset to defaults", Toast.LENGTH_SHORT).show();
    }
    
    private void applyAccessibilitySettings() {
        // Apply the saved accessibility settings throughout the app
        boolean voiceGuidance = preferences.getBoolean("voice_guidance", false);
        boolean highContrast = preferences.getBoolean("high_contrast", false);
        boolean screenReader = preferences.getBoolean("screen_reader", false);
        boolean largeText = preferences.getBoolean("large_text", false);
        boolean vibrationFeedback = preferences.getBoolean("vibration_feedback", false);
        int textSize = preferences.getInt("text_size", 16);
        
        // In a real app, this would:
        // - Enable/disable voice guidance for navigation
        // - Apply high contrast theme
        // - Enable screen reader announcements
        // - Increase text size throughout the app
        // - Enable vibration feedback for interactions
        
        StringBuilder appliedSettings = new StringBuilder("Applied settings:\n");
        if (voiceGuidance) appliedSettings.append("• Voice guidance\n");
        if (highContrast) appliedSettings.append("• High contrast mode\n");
        if (screenReader) appliedSettings.append("• Screen reader support\n");
        if (largeText) appliedSettings.append("• Large text mode\n");
        if (vibrationFeedback) appliedSettings.append("• Vibration feedback\n");
        appliedSettings.append("• Text size: ").append(textSize).append("sp");
        
        Toast.makeText(this, appliedSettings.toString(), Toast.LENGTH_LONG).show();
    }
    
    // Accessibility helper methods
    public static boolean isVoiceGuidanceEnabled(SharedPreferences preferences) {
        return preferences.getBoolean("voice_guidance", false);
    }
    
    public static boolean isHighContrastEnabled(SharedPreferences preferences) {
        return preferences.getBoolean("high_contrast", false);
    }
    
    public static boolean isScreenReaderEnabled(SharedPreferences preferences) {
        return preferences.getBoolean("screen_reader", false);
    }
    
    public static boolean isLargeTextEnabled(SharedPreferences preferences) {
        return preferences.getBoolean("large_text", false);
    }
    
    public static boolean isVibrationFeedbackEnabled(SharedPreferences preferences) {
        return preferences.getBoolean("vibration_feedback", false);
    }
    
    public static int getTextSize(SharedPreferences preferences) {
        return preferences.getInt("text_size", 16);
    }
} 