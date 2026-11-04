package com.gestionventas;
import java.io.Serializable; import java.time.LocalDate; import java.util.*;
public class OrdenCompra implements Serializable {
 private static final long serialVersionUID=1L;
 private final int numero; private final Cliente cliente; private final List<DetalleOrden> detalles;
 private final LocalDate fecha; private final double total; private String estado;
 public OrdenCompra(int numero,Cliente cliente,List<DetalleOrden> detalles){
  if(detalles==null||detalles.isEmpty())throw new IllegalArgumentException("La orden debe incluir al menos un producto.");
  this.numero=numero;this.cliente=cliente;this.detalles=new ArrayList<>(detalles);this.fecha=LocalDate.now();
  this.total=this.detalles.stream().mapToDouble(DetalleOrden::getSubtotal).sum();this.estado="Pendiente";
 }
 public int getNumero(){return numero;} public Cliente getCliente(){return cliente;} public List<DetalleOrden> getDetalles(){return Collections.unmodifiableList(detalles);}
 public LocalDate getFecha(){return fecha;} public double getTotal(){return total;} public String getEstado(){return estado;}
 public void setEstado(String estado){this.estado=estado;}
}
