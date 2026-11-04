# Diagramas UML
## 1. Diagrama de clases (modelo del dominio)

Muestra las seis entidades del enunciado y cómo se relacionan.

```mermaid
classDiagram
    direction LR

    class Producto {
        -String codigo
        -String nombre
        -String descripcion
        -String marca
        -BigDecimal precio
    }
    class Cliente {
        -String dpi
        -String nombre
        -String direccion
        -LocalDate fechaNacimiento
        -TipoCliente tipo
    }
    class Empleado {
        -String id
        -String nombre
        -String dpi
        -String direccion
        -String puesto
        -BigDecimal salario
    }
    class Bodega {
        -String id
        -int capacidad
        -String direccion
    }
    class Inventario {
        -Map~Producto,Integer~ stock
        +agregarStock(Producto, int)
        +restarStock(Producto, int)
        +getStock(Producto) int
        +getTotalUnidades() int
    }
    class OrdenCompra {
        -int id
        -LocalDate fecha
        -EstadoOrden estado
        +getTotal() BigDecimal
        +completar()
        +cancelar()
    }
    class DetalleOrden {
        -int cantidad
        -BigDecimal precioUnitario
        +getSubtotal() BigDecimal
    }
    class TipoCliente {
        <<enumeration>>
        REGULAR
        VIP
    }
    class EstadoOrden {
        <<enumeration>>
        PENDIENTE
        COMPLETADA
        CANCELADA
    }

    Bodega "1" --> "1" Empleado : encargado
    Inventario "1" --> "1" Bodega : almacenado en
    Inventario "1" o-- "*" Producto : stock por producto
    OrdenCompra "*" --> "1" Cliente : realizada por
    OrdenCompra "*" --> "1" Bodega : sale de
    OrdenCompra "1" *-- "1..*" DetalleOrden : contiene
    DetalleOrden "*" --> "1" Producto : se refiere a
    Cliente --> TipoCliente
    OrdenCompra --> EstadoOrden
```

**Cómo leerlo**

- `-->` asociación (un objeto conoce a otro).
- `o--` agregación (el inventario *tiene* productos, pero los productos existen sin él).
- `*--` composición (los detalles no existen sin su orden).
- `"1..*"` significa "uno o más": la orden **siempre** tiene al menos un detalle.

## 2. Diagrama de capas (arquitectura)

```mermaid
flowchart TD
    UI["ui: VentanaPrincipal y Paneles (Swing)"]
    SRV["servicio: TiendaServicio (reglas de negocio)"]
    MOD["modelo: Producto, Cliente, OrdenCompra..."]
    PER["persistencia: AlmacenDatos / ArchivoCsv"]
    CSV[("data/*.csv")]

    UI --> SRV
    SRV --> MOD
    SRV --> PER
    PER --> MOD
    PER --> CSV
```

Regla de oro: **cada capa solo habla con la de abajo**. La interfaz nunca toca archivos
y el modelo no sabe que existen ventanas ni archivos.

## 3. Diagrama de secuencia: completar una orden

```mermaid
sequenceDiagram
    actor U as Usuario
    participant P as PanelOrdenes
    participant S as TiendaServicio
    participant I as Inventario
    participant O as OrdenCompra
    participant A as AlmacenDatos

    U->>P: clic en "Completar orden"
    P->>S: completarOrden(numero)
    S->>O: getEstado()
    O-->>S: PENDIENTE
    loop cada detalle (verificar)
        S->>I: getStock(producto)
        I-->>S: disponible
        Note over S: si disponible < requerido -> ValidacionException
    end
    loop cada detalle (restar)
        S->>I: restarStock(producto, cantidad)
    end
    S->>O: completar()
    S->>A: guardarInventarios() y guardarOrdenes()
    S-->>P: listo
    P-->>U: "Orden completada"
```

## 4. Estados de una orden

```mermaid
stateDiagram-v2
    [*] --> PENDIENTE: crearOrden
    PENDIENTE --> COMPLETADA: completarOrden (resta stock)
    PENDIENTE --> CANCELADA: cancelarOrden
    COMPLETADA --> [*]
    CANCELADA --> [*]
```
