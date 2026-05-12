package ap1.luis.rivas.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(
    basePackages = "ap1.luis.rivas.repository.chat",
    reactiveMongoTemplateRef = "chatMongoTemplate"
)
public class ChatMongoConfig {

    @Value("${mongodb.chat.uri}")
    private String uri;

    @Value("${mongodb.chat.database}")
    private String database;

    @Primary
    @Bean(name = "chatMongoClient")
    public MongoClient chatMongoClient() {
        return MongoClients.create(uri);
    }

    @Primary
    @Bean(name = "chatMongoTemplate")
    public ReactiveMongoTemplate chatMongoTemplate() {
        return new ReactiveMongoTemplate(chatMongoClient(), database);
    }
}
