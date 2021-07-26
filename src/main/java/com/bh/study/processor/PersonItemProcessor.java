package com.bh.study.processor;

import com.bh.study.domain.Person;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * ItemProcessor : (인터페이스 구현) 일괄 처리 작업에 쉽게 코드 연결 가능 -> reader 로부터 받은 데이터를 가공/처리
 */
public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

    private final RestTemplate restTemplate;
    private final Gson gson;

    public PersonItemProcessor(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    @Override
    public Person process(final Person item) throws Exception {
        String domainToJsonString = gson.toJson(item);

        boolean sendSuccess = false;

        try {
            postPersonData(item.getRecvURL(), domainToJsonString);
            sendSuccess = true;
        } catch (Exception e) {
            log.error("[ERROR] " + e.getMessage());
        }

        if (sendSuccess) {
            item.setSendYn("Y");
        }

        return item;
    }

    private void postPersonData(String recvURL, String data) throws Exception {
        try {
            log.info("[SEND] url = " + recvURL + ", data = " + data);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");
            HttpEntity<String> entity = new HttpEntity<>(data, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(recvURL, entity, String.class);
            log.info("[RECV] " + responseEntity.getBody());
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}