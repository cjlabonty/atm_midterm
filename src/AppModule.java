import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IDatabase.class).to(Database.class).in(com.google.inject.Singleton.class);
        bind(AccountService.class).in(Singleton.class);
        bind(TransactionService.class).in(Singleton.class);
    }
}