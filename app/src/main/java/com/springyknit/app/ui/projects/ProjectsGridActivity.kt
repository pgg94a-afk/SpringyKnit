package com.springyknit.app.ui.projects

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.springyknit.app.R
import com.springyknit.app.SpringyKnitApp
import com.springyknit.app.data.model.KnittingProject
import com.springyknit.app.databinding.ActivityProjectsGridBinding
import com.springyknit.app.ui.report.KnittingReportActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProjectsGridActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectsGridBinding
    private lateinit var adapter: ProjectsAdapter

    private val viewModel: ProjectsViewModel by viewModels {
        ProjectsViewModelFactory((application as SpringyKnitApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectsGridBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeProjects()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProjectsAdapter(
            onProjectClick = { project -> openProject(project) },
            onAddClick = { createNewProject() },
            onProjectLongClick = { project -> showDeleteDialog(project) }
        )

        binding.rvProjects.apply {
            layoutManager = GridLayoutManager(this@ProjectsGridActivity, 3)
            adapter = this@ProjectsGridActivity.adapter
        }
    }

    private fun observeProjects() {
        lifecycleScope.launch {
            viewModel.projects.collectLatest { projects ->
                adapter.submitProjects(projects)
            }
        }
    }

    private fun createNewProject() {
        lifecycleScope.launch {
            val projectId = viewModel.createProjectAndGetId()
            openProject(projectId)
        }
    }

    private fun openProject(project: KnittingProject) {
        openProject(project.id)
    }

    private fun openProject(projectId: Long) {
        val intent = Intent(this, KnittingReportActivity::class.java).apply {
            putExtra(KnittingReportActivity.EXTRA_PROJECT_ID, projectId)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(project: KnittingProject) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_project)
            .setMessage(R.string.delete_confirm)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteProject(project)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
