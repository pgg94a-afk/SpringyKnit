package com.springyknit.app.ui.report

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.springyknit.app.R
import com.springyknit.app.SpringyKnitApp
import com.springyknit.app.databinding.ActivityKnittingReportBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KnittingReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKnittingReportBinding

    private val viewModel: KnittingReportViewModel by viewModels {
        val projectId = intent.getLongExtra(EXTRA_PROJECT_ID, -1)
        KnittingReportViewModelFactory(
            (application as SpringyKnitApp).repository,
            projectId
        )
    }

    private var saveMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKnittingReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewPager()
        observeProject()
        observeSaveState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupViewPager() {
        val adapter = ReportPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pattern)
                1 -> getString(R.string.counter)
                else -> ""
            }
        }.attach()
    }

    private fun observeProject() {
        lifecycleScope.launch {
            viewModel.project.collectLatest { project ->
                project?.let {
                    binding.toolbar.title = it.name
                }
            }
        }
    }

    private fun observeSaveState() {
        lifecycleScope.launch {
            viewModel.saveState.collectLatest { state ->
                updateSaveIndicator(state)
            }
        }
    }

    private fun updateSaveIndicator(state: KnittingReportViewModel.SaveState) {
        saveMenuItem?.let { item ->
            when (state) {
                KnittingReportViewModel.SaveState.SAVING -> {
                    item.setIcon(R.drawable.ic_save)
                    item.title = getString(R.string.saving)
                }
                KnittingReportViewModel.SaveState.SAVED -> {
                    item.setIcon(R.drawable.ic_check)
                    item.title = getString(R.string.saved)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_knitting_report, menu)
        saveMenuItem = menu.findItem(R.id.action_save_status)
        return true
    }

    companion object {
        const val EXTRA_PROJECT_ID = "extra_project_id"
    }
}
