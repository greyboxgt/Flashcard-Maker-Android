package com.piapps.flashcardpro.features.editor.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kent.layouts.textColorResource
import com.piapps.flashcardpro.R
import com.piapps.flashcardpro.core.db.tables.CardDb
import com.piapps.flashcardpro.core.extension.color
import com.piapps.flashcardpro.core.extension.getLocalizedString
import com.piapps.flashcardpro.core.extension.load
import com.piapps.flashcardpro.core.extension.toColor
import com.piapps.flashcardpro.core.platform.LONG_ANIMATION
import com.piapps.flashcardpro.core.platform.component.FlipAnimation
import com.piapps.flashcardpro.features.editor.adapter.cells.CardUI

/**
 * Created by abduaziz on 2019-10-01 at 21:26.
 */

class CardsEditorAdapter : RecyclerView.Adapter<CardsEditorAdapter.ViewHolder>() {

    val list = arrayListOf<CardDb>()
    var defaultColor = ""

    fun addNewCard(card: CardDb, pos: Int) {
        list.add(pos, card)
        notifyItemInserted(pos)
    }

    fun addAll(cards: List<CardDb>) {
        list.addAll(cards)
        notifyDataSetChanged()
    }

    fun updateCard(card: CardDb) {
        list.forEachIndexed { index, c ->
            if (c.id == card.id) {
                list[index] = card
                notifyItemChanged(index)
            }
        }
    }

    fun remove(card: CardDb) {
        val index = list.indexOfFirst { c -> c.id == card.id }
        if (index != -1) {
            list.removeAt(index)
            notifyItemRemoved(index)
            notifyItemRangeChanged(index, list.size)
        }
    }

    fun reverseCards() {
        list.forEach {
            it.reverse()
        }
        notifyDataSetChanged()
    }

    fun updateOrders() {
        list.forEachIndexed { index, card ->
            card.order = index
        }
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(CardUI().createView(p0.context))
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(list[pos])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val root: CardView

        val front: FrameLayout
        val ivDelete: AppCompatImageView
        val ivFrontEdit: AppCompatImageView
        val ivFrontFlip: AppCompatImageView
        val tvFront: TextView
        val ivFront: AppCompatImageView

        val back: FrameLayout
        val ivBackEdit: AppCompatImageView
        val ivBackFlip: AppCompatImageView
        val tvBack: TextView
        val ivBack: AppCompatImageView

        init {
            root = itemView.findViewById(CardUI.rootId)

            front = itemView.findViewById(CardUI.frontId)
            tvFront = itemView.findViewById(CardUI.frontTvId)
            ivFront = itemView.findViewById(CardUI.frontIvId)
            ivFrontEdit = itemView.findViewById(CardUI.frontIvEditId)
            ivFrontFlip = itemView.findViewById(CardUI.frontIvFlipId)

            back = itemView.findViewById(CardUI.backId)
            tvBack = itemView.findViewById(CardUI.backTvId)
            ivBack = itemView.findViewById(CardUI.backIvId)
            ivBackEdit = itemView.findViewById(CardUI.backIvEditId)
            ivBackFlip = itemView.findViewById(CardUI.backIvFlipId)

            ivDelete = itemView.findViewById(CardUI.frontIvDeleteId)

            ivFrontEdit.setOnClickListener(this)
            ivFrontFlip.setOnClickListener(this)
            tvFront.setOnClickListener(this)
            ivBackEdit.setOnClickListener(this)
            ivBackFlip.setOnClickListener(this)
            tvBack.setOnClickListener(this)
            ivDelete.setOnClickListener(this)
        }

        fun bind(card: CardDb) {
            front.visibility = View.VISIBLE
            back.visibility = View.GONE

            card.order = adapterPosition
            // set texts
            tvFront.text =
                if (card.front.isNotBlank() || card.frontImage.isNotBlank()) card.front else
                    itemView.context.getLocalizedString(R.string.front_side)
            tvBack.text =
                if (card.back.isNotBlank() || card.backImage.isNotBlank()) card.back else
                    itemView.context.getLocalizedString(R.string.back_side)

            // set background colors
            if (card.frontColor.isNotBlank())
                front.setBackgroundColor(card.frontColor.toColor())
            else if (defaultColor.isNotBlank())
                front.setBackgroundColor(defaultColor.toColor())
            else
                front.setBackgroundColor(ContextCompat.getColor(itemView.context, card.setId.color()))

            if (card.backColor.isNotBlank())
                back.setBackgroundColor(card.backColor.toColor())
            else if (defaultColor.isNotBlank())
                back.setBackgroundColor(defaultColor.toColor())
            else
                back.setBackgroundColor(ContextCompat.getColor(itemView.context, card.setId.color()))

            // set text colors
            if (card.frontTextColor.isNotBlank())
                tvFront.setTextColor(card.frontTextColor.toColor())
            else
                tvFront.textColorResource = R.color.colorPrimaryText

            if (card.backTextColor.isNotBlank())
                tvBack.setTextColor(card.backTextColor.toColor())
            else
                tvBack.textColorResource = R.color.colorPrimaryText

            // set images
            if (card.frontImage.isNotBlank())
                ivFront.load(card.frontImage)
            else
                ivFront.load("")
            if (card.backImage.isNotBlank())
                ivBack.load(card.backImage)
            else
                ivBack.load("")
        }

        override fun onClick(v: View?) {
            when (v) {
                ivFrontEdit -> {
                    list[adapterPosition].isEditingBack = false
                    onCardClickListener?.onCardEditClick()
                }
                ivBackEdit -> {
                    list[adapterPosition].isEditingBack = true
                    onCardClickListener?.onCardEditClick()
                }
                tvFront -> {
                    list[adapterPosition].isEditingBack = false
                    onCardClickListener?.onCardEditTextClick()
                }
                tvBack -> {
                    list[adapterPosition].isEditingBack = true
                    onCardClickListener?.onCardEditTextClick()
                }
                ivDelete -> {
                    onCardClickListener?.onCardDeleteClick()
                }
                ivFrontFlip -> {
                    flip()
                }
                ivBackFlip -> {
                    flip(true)
                }
            }
        }

        fun flip(reverse: Boolean = false) {
            val anim = FlipAnimation(front, back, LONG_ANIMATION.toInt())
            if (reverse) anim.reverse()
            root.startAnimation(anim)
        }
    }

    var onCardClickListener: OnCardClickListener? = null

    interface OnCardClickListener {
        fun onCardEditClick()
        fun onCardDeleteClick()
        fun onCardEditTextClick()
    }
}