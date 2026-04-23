# Smart Campus Sensor & Room Management API

**Student:** Nafees Ahamed  
**Student ID:** w2120783  
**Module:** 5COSC022C.2 Client-Server Architectures

---

## 📖 Overview

The Smart Campus API is a RESTful web service that is built on top of JAX-RS and Jersey. It should be built to facilitate Smart Campus initiatives of the university whereby facilities managers and automated systems can manage physical location (Rooms), hardware (Sensors) and history of telemetry (Sensor Readings).

It is deployed on Apache Tomcat with the context path /smart-campus-api and all the REST endpoints are served on /api/v1 as specified by @ApplicationPath("/api/v1") in RestConfiguration.java.

### Key Architectural Features

- **Resource-Oriented Design:** A well-defined resource hierarchy is defined with regard to actual campus objects  — `Room`, `Sensor`, and `Reading`.
- **Single Application Path:** There is no sub-path covering the endpoints, with all endpoints being exposed as `/api/v1` 
- **In-Memory Data Store:** Relies on thread-safe singleton `DataService` on the basis of `CopyOnWriteArrayList` instead of a database, as required by the coursework.
- **Sub-Resource Locator Pattern:** `SensorResource` forwards `/sensors/{sensorId}/readings` to a special `ReadingResource` class.
- **Advanced Error Handling:** `GenericExceptionMapper` handles all uncontrolled exceptions and responds to them with clean JSON errors  — `409 Conflict`, `422 Unprocessable Entity`, `403 Forbidden`, `404 Not Found`, and `500 Internal Server Error` — without showing stack traces.
- **HATEOAS Discovery:** The root `GET /api` endpoint will provide navigational links to the  `/api/rooms` and `/api/sensors`.

### Models

| Model | Fields |
|-------|--------|
| `Room` | `id` (String), `name` (String), `capacity` (int) |
| `Sensor` | `id` (String), `type` (String), `roomId` (String), `status` (String), `currentValue` (double) |
| `Reading` | `id` (int — auto-assigned), `sensorId` (String), `value` (double), `timestamp` (String) |

### API Endpoints

```
GET    /api/v1                              → Discovery (links to rooms & sensors)
GET    /api/v1/rooms                        → List all rooms
POST   /api/v1/rooms                        → Create a new room
GET    /api/v1/rooms/{roomId}               → Get a room by ID
DELETE /api/v1/rooms/{roomId}               → Delete a room (is blocked on sensor connection)
GET    /api/v1/sensors                      → List all sensors (can be filtered by ?type)
POST   /api/v1/sensors                      → Register a new sensor
GET    /api/v1/sensors/{sensorId}           → Get a sensor by ID
GET    /api/v1/sensors/{sensorId}/readings  → Get all readings for a sensor
POST   /api/v1/sensors/{sensorId}/readings  → Add a new reading (blocked if MAINTENANCE)
GET    /api/v1/debug/error                  → Trigger a 500 error (testing only)
```

---

## 📁 Project Structure

```
smart-campus-api/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/smartcampus/
        │       ├── config/
        │       │   └── RestConfiguration.java        ← @ApplicationPath("/api")
        │       ├── exceptions/
        │       │   └── GenericExceptionMapper.java   ← Global error handler
        │       ├── models/
        │       │   ├── Room.java
        │       │   ├── Sensor.java
        │       │   └── Reading.java
        │       ├── resources/
        │       │   ├── DiscoveryResource.java        ← GET /api/v1
        │       │   ├── RoomResource.java             ← /api/v1/rooms
        │       │   ├── SensorResource.java           ← /api/v1/sensors
        │       │   ├── ReadingResource.java          ← /api/v1/sensors/{id}/readings
        │       │   └── DebugResource.java            ← /api/v1/debug/error
        │       └── service/
        │           └── DataService.java              ← Singleton in-memory store
        └── webapp/
            ├── META-INF/
            │   └── context.xml                       ← Context path: /smart-campus-api
            ├── WEB-INF/
            │   ├── beans.xml
            │   └── web.xml
            └── index.html
```

---

## 🛠 Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 8 | Core language |
| JAX-RS | 2.x (javax) | REST specification |
| Jersey | 2.34 | JAX-RS implementation |
| Apache Tomcat | 9.x | Servlet container |
| Maven | 3.x | Build and dependency management |
| JAXB | 2.3.1 | JSON/XML binding |
| Apache NetBeans | — | Recommended IDE |
| Postman | — | API testing |

---

