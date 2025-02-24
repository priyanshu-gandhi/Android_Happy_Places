package com.example.happyplaces

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.happyplaces.databinding.ItemRvBinding



class ItemAdapter(private var items :ArrayList<HappyPlacesModel>,
    private var listener: onItemClickListener) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {


        interface onItemClickListener{
            fun onItemClick(position: Int, items: ArrayList<HappyPlacesModel>)
        }

        // create view holder class to hold the refrences
    class ViewHolder(binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {

        val rvCard= binding.rvCard
        val image=binding.rvImage
        val title= binding.rvTitle
        val location = binding.rvLocation

    }
    // Inflate item layout and create Viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    // How much the size of Recycler View we will return
    override fun getItemCount(): Int {
       return  items.size
    }


    // Bind data to each Viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         val item=items[position]

//        val imageUri= item.getImage()?.toUri()
//        if(imageUri !=null)
//        {
//            holder.image.setImageURI(imageUri)
//        }

          holder.title.text =item.getTitle()
          holder.location.text =item.getLocation()
          holder.itemView.setOnClickListener {
              listener.onItemClick(position,items)
          }


         if(position%2!=0)
         {
             holder.rvCard.setBackgroundColor(Color.parseColor("#FFFFFF"))

         }
        else
         {
             holder.rvCard.setBackgroundColor(Color.parseColor("#FFC7C7"))
         }

    }


}