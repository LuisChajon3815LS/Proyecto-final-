package com.gestionventas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private final SistemaService s = new SistemaService();
    private final JTabbedPane tabs = new JTabbedPane();

    // Modelos de tablas (centralizados para refresco global)
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloClientes;
    private DefaultTableModel modeloEmpleados;
    private DefaultTableModel modeloBodegas;
    private DefaultTableModel modeloInventario;
    private DefaultTableModel modeloOrdenes;

    // Tablas
    private JTable tablaProductos;
    private JTable tablaClientes;
    private JTable tablaEmpleados;
    private JTable tablaBodegas;
    private JTable tablaInventario;
    private JTable tablaOrdenes;

    // Combos dinámicos sincronizados
    private JComboBox<Empleado> comboBodegaEmpleado;
    private JComboBox<Producto> comboInventarioProducto;
    private JComboBox<Bodega> comboInventarioBodega;
    private JComboBox<Producto> comboProductoOrden;
    private JComboBox<Cliente> comboClienteOrden;

    // Carrito de compras
    private final List<DetalleOrden> carrito = new ArrayList<>();
    private DefaultTableModel modeloCarrito;
    private JTable tablaCarrito;
    private JLabel lblTotalCarrito;

    public Main() {
        super("Sistema de Gestión de Ventas e Inventario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        construirProductos();
        construirClientes();
        construirEmpleados();
        construirBodegas();
        construirInventario();
        construirOrdenes();

        // Al cambiar de pestaña, asegurar sincronización total de tablas y combos
        tabs.addChangeListener(e -> refrescarTodo());

        add(tabs);
        refrescarTodo();
    }

    private void refrescarTodo() {
        actualizarCombos();
        actualizarProductos();
        actualizarClientes();
        actualizarEmpleados();
        actualizarBodegas();
        actualizarInventario();
        actualizarOrdenes();
    }

    private JPanel panel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return p;
    }

    private JTextField campo(int n) {
        JTextField f = new JTextField(n);
        f.setPreferredSize(new Dimension(f.getPreferredSize().width, 28));
        return f;
    }

    private DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int col) {
                return false;
            }
        };
    }

    private JTable crearTabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD));
        t.setShowGrid(true);
        t.setGridColor(new Color(230, 230, 230));
        return t;
    }

    private JPanel formulario(String titulo, JComponent[] campos, String[] etiquetas, Runnable accion) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < campos.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            JLabel lbl = new JLabel(etiquetas[i]);
            lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12.5f));
            p.add(lbl, c);

            c.gridx = 1;
            c.weightx = 1;
            p.add(campos[i], c);
        }

        JButton b = new JButton("Registrar");
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        c.gridx = 1;
        c.gridy = campos.length;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(8, 8, 8, 8);
        p.add(b, c);

        b.addActionListener(e -> ejecutar(accion));
        return p;
    }

    private void ejecutar(Runnable r) {
        try {
            r.run();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido en los campos numéricos.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Utilice el formato AAAA-MM-DD (ej. 1995-05-12).", "Fecha inválida", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso del Sistema", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void exito(String m) {
        JOptionPane.showMessageDialog(this, m, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    private double decimal(String x) {
        double v = Double.parseDouble(x.trim());
        if (v < 0) throw new IllegalArgumentException("El valor monetario no puede ser negativo.");
        return v;
    }

    private int entero(String x) {
        int v = Integer.parseInt(x.trim());
        if (v <= 0) throw new IllegalArgumentException("La cantidad debe ser un número entero mayor que cero.");
        return v;
    }

    // ─────────────────────────────────────────────────────────────
    // 1. PRODUCTOS
    // ─────────────────────────────────────────────────────────────
    private void construirProductos() {
        JPanel p = panel();
        modeloProductos = modelo("Código", "Nombre", "Descripción", "Marca", "Precio (Q)", "Stock Total");
        tablaProductos = crearTabla(modeloProductos);
        p.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        JTextField cod = campo(10), nom = campo(15), des = campo(15), mar = campo(10), pre = campo(8);

        p.add(formulario("Registrar nuevo producto",
                new JComponent[]{cod, nom, des, mar, pre},
                new String[]{"Código:", "Nombre:", "Descripción:", "Marca:", "Precio (Q):"},
                () -> {
                    s.agregarProducto(new Producto(
                            cod.getText().trim(),
                            nom.getText().trim(),
                            des.getText().trim(),
                            mar.getText().trim(),
                            decimal(pre.getText())
                    ));
                    actualizarProductos();
                    actualizarCombos();
                    cod.setText(""); nom.setText(""); des.setText(""); mar.setText(""); pre.setText("");
                    exito("Producto registrado con éxito.");
                }), BorderLayout.NORTH);

        tabs.addTab("Productos", p);
    }

    private void actualizarProductos() {
        if (modeloProductos == null) return;
        modeloProductos.setRowCount(0);
        for (Producto x : s.productos) {
            modeloProductos.addRow(new Object[]{
                    x.getCodigo(),
                    x.getNombre(),
                    x.getDescripcion(),
                    x.getMarca(),
                    String.format("%.2f", x.getPrecio()),
                    s.stock(x)
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 2. CLIENTES
    // ─────────────────────────────────────────────────────────────
    private void construirClientes() {
        JPanel p = panel();
        modeloClientes = modelo("DPI", "Nombre", "Dirección", "Nacimiento", "Tipo");
        tablaClientes = crearTabla(modeloClientes);
        p.add(new JScrollPane(tablaClientes), BorderLayout.CENTER);

        JTextField dpi = campo(13), nom = campo(15), dir = campo(15), fec = campo(10);
        fec.setToolTipText("Ejemplo: 1995-05-12");
        JComboBox<String> tipo = new JComboBox<>(new String[]{"Regular", "VIP"});

        p.add(formulario("Registrar nuevo cliente",
                new JComponent[]{dpi, nom, dir, fec, tipo},
                new String[]{"DPI:", "Nombre:", "Dirección:", "Nacimiento (AAAA-MM-DD):", "Tipo:"},
                () -> {
                    Cliente c = new Cliente(
                            dpi.getText().trim(),
                            nom.getText().trim(),
                            dir.getText().trim(),
                            LocalDate.parse(fec.getText().trim()),
                            (String) tipo.getSelectedItem()
                    );
                    s.agregarCliente(c);
                    actualizarClientes();
                    actualizarCombos();
                    dpi.setText(""); nom.setText(""); dir.setText(""); fec.setText("");
                    exito("Cliente registrado con éxito.");
                }), BorderLayout.NORTH);

        tabs.addTab("Clientes", p);
    }

    private void actualizarClientes() {
        if (modeloClientes == null) return;
        modeloClientes.setRowCount(0);
        for (Cliente c : s.clientes) {
            modeloClientes.addRow(new Object[]{
                    c.getDpi(),
                    c.getNombre(),
                    c.getDireccion(),
                    c.getFechaNacimiento(),
                    c.getTipo()
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 3. EMPLEADOS
    // ─────────────────────────────────────────────────────────────
    private void construirEmpleados() {
        JPanel p = panel();
        modeloEmpleados = modelo("ID Empleado", "Nombre", "DPI", "Dirección", "Puesto", "Salario (Q)");
        tablaEmpleados = crearTabla(modeloEmpleados);
        p.add(new JScrollPane(tablaEmpleados), BorderLayout.CENTER);

        JTextField id = campo(10), nom = campo(15), dpi = campo(13), dir = campo(15), puesto = campo(12), sal = campo(8);

        p.add(formulario("Registrar nuevo empleado",
                new JComponent[]{id, nom, dpi, dir, puesto, sal},
                new String[]{"ID Empleado:", "Nombre:", "DPI:", "Dirección:", "Puesto:", "Salario (Q):"},
                () -> {
                    Empleado e = new Empleado(
                            id.getText().trim(),
                            nom.getText().trim(),
                            dpi.getText().trim(),
                            dir.getText().trim(),
                            puesto.getText().trim(),
                            decimal(sal.getText())
                    );
                    s.agregarEmpleado(e);
                    actualizarEmpleados();
                    actualizarCombos();
                    id.setText(""); nom.setText(""); dpi.setText(""); dir.setText(""); puesto.setText(""); sal.setText("");
                    exito("Empleado registrado con éxito.");
                }), BorderLayout.NORTH);

        tabs.addTab("Empleados", p);
    }

    private void actualizarEmpleados() {
        if (modeloEmpleados == null) return;
        modeloEmpleados.setRowCount(0);
        for (Empleado e : s.empleados) {
            modeloEmpleados.addRow(new Object[]{
                    e.getId(),
                    e.getNombre(),
                    e.getDpi(),
                    e.getDireccion(),
                    e.getPuesto(),
                    String.format("%.2f", e.getSalario())
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 4. BODEGAS
    // ─────────────────────────────────────────────────────────────
    private void construirBodegas() {
        JPanel p = panel();
        modeloBodegas = modelo("ID Bodega", "Empleado Responsable", "Capacidad", "Dirección");
        tablaBodegas = crearTabla(modeloBodegas);
        p.add(new JScrollPane(tablaBodegas), BorderLayout.CENTER);

        JTextField id = campo(10), cap = campo(8), dir = campo(15);
        comboBodegaEmpleado = new JComboBox<>(s.empleados.toArray(new Empleado[0]));

        p.add(formulario("Registrar nueva bodega",
                new JComponent[]{id, comboBodegaEmpleado, cap, dir},
                new String[]{"ID Bodega:", "Responsable:", "Capacidad (unidades):", "Dirección:"},
                () -> {
                    if (comboBodegaEmpleado.getSelectedItem() == null) {
                        throw new IllegalArgumentException("Debe registrar al menos un empleado antes de crear bodegas.");
                    }
                    Bodega b = new Bodega(
                            id.getText().trim(),
                            (Empleado) comboBodegaEmpleado.getSelectedItem(),
                            entero(cap.getText()),
                            dir.getText().trim()
                    );
                    s.agregarBodega(b);
                    actualizarBodegas();
                    actualizarCombos();
                    id.setText(""); cap.setText(""); dir.setText("");
                    exito("Bodega registrada con éxito.");
                }), BorderLayout.NORTH);

        tabs.addTab("Bodegas", p);
    }

    private void actualizarBodegas() {
        if (modeloBodegas == null) return;
        modeloBodegas.setRowCount(0);
        for (Bodega b : s.bodegas) {
            modeloBodegas.addRow(new Object[]{
                    b.getId(),
                    b.getEmpleado().getNombre() + " (" + b.getEmpleado().getId() + ")",
                    b.getCapacidad(),
                    b.getDireccion()
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 5. INVENTARIO
    // ─────────────────────────────────────────────────────────────
    private void construirInventario() {
        JPanel p = panel();
        modeloInventario = modelo("Producto", "Cantidad Disponible", "Bodega Destino");
        tablaInventario = crearTabla(modeloInventario);
        p.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);

        comboInventarioProducto = new JComboBox<>(s.productos.toArray(new Producto[0]));
        JTextField cant = campo(8);
        comboInventarioBodega = new JComboBox<>(s.bodegas.toArray(new Bodega[0]));

        p.add(formulario("Ingreso de existencias a bodega",
                new JComponent[]{comboInventarioProducto, cant, comboInventarioBodega},
                new String[]{"Producto:", "Cantidad a ingresar:", "Bodega destino:"},
                () -> {
                    if (comboInventarioProducto.getSelectedItem() == null || comboInventarioBodega.getSelectedItem() == null) {
                        throw new IllegalArgumentException("Debe tener al menos un producto y una bodega registrados.");
                    }
                    s.agregarInventario(
                            (Producto) comboInventarioProducto.getSelectedItem(),
                            entero(cant.getText()),
                            (Bodega) comboInventarioBodega.getSelectedItem()
                    );
                    actualizarInventario();
                    actualizarProductos();
                    cant.setText("");
                    exito("Existencias agregadas al inventario con éxito.");
                }), BorderLayout.NORTH);

        tabs.addTab("Inventario", p);
    }

    private void actualizarInventario() {
        if (modeloInventario == null) return;
        modeloInventario.setRowCount(0);
        for (Inventario i : s.inventarios) {
            modeloInventario.addRow(new Object[]{
                    i.getProducto().getNombre() + " (" + i.getProducto().getCodigo() + ")",
                    i.getCantidad(),
                    i.getBodega().getId() + " - " + i.getBodega().getDireccion()
            });
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 6. ÓRDENES
    // ─────────────────────────────────────────────────────────────
    private void construirOrdenes() {
        JPanel p = panel();

        // 1. Historial de órdenes en el centro
        modeloOrdenes = modelo("No. Orden", "Cliente", "Fecha", "Total (Q)", "Estado");
        tablaOrdenes = crearTabla(modeloOrdenes);
        tablaOrdenes.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaOrdenes.getColumnModel().getColumn(0).setMaxWidth(90);
        tablaOrdenes.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaOrdenes.getColumnModel().getColumn(3).setPreferredWidth(110);
        tablaOrdenes.getColumnModel().getColumn(4).setPreferredWidth(110);

        JPanel panelHistorial = new JPanel(new BorderLayout(6, 6));
        panelHistorial.setBorder(BorderFactory.createTitledBorder("Historial de órdenes registradas"));
        panelHistorial.add(new JScrollPane(tablaOrdenes), BorderLayout.CENTER);
        p.add(panelHistorial, BorderLayout.CENTER);

        // 2. Sección superior: Creación de orden
        JPanel top = new JPanel(new BorderLayout(14, 14));
        top.setBorder(BorderFactory.createTitledBorder("Nueva orden de compra"));

        // Formulario a la izquierda
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        comboClienteOrden = new JComboBox<>(s.clientes.toArray(new Cliente[0]));
        comboProductoOrden = new JComboBox<>(s.productos.toArray(new Producto[0]));
        JTextField cantidad = campo(6);
        JButton agregarAlCarrito = new JButton("Agregar al carrito");
        JButton limpiarCarrito = new JButton("Limpiar carrito");

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        formPanel.add(new JLabel("Cliente:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1;
        formPanel.add(comboClienteOrden, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        formPanel.add(new JLabel("Producto:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1;
        formPanel.add(comboProductoOrden, c);

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        formPanel.add(new JLabel("Cantidad:"), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1;
        formPanel.add(cantidad, c);

        JPanel panelBtnForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        panelBtnForm.add(agregarAlCarrito);
        panelBtnForm.add(limpiarCarrito);
        c.gridx = 1; c.gridy = 3; c.weightx = 1;
        formPanel.add(panelBtnForm, c);

        formPanel.setPreferredSize(new Dimension(470, 165));
        top.add(formPanel, BorderLayout.WEST);

        // Subpanel Carrito (a la derecha)
        JPanel carritoPanel = new JPanel(new BorderLayout(6, 6));
        carritoPanel.setBorder(BorderFactory.createTitledBorder("Productos en la orden actual (Carrito)"));

        modeloCarrito = modelo("Producto", "Cantidad", "Subtotal (Q)");
        tablaCarrito = crearTabla(modeloCarrito);
        tablaCarrito.getColumnModel().getColumn(0).setPreferredWidth(210);
        tablaCarrito.getColumnModel().getColumn(1).setPreferredWidth(70);
        tablaCarrito.getColumnModel().getColumn(2).setPreferredWidth(90);

        JScrollPane scrollCarrito = new JScrollPane(tablaCarrito);
        scrollCarrito.setPreferredSize(new Dimension(420, 100));
        carritoPanel.add(scrollCarrito, BorderLayout.CENTER);

        // Barra inferior del carrito: Total + Botón Quitar + Botón Crear
        JPanel barraCarrito = new JPanel(new BorderLayout(10, 5));
        lblTotalCarrito = new JLabel("Total: Q0.00");
        lblTotalCarrito.setFont(lblTotalCarrito.getFont().deriveFont(Font.BOLD, 13f));
        lblTotalCarrito.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JPanel botonesCarrito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton btnQuitar = new JButton("Quitar ítem");
        JButton btnCrearOrden = new JButton("Confirmar y Crear Orden");
        btnCrearOrden.setFont(btnCrearOrden.getFont().deriveFont(Font.BOLD));

        botonesCarrito.add(btnQuitar);
        botonesCarrito.add(btnCrearOrden);

        barraCarrito.add(lblTotalCarrito, BorderLayout.WEST);
        barraCarrito.add(botonesCarrito, BorderLayout.EAST);
        carritoPanel.add(barraCarrito, BorderLayout.SOUTH);

        top.add(carritoPanel, BorderLayout.CENTER);
        p.add(top, BorderLayout.NORTH);

        // 3. Botones de acción inferiores
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnCompletar = new JButton("Completar orden seleccionada");
        btnCompletar.setFont(btnCompletar.getFont().deriveFont(Font.BOLD));
        JButton btnCancelar = new JButton("Cancelar orden seleccionada");
        south.add(btnCompletar);
        south.add(btnCancelar);
        p.add(south, BorderLayout.SOUTH);

        // Listeners
        agregarAlCarrito.addActionListener(e -> ejecutar(() -> {
            if (comboProductoOrden.getSelectedItem() == null) {
                throw new IllegalArgumentException("Seleccione un producto primero.");
            }
            int n = entero(cantidad.getText());
            Producto pr = (Producto) comboProductoOrden.getSelectedItem();

            // Si el producto ya está en el carrito, sumar cantidad en lugar de duplicar fila
            DetalleOrden existente = null;
            int idxExistente = -1;
            for (int i = 0; i < carrito.size(); i++) {
                if (carrito.get(i).getProducto().getCodigo().equalsIgnoreCase(pr.getCodigo())) {
                    existente = carrito.get(i);
                    idxExistente = i;
                    break;
                }
            }

            if (existente != null) {
                int nuevaCantidad = existente.getCantidad() + n;
                DetalleOrden actualizado = new DetalleOrden(pr, nuevaCantidad);
                carrito.set(idxExistente, actualizado);
                modeloCarrito.setValueAt(nuevaCantidad, idxExistente, 1);
                modeloCarrito.setValueAt(String.format("%.2f", actualizado.getSubtotal()), idxExistente, 2);
            } else {
                DetalleOrden d = new DetalleOrden(pr, n);
                carrito.add(d);
                modeloCarrito.addRow(new Object[]{pr.getNombre(), n, String.format("%.2f", d.getSubtotal())});
            }

            cantidad.setText("");
            actualizarTotalCarrito();
        }));

        btnQuitar.addActionListener(e -> {
            int fila = tablaCarrito.getSelectedRow();
            if (fila >= 0 && fila < carrito.size()) {
                int idx = tablaCarrito.convertRowIndexToModel(fila);
                carrito.remove(idx);
                modeloCarrito.removeRow(idx);
                actualizarTotalCarrito();
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto en la tabla del carrito para quitarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        limpiarCarrito.addActionListener(e -> {
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotalCarrito();
        });

        btnCrearOrden.addActionListener(e -> ejecutar(() -> {
            if (comboClienteOrden.getSelectedItem() == null) {
                throw new IllegalArgumentException("Seleccione un cliente.");
            }
            if (carrito.isEmpty()) {
                throw new IllegalArgumentException("Agregue al menos un producto al carrito antes de crear la orden.");
            }
            OrdenCompra o = s.crearOrden((Cliente) comboClienteOrden.getSelectedItem(), carrito);
            actualizarOrdenes();
            carrito.clear();
            modeloCarrito.setRowCount(0);
            actualizarTotalCarrito();
            exito("Orden #" + o.getNumero() + " registrada con éxito (Estado: Pendiente).");
        }));

        btnCompletar.addActionListener(e -> procesar(true));
        btnCancelar.addActionListener(e -> procesar(false));

        tabs.addTab("Órdenes", p);
    }

    private void actualizarTotalCarrito() {
        double total = carrito.stream().mapToDouble(DetalleOrden::getSubtotal).sum();
        lblTotalCarrito.setText(String.format("Total: Q%.2f", total));
    }

    private void actualizarOrdenes() {
        if (modeloOrdenes == null) return;
        modeloOrdenes.setRowCount(0);
        for (OrdenCompra o : s.ordenes) {
            modeloOrdenes.addRow(new Object[]{
                    o.getNumero(),
                    o.getCliente().getNombre(),
                    o.getFecha(),
                    String.format("%.2f", o.getTotal()),
                    o.getEstado()
            });
        }
    }

    private void procesar(boolean completar) {
        int fila = tablaOrdenes.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden en la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idx = tablaOrdenes.convertRowIndexToModel(fila);
        if (idx >= s.ordenes.size()) return;

        ejecutar(() -> {
            OrdenCompra o = s.ordenes.get(idx);
            s.cambiarEstado(o, completar ? "Completada" : "Cancelada");
            modeloOrdenes.setValueAt(o.getEstado(), idx, 4);
            actualizarInventario();
            actualizarProductos();
            exito(completar ? "Venta completada exitosamente y stock descontado." : "Orden cancelada.");
        });
    }

    private void actualizarCombos() {
        if (comboBodegaEmpleado != null) {
            Object sel = comboBodegaEmpleado.getSelectedItem();
            comboBodegaEmpleado.setModel(new DefaultComboBoxModel<>(s.empleados.toArray(new Empleado[0])));
            if (sel != null) comboBodegaEmpleado.setSelectedItem(sel);
        }
        if (comboInventarioProducto != null) {
            Object sel = comboInventarioProducto.getSelectedItem();
            comboInventarioProducto.setModel(new DefaultComboBoxModel<>(s.productos.toArray(new Producto[0])));
            if (sel != null) comboInventarioProducto.setSelectedItem(sel);
        }
        if (comboInventarioBodega != null) {
            Object sel = comboInventarioBodega.getSelectedItem();
            comboInventarioBodega.setModel(new DefaultComboBoxModel<>(s.bodegas.toArray(new Bodega[0])));
            if (sel != null) comboInventarioBodega.setSelectedItem(sel);
        }
        if (comboProductoOrden != null) {
            Object sel = comboProductoOrden.getSelectedItem();
            comboProductoOrden.setModel(new DefaultComboBoxModel<>(s.productos.toArray(new Producto[0])));
            if (sel != null) comboProductoOrden.setSelectedItem(sel);
        }
        if (comboClienteOrden != null) {
            Object sel = comboClienteOrden.getSelectedItem();
            comboClienteOrden.setModel(new DefaultComboBoxModel<>(s.clientes.toArray(new Cliente[0])));
            if (sel != null) comboClienteOrden.setSelectedItem(sel);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
