-- SpeedFast - Semana 8
-- CRUD completo con repartidores, pedidos y entregas.

CREATE DATABASE IF NOT EXISTS speedfast_db;
USE speedfast_db;

CREATE TABLE IF NOT EXISTS repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS') NOT NULL,
    estado ENUM('PENDIENTE','EN_REPARTO','ENTREGADO') NOT NULL
);

CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_repartidor INT NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    CONSTRAINT fk_entregas_pedidos
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    CONSTRAINT fk_entregas_repartidores
        FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
);
