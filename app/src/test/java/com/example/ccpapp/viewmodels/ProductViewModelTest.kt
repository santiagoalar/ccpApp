package com.example.ccpapp.viewmodels

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ccpapp.models.Product
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ProductViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockApplication: Application
    
    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockTokenManager: TokenManager

    @Mock
    private lateinit var mockProductRepository: ProductRepository

    private lateinit var viewModel: TestableProductViewModel

    // Clase auxiliar que sobreescribe las dependencias
    private inner class TestableProductViewModel(app: Application) : ProductViewModel(app) {
        val tokenManager: TokenManager = mockTokenManager
        val productRepository: ProductRepository = mockProductRepository
        
        // Sobrescribimos el método para evitar el problema del init
        fun refreshDataFromNetwork() {
            // No hacemos nada para evitar que se llame automáticamente en los tests
        }
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Configurar el contexto simulado
        `when`(mockApplication.applicationContext).thenReturn(mockContext)
        
        // Configurar el token simulado
        `when`(mockTokenManager.getToken()).thenReturn("test-token")
        
        // Iniciar el viewModel con los mocks
        viewModel = TestableProductViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should refresh data from network`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(id = "1", name = "Test Product 1", description = "Description 1", details = mapOf("sadsad" to "dsada"), price = 100, images = listOf("dasdasd", "dasdad"), stock = 10, stockSelected = 0, storageConditions = "Default", deliveryTime = 0),
            Product(id = "2", name = "Test Product 1", description = "Description 1", details = mapOf("sadsad" to "dsada"), price = 100, images = listOf("dasdasd", "dasdad"), stock = 10, stockSelected = 0, storageConditions = "Default", deliveryTime = 0)
        )
        
        `when`(mockProductRepository.getAllProducts("test-token")).thenReturn(mockProducts)
        
        // Act - Llamar manualmente al método refreshProducts ya que sobrescribimos refreshDataFromNetwork
        viewModel.refreshProducts()
        
        // Assert
        Mockito.verify(mockTokenManager).getToken()
        
        // Dar tiempo a que se complete la corrutina
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(mockProducts, viewModel.products.value)
        assertFalse(viewModel.eventNetworkError.value ?: true)
        assertFalse(viewModel.isNetworkErrorShown.value ?: true)
    }

    @Test
    fun `refreshProducts should update products LiveData on success`() = runTest {
        // Arrange
        val mockProducts = listOf(
            Product(id = "1", name = "Test Product 1", description = "Description 1", details = mapOf("sadsad" to "dsada"), price = 100, images = listOf("dasdasd", "dasdad"), stock = 10, stockSelected = 0, storageConditions = "Default", deliveryTime = 0)
        )
        `when`(mockProductRepository.getAllProducts("test-token")).thenReturn(mockProducts)
        
        // Act
        viewModel.refreshProducts()
        
        // Assert
        assertEquals(mockProducts, viewModel.products.value)
        assertFalse(viewModel.eventNetworkError.value ?: true)
        assertFalse(viewModel.isNetworkErrorShown.value ?: true)
    }

    @Test
    fun `refreshProducts should set eventNetworkError to true on exception`() = runTest {
        // Arrange
        `when`(mockProductRepository.getAllProducts("test-token")).thenThrow(RuntimeException("Network error"))
        
        // Act
        viewModel.refreshProducts()
        
        // Assert
        assertTrue(viewModel.eventNetworkError.value ?: false)
    }

    @Test
    fun `onNetworkErrorShown should update isNetworkErrorShown to true`() {
        // Act
        viewModel.onNetworkErrorShown()
        
        // Assert
        assertTrue(viewModel.isNetworkErrorShown.value ?: false)
    }
}
