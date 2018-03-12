package com.kvteam.deliverytracker.core.ui.autocomplete

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.kvteam.deliverytracker.core.R
import com.kvteam.deliverytracker.core.models.ModelBase


class AutocompleteListAdapter <out T : ModelBase> (
        private val ctx: Context,
        private val getListFunc: (String) -> MutableList<T>,
        private val mainLineCaptionFunc: (T) -> String,
        private val secondLineCaptionFunc: ((T) -> String)? = null,
        private val limit: Int = 10)
    : BaseAdapter(), Filterable {

    private val filter = object: Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            results.values = getListFunc(constraint.toString())
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val values = results?.values
            if (results != null
                    && values is MutableList<*>) {
                list.clear()
                for (item in values) {
                    @Suppress("UNCHECKED_CAST")
                    list.add(item as T)
                }
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

    }

    private val list = mutableListOf<T>()


    override fun getItem(position: Int): T = list[position]

    override fun getItemId(position: Int): Long = list[position].id?.hashCode()?.toLong() ?: 0L

    override fun getCount(): Int = list.count()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = if (convertView == null) {
            val inflater = LayoutInflater.from(ctx)
            inflater.inflate(R.layout.autocomplete_list_item, parent, false)
        } else {
            convertView
        }
        val item = getItem(position)
        (view.findViewById(R.id.tvMainLine) as TextView).text = mainLineCaptionFunc(item)
        val slcf = secondLineCaptionFunc
        if(slcf != null){
            (view.findViewById(R.id.tvSecondLine) as TextView).text = slcf(item)
        }
        return view
    }

    override fun getFilter() = filter

}