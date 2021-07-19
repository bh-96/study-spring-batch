package com.bh.study.config;

import com.bh.study.domain.Person;
import com.bh.study.job.JobCompletionNotificationListener;
import com.bh.study.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration          // 클래스를 Application Context 의 bean 정의 소스로 태그 지정
@EnableBatchProcessing  // reader, processor, writer (Chunk) 를 정의한다.
public class BatchConfiguration {

    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    /**
     * first chunk (입력, 프로세서 및 출력 정의)
     */
    // reader : ItemReader 생성, file 을 찾아 각 라인 항목을 Person 객체로 읽는다.
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    // processor : PersonItemProcessor 데이터 일괄 처리 작업을 위해 정의한 인스턴스를 생성한다.
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    // writer : ItemWriter 생성, @EnableBatchProcessing 에서 생성한 dataSource 의 복사본을 자동으로 가져온다.
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(dataSource)
                .build();
    }

    /**
     * last chunk (실제 작업 구성 정의)
     */
    // Job : 단계별로 작성된다. (Step)
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())    // 디비를 사용하여 실행 상태를 유지하므로 incrementer 가 필요함
                .listener(listener)
                .flow(step1)    // 각 단계를 나열
                .end()
                .build();
    }

    // Step : 각 단계에서는 reader, processor, writer 가 포함 될 수 있다.
    @Bean
    public Step step1(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)     // 한번에 쓸 데이터 양을 정의
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }
}