# Guía de entrega - Semana 8

## 1. Preparar MySQL

En MySQL Workbench ejecuta:

```text
database/speedfast_db_semana8.sql
```

Después confirma que en `speedfast_db` aparezcan:

```text
repartidores
pedidos
entregas
```

## 2. Configurar IntelliJ

Abre el proyecto y carga Maven.

En:

```text
Run > Edit Configurations > main.Main
```

mantén tus variables:

```text
SPEEDFAST_DB_USER=speedfast
SPEEDFAST_DB_PASSWORD=tu_contraseña
```

No subas la contraseña a GitHub.

## 3. Flujo de prueba recomendado

### Repartidores

1. Crear `Camila Soto`.
2. Crear `Juan Pérez`.
3. Seleccionar uno y cambiar su nombre.
4. Verificar la actualización.
5. Crear un tercero y eliminarlo.

### Pedidos

1. Crear pedido:
   - Dirección: `Av. Providencia 123`
   - Tipo: `COMIDA`
   - Estado: `PENDIENTE`
2. Crear otro pedido.
3. Editar dirección, tipo o estado.
4. Crear un tercero y eliminarlo.

### Entregas

1. Abrir Gestión de Entregas.
2. Confirmar que los combos muestran algo como:
   - `1 - Av. Providencia 123`
   - `1 - Camila Soto`
3. Crear una entrega.
4. Seleccionarla y cambiar pedido, repartidor, fecha u hora.
5. Eliminar una entrega.
6. Verificar que la tabla se refresque después de cada operación.

## 4. Prueba de integridad referencial

Si intentas eliminar un pedido o repartidor que tiene una entrega asociada,
MySQL puede impedir la eliminación por la clave foránea. Eso es correcto.

La aplicación mostrará un mensaje de error en vez de cerrarse.

## 5. Git local

Esta semana usa Git local desde el inicio.

Crea primero un repositorio público nuevo en GitHub, por ejemplo:

```text
SpeedFast_Semana8
```

Después abre una terminal dentro de esta carpeta y ejecuta:

```bash
git init
git branch -M main
git remote add origin https://github.com/Lfante/SpeedFast_Semana8.git
```

Para evidenciar control de versiones conviene hacer varios commits.

### Commit 1 - estructura, modelo y base de datos

```bash
git add .gitignore pom.xml database src/modelo src/dao/ConexionDB.java src/dao/CrudDAO.java
git commit -m "Semana 8 - estructura, modelo y base de datos"
```

### Commit 2 - CRUD DAO

```bash
git add src/dao/RepartidorDAO.java src/dao/PedidoDAO.java src/dao/EntregaDAO.java
git commit -m "Semana 8 - CRUD completo con JDBC"
```

### Commit 3 - interfaz gráfica

```bash
git add src/vista src/main
git commit -m "Semana 8 - interfaz Swing conectada a CRUD"
```

### Commit 4 - documentación

```bash
git add README.md GUIA_ENTREGA.md
git commit -m "Semana 8 - documentación y guía de entrega"
```

Finalmente:

```bash
git push -u origin main
```

## 6. Entrega en AVA

Entrega:

1. Enlace del repositorio GitHub nuevo.
2. Archivo ZIP del proyecto.

Antes de entregar, verifica que el repositorio no tenga:

```text
.idea/
out/
target/
*.class
```
