package com.example.vitatrack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.vitatrack.viewmodel.ProfileViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {

    private lateinit var profileImage: ImageView
    private lateinit var usernameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var bioInput: TextInputEditText
    private lateinit var btnChangePhoto: FloatingActionButton
    private lateinit var btnSaveChanges: Button
    private lateinit var btnLogout: Button
    private var selectedImageUri: Uri? = null
    
    private val viewModel: ProfileViewModel by viewModels()

    // Modern image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Take persistable URI permission
                try {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: SecurityException) {
                    // Some URIs don't support persistable permissions
                    e.printStackTrace()
                }
                
                selectedImageUri = uri
                loadProfileImage(uri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        profileImage = view.findViewById(R.id.profileImage)
        usernameInput = view.findViewById(R.id.usernameInput)
        emailInput = view.findViewById(R.id.emailInput)
        bioInput = view.findViewById(R.id.bioInput)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges)
        btnLogout = view.findViewById(R.id.btnLogout)

        // Initialize user settings if not exists
        viewModel.initializeUserSettings()
        
        setupObservers()
        setupClickListeners()

        return view
    }
    
    private fun setupObservers() {
        // Observe user settings
        viewModel.userSettings.observe(viewLifecycleOwner, Observer { settings ->
            settings?.let {
                usernameInput.setText(it.username)
                emailInput.setText(it.email)
                bioInput.setText(it.bio)
                
                if (it.profileImageUri.isNotEmpty()) {
                    loadProfileImage(it.profileImageUri)
                }
            }
        })
        
        // Observe success messages
        viewModel.successMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        })
    }
    
    private fun setupClickListeners() {
        // Change photo
        btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        // Save profile updates
        btnSaveChanges.setOnClickListener {
            saveProfile()
        }

        // Logout
        btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logged out!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        imagePickerLauncher.launch(intent)
    }
    
    private fun loadProfileImage(uriString: String) {
        try {
            Glide.with(this)
                .load(Uri.parse(uriString))
                .transform(CircleCrop())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(profileImage)
        } catch (e: Exception) {
            e.printStackTrace()
            profileImage.setImageResource(R.mipmap.ic_launcher_round)
        }
    }
    
    private fun saveProfile() {
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val bio = bioInput.text.toString().trim()
        
        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a username", Toast.LENGTH_SHORT).show()
            return
        }
        
        val imageUri = selectedImageUri?.toString() ?: viewModel.userSettings.value?.profileImageUri ?: ""
        
        viewModel.updateProfile(username, email, bio, imageUri)
    }
}
