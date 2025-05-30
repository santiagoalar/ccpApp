package com.example.ccpapp.ui.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.ccpapp.R
import com.example.ccpapp.adapters.CartAdapter
import com.example.ccpapp.adapters.ClientAdapter.UserStorage
import com.example.ccpapp.adapters.ProductAdapter.CartStorage
import com.example.ccpapp.databinding.FragmentShoppingCartBinding
import com.example.ccpapp.models.Rol
import com.example.ccpapp.network.TokenManager
import com.example.ccpapp.viewmodels.CartItemViewModel
import com.example.ccpapp.viewmodels.ProductViewModel
import org.json.JSONObject

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var viewModelPurchase: CartItemViewModel
    private var navc: NavController? = null
    val cartItems = CartStorage.getItems().toMutableList()
    val user = UserStorage
    var userId: String = ""
    var userName: String = ""
    var userEmail: String = ""
    var userPhone: String = ""
    var userAddress: String = ""
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)

        binding.buttonBack.setOnClickListener {
            navc?.popBackStack()
        }

        binding.buttonBack.setOnClickListener {
            val role = UserStorage.getZeroUser()?.role?.name
            if (role.equals(Rol.CLIENTE.name)) {
                navc?.navigate(R.id.clientFragment)
            } else if (role.equals(Rol.VENDEDOR.name)) {
                navc?.navigate(R.id.sellerClientProductFragment)
            } else if (role.equals(Rol.TRANSPORTISTA.name)) {
                navc?.navigate(R.id.carrierFragment)
            }
        }

        binding.buttonCheckout.setOnClickListener {

            if (cartItems.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "No hay productos en el carrito",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (UserStorage.getZeroUser()?.role?.name == Rol.CLIENTE.name){
                userId = UserStorage.getZeroUser()?.id.toString()
                userName = UserStorage.getZeroUser()?.name.toString()
                userEmail = UserStorage.getZeroUser()?.email.toString()
                userPhone = UserStorage.getZeroUser()?.phone.toString()
                userAddress =  "Calle 12 # 11-30"
            } else {
                userId = UserStorage.getTempUser()?.clientId.toString()
                userName = UserStorage.getTempUser()?.clientName.toString()
                userEmail = UserStorage.getTempUser()?.clientEmail.toString()
                userPhone = UserStorage.getTempUser()?.clientPhone.toString()
                userAddress = UserStorage.getTempUser()?.address.toString()
            }

            val subtotal: Int = cartItems.sumOf { it.unitPrice * it.quantity }
            val totalQuantity = cartItems.sumOf { it.quantity }
            val jsonBody = JSONObject().apply {
                put("clientId", userId)
                put("quantity", totalQuantity)
                put("subtotal", subtotal)
                put("tax", 50.96)
                put("total", subtotal)
                put("currency", "USD")
                put("clientInfo", JSONObject().apply {
                    put("name", userName)
                    put("address", userAddress)
                    put("email", userEmail)
                    put("phone", userPhone)
                })
                put("payment", JSONObject().apply {
                    put("amount", subtotal)
                    put("cardNumber", "4111111111111111")
                    put("cvv", "123")
                    put("expiryDate", "12/25")
                    put("currency", "USD")
                })
                put("orderDetails", cartItems.fold(org.json.JSONArray()) { array, product ->
                    array.put(JSONObject().apply {
                        put("productId", product.id)
                        put("quantity", product.quantity)
                        put("unitPrice", product.unitPrice)
                        put("totalPrice", product.totalPrice)
                        put("currency", "USD")
                    })
                })
            }
            Log.d(":::::::::COMPRA::::::::", jsonBody.toString())
            viewModelPurchase.savePurchase(jsonBody)

            binding.successLayout.isVisible = true
            binding.recyclerViewCartItems.isVisible = false
            binding.cartSummaryLayout.isVisible = false

            Glide.with(this)
                .asGif()
                .load("https://media.tenor.com/bm8Q6yAlsPsAAAAj/verified.gif")
                .into(binding.imageSuccess)

            binding.buttonAccept.setOnClickListener {
                CartStorage.clearCart()
                val role = UserStorage.getZeroUser()?.role?.name ?: "VENDEDOR"
                if (role.equals(Rol.CLIENTE.name)) {
                    navc?.navigate(R.id.clientFragment)
                } else if (role.equals(Rol.VENDEDOR.name)) {
                    navc?.navigate(R.id.sellerClientProductFragment)
                } else if (role.equals(Rol.TRANSPORTISTA.name)) {
                    navc?.navigate(R.id.carrierFragment)
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tokenManager = TokenManager(requireContext())

        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        viewModel = ViewModelProvider(
            this,
            ProductViewModel.Factory(activity.application)
        )[ProductViewModel::class.java]

        viewModelPurchase = ViewModelProvider(
            this,
            CartItemViewModel.Factory(activity.application)
        )[CartItemViewModel::class.java]

        navc = view.findNavController()

        val cartItems = CartStorage.getItems().toMutableList()
        val adapter = CartAdapter(cartItems) {
            val updatedTotal = CartStorage.getItems().sumOf { it.totalPrice }
            binding.textTotal.text = "Total: $$updatedTotal"
        }
        binding.recyclerViewCartItems.adapter = adapter

        val totalQuantity = cartItems.sumOf { it.totalPrice }
        binding.textTotal.text = "Total: $$totalQuantity"

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        viewModelPurchase.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}
