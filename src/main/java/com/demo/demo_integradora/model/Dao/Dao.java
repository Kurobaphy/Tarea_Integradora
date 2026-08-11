package com.demo.demo_integradora.model.Dao;
import java.util.List;

public interface Dao<T, K> {
    boolean create(T entidad);
    List<T> getAll();
    T getById(K id);
    boolean update(T entidad);
    boolean delete(K id);
}