package com.example.b_trak

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

/**
 * BikeAdapter manages the connection between the list of [Bike] data and the [RecyclerView].
 * Updated to handle database deletions.
 */
class BikeAdapter(private val bikeList: MutableList<Bike>) : RecyclerView.Adapter<BikeAdapter.BikeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BikeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bike, parent, false)
        return BikeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BikeViewHolder, position: Int) {
        val currentBike = bikeList[position]

        holder.nameText.text = currentBike.name
        holder.typeText.text = "TYPE: ${currentBike.type}"
        holder.iconImage.setImageResource(currentBike.iconResId)

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, BikeDetailActivity::class.java)
            intent.putExtra("BIKE_INDEX", holder.bindingAdapterPosition)
            context.startActivity(intent)
        }

        holder.menuBtn.setOnClickListener { view ->
            val context = view.context
            val popup = PopupMenu(context, view)
            popup.menu.add("RENAME UNIT")
            popup.menu.add("DELETE UNIT")

            popup.setOnMenuItemClickListener { item ->
                val currentPosition = holder.bindingAdapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnMenuItemClickListener false

                when (item.title) {
                    "DELETE UNIT" -> {
                        AlertDialog.Builder(context)
                            .setTitle("CONFIRM DELETION")
                            .setMessage("Are you sure you want to remove ${currentBike.name} from the garage?")
                            .setPositiveButton("YES") { _, _ ->
                                // Delete from Database
                                (context as? AppCompatActivity)?.lifecycleScope?.launch {
                                    val db = AppDatabase.getDatabase(context)
                                    val entity = BikeEntity(
                                        id = currentBike.id,
                                        userId = 0, // Placeholder
                                        name = currentBike.name,
                                        type = currentBike.type,
                                        odometer = currentBike.odometer,
                                        iconResId = currentBike.iconResId,
                                        isSecondHand = currentBike.isSecondHand
                                    )
                                    db.bikeDao().deleteBike(entity)
                                    
                                    // Remove from local list and Notify
                                    bikeList.removeAt(currentPosition)
                                    notifyItemRemoved(currentPosition)
                                    notifyItemRangeChanged(currentPosition, itemCount)
                                    Toast.makeText(context, "UNIT REMOVED", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("NO", null)
                            .show()
                        true
                    }
                    "RENAME UNIT" -> {
                        val input = android.widget.EditText(context)
                        input.setText(currentBike.name)
                        
                        AlertDialog.Builder(context)
                            .setTitle("RENAME UNIT")
                            .setView(input)
                            .setPositiveButton("SAVE") { _, _ ->
                                val newName = input.text.toString().trim()
                                if (newName.isNotEmpty()) {
                                    currentBike.name = newName
                                    notifyItemChanged(currentPosition)
                                    
                                    (context as? AppCompatActivity)?.lifecycleScope?.launch {
                                        val db = AppDatabase.getDatabase(context)
                                        db.bikeDao().updateBikeName(currentBike.id, newName)
                                        Toast.makeText(context, "UNIT RENAMED", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .setNegativeButton("CANCEL", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = bikeList.size

    class BikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.text_item_name)
        val typeText: TextView = itemView.findViewById(R.id.text_item_type)
        val menuBtn: ImageView = itemView.findViewById(R.id.btn_item_menu)
        val iconImage: ImageView = itemView.findViewById(R.id.img_bike_icon)
    }
}
