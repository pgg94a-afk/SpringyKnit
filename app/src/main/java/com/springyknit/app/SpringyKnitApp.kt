package com.springyknit.app

import android.app.Application
import com.springyknit.app.data.db.AppDatabase
import com.springyknit.app.data.repository.ProjectRepository

class SpringyKnitApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { ProjectRepository(database.projectDao()) }
}
