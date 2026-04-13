package com.example.blinkitclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.DrawableRes
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BlinkitCloneApp()
                }
            }
        }
    }
}

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val qtyText: String,
    val offer: String,
    val rating: Double,
    val imageRes: Int
)

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object Search : Screen()
    object Cart : Screen()
    object Payment : Screen()
    object Success : Screen()
}

@Composable
fun BlinkitCloneApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var selectedBottomTab by remember { mutableStateOf("Home") }
    var userName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val products = remember {
        mutableStateListOf(
            Product(1, "Fresh Milk", "Dairy", 32, "500 ml", "10% OFF", 4.5, R.drawable.milk),
            Product(2, "Brown Bread", "Bakery", 40, "1 pack", "5% OFF", 4.3, R.drawable.bread),
            Product(3, "Banana", "Fruits", 50, "6 pcs", "12% OFF", 4.6, R.drawable.banana),
            Product(4, "Tomato", "Vegetables", 28, "500 g", "8% OFF", 4.2, R.drawable.tomato),
            Product(5, "Potato", "Vegetables", 35, "1 kg", "6% OFF", 4.1, R.drawable.potato),
            Product(6, "Apple Juice", "Drinks", 99, "1 L", "15% OFF", 4.4, R.drawable.juice),
            Product(7, "Biscuits", "Snacks", 25, "1 pack", "7% OFF", 4.0, R.drawable.biscuits),
            Product(8, "Eggs", "Dairy", 78, "12 pcs", "9% OFF", 4.7, R.drawable.eggs),
            Product(9, "Rice", "Groceries", 320, "5 kg", "18% OFF", 4.8, R.drawable.rice),
            Product(10, "Atta", "Groceries", 265, "5 kg", "14% OFF", 4.6, R.drawable.atta),
            Product(11, "Chips", "Snacks", 20, "1 pack", "4% OFF", 4.0, R.drawable.chips),
            Product(12, "Cold Drink", "Drinks", 45, "750 ml", "11% OFF", 4.1, R.drawable.colddrink)
        )
    }

    val cartItems = remember { mutableStateListOf<CartItem>() }
    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(
                userName = userName,
                phone = phone,
                onUserNameChange = { userName = it },
                onPhoneChange = { phone = it },
                onLoginClick = {
                    if (userName.isNotBlank() && phone.length >= 10) {
                        currentScreen = Screen.Home
                    }
                }
            )
        }

        is Screen.Home, is Screen.Search, is Screen.Cart -> {
            MainAppScaffold(
                currentTab = selectedBottomTab,
                cartCount = cartItems.sumOf { it.quantity },
                onTabChange = {
                    selectedBottomTab = it
                    currentScreen = when (it) {
                        "Home" -> Screen.Home
                        "Search" -> Screen.Search
                        else -> Screen.Cart
                    }
                }
            ) { innerPadding ->
                when (currentScreen) {
                    is Screen.Home -> HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        userName = userName,
                        products = products,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { category ->
                            selectedCategory = category
                        },
                        onSearchClick = {
                            selectedBottomTab = "Search"
                            currentScreen = Screen.Search
                        },
                        onAddToCart = { product ->
                            addToCart(product, cartItems)
                        }
                    )

                    is Screen.Search -> SearchScreen(
                        modifier = Modifier.padding(innerPadding),
                        products = products,
                        onAddToCart = { product ->
                            addToCart(product, cartItems)
                        }
                    )

                    is Screen.Cart -> CartScreen(
                        modifier = Modifier.padding(innerPadding),
                        cartItems = cartItems,
                        onIncrease = { item ->
                            item.quantity++
                        },
                        onDecrease = { item ->
                            if (item.quantity > 1) {
                                item.quantity--
                            } else {
                                cartItems.remove(item)
                            }
                        },
                        onRemove = { item ->
                            cartItems.remove(item)
                        },
                        onCheckout = {
                            if (cartItems.isNotEmpty()) {
                                currentScreen = Screen.Payment
                            }
                        }
                    )

                    else -> {}
                }
            }
        }

        is Screen.Payment -> {
            PaymentScreen(
                cartItems = cartItems,
                onBack = { currentScreen = Screen.Cart },
                onPaymentSuccess = {
                    currentScreen = Screen.Success
                }
            )
        }

        is Screen.Success -> {
            OrderSuccessScreen(
                onContinueShopping = {
                    cartItems.clear()
                    selectedBottomTab = "Home"
                    currentScreen = Screen.Home
                }
            )
        }
    }
}

fun addToCart(product: Product, cartItems: MutableList<CartItem>) {
    val existing = cartItems.find { it.product.id == product.id }
    if (existing != null) {
        existing.quantity++
    } else {
        cartItems.add(CartItem(product, 1))
    }
}

