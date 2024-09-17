package com.example.foodiefinder_apps.FoodieFinder_Module

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


const val Base_Url = "https://www.themealdb.com/api/json/v1/1/"

interface TheFoodieFinderService {
    @GET("search.php?s=")
    suspend fun searchMeals(@Query("s") query: String): MealsResponse

    @GET("search.php?f=")
    suspend fun searchMealsByFirstLetter(@Query("f") firstLetter: String): MealsResponse

    @GET("filter.php?c=")
    suspend fun searchMealsByCategory(@Query("c") category: String): MealsResponse

    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("filter.php?a=")
    suspend fun searchMealsByArea(@Query("a") area: String): MealsResponse

    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") mealId: String): MealDetailResponse

    companion object {
        private var apiService: TheFoodieFinderService? = null
        fun getInstance(): TheFoodieFinderService {
            if (apiService == null) {
                val gson = GsonBuilder().setLenient().create()
                apiService = Retrofit.Builder()
                    .baseUrl(Base_Url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(TheFoodieFinderService::class.java)
            }
            return apiService!!
        }
    }
}
