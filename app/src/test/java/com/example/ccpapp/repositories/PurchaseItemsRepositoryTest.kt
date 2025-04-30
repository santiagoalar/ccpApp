package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.network.NetworkServiceAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class PurchaseItemsRepositoryTest {

    @Mock
    private lateinit var mockApplication: Application
    
    @Mock
    private lateinit var mockNetworkServiceAdapter: NetworkServiceAdapter
    
    @Mock
    private lateinit var mockJsonObject: JSONObject
    
    private lateinit var purchaseItemsRepository: PurchaseItemsRepository
    
    @Before
    fun setUp() {
        // Configurar el mock de NetworkServiceAdapter
        NetworkServiceAdapter.setInstanceForTesting(mockNetworkServiceAdapter)
        
        // Inicializar el repositorio
        purchaseItemsRepository = PurchaseItemsRepository(mockApplication)
    }
    
    @After
    fun tearDown() {
        // Restablecer la instancia original
        NetworkServiceAdapter.setInstanceForTesting(null)
    }
    
    @Test
    fun `savePurchase should call postCartItems on network service adapter`() = runTest {
        // Arrange
        val token = "test-token"
        
        // Act
        purchaseItemsRepository.savePurchase(token, mockJsonObject)
        
        // Assert
        verify(mockNetworkServiceAdapter).postCartItems(token, mockJsonObject)
    }
    
    @Test(expected = RuntimeException::class)
    fun `savePurchase should propagate exceptions from network service`() = runTest {
        // Arrange
        val token = "test-token"
        val expectedException = RuntimeException("Error al enviar compra simulado")
        
        // Configurar el mock para lanzar una excepción
        doThrow(expectedException).`when`(mockNetworkServiceAdapter).postCartItems(token, mockJsonObject)
        
        // Act - Esto debería lanzar la excepción esperada
        purchaseItemsRepository.savePurchase(token, mockJsonObject)
    }
}
