package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Valoracion;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ValoracionDao implements Dao<Valoracion, Long> {

    private static final String INSERT =
            "INSERT INTO valoraciones (articulo_id, usuario_id, puntuacion) VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM valoraciones ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM valoraciones WHERE id = ?";
    private static final String SELECT_BY_USUARIO_ARTICULO =
            "SELECT * FROM valoraciones WHERE usuario_id = ? AND articulo_id = ?";
    private static final String UPDATE_PUNTUACION = "UPDATE valoraciones SET puntuacion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM valoraciones WHERE id = ?";
    private static final String RECALCULAR_PROMEDIO =
            "UPDATE ficha_tecnica SET puntuacion_promedio = (" +
            "  SELECT NVL(AVG(v.puntuacion), 0) FROM valoraciones v WHERE v.articulo_id = ?" +
            ") WHERE id = (SELECT ficha_tecnica_id FROM articulos WHERE id = ?)";

    public boolean guardarOActualizar(Valoracion valoracion) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                Valoracion existente = getByUsuarioYArticulo(
                        valoracion.getUsuarioId(), valoracion.getArticuloId(), con);

                if (existente != null) {
                    valoracion.setId(existente.getId());
                    try (PreparedStatement ps = con.prepareStatement(UPDATE_PUNTUACION)) {
                        ps.setInt(1, valoracion.getPuntuacion());
                        ps.setLong(2, valoracion.getId());
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {
                        ps.setLong(1, valoracion.getArticuloId());
                        ps.setLong(2, valoracion.getUsuarioId());
                        ps.setInt(3, valoracion.getPuntuacion());
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) valoracion.setId(keys.getLong(1));
                        }
                    }
                }

                recalcularPromedio(valoracion.getArticuloId(), con);

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void recalcularPromedio(Long articuloId, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(RECALCULAR_PROMEDIO)) {
            ps.setLong(1, articuloId);
            ps.setLong(2, articuloId);
            ps.executeUpdate();
        }
    }

    public Valoracion getByUsuarioYArticulo(Long usuarioId, Long articuloId) {
        try (Connection con = SQLConnector.getConnection()) {
            return getByUsuarioYArticulo(usuarioId, articuloId, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Valoracion getByUsuarioYArticulo(Long usuarioId, Long articuloId, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_USUARIO_ARTICULO)) {
            ps.setLong(1, usuarioId);
            ps.setLong(2, articuloId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public boolean create(Valoracion valoracion) {
        return guardarOActualizar(valoracion);
    }

    @Override
    public List<Valoracion> getAll() {
        List<Valoracion> valoraciones = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) valoraciones.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valoraciones;
    }

    @Override
    public Valoracion getById(Long id) {
        try (Connection con = SQLConnector.getConnection()) {
            return getById(id, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Valoracion getById(Long id, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    @Override
    public boolean update(Valoracion valoracion) {
        return guardarOActualizar(valoracion);
    }

    @Override
    public boolean delete(Long id) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                Valoracion valoracion = getById(id, con);
                if (valoracion == null) {
                    con.rollback();
                    return false;
                }
                try (PreparedStatement ps = con.prepareStatement(DELETE)) {
                    ps.setLong(1, id);
                    ps.executeUpdate();
                }
                recalcularPromedio(valoracion.getArticuloId(), con);
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Valoracion mapRow(ResultSet rs) throws SQLException {
        Valoracion v = new Valoracion();
        v.setId(rs.getLong("id"));
        v.setArticuloId(rs.getLong("articulo_id"));
        v.setUsuarioId(rs.getLong("usuario_id"));
        v.setPuntuacion(rs.getInt("puntuacion"));
        return v;
    }
}
