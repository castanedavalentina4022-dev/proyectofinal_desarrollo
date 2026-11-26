# Cards4Play – API REST

Backend de Cards4Play (Java 17 + Spark + Gson, persistencia en JSON, Docker).

## Integrantes
- Valentina Castañeda
- Samuel Rojas

## Construir y ejecutar con Docker
```bash
# el jar se compila dentro del Dockerfile (no hace falta correr Maven antes)
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
| DELETE | /productos/:id | ADMIN | 200, 401, 403, 404, 409 |
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

## PATCH y DELETE de productos
`PATCH /productos/:id` responde con un resumen de lo que cambió (solo aparecen los campos cuyo valor realmente cambió):
```json
{
  "mensaje": "Producto actualizado (2 campo(s) modificado(s))",
  "cambios": [
    { "campo": "precioUSD", "valorAnterior": 2.0, "valorNuevo": 3.5 },
    { "campo": "stock", "valorAnterior": 10, "valorNuevo": 25 }
  ],
  "producto": { "...": "producto ya actualizado" }
}
```
`DELETE /productos/:id` elimina el producto (200 con el producto eliminado). Un booster ya vendido no se puede eliminar (409).

## Despliegue en Railway
1. Subir el proyecto a GitHub (`pom.xml`, `Dockerfile`, `.gitignore` y `src` en la raíz del repositorio).
2. En railway.com: **New Project › Deploy from GitHub repo** y elegir el repositorio. Railway detecta el Dockerfile y compila solo.
3. En **Settings › Networking › Generate Domain** (si pide el puerto, usar el que aparece en los Deploy Logs).
4. Probar `https://TU-APP.up.railway.app/health`.
5. Opcional: variable `TASA_DEFECTO` en la pestaña Variables.

La API lee el puerto de la variable `PORT` (la define Railway); en local usa 4567.

## Colección de Postman
Está en `postman/Tienda_API.postman_collection.json`. Se importa en Postman (Import › seleccionar el archivo) y se ejecuta con *Run collection*.
La variable `baseUrl` apunta a `http://localhost:4567`; para probar la versión desplegada se cambia por la URL de Railway.

## API externa
`https://open.er-api.com/v6/latest/USD` (timeout 3 s). Si falla se usa la tasa por defecto
(variable `TASA_DEFECTO`, valor por omisión **4000**) y las respuestas indican `"fuenteTasa": "DEFECTO"`.

## Resolución de conflictos de fusión
No se pudo fusionar los cambios mediante git debido a un fallo propio del git en la cuenta de samuel, el cual no se pudo resolver.