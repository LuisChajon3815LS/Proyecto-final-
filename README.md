# Sistema de Tienda: Ventas e Inventario

Aplicación de escritorio en **Java 17** (Swing) para registrar productos, clientes,
empleados, bodegas, inventario y órdenes de compra. Aplica Programación Orientada a
Objetos y guarda todo en archivos CSV.

## Requisitos

- JDK 17 o superior (`javac -version` debe funcionar).
- Windows (los scripts son `.bat`; el código Java es multiplataforma).

## Cómo ejecutarlo

| Acción | Comando |
|---|---|
| Compilar | `compilar.bat` |
| Abrir la aplicación | `ejecutar.bat` |
| Correr pruebas automáticas | `probar.bat` |

Los datos se guardan en la carpeta `data/` (se crea sola al registrar el primer dato).

## Funciones principales

- Registrar **producto, cliente, empleado, bodega, inventario y orden de compra**.
- Una orden exige **al menos un producto**.
- Al **completar** una orden se **resta el stock** del inventario de la bodega (todo o nada).
- Los datos **persisten en archivos** y se recuperan al reabrir el programa.

## Estructura del proyecto

```
src/tienda/
 ├─ Main.java              Punto de entrada
 ├─ modelo/                Clases del dominio (Producto, Cliente, OrdenCompra...)
 ├─ servicio/              Reglas de negocio (TiendaServicio)
 ├─ persistencia/          Lectura/escritura de CSV (AlmacenDatos, ArchivoCsv)
 ├─ excepcion/             Excepciones propias
 ├─ ui/                    Interfaz gráfica Swing (un panel por pestaña)
 └─ pruebas/               Pruebas automáticas sin librerías externas
docs/                      Documentación (ver abajo)
data/                      Archivos CSV generados (no se sube a Git)
```

## Documentación

| Documento | Para qué sirve |
|---|---|
| [UML](https://github.com/LuisChajon3815LS/Proyecto-final-/blob/main/UML.md) | Diagrama de clases, de capas y de secuencia (requisito) |
| [Manual técnico]([tecnico.md](https://github.com/LuisChajon3815LS/Proyecto-final-/blob/main/manual-tecnico.md)) | Arquitectura, formato de archivos, reglas (requisito) |
| [Manual de usuario](https://github.com/LuisChajon3815LS/Proyecto-final-/blob/main/UML.md) | Cómo usar el programa paso a paso (requisito) |
