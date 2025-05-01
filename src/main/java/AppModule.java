import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Guice module that configures the bindings between interfaces and their implementations.
 * This class sets up the dependency injection (DI) for the application.
 */
public class AppModule extends AbstractModule {
    /**
     * Configures the bindings for the application's services and database.
     * This method binds the interfaces to their respective implementations
     * and ensures that singletons are used where appropriate.
     */
    @Override
    protected void configure() {
        bind(IDatabase.class).to(Database.class).in(com.google.inject.Singleton.class);
        bind(AccountService.class).in(Singleton.class);
        bind(TransactionService.class).in(Singleton.class);
    }
}
