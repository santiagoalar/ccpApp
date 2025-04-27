package com.example.ccpapp.ui.client

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel
    private lateinit var viewModelPurchase: CartItemViewModel
    private var navc: NavController? = null
    private val purchasedProducts = mutableListOf<Product>()
    val cartItems = ProductAdapter.CartStorage.getItems().toMutableList()

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
            val jsonBody = JSONObject().apply {
                put("clientId", "af8f7966-312f-4678-a829-309181dbdeea")
                put("quantity", 3)
                put("subtotal", 339.7)
                put("tax", 50.96)
                put("total", 390.66)
                put("currency", "USD")
                put("clientInfo", JSONObject().apply {
                    put("name", "Cliente 1.1")
                    put("address", "Calle 12 # 11-30")
                    put("email", "info@cliente1.com")
                    put("phone", "3123335566")
                })
                put("payment", JSONObject().apply {
                    put("amount", 390.66)
                    put("cardNumber", "4111111111111111")
                    put("cvv", "123")
                    put("expiryDate", "12/25")
                    put("currency", "USD")
                })
                put("orderDetails", cartItems.map { product ->
                    mapOf(
                        "productId" to product.id,
                        "quantity" to product.quantity,
                        "unitPrice" to product.unitPrice,
                        "totalPrice" to product.totalPrice,
                        "currency" to "USD"
                    )
                })
            }

            viewModelPurchase.savePurchase(jsonBody)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
