@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversation: ConversationEntity)

    @Query("SELECT * FROM conversations ORDER BY lastMessageTime DESC")
    fun getAll(): Flow<List<ConversationEntity>>

    @Query("DELETE FROM conversations WHERE userId = :userId")
    suspend fun delete(userId: String)
}