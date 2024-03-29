package com.example.to_do_dles.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.to_do_dles.databinding.FragmentReminderBinding
import com.example.to_do_dles.model.ToDo
import com.example.to_do_dles.viewmodel.HomeViewModel


class ReminderFragment : Fragment() {
    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = arguments?.getInt("uid")

        homeViewModel.getTask(uid!!)

        lateinit var toDo:ToDo
        homeViewModel.toDo.observe(viewLifecycleOwner){
            toDo = it
            binding.taskNameTv.text = toDo.task
            binding.detailsText.text = toDo.description
        }



    }



}