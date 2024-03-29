package com.example.to_do_dles.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_dles.R
import com.example.to_do_dles.databinding.ListItemBinding
import com.example.to_do_dles.enums.FilterType
import com.example.to_do_dles.model.TaskList
import com.example.to_do_dles.view.HomeFragment

class ListAdapter(private val dataSet: ArrayList<TaskList>, val fragment: HomeFragment): RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(private var binding:ListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(list:TaskList, fragment: HomeFragment, context: Context){
            val all = context.getString(R.string.all)
            val newList = context.getString(R.string.new_list)
            when (list.name) {
                "All" -> {
                    binding.listText.text = all
                }
                "New List" -> {
                    binding.listText.text = newList
                    binding.listText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_18, 0, 0, 0)
                }
                else -> {
                    binding.listText.text = list.name
                    binding.listText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
            binding.colorCard.strokeColor = ContextCompat.getColor(context, list.color)
            binding.colorCard.strokeWidth = 6
            binding.root.setOnClickListener {
                val menu = binding.longclickMenu
                if (menu.visibility == View.GONE){
                    fragment.clearAllListSelections()
                }
                fragment.closeFilters()
                fragment.clearAllSelections()
                fragment.minimizeExpandedRows()
                if(binding.listText.text != context.getString(R.string.new_list)){
                    binding.colorCard.strokeWidth = 8
                    var listText = binding.listText.text.toString()
                    if (listText == context.getString(R.string.all)){
                        listText = "All"
                    }
                    fragment.changeCurrentListName(listText)
                    fragment.filterList(listText, FilterType.List)
                }else if(binding.listText.text == newList){
                    fragment.navigateToAddList()
                }
            }

            binding.root.setOnLongClickListener {
                fragment.clearAllListSelections()
                if(binding.listText.text != newList && binding.listText.text != all){
                    val menu = binding.longclickMenu
                    if (menu.visibility == View.GONE){
                        menu.visibility = View.VISIBLE
                    }
                }else if(binding.listText.text == all){
                    Toast.makeText(context, R.string.not_editable, Toast.LENGTH_LONG).show()
                }
                return@setOnLongClickListener true
            }

            binding.closeButton.setOnClickListener {
                binding.longclickMenu.visibility = View.GONE
            }

            binding.editButton.setOnClickListener {
                fragment.moveToEditTaskList(list.uid)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = dataSet[position]
        holder.bind(list, fragment, context)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}