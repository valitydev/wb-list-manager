package dev.vality.wb.list.manager;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import dev.vality.wb.list.manager.extension.AwaitilityExtension;
import dev.vality.wb.list.manager.extension.RiakTestcontainerExtension;
import dev.vality.wb.list.manager.model.Row;
import dev.vality.wb.list.manager.repository.ListRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({RiakTestcontainerExtension.class, AwaitilityExtension.class})
@SpringBootTest
public class RiakTest {

    private static final String VALUE = "value";
    private static final String KEY = "key";

    @Value("${riak-config.bucket}")
    private String bucketName;

    @Autowired
    private ListRepository listRepository;

    @Autowired
    private RiakClient client;

    @Test
    void riakTest() throws ExecutionException, InterruptedException {
        Row row = new Row();
        row.setKey(KEY);
        row.setValue(VALUE);
        Awaitility.await()
                .pollDelay(5_000L, TimeUnit.MILLISECONDS)
                .atMost(30_000L, TimeUnit.MILLISECONDS)
                .ignoreExceptions()
                .until(() -> {
                    listRepository.create(row);
                    return listRepository.get(KEY).isPresent();
                });

        Namespace ns = new Namespace(bucketName);
        Location location = new Location(ns, KEY);
        FetchValue fv = new FetchValue.Builder(location).build();
        FetchValue.Response response = client.execute(fv);
        RiakObject obj = response.getValue(RiakObject.class);

        String result = obj.getValue().toString();
        assertEquals(VALUE, result);

        Optional<Row> resultGet = listRepository.get(KEY);
        assertFalse(resultGet.isEmpty());
        assertEquals(VALUE, resultGet.get().getValue());

        listRepository.remove(row);
        response = client.execute(fv);
        obj = response.getValue(RiakObject.class);
        assertNull(obj);
    }
}
