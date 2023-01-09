package dev.vality.wb.list.manager;

import dev.vality.damsel.wb_list.ChangeCommand;
import dev.vality.damsel.wb_list.Command;
import dev.vality.damsel.wb_list.ListType;
import dev.vality.damsel.wb_list.Row;
import dev.vality.testcontainers.annotations.KafkaSpringBootTest;
import dev.vality.testcontainers.annotations.kafka.KafkaTestcontainer;
import dev.vality.testcontainers.annotations.kafka.config.KafkaProducer;
import dev.vality.wb.list.manager.config.MockedStartupInitializers;
import dev.vality.wb.list.manager.exception.RiakExecutionException;
import dev.vality.wb.list.manager.repository.ListRepository;
import org.apache.thrift.TBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@KafkaSpringBootTest
@TestPropertySource(properties = {"retry.timeout=100"})
@KafkaTestcontainer(topicsKeys = {"kafka.wblist.topic.command", "kafka.wblist.topic.event.sink"})
@Import(MockedStartupInitializers.class)
public class WbListSafetyApplicationTest {

    @Value("${kafka.wblist.topic.command}")
    public String topic;

    @Autowired
    private KafkaProducer<TBase<?, ?>> testThriftKafkaProducer;

    @MockBean
    private ListRepository listRepository;

    @Test
    void kafkaRowTestException() throws Exception {
        doThrow(new RiakExecutionException(),
                new RiakExecutionException())
                .doNothing()
                .when(listRepository).create(any());
        ChangeCommand changeCommand = TestObjectFactory.testCommand();
        changeCommand.setCommand(Command.CREATE);

        testThriftKafkaProducer.send(topic, changeCommand);

        verify(listRepository, timeout(2000L).times(3)).create(any());
    }

    @Test
    void kafkaRowTestEmptyValue() throws Exception {
        ChangeCommand changeCommand = TestObjectFactory.testCommand();
        changeCommand.setCommand(Command.CREATE);
        changeCommand.setRow(new Row()
                .setListType(ListType.black)
                .setShopId("test")
                        .setValue("")
                .setListName("test"));
        testThriftKafkaProducer.send(topic, changeCommand);

        clearInvocations(listRepository);
        verify(listRepository, timeout(2000L).times(0)).create(any());
    }

}
