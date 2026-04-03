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
itcm-android/
├── auth/
│   ├── MainActivity.kt
│   ├── RegisterActivity.kt
│   └── ResetPasswordActivity.kt
├── community/
│   ├── HomepageActivity.kt
│   ├── PostDialogFragment.kt
│   ├── CommentDialogFragment.kt
│   ├── UpdatePostActivity.kt
│   ├── SearchForPostActivity.kt
│   └── SearchPostResultActivity.kt
├── marketplace/
│   ├── MarketplaceActivity.kt
│   ├── SellActivity.kt
│   ├── BuyActivity.kt
│   ├── UpdateProductActivity.kt
│   ├── SearchForProductActivity.kt
│   ├── SearchProductResultActivity.kt
│   └── ProductAdapter.kt
├── order/
│   ├── OrderActivity.kt
│   ├── OrderAdapter.kt
│   └── ProfileOrderActivity.kt
├── profile/
│   ├── ProfilePostActivity.kt
│   └── ProfileProductActivity.kt
├── notification/
│   ├── NotificationActivity.kt
│   └── NotificationAdapter.kt
├── chat/
│   └── ChatActivity.kt
├── admin/
│   ├── AdminLoginActivity.kt
│   ├── AdminResetPasswordActivity.kt
│   ├── AdminPostActivity.kt
│   ├── AdminProductActivity.kt
│   ├── AdminUpdatePostActivity.kt
│   ├── AdminUpdateProductActivity.kt
│   ├── AdminOrderActivity.kt
│   └── AdminUpdateOrderActivity.kt
└── util/
    └── imageAdapter.kt
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
