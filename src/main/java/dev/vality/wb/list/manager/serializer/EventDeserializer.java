package dev.vality.wb.list.manager.serializer;


import dev.vality.damsel.wb_list.Event;
import dev.vality.kafka.common.serialization.AbstractThriftDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventDeserializer extends AbstractThriftDeserializer<Event> {

    @Override
    public Event deserialize(String topic, byte[] data) {
        return super.deserialize(data, new Event());
    }

}