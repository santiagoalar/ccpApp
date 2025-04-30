package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Product
import com.example.ccpapp.network.NetworkServiceAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ProductRepositoryTest {

    @Mock
    private lateinit var mockApplication: Application
    
    @Mock
    private lateinit var mockNetworkServiceAdapter: NetworkServiceAdapter
    
    private lateinit var productRepository: ProductRepository
    
    @Before
    fun setUp() {
        // Configurar el mock de NetworkServiceAdapter
        NetworkServiceAdapter.setInstanceForTesting(mockNetworkServiceAdapter)
        
        // Inicializar el repositorio
        productRepository = ProductRepository(mockApplication)
    }
    
    @After
    fun tearDown() {
        // Restablecer la instancia original
        NetworkServiceAdapter.setInstanceForTesting(null)
    }
    
    @Test
    fun `getAllProducts should return products from network service`() = runTest {
        // Arrange
        val token = "test-token"
        val expectedProducts = listOf(
            Product(
                id = "1",
                name = "Producto de Prueba",
                description = "Descripción del producto",
                details = mapOf("origen" to "Colombia", "tipo" to "Café"),
                price = 15000,
                images = listOf("imagen1.jpg", "imagen2.jpg"),
                stock = 100,
                stockSelected = 0,
                storageConditions = "En lugar fresco y seco",
                deliveryTime = 2
            ),
            Product(
                id = "2",
                name = "Otro Producto",
                description = "Otra descripción",
                details = mapOf("origen" to "Ecuador", "tipo" to "Chocolate"),
                price = 25000,
                images = listOf("imagen3.jpg", "imagen4.jpg"),
                stock = 50,
                stockSelected = 0,
                storageConditions = "En lugar refrigerado",
                deliveryTime = 3
            )
        )
        
        `when`(mockNetworkServiceAdapter.getProducts(token)).thenReturn(expectedProducts)
        
        // Act
        val result = productRepository.getAllProducts(token)
        
        // Assert
        assertEquals(expectedProducts, result)
        verify(mockNetworkServiceAdapter).getProducts(token)
    }
    
    @Test
    fun `getAllProducts should handle empty product list`() = runTest {
        // Arrange
        val token = "test-token"
        val expectedProducts = emptyList<Product>()
        
        `when`(mockNetworkServiceAdapter.getProducts(token)).thenReturn(expectedProducts)
        
        // Act
        val result = productRepository.getAllProducts(token)
        
        // Assert
        assertEquals(expectedProducts, result)
        assertEquals(0, result.size)
        verify(mockNetworkServiceAdapter).getProducts(token)
    }
    
    @Test
    fun `getAllProducts should propagate exceptions from network service`() = runTest {
        // Arrange
        val token = "test-token"
        val expectedException = RuntimeException("Error de red simulado")
        
        `when`(mockNetworkServiceAdapter.getProducts(token)).thenThrow(expectedException)
        
        // Act & Assert
        try {
            productRepository.getAllProducts(token)
            // Si llegamos aquí, la prueba falla
            assert(false) { "Se esperaba una excepción pero no se lanzó ninguna" }
        } catch (e: Exception) {
            // Verificar que se propaga la misma excepción
            assertEquals(expectedException, e)
        }
        
        // Verificar que se llamó al método
        verify(mockNetworkServiceAdapter).getProducts(token)
    }
}
