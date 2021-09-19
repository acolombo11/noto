package com.noto.app.label

import android.annotation.SuppressLint
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.AllLabelItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.util.*

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.all_label_item)
abstract class AllLabelItem : EpoxyModelWithHolder<AllLabelItem.Holder>() {

    @EpoxyAttribute
    open var isSelected: Boolean = false

    @EpoxyAttribute
    lateinit var color: NotoColor

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var onClickListener: View.OnClickListener

    override fun bind(holder: Holder) = with(holder.binding) {
        val resources = root.resources
        val backgroundColor = resources.colorResource(R.color.colorBackground)
        val resourceColor = resources.colorResource(color.toResource())
        tvAllLabel.setOnClickListener(onClickListener)
        if (isSelected) {
            tvAllLabel.animateBackgroundColor(backgroundColor, resourceColor)
            tvAllLabel.animateTextColor(resourceColor, backgroundColor)
        } else {
            tvAllLabel.animateLabelColors(fromColor = resourceColor, toColor = backgroundColor)
            tvAllLabel.animateTextColor(backgroundColor, resourceColor)
        }
        Unit
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: AllLabelItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = AllLabelItemBinding.bind(itemView)
        }
    }
}