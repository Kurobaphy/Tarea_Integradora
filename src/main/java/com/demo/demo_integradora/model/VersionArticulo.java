package com.demo.demo_integradora.model;

import java.time.LocalDateTime;

public class VersionArticulo {

    public enum TipoAccion {
        CREACION, EDICION, REVERSION
    }

    private Long id;
    private Long articuloId;
    private String contenidoSnapshot;
    private Usuario autorEdicion;
    private LocalDateTime fechaHora;
    private TipoAccion tipoAccion;

    public VersionArticulo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Long articuloId) {
        this.articuloId = articuloId;
    }

    public String getContenidoSnapshot() {
        return contenidoSnapshot;
    }

    public void setContenidoSnapshot(String contenidoSnapshot) {
        this.contenidoSnapshot = contenidoSnapshot;
    }

    public Usuario getAutorEdicion() {
        return autorEdicion;
    }

    public void setAutorEdicion(Usuario autorEdicion) {
        this.autorEdicion = autorEdicion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoAccion getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(TipoAccion tipoAccion) {
        this.tipoAccion = tipoAccion;
    }
}
