package com.example.juan.aswitch.lib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.juan.aswitch.adapters.PlacesSwipeAdapter
import com.example.juan.aswitch.R

// ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
class SwipeToDeleteCallback(context: Context, adapterPlaces: PlacesSwipeAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val mAdapterPlaces: PlacesSwipeAdapter = adapterPlaces
    private val icon: Drawable? = ContextCompat.getDrawable(context,
            R.drawable.ic_delete_white_24dp)
    private val background: ColorDrawable = ColorDrawable(Color.RED)


    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        // used for up and down movements
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        mAdapterPlaces.deleteItem(position)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20 //so background is behind the rounded corners of itemView

        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
//            dX > 0 -> { // Swiping to the right
//                val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
//                val iconRight = itemView.left + iconMargin
//                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//                background.setBounds(itemView.left, itemView.top,
//                        itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom)
//            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom)
            }
            else -> // view is unSwiped
                background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon.draw(c)
    }
}