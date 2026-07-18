package com.example.mypicture_art

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * The main activity of the application, responsible for handling UI interactions
 * and orchestrating the drawing functionality.
 */
class MainActivity : AppCompatActivity() {

    // Member variables for UI components and the custom drawing view
    private lateinit var drawingView: DrawingView
    private lateinit var brushButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var saveButton: ImageButton
    private lateinit var colorPickerButton: ImageButton

    // Activity result launcher to open the gallery and receive an image URI
    private val openGalleryLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                // This is a placeholder for actual image handling logic
                // (e.g., setting the selected image as a background for the DrawingView)
                Toast.makeText(this, "Image selected: $it", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables edge-to-edge display to utilize the full screen space behind system bars
        enableEdgeToEdge()
        
        // Loads the main UI layout defined in XML
        setContentView(R.layout.activity_main)
        
        // Adjusts view padding to prevent UI elements from being hidden under status or navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components by finding them in the layout via their IDs
        drawingView = findViewById(R.id.drawing_view)
        brushButton = findViewById(R.id.brush_button)
        galleryButton = findViewById(R.id.gallery_button)
        undoButton = findViewById(R.id.undo_button)
        saveButton = findViewById(R.id.save_button)
        colorPickerButton = findViewById(R.id.color_picker_button)
        
        // Sets a default brush size to start drawing immediately
        drawingView.changeBurshSize(23f)

        // Opens the brush size selection dialog when the brush button is clicked
        brushButton.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        // Launches the gallery picker when the gallery button is clicked
        galleryButton.setOnClickListener {
            openGalleryLauncher.launch("image/*")
        }

        // Triggers the undo logic in DrawingView when the undo button is clicked
        undoButton.setOnClickListener {
            drawingView.onClickUndo()
        }

        // Shows a toast for the save functionality (to be implemented)
        saveButton.setOnClickListener {
            Toast.makeText(this, "Save functionality coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Opens the color picker dialog when the color picker button is clicked
        colorPickerButton.setOnClickListener {
            showColorPickerDialog()
        }
    }

    /**
     * Placeholder function to show a color selection dialog.
     */
    private fun showColorPickerDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_brush) 
        dialog.setTitle("Select Color")
        
        // For demonstration, we simply set the color to Red and close the dialog
        drawingView.setColor(Color.RED) 
        dialog.dismiss()
        Toast.makeText(this, "Color set to Red", Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays a dialog containing a SeekBar to let users choose the brush thickness.
     */
    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush)
        brushDialog.setTitle("Brush Size: ")

        val seekBarProgress = brushDialog.findViewById<SeekBar>(R.id.dialog_seek_bar)
        val progressTextView = brushDialog.findViewById<TextView>(R.id.dialog_text_view_progress)

        // Listener to update the brush size in real-time as the user slides the SeekBar
        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                progressTextView.text = progress.toString()
                drawingView.changeBurshSize(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Displays the constructed dialog
        brushDialog.show()
    }
}
