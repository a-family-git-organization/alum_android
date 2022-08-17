package com.android.example.alum0808
import android.app.LauncherActivity
import android.content.Intent
import android.graphics.Insets.add
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

import com.android.example.alum0808.databinding.ActivityMainBinding
import com.android.example.alum0808.databinding.GalleryViewBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val add = viewBinding.add
        val listView = viewBinding.listView
        setListViewAdapter(listView)


        add.setOnClickListener {
            val intent = Intent(this, CreateAlum::class.java)
            startActivity(intent)
        }
        listView.setOnItemClickListener { av, view, position, id ->
            val intent = Intent(this, GalleryView::class.java)
            val itemId = listView.adapter.getItemId(position)
            intent.putExtra("id", itemId)
            startActivity(intent)
        }
    }
    override fun onResume() {
        super.onResume()
        val helper = DBHelper(this)
        val listView = viewBinding.listView
        setListViewAdapter(listView)
    }
    fun setListViewAdapter(listView: ListView)
    {
        val helper = DBHelper(this)
        helper.readableDatabase.use {
                db -> db.query("alums", arrayOf("id", "title"),null,null,null,null,null,null)
            .use { cursor ->
                val alumList = mutableListOf<ListItem>()
                if (cursor.moveToFirst()) {
                    for (i in 1..cursor.count) {
                        val alumId = cursor.getInt(0)
                        val title = cursor.getString(1)
                        alumList.add(ListItem(alumId.toLong(),title))
                        cursor.moveToNext()
                    }
                }
                listView.adapter = CustomListAdapter(this, alumList, R.layout.list_item)
            }
        }
    }
}
