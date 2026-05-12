package ap1.luis.rivas.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "ap1.luis.rivas.repository.translate",
    reactiveMongoTemplateRef = "translateMongoTemplate"
)
public class TranslateMongoConfig {

    @Value("${mongodb.translate.uri}")
    private String uri;

    @Value("${mongodb.translate.database}")
    private String database;

    @Bean(name = "translateMongoClient")
    public MongoClient translateMongoClient() {
        return MongoClients.create(uri);
    }

    @Bean(name = "translateMongoTemplate")
    public ReactiveMongoTemplate translateMongoTemplate() {
        return new ReactiveMongoTemplate(translateMongoClient(), database);
    }
}
