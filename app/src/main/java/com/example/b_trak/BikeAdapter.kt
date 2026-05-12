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

import android.content.Intent

/**
 * BikeAdapter manages the connection between the list of [Bike] data and the [RecyclerView] visual components.
 * It is responsible for creating item views, binding data to those views, and handling user interactions 
 * like clicking an item to view details or using the context menu to delete a unit.
 *
 * @property bikeList The data source: a list of registered bicycles to be displayed.
 */
class BikeAdapter(private val bikeList: List<Bike>) : RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {

    /**
     * Called when the RecyclerView needs a new ViewHolder of the given type to represent an item.
     * This creates the visual container for a single bicycle card.
     *
     * @param parent The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new BikeViewHolder that holds the View for each list item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        // Inflate the XML layout for a single bike item card
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bike, parent, false)
        return BikeViewHolder(view)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method binds the specific [Bike] properties (name, type, icon) to the UI elements.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        // Retrieve the specific bike object for this list index
        val currentBike = bikeList[position]

        // Update the text fields with the bike's specific data
        holder.nameText.text = currentBike.name
        holder.typeText.text = "TYPE: ${currentBike.type}"
        // Set the discipline-specific icon
        holder.iconImage.setImageResource(currentBike.iconResId)

        // Set a click listener on the entire item card to open the Detail screen
        holder.itemView.setOnClickListener {
            // Get the current position safely using the bindingAdapterPosition property
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val context = it.context
                // Create an intent to launch the BikeDetailActivity
                val intent = Intent(context, BikeDetailActivity::class.java)
                // Pass the index of the clicked bike so the Detail screen knows which data to load
                intent.putExtra("BIKE_INDEX", position)
                context.startActivity(intent) // Execute the transition
            }
        }

        // Set a click listener on the vertical three-dot menu button
        holder.menuBtn.setOnClickListener { view ->
            val context = view.context

            // Initialize a PopupMenu anchored to the menu button
            val popup = PopupMenu(context, view)
            // Add options to the menu
            popup.menu.add("EDIT UNIT")
            popup.menu.add("DELETE UNIT")

            // Define the logic for when a menu item is selected
            popup.setOnMenuItemClickListener { item ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnMenuItemClickListener false

                when (item.title) {
                    "DELETE UNIT" -> {
                        // Display a confirmation dialog before removing data
                        AlertDialog.Builder(context)
                            .setTitle("CONFIRM DELETION")
                            .setMessage("Are you sure you want to remove ${currentBike.name} from the garage?")
                            .setPositiveButton("YES") { _, _ ->
                                // Remove the bike from the central in-memory list
                                GarageManager.myGarage.removeAt(currentPosition)

                                // Notify the RecyclerView that an item was removed to trigger the animation
                                notifyItemRemoved(currentPosition)
                                // Notify that the range of items below has changed (updates their indices)
                                notifyItemRangeChanged(currentPosition, itemCount)

                                // Show confirmation toast
                                Toast.makeText(context, "UNIT REMOVED", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("NO", null) // Do nothing on cancel
                            .show()
                        true
                    }
                    "EDIT UNIT" -> {
                        // Placeholder for future edit functionality
                        Toast.makeText(context, "EDIT MODE: COMING SOON", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show() // Display the menu
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int = bikeList.size

    /**
     * Cache class that holds references to the UI components within a single item view.
     * This prevents repetitive and expensive calls to findViewById during scrolling.
     */
    class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_item_name)
        val typeText: TextView = itemView.findViewById(R.id.text_item_type)
        val menuBtn: ImageView = itemView.findViewById(R.id.btn_item_menu)
        val iconImage: ImageView = itemView.findViewById(R.id.img_bike_icon)
    }
}