package com.example.b_trak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import android.widget.Toast

// This class is the "Valet" that handles your bike cards
class BikeAdapter(private val bikeList: List<Bike>) : RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {

    // 1. This "inflates" your item_bike.xml layout (the card)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bike, parent, false)
        return BikeViewHolder(view)
    }

    // 2. This fills the card with the actual bike data (Name and Type)
    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        val currentBike = bikeList[position]
        holder.nameText.text = currentBike.name
        holder.typeText.text = "TYPE: ${currentBike.type}"

        // 1. Listen for the click on the 3 dots
        holder.menuBtn.setOnClickListener { view ->
            val context = view.context

            // 2. Create the PopupMenu (Just like Java!)
            val popup = PopupMenu(context, view)
            popup.menu.add("EDIT_UNIT")
            popup.menu.add("DELETE_UNIT")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "DELETE_UNIT" -> {
                        // 3. Show the Confirmation Dialog
                        AlertDialog.Builder(context)
                            .setTitle("CONFIRM_DELETION")
                            .setMessage("Are you sure you want to remove ${currentBike.name} from the garage?")
                            .setPositiveButton("YES") { _, _ ->
                                // 4. Remove from the "In-Memory" list
                                GarageManager.myGarage.removeAt(holder.adapterPosition)

                                // 5. Refresh the list so it disappears immediately
                                notifyItemRemoved(holder.adapterPosition)
                                notifyItemRangeChanged(holder.adapterPosition, itemCount)

                                Toast.makeText(context, "UNIT_REMOVED", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("NO", null) // "null" just closes the dialog
                            .show()
                        true
                    }
                    "EDIT_UNIT" -> {
                        Toast.makeText(context, "EDIT_MODE: COMING_SOON", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    // 3. Tells the list how many bikes to show
    override fun getItemCount(): Int = bikeList.size

    // This "ViewHolder" holds the IDs for one single card
    class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_item_name)
        val typeText: TextView = itemView.findViewById(R.id.text_item_type)
        val menuBtn: ImageView = itemView.findViewById(R.id.btn_item_menu)
    }
}