package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.adapters.CartAdapter
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentShoppingCartBinding
import com.example.ccpapp.models.Product
import com.example.ccpapp.viewmodels.ProductViewModel

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProductViewModel
    //private var viewModelAdapter: ProductAdapter? = null
    private var navc: NavController? = null
    private val purchasedProducts = mutableListOf<Product>()
    val cartItems = ProductAdapter.CartStorage.getItems()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        /*viewModelAdapter = ProductAdapter{ product, quantity ->
            if (quantity > 0) {
                purchasedProducts.add(product)
            }
        }*/

        binding.buttonBack.setOnClickListener{
            navc?.navigate(R.id.clientFragment)
        }

        binding.buttonCheckout.setOnClickListener{
            
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        viewModel = ViewModelProvider(
            this,
            ProductViewModel.Factory(activity.application)
        )[ProductViewModel::class.java]

        navc = Navigation.findNavController(view)
        //observeAuthUserResult(view)

        val cartItems = ProductAdapter.CartStorage.getItems()
        val adapter = CartAdapter(cartItems)
        binding.recyclerViewCartItems.adapter = adapter

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
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