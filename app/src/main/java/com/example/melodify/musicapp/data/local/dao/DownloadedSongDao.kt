@Dao
interface DownloadedSongDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: DownloadedSongEntity)

    @Delete
    suspend fun delete(song: DownloadedSongEntity)

    @Query("SELECT * FROM downloaded_songs ORDER BY downloadedAt DESC")
    fun getAll(): Flow<List<DownloadedSongEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM downloaded_songs WHERE songId = :songId)")
    suspend fun isDownloaded(songId: String): Boolean
}