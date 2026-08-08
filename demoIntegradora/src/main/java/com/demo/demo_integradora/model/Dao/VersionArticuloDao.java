package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.model.VersionArticulo;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VersionArticuloDao implements Dao<VersionArticulo, Long> {

    private static final String BASE_SELECT =
            "SELECT v.id, v.articulo_id, v.contenido_snapshot, v.autor_edicion_id, v.fecha_hora, v.tipo_accion, " +
            "u.nombre_usuario AS autor_nombre " +
            "FROM versiones_articulo v JOIN usuarios u ON u.id = v.autor_edicion_id ";

    private static final String INSERT =
            "INSERT INTO versiones_articulo (articulo_id, contenido_snapshot, autor_edicion_id, fecha_hora, tipo_accion) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL = BASE_SELECT + "ORDER BY v.fecha_hora DESC";
    private static final String SELECT_BY_ID = BASE_SELECT + "WHERE v.id = ?";
    private static final String SELECT_BY_ARTICULO = BASE_SELECT + "WHERE v.articulo_id = ? ORDER BY v.fecha_hora DESC";
    private static final String DELETE = "DELETE FROM versiones_articulo WHERE id = ?";

    @Override
    public boolean create(VersionArticulo version) {
        try (Connection con = SQLConnector.getConnection()) {
            return create(version, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean create(VersionArticulo version, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {
            ps.setLong(1, version.getArticuloId());
            ps.setString(2, version.getContenidoSnapshot());
            ps.setLong(3, version.getAutorEdicion().getId());
            ps.setTimestamp(4, Timestamp.valueOf(version.getFechaHora()));
            ps.setString(5, version.getTipoAccion().name());

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) version.setId(keys.getLong(1));
            }
            return true;
        }
    }

    @Override
    public List<VersionArticulo> getAll() {
        List<VersionArticulo> versiones = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) versiones.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return versiones;
    }

    @Override
    public VersionArticulo getById(Long id) {
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

    public List<VersionArticulo> getByArticuloId(Long articuloId) {
        List<VersionArticulo> versiones = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTICULO)) {
            ps.setLong(1, articuloId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) versiones.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return versiones;
    }

    @Override
    public boolean update(VersionArticulo version) {
        return false;
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

    private VersionArticulo mapRow(ResultSet rs) throws SQLException {
        VersionArticulo v = new VersionArticulo();
        v.setId(rs.getLong("id"));
        v.setArticuloId(rs.getLong("articulo_id"));
        v.setContenidoSnapshot(rs.getString("contenido_snapshot"));
        v.setTipoAccion(VersionArticulo.TipoAccion.valueOf(rs.getString("tipo_accion")));

        Timestamp fecha = rs.getTimestamp("fecha_hora");
        if (fecha != null) v.setFechaHora(fecha.toLocalDateTime());

        Usuario autor = new Usuario();
        autor.setId(rs.getLong("autor_edicion_id"));
        autor.setNombreUsuario(rs.getString("autor_nombre"));
        v.setAutorEdicion(autor);

        return v;
    }
}
