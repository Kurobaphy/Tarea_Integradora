package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Comentario;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ComentarioDao implements Dao<Comentario, Long> {

    private static final String BASE_SELECT =
            "SELECT c.id, c.articulo_id, c.autor_id, c.texto, c.fecha_hora, u.nombre_usuario AS autor_nombre " +
            "FROM comentarios c JOIN usuarios u ON u.id = c.autor_id ";

    private static final String INSERT =
            "INSERT INTO comentarios (articulo_id, autor_id, texto, fecha_hora) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL = BASE_SELECT + "ORDER BY c.fecha_hora DESC";
    private static final String SELECT_BY_ID = BASE_SELECT + "WHERE c.id = ?";
    private static final String SELECT_BY_ARTICULO = BASE_SELECT + "WHERE c.articulo_id = ? ORDER BY c.fecha_hora ASC";
    private static final String UPDATE = "UPDATE comentarios SET texto = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM comentarios WHERE id = ?";

    @Override
    public boolean create(Comentario comentario) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {

            ps.setLong(1, comentario.getArticuloId());
            ps.setLong(2, comentario.getAutor().getId());
            ps.setString(3, comentario.getTexto());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) comentario.setId(keys.getLong(1));
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Comentario> getAll() {
        List<Comentario> comentarios = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) comentarios.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comentarios;
    }

    @Override
    public Comentario getById(Long id) {
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

    public List<Comentario> getByArticuloId(Long articuloId) {
        List<Comentario> comentarios = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ARTICULO)) {
            ps.setLong(1, articuloId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) comentarios.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comentarios;
    }

    @Override
    public boolean update(Comentario comentario) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, comentario.getTexto());
            ps.setLong(2, comentario.getId());
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

    private Comentario mapRow(ResultSet rs) throws SQLException {
        Comentario c = new Comentario();
        c.setId(rs.getLong("id"));
        c.setArticuloId(rs.getLong("articulo_id"));
        c.setTexto(rs.getString("texto"));

        Timestamp fecha = rs.getTimestamp("fecha_hora");
        if (fecha != null) c.setFechaHora(fecha.toLocalDateTime());

        Usuario autor = new Usuario();
        autor.setId(rs.getLong("autor_id"));
        autor.setNombreUsuario(rs.getString("autor_nombre"));
        c.setAutor(autor);

        return c;
    }
}
