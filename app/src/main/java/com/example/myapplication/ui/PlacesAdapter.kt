package com.example.myapplication.ui

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.models.TravelDeals
import com.example.myapplication.util.FirebaseUtil
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.deal_item.view.*

class PlacesAdapter: RecyclerView.Adapter<PlacesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.deal_item,parent,false)
        return MyViewHolder(v)
    }

    var travelDeals = ArrayList<TravelDeals>()
    private val firebaseDatabase: FirebaseDatabase
    private val firebaseReference: DatabaseReference

    init {
        travelDeals = FirebaseUtil.travelDeals
        firebaseDatabase = FirebaseUtil.firebaseDatabase
        firebaseReference = FirebaseUtil.databaseReference
        firebaseReference.addChildEventListener(object : ChildEventListener{
            override fun onChildRemoved(p0: DataSnapshot) {
                notifyDataSetChanged()
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(data: DataSnapshot, p1: String?) {
                val travelDeal = data.getValue(TravelDeals::class.java)
                travelDeal?.id = data.key
                travelDeals.add(travelDeal!!)
            }

            override fun onChildAdded(data: DataSnapshot, p1: String?) {
                val travelDeal = data.getValue(TravelDeals::class.java)
                travelDeal?.id = data.key
                travelDeals.add(travelDeal!!)
                notifyItemInserted(travelDeals.size-1)
            }
        })
    }

    override fun getItemCount()=travelDeals.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(travelDeals[position])
    }

    inner class MyViewHolder(private val item:View): RecyclerView.ViewHolder(item), View.OnClickListener{
        override fun onClick(p0: View?) {
            val position = adapterPosition
            Log.d("Click",position.toString())
            val selectedDeal = travelDeals[position]
            val intent = Intent(item.context, DealActivity::class.java)
            intent.putExtra(FirebaseUtil.SELECTED_ITEM,selectedDeal)
            item.context.startActivity(intent)
        }


        val placeImageView = item.imageView
            val tvPlace = item.tvPlace
            val tvPrice = item.tvPrice
            val tvDescription = item.tvDescription


        fun bind(travelDeal:TravelDeals){
            if(!travelDeal.imageURL.isNullOrEmpty()){
                Glide.with(item.context).load(travelDeal.imageURL)
                    .centerCrop()
                    .apply(RequestOptions().override(160,160))
                    .into(placeImageView)            }
            tvPlace.text = travelDeal.title
            tvPrice.text = travelDeal.price
            tvDescription.text = travelDeal.description
            item.setOnClickListener(this)
        }
    }

}
