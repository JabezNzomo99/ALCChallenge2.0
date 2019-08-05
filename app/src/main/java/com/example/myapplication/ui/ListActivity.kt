package com.example.myapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.util.FirebaseUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    private lateinit var dealsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_menu,menu)
        val insertMenu = menu?.findItem(R.id.insert_menu)
        if(FirebaseUtil.isAdmin){
            insertMenu?.isVisible = true
        }else{
            insertMenu?.isVisible = false

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.insert_menu->{
                val intent = Intent(this, DealActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.logOut->{
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        Log.d("LOGOUT", "user logged out")
                        FirebaseUtil.attachListener()
                    }
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference("traveldeals",this)
        dealsRecyclerView = travelDealsList
        dealsRecyclerView.adapter = PlacesAdapter()
        dealsRecyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        dealsRecyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
        FirebaseUtil.attachListener()
    }


    public fun showMenu():Unit{
        invalidateOptionsMenu()
    }

}
