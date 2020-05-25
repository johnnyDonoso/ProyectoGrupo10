package com.example.proyecto_todolist_grupo10

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_todolist_grupo10.model.Users
import com.example.proyecto_todolist_grupo10.networking.ApiApi
import com.example.proyecto_todolist_grupo10.networking.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object{
        var logUser : Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val user: Users? = intent.getSerializableExtra("usuario_conect") as? Users

        if (user != null){
            logUser = user
        }

        val request = ApiService.buildService(ApiApi::class.java)

        val call = request.getUser()
        call.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val users =  response.body()!!
                        WelcomeActivity.loginuser.email = users.email
                        WelcomeActivity.loginuser.name = users.name
                        WelcomeActivity.loginuser.phone = users.phone
                        WelcomeActivity.loginuser.last_name = users.last_name
                        WelcomeActivity.loginuser.profile_photo = users.profile_photo
                    }
                }
            }
            override fun onFailure(call: Call<Users>, t: Throwable) {
                Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }

    fun onLogin(view: View){
        val email: String = etMail.text.toString()
        val pass: String = etPassword.text.toString()
        if(logUser == null){
            var intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }
        else{
            var intent = Intent(this,WelcomeActivity::class.java)
            intent.putExtra("logUser",logUser)
            startActivity(intent)

        }

    }

}