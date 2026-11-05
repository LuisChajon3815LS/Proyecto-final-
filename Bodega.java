package com.gestionventas;

import java.io.Serializable;

public class Bodega implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final Empleado empleado;
    private final int capacidad;
    private final String direccion;

    public Bodega(String id, Empleado empleado, int capacidad, String direccion) {
        this.id = id;
        this.empleado = empleado;
        this.capacidad = capacidad;
        this.direccion = direccion;
    }

    public String getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public int getCapacidad() { return capacidad; }
    public String getDireccion() { return direccion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bodega that)) return false;
        return id != null && id.equalsIgnoreCase(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return id + " - " + direccion + " (Cap: " + capacidad + ")";
    }
}
