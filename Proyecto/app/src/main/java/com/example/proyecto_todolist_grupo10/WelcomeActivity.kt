package com.example.proyecto_todolist_grupo10

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_todolist_grupo10.model.Item
import com.example.proyecto_todolist_grupo10.model.Lists
import com.example.proyecto_todolist_grupo10.model.Users
import com.example.proyecto_todolist_grupo10.networking.ApiApi
import com.example.proyecto_todolist_grupo10.networking.ApiService
import kotlinx.android.synthetic.main.activity_welcome.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.FileOutputStream
import java.io.ObjectOutputStream


class WelcomeActivity : AppCompatActivity() {

    companion object{

        var loginuser: Users = Users("","","","","","",ArrayList<Lists>())
        var temp_listas = ArrayList<Lists>()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val temper : Users? = intent.getSerializableExtra("logUser") as? Users

        val tempList = intent.getSerializableExtra("newList") as? ArrayList<Lists>


        if(temper !=  null){
            loginuser = temper
        }
        else{
            println("entra aqui hgablando como shoro kjiee tu vas al rancho puro mochila")
            val item1 = Item("item1", 0, 0, "Bueno, esta es la nota generada automaticamente al crear la lista", "25/12/2020",0)
            val item2 = Item("item2", 0, 0, "Bueno, esta es la nota generada automaticamente al crear la lista", "25/12/2020",0)
            var items = ArrayList<Item>()
            items.add(item1)
            items.add(item2)
            val lista_usuario = Lists(items, "lista1", 0, itemsComplete = ArrayList<Item>())
            var listas_usuario= ArrayList<Lists>()
            listas_usuario.add(lista_usuario)



            println(loginuser.toString())
            loginuser!!.UsersLists = listas_usuario
        }

        if (savedInstanceState != null) {
            loginuser = savedInstanceState.getSerializable("user_log") as Users
        }

        if(tempList != null){
            loginuser!!.UsersLists = tempList
        }

        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()

        btToNewList.setOnClickListener {
            startActivityForResult(CreateList.newInstance(this), 1)
        }

        temp_listas = loginuser!!.UsersLists
        recycler_view.adapter = HistoricAdapter1(temp_listas)
        recycler_view.layoutManager = LinearLayoutManager(this)

        (recycler_view.adapter as HistoricAdapter1).setDataset(temp_listas)

        val ivUsers = findViewById<ImageView>(R.id.imageviewUser)
        val userPhotoId = this.resources.getIdentifier("descarga", "drawable", packageName)
        ivUsers.setImageResource(userPhotoId)
        val twName = findViewById<TextView>(R.id.twnameuser)
        twName.text = loginuser!!.name

        var newList: ArrayList<Lists> = (recycler_view.adapter as HistoricAdapter1).getDataset() as ArrayList<Lists>

        loginuser!!.UsersLists = newList




        //val fis: FileInputStream = applicationContext.openFileInput("usuarios")
        //val sis = ObjectInputStream(fis)
        //loginuser = sis.readObject() as Users
        //sis.close()
        //fis.close()


    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putSerializable("user_log", loginuser)
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( resultCode == Activity.RESULT_OK) {
            data?.apply {
                var list = getSerializableExtra(CreateList.LIST) as Lists
                temp_listas.add(list!!)
                recycler_view.adapter?.notifyDataSetChanged()
                loginuser!!.UsersLists = temp_listas

            }
        }

    }

    override fun onPause() {
        super.onPause()
        val fos: FileOutputStream =
            applicationContext.openFileOutput("usuarios", Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        os.writeObject(loginuser)
        os.close()
        fos.close()
    }

    fun onLogout(view: View){
        var intent = Intent(this,MainActivity::class.java)
        intent.putExtra("usuario_conect",loginuser)
        startActivity(intent)
    }

}


