package com.example.b_trak

data class Bike(val name: String, val type: String)

object GarageManager {
    val myGarage = mutableListOf<Bike>()
}