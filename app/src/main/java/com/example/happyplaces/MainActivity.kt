package com.example.happyplaces

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.databinding.ActivityMainBinding
import com.example.happyplaces.ui.theme.DatabaseHelper
import com.example.happyplaces.ui.theme.HappyplacesTheme
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList


class MainActivity : AppCompatActivity(), ItemAdapter.onItemClickListener {



    private var binding: ActivityMainBinding? = null
    private  var recyclerView: RecyclerView? = null
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var dbhelper : DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        dbhelper= DatabaseHelper(this)



        Log.d("check","check1")
        Log.d("check", "onCreate called")
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Log.d("check","check2")
        Log.d("check", "Binding initialized")

        setSupportActionBar(binding?.toolbarAddPlace)

        if (supportActionBar != null) {
            supportActionBar?.title = "  HAPPY PLACES"
            binding?.toolbarAddPlace?.setTitleTextColor(ContextCompat.getColor(this,R.color.light_white))
        }

        val imageName ="ic_upload_image"
        val resourceId : Int= resources.getIdentifier(imageName,"drawable",packageName)
        // Initialize Recycler View
        recyclerView = binding?.rvView


        //Sample data
//        val items = arrayListOf(
//            HappyPlacesModel("Home", "Abohar"),
//            HappyPlacesModel("Work", "Delhi"),
//            HappyPlacesModel("Visit", "Udaipur")
//
//        )

        val items=dbhelper.getAllUsers()


        Log.d("check", "Items size: ${items.size}")
        // Initialize Adapter
        itemAdapter = ItemAdapter(items,this)

        //Set LayoutManager
        recyclerView?.layoutManager = LinearLayoutManager(this)

        // attach the adapter to recycler view
        recyclerView?.adapter =itemAdapter






        Log.d("check","check3")

        // add itemtouchhelper object for swiping delete functionality

        val itemTouchHelperCaller =object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("SuspiciousIndentation")

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                if(direction == ItemTouchHelper.RIGHT) {
                    val position = viewHolder.adapterPosition
                    val deletedItem = items[position]

                    // Remove the item from the list

                    items.removeAt(position)
                    dbhelper.deleteUser(position)
                    itemAdapter.notifyItemRemoved(position)

                    // Snackbar to undo the functionality

                    Snackbar.make(recyclerView!!, "Item deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO")
                        {
                            items.add(position, deletedItem)
                            itemAdapter.notifyItemInserted(position)
                        }.show()

                }
                else
                {
                    val item = items[viewHolder.adapterPosition]
                    val intent =Intent(this@MainActivity, AddPlaces::class.java)
                    intent.putExtra("mode","update")
                    intent.putExtra("happyPlaceObject",item)
                    startActivity(intent)
                }


            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemview = viewHolder.itemView
                val icon: Drawable
                val iconMargin: Int
                val background: ColorDrawable
                val iconSize = 80

                // define icon and background color
                // right to left swipe

                if (dX > 0) {
                    icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.delete)!!
                    background = ColorDrawable(Color.RED)
                }
                else   // left to right
                {
                    icon = ContextCompat.getDrawable(recyclerView.context, R.drawable.edit)!!
                    background = ColorDrawable(Color.GREEN)
                }


                // Update the background bounds based on swipe direction and displacement (dX)
                if (dX > 0) {
                    // Swipe to the right: left bound starts at itemView.left, right bound is itemView.left + dX
                    background.setBounds(
                        itemview.left,
                        itemview.top,
                        itemview.left + dX.toInt(),
                        itemview.bottom
                    )
                } else {
                    // Swipe to the left: right bound is itemView.right, left bound is itemView.right + dX
                    background.setBounds(
                        itemview.right + dX.toInt(),
                        itemview.top,
                        itemview.right,
                        itemview.bottom
                    )
                }

                    background.draw(c)


                    //val iconHeight = icon.intrinsicHeight
                    //val iconWidth = icon.intrinsicWidth
                    val iconTop = itemview.top + (itemview.height - iconSize) / 2
                    val iconBottom = iconTop + iconSize

                    Log.d("cal", "  ${iconTop},  ${iconBottom}")


                    if (dX > 0) { // Swipe to the right
                        iconMargin = (itemview.height - iconSize) / 2
                        val iconLeft = itemview.left + iconMargin
                        val iconRight = iconLeft + iconSize
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    } else if (dX < 0) { // Swipe to the left
                        iconMargin = (itemview.height - iconSize) / 2
                        val iconRight = itemview.right - iconMargin
                        val iconLeft = iconRight - iconSize
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    }



                    icon.draw(c)
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }




        }

         // attach touch Helper to Recyler View
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCaller)
        itemTouchHelper.attachToRecyclerView(recyclerView)



        binding?.fab?.setOnClickListener {
            val intent = Intent(this, AddPlaces::class.java)
            startActivity(intent)
        }


        Log.d("check","check4")





    }

     //val items= dbhelper.getAllUsers()

    override fun onItemClick(position : Int, items:ArrayList<HappyPlacesModel>)
    {
        val intent =Intent(this,AddDetails::class.java)

        intent.putExtra("title",items[position].getTitle())
        intent.putExtra("desc",items[position].getDesc())

        startActivity(intent)

    }




    override fun onDestroy() {
        super.onDestroy()
        binding =null
    }
}



