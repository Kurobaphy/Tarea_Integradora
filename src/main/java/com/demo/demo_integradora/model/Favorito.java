package com.demo.demo_integradora.model;

import java.time.LocalDateTime;

public class Favorito {

    private Long id;
    private Long usuarioId;
    private Long articuloId;
    private LocalDateTime fechaAgregado;

    public Favorito() {
    }

    public Favorito(Long usuarioId, Long articuloId) {
        this.usuarioId = usuarioId;
        this.articuloId = articuloId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Long articuloId) {
        this.articuloId = articuloId;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }
}
