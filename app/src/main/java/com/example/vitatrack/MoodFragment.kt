package com.example.vitatrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vitatrack.data.MoodEntry
import com.example.vitatrack.viewmodel.MoodViewModel

class MoodFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputNote: EditText
    private lateinit var btnSaveMood: Button
    private lateinit var adapter: MoodAdapter
    private var selectedEmoji: String? = null
    
    private val viewModel: MoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mood, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerMoodHistory)
        inputNote = view.findViewById(R.id.inputNote)
        btnSaveMood = view.findViewById(R.id.btnSaveMood)

        // Emoji buttons
        val btnHappy = view.findViewById<Button>(R.id.btnHappy)
        val btnNeutral = view.findViewById<Button>(R.id.btnNeutral)
        val btnSad = view.findViewById<Button>(R.id.btnSad)
        val btnAngry = view.findViewById<Button>(R.id.btnAngry)
        val btnSleepy = view.findViewById<Button>(R.id.btnSleepy)

        // Handle emoji selection
        val emojiButtons = listOf(btnHappy, btnNeutral, btnSad, btnAngry, btnSleepy)
        for (button in emojiButtons) {
            button.setOnClickListener {
                selectedEmoji = button.text.toString()
                highlightSelectedEmoji(emojiButtons, button)
            }
        }

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MoodAdapter(mutableListOf())
        recyclerView.adapter = adapter

        // Setup observers and click listeners
        setupObservers()
        setupClickListeners()

        return view
    }
    
    private fun setupObservers() {
        // Observe mood entries
        viewModel.moodEntries.observe(viewLifecycleOwner, Observer { entries ->
            adapter.updateMoodEntries(entries)
        })
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // You can show/hide loading indicator here
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        })
    }
    
    private fun setupClickListeners() {
        btnSaveMood.setOnClickListener {
            saveMood()
        }
    }

    // Highlight the selected emoji with better visual feedback
    private fun highlightSelectedEmoji(allButtons: List<Button>, selected: Button) {
        for (btn in allButtons) {
            if (btn == selected) {
                btn.alpha = 1.0f
                btn.elevation = 12f
                btn.scaleX = 1.1f
                btn.scaleY = 1.1f
            } else {
                btn.alpha = 0.5f
                btn.elevation = 4f
                btn.scaleX = 1.0f
                btn.scaleY = 1.0f
            }
        }
    }

    // Save mood entry
    private fun saveMood() {
        if (selectedEmoji == null) {
            Toast.makeText(requireContext(), "Please select a mood emoji", Toast.LENGTH_SHORT).show()
            return
        }

        val note = inputNote.text.toString().trim()
        
        viewModel.addMoodEntry(selectedEmoji!!, note)
        
        // Clear input and reset selection
        inputNote.text.clear()
        selectedEmoji = null
        
        // Reset emoji button highlights to default state
        val btnHappy = view?.findViewById<Button>(R.id.btnHappy)
        val btnNeutral = view?.findViewById<Button>(R.id.btnNeutral)
        val btnSad = view?.findViewById<Button>(R.id.btnSad)
        val btnAngry = view?.findViewById<Button>(R.id.btnAngry)
        val btnSleepy = view?.findViewById<Button>(R.id.btnSleepy)
        val emojiButtons = listOfNotNull(btnHappy, btnNeutral, btnSad, btnAngry, btnSleepy)
        for (btn in emojiButtons) {
            btn.alpha = 1.0f
            btn.elevation = 4f
            btn.scaleX = 1.0f
            btn.scaleY = 1.0f
        }
        
        Toast.makeText(requireContext(), "Mood saved!", Toast.LENGTH_SHORT).show()
    }
}
