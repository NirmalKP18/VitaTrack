package com.example.vitatrack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.vitatrack.utils.DemoDataSeeder

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var btnChangePhoto: Button
    private lateinit var btnSaveChanges: Button
    private lateinit var btnDemoData: Button
    private lateinit var btnLogout: Button
    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private val PREFS_NAME = "user_prefs"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        usernameInput = view.findViewById(R.id.usernameInput)
        emailInput = view.findViewById(R.id.emailInput)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges)
        btnDemoData = view.findViewById(R.id.btnDemoData)
        btnLogout = view.findViewById(R.id.btnLogout)

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        usernameInput.setText(prefs.getString("username", "Guest User"))
        emailInput.setText(prefs.getString("email", "Not available"))

        val savedUri = prefs.getString("profile_uri", null)
        if (savedUri != null) profileImage.setImageURI(Uri.parse(savedUri))

        // Change photo
        btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Save profile updates
        btnSaveChanges.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()

            val editor = prefs.edit()
            editor.putString("username", username)
            editor.putString("email", email)
            if (selectedImageUri != null)
                editor.putString("profile_uri", selectedImageUri.toString())
            editor.apply()

            Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
        }

        // Demo Data
        btnDemoData.setOnClickListener {
            val seeder = DemoDataSeeder(requireContext())
            seeder.seedDemoData()
            Toast.makeText(requireContext(), "Demo data loaded! Check other tabs to see the data.", Toast.LENGTH_LONG).show()
        }

        // Logout
        btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImage.setImageURI(selectedImageUri)
        }
    }
}
