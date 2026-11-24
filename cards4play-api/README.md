# Cards4Play – API REST

Backend de Cards4Play (Java 17 + Spark + Gson, persistencia en JSON, Docker).

## Integrantes
- Valentina Castañeda
- Samuel Rojas

## Construir y ejecutar con Docker
```bash
mvn clean package                         # o Maven > Lifecycle > clean, package en IntelliJ
docker build -t cards4play-api .
docker volume create datos_cards4play
docker run -d -p 4567:4567 -v datos_cards4play:/app/data -e TASA_DEFECTO=4000 --name cards4play cards4play-api
```

## Autenticación
Basic Auth en todas las rutas (excepto `/health`). 
Administrador por defecto: `admin` / `admin123`.

## Endpoints
| Método | Ruta | Rol | Códigos |
|---|---|---|---|
| GET | /health | público | 200 |
| POST | /clientes | ADMIN | 201, 400, 401, 403, 409 |
| GET | /clientes, /clientes/:id | ADMIN | 200, 401, 403, 404 |
| GET | /productos, /productos/:id | cualquiera | 200, 401, 404 |
| POST | /productos | ADMIN | 201, 400, 401, 403 |
| PATCH | /productos/:id | ADMIN | 200, 400, 401, 403, 404 |
| POST | /boosters | ADMIN | 201, 400, 401, 403, 409 |
| GET | /boosters | cualquiera | 200 |
| GET | /boosters/mios | CLIENTE | 200, 403 |
| POST | /boosters/:id/abrir | CLIENTE | 200, 403, 404, 409 |
| POST | /torneos | ADMIN | 201, 400, 403 |
| GET | /torneos, /torneos/:id | cualquiera | 200, 404 |
| POST | /torneos/:id/inscripciones | CLIENTE | 201, 403, 404, 409, 410 |
| POST | /compras | CLIENTE | 201, 400, 403, 404, 409 |
| GET | /compras/historial | CLIENTE | 200, 403 |
| GET | /clientes/:id/compras | ADMIN | 200, 403, 404 |

## API externa
`https://open.er-api.com/v6/latest/USD` (timeout 3 s). Si falla se usa la tasa por defecto
(variable `TASA_DEFECTO`, valor por omisión **4000**) y las respuestas indican `"fuenteTasa": "DEFECTO"`.

## Resolución de conflictos de fusión
(documentar aquí el conflicto que resuelvan)_
