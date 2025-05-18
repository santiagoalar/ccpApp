package com.example.ccpapp.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentSellerClientProductBinding
import com.example.ccpapp.models.Product
import com.example.ccpapp.viewmodels.ProductViewModel
import com.example.ccpapp.viewmodels.UserViewModel

class SellerClientProductFragment : Fragment() {

    private var _binding: FragmentSellerClientProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModelAdapter: ProductAdapter
    private var navc: NavController? = null
    val cartItems = ProductAdapter.CartStorage.getItems()
    private val purchasedProducts = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerClientProductBinding.inflate(inflater, container, false)

        viewModelAdapter = ProductAdapter { product, quantity ->
            if (quantity > 0) {
                purchasedProducts.add(product)
                binding.cartBadge.visibility = View.VISIBLE
            }
        }

        binding.buttonCart.setOnClickListener {
            navc?.navigate(R.id.shoppingCartFragment)
        }

        binding.btnBack.setOnClickListener {
            navc?.navigate(R.id.sellerFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewProducts
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewModelAdapter
        navc = view.findNavController()

        viewModel = ViewModelProvider(
            this,
            ProductViewModel.Factory(requireActivity().application)
        ).get(ProductViewModel::class.java)

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            products?.let {
                if (it.isEmpty()) {
                    binding.tvEmptyProducts.visibility = View.VISIBLE
                    binding.recyclerViewProducts.visibility = View.GONE
                } else {
                    binding.tvEmptyProducts.visibility = View.GONE
                    binding.recyclerViewProducts.visibility = View.VISIBLE
                    val updatedProducts = it.map { product ->
                        val matchingProductSelected = cartItems.find { selected ->
                            selected.id == product.id
                        }
                        product.copy(
                            stockSelected = matchingProductSelected?.quantity ?: 0
                        )
                    }
                    viewModelAdapter.products = updatedProducts
                }
            }
            binding.progressBar.visibility = View.GONE
        }
        viewModel.refreshProducts()

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
