@Entity(tableName = "liked_songs")
data class LikedSongEntity(
    @PrimaryKey val songId: String,
    val title: String,
    val artistId: String,
    val albumId: String,
    val coverUrl: String,
    val audioUrl: String,
    val duration: Long,
    val genre: String,
    val playCount: Long,
    val likedAt: Long
)