package com.redis;

import com.redis.stream.CapturingStreamListener;
import com.redis.stream.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StreamTest extends RedisApplicationTest {

    @Autowired
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> messageListenerContainer;

    StreamOperations<String, String, String> streamOpts;

    @BeforeEach
    void setUp() {
        streamOpts = redisTemplate.opsForStream();
    }

    @Test
    void basics() {
        RecordId fixedId1 = streamOpts.add(SensorData.RECORD_1234_0);
        RecordId fixedId2 = streamOpts.add(SensorData.RECORD_1234_1);
        Assertions.assertThat(fixedId1).isEqualTo(SensorData.RECORD_1234_0.getId());
        Assertions.assertThat(fixedId2).isEqualTo(SensorData.RECORD_1234_1.getId());
        Assertions.assertThat(streamOpts.size(SensorData.KEY)).isEqualTo(2L);

        List<MapRecord<String, String, String>> fromStart = streamOpts.read(StreamOffset.fromStart(SensorData.KEY));
        Assertions.assertThat(fromStart).hasSize(2).extracting(MapRecord::getId).containsExactly(fixedId1, fixedId2);

        List<MapRecord<String, String, String>> fromOffset = streamOpts.read(StreamOffset.create(SensorData.KEY, ReadOffset.from(fixedId1)));
        Assertions.assertThat(fromOffset).hasSize(1).extracting(MapRecord::getId).containsExactly(fixedId2);
    }

    @Test
    void continuousRead() throws InterruptedException {
        if (!messageListenerContainer.isRunning()) {
            messageListenerContainer.start();
        }
        CapturingStreamListener streamListener = new CapturingStreamListener();
        messageListenerContainer.receive(StreamOffset.fromStart(SensorData.KEY), streamListener);
        TimeUnit.MILLISECONDS.sleep(100);
        Assertions.assertThat(streamListener.recordsReceived()).isEqualTo(0);

        streamOpts.add(SensorData.RECORD_1234_0);
        streamOpts.add(SensorData.RECORD_1234_1);
        Assertions.assertThat(streamListener.take().getId()).isEqualTo(SensorData.RECORD_1234_0.getId());
        Assertions.assertThat(streamListener.take().getId()).isEqualTo(SensorData.RECORD_1234_1.getId());
        Assertions.assertThat(streamListener.recordsReceived()).isEqualTo(2);

        streamOpts.add(SensorData.RECORD_1235_0);
        Assertions.assertThat(streamListener.take().getId()).isEqualTo(SensorData.RECORD_1235_0.getId());
        Assertions.assertThat(streamListener.recordsReceived()).isEqualTo(3);
    }

}
