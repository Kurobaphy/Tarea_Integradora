package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Favorito;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class FavoritoDao implements Dao<Favorito, Long> {

    private final ArticuloDao articuloDao = new ArticuloDao();

    private static final String INSERT =
            "INSERT INTO favoritos (usuario_id, articulo_id, fecha_agregado) VALUES (?, ?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM favoritos ORDER BY fecha_agregado DESC";
    private static final String SELECT_BY_ID = "SELECT * FROM favoritos WHERE id = ?";
    private static final String SELECT_BY_USUARIO =
            "SELECT * FROM favoritos WHERE usuario_id = ? ORDER BY fecha_agregado DESC";
    private static final String SELECT_BY_USUARIO_ARTICULO =
            "SELECT * FROM favoritos WHERE usuario_id = ? AND articulo_id = ?";
    private static final String DELETE = "DELETE FROM favoritos WHERE id = ?";

    @Override
    public boolean create(Favorito favorito) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {

            ps.setLong(1, favorito.getUsuarioId());
            ps.setLong(2, favorito.getArticuloId());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) favorito.setId(keys.getLong(1));
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Favorito> getAll() {
        List<Favorito> favoritos = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) favoritos.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoritos;
    }

    @Override
    public Favorito getById(Long id) {
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

    public List<Favorito> getByUsuarioId(Long usuarioId) {
        List<Favorito> favoritos = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_USUARIO)) {
            ps.setLong(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) favoritos.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoritos;
    }

    public List<Articulo> getArticulosFavoritos(Long usuarioId) {
        List<Articulo> articulos = new ArrayList<>();
        for (Favorito favorito : getByUsuarioId(usuarioId)) {
            Articulo articulo = articuloDao.getById(favorito.getArticuloId());
            if (articulo != null) articulos.add(articulo);
        }
        return articulos;
    }

    public Favorito getByUsuarioYArticulo(Long usuarioId, Long articuloId) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_USUARIO_ARTICULO)) {
            ps.setLong(1, usuarioId);
            ps.setLong(2, articuloId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean alternar(Long usuarioId, Long articuloId) {
        Favorito existente = getByUsuarioYArticulo(usuarioId, articuloId);
        if (existente != null) {
            return delete(existente.getId());
        }
        return create(new Favorito(usuarioId, articuloId));
    }

    @Override
    public boolean update(Favorito favorito) {
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

    private Favorito mapRow(ResultSet rs) throws SQLException {
        Favorito f = new Favorito();
        f.setId(rs.getLong("id"));
        f.setUsuarioId(rs.getLong("usuario_id"));
        f.setArticuloId(rs.getLong("articulo_id"));
        Timestamp fecha = rs.getTimestamp("fecha_agregado");
        if (fecha != null) f.setFechaAgregado(fecha.toLocalDateTime());
        return f;
    }
}
