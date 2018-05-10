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
import com.kvteam.deliverytracker.core.common.EMPTY_STRING
import com.kvteam.deliverytracker.core.models.ModelBase
import java.lang.ref.WeakReference
import kotlin.math.min


open class AutocompleteListAdapter <T : ModelBase> (
        private val ctx: Context,
        private val getListFunc: (String) -> MutableList<T>,
        private val mainLineCaptionFunc: (T) -> String = { EMPTY_STRING },
        private val limit: Int = 5)
    : BaseAdapter(), Filterable {
    class AutocompleteFilter<out T : ModelBase>(
            adapter: AutocompleteListAdapter<T>) : Filter() {
        private val adapterRef = WeakReference(adapter)

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (constraint == null) {
                return results
            }
            val adapter = adapterRef.get() ?: return results
            val items = adapter.getListFunc(constraint.toString())
            results.values = items.subList(0, min(adapter.limit, items.size))
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val adapter = adapterRef.get() ?: return
            val values = results?.values as? MutableList<*>
            if (values != null && values.size > 0) {
                adapter.list.clear()
                for (item in values) {
                    @Suppress("UNCHECKED_CAST")
                    adapter.list.add(item as T)
                }
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyDataSetInvalidated()
            }
        }
    }

    private val autocompleteFilter: AutocompleteFilter<T> by lazy { AutocompleteFilter(this) }

    private val list = mutableListOf<T>()

    protected open val viewLayoutId = R.layout.autocomplete_list_item

    protected open fun updateView(position: Int, item: T, view: View, parent: ViewGroup?) {
        (view.findViewById(R.id.tvMainLine) as TextView).text = mainLineCaptionFunc(item)
    }

    override fun getItem(position: Int): T = list[position]

    override fun getItemId(position: Int): Long = list[position].id?.hashCode()?.toLong() ?: 0L

    override fun getCount(): Int = list.count()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = if (convertView == null) {
            val inflater = LayoutInflater.from(ctx)
            inflater.inflate(viewLayoutId, parent, false)
        } else {
            convertView
        }
        val item = getItem(position)
        updateView(position, item, view, parent)
        return view
    }

    override fun getFilter() = autocompleteFilter

}