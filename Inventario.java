package com.gestionventas;
import java.io.Serializable;
public class Inventario implements Serializable {
 private static final long serialVersionUID=1L;
 private final Producto producto; private int cantidad; private final Bodega bodega;
 public Inventario(Producto producto,int cantidad,Bodega bodega){this.producto=producto;this.cantidad=cantidad;this.bodega=bodega;}
 public Producto getProducto(){return producto;} public int getCantidad(){return cantidad;} public Bodega getBodega(){return bodega;}
 public void setCantidad(int cantidad){this.cantidad=cantidad;}
}