## 🚀 Instructions to Build and Launch.

### Prerequisites

- JDK 8 or higher
- Apache Maven 3.x
- Apache Tomcat 9.x
- Apache NetBeans (recommended)

### Option 1: Run with NetBeans

1. Open the project in Apache NetBeans.
2. Ensure that Apache Tomcat is setup as the server.
3. Right-click the project and choose **Clean and Build**.
4. Right-click the project and choose **Run**.
5. The context path is set to `/smart-campus-api` in `context.xml`. Open the discovery endpoint:

```
http://localhost:8080/smart-campus-api/api
```

### Option 2: Build with Maven and deploy manually

1. Open a terminal in the root of the project.
2. Run:

```bash
mvn clean install
```

3. This generates `smart-campus-api-1.0-SNAPSHOT.war` in the `target/` directory.
4. Move the copy of the `.war` file into the `webapps/` folder of Apache Tomcat.
5. Start Tomcat and open:

```
http://localhost:8080/smart-campus-api/api/v1
```

> **Note:** The context path is set to `/smart-campus-api` is defined in `src/main/webapp/META-INF/context.xml`. When deploying on a another machine, make sure that the context configuration or the name of the  `.war` file is the same.

---

## 📡 Sample cURL Commands

### 1. Discover API endpoint

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1
```

**Expected response:**
```json
{
  "rooms": "/api/rooms",
  "sensors": "/api/sensors"
}
```

---

### 2. Get all rooms

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/rooms
```

---

### 3. Create or Add  a new room

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"ENG-101\",\"name\":\"Engineering Lab\",\"capacity\":40}"
```

**Expected:** `201 Created` with the created room in the response body.
Sending the same request again returns `409 Conflict`.

---

### 4. Get a room by ID

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/rooms/ENG-101
```

---

### 5. Register a valid sensor

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400.0,\"roomId\":\"ENG-101\"}"
```

**Expected:** `201 Created`. The `roomId` must reference an existing room.

---

### 6. Register an invalid sensor (missing room reference)

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-999\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":410.0,\"roomId\":\"NO-ROOM\"}"
```

**Expected:** `422 Unprocessable Entity` — room `NO-ROOM` does not exist.

---

### 7. Select type of filter sensors

```bash
curl -i "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

---

### 8. Retrieve a sensor by ID

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001
```

---

### 9. Add or Create a new reading to a sensor

```bash
curl -i -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":421.7}"
```

**Expected:** `201 Created`. Gives the reading with an auto-assigned id `id`.  If the sensor status is `MAINTENANCE`, returns `403 Forbidden`.

---

