@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :currentUserId) OR (senderId = :currentUserId AND receiverId = :userId) ORDER BY createdAt ASC")
    fun getMessagesForUser(userId: String, currentUserId: String): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isSeen = 1 WHERE id = :messageId")
    suspend fun markAsSeen(messageId: String)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun delete(messageId: String)

    @Query("SELECT * FROM messages WHERE (senderId = :userId AND receiverId = :currentUserId) OR (senderId = :currentUserId AND receiverId = :userId) ORDER BY createdAt ASC")
    fun getMessagesPaging(userId: String, currentUserId: String): PagingSource<Int, MessageEntity>
}