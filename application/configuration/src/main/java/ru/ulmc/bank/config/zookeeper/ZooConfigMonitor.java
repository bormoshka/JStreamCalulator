package ru.ulmc.bank.config.zookeeper;

import lombok.NonNull;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.transaction.CuratorMultiTransaction;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.async.AsyncCuratorFramework;
import org.apache.curator.x.async.modeled.*;
import org.apache.curator.x.async.modeled.cached.CachedModeledFramework;
import org.apache.curator.x.async.modeled.typed.TypedModeledFramework0;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ulmc.bank.config.zookeeper.serializers.JsonModelSerializer;
import ru.ulmc.bank.config.zookeeper.serializers.StringModelSerializer;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZooConfigMonitor<T> implements AutoCloseable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ZooConfigMonitor.class);

    private final CuratorFramework client;
    private final AsyncCuratorFramework asyncFramework;
    private final Class<T> clazz;
    private final List<CachedModeledFramework<T>> subscribers = new ArrayList<>();
    private ModelSpecBuilder<T> specBuilder;
    private String zNode;
    private boolean closed;
    private ModelSerializer<T> serializer;

    /**
     * @param connectString comma separated host:port like "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002"
     * @throws Exception in cases of network failure
     */
    public ZooConfigMonitor(@NonNull String connectString, @NonNull String zNode, @NonNull Class<T> clazz)
            throws Exception {
        this.zNode = zNode.startsWith("/") ? zNode : "/" + zNode;
        this.clazz = clazz;
        serializer = clazz == String.class ?
                (ModelSerializer<T>) new StringModelSerializer() :
                JsonModelSerializer.build(clazz);
        client = CuratorFrameworkFactory.newClient(connectString, new RetryNTimes(10, 500));

        asyncFramework = AsyncCuratorFramework.wrap(client);
        specBuilder = ModelSpec.builder(ZPath.parse(zNode), serializer)
                .withTtl(-1)
                .withCreateMode(CreateMode.PERSISTENT);
        client.start();
        createNode();
    }

    public ZooConfigMonitor(@NonNull String connectString, @NonNull String zNode, @NonNull Class<T> clazz,
                            @NonNull Map<String, T> mapStore) throws Exception {
        this(connectString, zNode, clazz);
        startSubscriber(new Listener<T>() {
            @Override
            public void added(String key, T model) {
                LOG.debug("Added to node " + key + " zoo path " + zNode);
                mapStore.put(key, model);
            }

            @Override
            public void updated(String key, T model) {
                LOG.debug("Updated into node " + key + " zoo path " + zNode);
                mapStore.put(key, model);
            }

            @Override
            public void removed(String key) {
                LOG.debug("Removed from node " + key + " zoo path " + zNode);
                mapStore.remove(key);
            }
        });
    }

    public void startSubscriber(Listener<T> listener) {
        startSubscriber(zNode + "/{id}", listener);
    }

    private void startSubscriber(String node, @NonNull Listener<T> listener) {
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
            if (!isNodeExist(zNode)) {
                CreateBuilder createBuilder = client.create();
                createBuilder.creatingParentContainersIfNeeded();
                createBuilder.creatingParentsIfNeeded();
                createBuilder.forPath(zNode);
            }
        } catch (Exception e) {
            LOG.error("Error establish connection with the Zookeeper's node " + zNode, e);
            throw e;
        }
    }

    public Result<T> save(@NonNull Object id, @NonNull T model) {
        String nodePath = zNode + "/" + id;
        try {
            byte[] data = serializer.serialize(model);
            if (isNodeExist(nodePath)) {
                client.setData().forPath(nodePath, data);
            } else {
                client.create().forPath(nodePath, data);
            }

            return new Result<>(model);
        } catch (Exception e) {
            LOG.error("Error to run transaction save operation for node " + zNode + " and id " + id, e);
            return new Result<>("Error to save node " + id, e);
        }
    }


    public Result<List<T>> saveAll(@NonNull Map<String, T> configSymbols) {
        CuratorMultiTransaction transaction = client.transaction();
        List<CuratorOp> operations = new ArrayList<>(configSymbols.size());
        Result<List<T>> result = null;
        try {
            List<String> childrenNodes = getChildrenNodes();
            for (String id : configSymbols.keySet()) {
                T model = configSymbols.get(id);
                String nodePath = zNode + "/" + id;
                CuratorOp curatorOp = !childrenNodes.contains(id) ?
                        client.transactionOp().create().forPath(nodePath, serializer.serialize(model)) :
                        client.transactionOp().setData().forPath(nodePath, serializer.serialize(model));

                operations.add(curatorOp);
            }
        } catch (Exception e) {
            result = new Result<>("Error to collect transaction save operations for node " + zNode, e);
            LOG.error(result.getErrorMessage(), e);
        }

        if (result == null) {
            try {
                List<CuratorTransactionResult> transacResult = transaction.forOperations(operations);
                result = combineTransactionResult(transacResult, new ArrayList<>(configSymbols.values()));
            } catch (Exception e) {
                result = new Result<>("Error to commit transaction for node " + zNode, e);
                LOG.error(result.getErrorMessage(), e);
            }
        }

        return result;
    }

    private boolean isNodeExist(String nodePath) throws Exception {
        return client.checkExists().forPath(nodePath) != null;
    }

    public Result<Object> delete(@NonNull Object id) {
        CuratorMultiTransaction transaction = client.transaction();
        try {
            List<CuratorTransactionResult> transacResult = transaction.forOperations(client.transactionOp().delete().forPath(zNode + "/" + id));
            return combineTransactionResult(transacResult, id);
        } catch (Exception e) {
            LOG.error("Error to run transaction delete operation for node " + zNode + " and id " + id, e);
            return new Result<>("Error to delete node " + id, e);
        }
    }

    private <R> Result<R> combineTransactionResult(List<CuratorTransactionResult> transactResult, R data) throws Exception {
        Result<R> result;
        StringBuilder errors = new StringBuilder("Errors: ");
        transactResult.stream().filter(r -> r.getError() != 0).forEach(r -> errors.append("Code: ").append(r.getError())
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

            List<Result<T>> nodeValues = childrenNodes.stream().map(this::readFromNode).collect(Collectors.toList());
            List<Result<T>> errors = nodeValues.stream().filter(n -> n.getException() != null).collect(Collectors.toList());
            return !errors.isEmpty() ?
                    new Result<>("Error to get all data", errors.get(0).getException()) :
                    new Result<>(nodeValues.stream().map(Result::getData).collect(Collectors.toList()));

        } catch (Exception e) {
            LOG.error("Error to collect all children data for node " + zNode, e);
            return new Result<>("Error to collect all children data for node " + zNode, e);
        }
    }

    public List<Result<T>> readAllUnmodified() {
        try {
            List<String> childrenNodes = getChildrenNodes();
            if (childrenNodes.isEmpty()) {
                return new ArrayList<>();
            }

            List<Result<T>> nodeValues = childrenNodes.stream().map(this::readFromNode).collect(Collectors.toList());
            List<Result<T>> errors = nodeValues.stream().filter(n -> n.getException() != null).collect(Collectors.toList());
            return !errors.isEmpty() ?
                    Collections.singletonList(new Result<>("Error to get all data", errors.get(0).getException())) :
                    nodeValues;

        } catch (Exception e) {
            LOG.error("Error to collect all children data for node " + zNode, e);
            return Collections.singletonList(new Result<>("Error to collect all children data for node " + zNode, e));
        }
    }

    public Result<T> read(@NonNull String id) {
        return readFromNode(id);
    }

    private Result<T> readFromNode(String id) {
        Result<T> result;
        String childNode = zNode + "/" + id;
        try {
            if (!isNodeExist(childNode)) {
                return new Result<>((!clazz.isPrimitive() ? null : clazz.newInstance()));
            }

            T model = serializer.deserialize(client.getData().forPath(childNode));
            result = new Result<>(id, model);
        } catch (Exception e) {
            result = new Result<>("Error to collect all children datas for node " + zNode, e);
            LOG.error(result.getErrorMessage(), e);
        }

        return result;
    }


    private List<String> getChildrenNodes() throws Exception {
        return client.getChildren().forPath(zNode);
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
