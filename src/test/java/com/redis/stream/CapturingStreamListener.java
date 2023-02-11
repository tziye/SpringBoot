package com.redis.stream;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@NoArgsConstructor
public class CapturingStreamListener implements StreamListener<String, MapRecord<String, String, String>> {

    AtomicInteger counter = new AtomicInteger(0);
    BlockingDeque<MapRecord<String, String, String>> deque = new LinkedBlockingDeque<>();

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        counter.incrementAndGet();
        log.info("Received: {}", record);
        deque.add(record);
    }

    public int recordsReceived() {
        return counter.get();
    }

    public MapRecord<String, String, String> take() throws InterruptedException {
        MapRecord<String, String, String> record = deque.take();
        log.info("Handle: {}", record);
        return record;
    }
}
