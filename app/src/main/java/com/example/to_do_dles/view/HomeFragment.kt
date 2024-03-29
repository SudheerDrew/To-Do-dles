package com.example.to_do_dles.view

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_do_dles.MainActivity
import com.example.to_do_dles.R
import com.example.to_do_dles.adapter.HomeAdapter
import com.example.to_do_dles.adapter.ListAdapter
import com.example.to_do_dles.databinding.FragmentHomeBinding
import com.example.to_do_dles.enums.FilterType
import com.example.to_do_dles.model.TaskList
import com.example.to_do_dles.model.ToDo
import com.example.to_do_dles.viewmodel.HomeViewModel
import com.google.android.material.card.MaterialCardView
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homeAdapter: HomeAdapter
    private val listToDo:ArrayList<ToDo> = arrayListOf()
    private  lateinit var searchItem: MenuItem
    private var isFilterOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // to add appbar menu icon
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                searchItem = menu.findItem(R.id.toolbar_search)

                val searchView = searchItem.actionView as SearchView
                val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                val closeButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
                editText.setTextColor(ContextCompat.getColor(requireContext(),R.color.searchview_color))
                editText.hint = context?.getString(R.string.search_here)
                editText.setHintTextColor(Color.GRAY)
                closeButton.setImageResource(R.drawable.ic_search_close)


                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            filterList(newText, FilterType.Query)
                        }
                        return true
                    }

                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.toolbar_search -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        homeViewModel.toDoList.observe(viewLifecycleOwner) {
            listToDo.clear()
            listToDo.addAll(it)
            if(listToDo.isEmpty()){
                showNoDataText()
            }else{
                hideNoDataText()
            }
            filterList(homeViewModel.currentListName, FilterType.List)
        }

        val listList:ArrayList<TaskList> = arrayListOf()
        val listAll = TaskList("All", R.color.list_color_all)
        val listNew = TaskList("New List", R.color.list_color_new)
        homeViewModel.listList.observe(viewLifecycleOwner) {
            listList.clear()
            listList.add(listAll)
            listList.addAll(it)
            listList.add(listNew)
            binding.rvHomeList.adapter?.notifyDataSetChanged()
        }

        homeAdapter = HomeAdapter(listToDo, this@HomeFragment)
        homeAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvHome.adapter = homeAdapter
        val homeLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.layoutManager = homeLayoutManager

        val listAdapter = ListAdapter(listList, this@HomeFragment)
        listAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvHomeList.adapter = listAdapter
        val listLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHomeList.layoutManager = listLayoutManager

        homeViewModel.updateData()

        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToAddFragment()
            findNavController().navigate(action)
        }

        // to hide fab when recyclerview reached at bottom to make last row more visible
        binding.rvHome.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.rvHome.canScrollVertically(1) && binding.rvHome.canScrollVertically(-1)){
                binding.fab.hide()
            }else{
                binding.fab.show()
            }

            if (binding.rvHome.isEmpty() && binding.noDataInclude.noDataLayout.visibility == View.GONE){
                binding.noResultInclude.noResultLayout.visibility = View.VISIBLE
            }else{
                binding.noResultInclude.noResultLayout.visibility = View.INVISIBLE
            }
        }


        //to open filter view
        binding.filterIcon.setOnClickListener {
            isFilterOpen = if(isFilterOpen){
                binding.filterIcon.setImageResource(R.drawable.ic_filter)
                binding.filterChipInclude.filterChipGroup.clearCheck()
                binding.filterChipInclude.filterChipGroup.visibility = View.GONE
                false
            }else{
                binding.filterIcon.setImageResource(R.drawable.ic_filter_off)
                binding.filterChipInclude.filterChipGroup.visibility = View.VISIBLE
                true
            }
        }

        binding.filterChipInclude.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()){
                if (checkedIds[0] == R.id.low_filter){
                    filterList("LOW", FilterType.Priority)
                }else if (checkedIds[0] == R.id.med_filter){
                    filterList("MED", FilterType.Priority)
                }else if (checkedIds[0] == R.id.high_filter){
                    filterList("HIGH", FilterType.Priority)
                }else if (checkedIds[0] == R.id.streak_filter){
                    filterList("STREAK", FilterType.Tracker)
                }
            }else{
                filterList(homeViewModel.currentListName, FilterType.List)
            }
        }

        // to set my..task header from beginning
        val currentList = if (homeViewModel.currentListName == "All"){
            getString(R.string.all)
        } else {
            homeViewModel.currentListName
        }
        binding.myTasksText.text = getString(R.string.my_tasks_format,currentList.lowercase())

        // to set theme when app started
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        val isDarkModeOpen = sharedPreferences.getBoolean("isDarkModeOpen",false)
        if (isDarkModeOpen){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean("isAppOpen",false).apply()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = requireContext().getSharedPreferences("com.ercanpalta.todo",
            AppCompatActivity.MODE_PRIVATE
        )
        sharedPreferences.edit().putBoolean("isAppOpen",true).apply()
    }

    fun showNoDataText(){
        binding.noDataInclude.noDataLayout.visibility = View.VISIBLE
    }

    fun hideNoDataText(){
        binding.noDataInclude.noDataLayout.visibility = View.GONE
    }

    fun changeCurrentListName(listName:String){
        var name = listName
        binding.noResultInclude.noResultLayout.visibility = View.INVISIBLE
        homeViewModel.currentListName = listName
        if (listName == "All"){
            name = context?.getString(R.string.all) ?: "All"
        }
        binding.myTasksText.text = getString(R.string.my_tasks_format,name.lowercase())

        // to close searchview when list changed
        if (searchItem.isActionViewExpanded){
            searchItem.collapseActionView()
        }
    }

    //to close and clear filters
    fun closeFilters(){
        binding.filterIcon.setImageResource(R.drawable.ic_filter)
        binding.filterChipInclude.filterChipGroup.clearCheck()
        binding.filterChipInclude.filterChipGroup.visibility = View.GONE
        isFilterOpen = false
    }

    fun cancelReminder(requestCode:Int){
        (activity as MainActivity).cancelReminder(requestCode)
        homeViewModel.updateRequestCode(requestCode)
    }



    fun filterList(filterText:String, filterType: FilterType){
        when(filterType){
            FilterType.List -> homeAdapter.filter.filter(filterText + "L")
            FilterType.Priority -> homeAdapter.filter.filter(filterText + "P")
            FilterType.Query -> homeAdapter.filter.filter(filterText + "Q")
            FilterType.Tracker -> homeAdapter.filter.filter(filterText + "T")
        }
    }

    fun navigateToAddList(){
        val action = HomeFragmentDirections.actionNavHomeToAddListFragment()
        findNavController().navigate(action)
    }

    fun updateCompletion(uid:Int, isCompleted:Boolean){
        homeViewModel.updateCompletion(uid, isCompleted)
    }

    fun updateTracker(task: ToDo){
        homeViewModel.updateTracker(task)
    }

    fun updateListToDo(task: ToDo, isCompleted: Boolean){
        for (todo in listToDo){
            if (todo == task){
                task.isCompleted = isCompleted
            }
        }
    }

    fun moveDown(position: Int, task:ToDo){
        homeAdapter.filteredList.remove(task)
        homeAdapter.filteredList.add(task)
        homeAdapter.notifyItemMoved(position,homeAdapter.itemCount-1)
    }

    fun moveUp(position: Int, task:ToDo){
        homeAdapter.filteredList.remove(task)
        homeAdapter.filteredList.add(0,task)
        homeAdapter.notifyItemMoved(position,0)
    }

    fun deleteTask(task: ToDo, position:Int){
        val builder = AlertDialog.Builder(this.context, R.style.MyDialogTheme)
        builder.setTitle(R.string.delete)
        builder.setMessage(R.string.ask_delete)
        builder.setPositiveButton(R.string.delete) { _, _ ->
                homeViewModel.deleteTask(task.uid)
                listToDo.remove(task)
                filterList(homeViewModel.currentListName, FilterType.List)
                homeAdapter.notifyItemRemoved(position)
            }
        builder.setNegativeButton(R.string.cancel){ _, _ ->

            }
        builder.show()
    }


    fun moveToEditTask(uid: Int){
        val action = HomeFragmentDirections.actionNavHomeToEditFragment(uid)
        findNavController().navigate(action)
    }

    fun moveToEditTaskList(uid: Int){
        val action = HomeFragmentDirections.actionNavHomeToEditListFragment(uid)
        findNavController().navigate(action)
    }

    fun clearAllSelections(){
        val itemCount = binding.rvHome.adapter?.itemCount
        for(i in 0..itemCount!!){
            val holder = binding.rvHome.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val menu = holder.itemView.findViewById<View>(R.id.longclick_menu)
                menu.visibility = View.GONE
            }
        }
    }

    fun minimizeExpandedRows(){
        val itemCount = binding.rvHome.adapter?.itemCount
        for(i in 0..itemCount!!){
            val holder = binding.rvHome.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val detail = holder.itemView.findViewById<View>(R.id.detail_text)
                val dateContainer = holder.itemView.findViewById<View>(R.id.reminder_date_container)
                val trackerText = holder.itemView.findViewById<LinearLayout>(R.id.tracker_text_layout)
                val addFireIcon = holder.itemView.findViewById<View>(R.id.add_fire_icon)
                if (detail.visibility == View.VISIBLE){
                    detail.visibility = View.GONE
                    dateContainer.visibility = View.GONE
                    trackerText.visibility = View.GONE
                    addFireIcon.visibility = View.GONE
                }
            }
        }
    }

    fun clearAllListSelections(){
        val itemCount = binding.rvHomeList.adapter?.itemCount
        for(i in 0..itemCount!!){
            val holder = binding.rvHomeList.findViewHolderForAdapterPosition(i)
            if (holder != null) {
                val menu = holder.itemView.findViewById<View>(R.id.longclick_menu)
                menu.visibility = View.GONE
                val cardView = holder.itemView.findViewById<MaterialCardView>(R.id.colorCard)
                cardView.strokeWidth = 6
            }
        }
    }

    /*
    * Return 1 then streak can increase by one
    * Return 0 then streak increased today show toast
    * Return -1 then streak cannot continue, reset counter
    * */
    fun canStreakCont(trackedDayMillis:Long, counter:Int):Int{
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        val thisMonth = calendar.get(Calendar.MONTH)
        calendar.timeInMillis = trackedDayMillis
        val trackerDay = calendar.get(Calendar.DAY_OF_MONTH)
        val trackerMonth = calendar.get(Calendar.MONTH)
        var value = 0

        if (counter == 0){
            value = 1
        }else if (thisMonth == trackerMonth){
            value = when (today) {
                trackerDay -> {
                    0
                }
                trackerDay + 1 -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
        return  value
    }

    fun getFormattedDate(millis:Long):String{
        return (activity as MainActivity).getFormattedDate(millis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}