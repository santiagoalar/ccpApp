package com.example.ccpapp.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.PurchaseItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CartItemViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var tokenManager: TokenManager

    @Mock
    private lateinit var purchaseItemsRepository: PurchaseItemsRepository

    private lateinit var cartItemViewModel: CartItemViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        cartItemViewModel = CartItemViewModel(application)
        cartItemViewModel.apply {
            // Inyectar mocks
            this::class.java.getDeclaredField("tokenManager").apply {
                isAccessible = true
                set(cartItemViewModel, tokenManager)
            }
            this::class.java.getDeclaredField("purchaseItemsRepository").apply {
                isAccessible = true
                set(cartItemViewModel, purchaseItemsRepository)
            }
        }
    }

    @Test
    fun `savePurchase should call repository with correct token and purchase`() = runBlocking {
        // Arrange
        val token = "mockToken"
        val purchase = JSONObject().apply {
            put("key", "value")
        }
        `when`(tokenManager.getToken()).thenReturn(token)

        // Act
        cartItemViewModel.savePurchase(purchase)

        // Assert
        verify(tokenManager).getToken()
        verify(purchaseItemsRepository).savePurchase(token, purchase)
    }

    @Test
    fun `savePurchase should handle exceptions gracefully`() = runBlocking {
        // Arrange
        val token = "mockToken"
        val purchase = JSONObject().apply {
            put("key", "value")
        }
        `when`(tokenManager.getToken()).thenReturn(token)
        `when`(
            purchaseItemsRepository.savePurchase(
                token,
                purchase
            )
        ).thenThrow(RuntimeException("Mock exception"))

        // Act
        cartItemViewModel.savePurchase(purchase)

        // Assert
        verify(tokenManager).getToken()
        verify(purchaseItemsRepository).savePurchase(token, purchase)
    }
}
