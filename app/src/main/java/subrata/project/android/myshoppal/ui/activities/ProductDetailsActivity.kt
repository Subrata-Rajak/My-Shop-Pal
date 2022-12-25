package subrata.project.android.myshoppal.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import subrata.project.android.myshoppal.R
import subrata.project.android.myshoppal.databinding.ActivityProductDetailsBinding
import subrata.project.android.myshoppal.models.Cart
import subrata.project.android.myshoppal.models.Product
import subrata.project.android.myshoppal.utils.Constants
import subrata.project.android.myshoppal.utils.FirestoreClass
import subrata.project.android.myshoppal.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityProductDetailsBinding

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this@ProductDetailsActivity, R.color.colorPrimaryText)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product Id", mProductId)
        }

        var productOwnerId = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserId() == productOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)

        getProductDetails()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "$${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsStockQuantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsStockQuantity.text = resources.getString(R.string.lbl_out_of_stock)
            binding.tvProductDetailsStockQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    private fun addToCart() {

        val addToCart = Cart(
            FirestoreClass().getCurrentUserId(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)
    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        hideProgressDialog()

        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}