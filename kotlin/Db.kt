// UserDao.kt
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAll(): List<UserEntity>

    @Insert
    suspend fun insert(user: UserEntity)
}

// UserEntity.kt
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String
)

// AppDatabase.kt
@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
