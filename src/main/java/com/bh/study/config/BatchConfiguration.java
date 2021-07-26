package com.bh.study.config;

import com.bh.study.domain.Person;
import com.bh.study.job.JobCompletionNotificationListener;
import com.bh.study.processor.PersonItemProcessor;
import com.bh.study.repository.PersonRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    // Job 생성을 직관적이고 편리하게 도와주는 빌더
    public final JobBuilderFactory jobBuilderFactory;
    public final StepBuilderFactory stepBuilderFactory;

    public final PersonRepository personRepository;
    public final RestTemplate restTemplate;
    public final Gson gson;

    @Autowired
    public BatchConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
                              PersonRepository personRepository, RestTemplate restTemplate, Gson gson) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    /**
     * first chunk (입력, 프로세서 및 출력 정의)
     *      - reader : ItemReader 생성, file 을 찾아 각 라인 항목을 Person 객체로 읽는다.
     *      - processor : ItemProcessor 데이터 일괄 처리 작업을 위해 정의한 인스턴스를 생성한다.
     *      - writer : ItemWriter 생성, @EnableBatchProcessing 에서 생성한 dataSource 의 복사본을 자동으로 가져온다.
     */

    @Bean
    @StepScope  // Step 의 실행시점에 해당 컴포넌트를 Spring Bean 으로 생성
    public ListItemReader<Person> reader() {
        /*  ItemReader 구현에 Repository 를 사용하는 경우
            - ListItemReader: paging 지원 안됨
            - RepositoryItemReader: paging 지원 (추천)
         */
        return new ListItemReader<>(personRepository.findAllByAgeAfter(5));
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor(restTemplate, gson);
    }

    @Bean
    public ItemWriter<Person> writer() {
        return personRepository::saveAll;
    }

    /**
     * last chunk (실제 작업 구성 정의)
     *      - Job : Job 은 배치 처리 과정을 하나의 단위로 만들어 포현한 객체
     *      - Step : 각 단계에서는 reader, processor, writer 가 포함 될 수 있다.
     */

    @Bean
    public Job importJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("send-job")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)     // 각 단계를 나열
                .end()
                .build();
    }

    @Bean
    @JobScope   // Job 실행시점에 Bean 이 생성
    public Step step1(ItemWriter<Person> writer) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(5)  // 한번에 쓸 데이터 양을 정의
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .allowStartIfComplete(true) // 이미 성공적으로 끝난 step 은 스킵하기 때문에 true 로 하면 항상 실행된다.
                .build();
    }
}