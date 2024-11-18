package by.innowise.productservice.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class TransactionManagerConfig implements TransactionManagementConfigurer {

    private final PlatformTransactionManager transactionManager;

    @Override
    @NonNull
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}
