package com.demo.demo_integradora.model.Dao;

import com.demo.demo_integradora.model.Rol;
import com.demo.demo_integradora.model.Usuario;
import com.demo.demo_integradora.utils.SQLConnector;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDao implements Dao<Usuario, Long> {

    private static final String INSERT =
            "INSERT INTO usuarios (nombre_usuario, correo_electronico, contrasena_hash, rol, intentos_fallidos, fecha_registro) " +
            "VALUES (?, ?, ?, ?, 0, ?)";
    private static final String SELECT_ALL = "SELECT * FROM usuarios ORDER BY id";
    private static final String SELECT_BY_ID = "SELECT * FROM usuarios WHERE id = ?";
    private static final String SELECT_BY_CORREO = "SELECT * FROM usuarios WHERE correo_electronico = ?";
    private static final String UPDATE =
            "UPDATE usuarios SET nombre_usuario = ?, correo_electronico = ?, contrasena_hash = ?, rol = ?, " +
            "intentos_fallidos = ?, bloqueado_hasta = ? WHERE id = ?";
    private static final String DELETE = "DELETE FROM usuarios WHERE id = ?";
    private static final String INCREMENTAR_INTENTOS =
            "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE id = ?";
    private static final String RESETEAR_INTENTOS =
            "UPDATE usuarios SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id = ?";
    private static final String BLOQUEAR =
            "UPDATE usuarios SET bloqueado_hasta = ? WHERE id = ?";

    @Override
    public boolean create(Usuario usuario) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(INSERT, new String[]{"id"})) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getCorreoElectronico());
            ps.setString(3, usuario.getContrasenaHash());
            ps.setString(4, usuario.getRol().name());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int filas = ps.executeUpdate();
            if (filas == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getLong(1));
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Usuario> getAll() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    @Override
    public Usuario getById(Long id) {
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

    public Usuario getByCorreo(String correo) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_BY_CORREO)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean update(Usuario usuario) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE)) {

            ps.setString(1, usuario.getNombreUsuario());
            ps.setString(2, usuario.getCorreoElectronico());
            ps.setString(3, usuario.getContrasenaHash());
            ps.setString(4, usuario.getRol().name());
            ps.setInt(5, usuario.getIntentosFallidos());
            if (usuario.getBloqueadoHasta() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(usuario.getBloqueadoHasta()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }
            ps.setLong(7, usuario.getId());

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

    public boolean incrementarIntentosFallidos(Long id) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(INCREMENTAR_INTENTOS)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean bloquearHasta(Long id, LocalDateTime hasta) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(BLOQUEAR)) {
            ps.setTimestamp(1, Timestamp.valueOf(hasta));
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean resetearIntentos(Long id) {
        try (Connection con = SQLConnector.getConnection();
             PreparedStatement ps = con.prepareStatement(RESETEAR_INTENTOS)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setCorreoElectronico(rs.getString("correo_electronico"));
        u.setContrasenaHash(rs.getString("contrasena_hash"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));

        Timestamp bloqueado = rs.getTimestamp("bloqueado_hasta");
        if (bloqueado != null) {
            u.setBloqueadoHasta(bloqueado.toLocalDateTime());
        }

        Timestamp registro = rs.getTimestamp("fecha_registro");
        if (registro != null) {
            u.setFechaRegistro(registro.toLocalDateTime());
        }

        return u;
    }
}
