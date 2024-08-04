package com.rcunal.supernotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.createGraph
import com.noteapp.core.ui.navigation.NavigationNode
import com.noteapp.home.ui.navigation.HomeNavigationNode
import com.rcunal.supernotes.databinding.ActivityMainBinding
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigationNodes: @JvmSuppressWildcards Set<NavigationNode>

    @Inject
    lateinit var lazyNavController: Lazy<NavController>
    private val navController by lazy { lazyNavController.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_NoteApp)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavGraph()
    }

    private fun setupNavGraph() {
        navController.graph = navController.createGraph(
            startDestination = HomeNavigationNode.ROUTE
        ) {
            navigationNodes.forEach { navNode ->
                navNode.addNode(this)
            }
        }
    }
}