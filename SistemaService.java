package com.gestionventas;
import java.util.*;
public class SistemaService {
 public final List<Producto> productos=new Repositorio<Producto>("productos").cargar();
 public final List<Cliente> clientes=new Repositorio<Cliente>("clientes").cargar();
 public final List<Empleado> empleados=new Repositorio<Empleado>("empleados").cargar();
 public final List<Bodega> bodegas=new Repositorio<Bodega>("bodegas").cargar();
 public final List<Inventario> inventarios=new Repositorio<Inventario>("inventario").cargar();
 public final List<OrdenCompra> ordenes=new Repositorio<OrdenCompra>("ordenes").cargar();
 private void validarTexto(String s,String campo){if(s==null||s.isBlank())throw new IllegalArgumentException("El campo "+campo+" es obligatorio.");}
 public void agregarProducto(Producto p){validarTexto(p.getCodigo(),"código");validarTexto(p.getNombre(),"nombre");if(p.getPrecio()<0)throw new IllegalArgumentException("Precio inválido.");if(productos.stream().anyMatch(x->x.getCodigo().equalsIgnoreCase(p.getCodigo())))throw new IllegalArgumentException("El código ya existe.");productos.add(p);guardarProductos();}
 public void agregarCliente(Cliente c){validarTexto(c.getDpi(),"DPI");validarTexto(c.getNombre(),"nombre");if(clientes.stream().anyMatch(x->x.getDpi().equals(c.getDpi())))throw new IllegalArgumentException("El DPI ya existe.");clientes.add(c);guardarClientes();}
 public void agregarEmpleado(Empleado e){validarTexto(e.getId(),"ID");if(empleados.stream().anyMatch(x->x.getId().equalsIgnoreCase(e.getId())))throw new IllegalArgumentException("El ID ya existe.");empleados.add(e);guardarEmpleados();}
 public void agregarBodega(Bodega b){validarTexto(b.getId(),"ID de bodega");if(b.getCapacidad()<=0)throw new IllegalArgumentException("La capacidad debe ser mayor que cero.");if(bodegas.stream().anyMatch(x->x.getId().equalsIgnoreCase(b.getId())))throw new IllegalArgumentException("El ID de bodega ya existe.");bodegas.add(b);guardarBodegas();}
 public void agregarInventario(Producto p,int cantidad,Bodega b){if(cantidad<=0)throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");Inventario i=inventarios.stream().filter(x->x.getProducto().getCodigo().equals(p.getCodigo())&&x.getBodega().getId().equals(b.getId())).findFirst().orElse(null);if(i==null)inventarios.add(new Inventario(p,cantidad,b));else i.setCantidad(i.getCantidad()+cantidad);guardarInventario();}
 public OrdenCompra crearOrden(Cliente c,List<DetalleOrden> ds){if(c==null)throw new IllegalArgumentException("Seleccione un cliente.");if(ds==null||ds.isEmpty())throw new IllegalArgumentException("La orden debe incluir al menos un producto.");OrdenCompra o=new OrdenCompra(ordenes.size()+1,c,ds);ordenes.add(o);guardarOrdenes();return o;}
 public void cambiarEstado(OrdenCompra o,String nuevo){
  if(!"Pendiente".equals(o.getEstado()))throw new IllegalArgumentException("La orden ya fue procesada.");
  if("Completada".equals(nuevo)){
   Map<String,Integer> requeridos=new HashMap<>();for(DetalleOrden d:o.getDetalles())requeridos.merge(d.getProducto().getCodigo(),d.getCantidad(),Integer::sum);
   for(DetalleOrden d:o.getDetalles())if(stock(d.getProducto())<requeridos.get(d.getProducto().getCodigo()))throw new IllegalArgumentException("Stock insuficiente para "+d.getProducto().getNombre()+". Disponible: "+stock(d.getProducto()));
   for(var entry:requeridos.entrySet())descontar(entry.getKey(),entry.getValue());guardarInventario();
  }else if(!"Cancelada".equals(nuevo))throw new IllegalArgumentException("Estado no válido.");
  o.setEstado(nuevo);guardarOrdenes();
 }
 private void descontar(String codigo,int cantidad){int restante=cantidad;for(Inventario i:inventarios){if(i.getProducto().getCodigo().equals(codigo)&&restante>0){int toma=Math.min(i.getCantidad(),restante);i.setCantidad(i.getCantidad()-toma);restante-=toma;}}}
 public int stock(Producto p){return inventarios.stream().filter(i->i.getProducto().getCodigo().equals(p.getCodigo())).mapToInt(Inventario::getCantidad).sum();}
 public void guardarProductos(){new Repositorio<Producto>("productos").guardar(productos);}
 public void guardarClientes(){new Repositorio<Cliente>("clientes").guardar(clientes);}
 public void guardarEmpleados(){new Repositorio<Empleado>("empleados").guardar(empleados);}
 public void guardarBodegas(){new Repositorio<Bodega>("bodegas").guardar(bodegas);}
 public void guardarInventario(){new Repositorio<Inventario>("inventario").guardar(inventarios);}
 public void guardarOrdenes(){new Repositorio<OrdenCompra>("ordenes").guardar(ordenes);}
}
