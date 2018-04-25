package ru.ulmc.bank.core.dao;

import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.ulmc.bank.core.common.exception.JdbcException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class TransactionManager implements AutoCloseable {

    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get connection from JDBC datasource
     *
     * @return JDBC connection
     * @apiNote have to close the connection by try with resources or into try/finally blocks
     */
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error while get connection from the datasource. Error code: {} ; Sql state: {}, message: {}",
                    e.getErrorCode(), e.getSQLState(), e.getMessage(), e);
            throw new JdbcException("Ошибка при выполнении запроса " + e.getMessage(), e);
        }
    }

    public <T> Optional<T> get(@NonNull Function<Connection, T> transaction) {
        log.trace("Init transaction");
        try (Connection connection = getConnection()) {
            try {
                connection.setAutoCommit(false);
                Optional<T> optional = Optional.ofNullable(transaction.apply(connection));
                connection.commit();
                return optional;
            } catch (SQLException e) {
                log.error("Error while commit changes. Error code: {} ; Sql state: {}, message: {}",
                        e.getErrorCode(), e.getSQLState(), e.getMessage(), e);
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("Error while rollback changes. Error code: {} ; Sql state: {}, message: {}",
                            e.getErrorCode(), e.getSQLState(), e.getMessage(), e);
                    throw new JdbcException("Ошибка при откате транзакции " + ex.getMessage(), ex);
                }
                throw new JdbcException("Ошибка при выполнении запроса " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            log.error("Error while rollback changes. Error code: {} ; Sql state: {}, message: {}",
                    e.getErrorCode(), e.getSQLState(), e.getMessage(), e);
            throw new JdbcException("Ошибка при выполнении запроса", e);
        }
    }

    public void run(@NonNull Consumer<Connection> transaction) {
        get(con -> {
            transaction.accept(con);
            return null;
        });
    }

    @Override
    public void close() {
        log.debug("Dispose datasource");
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
