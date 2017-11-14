package ru.ulmc.bank.config.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.modeled.ModelSerializer;
import org.apache.curator.x.async.modeled.ModelSpec;
import org.apache.curator.x.async.modeled.ModelSpecBuilder;
import org.apache.curator.x.async.modeled.ModeledFramework;
import org.apache.curator.x.async.modeled.ZPath;
import org.apache.curator.x.async.modeled.cached.CachedModeledFramework;
import org.apache.curator.x.async.modeled.typed.TypedModeledFramework0;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.ulmc.bank.config.zookeeper.serializers.JsonModelSerializer;
import ru.ulmc.bank.config.zookeeper.serializers.StringModelSerializer;

public class ZooConfigMonitor<T> implements AutoCloseable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ZooConfigMonitor.class);

    private final CuratorFramework client;
    private final AsyncCuratorFramework asyncFramework;
    private final Class<T> clazz;
    private ModelSpecBuilder<T> specBuilder;

    private String znode;

    private boolean closed;

    private ModelSerializer<T> serializer;

    private final List<CachedModeledFramework<T>> subscribers = new ArrayList<>();

    /**
     * @param connectString comma separated host:port pairs, each corresponding to a zk server. e.g.
     *                      "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002" If the optional chroot
     *                      suffix is used the example would look like: "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002/app/a"
     *                      where the client would be rooted at "/app/a" and all paths would be
     *                      relative to this root - ie getting/setting/etc... "/foo/bar" would
     *                      result in operations being run on "/app/a/foo/bar" (from the server
     *                      perspective).
     * @throws Exception in cases of network failure
     */
    public ZooConfigMonitor(String connectString, String znode, Class<T> clazz) throws Exception {
        Objects.requireNonNull(connectString);
        Objects.requireNonNull(znode);
        Objects.requireNonNull(clazz);
        this.znode = znode.startsWith("/") ? znode : "/" + znode;
        this.clazz = clazz;
        serializer = clazz == String.class ?
                (ModelSerializer<T>) new StringModelSerializer() :
                JsonModelSerializer.build(clazz);
        client = CuratorFrameworkFactory.newClient(connectString, new RetryNTimes(10, 500));

        asyncFramework = AsyncCuratorFramework.wrap(client);
        specBuilder = ModelSpec.builder(ZPath.parse(znode), serializer)
                .withTtl(-1)
                .withCreateMode(CreateMode.PERSISTENT);
        client.start();
        createNode();
    }

    public ZooConfigMonitor(String connectString, String znode, Class<T> clazz, Map<String, T> mapStore) throws Exception {
        this(connectString, znode, clazz);
        Objects.requireNonNull(mapStore);
        startSubscriber(new Listener<T>() {
            @Override
            public void added(String key, T model) {
                LOG.debug("Added to node " + key + " zoo path " + znode);
                mapStore.put(key, model);
            }

            @Override
            public void updated(String key, T model) {
                LOG.debug("Updated into node " + key + " zoo path " + znode);
                mapStore.put(key, model);
            }

            @Override
            public void removed(String key) {
                LOG.debug("Removed from node " + key + " zoo path " + znode);
                mapStore.remove(key);
            }
        });
    }

    public void startSubscriber(Listener<T> listener) {
        startSubscriber(znode + "/{id}", listener);
    }

    private void startSubscriber(String node, Listener<T> listener) {
        Objects.requireNonNull(listener, "Listener instance shuld be specified");
        TypedModeledFramework0<T> typedClient = TypedModeledFramework0.from(ModeledFramework.builder(), specBuilder, node);
        CachedModeledFramework<T> cache = typedClient.resolved(asyncFramework).cached();
        cache.start();
        cache.listenable().addListener((type, path, stat, model) -> {
            switch (type) {
                case NODE_REMOVED:
                    listener.removed(path.nodeName());
                    break;
                case NODE_ADDED:
                    listener.added(path.nodeName(), model);
                    break;
                case NODE_UPDATED:
                    listener.updated(path.nodeName(), model);
                    break;
            }

            LOG.trace("Subscribed name: {} ; path: {} ; version: {}", model.getClass().getSimpleName(), path, stat.getVersion());
        });

        subscribers.add(cache);
    }

    private void createNode() throws Exception {
        try {
            if (!isNodeExist(znode)) {
                CreateBuilder createBuilder = client.create();
                createBuilder.creatingParentContainersIfNeeded();
                createBuilder.creatingParentsIfNeeded();
                createBuilder.forPath(znode);
            }
        } catch (Exception e) {
            LOG.error("Error establish connection with the Zookeeper's node " + znode, e);
            throw e;
        }
    }

    public Result<T> save(Object id, T model) {
        Objects.requireNonNull(id, "id instance should be specified");
        Objects.requireNonNull(model, "model instance should be specified");
        // change the affected path to be modeled's base path plus id: i.e. "/example/path/{id}"
        // by default ModeledFramework instances update the node if it already exists
        // so this will either create or update the node
        String nodePath = znode + "/" + id;
        try {
            byte[] data = serializer.serialize(model);
            if (isNodeExist(nodePath)) {
                client.setData().forPath(nodePath, data);
            } else {
                client.create().forPath(nodePath, data);
            }

            return new Result<>(model);
        } catch (Exception e) {
            LOG.error("Error to run transaction save operation for node " + znode + " and id " + id, e);
            return new Result<>("Error to save node " + id, e);
        }
    }


    public Result<List<T>> saveAll(Map<String, T> configSymbols) {
        Objects.requireNonNull(configSymbols, "id instance should be specified");

        CuratorMultiTransaction transaction = client.transaction();
        List<CuratorOp> operations = new ArrayList<>(configSymbols.size());
        Result<List<T>> result = null;
        try {
            List<String> childrenNodes = getChildrenNodes();
            for (String id : configSymbols.keySet()) {
                T model = configSymbols.get(id);
                String nodePath = znode + "/" + id;
                CuratorOp curatorOp = !childrenNodes.contains(id) ?
                        client.transactionOp().create().forPath(nodePath, serializer.serialize(model)) :
                        client.transactionOp().setData().forPath(nodePath, serializer.serialize(model));

                operations.add(curatorOp);
            }
        } catch (Exception e) {
            result = new Result<>("Error to collect transaction save operations for node " + znode, e);
            LOG.error(result.getErrorMessage(), e);
        }

        if (result == null) {
            try {
                List<CuratorTransactionResult> transacResult = transaction.forOperations(operations);
                result = combineTransactionResult(transacResult, new ArrayList<>(configSymbols.values()));
            } catch (Exception e) {
                result = new Result<>("Error to commit transaction for node " + znode, e);
                LOG.error(result.getErrorMessage(), e);
            }
        }

        return result;
    }

    private boolean isNodeExist(String nodePath) throws Exception {
        return client.checkExists().forPath(nodePath) != null;
    }

    public Result<Object> delete(Object id) {
        Objects.requireNonNull(id, "id instance should be specified");
        // change the affected path to be modeled's base path plus id: i.e. "/example/path/{id}"
        // by default ModeledFramework instances update the node if it already exists
        // so this will either create or update the node
        CuratorMultiTransaction transaction = client.transaction();
        try {
            List<CuratorTransactionResult> transacResult = transaction.forOperations(client.transactionOp().delete().forPath(znode + "/" + id));
            return combineTransactionResult(transacResult, id);
        } catch (Exception e) {
            LOG.error("Error to run transaction delete operation for node " + znode + " and id " + id, e);
            return new Result<>("Error to delete node " + id, e);
        }
    }

    public Result<List<String>> deleteAll(List<String> ids) throws Exception {
        Objects.requireNonNull(ids, "id instance should be specified");

        // change the affected path to be modeled's base path plus id: i.e. "/example/path/{id}"
        // by default ModeledFramework instances update the node if it already exists
        // so this will either create or update the node
        CuratorMultiTransaction transaction = client.transaction();
        List<CuratorOp> operations = new ArrayList<>(ids.size());
        for (Object id : ids) {
            try {
                operations.add(client.transactionOp().delete().forPath(znode + "/" + id));
            } catch (Exception e) {
                LOG.error("Error to collect transaction delete operations for node " + znode + " and ids " + ids, e);
                throw e;
            }
        }

        List<CuratorTransactionResult> transacResult = transaction.forOperations(operations);
        return combineTransactionResult(transacResult, ids);
    }

    private <R> Result<R> combineTransactionResult(List<CuratorTransactionResult> transacResult, R data) throws Exception {
        Result<R> result;
        StringBuilder errors = new StringBuilder("Errors: ");
        transacResult.stream().filter(r -> r.getError() != 0).forEach(r -> errors.append("Code: ").append(r.getError())
                .append("; path: ").append(r.getForPath())
                .append("; stat: ").append(r.getResultStat()));
        if (errors.length() > 10) {
            result = new Result<>(errors.toString());
        } else {
            result = new Result<>(data);
        }

        return result;
    }

    public Result<List<T>> readAll() {
        try {
            List<String> childrenNodes = getChildrenNodes();
            if (childrenNodes.isEmpty()) {
                return new Result<>(new ArrayList<>());
            }

            List<Result<T>> nodeValues = childrenNodes.stream().map(childNode -> readFromNode(znode + "/" + childNode)).collect(Collectors.toList());
            List<Result<T>> errors = nodeValues.stream().filter(n -> n.getException() != null).collect(Collectors.toList());
            return !errors.isEmpty() ?
                    new Result<>("Error to get all datas", errors.get(0).getException()) :
                    new Result<>(nodeValues.stream().map(Result::getData).collect(Collectors.toList()));

        } catch (Exception e) {
            LOG.error("Error to collect all children datas for node " + znode, e);
            return new Result<>("Error to collect all children datas for node " + znode, e);
        }
    }

    public Result<T> read(String id) {
        // read the person with the given ID and asynchronously call the receiver after it is read
        Objects.requireNonNull(id, "id instance could not be null");
        String childNode = znode + "/" + id;
        return readFromNode(childNode);
    }

    private Result<T> readFromNode(String childNode) {
        Result<T> result;
        try {
            if (!isNodeExist(childNode)) {
                return new Result<>((!clazz.isPrimitive() ? null : clazz.newInstance()));
            }

            T model = serializer.deserialize(client.getData().forPath(childNode));
            result = new Result<>(model);
        } catch (Exception e) {
            result = new Result<>("Error to collect all children datas for node " + znode, e);
            LOG.error(result.getErrorMessage(), e);
        }

        return result;
    }


    List<String> getChildrenNodes() throws Exception {
        return client.getChildren().forPath(znode);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            subscribers.forEach(CloseableUtils::closeQuietly);
            CloseableUtils.closeQuietly(asyncFramework.unwrap());
            CloseableUtils.closeQuietly(client);
        }
    }

}
