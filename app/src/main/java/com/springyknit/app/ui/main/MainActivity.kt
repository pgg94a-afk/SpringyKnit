package com.springyknit.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.springyknit.app.databinding.ActivityMainBinding
import com.springyknit.app.ui.projects.ProjectsGridActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnKnittingReport.setOnClickListener {
            val intent = Intent(this, ProjectsGridActivity::class.java)
            startActivity(intent)
        }
    }
}
