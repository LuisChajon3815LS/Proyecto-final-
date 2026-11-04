package com.gestionventas;
import java.io.Serializable;
public class Empleado implements Serializable {
 private static final long serialVersionUID=1L;
 private final String id,nombre,dpi,direccion,puesto; private final double salario;
 public Empleado(String id,String nombre,String dpi,String direccion,String puesto,double salario){
  this.id=id;this.nombre=nombre;this.dpi=dpi;this.direccion=direccion;this.puesto=puesto;this.salario=salario;
 }
 public String getId(){return id;} public String getNombre(){return nombre;} public String getDpi(){return dpi;}
 public String getDireccion(){return direccion;} public String getPuesto(){return puesto;} public double getSalario(){return salario;}
 public String toString(){return id+" - "+nombre;}
}
