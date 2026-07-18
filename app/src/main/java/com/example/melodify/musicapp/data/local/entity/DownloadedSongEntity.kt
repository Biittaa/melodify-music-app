@Entity(tableName = "downloaded_songs")
data class DownloadedSongEntity(
    @PrimaryKey val songId: String,
    val localFilePath: String,
    val downloadedAt: Long
)