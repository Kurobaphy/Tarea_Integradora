package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.FichaTecnica;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FichaTecnicaDao implements Dao<FichaTecnica, Long> {

    private static final String INSERT =
            "INSERT INTO ficha_tecnica (desarrollador, fecha_lanzamiento, genero, puntuacion_promedio) " +
            "VALUES (?, ?, ?, 0)";
    private static final String INSERT_PLATAFORMA =
            "INSERT INTO ficha_tecnica_plataformas (ficha_tecnica_id, plataforma) VALUES (?, ?)";
    private static final String DELETE_PLATAFORMAS =
            "DELETE FROM ficha_tecnica_plataformas WHERE ficha_tecnica_id = ?";
    private static final String SELECT_PLATAFORMAS =
            "SELECT plataforma FROM ficha_tecnica_plataformas WHERE ficha_tecnica_id = ? ORDER BY plataforma";
    private static final String SELECT_ALL = "SELECT * FROM ficha_tecnica ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM ficha_tecnica WHERE id = ?";
    private static final String UPDATE =
            "UPDATE ficha_tecnica SET desarrollador = ?, fecha_lanzamiento = ?, genero = ? WHERE id = ?";
    private static final String UPDATE_PROMEDIO =
            "UPDATE ficha_tecnica SET puntuacion_promedio = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM ficha_tecnica WHERE id = ?";
    private static final String SELECT_GENEROS_DISTINTOS =
            "SELECT DISTINCT genero FROM ficha_tecnica WHERE genero IS NOT NULL ORDER BY genero";

    @Override
    public boolean create(FichaTecnica ficha) {
        try (Connection con = SQLConnector.getConnection()) {
            return create(ficha, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Sobrecarga de paquete: usada por ArticuloDao dentro de su propia transacción. */
    boolean create(FichaTecnica ficha, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {
            ps.setString(1, ficha.getDesarrollador());
            ps.setDate(2, ficha.getFechaLanzamiento() != null ? Date.valueOf(ficha.getFechaLanzamiento()) : null);
            ps.setString(3, ficha.getGenero());

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ficha.setId(keys.getLong(1));
            }
        }
        guardarPlataformas(ficha.getId(), ficha.getPlataformas(), con);
        return true;
    }

    private void guardarPlataformas(Long fichaId, List<String> plataformas, Connection con) throws SQLException {
        try (PreparedStatement del = con.prepareStatement(DELETE_PLATAFORMAS)) {
            del.setLong(1, fichaId);
            del.executeUpdate();
        }
        if (plataformas == null || plataformas.isEmpty()) return;

        try (PreparedStatement ps = con.prepareStatement(INSERT_PLATAFORMA)) {
            for (String plataforma : plataformas) {
                ps.setLong(1, fichaId);
                ps.setString(2, plataforma);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<FichaTecnica> getAll() {
        List<FichaTecnica> fichas = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) fichas.add(mapRow(rs, con));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fichas;
    }

    @Override
    public FichaTecnica getById(Long id) {
        try (Connection con = SQLConnector.getConnection()) {
            return getById(id, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Sobrecarga de paquete: usada por ArticuloDao para leer la ficha reutilizando su conexión. */
    FichaTecnica getById(Long id, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs, con) : null;
            }
        }
    }

    @Override
    public boolean update(FichaTecnica ficha) {
        try (Connection con = SQLConnector.getConnection()) {
            return update(ficha, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean update(FichaTecnica ficha, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
            ps.setString(1, ficha.getDesarrollador());
            ps.setDate(2, ficha.getFechaLanzamiento() != null ? Date.valueOf(ficha.getFechaLanzamiento()) : null);
            ps.setString(3, ficha.getGenero());
            ps.setLong(4, ficha.getId());
            int filas = ps.executeUpdate();
            if (filas == 0) return false;
        }
        guardarPlataformas(ficha.getId(), ficha.getPlataformas(), con);
        return true;
    }

    /** Requerimiento 4.2: recalcula y persiste el promedio de estrellas del videojuego. */
    boolean actualizarPromedio(Long fichaId, double promedio, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(UPDATE_PROMEDIO)) {
            ps.setDouble(1, promedio);
            ps.setLong(2, fichaId);
            return ps.executeUpdate() > 0;
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

    /** Requerimiento 3.1: alimenta el <select> de género en el formulario de búsqueda. */
    public List<String> obtenerGenerosDistintos() {
        List<String> generos = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_GENEROS_DISTINTOS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) generos.add(rs.getString("genero"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generos;
    }

    private FichaTecnica mapRow(ResultSet rs, Connection con) throws SQLException {
        FichaTecnica f = new FichaTecnica();
        f.setId(rs.getLong("id"));
        f.setDesarrollador(rs.getString("desarrollador"));
        Date fecha = rs.getDate("fecha_lanzamiento");
        if (fecha != null) f.setFechaLanzamiento(fecha.toLocalDate());
        f.setGenero(rs.getString("genero"));
        f.setPuntuacionPromedio(rs.getDouble("puntuacion_promedio"));
        f.setPlataformas(obtenerPlataformas(f.getId(), con));
        return f;
    }

    private List<String> obtenerPlataformas(Long fichaId, Connection con) throws SQLException {
        List<String> plataformas = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SELECT_PLATAFORMAS)) {
            ps.setLong(1, fichaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) plataformas.add(rs.getString("plataforma"));
            }
        }
        return plataformas;
    }
}
