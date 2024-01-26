package com.xeridia.config;

import com.xeridia.listener.JobCompletionNotificationListener;
import com.xeridia.model.Comment;
import com.xeridia.processor.CommentProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.GsonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Value("${batch.input}")
    private String batchInput;

    @Bean
    public JsonItemReader<Comment> reader() {
        return new JsonItemReaderBuilder<Comment>()
                .name("commentReader")
                .resource(new ClassPathResource(batchInput))
                .jsonObjectReader(new GsonJsonObjectReader<>(Comment.class))
                .build();
    }

    @Bean
    public CommentProcessor processor() {
        return new CommentProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Comment> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Comment>().itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO comment (id, postId, name, email, body) VALUES (:id, :postId, :name, :email, :body)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Comment> writer) {
        return new StepBuilder("step", jobRepository)
                .<Comment, Comment> chunk(500, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}
