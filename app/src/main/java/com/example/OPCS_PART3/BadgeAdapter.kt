package com.example.OPCS_PART3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class BadgeAdapter(private val badges: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val badgeIcon: ImageView = view.findViewById(R.id.ivBadgeIcon)
        val badgeName: TextView = view.findViewById(R.id.tvBadgeName)
        val badgeDescription: TextView = view.findViewById(R.id.tvBadgeDescription)
        // Fix: Use itemView instead of view.itemView
        val badgeCard: CardView = itemView as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]

        holder.badgeName.text = badge.name
        holder.badgeDescription.text = badge.description

        // Set badge appearance based on unlock status
        if (badge.isUnlocked) {
            // Badge is unlocked - show in full color
            holder.badgeIcon.alpha = 1.0f
            holder.badgeName.alpha = 1.0f
            holder.badgeDescription.alpha = 1.0f
        } else {
            // Badge is locked - show as grayed out
            holder.badgeIcon.alpha = 0.5f
            holder.badgeName.alpha = 0.5f
            holder.badgeDescription.alpha = 0.5f
        }
    }

    override fun getItemCount() = badges.size
}
