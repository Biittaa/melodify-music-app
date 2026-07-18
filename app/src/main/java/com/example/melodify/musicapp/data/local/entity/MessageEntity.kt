@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val songId: String?,
    val createdAt: Long,
    val isSeen: Boolean,
    val isSent: Boolean // User sent
)