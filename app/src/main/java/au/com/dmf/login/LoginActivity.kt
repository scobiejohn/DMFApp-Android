package au.com.dmf.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import au.com.dmf.MainActivity
import au.com.dmf.R
import au.com.dmf.model.User
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        submitBtn.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)

            //User("1234", "6789", 1111, "ray@mail.com", "Raymond").save()
        })

        readButton.setOnClickListener({
            val firstUser = User().queryFirst()
            print(firstUser)
        })

    }

}
