## Trabajo Práctico Nro. 2 Arquitectura REST con Spring Boot

Análisis y Diseño de Sistemas II

September 16, 2026

## Objetivos

El objetivo de este trabajo práctico es desarrollar una API REST con Spring Boot para gestionar diferentes funcionalidades de una tienda de tecnología.

Todos los ejercicios estarán relacionados con TechStore, un pequeño sistema de gestión de una tienda que trabaja con productos, categorías, clientes, pedidos y ventas.

A lo largo de los ejercicios se incorporarán distintos conceptos de desarrollo de APIs REST,

como:

- creación de endpoints con Spring Boot;

- uso de diferentes métodos HTTP (GET, POST, PUT y DELETE);

- recepción de datos mediante @RequestBody, @PathVariable y @RequestParam;

- utilización de DTOs para representar los datos que entran y salen de la API;

- validación de datos recibidos;

- manejo de errores y respuestas HTTP;

- separación de responsabilidades entre controlador, servicio y otras capas de la aplicación;

- documentación y prueba de los endpoints mediante Swagger/OpenAPI.

Todos los ejercicios giran en torno a TechStore, un pequeño sistema de gestión de una tienda de tecnología: productos, categorías, clientes y pedidos. Van a ir construyendo distintos módulos de la misma aplicación.

## Consignas generales

Los siguientes requisitos deben ser aplicados a todos los ejercicios.

- 1. Formato de respuesta estándar. Toda respuesta, sin excepción (éxito o error), debe tener esta forma:

```
{
"status": 200,
"messege": "Operacion realizada con exito",
"data": {}
}
```

- status: código HTTP como número (200, 201, 400, 404, 500, etc.)


- messege: mensaje descriptivo, en español, de lo que pasó.

- data: el contenido real de la respuesta. Puede ser un objeto, una lista, o null en errores.

Se recomienda armar una clase genérica ApiResponse<T> reutilizable y un @ControllerAdvice con @ExceptionHandler para que los errores (validación, no encontrado, error interno) también respeten este formato automáticamente, sin tener que armarlo a mano en cada

controller.

- 2. Documentación con Swagger. Todos los endpoints deben estar documentados con springdoc-openapi (@Operation, @ApiResponse, @Parameter, @Schema donde corresponda). La corrección se va a probar desde Swagger UI (/swagger-ui.html).

- 3. Códigos de estado correctos. 200/201 en éxito, 400 en datos inválidos, 404 en recurso no encontrado, 500 solo para errores no controlados.

- 4. Paquetes sugeridos: controller, service, repository, dto, model/entity, exception.

- 5. Convenciones de código

El código deberá respetar las convenciones de nombres habituales de Java. En particular:

- Las clases e interfaces deberán utilizar UpperCamelCase, por ejemplo: ProductoDTO.

- Los métodos y variables o precioMaximo. deberán utilizar lowerCamelCase, por ejemplo: buscarProductos

- Las constantes deberán utilizar UPPER_SNAKE_CASE, por ejemplo: MAX_STOCK.

- Los paquetes deberán escribirse utilizando letras minúsculas.

- Los nombres deberán ser claros, descriptivos y consistentes, evitando abreviat- uras innecesarias.

