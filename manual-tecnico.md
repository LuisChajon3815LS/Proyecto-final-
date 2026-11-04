# Manual técnico
## 1. Tecnologías

| Elemento | Decisión | Motivo |
|---|---|---|
| Lenguaje | Java 17 | Versión LTS; el enunciado exige Java |
| Interfaz | Swing | Viene en el JDK, sin dependencias |
| Persistencia | CSV con `;` en UTF-8 | Legible y editable con Excel o Bloc de notas |
| Compilación | `javac` + scripts `.bat` | Sin Maven/Gradle para mantenerlo simple |
| Dinero | `BigDecimal` | `double` produce errores de redondeo |
| Fechas | `java.time.LocalDate` | API moderna e inmutable |

## 2. Arquitectura en capas

```
ui  ->  servicio  ->  modelo
              \->  persistencia -> modelo
```

| Paquete | Responsabilidad | Clases |
|---|---|---|
| `modelo` | Datos y reglas propias de cada objeto | `Producto`, `Cliente`, `Empleado`, `Bodega`, `Inventario`, `OrdenCompra`, `DetalleOrden`, `TipoCliente`, `EstadoOrden` |
| `servicio` | Reglas de negocio y coordinación | `TiendaServicio` |
| `persistencia` | Leer/escribir archivos | `AlmacenDatos`, `ArchivoCsv` |
| `excepcion` | Errores propios | `ValidacionException`, `PersistenciaException` |
| `ui` | Ventanas | `VentanaPrincipal`, `PanelBase` y un panel por entidad, `UiUtil` |
| `pruebas` | Verificación automática | `PruebasSistema` |

## 3. Conceptos de POO aplicados

| Concepto | Dónde se ve |
|---|---|
| **Encapsulamiento** | Atributos `private final`; `Inventario` solo se modifica con `agregarStock`/`restarStock`, que validan |
| **Inmutabilidad** | Las entidades no tienen setters |
| **Abstracción** | `PanelBase` define el contrato `refrescar()` |
| **Herencia** | Los seis paneles extienden `PanelBase` |
| **Polimorfismo** | `VentanaPrincipal` llama `refrescar()` sobre cualquier `PanelBase` |
| **Composición** | `OrdenCompra` contiene `DetalleOrden`; `Inventario` contiene un mapa de productos |
| **Enumeraciones** | `TipoCliente`, `EstadoOrden` evitan textos "mágicos" |
| **Separación de responsabilidades** | Una capa por tarea |

## 4. Reglas de negocio

| # | Regla | Dónde se aplica |
|---|---|---|
| R1 | Código/ID únicos para producto, empleado y bodega; DPI único por cliente | `TiendaServicio.registrar*` |
| R2 | DPI = exactamente 13 dígitos | `TiendaServicio.validarDpi` |
| R3 | Códigos e IDs: solo letras, números, `-` y `_` | `TiendaServicio.validarCodigo` |
| R4 | Precio, salario y capacidad > 0 | `TiendaServicio.registrar*` |
| R5 | Fecha de nacimiento anterior a hoy | `registrarCliente` |
| R6 | Una bodega requiere un empleado existente como encargado | `registrarBodega` |
| R7 | Toda bodega nace con un inventario vacío | `registrarBodega` |
| R8 | El stock total de una bodega no supera su capacidad | `Inventario.agregarStock` |
| R9 | Una orden tiene al menos un producto | `OrdenCompra` (constructor) y `crearOrden` |
| R10 | Una orden nace `PENDIENTE` y no descuenta stock | `crearOrden` |
| R11 | Al completar, se descuenta el stock de la bodega de la orden | `completarOrden` |
| R12 | Completar es "todo o nada": si una línea no tiene stock, no se descuenta ninguna | `completarOrden` |
| R13 | Solo una orden `PENDIENTE` puede completarse o cancelarse | `OrdenCompra.exigirPendiente` |
| R14 | El precio de cada línea se congela al crear la orden | `crearOrden` / `DetalleOrden` |

