package dev.vality.wb.list.manager.repository;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.Quorum;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;
import dev.vality.wb.list.manager.exception.RiakExecutionException;
import dev.vality.wb.list.manager.model.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListRepository implements CrudRepository<Row, String> {

    private static final String TEXT_PLAIN = "text/plain";
    private final RiakClient client;

    @Value("${riak-config.bucket}")
    private String bucket;

    @Override
    public void create(Row row) {
        try {
            log.info("ListRepository create in bucket: {} row: {}", bucket, row);
            RiakObject quoteObject = new RiakObject()
                    .setContentType(TEXT_PLAIN)
                    .setValue(BinaryValue.create(row.getValue()));
            Location quoteObjectLocation = createLocation(bucket, row.getKey());
            StoreValue storeOp = new StoreValue.Builder(quoteObject)
                    .withOption(StoreValue.Option.W, Quorum.oneQuorum())
                    .withLocation(quoteObjectLocation)
                    .build();
            client.execute(storeOp);
        } catch (InterruptedException e) {
            log.error("InterruptedException in ListRepository when create e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in ListRepository when create e: ", e);
            throw new RiakExecutionException();
        }
    }

    @Override
    public void remove(Row row) {
        try {
            log.info("ListRepository remove from bucket: {} row: {}", bucket, row);
            Location quoteObjectLocation = createLocation(bucket, row.getKey());
            DeleteValue delete = new DeleteValue.Builder(quoteObjectLocation)
                    .withOption(DeleteValue.Option.W, Quorum.oneQuorum())
                    .build();
            client.execute(delete);
        } catch (InterruptedException e) {
            log.error("InterruptedException in ListRepository when remove e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (ExecutionException e) {
            log.error("Exception in ListRepository when remove e: ", e);
            throw new RiakExecutionException(e);
        }
    }

    @Override
    public Optional<Row> get(String key) {
        try {
            log.info("ListRepository get bucket: {} key: {}", bucket, key);
            Location quoteObjectLocation = createLocation(bucket, key);
            FetchValue fetch = new FetchValue.Builder(quoteObjectLocation)
                    .withOption(FetchValue.Option.R, Quorum.quorumQuorum())
                    .build();
            FetchValue.Response response = client.execute(fetch);
            RiakObject obj = response.getValue(RiakObject.class);
            return obj != null && obj.getValue() != null
                    ? Optional.of(new Row(key, obj.getValue().toString()))
                    : Optional.empty();
        } catch (InterruptedException e) {
            log.error("InterruptedException in ListRepository when get e: ", e);
            Thread.currentThread().interrupt();
            throw new RiakExecutionException(e);
        } catch (Exception e) {
            log.error("Exception in ListRepository when get e: ", e);
            throw new RiakExecutionException(e);
        }
    }

    private Location createLocation(String bucketName, String key) {
        Namespace quotesBucket = new Namespace(bucketName);
        return new Location(quotesBucket, key);
    }
}
