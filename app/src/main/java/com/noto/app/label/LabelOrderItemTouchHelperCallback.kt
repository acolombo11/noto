package com.noto.app.label

import android.os.Build
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModelTouchCallback
import com.noto.app.R
import com.noto.app.util.*

class LabelOrderItemTouchHelperCallback(
    epoxyController: EpoxyController,
    private val callback: () -> Unit,
) : EpoxyModelTouchCallback<LabelOrderItem>(epoxyController, LabelOrderItem::class.java) {

    override fun isLongPressDragEnabled(): Boolean = false

    override fun getMovementFlagsForModel(model: LabelOrderItem?, adapterPosition: Int): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onModelMoved(fromPosition: Int, toPosition: Int, modelBeingMoved: LabelOrderItem?, itemView: View?) {
        super.onModelMoved(fromPosition, toPosition, modelBeingMoved, itemView)
        callback()
    }

    override fun onDragStarted(model: LabelOrderItem?, itemView: View?, adapterPosition: Int) {
        super.onDragStarted(model, itemView, adapterPosition)
        itemView?.isSelected = true
        itemView?.context?.let { context ->
            if (model != null) {
                val selectedBackgroundColor = context.colorResource(model.color.toResource())
                val selectedTextColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
                val backgroundColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
                val textColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
                val tvLabel = itemView.findViewById<TextView>(R.id.tv_label)
                val ibDrag = itemView.findViewById<ImageButton>(R.id.ib_drag)
                tvLabel.animateBackgroundColor(backgroundColor, selectedBackgroundColor)
                tvLabel.animateTextColor(textColor, selectedTextColor)
                ibDrag.imageTintList = selectedBackgroundColor.toColorStateList()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    itemView.outlineAmbientShadowColor = selectedBackgroundColor
                    itemView.outlineSpotShadowColor = selectedBackgroundColor
                }
            }
        }
    }

    override fun onDragReleased(model: LabelOrderItem?, itemView: View?) {
        super.onDragReleased(model, itemView)
        itemView?.isSelected = false
        itemView?.context?.let { context ->
            if (model != null) {
                val selectedBackgroundColor = context.colorResource(model.color.toResource())
                val selectedTextColor = context.colorAttributeResource(R.attr.notoBackgroundColor)
                val backgroundColor = context.colorAttributeResource(R.attr.notoSurfaceColor)
                val textColor = context.colorAttributeResource(R.attr.notoPrimaryColor)
                val tvLabel = itemView.findViewById<TextView>(R.id.tv_label)
                val ibDrag = itemView.findViewById<ImageButton>(R.id.ib_drag)
                tvLabel.animateBackgroundColor(selectedBackgroundColor, backgroundColor)
                tvLabel.animateTextColor(selectedTextColor, textColor)
                ibDrag.imageTintList = textColor.toColorStateList()
            }
        }
    }

}