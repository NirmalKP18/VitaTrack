package com.example.vitatrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputNote: EditText
    private lateinit var btnSaveMood: Button
    private lateinit var adapter: MoodAdapter
    private val moodList = mutableListOf<MoodEntry>()
    private var selectedEmoji: String? = null
    private val prefsName = "mood_data"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ✅ Clean correct syntax (no named args)
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
        adapter = MoodAdapter(moodList)
        recyclerView.adapter = adapter

        // Save button
        btnSaveMood.setOnClickListener {
            saveMood()
        }

        // Load saved moods
        loadMoods()

        return view
    }

    // Highlight the selected emoji
    private fun highlightSelectedEmoji(allButtons: List<Button>, selected: Button) {
        for (btn in allButtons) {
            btn.alpha = if (btn == selected) 1.0f else 0.4f
        }
    }

    // Save mood entry
    private fun saveMood() {
        if (selectedEmoji == null) {
            Toast.makeText(requireContext(), "Please select a mood emoji", Toast.LENGTH_SHORT).show()
            return
        }

        val note = inputNote.text.toString().trim()
        val date = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(Date())

        val newEntry = MoodEntry(selectedEmoji!!, note, date)
        moodList.add(0, newEntry) // add new entry to top
        adapter.notifyItemInserted(0)
        recyclerView.scrollToPosition(0)

        inputNote.text.clear()
        selectedEmoji = null

        saveMoodsToPrefs()
        Toast.makeText(requireContext(), "Mood saved!", Toast.LENGTH_SHORT).show()
    }

    // Save moods to SharedPreferences as JSON
    private fun saveMoodsToPrefs() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(moodList)
        editor.putString("mood_list", json)
        editor.apply()
    }

    // Load saved moods from SharedPreferences
    private fun loadMoods() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val json = prefs.getString("mood_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val savedList: MutableList<MoodEntry> = Gson().fromJson(json, type)
            moodList.clear()
            moodList.addAll(savedList)
            adapter.notifyDataSetChanged()
        }
    }
}
