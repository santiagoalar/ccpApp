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
import com.example.ccpapp.R
import com.example.ccpapp.adapters.CartAdapter
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentShoppingCartBinding
import com.example.ccpapp.models.Product
import com.example.ccpapp.viewmodels.CartItemViewModel
import com.example.ccpapp.viewmodels.ProductViewModel
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.example.ccpapp.adapters.ProductAdapter.CartStorage
import com.example.ccpapp.network.TokenManager

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var viewModelPurchase: CartItemViewModel
    private var navc: NavController? = null
    private val purchasedProducts = mutableListOf<Product>()
    val cartItems = ProductAdapter.CartStorage.getItems().toMutableList()
    private lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)

        binding.buttonBack.setOnClickListener {
            navc?.navigate(R.id.clientFragment)
        }

        binding.buttonCheckout.setOnClickListener {

            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val subtotal:Int = cartItems.sumOf { it.unitPrice * it.quantity }
            val jsonBody = JSONObject().apply {
                put("clientId", tokenManager.getUserId())
                put("quantity", cartItems.size)
                put("subtotal", subtotal)
                put("tax", 50.96)
                put("total", subtotal)
                put("currency", "USD")
                put("clientInfo", JSONObject().apply {
                    put("name", "Cliente 1.1")
                    put("address", "Calle 12 # 11-30")
                    put("email", "info@cliente1.com")
                    put("phone", "3123335566")
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
                navc?.navigate(R.id.clientFragment)
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

        val cartItems = ProductAdapter.CartStorage.getItems().toMutableList()
        val adapter = CartAdapter(cartItems) {
            // Actualizar el total cuando cambie el carrito
            val updatedTotal = ProductAdapter.CartStorage.getItems().sumOf { it.totalPrice }
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
