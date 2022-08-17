package com.android.example.alum0808

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomListAdapter(private val context: Context, private val data: List<ListItem>, private val resource: Int): BaseAdapter() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): ListItem {
        return data[p0]
    }

    override fun getItemId(p0: Int): Long {
        return data[p0].id
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val item = getItem(p0)
        val view = p1 ?: inflater.inflate(resource, null)
        view.findViewById<TextView>(R.id.listView).text = item.title
        return view
    }
}