Como referencia, consultar la guía Java Naming Conventions de Baeldung. [URL 🔗](https://www.baeldung.com/java-naming-conventions)


## 1 Ejercicio 1 — Procesamiento de ventas

La tienda recibe lotes de ventas desde otro sistema y los quiere analizar. En cada petición recibe una lista de ventas en formato JSON y se devuelve un conjunto de estadísticas calculadas.

La información recibida no debe guardarse en una base de datos. Simplemente se procesa en memoria y se devuelve el resultado.

Cada venta tiene:

```
{
"producto": "Mouse inalambrico",
"cantidad": 3,
"precioUnitario": 4500.0
}
```

Para recibir la lista de ventas deberán utilizar @RequestBody y @Valid.

## Endpoint 1: POST /api/ventas/estadisticas

Recibe una lista de VentaDTO y devuelve:

- totalFacturado: suma de (cantidad × precioUnitario) de todas las ventas.

- cantidadVentas: cantidad de ventas recibidas.

- ticketPromedio: totalFacturado / cantidadVentas.

- ventaMayor y ventaMenor: el objeto venta (con su importe) de mayor y menor monto.

- productoMasVendido: el nombre del producto con mayor cantidad acumulada (sumando cantidad si se repite el producto en la lista).

Validaciones requeridas (con Bean Validation, @Valid):

- producto: no vacío.

- cantidad: entero positivo (mayor a 0).

- precioUnitario: positivo, mayor a 0.

- La lista no puede venir vacía → 400 con mensaje claro.

Si alguno de los elementos de la lista no cumple con las validaciones, el endpoint debe responder con HTTP 400 (Bad Request) e informar claramente:

- la posición del elemento que contiene el error;

- el campo que no es válido;

- el motivo del error.

Para manejar estos errores se deberá utilizar un @ExceptionHandler para MethodArgumentNotValidException.


## Endpoint 2: POST /api/ventas/aplicar-descuento

## Se recibe:

- la misma lista de VentaDTO mediante @RequestBody;

- un porcentaje de descuento mediante @RequestParam.

Para cada venta se debe calcular su monto con descuento y devolverlo como un campo adicional llamado montoConDescuento.

Además, se debe calcular el totalConDescuento, que corresponde a la suma de los montos de todas las ventas después de aplicar el descuento.

La respuesta debe incluir la lista de ventas con el nuevo campo montoConDescuento y el totalConDescuento general.

## Validación del porcentaje

El parámetro porcentaje debe ser un valor entre 0 y 100, inclusive.

- 0: no se aplica descuento.

- 10: se aplica un descuento del 10

- 100: se aplica un descuento del 100

- Un valor menor que 0 o mayor que 100: se debe devolver HTTP 400 (Bad Request) con un mensaje claro indicando que el porcentaje no es válido.


## 2 Ejercicio 2 – Gestión y búsqueda de productos en memoria

Módulo: Catálogo de productos En este ejercicio se construirá un pequeño catálogo de productos que se mantendrá en memoria, sin utilizar una base de datos. La aplicación deberá tener una colección List<Producto> dentro de un @Service. Al iniciar la aplicación, la colección deberá cargarse con al menos 8 productos de ejemplo.

Cada producto tendrá, como mínimo, los siguientes datos:

- id

- nombre

- categoria

- precio

- stock

El objetivo es implementar diferentes operaciones sobre esta colección: consultar productos, buscar y filtrar, ordenar, agregar, modificar y eliminar.

## GET /api/catalogo

Devuelve todos los productos disponibles en el catálogo.

## GET /api/catalogo/buscar?

Permite buscar productos utilizando los siguientes parámetros de consulta: /api/catalogo/buscar?categoria=Perifericos&precioMin=1000&precioMax=10000 Todos los parámetros son opcionales y pueden combinarse.

- categoria: filtra los productos que pertenecen a esa categoría.

- precioMin: devuelve productos cuyo precio sea mayor o igual a ese valor.

- precioMax: devuelve productos cuyo precio sea menor o igual a ese valor.

Si se envía más de un parámetro, se deben aplicar todos los filtros al mismo tiempo. Si no se envía ningún parámetro, se deben devolver todos los productos. Importante: el filtrado deberá realizarse utilizando Streams de Java, especialmente

filter().

## GET /api/catalogo/ordenar

Permite ordenar los productos del catálogo utilizando parámetros de consulta. Ejemplo: /api/catalogo/ordenar?criterio=precio&orden=desc

El parámetro criterio puede tomar los siguientes valores:

- precio

- nombre

El parámetro orden puede ser:

- asc: orden ascendente.

- desc: orden descendente.


Si no se envía orden, se debe utilizar asc como valor predeterminado.

Importante: para realizar el ordenamiento deberán utilizar Streams de Java y Comparator.

## POST /api/catalogo

Recibe un nuevo producto mediante @RequestBody. Ejemplo:

```
{
"nombre": "Teclado",
"categoria": "Perifericos",
"precio": 25000,
"stock": 10
}
```

El producto debe agregarse a la colección en memoria.

Validar que:

- nombre no esté vacío.

- precio sea mayor que 0.

- stock sea mayor o igual que 0.

Si los datos son correctos, se debe devolver HTTP 201 (Created).

## PUT /api/catalogo/{id}/stock?cantidad=5

Permite modificar el stock de un producto. El parámetro cantidad indica cuánto se debe modificar el stock actual:

- Un valor positivo aumenta el stock.

- Un valor negativo disminuye el stock.

El stock nunca puede quedar por debajo de 0.

Si la modificación provocaría un stock negativo, se debe devolver HTTP 400 (Bad Re- quest).

Si no existe un producto con el id indicado, se debe devolver HTTP 404 (Not Found).

## DELETE /api/catalogo/{id}

Elimina de la colección el producto que tenga el id indicado.

- Si el producto existe, se elimina correctamente.

- Si no existe, se debe devolver HTTP 404 (Not Found).


## 3 Ejercicio 3 — Consumo de una API externa

## Módulo: Conversor de divisas

En este ejercicio se incorporará una nueva funcionalidad a la aplicación: un conversor de

divisas.

A diferencia de los ejercicios anteriores, en este caso la aplicación deberá obtener información desde un servicio externo. Para ello se utilizará la API pública y gratuita Frankfurter, que permite consultar tasas de cambio entre diferentes monedas y no requiere una API key.

La API utiliza la siguiente URL:

https://api.frankfurter.app/latest?amount={monto}&from={origen}&to={destino} El endpoint recibirá los siguientes parámetros mediante @RequestParam: [URL 🔗](https://api.frankfurter.app/latest?amount={monto}&from={origen}&to={destino})

- monto: cantidad de dinero que se desea convertir.

- origen: código de la moneda de origen.

- destino: código de la moneda de destino.

Ejemplo: GET /api/divisas/convertir?monto=100&origen=USD&destino=ARS

## GET /api/divisas/convertir?monto=100&origen=USD&destino=ARS

## 1. Validar los datos recibidos.

- monto debe ser mayor que 0.

- origen debe ser un código de moneda de 3 letras.

- destino debe ser un código de moneda de 3 letras.

Se pueden utilizar códigos ISO como:

USD

ARS

EUR

BRL

Si algún dato no es válido, se debe devolver HTTP 400 (Bad Request) con un mensaje claro.

## 2. Consultar la API externa.

La aplicación deberá realizar una petición HTTP a Frankfurter utilizando los datos recibidos.

Para realizar la llamada se deberá utilizar

utilizarse RestTemplate si así se indica en clase.

- 3. Procesar la respuesta.

La respuesta recibida desde Frankfurter contiene información que no necesariamente debe ser enviada al cliente.

La aplicación deberá parsear la respuesta y construir su propia respuesta, devolviendo solamente los datos relevantes para nuestra aplicación.

La respuesta deberá tener una estructura similar a:

RestClient

de Spring. También puede


```
{
"montoOriginal": 100,
"monedaOrigen": "USD",
"monedaDestino": "ARS",
"tasaCambio": 1234.56,
"montoConvertido": 123456.0,
"fecha": "2026-09-02"
}
```

## Manejo de errores

La aplicación debe contemplar posibles problemas al comunicarse con el servicio externo. Por ejemplo:

- Frankfurter no responde.

- Se produce un timeout.

- La API externa devuelve un error HTTP.

- Se solicita una moneda que no existe.

Estos errores deben ser capturados y tratados por la API. Según el tipo de error, se deberá devolver una respuesta adecuada, por ejemplo:

- HTTP 400 (Bad Request) cuando los datos enviados por el cliente sean incorrectos.

- HTTP 502 (Bad Gateway) cuando la API no pueda obtener correctamente la infor- mación del servicio externo.

La respuesta debe incluir un mensaje claro que permita entender qué ocurrió.

## Consideraciones técnicas

La llamada a Frankfurter puede realizarse utilizando, por ejemplo:

RestClient.builder().build() o mediante un RestClient configurado como bean e inyectado en el servicio.

El procesamiento de la llamada deberá realizarse preferentemente en una capa de Service, manteniendo en el Controller la responsabilidad de recibir los parámetros y devolver la respuesta HTTP.


## 4 Ejercicio 4 —Alta de clientes

## Módulo: Conversor de clientes

En este ejercicio se implementará un endpoint REST para registrar clientes en una base de datos MySQL, utilizando la tabla clientes incluida en el script SQL provisto.

Se trabajará con un ClienteDTO para recibir los datos enviados en la petición.

El endpoint recibirá un objeto con la siguiente estructura:

```
{
"nombre": "Ana",
"apellido": "Garcia",
"email": "ana.garcia@mail.com",
"telefono": "3814567890"
}
```

## Endpoint 1 Alta simple: POST /api/clientes

Implementar un endpoint que:

- 1. Reciba un ClienteDTO.

- 2. Inserte un nuevo cliente en la tabla clientes.

- 3. Permita que la base de datos genere el id.

- 4. Devuelva una respuesta HTTP 201 Created.

- 5. La respuesta debe incluir los datos del cliente creado, incluyendo el id generado.

## Endpoint 2 Alta con validación: POST /api/clientes/validado

Implementar un segundo endpoint que realice el mismo alta, pero incorporando valida- ciones mediante Bean Validation.

## Tabla: Reglas de validación del ClienteDTO

| Campo Regla de validación |   |   |
| --- | --- | --- |
| nombre | No vacío y mínimo 2 caracteres | (@NotBlank, |
|   | @Size(min=2)). |   |
| apellido | No vacío y mínimo 2 caracteres | (@NotBlank, |
|   | @Size(min=2)). |   |
| email | Obligatorio y con formato de email válido |   |
|   | (@NotBlank, @Email). |   |
| email | No debe estar registrado previamente en la base |   |
|   | de datos. |   |
| telefono | Opcional. Si se informa, debe contener única- |   |
|   | mente dígitos (@Pattern). |   |

- La comprobación de que el email ya existe no se realiza mediante Bean Validation. Deberá consultarse la base de datos antes de realizar el INSERT.

- Si el email ya está registrado, el endpoint debe devolver: HTTP 400 Bad Request

```
{
"status": 400,
```


```
"message": "El email ya est registrado",
"data": null
}
```

- Cuando uno o más campos no cumplan las reglas de validación, el endpoint debe devolver: HTTP 400 Bad Request. La respuesta deberá indicar qué campos son inválidos y cuál es el problema, agrupando los errores dentro de data. Ejemplo:

```
{
"status": 400,
"message": "Error de validacin",
"data": {
"nombre": "debe tener al menos 2 caracteres",
"email": "debe ser un email vlido",
"telefono": "solo debe contener dgitos"
}
}
```

- Si existen errores en varios campos, todos ellos deben informarse en la misma re- spuesta.


## 5 Ejercicio 5 — Consulta de pedidos con filtros combinables

Implementar un endpoint REST que permita consultar el historial de pedidos aplicando distintos filtros opcionales.

Para resolver la consulta se utilizarán las tablas:

- pedidos

- detalle_pedidos

- productos

- categorias

- clientes

## Módulo: Historial de pedidos

## GET /api/pedidos/buscar

El endpoint debe aceptar los siguientes parámetros como query_params. Todos son opcionales y pueden utilizarse de manera individual o combinada.

*Tabla: Parámetros de consulta*

| Parámetro Descripción |   |
| --- | --- |
| clienteId | Devuelve los pedidos realizados por el cliente indicado. |
| categoria | Devuelve los pedidos que incluyan al menos un pro- |
|   | ducto perteneciente a la categoría indicada. |
| fechaDesde | Fecha inicial del período a consultar. Formato |
|   | yyyy-MM-dd. |
| fechaHasta | Fecha final del período a consultar. Formato |
|   | yyyy-MM-dd. |
| estado | Filtra por estado del pedido. Valores posibles: |
|   | PENDIENTE, ENVIADO, ENTREGADO o CANCELADO. |

- Cuando se informen varios parámetros, todos los filtros deben cumplirse simultáneamente (AND). Por ejemplo: /api/pedidos/buscar?clienteId=5&categoria=Perifericos&estado=ENTREGADO. Debe devolver únicamente los pedidos del cliente 5 que: GET

- tengan estado ENTREGADO, y

- incluyan al menos un producto de la categoría Perifericos.

## Datos de respuesta

Por cada pedido que cumpla con los filtros indicados, se deberá devolver la información del pedido, su cliente y sus productos. Ejemplo:

```
{
"pedidoId": 12,
"cliente": "Ana Garcia",
"fecha": "2026-08-15",
"estado": "ENTREGADO",
"totalPedido": 25400.0,
"productos": [
{
"nombre": "Mouse inalambrico",
"categoria": "Perifericos",
```


"cantidad":

2,

"subtotal":

9000.0

}

]

}

