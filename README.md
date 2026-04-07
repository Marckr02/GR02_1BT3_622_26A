# Dark Kitchen - Base del Sistema

Base inicial de una aplicacion web Java con JSP + Servlet + Hibernate/JPA para implementar 4 casos de uso.

## Arquitectura

- `src/main/java/model`: entidades JPA
- `src/main/java/dao`: CRUD generico e implementaciones Hibernate
- `src/main/java/service`: servicios stub para logica de negocio
- `src/main/java/servlet`: servlets stub mapeados en `web.xml`
- `src/main/resources/META-INF/persistence.xml`: configuracion JPA/Hibernate
- `src/main/webapp/WEB-INF/views`: JSPs base por caso de uso
- `src/main/webapp/resources`: recursos estaticos (CSS/JS)

## Rutas pre-mapeadas

- `/pedidos/recibir` (CU1)
- `/pedidos/kanban` (CU2)
- `/insumos/entrada` (CU3)
- `/menu/bloqueo` (CU4)

## Base de datos

La configuracion por defecto usa H2 local con:

- `hibernate.hbm2ddl.auto=update`
- URL: `jdbc:h2:./darkkitchen-db;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`

## Verificacion rapida

```powershell
mvn test
mvn package
```

El artefacto generado queda en `target/GR02_1BT3_622_26A.war`.

