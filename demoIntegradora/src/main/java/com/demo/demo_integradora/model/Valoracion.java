package com.demo.demo_integradora.model;

public class Valoracion {

    private Long id;
    private Long articuloId;
    private Long usuarioId;
    private int puntuacion; // 1 a 5

    public Valoracion() {
    }

    public Valoracion(Long articuloId, Long usuarioId, int puntuacion) {
        this.articuloId = articuloId;
        this.usuarioId = usuarioId;
        this.puntuacion = puntuacion;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
