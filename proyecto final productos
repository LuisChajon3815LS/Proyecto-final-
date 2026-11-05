package com.gestionventas;

import java.io.Serializable;

public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final String marca;
    private final double precio;

    public Producto(String codigo, String nombre, String descripcion, String marca, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.precio = precio;
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getMarca() { return marca; }
    public double getPrecio() { return precio; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto that)) return false;
        return codigo != null && codigo.equalsIgnoreCase(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (Q" + String.format("%.2f", precio) + ")";
    }
}
