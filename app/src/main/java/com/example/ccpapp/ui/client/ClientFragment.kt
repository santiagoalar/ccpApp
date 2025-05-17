package com.example.ccpapp.ui.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentClientBinding
import com.example.ccpapp.models.Product
import com.example.ccpapp.viewmodels.ProductViewModel

class ClientFragment : Fragment() {

    private var _binding: FragmentClientBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ProductViewModel
    private var viewModelAdapter: ProductAdapter? = null
    private var navc: NavController? = null
    private val purchasedProducts = mutableListOf<Product>()
    val cartItems = ProductAdapter.CartStorage.getItems()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientBinding.inflate(inflater, container, false)

        viewModelAdapter = ProductAdapter { product, quantity ->
            if (quantity > 0) {
                val price = product.price
                purchasedProducts.add(product)
                binding.cartBadge.visibility = View.VISIBLE
            }
        }

        binding.buttonCart.setOnClickListener {
            navc?.navigate(R.id.shoppingCartFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewProducts
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = viewModelAdapter
        navc = Navigation.findNavController(view)

        viewModel = ViewModelProvider(
            this,
            ProductViewModel.Factory(requireActivity().application)
        ).get(ProductViewModel::class.java)

        viewModel.eventNetworkError.observe(viewLifecycleOwner) { isNetworkError ->
            if (isNetworkError) onNetworkError()
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            products?.let {
                if (it.isEmpty()) {
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerViewProducts.visibility = View.GONE
                } else {
                    binding.emptyView.visibility = View.GONE
                    binding.recyclerViewProducts.visibility = View.VISIBLE
                    val updatedProducts = it.map { product ->
                        val matchingProductSelected = cartItems.find { selected ->
                            selected.id == product.id
                        }
                        product.copy(
                            stockSelected = matchingProductSelected?.quantity ?: 0
                        )
                    }
                    viewModelAdapter?.products = updatedProducts
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
        /*if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }*/
    }

}