### Decisiones que van más allá del enunciado

1. **Estado `PENDIENTE`**: el enunciado pide `Completada` y `Cancelada`, pero también habla de "pasar a estado completada", lo que implica un estado previo.
2. **Bodega en la orden**: para restar stock hay que saber de qué bodega sale la mercadería.
3. **Cancelar una orden completada no está permitido**: implicaría devolver stock; queda como mejora futura.

## 5. Archivos de datos (carpeta `data/`)

Formato común: UTF-8, separador `;`, primera línea = encabezado. Dentro de un valor, `;` se
escribe `\;`, `\` se escribe `\\` y el salto de línea `\n`.

| Archivo | Columnas |
|---|---|
| `productos.csv` | `codigo;nombre;descripcion;marca;precio` |
| `clientes.csv` | `dpi;nombre;direccion;fechaNacimiento;tipo` (tipo = `REGULAR` o `VIP`) |
| `empleados.csv` | `id;nombre;dpi;direccion;puesto;salario` |
| `bodegas.csv` | `id;idEmpleado;capacidad;direccion` |
| `inventario.csv` | `idBodega;codigoProducto;cantidad` |
| `ordenes.csv` | `id;dpiCliente;idBodega;fecha;estado;detalles` |

En `ordenes.csv`, `detalles` es una lista `codigo*cantidad*precio` separada por `|`:

```
1;1234567890101;B001;2026-11-02;COMPLETADA;P001*3*100.00|P002*1*50.00
```

**Relaciones**: se guardan solo con la llave (DPI, código, ID). Al iniciar se reconstruyen
los objetos buscando en mapas. Por eso `TiendaServicio` carga en este orden:
productos → clientes → empleados → bodegas → inventarios → órdenes.

**Cuándo se guarda**: después de cada operación que cambia datos. Se escribe primero un
archivo `.tmp` y luego se renombra, para no dejar un archivo a medias si algo falla.

## 6. Flujo de una operación típica

Ejemplo: registrar un producto.

1. El usuario pulsa **Registrar producto** en `PanelProductos`.
2. El botón llama a `ejecutar(this::registrar)` (en `PanelBase`), que envuelve todo en un `try/catch`.
3. `registrar()` lee los campos y llama a `servicio.registrarProducto(...)`.
4. `TiendaServicio` valida, crea el `Producto`, lo guarda en su mapa y llama a `almacen.guardarProductos(...)`.
5. `AlmacenDatos` convierte cada producto a una fila y `ArchivoCsv.escribir` crea `data/productos.csv`.
6. Si algo falla en 3-5 se lanza una excepción; `ejecutar` la captura y muestra el mensaje.

## 7. Cómo extender el sistema

**Agregar un campo a una entidad** (ejemplo: `email` en `Cliente`):

1. Agregar el atributo, constructor y getter en `Cliente`.
2. Pedirlo y validarlo en `TiendaServicio.registrarCliente`.
3. Agregarlo en `AlmacenDatos.cargarClientes` / `guardarClientes` (y en el encabezado).
4. Agregar el campo en `PanelClientes`.
5. Actualizar este manual y los archivos CSV existentes (agregar la columna).

**Agregar una entidad nueva**: modelo → métodos en `TiendaServicio` → métodos de carga/guardado en `AlmacenDatos` → panel nuevo → registrarlo en `VentanaPrincipal`.

## 8. Pruebas

`probar.bat` ejecuta `PruebasSistema` (10 pruebas sobre la lógica de negocio y la persistencia).
Usan carpetas temporales, así que **no modifican** `data/`. Detalle en la [guía de estudio](guia-de-estudio.md).

## 9. Limitaciones conocidas

- Una sola persona usa el programa a la vez (los archivos no se bloquean entre procesos).
- Los registros no se pueden editar ni eliminar (el enunciado solo pide registrar).
- Todo se carga en memoria al iniciar; es adecuado para miles de registros, no para millones.
- Si se edita un CSV a mano con datos inválidos, el programa avisa archivo y línea del error.
