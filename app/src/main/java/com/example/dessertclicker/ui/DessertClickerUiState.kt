package com.example.dessertclicker.ui

import androidx.annotation.DrawableRes

data class DessertClickerUiState(
    @DrawableRes val curImage : Int = 0,
    val curRevenue : Int = 0,
    val dessertsSold : Int = 0

)
