# SpeedFast - Semana 8

Actividad sumativa de **Desarrollo Orientado a Objetos II**.

## Objetivo

Completar el ciclo funcional de SpeedFast implementando CRUD completo para:

- Repartidores.
- Pedidos.
- Entregas.

La aplicación utiliza Java Swing, JDBC, MySQL, `PreparedStatement`,
`ResultSet`, validaciones y manejo de excepciones.

## Estructura

```text
SpeedFast_Semana8/
├── database/
│   └── speedfast_db_semana8.sql
├── src/
│   ├── dao/
│   │   ├── ConexionDB.java
│   │   ├── CrudDAO.java
│   │   ├── EntregaDAO.java
│   │   ├── PedidoDAO.java
│   │   └── RepartidorDAO.java
│   ├── main/
│   │   └── Main.java
│   ├── modelo/
│   │   ├── Entrega.java
│   │   ├── EstadoPedido.java
│   │   ├── Pedido.java
│   │   ├── Repartidor.java
│   │   └── TipoPedido.java
│   └── vista/
│       ├── VentanaEntregas.java
│       ├── VentanaPedidos.java
│       ├── VentanaPrincipal.java
│       └── VentanaRepartidores.java
├── .gitignore
├── pom.xml
└── README.md
```

## CRUD implementado

Los tres DAO implementan los métodos:

```java
create()
readAll()
update()
delete()
```

mediante una interfaz genérica `CrudDAO<T>`.

### RepartidorDAO

Gestiona `repartidores`.

### PedidoDAO

Gestiona `pedidos`.

### EntregaDAO

Gestiona `entregas`.

## Interfaz gráfica

### Repartidores

Permite:

- Crear.
- Listar.
- Seleccionar un registro.
- Actualizar.
- Eliminar.
- Refrescar la tabla.

### Pedidos

Permite:

- Crear pedidos con dirección, tipo y estado.
- Listar.
- Actualizar.
- Eliminar.
- Refrescar la tabla.

### Entregas

Permite:

- Seleccionar Pedido mediante `JComboBox`.
- Seleccionar Repartidor mediante `JComboBox`.
- Registrar fecha y hora.
- Crear, listar, actualizar y eliminar entregas.
- Refrescar combos y tabla.

Los `JComboBox` muestran un texto legible con ID y nombre/dirección,
pero mantienen el ID dentro del objeto seleccionado.

## Base de datos

Ejecuta en MySQL Workbench:

```text
database/speedfast_db_semana8.sql
```

La Semana 8 utiliza las tablas indicadas en la pauta:

```text
repartidores
pedidos
entregas
```

## Configuración JDBC

El proyecto utiliza las mismas variables de entorno que la Semana 7:

```text
SPEEDFAST_DB_URL
SPEEDFAST_DB_USER
SPEEDFAST_DB_PASSWORD
```

Por ejemplo:

```text
SPEEDFAST_DB_USER=speedfast
SPEEDFAST_DB_PASSWORD=TU_PASSWORD
```

No es necesario guardar credenciales dentro del código.

## Ejecución

1. Abrir el proyecto en IntelliJ IDEA.
2. Cargar Maven.
3. Ejecutar el script SQL.
4. Configurar las variables de entorno de la configuración `main.Main`.
5. Ejecutar `src/main/Main.java`.
6. Presionar **Probar conexión a MySQL**.
7. Probar el CRUD completo de las tres entidades.
