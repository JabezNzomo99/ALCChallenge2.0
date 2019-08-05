package com.example.myapplication.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ListActivity
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.models.TravelDeals
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseUtil{
    companion object{
        private const val RC_SIGN_IN =100
        const val SELECTED_ITEM= "SELECTED ITEM"
        lateinit var firebaseDatabase:FirebaseDatabase
        lateinit var databaseReference:DatabaseReference
        lateinit var firebaseAuth: FirebaseAuth
        lateinit var firebaseStorage:FirebaseStorage
        lateinit var storageReference: StorageReference
        lateinit var authListener: FirebaseAuth.AuthStateListener
         var isAdmin:Boolean = false
        @SuppressLint("StaticFieldLeak")
        public var firebaseUtil: FirebaseUtil? = null
        lateinit var travelDeals: ArrayList<TravelDeals>
        @SuppressLint("StaticFieldLeak")
        lateinit var caller:com.example.myapplication.ui.ListActivity



        public fun openFbReference(ref:String, callerActivity:com.example.myapplication.ui.ListActivity) {
            if (firebaseUtil == null) {
                firebaseUtil = FirebaseUtil()
                firebaseDatabase = FirebaseDatabase.getInstance()
                firebaseAuth = FirebaseAuth.getInstance()
                caller = callerActivity
                authListener = FirebaseAuth.AuthStateListener {auth->
                   if(firebaseAuth.currentUser == null){
                       signIn()
                   }else{
                       val userId = auth.uid
                       checkAdmin(userId)
                   }
                    Toast.makeText(callerActivity.baseContext, "Welcome Back",Toast.LENGTH_LONG).show()
                }
                connectStorage()
            }
            travelDeals = ArrayList<TravelDeals>()
            databaseReference = firebaseDatabase.getReference().child(ref)
        }

        private fun checkAdmin(userId: String?) {
            isAdmin = false
            val reference = firebaseDatabase.reference.child("administrators").child(userId!!)
            reference.addChildEventListener(object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin = true
                    Log.d("Admin", "You are and administrator")
                    caller.showMenu()
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })


        }

        fun signIn(){
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
            // Create and launch sign-in intent
            caller.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN)
        }
        fun attachListener(){
            firebaseAuth.addAuthStateListener(authListener)
        }

        fun detachListener(){
            firebaseAuth.removeAuthStateListener(authListener)
        }
        fun connectStorage(){
            firebaseStorage = FirebaseStorage.getInstance()
            storageReference = firebaseStorage.reference.child("deals_pictures")
        }
        fun showImage(context: Context,url:String,imageView: ImageView){
            val width = Resources.getSystem().displayMetrics.widthPixels
            Glide.with(context).load(url)
                .centerCrop()
                .apply(RequestOptions().override(width,width*2/3))
                .into(imageView)
        }
        }



    }



