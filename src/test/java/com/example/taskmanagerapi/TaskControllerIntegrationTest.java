package com.example.taskmanagerapi;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String jwtToken;
    private Long taskId;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // 1. Register test user (ignore if already registered)
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"testuser\",\"password\":\"testpass\",\"email\":\"test@example.com\"}")
                .when()
                .post("/api/auth/register");

        // 2. Login to retrieve JWT token
        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"testuser\",\"password\":\"testpass\",\"email\":\"test@example.com\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        jwtToken = loginResponse.jsonPath().getString("token");

        // 3. Create initial setup task
        Response createResponse = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body("{\"title\":\"Setup Task\",\"description\":\"For testing\",\"status\":\"PENDING\"}")
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(200)
                .extract()
                .response();

        Long extractedId = createResponse.jsonPath().getLong("taskId");
        if (extractedId == null || extractedId == 0) {
            extractedId = createResponse.jsonPath().getLong("id");
        }

        this.taskId = extractedId;
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    public void testRegister_Success() {
        String uniqueUser = "user_" + System.currentTimeMillis();
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"" + uniqueUser + "\",\"password\":\"pass123\",\"email\":\"" + uniqueUser + "@example.com\"}")
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("username", equalTo(uniqueUser))
                .body("message", equalTo("User registered successfully"));
    }

    @Test
    public void testLogin_Success() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"testuser\",\"password\":\"testpass\",\"email\":\"test@example.com\"}")
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("message", equalTo("Login Successful"));
    }

    // ==================== CREATE TASK TESTS ====================

    @Test
    public void testCreateTask_WithValidToken() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body("{\"title\":\"Buy groceries\",\"description\":\"Milk, eggs\",\"status\":\"PENDING\"}")
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(200)
                .body("title", equalTo("Buy groceries"))
                .body("status", equalTo("PENDING"));
    }

    @Test
    public void testCreateTask_WithoutToken() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Test\",\"description\":\"Test\",\"status\":\"PENDING\"}")
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(500);
    }

    // ==================== GET ALL TASKS TESTS ====================

    @Test
    public void testGetAllTasks_WithValidToken() {
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetAllTasks_WithoutToken() {
        given()
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(500);
    }

    // ==================== GET TASK BY ID TESTS ====================

    @Test
    public void testGetTaskById_WithValidToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/" + taskId;
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .get(targetUrl)
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetTaskById_WithoutToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/1";
        given()
                .when()
                .get(targetUrl)
                .then()
                .statusCode(500);
    }

    // ==================== UPDATE TASK TESTS ====================

    @Test
    public void testUpdateTask_WithValidToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/" + taskId;
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .body("{\"title\":\"Updated Task\",\"description\":\"Updated desc\",\"status\":\"IN_PROGRESS\"}")
                .when()
                .put(targetUrl)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Task"))
                .body("status", equalTo("IN_PROGRESS"));
    }

    @Test
    public void testUpdateTask_WithoutToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/1";
        given()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"Test\",\"description\":\"Test\",\"status\":\"PENDING\"}")
                .when()
                .put(targetUrl)
                .then()
                .statusCode(500);
    }

    // ==================== DELETE TASK TESTS ====================

    @Test
    public void testDeleteTask_WithValidToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/" + taskId;
        given()
                .header("Authorization", "Bearer " + jwtToken)
                .when()
                .delete(targetUrl)
                .then()
                .statusCode(200);
    }

    @Test
    public void testDeleteTask_WithoutToken() {
        String targetUrl = RestAssured.baseURI + ":" + port + "/api/tasks/1";
        given()
                .when()
                .delete(targetUrl)
                .then()
                .statusCode(500);
    }
}