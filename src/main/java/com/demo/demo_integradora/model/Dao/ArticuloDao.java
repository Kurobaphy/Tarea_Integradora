package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Articulo;
import com.demo.demo_integradora.model.Etiqueta;
import com.demo.demo_integradora.model.FichaTecnica;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.model.VersionArticulo;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDao implements Dao<Articulo, Long> {

    private final FichaTecnicaDao fichaTecnicaDao = new FichaTecnicaDao();
    private final EtiquetaDao etiquetaDao = new EtiquetaDao();
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final VersionArticuloDao versionArticuloDao = new VersionArticuloDao();

    private static final String INSERT =
            "INSERT INTO articulos (titulo, cuerpo_texto, autor_id, ficha_tecnica_id, fecha_creacion) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ETIQUETA =
            "INSERT INTO articulo_etiquetas (articulo_id, etiqueta_id) VALUES (?, ?)";
    private static final String DELETE_ETIQUETAS =
            "DELETE FROM articulo_etiquetas WHERE articulo_id = ?";
    private static final String SELECT_ALL = "SELECT * FROM articulos ORDER BY titulo";
    private static final String SELECT_BY_ID = "SELECT * FROM articulos WHERE id = ?";
    private static final String SELECT_BY_ETIQUETA =
            "SELECT a.* FROM articulos a " +
            "JOIN articulo_etiquetas ae ON ae.articulo_id = a.id " +
            "WHERE ae.etiqueta_id = ? ORDER BY a.titulo";
    private static final String UPDATE =
            "UPDATE articulos SET titulo = ?, cuerpo_texto = ?, fecha_actualizacion = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM articulos WHERE id = ?";

    @Override
    public boolean create(Articulo articulo) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                fichaTecnicaDao.create(articulo.getFichaTecnica(), con);

                try (PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {
                    ps.setString(1, articulo.getTitulo());
                    ps.setString(2, articulo.getCuerpoTexto());
                    ps.setLong(3, articulo.getAutor().getId());
                    ps.setLong(4, articulo.getFichaTecnica().getId());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                    int filas = ps.executeUpdate();
                    if (filas == 0) throw new SQLException("No se pudo insertar el artículo");

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) articulo.setId(keys.getLong(1));
                    }
                }

                guardarEtiquetas(articulo, con);

                versionArticuloDao.create(construirVersion(articulo, VersionArticulo.TipoAccion.CREACION), con);

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

    private VersionArticulo construirVersion(Articulo articulo, VersionArticulo.TipoAccion tipo) {
        VersionArticulo version = new VersionArticulo();
        version.setArticuloId(articulo.getId());
        version.setContenidoSnapshot(articulo.getCuerpoTexto());
        version.setAutorEdicion(articulo.getAutor());
        version.setFechaHora(LocalDateTime.now());
        version.setTipoAccion(tipo);
        return version;
    }

    private void guardarEtiquetas(Articulo articulo, Connection con) throws SQLException {
        try (PreparedStatement del = con.prepareStatement(DELETE_ETIQUETAS)) {
            del.setLong(1, articulo.getId());
            del.executeUpdate();
        }
        if (articulo.getEtiquetas() == null || articulo.getEtiquetas().isEmpty()) return;

        try (PreparedStatement ps = con.prepareStatement(INSERT_ETIQUETA)) {
            for (Etiqueta etiqueta : articulo.getEtiquetas()) {
                Etiqueta resuelta = (etiqueta.getId() == null)
                        ? etiquetaDao.obtenerOCrear(etiqueta.getNombre(), con)
                        : etiqueta;
                ps.setLong(1, articulo.getId());
                ps.setLong(2, resuelta.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<Articulo> getAll() {
        List<Articulo> articulos = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) articulos.add(mapRow(rs, con));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articulos;
    }

    @Override
    public Articulo getById(Long id) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ID)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs, con) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Articulo> getByEtiquetaId(Long etiquetaId) {
        List<Articulo> articulos = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_ETIQUETA)) {
            ps.setLong(1, etiquetaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) articulos.add(mapRow(rs, con));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articulos;
    }

    public List<Articulo> buscar(String nombre, Integer anio, String plataforma,
                                  String genero, String desarrollador, String orden) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.* FROM articulos a JOIN ficha_tecnica f ON f.id = a.ficha_tecnica_id ");

        boolean filtraPlataforma = plataforma != null && !plataforma.trim().isEmpty();
        if (filtraPlataforma) {
            sql.append("JOIN ficha_tecnica_plataformas p ON p.ficha_tecnica_id = f.id ");
        }

        List<Object> parametros = new ArrayList<>();
        List<String> condiciones = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            condiciones.add("LOWER(a.titulo) LIKE ?");
            parametros.add("%" + nombre.trim().toLowerCase() + "%");
        }
        if (anio != null) {
            condiciones.add("EXTRACT(YEAR FROM f.fecha_lanzamiento) = ?");
            parametros.add(anio);
        }
        if (filtraPlataforma) {
            condiciones.add("TRIM(LOWER(p.plataforma)) = ?");
            parametros.add(plataforma.trim().toLowerCase());
        }
        if (genero != null && !genero.trim().isEmpty()) {
            condiciones.add("TRIM(LOWER(f.genero)) = ?");
            parametros.add(genero.trim().toLowerCase());
        }
        if (desarrollador != null && !desarrollador.trim().isEmpty()) {
            condiciones.add("LOWER(f.desarrollador) LIKE ?");
            parametros.add("%" + desarrollador.trim().toLowerCase() + "%");
        }

        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(" ");
        }
        sql.append("ORDER BY a.titulo ").append("DESC".equalsIgnoreCase(orden) ? "DESC" : "ASC");

        List<Articulo> resultado = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) resultado.add(mapRow(rs, con));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    @Override
    public boolean update(Articulo articulo) {
        return update(articulo, articulo.getAutor());
    }

    public boolean update(Articulo articulo, Usuario editor) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                    ps.setString(1, articulo.getTitulo());
                    ps.setString(2, articulo.getCuerpoTexto());
                    ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setLong(4, articulo.getId());
                    int filas = ps.executeUpdate();
                    if (filas == 0) throw new SQLException("Artículo no encontrado: " + articulo.getId());
                }

                if (articulo.getFichaTecnica() != null) {
                    fichaTecnicaDao.update(articulo.getFichaTecnica(), con);
                }
                guardarEtiquetas(articulo, con);

                VersionArticulo version = construirVersion(articulo, VersionArticulo.TipoAccion.EDICION);
                version.setAutorEdicion(editor);
                versionArticuloDao.create(version, con);

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

    public boolean revertirAVersion(Long articuloId, VersionArticulo versionObjetivo, Usuario administrador) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE articulos SET cuerpo_texto = ?, fecha_actualizacion = ? WHERE id = ?")) {
                    ps.setString(1, versionObjetivo.getContenidoSnapshot());
                    ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setLong(3, articuloId);
                    int filas = ps.executeUpdate();
                    if (filas == 0) throw new SQLException("Artículo no encontrado: " + articuloId);
                }

                VersionArticulo nuevaVersion = new VersionArticulo();
                nuevaVersion.setArticuloId(articuloId);
                nuevaVersion.setContenidoSnapshot(versionObjetivo.getContenidoSnapshot());
                nuevaVersion.setAutorEdicion(administrador);
                nuevaVersion.setFechaHora(LocalDateTime.now());
                nuevaVersion.setTipoAccion(VersionArticulo.TipoAccion.REVERSION);
                versionArticuloDao.create(nuevaVersion, con);

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

    @Override
    public boolean delete(Long id) {
        try (Connection con = SQLConnector.getConnection()) {
            con.setAutoCommit(false);
            try {
                Long fichaTecnicaId = null;
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT ficha_tecnica_id FROM articulos WHERE id = ?")) {
                    ps.setLong(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) fichaTecnicaId = rs.getLong("ficha_tecnica_id");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(DELETE)) {
                    ps.setLong(1, id);
                    int filas = ps.executeUpdate();
                    if (filas == 0) {
                        con.rollback();
                        return false;
                    }
                }

                if (fichaTecnicaId != null) {
                    try (PreparedStatement ps = con.prepareStatement("DELETE FROM ficha_tecnica WHERE id = ?")) {
                        ps.setLong(1, fichaTecnicaId);
                        ps.executeUpdate();
                    }
                }

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

    private Articulo mapRow(ResultSet rs, Connection con) throws SQLException {
        Articulo a = new Articulo();
        a.setId(rs.getLong("id"));
        a.setTitulo(rs.getString("titulo"));
        a.setCuerpoTexto(rs.getString("cuerpo_texto"));

        Usuario autor = usuarioDao.getById(rs.getLong("autor_id"));
        a.setAutor(autor);

        FichaTecnica ficha = fichaTecnicaDao.getById(rs.getLong("ficha_tecnica_id"), con);
        a.setFichaTecnica(ficha);

        a.setEtiquetas(etiquetaDao.getByArticuloId(a.getId(), con));

        Timestamp creacion = rs.getTimestamp("fecha_creacion");
        if (creacion != null) a.setFechaCreacion(creacion.toLocalDateTime());

        Timestamp actualizacion = rs.getTimestamp("fecha_actualizacion");
        if (actualizacion != null) a.setFechaActualizacion(actualizacion.toLocalDateTime());

        return a;
    }
}
