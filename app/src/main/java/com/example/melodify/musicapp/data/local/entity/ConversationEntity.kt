@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val userId: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)