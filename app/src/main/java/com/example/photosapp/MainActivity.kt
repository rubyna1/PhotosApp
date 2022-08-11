package com.example.photosapp

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photosapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ItemImagePickerAdapter.OnItemCallback {
    lateinit var binding: ActivityMainBinding
    var imageData = mutableListOf<ImageModel>()
    var allMedia = ArrayList<ImageModel>()
    var selectedImage = mutableListOf<ImageModel>()
    private lateinit var adapter: ItemAdapter
    lateinit var itemPickerAdapter: ItemImagePickerAdapter
    private var imagePickerDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
    }

    private fun initialize() {
        adapter = ItemAdapter(imageData)
        binding.activityMainRecyclerView.layoutManager =
            GridLayoutManager(this, 2)
        binding.activityMainRecyclerView.adapter = adapter
        binding.activityMainSelectTextView.setOnClickListener {
            selectedImage.clear()
            imageData.clear()
            if (checkReadStoragePermission()) {
                getImages()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 10)
            }
        }
        binding.activityMainEditText.setOnEditorActionListener { _, actionID, _ ->
            if (actionID == EditorInfo.IME_ACTION_DONE) {
                hideSoftKeyboard()
                binding.activityMainEditText.clearFocus()
                if (selectedImage.isNotEmpty())
                    sequence()
            }
            false
        }
    }

    private fun hideSoftKeyboard() {
        try {
            val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (checkReadStoragePermission()) {
                getImages()
            } else {
                Toast.makeText(this, "Require Read Permission. Go to settings", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sequence() {
        imageData.clear()
        val n = binding.activityMainEditText.text.toString().toInt()
        val sequence = mutableListOf<Int>()
        for (j in 1 until n + 1) {
            var a: Int?
            a = j * (j + 1) / 2
            sequence.add(a)
        }
        for (i in 1 until n + 1) {
            if (sequence.contains(i)) {
                imageData.add(ImageModel(selectedImage[0].image, i))
            } else {
                imageData.add(ImageModel(selectedImage[1].image, i))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun checkReadStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getImages() {
        allMedia.clear()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA)
        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        this.contentResolver.query(uri, projection, null, null, "$orderBy DESC")?.use {
            while (it.moveToNext()) {
                val columnIndexData = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val absolutePathOfImage = it.getString(columnIndexData)
                allMedia.add(ImageModel(absolutePathOfImage, 1))
            }
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val dialogLayout = layoutInflater.inflate(R.layout.item_image_picker_dialog, null)
        val recyclerView =
            dialogLayout.findViewById<RecyclerView>(R.id.item_image_picker_recycler_view)
        val cancelTextView =
            dialogLayout.findViewById<TextView>(R.id.image_picker_cancel_text_view)
        val doneTextView =
            dialogLayout.findViewById<TextView>(R.id.image_picker_done_text_view)
        itemPickerAdapter =
            ItemImagePickerAdapter(allMedia, this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = itemPickerAdapter
        recyclerView.setItemViewCacheSize(allMedia.size)
        val alertBuilder = AlertDialog.Builder(this, R.style.alertDialog)
        alertBuilder.setView(dialogLayout)
        imagePickerDialog = alertBuilder.create()
        imagePickerDialog?.show()
        cancelTextView.setOnClickListener {
            imagePickerDialog?.hide()
        }
        doneTextView.setOnClickListener {
            if (selectedImage.size < 2) {
                Toast.makeText(this, "Please select two images", Toast.LENGTH_SHORT).show()
            } else {
                imagePickerDialog?.hide()
                sequence()
            }
        }
    }

    override fun onItemSelected(images: List<ImageModel>) {
        selectedImage.clear()
        selectedImage.addAll(images)
    }
}