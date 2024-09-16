package com.example.foodiefinder_apps.FoodieFinder_Module
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.NavigationBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp

@Composable
fun SearchScreenUI(nc: NavController, vm: TheFoodieFinderViewModel) {
    val meals by vm.meals.collectAsState()
    val error by vm.error.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Track sort order state
    var isAscending by remember { mutableStateOf(true) }
    // Fetch default meals initially
    LaunchedEffect(Unit) {
        vm.fetchDefaultMeals()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFFDC3D74),
                contentColor = Color.White,
                title = {
                    Text(text = "Search Restaurants", style = TextStyle(fontSize = 20.sp, fontFamily = FontFamily.Cursive))
                },
                navigationIcon = {
                    IconButton(onClick = { nc.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        vm.toggleSort()
                        isAscending = !isAscending // Toggle sort state
                    }) {
                        Icon(
                            if (isAscending) Icons.Default.ArrowCircleDown else Icons.Default.ArrowCircleUp,
                            contentDescription = "Sort"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0x81DDD6D6))
        ) {
            // Search Bar
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .background(Color(0xFFFFFFFF))
                    .clip(RoundedCornerShape(30.dp)) // Apply shape here
            ) {
                TextField(
                    value = searchQuery.value,
                    onValueChange = { query ->
                        searchQuery.value = query
                        if (query.isEmpty()) {
                            // Fetch default meals when search query is empty
                            vm.fetchDefaultMeals()
                        } else {
                            // Perform search
                            vm.searchMeals(query)
                        }
                    },
                    placeholder = {
                        Text("Search for your local favorites", style = TextStyle(color = Color.Gray))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        textColor = Color.Black,
                        backgroundColor = Color.White, // Lighter background
                        focusedIndicatorColor = Color.Transparent, // No underline
                        unfocusedIndicatorColor = Color.Transparent // No underline
                    ),
                    shape = RoundedCornerShape(30.dp), // More rounded corners
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, Color(0xFFF806B6), RoundedCornerShape(40.dp)) // Vibrant border
                        .padding(horizontal = 16.dp) // Add padding inside
                )
            }

            // Error Handling
            error?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Meals List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (meals.isEmpty() && searchQuery.value.isNotEmpty()) {
                    item {
                        Text(
                            "No meals found",
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                } else {
                    items(meals) { meal ->
                        MealItems(
                            meal = meal,
                            viewModel = vm,
                            onClick = {
                                nc.navigate("MealDetail/${meal.strMeal}")
                            },
                            onFavoriteChanged = { isFavorite ->
                                coroutineScope.launch {
                                    val message = if (isFavorite) {
                                        "Added to favorites"
                                    } else {
                                        "Removed from favorites"
                                    }
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun MealItems(
    meal: Meal?,
    viewModel: TheFoodieFinderViewModel,
    onClick: (Meal) -> Unit,
    onFavoriteChanged: (Boolean) -> Unit
) {
    meal?.let {
        var isFavorite by remember { mutableStateOf(viewModel.isFavorite(it.idMeal)) }

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start =16.dp, end = 16.dp, bottom = 10.dp)
                .clickable { onClick(meal) },
            elevation = 8.dp // Set elevation directly as Dp
        ) {
            Row(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Align items to space between
            ) {
                // Meal Image
                AsyncImage(
                    model = it.strMealThumb,
                    contentDescription = it.strMeal,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray)
                )

                // Meal Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp) // Add padding to the start
                ) {
                    Text(
                        text = it.strMeal,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    )
                    Text(
                        text = "From: ${it.strArea ?: "Unknown"}",
                        style = TextStyle(
                            fontSize = 14.sp, // Adjust font size if needed
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF666666) // Lighter color for area
                        )
                    )
                }

                // Favorite Icon
                IconButton(
                    onClick = {
                        viewModel.toggleFavorite(it)
                        isFavorite = !isFavorite
                        onFavoriteChanged(isFavorite)
                    },
                    modifier = Modifier.align(Alignment.CenterVertically) // Center icon vertically
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = Color(0xFFFF80AB)
                    )
                }
            }
        }
    }
}
