package dev.vality.wb.list.manager.stream;

import dev.vality.damsel.wb_list.ChangeCommand;
import dev.vality.wb.list.manager.serializer.CommandSerde;
import dev.vality.wb.list.manager.serializer.EventSerde;
import dev.vality.wb.list.manager.service.CommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class WbListStreamFactory {

    private final CommandSerde commandSerde = new CommandSerde();
    private final EventSerde eventSerde = new EventSerde();
    private final CommandService commandService;
    private final RetryTemplate retryTemplate;

    @Value("${kafka.wblist.topic.command}")
    private String readTopic;

    @Value("${kafka.wblist.topic.event.sink}")
    private String resultTopic;

    public KafkaStreams create(final Properties streamsConfiguration) {
        try {
            StreamsBuilder builder = new StreamsBuilder();
            builder.stream(readTopic, Consumed.with(Serdes.String(), commandSerde))
                    .filter((s, changeCommand) -> hasChangeCommand(changeCommand) && !isP2P(changeCommand))
                    .peek((s, changeCommand) -> log.info("Command stream check command: {}", changeCommand))
                    .mapValues(command ->
                            retryTemplate.execute(args -> commandService.apply(command)))
                    .filter((s, event) -> event != null)
                    .to(resultTopic, Produced.with(Serdes.String(), eventSerde));
            return new KafkaStreams(builder.build(), streamsConfiguration);
        } catch (Exception e) {
            log.error("WbListStreamFactory error when create stream e: ", e);
            throw new RuntimeException(e);
        }
    }

    private boolean hasChangeCommand(ChangeCommand changeCommand) {
        return changeCommand != null && changeCommand.getCommand() != null;
    }

    private boolean isP2P(ChangeCommand changeCommand) {
        return changeCommand.getRow() != null
                && changeCommand.getRow().isSetId()
                && changeCommand.getRow().getId().isSetP2pId();
    }

}
