package com.gestionventas;
import java.io.Serializable;
public class DetalleOrden implements Serializable {
 private static final long serialVersionUID=1L;
 private final Producto producto; private final int cantidad;
 public DetalleOrden(Producto producto,int cantidad){this.producto=producto;this.cantidad=cantidad;}
 public Producto getProducto(){return producto;} public int getCantidad(){return cantidad;}
 public double getSubtotal(){return producto.getPrecio()*cantidad;}
}
