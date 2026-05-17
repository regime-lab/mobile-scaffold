// UserRepositoryImpl.kt
class UserRepositoryImpl(
    private val api: UserApiClient,
    private val dao: UserDao
) : UserRepository {
    override suspend fun getUsers(): List<User> {
        // Map DTO/Entity to Domain User
        return api.getUsers().map { it.toDomain() }
    }
}
