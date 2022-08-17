package com.android.example.alum0808

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.example.alum0808.DBHelper
import com.android.example.alum0808.R

class CreateAlum: AppCompatActivity() {

        companion object{
         private const val TABLE_NAME="alums"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_alum)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val helper = DBHelper(this)
        val textTitle = findViewById<EditText>(R.id.etnote)
        val alumId: Long = intent.getLongExtra("id",0)
        if (alumId != 0L) {
            helper.readableDatabase.use {
                    db -> db.query(TABLE_NAME, arrayOf("id", "title"), "id = ?", arrayOf(alumId.toString()), null, null, null, "1")
                .use { cursor ->
                    if (cursor.moveToFirst()) {
                        textTitle.setText(cursor.getString(1))
                    }
                }
            }
        }

        findViewById<Button>(R.id.create_alum_button).setOnClickListener{
            helper.writableDatabase.use {
                    db ->
                val values = ContentValues().apply {
                    put("title", textTitle.text.toString())
                }
                if (alumId != 0L) {
                    db.update(TABLE_NAME, values,"id = ?", arrayOf(alumId.toString()))
                } else {
                    db.insert(TABLE_NAME,null, values)
                }
            }
            finish()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            android.R.id.home->{
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