### 10. Take reading history for a sensor

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings
```

---

### 11. start a room deletion conflict

```bash
curl -i -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/ENG-101
```

**Expected:** `409 Conflict` when sensors are still attached to the room. `204 No Content` in case deletion is successful

---

### 12. Trigger the global 500 error mapper

```bash
curl -i http://localhost:8080/smart-campus-api/api/v1/debug/error
```

**Expected:** `500 Internal Server Error` with a clean JSON response — no stack trace exposed.

---

## 📝 Conceptual Report Answers

### Chapter 1: Setup & Discovery

**1. ⁠describe the default lifecycle of a JAX-RS Resource class. Does the runtime create a new instance with each incoming request or is it considered a singleton? Discuss the implications of this architectural choice to your data management and synchronization of your in-memory data structures to avoid data loss or race conditions.**

JAX-RS resource classes are by default request-scoped. It says that every incoming HTTP request is processed by a new object of the resource class, instantiated by the Jersey runtime and f the rooms, sensors and readings.

This directly affects the architecture: the instance-level variables can not be relied upon to store shared state since they are destroyed during every request. To address this, another `DataService` class was introduced with a Singleton pattern - one single instance will be created and will be shared in all the requests. It also contains three  `CopyOnWriteArrayList` collections for rooms, sensors, and readings.

was to be thread safe. Since a single collection can be accessed by multiple concurrent requests trying to read or write to the same collection at the same time, an ordinary  `ArrayList` would lead to race conditions and data corruption `CopyOnWriteArrayList`,uses writes to make a new copy of the underlying array, which ensures a consistent snapshot is always visible to concurrent reads. The `addReading()` method is also `synchronized` so that it can safely handle the  `currentReadingId` counter which is auto-incrementing.

---

**2. What is the significance of the Hypermedia (links and navigation between responses) provision being a characteristic of high-level RESTful design (HATEOAS)? What benefits does this methodology have over static documentation to client developers?**

The Hypermedia as the Engine of Application State (HATEOAS) implies that an API response includes links that instruct the client on what to do next or what is available. This enables the clients to find the API dynamically without the need to have hard-coded URLs or external documentation. It minimizes client-server coupling - in case of endpoint paths changing, only the server needs to be updated; clients that use links instead of hard-coded paths are automatically adjusted.


In this project, the `DiscoveryResource` at `GET /api/v1` is a JSON map that has links to `/api/v1/rooms` and `/api/v1/sensors`. Here we can see the HATEOAS principle simplified: hitting the root endpoint, a client will understand the next place to go without any prior knowledge of the API structure.

---

### Chapter 2: Room Management

**3. In returning a list of rooms, what is the implication of returning only the IDs or the entire room objects? Take network bandwidth and client-side processing into consideration.**

Sending only room IDs also cuts down on payload sizes and decreases serialization costs on the server which is advantageous when dealing with very large collections. It however compels the client to make a separate `GET /api/v1/rooms/{id}` request to the server per room that it requires information about, and complicates the client code by doubling the number of network round trips.

To send full `Room` objects (as this API does) makes the payload size larger, but provides the client with all it requires in one response. Since this is a campus management system where the number of rooms to be serviced is relatively small, returning full objects is more feasible and user friendly to clients. It makes the API minimal and minimizes chatting between clients and the server.

---

**4. Does your implementation have an idempent DELETE operation? Give a thorough explanation by stating what will occur should a client send the same DELETE request to a room with the exact same parameters more than once.**

Yes, in this implementation the DELETE operation is idempotent. When calling `DELETE /api/v1/rooms/ENG-101`, the first time, the system verifies the presence of any sensors attached to a room. In case of no sensors being connected, the room is deleted and `204 No Content` sent. When sensors are connected, `409 Conflict` is returned and nothing is deleted.

On a second request with the same DELETE request, a successful deletion will have been made and the `removeIf` call will fail to find a matching room and the list will not change again `204 No Content` will again be returned. State of the server (the room is not there) is the same, despite the number of times the request is repeated after the initial successful deletion. This meets the definition of idempotency: making multiple requests makes no difference, except in terms of side effects.

---

### Chapter 3: Sensors & Filtering

**5. ⁠POST endpoints consume with Mediatype.APPLICATION_JSON). State what would happen technically in case a client tries to send data in alternative format, e.g. text/plain or application/xml. What does JAX-RS do about this mismatch?**

The `@Consumes(MediaType.APPLICATION_JSON)` annotation states that the resource method will accept request bodies only with `Content-Type` of `application/json`. When a client makes a request of `Content-Type: text/plain` or `Content-Type: application/xml`, the Jersey runtime intercepts the request prior to being sent to the resource method, and will automatically send `415 Unsupported Media Type`.

This is enforced at the framework level - not even a single application code is executed. It shields the API against bad payloads that the JSON deserializer (JerseyJSON-B binding) could not serve, avoiding internal parsing failures and maintaining the API contract well defined.


---

**6. You applied sensor filtering by `@QueryParam("type")`. Compare to a different design where the type is included in the URL path (e.g. /api/v1/sensors/type/CO2). Why is query parameter approach usually regarded as the best in terms of filtering and searching collections?**

In the existing implementation, the query parameter is used to filter the collection of sensors with the query `GET /api/v1/sensors?type=CO2`. It is the desired method since the `?type=CO2` is an optional restriction on the same resource - the group of sensors - and not a new, separate resource. The endpoint `/api/v1/sensors` still serves the purpose of representing all sensors and the query parameter only filters the result set.

The design to embed the filter into the path as `/api/v1/sensors/type/CO2` means that `type/CO2` is a unique resource, which is semantically incorrect. It also complicates the API to expand - adding a second filter (such as status) would involve a deeply nested path, but query parameters build intuitively: `?type=CO2&status=ACTIVE`. Query parameters are also compatible with common REST conventions of filtering, sorting and pagination.

---

### Chapter 4: Sub-Resources

**7. Talk about how the Sub-Resource Locator pattern can help with architecture. How does separating logic into different classes make things less complicated than putting all the nested paths in one big controller?**

In this project, `SensorResource` uses a Sub-Resource Locator at `@Path("/{sensorId}/readings")` hat gives back a new ReadingResource instance instead of doing the logic itself:

```java
@Path("/{sensorId}/readings")
public ReadingResource getReadingResource() {
    return new ReadingResource();
}
```

Jersey then sends all `GET` and `POST` requests under that path to `ReadingResource`, which is in charge of getting and making readings. This separation means that each class has one clear job: `SensorResource` handles sensors, `ReadingResource` handles readings. Neither class needs to know how the other one works inside.

This pattern stops resource classes in bigger APIs from turning into huge, monolithic controllers. You can develop, test, and improve each sub-resource on its own. It also naturally follows the REST resource hierarchy: readings belong to sensors, and this is shown in both the URL and code structures.

---

### Chapter 5: How to Handle Errors and Keep Logs

**8. Why is HTTP 422 often considered more semantically accurate than 404 when the issue is a missing reference inside a valid JSON payload?**

If you get an `HTTP 404 Not Found` message, it means that the URL resource you asked for doesn't exist. `HTTP 422 Unprocessable Entity` means that the server got the request, understood it, and it was syntactically correct, but it can't process it because there is a semantic error in the payload content.

`HTTP 404 Not Found`means that the URL resource you asked for doesn't exist.`HTTP 422 Unprocessable Entity`means that the server got the request, understood it, and it was written correctly, but it can't process it because there is a semantic error in the payload content.

---

**9. From a cybersecurity point of view, what are the dangers of letting external API users see internal Java stack traces?**

Raw Java stack traces show a lot of sensitive internal information, like fully qualified class names and package structure (which shows the application's architecture), method signatures and call chains (which show the flow of business logic), framework and library versions (which can be compared to known CVEs), and the exact line number where a failure happened.


In this project all unhandled Throwables are caught by the GenericExceptionMapper which is annotated with `@Provider`.It returns only a sanitized JSON message — `{"error": "A server error occurred." Please try again later.It prints the stack trace server-side via exception.printStackTrace() }` or developer debugging. Sorry, please try again later. — To the client This is a good compromise between being debuggable internally and secure externally.

---

**10. What is the value of using JAX-RS filters to handle cross cutting concerns such as logging, rather than inserting Logger.info() statements into each resource method?**

Cross-cutting concerns are behaviors that can be uniformly applied to many parts of an application regardless of the particular business logic involved — logging, authentication, and CORS headers are classic examples. However, using `Logger.info()` calls in each resource method violates the DRY (Don't Repeat Yourself) principle where the same logging logic has to be repeated in each method, which is a maintenance overhead and easy to forget for new endpoints.

JAX-RS `ContainerRequestFilter` and `ContainerResponseFilter` interfaces allow this logic to be implemented once in a dedicated class annotated with `@Provider`. Jersey automatically applies the filter to every request and response in the application. Resource methods remain focused purely on business logic — receiving input, querying `DataService`, and building responses — with no logging boilerplate. If the logging format needs to change, it is updated in one place rather than across dozens of methods.

---

## ✅ Video Demonstration Checklist

The video demonstrates:

- [x] `GET /api/v1` — discovery endpoint returning links to rooms and sensors
- [x] `GET /api/v1/rooms` — list all rooms
- [x] `POST /api/v1/rooms` — create a new room (`201 Created`)
- [x] `GET /api/v1/rooms/{id}` — retrieve a room by ID
- [x] `POST /api/v1/rooms/` duplicate — triggers `409 Conflict`
- [x] Valid `POST /api/v1/sensors` with existing `roomId` — `201 Created`
- [x] Invalid `POST /api/v1/sensors` with non-existent `roomId` — `422 Unprocessable Entity`
- [x] `GET /api/v1/sensors?type=CO2` — filtered sensor list
- [x] `POST /api/v1/sensors/{id}/readings` — add reading (`201 Created`)
- [x] `POST /api/v1/sensors/{id}/readings` on MAINTENANCE sensor — `403 Forbidden`
- [x] `GET /api/v1/sensors/{id}/readings` — reading history
- [x] `DELETE /api/v1/rooms/{id}` with linked sensors — `409 Conflict`
- [x] `GET /api/v1/debug/error` — clean `500 Internal Server Error` with no stack trace

---

## 🔗 GitHub Repository

https://github.com/Ahamed2006

---

## Notes

- This project uses only JAX-RS (Jersey 2.34), as required by the coursework.
- No database was used. All data is stored in memory using `CopyOnWriteArrayList` inside a singleton `DataService`.
- The API context path `/smart-campus-api/v1` is configured in `src/main/webapp/META-INF/context.xml`.
- Java source and target compatibility is set to Java 8 in `pom.xml`.
- The API was tested using Postman.
