package com.example.infostation.ui.display

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.infostation.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_display.*

@AndroidEntryPoint
class DisplayFragment : Fragment(R.layout.fragment_display) {
    private lateinit var adapter: DisplayAdapter
    private val viewModel: DisplayViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
    }

    private fun setupAdapter() {
        viewModel.time.observe(viewLifecycleOwner, { time ->
            viewModel.date.observe(viewLifecycleOwner, { date ->
                viewModel.temp.observe(viewLifecycleOwner, { temp ->
                    adapter = DisplayAdapter(arrayListOf(time, date, temp))
                    recycler_view.adapter = adapter
                    adapter.notifyDataSetChanged()
                })
            })
        })

    }
}