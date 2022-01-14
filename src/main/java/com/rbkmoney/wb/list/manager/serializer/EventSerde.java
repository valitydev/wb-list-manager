package com.rbkmoney.wb.list.manager.serializer;


import com.rbkmoney.kafka.common.serialization.ThriftSerializer;
import dev.vality.damsel.wb_list.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class EventSerde implements Serde<Event> {


    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public void close() {

    }

    @Override
    public Serializer<Event> serializer() {
        return new ThriftSerializer<>();
    }

    @Override
    public Deserializer<Event> deserializer() {
        return new EventDeserializer();
    }
}
