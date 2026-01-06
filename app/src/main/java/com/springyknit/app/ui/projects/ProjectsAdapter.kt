package com.springyknit.app.ui.projects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.springyknit.app.data.model.KnittingProject
import com.springyknit.app.databinding.ItemAddProjectBinding
import com.springyknit.app.databinding.ItemProjectBinding

class ProjectsAdapter(
    private val onProjectClick: (KnittingProject) -> Unit,
    private val onAddClick: () -> Unit,
    private val onProjectLongClick: (KnittingProject) -> Unit
) : ListAdapter<ProjectsAdapter.ProjectItem, RecyclerView.ViewHolder>(ProjectDiffCallback()) {

    sealed class ProjectItem {
        data class Project(val project: KnittingProject) : ProjectItem()
        object AddButton : ProjectItem()
    }

    companion object {
        private const val VIEW_TYPE_ADD = 0
        private const val VIEW_TYPE_PROJECT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ProjectItem.AddButton -> VIEW_TYPE_ADD
            is ProjectItem.Project -> VIEW_TYPE_PROJECT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ADD -> {
                val binding = ItemAddProjectBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                AddViewHolder(binding)
            }
            else -> {
                val binding = ItemProjectBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ProjectViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ProjectItem.AddButton -> (holder as AddViewHolder).bind()
            is ProjectItem.Project -> (holder as ProjectViewHolder).bind(item.project)
        }
    }

    inner class AddViewHolder(
        private val binding: ItemAddProjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener { onAddClick() }
        }
    }

    inner class ProjectViewHolder(
        private val binding: ItemProjectBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(project: KnittingProject) {
            binding.tvProjectName.text = project.name
            binding.root.setOnClickListener { onProjectClick(project) }
            binding.root.setOnLongClickListener {
                onProjectLongClick(project)
                true
            }
        }
    }

    fun submitProjects(projects: List<KnittingProject>) {
        val items = mutableListOf<ProjectItem>()
        items.add(ProjectItem.AddButton)
        items.addAll(projects.map { ProjectItem.Project(it) })
        submitList(items)
    }

    class ProjectDiffCallback : DiffUtil.ItemCallback<ProjectItem>() {
        override fun areItemsTheSame(oldItem: ProjectItem, newItem: ProjectItem): Boolean {
            return when {
                oldItem is ProjectItem.AddButton && newItem is ProjectItem.AddButton -> true
                oldItem is ProjectItem.Project && newItem is ProjectItem.Project ->
                    oldItem.project.id == newItem.project.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ProjectItem, newItem: ProjectItem): Boolean {
            return oldItem == newItem
        }
    }
}
