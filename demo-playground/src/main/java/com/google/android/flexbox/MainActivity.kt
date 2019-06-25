/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.flexbox

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.apps.flexbox.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yarolegovich.slidingrootnav.SlidingRootNav
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import java.util.*

//NavigationView.OnNavigationItemSelectedListener

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //전역 변수 선언
        var mApp = GlobalVar()
        var strGlobalVar = mApp.server_address

 //       Toast.makeText(this, strGlobalVar, Toast.LENGTH_LONG).show()




/*
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        */


        val menu_icon: View = findViewById(R.id.menu_icon)
        menu_icon.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()


        }



        val sidemenu_icon: View = findViewById(R.id.sidemenu_icon)
        sidemenu_icon.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar side", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }

        val refesh_icon: View = findViewById(R.id.refresh_icon)
        refesh_icon.setOnClickListener { view ->
            Snackbar.make(view, "새로고침 완료되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()
        }
        val connect_icon: View = findViewById(R.id.connect_icon)
        connect_icon.setOnClickListener { view ->
            Snackbar.make(view, "기기와 연결 시도 중입니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            val intent = Intent (this, WebViewActivity::class.java)
            startActivity(intent)
        }
        val lock_icon: View = findViewById(R.id.lock_icon)
        lock_icon.setOnClickListener { view ->
            Snackbar.make(view, "전체 잠금 해제되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            val intent = Intent (this, LockActivityAll::class.java)
            startActivity(intent)
        }


        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val radioGroup: RadioGroup = navigationView.getHeaderView(0)
                .findViewById(R.id.radiogroup_container_implementation)
        val fragmentManager = supportFragmentManager


        /*
        var menu_button:Button = findViewById(R.id.menu_icon)
        menu_button.setOnClickListener {
            replaceToFlexboxLayoutFragment(fragmentManager)
        }*/




        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radiobutton_viewgroup) {
                replaceToFlexboxLayoutFragment(fragmentManager)
            } else {
                replaceToRecyclerViewFragment(fragmentManager)
            }
        }

        if (savedInstanceState == null) {
            replaceToFlexboxLayoutFragment(fragmentManager)
        }
    }

    private fun replaceToFlexboxLayoutFragment(fragmentManager: FragmentManager) {
        var fragment: FlexboxLayoutFragment? = fragmentManager.findFragmentByTag(FLEXBOXLAYOUT_FRAGMENT) as FlexboxLayoutFragment?
        if (fragment == null) {
            fragment = FlexboxLayoutFragment.newInstance()
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, FLEXBOXLAYOUT_FRAGMENT).commit()
    }

    private fun replaceToRecyclerViewFragment(fragmentManager: FragmentManager) {
        var fragment: RecyclerViewFragment? = fragmentManager.findFragmentByTag(RECYCLERVIEW_FRAGMENT) as RecyclerViewFragment?
        if (fragment == null) {
            fragment = RecyclerViewFragment.newInstance()
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, RECYCLERVIEW_FRAGMENT).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val FLEXBOXLAYOUT_FRAGMENT = "flexboxlayout_fragment"

        private const val RECYCLERVIEW_FRAGMENT = "recyclerview_fragment"
    }
}
