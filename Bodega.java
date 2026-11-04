package com.gestionventas;
import java.io.Serializable;
public class Bodega implements Serializable {
 private static final long serialVersionUID=1L;
 private final String id,direccion; private final Empleado empleado; private final int capacidad;
 public Bodega(String id,Empleado empleado,int capacidad,String direccion){this.id=id;this.empleado=empleado;this.capacidad=capacidad;this.direccion=direccion;}
 public String getId(){return id;} public Empleado getEmpleado(){return empleado;} public int getCapacidad(){return capacidad;} public String getDireccion(){return direccion;}
 public String toString(){return id+" - "+direccion;}
}
