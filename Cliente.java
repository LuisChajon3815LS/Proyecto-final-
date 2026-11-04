package com.gestionventas;
import java.io.Serializable; import java.time.LocalDate;
public class Cliente implements Serializable {
 private static final long serialVersionUID=1L;
 private final String dpi,nombre,direccion,tipo; private final LocalDate fechaNacimiento;
 public Cliente(String dpi,String nombre,String direccion,LocalDate fechaNacimiento,String tipo){
  this.dpi=dpi;this.nombre=nombre;this.direccion=direccion;this.fechaNacimiento=fechaNacimiento;this.tipo=tipo;
 }
 public String getDpi(){return dpi;} public String getNombre(){return nombre;} public String getDireccion(){return direccion;}
 public LocalDate getFechaNacimiento(){return fechaNacimiento;} public String getTipo(){return tipo;}
 public String toString(){return nombre+" ("+dpi+")";}
}
