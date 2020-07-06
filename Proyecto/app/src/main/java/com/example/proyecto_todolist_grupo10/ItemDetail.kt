package com.example.proyecto_todolist_grupo10

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import com.example.proyecto_todolist_grupo10.model.Item
import com.example.proyecto_todolist_grupo10.model.Lists
import com.example.proyecto_todolist_grupo10.model.Users
import com.example.proyecto_todolist_grupo10.util.LocationUtil
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.custom_dialog_changenote.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class ItemDetail : AppCompatActivity() {

    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        var ItemRecive : Item= Item("", 0, 0,"", LocalDate.now().plusDays(30),LocalDate.now(),0,0.0,0.0)
        var loguser : Users? = null
        var tempList1 : Lists? = null
        var cal: Calendar = Calendar.getInstance()

        lateinit var mMap: GoogleMap
        lateinit var locationData: LocationUtil
        var LOCATION_PERMISSION = 100



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        supportActionBar?.hide()

        imageViewPrioridadItem.visibility = View.GONE


        ItemRecive = intent.getSerializableExtra("Item") as Item
        loguser = intent.getSerializableExtra("user_log") as Users
        tempList1 = intent.getSerializableExtra("Lists_usurer") as Lists

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap
        }
        invokeLocationAction(ItemRecive)

        //seteo de variables
        twNameItem.text = ItemRecive.name
        if(ItemRecive.prioridad==1){
            imageViewPrioridadItem.visibility = View.VISIBLE
        }
        if(ItemRecive.prioridad==0){
            imageViewPrioridadItem.visibility = View.GONE
        }
        twFechaItem.setText(ItemRecive.plazo.toString())
        twNotasItem.text = ItemRecive.notas
        fechacreacion.text = "Creado el "+ItemRecive.create_at.toString()

        //edicion de fecha lo veremos en un rato

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        twFechaItem.setOnClickListener{
            val picker = DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            picker.show()


        }





        //edicion de prioridad
        lyPrioridad.setOnClickListener{
            var counter = 0
            if(ItemRecive.prioridad == 0 && counter == 0){
                imageViewPrioridadItem.visibility = View.VISIBLE
               ItemRecive.prioridad = 1
                counter = 1
                println("prioridad del item cambio correctamente "+ItemRecive.name+" cambio a prioridad: "+ ItemRecive.prioridad)
            }
            if(ItemRecive.prioridad == 1 && counter == 0){
                imageViewPrioridadItem.visibility = View.GONE
                ItemRecive.prioridad = 0
                println("prioridad del item cambio correctamente "+ItemRecive.name+" cambio a prioridad: "+ ItemRecive.prioridad)
                counter = 1
            }
        }

        ContainerNotas.setOnClickListener{
            val dialog = Dialog(it.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog_changenote)
            val cancelButton: Button = dialog.findViewById(R.id.btnCancel1) as Button
            val confirmButton: Button = dialog.findViewById(R.id.btnConfirm1) as Button
            cancelButton.setOnClickListener(View.OnClickListener { dialog.dismiss() })
            confirmButton.setOnClickListener(View.OnClickListener {
                val NewNote: String = dialog.etNewNote.text.toString()
                ItemRecive.notas = NewNote
                dialog.dismiss()
                finish()
                startActivity(intent)
            })
            dialog.show()
        }

        btnDeleteItem.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            builder.setCancelable(false)
            builder.setTitle("Alerta")
            builder.setMessage("Desea Eliminar?")

            builder.setPositiveButton(android.R.string.yes){ _, _ ->
                var index1 = 0
                var tempitem : Item = Item("",0, 0, "",LocalDate.now().plusDays(30),LocalDate.now(),0,0.0,0.0)
                tempList1!!.items.forEach{ it1 ->
                    if(it1.name == ItemRecive.name) {
                        tempitem = it1
                        return@forEach
                    }
                    index1 += 1
                }
                tempList1!!.items.remove(tempitem)

                var intent = Intent(this,ToDoActivity::class.java)
                intent.putExtra("user_log", loguser)
                intent.putExtra("List", tempList1)
                startActivity(intent)
            }
            builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()

        }

        btnCompleteItem.setOnClickListener{
            ItemRecive.estado = 1
            Toast.makeText(it.context, "Item completado existosamente", Toast.LENGTH_SHORT).show()
            var index1 = 0
            tempList1!!.items.forEach{ it1 ->
                if(it1.name == ItemRecive.name) {
                    tempList1!!.items[index1] = ItemRecive
                    return@forEach
                }
                index1 += 1
            }

            var intent = Intent(this,ToDoActivity::class.java)
            intent.putExtra("user_log", loguser)
            intent.putExtra("List", tempList1)
            startActivity(intent)
        }




        println(loguser!!.name)
        println(tempList1!!.items[0].name)

        btLogout1.setOnClickListener{
            var index1 = 0
            tempList1!!.items.forEach{
                if(it.name == ItemRecive.name) {
                   tempList1!!.items[index1] = ItemRecive
                    return@forEach
                }
                index1 += 1
            }

            var intent = Intent(this,ToDoActivity::class.java)
            intent.putExtra("user_log", loguser)
            intent.putExtra("List", tempList1)
            startActivity(intent)
        }

    }

    private fun invokeLocationAction(item: Item) {


            when {
                isPermissionsGranted() -> locationData.observe(this, Observer {
                    println("${item.latitude} , ${item.longitude}")

                    MainActivity.fusedLocationClient1 = LocationServices.getFusedLocationProviderClient(this)


                    val polylineOptions = PolylineOptions().clickable(false).color(R.color.colorAccent).geodesic(true)
                        .width(10f)

                    mMap.addMarker(MarkerOptions().position(LatLng(item.latitude, item.longitude)).title(item.name))
                    polylineOptions.add(LatLng(item.latitude,item.longitude))
                    mMap.addPolyline(polylineOptions)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(item.latitude,item.longitude), 12.0f))

                })


                shouldShowRequestPermissionRationale() -> println("Ask Permission")

                else -> ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION
                )
            }
        }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION -> {
                invokeLocationAction(ItemRecive)
            }
        }
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        twFechaItem!!.setText(sdf.format(cal.time))

        if (LocalDate.now() < LocalDate.parse(twFechaItem.text.toString())) {
            ItemRecive.plazo = LocalDate.parse(twFechaItem.text.toString())
            Toast.makeText(this, "Fecha de plazo cambiada correctamente", Toast.LENGTH_SHORT).show()
        }
        else{
            twFechaItem!!.setText(ItemRecive.plazo.toString())
            Toast.makeText(this, "no se pudo crear el nuevo plazo ya que es anterior a la fecha de hoy", Toast.LENGTH_SHORT).show()
        }

    }
}