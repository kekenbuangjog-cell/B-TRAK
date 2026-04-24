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

/**
 * UI_PROTOCOL: BikeAdapter handles the mapping of Bike data models to visual list components (RecyclerView).
 * It manages individual item state and user interactions (Edit/Delete).
 */
class BikeAdapter(private val bikeList: List<Bike>) : RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {

    /**
     * INFLATION_PROTOCOL: Generates the visual container (ViewHolder) for a single bike unit.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bike, parent, false)
        return BikeViewHolder(view)
    }

    /**
     * DATA_BINDING: Connects properties from a Bike object to the specific UI elements in a card.
     */
    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        val currentBike = bikeList[position]
        holder.nameText.text = currentBike.name
        holder.typeText.text = "TYPE: ${currentBike.type}"

        // ACTION: Initialize Context Menu for the specific unit
        holder.menuBtn.setOnClickListener { view ->
            val context = view.context

            // PROTOCOL: INDUSTRIAL_POPUP_MENU
            val popup = PopupMenu(context, view)
            popup.menu.add("EDIT UNIT")
            popup.menu.add("DELETE UNIT")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "DELETE UNIT" -> {
                        // PROTOCOL: DELETION_CONFIRMATION
                        AlertDialog.Builder(context)
                            .setTitle("CONFIRM_DELETION")
                            .setMessage("Are you sure you want to remove ${currentBike.name} from the garage?")
                            .setPositiveButton("YES") { _, _ ->
                                // DATA_MODIFICATION: Remove from the Singleton GarageManager
                                GarageManager.myGarage.removeAt(holder.adapterPosition)

                                // UI_SYNC: Immediate refresh of the list component
                                notifyItemRemoved(holder.adapterPosition)
                                notifyItemRangeChanged(holder.adapterPosition, itemCount)

                                Toast.makeText(context, "UNIT_REMOVED", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("NO", null)
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

    /**
     * SYSTEM_QUERY: Returns the total count of active units in the provided list.
     */
    override fun getItemCount(): Int = bikeList.size

    /**
     * UI_CACHE: Holds references to the views within a single item card to optimize scrolling performance.
     */
    class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_item_name)
        val typeText: TextView = itemView.findViewById(R.id.text_item_type)
        val menuBtn: ImageView = itemView.findViewById(R.id.btn_item_menu)
    }
}