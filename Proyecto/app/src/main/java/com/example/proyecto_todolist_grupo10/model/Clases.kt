package com.example.proyecto_todolist_grupo10.model

import java.io.Serializable

data class Users(val email: String, val pass: String, val name: String, var UsersLists: ArrayList<Lists>):
    Serializable {}

data class Lists(var items: ArrayList<Item>, val name: String, val position: Int): Serializable {}

data class Item(var name: String): Serializable {}