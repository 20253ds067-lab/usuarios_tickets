CREATE DATABASE IF NOT EXISTS tickets_db;
USE tickets_db;

DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE tickets (
    id BIGINT NOT NULL AUTO_INCREMENT,
    descripcion VARCHAR(255) NOT NULL,
    estado ENUM('ABIERTO', 'EN_PROCESO', 'CERRADO') NOT NULL DEFAULT 'ABIERTO',
    prioridad ENUM('BAJA', 'MEDIA', 'ALTA') NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tickets_usuarios FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO usuarios (nombre, activo) VALUES
('Juan Perez', TRUE),
('Maria Garcia', TRUE),
('Carlos Lopez', FALSE);

INSERT INTO tickets (descripcion, estado, prioridad, usuario_id) VALUES
('Fallo en conexion de red', 'ABIERTO', 'ALTA', 1),
('Instalacion de paquete ofimatico', 'EN_PROCESO', 'MEDIA', 1),
('Reemplazo de teclado', 'CERRADO', 'BAJA', 2);
