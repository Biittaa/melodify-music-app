@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideSearchHistoryDao(db: AppDatabase): SearchHistoryDao = db.searchHistoryDao()
    @Provides
    fun provideLikedSongDao(db: AppDatabase): LikedSongDao = db.likedSongDao()
    @Provides
    fun provideDownloadedSongDao(db: AppDatabase): DownloadedSongDao = db.downloadedSongDao()
    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()
}