package com.example.sarvin_tia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.sarvin_tia.databinding.ItemGerakanBinding

class GerakanAdapter(
    private val context: Context,
    private val data: List<Gerakan>
) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Gerakan = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemBinding: ItemGerakanBinding
        val view: View

        if (convertView == null) {
            itemBinding = ItemGerakanBinding.inflate(LayoutInflater.from(context), parent, false)
            view = itemBinding.root
            view.tag = itemBinding
        } else {
            view = convertView
            itemBinding = convertView.tag as ItemGerakanBinding
        }

        val item = data[position]
        itemBinding.imgGerakanItem.setImageResource(item.gambar)
        itemBinding.tvNamaGerakan.text = item.nama
        itemBinding.tvDetailGerakan.text = item.detail
        itemBinding.tvXpGerakan.text = "+${item.xp} XP"
        return view
    }
}