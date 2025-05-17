package com.example.ccpapp.ui.seller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.adapters.ProductAdapter
import com.example.ccpapp.databinding.FragmentSellerProductBinding
import com.example.ccpapp.models.Product
import com.example.ccpapp.viewmodels.ProductViewModel

class SellerProductFragment : Fragment() {

    private var _binding: FragmentSellerProductBinding? = null
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
        _binding = FragmentSellerProductBinding.inflate(inflater, container, false)

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
        recyclerView.adapter = viewModelAdapter
        navc = NavController(requireContext())

        viewModel = ProductViewModel(requireActivity().application)
        viewModel.products.observe(viewLifecycleOwner) { products ->
            products?.let {
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

        binding.btnBack.setOnClickListener {
            navc?.navigate(R.id.clientFragment)
        }
    }
}
