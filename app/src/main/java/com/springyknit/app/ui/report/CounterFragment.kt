package com.springyknit.app.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.springyknit.app.databinding.FragmentCounterBinding

class CounterFragment : Fragment() {

    private var _binding: FragmentCounterBinding? = null
    private val binding get() = _binding!!

    private var counter = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCounterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        updateCounterDisplay()
    }

    private fun setupClickListeners() {
        binding.btnIncrease.setOnClickListener {
            counter++
            updateCounterDisplay()
        }

        binding.btnDecrease.setOnClickListener {
            if (counter > 0) {
                counter--
                updateCounterDisplay()
            }
        }
    }

    private fun updateCounterDisplay() {
        binding.tvCounter.text = counter.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