La respuesta debe contener una colección de pedidos.

## Requisitos técnicos

- 1. La consulta debe permitir relacionar la información de las cinco tablas necesarias para construir la respuesta.

- 2. La consulta deberá realizarse utilizando Spring Data JPA mediante un método del PedidoRepository y una consulta @Query. La consulta deberá permitir aplicar los filtros opcionales y recuperar la información necesaria de los pedidos y sus entidades relacionadas (Cliente, DetallePe- dido, Producto y Categoria).

- 3. Si no se proporciona ningún parámetro, el endpoint debe devolver todos los pedidos, in- cluyendo sus detalles.

- 4. Si se proporciona un solo parámetro, se aplica únicamente ese filtro.

- 5. Si se proporcionan varios parámetros, se deben aplicar todos simultáneamente.

- 6. Si ningún pedido cumple con los filtros especificados, la operación no se considera un error. Se debe devolver: HTTP 200 OK, con una colección vacía:

```
{
"status": 200,
"message": "Consulta realizada correctamente",
"data": []
}
```

A contiuación, se muestra el DER que relaciona las tablas involucradas en el ejercicio.


## Diagrama Entidad - Relacién (DER)

Script con la estructura de las tablas

```
CREATE TABLE categorias (
id INT PRIMARY KEY AUTO_INCREMENT,
nombre VARCHAR(100) NOT NULL,
descripcion VARCHAR(255)
);
CREATE TABLE productos (
id INT PRIMARY KEY AUTO_INCREMENT,
nombre VARCHAR(100) NOT NULL,
descripcion VARCHAR(255),
precio DECIMAL(10,2) NOT NULL,
stock INT NOT NULL,
categoria_id INT,
CONSTRAINT fk_productos_categoria
FOREIGN KEY (categoria_id)
REFERENCES categorias(id)
);
CREATE TABLE clientes (
id INT PRIMARY KEY AUTO_INCREMENT,
nombre VARCHAR(100) NOT NULL,
apellido VARCHAR(100) NOT NULL,
email VARCHAR(150) NOT NULL UNIQUE,
telefono VARCHAR(30),
fecha_registro DATETIME
);
CREATE TABLE pedidos (
id INT PRIMARY KEY AUTO_INCREMENT,
cliente_id INT NOT NULL,
```