@Composable
fun LoginScreen(
    userName: String,
    phone: String,
    onUserNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    val yellow = Color(0xFFFFE141)
    val green = Color(0xFF1BA94C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(yellow),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "blinkit",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "India's last minute app",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Login / Sign Up",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = onUserNameChange,
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                            onPhoneChange(it)
                        }
                    },
                    label = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = green)
                ) {
                    Text("Continue", fontSize = 18.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "By continuing, you agree to Terms & Conditions",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MainAppScaffold(
    currentTab: String,
    cartCount: Int,
    onTabChange: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = currentTab == "Home",
                    onClick = { onTabChange("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentTab == "Search",
                    onClick = { onTabChange("Search") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = currentTab == "Cart",
                    onClick = { onTabChange("Cart") },
                    icon = {
                        Box {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            if (cartCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (cartCount > 9) "9+" else cartCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    },
                    label = { Text("Cart") }
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String,
    products: List<Product>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val yellow = Color(0xFFFFE141)
    val categories = listOf("All", "Fruits", "Vegetables", "Dairy", "Snacks", "Drinks", "Groceries", "Bakery")

    val filteredProducts = if (selectedCategory == "All") {
        products
    } else {
        products.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(yellow)
                    .padding(16.dp)
            ) {
                Text(
                    text = "blinkit in",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "10 minutes",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Home • Hello ${if (userName.isBlank()) "User" else userName}",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { onSearchClick() }
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search groceries, fruits, snacks...",
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Shop by Category",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category

                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (isSelected) Color(0xFF1BA94C) else Color.White
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF1BA94C) else Color(0xFFE2E2E2),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { onCategorySelected(category) }
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = category,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (selectedCategory == "All") "Best Sellers" else selectedCategory,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (filteredProducts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No items found in $selectedCategory",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            items(filteredProducts.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { product ->
                        ProductCard(
                            product = product,
                            modifier = Modifier.weight(1f),
                            onAddToCart = { onAddToCart(product) }
                        )
                    }

                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    products: List<Product>,
    onAddToCart: (Product) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filtered = products.filter {
        it.name.contains(query, true) || it.category.contains(query, true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .padding(12.dp)
    ) {
        Text(
            text = "Search",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search products") },
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(filtered) { product ->
                SearchItemRow(product = product, onAddToCart = { onAddToCart(product) })
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SearchItemRow(
    product: Product,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(product.qtyText, color = Color.Gray)
                Text("₹${product.price}", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onAddToCart,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA94C))
            ) {
                Text("Add")
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.offer,
                color = Color(0xFF1BA94C),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1
            )

            Text(
                text = product.qtyText,
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = product.rating.toString(), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "₹${product.price}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )

                Button(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA94C))
                ) {
                    Text("Add")
                }
            }
        }
    }
}

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,
    cartItems: MutableList<CartItem>,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onRemove: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {
    val itemTotal = cartItems.sumOf { it.product.price * it.quantity }
    val deliveryCharge = if (cartItems.isEmpty()) 0 else 25
    val handlingCharge = if (cartItems.isEmpty()) 0 else 8
    val grandTotal = itemTotal + deliveryCharge + handlingCharge

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .padding(12.dp)
    ) {
        Text(
            text = "My Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { onIncrease(item) },
                        onDecrease = { onDecrease(item) },
                        onRemove = { onRemove(item) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PriceRow("Items Total", itemTotal)
                    PriceRow("Delivery Charge", deliveryCharge)
                    PriceRow("Handling Charge", handlingCharge)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    PriceRow("Grand Total", grandTotal, true)

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA94C))
                    ) {
                        Text("Proceed to Payment", fontSize = 17.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.product.imageRes),
                contentDescription = item.product.name,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.product.qtyText, color = Color.Gray, fontSize = 13.sp)
                Text("₹${item.product.price}", fontWeight = FontWeight.Bold)
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuantityButton("-", onClick = onDecrease)
                    Text(
                        text = item.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp),
                        fontWeight = FontWeight.Bold
                    )
                    QuantityButton("+", onClick = onIncrease)
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}


@Composable
fun QuantityButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1BA94C))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("Cash on Delivery") }

    val itemTotal = cartItems.sumOf { it.product.price * it.quantity }
    val deliveryCharge = 25
    val handlingCharge = 8
    val grandTotal = itemTotal + deliveryCharge + handlingCharge

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .padding(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    PaymentOption(
                        title = "Cash on Delivery",
                        selected = selectedMethod == "Cash on Delivery",
                        onSelect = { selectedMethod = "Cash on Delivery" }
                    )

                    PaymentOption(
                        title = "UPI",
                        selected = selectedMethod == "UPI",
                        onSelect = { selectedMethod = "UPI" }
                    )

                    PaymentOption(
                        title = "Credit / Debit Card",
                        selected = selectedMethod == "Credit / Debit Card",
                        onSelect = { selectedMethod = "Credit / Debit Card" }
                    )

                    PaymentOption(
                        title = "Net Banking",
                        selected = selectedMethod == "Net Banking",
                        onSelect = { selectedMethod = "Net Banking" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bill Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow("Items", itemTotal)
                    PriceRow("Delivery", deliveryCharge)
                    PriceRow("Handling", handlingCharge)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    PriceRow("Total Payable", grandTotal, true)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onPaymentSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA94C))
            ) {
                Text("Pay ₹$grandTotal", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun PaymentOption(
    title: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = title)
    }
}

@Composable
fun OrderSuccessScreen(
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F9))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF1BA94C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order Successful",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your order has been placed successfully.\nDelivery partner will reach soon.",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1BA94C))
        ) {
            Text("Continue Shopping", fontSize = 17.sp)
        }
    }
}

@Composable
fun PriceRow(
    label: String,
    value: Int,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (bold) 17.sp else 15.sp
        )
        Text(
            text = "₹$value",
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (bold) 17.sp else 15.sp
        )
    }
}