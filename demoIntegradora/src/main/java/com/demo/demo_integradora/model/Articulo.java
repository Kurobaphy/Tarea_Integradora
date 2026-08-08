package com.demo.demo_integradora.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Articulo {

    private Long id;
    private String titulo;
    private String cuerpoTexto;
    private Usuario autor;
    private FichaTecnica fichaTecnica;
    private List<Etiqueta> etiquetas = new ArrayList<>();
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public Articulo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpoTexto() {
        return cuerpoTexto;
    }

    public void setCuerpoTexto(String cuerpoTexto) {
        this.cuerpoTexto = cuerpoTexto;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public FichaTecnica getFichaTecnica() {
        return fichaTecnica;
    }

    public void setFichaTecnica(FichaTecnica fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
    }

    public List<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<Etiqueta> etiquetas) {
        this.etiquetas = etiquetas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
