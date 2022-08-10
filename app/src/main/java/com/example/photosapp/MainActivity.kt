package com.example.photosapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photosapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val mArrayUri = mutableListOf<Uri>()
    var imageData = mutableListOf<ImageModel>()
    lateinit var adapter: ItemAdapter
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
            if (checkReadStoragePermission()) {
                Toast.makeText(
                    this,
                    "Tap and hold on the image to enable multiple selection",
                    Toast.LENGTH_LONG
                ).show()
                imageChooser()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 10)
            }
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
                imageChooser()
            } else {
                Toast.makeText(this, "Require Read Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data?.clipData != null) {
                mArrayUri.clear()
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageurl: Uri = data.clipData!!.getItemAt(i).uri
                    mArrayUri.add(imageurl)
                }
                binding.activityMainImageView.setImageURI(mArrayUri[0])
                binding.activityMainImageView1.setImageURI(mArrayUri[1])
                sequence(mArrayUri[0], mArrayUri[1])
            }
        }
    }

    private fun sequence(image1: Uri, image2: Uri) {
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
                imageData.add(ImageModel(image1, i))
            } else {
                imageData.add(ImageModel(image2, i))
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

    private fun imageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }
}