package dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Contrato común para las operaciones CRUD de los DAO.
 */
public interface CrudDAO<T> {
    void create(T entidad) throws SQLException;
    List<T> readAll() throws SQLException;
    void update(T entidad) throws SQLException;
    void delete(int id) throws SQLException;
}
