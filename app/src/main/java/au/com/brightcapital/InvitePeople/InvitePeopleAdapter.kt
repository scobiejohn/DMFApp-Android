package au.com.brightcapital.InvitePeople

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import au.com.brightcapital.R

class InvitePeopleAdapter(private val contacts: ArrayList<Contact>) : RecyclerView.Adapter<InvitePeopleAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var emailTV: TextView
        var selectedCB: CheckBox

        init {
            emailTV = view.findViewById<TextView>(R.id.contactEmail) as TextView
            selectedCB = view.findViewById<CheckBox>(R.id.contactSelectedCB) as CheckBox
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.contact_list_row, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder?, position: Int) {
        val contact = contacts[position]
        holder?.emailTV?.text = contact.email

        holder?.selectedCB?.setOnCheckedChangeListener(null)
        // TODO: based on data
        holder?.selectedCB?.isChecked = contact.selected

        holder?.selectedCB?.setOnCheckedChangeListener{ _, isChecked ->
            updateContactListWithSelectState(position, isChecked)
        }

    }

    private fun updateContactListWithSelectState(position: Int, selected: Boolean) {
        contacts[position].selected = selected
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    fun getSelectedEmails(): ArrayList<String> {
        var emails = ArrayList<String>(0)
        (0 until contacts.size)
                .filter { contacts[it].selected }
                .mapTo(emails) { contacts[it].email }

        return  emails
    }
}