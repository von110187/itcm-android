# IT Community & Marketplace (ITCM)

An Android mobile application that combines a community forum for IT knowledge sharing with a second-hand marketplace for buying and selling IT-related products.

Built as a Final Year Project, developed solo with self-taught Kotlin and Android Studio.

---

## Features

### User
- Register with profile picture upload
- Login and password reset via Firebase Authentication

### Community
- Browse and create forum-style posts
- Comment, edit, and delete own posts
- Search posts by keyword
- Notification feed for community activity

### Marketplace
- List items for sale with images, category, condition, and price
- Browse product listings
- Search products by keyword
- Place and track orders
- Chat entry point for buyer-seller communication

### Admin Panel
- Separate admin login
- Moderate community posts (view, edit, delete)
- Manage product listings (view, edit, delete)
- View and update order statuses

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| Platform | Android (Native) |
| Architecture | MVC |
| Authentication | Firebase Authentication |
| Database | Firebase Realtime Database |
| File Storage | Firebase Cloud Storage |
| Image Loading | Glide |
| IDE | Android Studio |

---

## Project Structure

```
itcm/
├── MainActivity.kt                  # User login
├── RegisterActivity.kt              # User registration + profile picture upload
├── ResetPasswordActivity.kt         # Password reset
│
├── HomepageActivity.kt              # Community feed
├── PostDialogFragment.kt            # Create post dialog
├── CommentDialogFragment.kt         # Comment dialog
├── UpdatePostActivity.kt            # Edit post
├── SearchForPostActivity.kt         # Post search input
├── SearchPostResultActivity.kt      # Post search results
│
├── MarketplaceActivity.kt           # Product listing feed
├── SellActivity.kt                  # Create product listing
├── BuyActivity.kt                   # Product detail & purchase
├── UpdateProductActivity.kt         # Edit product listing
├── SearchForProductActivity.kt      # Product search input
├── SearchProductResultActivity.kt   # Product search results
│
├── OrderActivity.kt                 # Order management
├── OrderAdapter.kt                  # Order list adapter
├── ProfileOrderActivity.kt          # Order history
├── ProfilePostActivity.kt           # User profile & posts
├── ProfileProductActivity.kt        # User product listings
│
├── NotificationActivity.kt          # Notifications feed
├── NotificationAdapter.kt           # Notification list adapter
├── ChatActivity.kt                  # Chat screen
├── imageAdapter.kt                  # Multi-image adapter
├── ProductAdapter.kt                # Product list adapter
│
├── AdminLoginActivity.kt            # Admin login
├── AdminResetPasswordActivity.kt    # Admin password reset
├── AdminPostActivity.kt             # Admin post moderation
├── AdminProductActivity.kt          # Admin product management
├── AdminUpdatePostActivity.kt       # Admin edit post
├── AdminUpdateProductActivity.kt    # Admin edit product
├── AdminOrderActivity.kt            # Admin order management
└── AdminUpdateOrderActivity.kt      # Admin update order status
```

---

## Setup

> UI layout files (`res/layout/`) are not included in this repository. This repo contains the core application logic for reference and portfolio purposes.

1. Clone this repository and open in Android Studio
2. Create a Firebase project and enable Authentication, Realtime Database, and Cloud Storage
3. Download `google-services.json` and place it in the `/app` directory
4. Add dependencies to `build.gradle`:
   ```gradle
   implementation 'com.google.firebase:firebase-auth'
   implementation 'com.google.firebase:firebase-database'
   implementation 'com.google.firebase:firebase-storage'
   implementation 'com.github.bumptech.glide:glide:4.x.x'
   ```

---

## Firebase Database Structure

```
├── users/        { username, email, profilePicture }
├── posts/        { title, content, author, timestamp, comments/ }
├── products/     { name, category, condition, price, sellerId, images/ }
├── orders/       { productId, buyerId, sellerId, address, deliveryOption, status }
├── notifications/
└── admins/
```

---

## Known Limitations

- Update and delete operations on products and orders are incomplete
- Input validation is minimal across most fields
- Only single image upload supported per post or product
- Admin panel UI is not sufficiently distinct from the user-facing UI
