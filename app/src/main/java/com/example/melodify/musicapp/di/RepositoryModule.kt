@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    @Binds
    abstract fun bindSongRepository(impl: SongRepositoryImpl): SongRepository
    @Binds
    abstract fun bindPlaylistRepository(impl: PlaylistRepositoryImpl): PlaylistRepository
    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
    @Binds
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository
    @Binds
    abstract fun bindDownloadRepository(impl: DownloadRepositoryImpl): DownloadRepository
    @Binds
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}