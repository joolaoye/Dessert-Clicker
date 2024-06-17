package com.example.dessertclicker.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.R
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertClickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DessertClickerUiState())
    val uiState : StateFlow<DessertClickerUiState>
        get() = _uiState.asStateFlow()

    private val dessertList = Datasource.dessertList

    var dessertsSold = 0
        private set

    var currentDessertPrice = dessertList[dessertsSold].price
        private set

    var currentDessertImageId = dessertList[dessertsSold].imageId
        private set

    var revenue = 0
        private set

    fun determineDessertToShow(
        desserts: List<Dessert>,
        dessertsSold: Int
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

    fun updateRevenue() {
        revenue += currentDessertPrice
        dessertsSold++
    }

    fun showNextDessert() {
        val dessertToShow = determineDessertToShow(dessertList, dessertsSold)
        currentDessertImageId = dessertToShow.imageId
        currentDessertPrice = dessertToShow.price
    }

    fun updateUiState() {
        _uiState.update {
                currentState ->
            currentState.copy(
                curImage = currentDessertImageId,
                curRevenue = revenue,
                dessertsSold = dessertsSold
            )
        }
    }

    fun onClickDessert() {
        updateRevenue()

        showNextDessert()

        updateUiState()
    }

    fun shareSoldDessertsInformation(intentContext: Context, dessertsSold: Int, revenue: Int) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                intentContext.getString(R.string.share_text, dessertsSold, revenue)
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)

        try {
            ContextCompat.startActivity(intentContext, shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                intentContext,
                intentContext.getString(R.string.sharing_not_available),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    init {
        updateUiState()
    }
}