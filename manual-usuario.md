# Manual de usuario

## 1. ¿Para qué sirve?

Para llevar el control de una tienda: registrar lo que se vende, a quién, desde qué bodega
y cuánto queda en existencias.

## 2. Iniciar el programa

1. Haz doble clic en `ejecutar.bat`.
2. Se abre la ventana con seis pestañas. Al cerrarla, todo queda guardado.

## 3. Orden recomendado de uso

La primera vez, registra en este orden (cada paso depende del anterior):

```
1 Productos -> 2 Clientes -> 3 Empleados -> 4 Bodegas -> 5 Inventario -> 6 Órdenes
```

## 4. Pestañas

En todas, el formulario está arriba y la tabla con lo ya registrado, abajo.
Si algo está mal, aparece un aviso que explica qué corregir. Haz clic en el encabezado de
una columna para ordenarla.

### Productos
| Campo | Qué escribir |
|---|---|
| Código | Único, sin espacios (ej. `P001`) |
| Nombre, Marca | Texto obligatorio |
| Descripción | Opcional |
| Precio (Q) | Número mayor que 0 (ej. `125.50`) |

Pulsa **Registrar producto**.

### Clientes
| Campo | Qué escribir |
|---|---|
| DPI | Exactamente 13 dígitos |
| Nombre, Dirección | Texto obligatorio |
| Fecha de nacimiento | `AAAA-MM-DD` (ej. `1990-05-20`) |
| Tipo | Regular o VIP |

### Empleados
ID único sin espacios (ej. `E001`), nombre, DPI de 13 dígitos, dirección, puesto y salario mayor que 0.

### Bodegas
ID único (ej. `B001`), **empleado encargado** (elige de la lista; primero registra empleados),
capacidad en unidades totales y dirección. Cada bodega nace con su inventario vacío.

### Inventario
Aquí **ingresas mercadería**: elige bodega, producto y cantidad, y pulsa **Registrar en
inventario**. Si el producto ya estaba, las unidades se suman. La columna "Ocupación" indica
`unidades usadas / capacidad`. No puedes superar la capacidad de la bodega.

### Órdenes de compra
**Para crear una orden**

1. Elige **cliente** y **bodega de salida**.
2. Elige un **producto**, indica la **cantidad** y pulsa **Agregar al carrito**. Repite por cada producto (mínimo uno).
3. Revisa el total. Para quitar una línea, selecciónala y pulsa **Quitar seleccionado**.
4. Pulsa **Crear orden**. Queda en estado **Pendiente**; el stock *todavía no* cambia.

**Para cerrar la venta**: en la tabla "Órdenes registradas" selecciona la orden y pulsa:

- **Completar orden (resta stock)**: la venta se concreta y el inventario baja. Si falta stock de algún producto, se avisa y **no se descuenta nada**.
- **Cancelar orden**: la orden queda cancelada y el inventario no cambia.

Una orden completada o cancelada ya no puede cambiar de estado.

## 5. Mensajes frecuentes

| Mensaje | Qué hacer |
|---|---|
| "Ya existe un producto con el código..." | Usa otro código |
| "El DPI debe tener exactamente 13 dígitos numéricos" | Revisa que no tenga letras, guiones ni espacios |
| "Fecha inválida. Use el formato AAAA-MM-DD" | Escríbela como `1990-05-20` |
| "Escriba un número válido" | Usa punto decimal, sin símbolo `Q` ni comas |
| "La bodega B001 solo tiene espacio para N unidades más" | Ingresa menos unidades o registra otra bodega |
| "Stock insuficiente de..." | Ingresa más inventario en esa bodega, o crea la orden con menos cantidad |
| "Seleccione una orden de la tabla" | Haz clic en una fila antes de pulsar el botón |

## 6. Dónde se guardan mis datos

En la carpeta `data/` junto al programa, en archivos `.csv` que también puedes abrir con
Excel **solo para consultar**. No los edites a mano con el programa abierto. Para hacer una
copia de seguridad, copia la carpeta `data/`.

Para empezar de cero, cierra el programa y borra la carpeta `data/`.
