package au.com.brightcapital.utils

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            afterTextChanged.invoke(p0.toString())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}

fun Activity.alert(title: String, message: String, cancellable: Boolean = false): MaterialDialog {
    val alert = MaterialDialog.Builder(this)
            .autoDismiss(true)
            .title(title)
            .content(message)
            .negativeText("Close")
            .show()
    alert.setCancelable(cancellable)

    return alert
}

fun Activity.processing(title: String): MaterialDialog {
    val dialog = MaterialDialog.Builder(this)
            .autoDismiss(false)
            .title(title)
            .progress(true, 0)
            .show()
    dialog.setCancelable(false)

    return dialog
}

fun hideSoftKeyBoard(activity: Activity) {
    try {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
    } catch (err: Exception){}

}