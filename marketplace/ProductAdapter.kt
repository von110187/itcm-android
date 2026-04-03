import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import my.edu.tarc.itcm.AdminProductActivity
import my.edu.tarc.itcm.AdminUpdateProductActivity
import my.edu.tarc.itcm.BuyActivity
import my.edu.tarc.itcm.MarketplaceActivity
import my.edu.tarc.itcm.ProfileProductActivity
import my.edu.tarc.itcm.R
import my.edu.tarc.itcm.UpdateProductActivity

class ProductAdapter(private val context: Activity, private val products: MutableList<MarketplaceActivity.Product>) :
    ArrayAdapter<MarketplaceActivity.Product>(context, 0, products) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        }

        val currentProduct = getItem(position)

        val productNameTextView: TextView = itemView!!.findViewById(R.id.productNameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val datetimeTextView: TextView = itemView.findViewById(R.id.datetimeTextView)
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val textViewUpdate: TextView = itemView.findViewById(R.id.textViewUpdate)
        val textViewDelete: TextView = itemView.findViewById(R.id.textViewDelete)

        currentProduct?.let {
            productNameTextView.text = it.productName
            priceTextView.text = it.price
            it.datetime?.let { datetime ->
                val datetimeStr = MarketplaceActivity.convertTimeToString(datetime)
                datetimeTextView.text = datetimeStr
            }

            // Load image into ImageView using Glide if the image URL exists
            currentProduct.images?.let { images ->
                val imageUrl = images["image0"]
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(context)
                        .load(imageUrl)
                        .into(productImageView)
                    productImageView.visibility = View.VISIBLE // Show the ImageView
                } else {
                    productImageView.visibility = View.GONE // Hide the ImageView
                }
            }

            // Show textViewUpdate and textViewDelete only in ProfileProductActivity
            if (context is ProfileProductActivity) {
                textViewUpdate.visibility = View.VISIBLE
                textViewDelete.visibility = View.VISIBLE

                // Set click listener for textViewUpdate
                textViewUpdate.setOnClickListener {
                    currentProduct?.let { product ->
                        // Redirect to UpdateProductActivity with appropriate data
                        val intent = Intent(context, UpdateProductActivity::class.java)
                        // Pass necessary data to UpdateProductActivity using intent extras
                        intent.putExtra("productName", product.productName)
                        intent.putExtra("productName", product.productName)
                        intent.putExtra("price", product.price)
                        intent.putExtra("imageUrl", product.images?.get("image0"))
                        intent.putExtra("datetime", product.datetime)
                        intent.putExtra("condition", product.condition)
                        intent.putExtra("category", product.category)
                        intent.putExtra("productDetails", product.productDetails)
                        intent.putExtra("email", product.email)
                        // Add more data if needed
                        context.startActivity(intent)
                    }
                }
            } else if (context is AdminProductActivity){
                textViewUpdate.visibility = View.VISIBLE
                textViewDelete.visibility = View.VISIBLE

                // Set click listener for textViewUpdate
                textViewUpdate.setOnClickListener {
                    currentProduct?.let { product ->
                        // Redirect to UpdateProductActivity with appropriate data
                        val intent = Intent(context, AdminUpdateProductActivity::class.java)
                        // Pass necessary data to UpdateProductActivity using intent extras
                        intent.putExtra("productName", product.productName)
                        intent.putExtra("productName", product.productName)
                        intent.putExtra("price", product.price)
                        intent.putExtra("imageUrl", product.images?.get("image0"))
                        intent.putExtra("datetime", product.datetime)
                        intent.putExtra("condition", product.condition)
                        intent.putExtra("category", product.category)
                        intent.putExtra("productDetails", product.productDetails)
                        intent.putExtra("email", product.email)
                        // Add more data if needed
                        context.startActivity(intent)
                    }
                }
            } else {
                textViewUpdate.visibility = View.GONE
                textViewDelete.visibility = View.GONE
            }
        }

        if (context !is AdminProductActivity) {
            // Set click listener for the entire item
            itemView.setOnClickListener {
                // Redirect to BuyActivity with appropriate data
                val intent = Intent(context, BuyActivity::class.java)
                // Pass necessary data to BuyActivity using intent extras
                currentProduct?.let { product ->
                    intent.putExtra("productName", product.productName)
                    intent.putExtra("price", product.price)
                    intent.putExtra("imageUrl", product.images?.get("image0"))
                    intent.putExtra("datetime", product.datetime)
                    intent.putExtra("condition", product.condition)
                    intent.putExtra("category", product.category)
                    intent.putExtra("productDetails", product.productDetails)
                    intent.putExtra("email", product.email)
                    // Add more data if needed
                }
                context.startActivity(intent)
            }
        }

        return itemView
    }
}