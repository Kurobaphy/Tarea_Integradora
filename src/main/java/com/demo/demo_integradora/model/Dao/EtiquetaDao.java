package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Etiqueta;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class EtiquetaDao implements Dao<Etiqueta, Long> {

    private static final String INSERT = "INSERT INTO etiquetas (nombre) VALUES (?)";
    private static final String SELECT_ALL = "SELECT * FROM etiquetas ORDER BY nombre";
    private static final String SELECT_BY_ID = "SELECT * FROM etiquetas WHERE id = ?";
    private static final String SELECT_BY_NOMBRE = "SELECT * FROM etiquetas WHERE nombre = ?";
    private static final String UPDATE = "UPDATE etiquetas SET nombre = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM etiquetas WHERE id = ?";
    private static final String SELECT_BY_ARTICULO =
            "SELECT e.* FROM etiquetas e " +
            "JOIN articulo_etiquetas ae ON ae.etiqueta_id = e.id " +
            "WHERE ae.articulo_id = ? ORDER BY e.nombre";

    @Override
    public boolean create(Etiqueta etiqueta) {
        try (Connection con = SQLConnector.getConnection()) {
            return create(etiqueta, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean create(Etiqueta etiqueta, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {
            ps.setString(1, etiqueta.getNombre());
            int filas = ps.executeUpdate();
            if (filas == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) etiqueta.setId(keys.getLong(1));
            }
            return true;
        }
    }

    @Override
    public List<Etiqueta> getAll() {
        List<Etiqueta> etiquetas = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) etiquetas.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etiquetas;
    }

    @Override
    public Etiqueta getById(Long id) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Etiqueta getByNombre(String nombre) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_NOMBRE)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    Etiqueta obtenerOCrear(String nombre, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_NOMBRE)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        Etiqueta nueva = new Etiqueta(nombre);
        create(nueva, con);
        return nueva;
    }

    @Override
    public boolean update(Etiqueta etiqueta) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, etiqueta.getNombre());
            ps.setLong(2, etiqueta.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(DELETE)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Etiqueta> getByArticuloId(Long articuloId) {
        try (Connection con = SQLConnector.getConnection()) {
            return getByArticuloId(articuloId, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    List<Etiqueta> getByArticuloId(Long articuloId, Connection con) throws SQLException {
        List<Etiqueta> etiquetas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTICULO)) {
            ps.setLong(1, articuloId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) etiquetas.add(mapRow(rs));
            }
        }
        return etiquetas;
    }

    private Etiqueta mapRow(ResultSet rs) throws SQLException {
        Etiqueta e = new Etiqueta();
        e.setId(rs.getLong("id"));
        e.setNombre(rs.getString("nombre"));
        return e;
    }
}
