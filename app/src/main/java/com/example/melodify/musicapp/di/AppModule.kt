@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideCurrentUser(authRepo: AuthRepository): User? {
        return authRepo.getCurrentUser()
    }
}