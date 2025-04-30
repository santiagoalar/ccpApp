package com.example.ccpapp.repositories

import android.app.Application
import com.example.ccpapp.models.Rol
import com.example.ccpapp.models.TokenInfo
import com.example.ccpapp.models.User
import com.example.ccpapp.network.NetworkServiceAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var mockApplication: Application

    @Mock
    private lateinit var mockNetworkServiceAdapter: NetworkServiceAdapter
    
    // Crear JSONObjects preconfigurados para evitar llamadas al método put
    private val mockUserJson = JSONObject()
    private val mockAuthUserJson = JSONObject()

    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Configura el mock para NetworkServiceAdapter
        NetworkServiceAdapter.setInstanceForTesting(mockNetworkServiceAdapter)
        
        // Inicializa el repositorio con la aplicación mock
        userRepository = UserRepository(mockApplication)
    }

    @After
    fun tearDown() {
        // Restablece la instancia para otros tests
        NetworkServiceAdapter.setInstanceForTesting(null)
        Dispatchers.resetMain()
    }

    @Test
    fun `authUser should return TokenInfo when authentication is successful`() = runTest {
        // Arrange
        val authJson = JSONObject().apply {
            put("email", "test@example.com")
            put("password", "password123")
        }
        val expectedTokenInfo = TokenInfo("test-token", "refresh-token", "3600")
        `when`(mockNetworkServiceAdapter.authUser(authJson)).thenReturn(expectedTokenInfo)

        // Act
        val result = userRepository.authUser(authJson)

        // Assert
        assert(result == expectedTokenInfo)
        verify(mockNetworkServiceAdapter, times(1)).authUser(authJson)
    }

    
    @Test
    fun `validateToken should return User when token is valid`() = runTest {
        // Arrange
        val token = "test-token"
        val expectedUser = User(
            id = "1",
            name = "Test User",
            email = "test@example.com",
            password = "",
            phone = "333232",
            role = Rol.CLIENTE
        )
        
        `when`(mockNetworkServiceAdapter.getUser(token)).thenReturn(expectedUser)
        
        // Act
        val result = userRepository.validateToken(token)
        
        // Assert
        assert(result == expectedUser)
        verify(mockNetworkServiceAdapter, times(1)).getUser(token)
    }
    
    @Test
    fun `getAllClients should return list of clients when request is successful`() = runTest {
        // Arrange
        val token = "test-token"
        val sellerId = "seller-123"
        val expectedClients = listOf(
            User(
                id = "1",
                name = "Client 1",
                email = "client1@example.com",
                password = "",
                phone = "31245454",
                role = Rol.CLIENTE
            ),
            User(
                id = "2",
                name = "Client 2",
                email = "client2@example.com",
                password = "",
                phone = "31245454",
                role = Rol.CLIENTE
            )
        )
        
        `when`(mockNetworkServiceAdapter.getClients(token, sellerId)).thenReturn(expectedClients)
        
        // Act
        val result = userRepository.getAllClients(token, sellerId)
        
        // Assert
        assert(result == expectedClients)
        assert(result.size == expectedClients.size)
        verify(mockNetworkServiceAdapter, times(1)).getClients(token, sellerId)
    }
}
