package com.android.example.alum0808

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class PhotoView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.photo_view)
        val intent = intent
        val uri: Uri? = intent.getParcelableExtra("imageUri")
        val image = findViewById<ImageView>(R.id.photo_image)
        image.setImageURI(uri)
        Picasso.get().load(uri).into(image)
    }
}