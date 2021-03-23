package si.uni_lj.fri.pbd.miniapp1.ui.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import si.uni_lj.fri.pbd.miniapp1.R

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.CardViewHolder>() {

    var contacts = ArrayList<ContactDTO>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = contacts.size

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTV: TextView = itemView.findViewById(R.id.name)
        var checkboxTV: CheckBox = itemView.findViewById(R.id.checkbox)

        init {
            itemView.setOnClickListener { onItemClick() }
            checkboxTV.setOnClickListener { onItemClick() }
        }

        private fun onItemClick() {
            val contact = contacts[adapterPosition]
            contact.toggleIsSelected()
            checkboxTV.isChecked = contact.isSelected
        }

        fun bind(contact: ContactDTO) {
            nameTV.text = contact.name
            checkboxTV.isChecked = contact.isSelected
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.contact_list_item, viewGroup, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(contacts[position])
    }
}
