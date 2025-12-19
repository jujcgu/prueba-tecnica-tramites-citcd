# Gestión de Trámites – Full Stack (Spring Boot 4 + Angular 21 + PostgreSQL 18)

Sistema web para **radicación, asignación, seguimiento y gestión de trámites**, con archivos adjuntos, control por roles y trazabilidad tipo *timeline*.

---

## Contenido
- [Stack](#stack)
- [Características](#características)
- [Requisitos e instalaciones](#Requisitos-e-instalaciones)
- [Estructura del repositorio](#estructura-del-repositorio)
- [Configuración del Backend](#configuración-del-backend)
- [Configuración del Frontend](#configuración-del-frontend)
- [Troubleshooting](#troubleshooting)

---

## Stack
- **Backend:** Spring Boot **4**
- **Frontend:** Angular **21.0.3**
- **DB:** PostgreSQL **18.1**
- **Build:** Gradle **9.2.1**

---

## Características
- ✅ Radicar trámite (formulario + validaciones + archivos adjuntos )
- ✅ Requisitos documentales por tipo de trámite
- ✅ Panel de funcionario: trámites asignados, filtro por estado, acciones
- ✅ Detalle del trámite: info general, adjuntos, **timeline** de seguimiento
- ✅ Seguridad por roles (ej: **ADMINISTRATIVO**, **FUNCIONARIO**, **CIUDADANO**)
- ✅ Buenas prácticas: módulos por funcionalidad, servicios organizados, guards

---

## Requisitos e instalaciones

### Backend
Instala:
- **Java JDK 25**
- **Git**
- **Gradle**
- **PostgreSQL**
- **Cliente DB: DBeaver o PgAdmin**

(**Java**) Debería mostrar "25.0.1".
```bash
java -version
```
(**git**) Debería mostrar "2.52.0".
```bash
git --version
```
(**postgresql**) Debería mostrar "18.1".
```bash
show server_version;
```

### Frontend
Instala:
- **NodeJS**

(**node**) Debería mostrar "v24.12.0".
```bash
node -v
```
(**npm**) Debería mostrar "11.6.2".
```bash
npm -v
```
(**angular cli**) Debería mostrar "21.0.3".
```bash
ng version
```

## Estructura del repositorio
├─ backend/demo/                  # Spring Boot 4\
│  ├─ src/main/java/...\
│  ├─ src/main/resources/\
│  │  └─ application.yml\
│  ├─ build.gradle\
│  └─ gradlew\
├─ frontend/demo/                 # Angular 21.0.3\
│  ├─ src/\
│  ├─ angular.json\
│  └─ package.json\
├─ db/                       # Scripts SQL\
└─ postman/

## Configuración del Backend
Entrar en la carpeta
```bash
cd backend/demo/
```
Configura tu conexión a DB en **application.yaml**
```bash
spring:
  application:
    name: demo
  datasource:
    url: jdbc:postgresql://localhost:5432/demo
    username: demo
    password: demo
  jpa:
    open-in-view: false
  config:
    import:
    - "optional:classpath:jwt.yaml"
    - "optional:classpath:storage.yaml"
  mvc:
    problemdetails:
      enabled: true
```
Configura tu directorio de archivos adjuntos en **storage.yaml**
```bash
storage:
  location: C:/Users/user/Documents/upload-dir
```
Ejecuta
```bash
./gradlew bootRun
```
## Configuración del Frontend
Entra a la carpeta
```bash
cd frontend/demo/
```
Instala dependencias
```bash
npm install
```
Configura **proxy.config.json** con la URL del API
```bash
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```
Ejecuta
```bash
npm start
```
## Troubleshooting
**Angular CLI / Node incompatible**
- Si ves errores tipo EBADENGINE, actualiza Node a v24.12.0.

**DB connection refused**

- Verifica que Postgres esté arriba y el puerto 5432 disponible.

- Revisa **application.yaml**, usuario y contraseña.
