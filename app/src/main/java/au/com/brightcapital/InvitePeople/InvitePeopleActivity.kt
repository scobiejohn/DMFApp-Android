package au.com.brightcapital.InvitePeople

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import au.com.brightcapital.R
import com.afollestad.materialdialogs.MaterialDialog

class InvitePeopleActivity : AppCompatActivity() {

    private val TAG = "InvitePeopleActivity"
    private val READ_CONTACTS_PERMISSIONS_REQUEST = 1
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: InvitePeopleAdapter? = null
    private var contacts: ArrayList<Contact>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_people)

        supportActionBar?.title = Html.fromHtml("<font color='#ffffff'>Invite Friends</font>")

        var contactListView = findViewById<RecyclerView>(R.id.contactListView)
        contactListView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        adapter = InvitePeopleAdapter(contacts!!)
        contactListView.layoutManager = layoutManager
        contactListView.itemAnimator = DefaultItemAnimator()
        contactListView.adapter = adapter


        // check permission
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        println(permissionCheck)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getEmailContacts()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), READ_CONTACTS_PERMISSIONS_REQUEST)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.invite_titlebar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        if (id == R.id.action_invite_cancel) {
            finish()
        } else if (id == R.id.action_invite_done) {
            if (adapter?.getSelectedEmails()!!.size == 0) {
                MaterialDialog.Builder(this)
                        .title("Warning")
                        .content("No contact selected")
                        .positiveText("Close")
                        .show()
            } else {
                val intent = Intent(Intent.ACTION_SEND)
                val addressees = adapter?.getSelectedEmails()!!.toTypedArray()
                println(addressees)
                intent.putExtra(Intent.EXTRA_EMAIL, addressees)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Darling Macro Fund")
                intent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nI thought you might be interested in a fund that I am currently invested in that you may find of interest.\nhttps://welcome.darlingmacro.fund")
                intent.type = "message/rfc822"
                startActivity(Intent.createChooser(intent, "Send Email using:"))
            }

        }

        return super.onOptionsItemSelected(item)
    }

    private fun getEmailContacts() {

        val cur = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        var emailIndex = 0
        if (cur.count > 0) {
            //val contactList = ArrayList<Contact>()
            while(cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val emails: Cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID+ " = " + id,
                        null, null)
                while (emails.moveToNext()) {
                    // this would allow you get several email addresses
                    val emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                    if ((!emailAddress.equals("", true)) && (emailAddress.contains("@"))) {
                        contacts?.add(Contact(name, emailAddress, false))
                        emailIndex++
                    }
                }
                emails.close()
            }

            println(contacts)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getEmailContacts()
            } else {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}

data class Contact (val name: String, val email: String, var selected: Boolean = false) {}