```
fecha_pedido DATE NOT NULL,
estado VARCHAR(50) NOT NULL,
CONSTRAINT fk_pedidos_cliente
FOREIGN KEY (cliente_id)
REFERENCES clientes(id)
);
CREATE TABLE detalle_pedidos (
id INT PRIMARY KEY AUTO_INCREMENT,
pedido_id INT NOT NULL,
producto_id INT NOT NULL,
cantidad INT NOT NULL,
precio_unitario DECIMAL(10,2) NOT NULL,
CONSTRAINT fk_detalle_pedido
FOREIGN KEY (pedido_id)
REFERENCES pedidos(id),
CONSTRAINT fk_detalle_producto
FOREIGN KEY (producto_id)
REFERENCES productos(id)
);
```


## 6 Ejercicio 6 — API externa + persistencia

## Módulo: Historial de cotizaciones

En este ejercicio se amplía el funcionamiento desarrollado en el Ejercicio 3. La aplicación deberá consultar la API externa Frankfurter y guardar cada consulta en la base de datos.

## POST /api/divisas/consultar?origen=USD&destino=ARS&monto=100

Al recibir una solicitud, se deberá:

- 1. Consultar la cotización actual utilizando la API de Frankfurter, de la misma manera que en el Ejercicio 3.

- 2. Calcular el monto convertido utilizando la tasa de cambio obtenida.

