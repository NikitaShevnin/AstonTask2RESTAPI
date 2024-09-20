package ru.rest.controller;

import com.sun.net.httpserver.HttpExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.rest.entity.User;
import ru.rest.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TestUserController {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private HttpExchange exchange;
    private ByteArrayOutputStream outputStream;

    @Before
    public void setUp() {
        // Инициализация мок-объекта HttpExchange и его потока
        exchange = mock(HttpExchange.class);
        outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
    }

    @Test
    public void testGetUsers() throws IOException {
        // Подготовка тестовых данных
        List<User> mockUsers = Arrays.asList(
                new User(1, "John Doe", "john.doe@example.com"),
                new User(2, "Jane Smith", "jane.smith@example.com")
        );

        // Настройка мока
        when(userService.getUsersFromDatabase()).thenReturn(mockUsers);

        // Вызов метода
        userController.getUsers(exchange);

        // Проверка ответа
        String expectedResponse = "[{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}," +
                "{\"id\":2,\"name\":\"Jane Smith\",\"email\":\"jane.smith@example.com\"}]";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetUserById_UserFound() throws IOException, URISyntaxException {
        // Подготовка тестовых данных
        User mockUser = new User(1, "John Doe", "john.doe@example.com");
        when(exchange.getRequestURI()).thenReturn(new URI("/users/1"));
        when(userService.getUserById(1)).thenReturn(mockUser);

        // Вызов метода
        userController.getUserById(exchange);

        // Проверка ответа
        String expectedResponse = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testGetUserById_UserNotFound() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/users/99"));
        when(userService.getUserById(99)).thenReturn(null);

        userController.getUserById(exchange);

        // Проверка кода и сообщения
        assertEquals(404, exchange.getResponseCode());
        assertEquals("User not found", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateUser_Success() throws IOException {
        // Пример JSON пользователя
        String jsonUser = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        InputStream inputStream = new ByteArrayInputStream(jsonUser.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        User newUser = new User(1, "John Doe", "john.doe@example.com");
        when(userService.insertUserIntoDatabase(newUser)).thenReturn(1);

        userController.createUser(exchange);

        // Проверка ответа
        String expectedResponse = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCreateUser_InvalidData() throws IOException {
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("invalid data".getBytes()));

        userController.createUser(exchange);

        // Проверка кода и сообщения
        assertEquals(400, exchange.getResponseCode());
        assertEquals("Invalid user data", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testUpdateUser_Success() throws IOException, URISyntaxException {
        String jsonUser = "{\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}";
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(jsonUser.getBytes()));
        when(exchange.getRequestURI()).thenReturn(new URI("/users/1"));

        User updatedUser = new User(1, "Jane Doe", "jane.doe@example.com");
        when(userService.updateUserInDatabase(updatedUser)).thenReturn(true);

        userController.updateUser(exchange);

        // Проверка ответа
        String expectedResponse = "{\"id\":1,\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}";
        assertEquals(expectedResponse, outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testUpdateUser_UserNotFound() throws IOException, URISyntaxException {
        String jsonUser = "{\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}";
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(jsonUser.getBytes()));
        when(exchange.getRequestURI()).thenReturn(new URI("/users/1"));
        User updatedUser = new User(1, "Jane Doe", "jane.doe@example.com");
        when(userService.updateUserInDatabase(updatedUser)).thenReturn(false);

        userController.updateUser(exchange);

        // Проверка кода и сообщения
        assertEquals(404, exchange.getResponseCode());
        assertEquals("User not found", outputStream.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testDeleteUser_Success() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/users/1"));
        when(userService.deleteUserFromDatabase(1)).thenReturn(true);

        userController.deleteUser(exchange);

        // Проверка кода ответа
        verify(exchange).sendResponseHeaders(204, 0);
    }

    @Test
    public void testDeleteUser_NotFound() throws IOException, URISyntaxException {
        when(exchange.getRequestURI()).thenReturn(new URI("/users/1"));
        when(userService.deleteUserFromDatabase(1)).thenReturn(false);

        userController.deleteUser(exchange);

        // Проверка кода и сообщения
        assertEquals(404, exchange.getResponseCode());
        assertEquals("User not found", outputStream.toString(StandardCharsets.UTF_8));
    }
}