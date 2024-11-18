package by.innowise.inventoryservice.aop;

import by.innowise.inventoryservice.filters.Context;
import by.innowise.inventoryservice.filters.ContextHolder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Aspect
public class KafkaListenerAspect {
    @Before("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    @SuppressWarnings("unchecked")
    public void fetchHeaders(JoinPoint joinPoint) {
        ConsumerRecord<String, Object> record = (ConsumerRecord<String, Object>) joinPoint.getArgs()[0];
        Headers headers = record.headers();
        Map<String, String> headersMap = Arrays.stream(headers.toArray()).collect(Collectors.
                toMap(Header::key, h -> new String(h.value())));
        ContextHolder.getContext().setAuthToken(headersMap.get(Context.AUTHORIZATION));
    }
}
