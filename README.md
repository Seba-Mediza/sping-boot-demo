# Calculadora (demo Spring Boot + MVC)

Demo de una API REST de calculadora construida con Spring Boot siguiendo el patrón
**Model-View-Controller (MVC)**. Expone 4 endpoints, uno por operación aritmética
(suma, resta, multiplicación, división), documentados y probables desde **Swagger UI**.

## Qué hace el proyecto

Recibe dos números (`a` y `b`) por query params y devuelve el resultado de aplicarles
una operación aritmética en formato JSON. Por ejemplo:

```
GET /api/calculadora/sumar?a=4&b=5
```

```json
{
  "a": 4.0,
  "b": 5.0,
  "operacion": "suma",
  "resultado": 9.0
}
```

Si se intenta dividir por cero, la API responde `400 Bad Request` con un mensaje de
error en lugar de romper con una excepción sin controlar.

## Estructura del proyecto

El código sigue el patrón MVC adaptado a una API REST: el **Model** es el dato de
dominio/DTO, la **Vista** es la representación JSON que arma Spring automáticamente,
y el **Controller** enruta las peticiones HTTP hacia el servicio (capa de negocio).

```
src/main/java/com/aydsii/calculadora/
├── CalculadoraApplication.java     # clase main (arranque de Spring Boot)
├── controller/
│   └── CalculadoraController.java  # expone los 4 endpoints REST
├── service/
│   └── CalculadoraService.java     # lógica de negocio (sumar, restar, multiplicar, dividir)
├── model/
│   └── ResultadoOperacion.java     # record usado como respuesta (Model/DTO)
├── exception/
│   └── GlobalExceptionHandler.java # traduce excepciones a respuestas HTTP (ej: división por cero -> 400)
└── config/
    └── OpenApiConfig.java          # metadata para Swagger/OpenAPI
```

Tests, en `src/test/java/com/aydsii/calculadora/`, replicando el mismo paquete que
la clase que testean (`service/` y `controller/`).

## Cómo configurarlo

Requisitos:

- JDK 25
- No hace falta instalar Maven: el proyecto trae Maven Wrapper (`mvnw` / `mvnw.cmd`)

No requiere base de datos para funcionar: aunque el `pom.xml` incluye el conector de
MySQL, no hay ningún `DataSource` configurado ni se usa en este demo (ver
[Cosas a tener en cuenta](#cosas-a-tener-en-cuenta)).

Pasos:

```powershell
git clone <url-del-repo>
cd sping-boot-demo
.\mvnw.cmd clean install
```

La configuración de la app vive en [src/main/resources/application.properties](src/main/resources/application.properties):

```properties
spring.application.name=calculadora

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

Si querés correr la app en otro puerto, agregá `server.port=8081` (por ejemplo) a ese
archivo.

## Cómo modificar y correr la suite de tests

Hay dos clases de test:

- `service/CalculadoraServiceTest.java`: tests unitarios puros sobre la lógica de
  negocio (sin levantar contexto de Spring), uno por operación más el caso de
  división por cero.
- `controller/CalculadoraControllerTest.java`: tests de la capa web con
  `@WebMvcTest` + `MockMvc`, simulando el `CalculadoraService` con `@MockitoBean`
  para verificar que cada endpoint devuelve el status HTTP y el JSON esperado.

Para agregar un caso nuevo (por ejemplo, otra operación), el patrón a seguir es:
1. Agregar el método en `CalculadoraService` (y su test en `CalculadoraServiceTest`).
2. Agregar el endpoint en `CalculadoraController` (y su test en `CalculadoraControllerTest`).
3. Si la operación puede fallar (como la división por cero), lanzar una excepción
   desde el service y mapearla en `GlobalExceptionHandler`.

Comandos:

```powershell
# Correr toda la suite
.\mvnw.cmd test

# Correr una sola clase
.\mvnw.cmd test -Dtest=CalculadoraServiceTest

# Correr un solo método
.\mvnw.cmd test -Dtest=CalculadoraServiceTest#dividirPorCeroLanzaArithmeticException
```

Los resultados detallados quedan en `target/surefire-reports/*.txt`. También se
pueden correr/debuggear individualmente desde el IDE (por ejemplo, con la extensión
de Java de VSCode aparece un botón ▶️ sobre cada `@Test`).

## Cómo ponerlo en marcha y probarlo con Swagger

Levantar la aplicación:

```powershell
.\mvnw.cmd spring-boot:run
```

Por defecto queda escuchando en `http://localhost:8080`. Con la app corriendo:

- **Swagger UI** (para probar los endpoints desde el navegador):
  `http://localhost:8080/swagger-ui.html`
- **Definición OpenAPI** (JSON, por si se quiere importar a otra herramienta):
  `http://localhost:8080/v3/api-docs`

Desde Swagger UI se puede desplegar cada operación (`GET /api/calculadora/sumar`,
`/restar`, `/multiplicar`, `/dividir`), tocar "Try it out", cargar `a` y `b`, y
ejecutar la petición para ver la respuesta real.

Los mismos endpoints se pueden probar directo por navegador o `curl`:

```
GET http://localhost:8080/api/calculadora/sumar?a=4&b=5
GET http://localhost:8080/api/calculadora/restar?a=10&b=3
GET http://localhost:8080/api/calculadora/multiplicar?a=6&b=7
GET http://localhost:8080/api/calculadora/dividir?a=10&b=2
```

## Cosas a tener en cuenta

- **`a` y `b` son `double`**: aceptan decimales (ej. `a=2.5`), no solo enteros.
- **División por cero**: no lanza un error 500 sin manejar; `GlobalExceptionHandler`
  lo intercepta y devuelve `400 Bad Request` con `{"error": "No se puede dividir por cero"}`.
- **Dependencias sin usar todavía**: el `pom.xml` trae `mysql-connector-j` y
  `lombok`, pero el código actual no los usa (no hay persistencia ni entidades). Se
  dejaron declarados pensando en una posible extensión futura del demo, no son
  necesarios para correr la calculadora tal como está.
- **`spring-boot-devtools`**: está en el classpath, así que al modificar código con
  la app corriendo (`spring-boot:run`) va a reiniciar sola.
- **Sin autenticación**: los endpoints están abiertos, es un demo educativo, no
  pensado para producción.
