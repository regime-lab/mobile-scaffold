// UserApiClient.kt
interface UserApiClient {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}

// UserDto.kt
data class UserDto(
    val id: String,
    val name: String,
    val email: String
)
