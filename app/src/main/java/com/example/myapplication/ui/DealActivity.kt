package com.example.myapplication.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.models.TravelDeals
import com.example.myapplication.util.FirebaseUtil
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_insert.*
import java.time.Duration

class DealActivity : AppCompatActivity() {
    private lateinit var firebaseDatabase:FirebaseDatabase
    private lateinit var myReference:DatabaseReference
    private lateinit var travelDeal: TravelDeals
    companion object{
        const val PICTURE_RESULT =42
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)
        setSupportActionBar(toolbar)
        firebaseDatabase = FirebaseUtil.firebaseDatabase
        myReference = FirebaseUtil.databaseReference
        val deal = intent.getSerializableExtra(FirebaseUtil.SELECTED_ITEM) as? TravelDeals
        if(deal==null){
            travelDeal = TravelDeals()
        }else{
            travelDeal = deal
        }
        edtTitle.setText(travelDeal.title)
        edtPrice.setText(travelDeal.price)
        edtDescription.setText(travelDeal.description)
        if(travelDeal.imageURL!=null) {
            FirebaseUtil.showImage(this, travelDeal.imageURL as String, imageViewDeal)
        }
        btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true)
            startActivityForResult(Intent.createChooser(intent,"Insert Picture"), PICTURE_RESULT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu,menu)
        if(FirebaseUtil.isAdmin){
            menu?.findItem(R.id.delete_item)?.isVisible = true
            menu?.findItem(R.id.save_item)?.isVisible = true
            enableEditTexts(true)
            btnUploadImage.visibility = View.VISIBLE
        }else{
            menu?.findItem(R.id.delete_item)?.isVisible = false
            menu?.findItem(R.id.save_item)?.isVisible = false
            enableEditTexts(false)
            btnUploadImage.visibility = View.GONE

        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_item->{
                saveDeal()
                cleanEditTexts()
                backtoList()
                return true
            }
            R.id.delete_item->{
                deleteDeal()
                this.displayToast("Deal Deleted")
                backtoList()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveDeal(){
        travelDeal.title= edtTitle.text.toString().trim()
        travelDeal.price = edtPrice.text.toString().trim()
        travelDeal.description = edtDescription.text.toString().trim()
        if(edtTitle.text.isNullOrEmpty()||edtPrice.text.isNullOrEmpty()||edtDescription.text.isNullOrEmpty()) {
            this.displayToast("All Fields Required!")
        }else{
            if(travelDeal.id==null) {
                myReference.push().setValue(travelDeal).addOnSuccessListener {
                    this.displayToast("Successfully Saved Item")

                }.addOnFailureListener { exception ->
                    exception.printStackTrace()
                    this.displayToast("Error:${exception.message}")
                }
            }else{
                myReference.child(travelDeal.id!!).setValue(travelDeal)
            }
        }

    }

    private fun deleteDeal(){
        if(travelDeal == null){
            this.displayToast("Save the deal before deleting")
            return
        }
        myReference.child(travelDeal.id!!).removeValue()
        if(!travelDeal.imageName.isNullOrEmpty()){
            val picRef = FirebaseUtil.firebaseStorage.reference.child(travelDeal.imageName as String)
            picRef.delete().addOnSuccessListener {
                this.displayToast("Image Deleted")
            }.addOnFailureListener {

            }
        }
    }

    private fun backtoList(){
        startActivity(Intent(this,ListActivity::class.java))
    }
    private fun cleanEditTexts(){
        edtTitle.apply {
            setText("")
            clearFocus()
        }
        edtDescription.setText("")
        edtPrice.setText("")



    }
    fun enableEditTexts(isEnabled:Boolean){
        edtTitle.isEnabled = isEnabled
        edtDescription.isEnabled = isEnabled
        edtPrice.isEnabled = isEnabled
    }
fun Context.displayToast(message:String, duration: Int=Toast.LENGTH_SHORT){
    Toast.makeText(this,message,duration).show()

}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICTURE_RESULT && resultCode==Activity.RESULT_OK){
            val imageUri = data?.data
            val reference = FirebaseUtil.storageReference.child(imageUri?.lastPathSegment!!)
            val uploadTask = reference.putFile(imageUri).addOnSuccessListener {
                val pictureName = it.storage.path
                travelDeal.imageName = pictureName
                Log.d("Picture Name",pictureName)
            }
            val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation reference.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.d("IMAGE URL", downloadUri.toString())
                    travelDeal.imageURL = downloadUri.toString()
                    FirebaseUtil.showImage(this,travelDeal.imageURL as String,imageViewDeal)
                } else {
                    // Handle failures
                    // ...
                }
            }

        }
    }
}
