package digital.upbeat.estisharati_consultant.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import digital.upbeat.estisharati_consultant.DataClassHelper.RecentChat.DataUserFireStore
import digital.upbeat.estisharati_consultant.Fragment.Subscribers
import digital.upbeat.estisharati_consultant.Helper.HelperMethods
import digital.upbeat.estisharati_consultant.R
import digital.upbeat.estisharati_consultant.ViewHolder.CreateGroupViewHolder

class CreateGroupAdapter(val context: Context, val subscribers: Subscribers, val datauserfirestoreArrayList: ArrayList<DataUserFireStore>, val groupMembers: ArrayList<String>) : RecyclerView.Adapter<CreateGroupViewHolder>() {
    val helperMethods: HelperMethods

    init {
        helperMethods = HelperMethods(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateGroupViewHolder {
        val layoutView = LayoutInflater.from(context).inflate(R.layout.group_user_item, parent, false)
        return CreateGroupViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return datauserfirestoreArrayList.size
    }

    override fun onBindViewHolder(holder: CreateGroupViewHolder, position: Int) {
        val dataUserFireStore = datauserfirestoreArrayList.get(position)
        Glide.with(context).load(dataUserFireStore.image).apply(helperMethods.profileRequestOption).into(holder.profile_picture)
        holder.user_name.text = "${dataUserFireStore.fname} ${dataUserFireStore.lname}"
        holder.parent_layout.setOnClickListener {
            if (subscribers.groupMembers.contains(dataUserFireStore.user_id)) {
                subscribers.groupMembers.remove(dataUserFireStore.user_id)
                holder.tick.visibility = View.GONE
            } else {
                subscribers.groupMembers.add(dataUserFireStore.user_id)
                holder.tick.visibility = View.VISIBLE
            }
        }
        if (groupMembers.contains(dataUserFireStore.user_id)) {
            holder.tick.visibility = View.VISIBLE
        } else {
            holder.tick.visibility = View.GONE
        }
    }
}
