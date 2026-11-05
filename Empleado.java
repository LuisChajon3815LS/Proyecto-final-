package com.gestionventas;

import java.io.Serializable;

public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String nombre;
    private final String dpi;
    private final String direccion;
    private final String puesto;
    private final double salario;

    public Empleado(String id, String nombre, String dpi, String direccion, String puesto, double salario) {
        this.id = id;
        this.nombre = nombre;
        this.dpi = dpi;
        this.direccion = direccion;
        this.puesto = puesto;
        this.salario = salario;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDpi() { return dpi; }
    public String getDireccion() { return direccion; }
    public String getPuesto() { return puesto; }
    public double getSalario() { return salario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empleado that)) return false;
        return id != null && id.equalsIgnoreCase(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + puesto + ")";
    }
}
}
