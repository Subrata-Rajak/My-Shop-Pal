package subrata.project.android.myshoppal.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import subrata.project.android.myshoppal.R
import subrata.project.android.myshoppal.adapters.CartItemsListAdapter
import subrata.project.android.myshoppal.databinding.ActivityCartListBinding
import subrata.project.android.myshoppal.models.Cart
import subrata.project.android.myshoppal.models.Product
import subrata.project.android.myshoppal.utils.FirestoreClass

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<Cart>
    private lateinit var binding:ActivityCartListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this@CartListActivity, R.color.colorPrimaryText)

        setupActionBar()
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        mProductsList = productsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CartListActivity)
    }

    fun successCartItemsList(cartList: ArrayList<Cart>) {
        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0) {
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if (mCartListItems.size > 0) {

            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding.rvCartItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            binding.tvSubTotal.text = "$$subTotal"
            binding.tvShippingCharge.text = "$10.0"

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 10
                binding.tvTotalAmount.text = "$$total"
            } else {
                binding.llCheckout.visibility = View.GONE
            }

        } else {
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getCartItemsList()
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }
}