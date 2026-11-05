package com.gestionventas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String dpi;
    private final String nombre;
    private final String direccion;
    private final String tipo;
    private final LocalDate fechaNacimiento;

    public Cliente(String dpi, String nombre, String direccion, LocalDate fechaNacimiento, String tipo) {
        this.dpi = dpi;
        this.nombre = nombre;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
    }

    public String getDpi() { return dpi; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getTipo() { return tipo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente that)) return false;
        return Objects.equals(dpi, that.dpi);
    }

    @Override
    public int hashCode() {
        return dpi != null ? dpi.hashCode() : 0;
    }

    @Override
    public String toString() {
        return nombre + " (DPI: " + dpi + " - " + tipo + ")";
    }
}
