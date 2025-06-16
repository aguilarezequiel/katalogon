INSERT INTO modelos_inventario (nombreMetodo, descripcion) VALUES ('LOTE_FIJO', 'Modelo de inventario con lote de pedido fijo');
INSERT INTO modelos_inventario (nombreMetodo, descripcion) VALUES ('INTERVALO_FIJO', 'Modelo de inventario con intervalo de tiempo fijo');

INSERT INTO estados_orden_compra (nombreEstadoOrdenCompra, descripcion) VALUES ('PENDIENTE', 'Orden de compra pendiente');
INSERT INTO estados_orden_compra (nombreEstadoOrdenCompra, descripcion) VALUES ('ENVIADA', 'Orden de compra enviada');
INSERT INTO estados_orden_compra (nombreEstadoOrdenCompra, descripcion) VALUES ('FINALIZADA', 'Orden de compra finalizada');
INSERT INTO estados_orden_compra (nombreEstadoOrdenCompra, descripcion) VALUES ('CANCELADA', 'Orden de compra cancelada');

INSERT INTO proveedores (nombreProveedor, activo) VALUES ('Proveedor ABC S.A.', true);
INSERT INTO proveedores (nombreProveedor, activo) VALUES ('Distribuidora XYZ', true);
INSERT INTO proveedores (nombreProveedor, activo) VALUES ('Importadora Global', true);
INSERT INTO proveedores (nombreProveedor, activo) VALUES ('Suministros del Norte', true);

INSERT INTO articulos (descripcionArticulo, stockActual, stockSeguridad, demanda, inventarioMaximo, costoMantenimiento, activo, loteOptimo, puntoPedido, costoAlmacenamiento, costoPedido, costoCompra, cgi, modelo_inventario_id, proveedor_predeterminado_id) VALUES ('Laptop Dell Inspiron 15', 25, 10.0, 50.0, 100.0, 25.0, true, 20.0, 15.0, 2.5, 150.0, 800.0, 1250.0, 1, 1);
INSERT INTO articulos (descripcionArticulo, stockActual, stockSeguridad, demanda, inventarioMaximo, costoMantenimiento, activo, loteOptimo, puntoPedido, costoAlmacenamiento, costoPedido, costoCompra, cgi, modelo_inventario_id, proveedor_predeterminado_id) VALUES ('Mouse Logitech MX Master', 100, 20.0, 200.0, 300.0, 5.0, true, 50.0, 30.0, 1.0, 25.0, 45.0, 275.0, 1, 2);
INSERT INTO articulos (descripcionArticulo, stockActual, stockSeguridad, demanda, inventarioMaximo, costoMantenimiento, activo, loteOptimo, puntoPedido, costoAlmacenamiento, costoPedido, costoCompra, cgi, modelo_inventario_id, proveedor_predeterminado_id) VALUES ('Teclado Mecánico RGB', 45, 15.0, 80.0, 150.0, 12.0, true, 25.0, 20.0, 3.0, 75.0, 120.0, 450.0, 1, 1);
INSERT INTO articulos (descripcionArticulo, stockActual, stockSeguridad, demanda, inventarioMaximo, costoMantenimiento, activo, loteOptimo, puntoPedido, costoAlmacenamiento, costoPedido, costoCompra, cgi, modelo_inventario_id, proveedor_predeterminado_id) VALUES ('Monitor 24 pulgadas', 15, 8.0, 30.0, 60.0, 45.0, true, 12.0, 10.0, 8.0, 200.0, 350.0, 850.0, 2, 3);
INSERT INTO articulos (descripcionArticulo, stockActual, stockSeguridad, demanda, inventarioMaximo, costoMantenimiento, activo, loteOptimo, puntoPedido, costoAlmacenamiento, costoPedido, costoCompra, cgi, modelo_inventario_id, proveedor_predeterminado_id) VALUES ('Impresora HP LaserJet', 8, 5.0, 15.0, 30.0, 75.0, true, 6.0, 6.0, 15.0, 300.0, 450.0, 1200.0, 2, 2);

INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (1, 1, 7, 800.00, 150.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (1, 3, 10, 790.00, 180.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (2, 2, 3, 45.00, 25.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (2, 4, 5, 42.00, 30.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (3, 1, 5, 120.00, 75.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (3, 2, 4, 125.00, 70.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (4, 3, 8, 350.00, 200.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (4, 4, 6, 345.00, 180.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (5, 2, 10, 450.00, 300.00, true);
INSERT INTO articulo_proveedor (articulo_id, proveedor_id, demoraEntrega, precioUnitario, costoPedido, activo) VALUES (5, 3, 12, 440.00, 320.00, true);

INSERT INTO ventas (fechaHoraVenta) VALUES ('2025-06-01 10:30:00');
INSERT INTO ventas (fechaHoraVenta) VALUES ('2025-06-02 14:15:00');
INSERT INTO ventas (fechaHoraVenta) VALUES ('2025-06-03 09:45:00');

INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (1, 1, 2, 850.00);
INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (1, 2, 3, 50.00);
INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (2, 3, 1, 135.00);
INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (2, 4, 1, 380.00);
INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (3, 2, 5, 48.00);
INSERT INTO venta_articulo (venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (3, 5, 1, 480.00);

INSERT INTO ordenes_compra (articulo_id, proveedor_id, cantidad, fechaCreacion) VALUES (1, 1, 20, '2025-06-15 08:00:00');
INSERT INTO ordenes_compra (articulo_id, proveedor_id, cantidad, fechaCreacion) VALUES (2, 2, 50, '2025-06-14 16:30:00');

INSERT INTO orden_compra_estado (orden_compra_id, estado_id, fechaHoraInicio) VALUES (1, 1, '2025-06-15 08:00:00');
INSERT INTO orden_compra_estado (orden_compra_id, estado_id, fechaHoraInicio) VALUES (2, 2, '2025-06-14 16:30:00');