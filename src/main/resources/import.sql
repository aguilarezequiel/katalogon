SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO modelos_inventario (id, nombreMetodo, descripcion) VALUES (1, 'LOTE_FIJO', 'Modelo de inventario con lote de pedido fijo');
INSERT INTO modelos_inventario (id, nombreMetodo, descripcion) VALUES (2, 'INTERVALO_FIJO', 'Modelo de inventario con intervalo de tiempo fijo');

INSERT INTO estados_orden_compra (codEstadoOrdenCompra, nombreEstadoOrdenCompra, descripcion) VALUES (1, 'PENDIENTE', 'Orden de compra creada, pendiente de envío');
INSERT INTO estados_orden_compra (codEstadoOrdenCompra, nombreEstadoOrdenCompra, descripcion) VALUES (2, 'ENVIADA', 'Orden de compra enviada al proveedor');
INSERT INTO estados_orden_compra (codEstadoOrdenCompra, nombreEstadoOrdenCompra, descripcion) VALUES (3, 'FINALIZADA', 'Orden de compra recibida y procesada');
INSERT INTO estados_orden_compra (codEstadoOrdenCompra, nombreEstadoOrdenCompra, descripcion) VALUES (4, 'CANCELADA', 'Orden de compra cancelada');

INSERT INTO proveedores (codProveedor, nombreProveedor, fechaHoraBaja, activo) VALUES (1, 'Distribuidora Central S.A.', NULL, TRUE);
INSERT INTO proveedores (codProveedor, nombreProveedor, fechaHoraBaja, activo) VALUES (2, 'Comercial Norte Ltda.', NULL, TRUE);
INSERT INTO proveedores (codProveedor, nombreProveedor, fechaHoraBaja, activo) VALUES (3, 'Importadora Sur S.R.L.', NULL, TRUE);
INSERT INTO proveedores (codProveedor, nombreProveedor, fechaHoraBaja, activo) VALUES (4, 'Mayorista Este S.A.', NULL, TRUE);
INSERT INTO proveedores (codProveedor, nombreProveedor, fechaHoraBaja, activo) VALUES (5, 'Proveedor Oeste Ltda.', NULL, TRUE);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (1, 'Smartphone Samsung Galaxy A54', 45, 10.0, 360.0, 25.50, NULL, TRUE, 60.0, 35.0, NULL, 9825.75, 1, 1, NULL);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (2, 'Laptop HP Pavilion 15', 12, 5.0, 120.0, 180.00, NULL, TRUE, 20.0, 15.0, NULL, 24650.00, 2, 1, NULL);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (3, 'Tablet iPad Air 10.9', 8, 3.0, 96.0, 150.00, NULL, TRUE, 15.0, 10.0, NULL, 19875.50, 1, 1, NULL);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (4, 'Auriculares Sony WH-1000XM4', 25, 8.0, 200.0, 45.00, NULL, TRUE, 35.0, 20.0, NULL, 7150.25, 3, 1, NULL);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (5, 'Monitor LG 27 4K', 6, 2.0, 72.0, 120.00, NULL, TRUE, 12.0, 8.0, NULL, 15420.00, 2, 1, NULL);

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (6, 'Mouse Logitech MX Master 3', 35, 12.0, 240.0, 15.75, NULL, TRUE, NULL, NULL, 43200, 3680.50, 4, 2, '2025-05-15 10:30:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (7, 'Teclado Mecánico Corsair K95', 18, 6.0, 144.0, 35.25, NULL, TRUE, NULL, NULL, 64800, 5950.75, 3, 2, '2025-04-20 14:15:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (8, 'Webcam Logitech C920', 22, 8.0, 180.0, 22.50, NULL, TRUE, NULL, NULL, 43200, 4575.00, 4, 2, '2025-05-25 09:45:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (9, 'Disco SSD Samsung 1TB', 40, 15.0, 300.0, 65.00, NULL, TRUE, NULL, NULL, 86400, 9125.25, 5, 2, '2025-04-10 16:20:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (10, 'Router WiFi TP-Link AX3000', 15, 5.0, 90.0, 85.00, NULL, TRUE, NULL, NULL, 129600, 7650.50, 5, 2, '2025-03-15 11:30:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (11, 'Cable USB-C Premium', 50, 20.0, 500.0, 5.25, NULL, TRUE, NULL, NULL, 240, 1250.00, 1, 2, '2025-06-20 08:00:00');

INSERT INTO articulos (codArticulo, descripcionArticulo, stockActual, stockSeguridad, demanda, costoAlmacenamiento, fechaHoraBaja, activo, loteOptimo, puntoPedido, tiempoIntervaloMinutos, cgi, proveedor_predeterminado_id, modelo_inventario_id, fechaUltimaCompra) VALUES (12, 'Protector de Pantalla', 75, 30.0, 800.0, 2.50, NULL, TRUE, NULL, NULL, 120, 800.00, 2, 2, '2025-06-21 06:30:00');

INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (1, 1, 1, 7, 285.50, NULL, 45.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (2, 1, 2, 10, 290.00, NULL, 50.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (3, 1, 3, 5, 288.75, NULL, 42.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (4, 2, 2, 14, 750.00, NULL, 85.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (5, 2, 4, 12, 745.50, NULL, 80.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (6, 3, 1, 10, 520.00, NULL, 65.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (7, 3, 3, 8, 525.75, NULL, 70.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (8, 4, 3, 6, 125.50, NULL, 25.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (9, 4, 1, 8, 128.00, NULL, 28.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (10, 4, 4, 5, 124.25, NULL, 22.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (11, 5, 2, 12, 485.00, NULL, 75.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (12, 5, 5, 15, 490.50, NULL, 78.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (13, 6, 4, 4, 65.25, NULL, 15.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (14, 6, 1, 6, 67.50, NULL, 18.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (15, 7, 3, 8, 185.75, NULL, 35.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (16, 7, 5, 10, 188.00, NULL, 38.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (17, 8, 4, 5, 95.50, NULL, 20.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (18, 8, 2, 7, 98.25, NULL, 22.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (19, 9, 5, 9, 145.00, NULL, 30.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (20, 9, 1, 11, 148.50, NULL, 32.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (21, 9, 3, 7, 143.75, NULL, 28.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (22, 10, 5, 12, 215.00, NULL, 45.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (23, 10, 2, 14, 218.75, NULL, 48.00, TRUE);

INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (24, 11, 1, 1, 8.50, NULL, 5.00, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (25, 11, 3, 2, 8.25, NULL, 4.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (26, 12, 2, 1, 3.50, NULL, 2.50, TRUE);
INSERT INTO articulo_proveedor (id, articulo_id, proveedor_id, demoraEntrega, precioUnitario, fechaHoraBaja, costoPedido, activo) VALUES (27, 12, 4, 1, 3.25, NULL, 2.00, TRUE);

INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (1, '2025-06-01 10:30:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (2, '2025-06-01 14:15:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (3, '2025-06-02 09:45:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (4, '2025-06-02 16:20:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (5, '2025-06-03 11:10:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (6, '2025-06-03 15:30:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (7, '2025-06-04 13:25:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (8, '2025-06-05 10:15:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (9, '2025-06-05 17:40:00');
INSERT INTO ventas (codVenta, fechaHoraVenta) VALUES (10, '2025-06-06 12:55:00');

INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (1, 1, 1, 2, 320.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (2, 1, 4, 1, 140.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (3, 2, 2, 1, 850.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (4, 3, 6, 3, 75.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (5, 3, 8, 1, 110.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (6, 4, 3, 1, 580.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (7, 4, 7, 1, 210.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (8, 5, 9, 2, 165.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (9, 5, 1, 1, 320.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (10, 6, 5, 1, 550.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (11, 7, 4, 2, 140.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (12, 7, 6, 1, 75.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (13, 8, 10, 1, 245.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (14, 9, 1, 3, 320.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (15, 9, 8, 2, 110.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (16, 10, 2, 1, 850.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (17, 10, 9, 1, 165.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (18, 1, 11, 5, 12.00);
INSERT INTO venta_articulo (id, venta_id, articulo_id, cantidadVentaArticulo, precioVenta) VALUES (19, 2, 12, 10, 5.50);

INSERT INTO ordenes_compra (codOC, articulo_id, proveedor_id, cantidad, fechaCreacion, fechaEnvio, fechaFinalizacion) VALUES (1, 1, 1, 60, '2025-06-01 08:00:00', '2025-06-01 16:00:00', '2025-06-08 10:30:00');
INSERT INTO ordenes_compra (codOC, articulo_id, proveedor_id, cantidad, fechaCreacion, fechaEnvio, fechaFinalizacion) VALUES (2, 4, 3, 35, '2025-06-02 09:15:00', '2025-06-02 15:30:00', NULL);
INSERT INTO ordenes_compra (codOC, articulo_id, proveedor_id, cantidad, fechaCreacion, fechaEnvio, fechaFinalizacion) VALUES (3, 6, 4, 50, '2025-06-03 11:00:00', NULL, NULL);
INSERT INTO ordenes_compra (codOC, articulo_id, proveedor_id, cantidad, fechaCreacion, fechaEnvio, fechaFinalizacion) VALUES (4, 9, 5, 40, '2025-06-04 14:20:00', '2025-06-04 17:45:00', NULL);
INSERT INTO ordenes_compra (codOC, articulo_id, proveedor_id, cantidad, fechaCreacion, fechaEnvio, fechaFinalizacion) VALUES (5, 2, 2, 20, '2025-06-05 10:30:00', NULL, NULL);

INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (1, 1, 1, '2025-06-01 08:00:00', '2025-06-01 16:00:00');
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (2, 1, 2, '2025-06-01 16:00:00', '2025-06-08 10:30:00');
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (3, 1, 3, '2025-06-08 10:30:00', NULL);
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (4, 2, 1, '2025-06-02 09:15:00', '2025-06-02 15:30:00');
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (5, 2, 2, '2025-06-02 15:30:00', NULL);
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (6, 3, 1, '2025-06-03 11:00:00', NULL);
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (7, 4, 1, '2025-06-04 14:20:00', '2025-06-04 17:45:00');
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (8, 4, 2, '2025-06-04 17:45:00', NULL);
INSERT INTO orden_compra_estado (id, orden_compra_id, estado_id, fechaHoraInicio, fechaHoraFin) VALUES (9, 5, 1, '2025-06-05 10:30:00', NULL);

INSERT INTO configuracion_gestion_inventario (id, loteOptimo, puntoPedido, fechaHoraBajaConfiguracion, tiempoIntervalo, fechoHoraAltaConfiguracion, activo) VALUES (1, 60, 35, '2025-12-31 23:59:59', 30, '2025-01-01 00:00:00', TRUE);
INSERT INTO configuracion_gestion_inventario (id, loteOptimo, puntoPedido, fechaHoraBajaConfiguracion, tiempoIntervalo, fechoHoraAltaConfiguracion, activo) VALUES (2, 20, 15, '2025-12-31 23:59:59', 45, '2025-01-01 00:00:00', TRUE);
INSERT INTO configuracion_gestion_inventario (id, loteOptimo, puntoPedido, fechaHoraBajaConfiguracion, tiempoIntervalo, fechoHoraAltaConfiguracion, activo) VALUES (3, 15, 10, '2025-12-31 23:59:59', 30, '2025-01-01 00:00:00', TRUE);

SET FOREIGN_KEY_CHECKS = 1;