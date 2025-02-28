package com.corusoft.ticketmanager.backend

import com.corusoft.ticketmanager.backend.dtos.common.GenericValueDTO

import com.corusoft.ticketmanager.backend.dtos.tickets.CategoryDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.CreateCustomizedCategoryParamsDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.CreateTicketParamsDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.CustomizedCategoryDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.ParsedTicketDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.ShareTicketParamsDTO
import com.corusoft.ticketmanager.backend.dtos.tickets.TicketDTO

import com.corusoft.ticketmanager.backend.dtos.tickets.filters.TicketFilterParamsDTO
import com.corusoft.ticketmanager.backend.dtos.users.AuthenticatedUserDTO
import com.corusoft.ticketmanager.backend.dtos.users.LoginParamsDTO
import com.corusoft.ticketmanager.backend.dtos.users.RegisterUserParamsDTO
import com.corusoft.ticketmanager.backend.dtos.users.SubscriptionDTO
import retrofit2.Response
import retrofit2.http.*

interface IBackendService {
    /* ******************** USER ENDPOINTS ******************** */
    @POST("users/register")
    suspend fun register(@Body params: RegisterUserParamsDTO): Response<AuthenticatedUserDTO>

    @POST("users/login")
    suspend fun login(@Body params: LoginParamsDTO): Response<AuthenticatedUserDTO>

    @POST("users/login/token")
    suspend fun loginFromToken(): Response<AuthenticatedUserDTO>

    @POST("users/subscribe/{userID}")
    suspend fun subscribeToPremium(@Path("userID") userID: Long): Response<SubscriptionDTO>


    /* ******************** TICKET ENDPOINTS ******************** */
    @GET("tickets/categories")
    suspend fun getAllCategories(): Response<List<CategoryDTO>>

    @POST("tickets/categories")
    suspend fun createCustomizedCategory(@Body params: CreateCustomizedCategoryParamsDTO): Response<CustomizedCategoryDTO>

    @PUT("tickets/categories/{categoryID}")
    suspend fun updateCustomizedCategory(
        @Path("categoryID") categoryID: Long,
        @Body params: GenericValueDTO<Float>
    ): Response<CustomizedCategoryDTO>

    @GET("tickets/categories/{userID}")
    suspend fun getCustomizedCategoriesByUser(@Path("userID") userID: Long): Response<List<CustomizedCategoryDTO>>

    @POST("tickets/parse")
    suspend fun parseTicket(@Body params: GenericValueDTO<String>): Response<ParsedTicketDTO>

    @POST("tickets/")
    suspend fun createTicket(@Body params: CreateTicketParamsDTO): Response<TicketDTO>

    @POST("share/{ticketID}")
    suspend fun shareTicket(
        @Path("userID") userID: Long,
        @Body params: ShareTicketParamsDTO
    ): Response<TicketDTO>

    @DELETE("tickets/{ticketID}")
    suspend fun deleteTicket(@Path("ticketID") ticketID: Long): Response<Unit>

    @GET("tickets/{ticketID}")
    suspend fun getTicketDetails(@Path("ticketID") ticketID: Long): Response<TicketDTO>

    @PUT("tickets/{userID}")
    suspend fun filterUserTicketsByCriteria(
        @Path("userID") userID: Long,
        @Body params: TicketFilterParamsDTO
    ): Response<List<TicketDTO>>

    @GET("/tickets/sharedTickets")
    suspend fun getSharedTickets(): Response<List<TicketDTO>>


    /* ******************** STATS ENDPOINTS ******************** */
    @GET("stats/spendingsPerMonth")
    suspend fun getCurrentMonthSpendings(): Response<Map<String, Double>>

    //@POST("stats/wastesCategory")
    //suspend fun getWastesPerCategory(@Body params: WastesPerCategoryParamsDTO): Response<Map<String, Double>>

    @GET("stats/spendingsThisMonth")
    suspend fun getSpendingsByCategories(): Response<Map<String, Float>>

    @GET("stats/percentagePerCategory")
    suspend fun getPercentagePerCategoryThisMonth(): Response<Map<String, Double>>
}