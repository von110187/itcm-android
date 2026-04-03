package my.edu.tarc.itcm

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class OrderAdapter(private val context: Activity, private val orders: MutableList<Order>) :
    ArrayAdapter<Order>(context, 0, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false)
        }

        val currentOrder = getItem(position)

        val productNameTextView: TextView = itemView!!.findViewById(R.id.productNameTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val orderImageView: ImageView = itemView.findViewById(R.id.orderImageView)
        val deliveryOptionTextView: TextView = itemView.findViewById(R.id.deliveryOptionTextView)
        val paymentOptionTextView: TextView = itemView.findViewById(R.id.paymentOptionTextView)

        currentOrder?.let {
            productNameTextView.text = it.productName
            priceTextView.text = "RM${it.price}"
            addressTextView.text = "Address: ${it.address}"
            deliveryOptionTextView.text = "Delivery Option: ${it.deliveryOption}"
            paymentOptionTextView.text = "Payment Option: ${it.paymentOption}"

            // Load image into ImageView using Glide if the image URL exists
            val imageUrl = it.imageUrl
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(imageUrl)
                    .into(orderImageView)
                orderImageView.visibility = View.VISIBLE // Show the ImageView
            } else {
                orderImageView.visibility = View.GONE // Hide the ImageView
            }
        }

        return itemView
    }
}