package com.example.etldemo.config;

import com.example.etldemo.entity.SalesRecord;
import com.example.etldemo.repository.SalesRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    private final SalesRepository repository;

    public BatchConfig(SalesRepository repository) {
        this.repository = repository;
    }

    @Bean
    public FlatFileItemReader<SalesRecord> reader() {
        FlatFileItemReader<SalesRecord> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("sales_data.csv"));
        reader.setName("csvReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
    }

    private LineMapper<SalesRecord> lineMapper() {
        DefaultLineMapper<SalesRecord> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("orderId", "region", "itemType", "unitsSold", "totalRevenue");

        BeanWrapperFieldSetMapper<SalesRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(SalesRecord.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public RepositoryItemWriter<SalesRecord> writer() {
        RepositoryItemWriter<SalesRecord> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("sales-step", jobRepository)
                .<SalesRecord, SalesRecord>chunk(1000, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }

    @Bean
    public Job runJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("importSalesJob", jobRepository)
                .flow(step1(jobRepository, transactionManager))
                .end()
                .build();
    }
}
