

package io.github.coffeegerm.glucoseguide.ui

import android.content.Context
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.view.MenuItem
import io.github.coffeegerm.glucoseguide.R
import io.github.coffeegerm.glucoseguide.ui.entry.NewEntryActivity
import io.github.coffeegerm.glucoseguide.ui.grade.GradeFragment
import io.github.coffeegerm.glucoseguide.ui.list.ListFragment
import io.github.coffeegerm.glucoseguide.ui.more.MoreFragment
import io.github.coffeegerm.glucoseguide.ui.statistics.StatisticsFragment

/**
 * Class dedicated to controlling what fragment should be presented base on user selection
 */

class NavigationOnItemSelectedListener(private val supportFragmentManager: FragmentManager, var context: Context) : BottomNavigationView.OnNavigationItemSelectedListener {
  
  private var coldLaunch = true
  
  private val listFragment: ListFragment by lazy { ListFragment() }
  private val statsFragment: StatisticsFragment by lazy { StatisticsFragment() }
  private val gradeFragment: GradeFragment by lazy { GradeFragment() }
  private val moreFragment: MoreFragment by lazy { MoreFragment() }
  
  init {
    if (coldLaunch) showFragment(listFragment)
    coldLaunch = false
  }
  
  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    var changesSelectedItem = true
    when (item.itemId) {
      
      R.id.nav_list -> {
        showFragment(listFragment)
        changesSelectedItem = true
      }
      
      R.id.nav_stats -> {
        showFragment(statsFragment)
        changesSelectedItem = true
      }
      
      R.id.nav_add_new_entry -> {
        context.startActivity(Intent(context, NewEntryActivity::class.java))
        changesSelectedItem = false
      }
      
      R.id.nav_grading -> {
        showFragment(gradeFragment)
        changesSelectedItem = true
      }
      
      R.id.nav_settings -> {
        showFragment(moreFragment)
        changesSelectedItem = true
      }
    }
    return changesSelectedItem
  }
  
  private fun showFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitNowAllowingStateLoss()
}