- 3. Guardar la consulta en la base de datos, en la tabla historial_conversiones, regis- trando :

- moneda de origen

- moneda de destino

- monto solicitado

- monto convertido

- tasa de cambio

- fecha y hora de la consulta

- 4. Devolver al cliente la información de la consulta actual

## GET /api/divisas/historial?origen=USD&destino=ARS

## Al recibir una solicitud se deberá:

- 1. Recuperar de la base de datos todas las consultas correspondientes al par de monedas indicado;

- 2. Ordenarlas por fecha y hora de consulta, de la más reciente a la más antigua;

- 3. Devolver la información necesaria para que el cliente pueda utilizarla, por ejemplo, para construir un gráfico de evolución de la cotización.

## Ejemplo:

```
[
{
"fecha": "2026-09-03T15:30:00",
"tasaCambio": 1234.56
},
{
"fecha": "2026-09-02T14:10:00",
"tasaCambio": 1206.28
},
{
"fecha": "2026-09-01T10:25:00",
"tasaCambio": 1198.50
}
]
```


Creación de la tabla a utilizar en este ejercicio:

```
HISTORIAL\_CONVERSIONES {
int id PK
varchar moneda_origen
varchar moneda_destino
decimal monto
decimal monto_convertido
decimal tasa
datetime fecha_consulta
}
```


## Criterios de corrección

- Todos los endpoints responden correctamente desde Swagger UI.

- Toda respuesta respeta el formato {status, messege, data}, incluidos los errores.

- Validaciones implementadas con Bean Validation donde corresponde.

- Manejo de errores centralizado (no try/catch repetido en cada controller).

- Documentación Swagger completa (operaciones, parámetros, posibles respuestas).
