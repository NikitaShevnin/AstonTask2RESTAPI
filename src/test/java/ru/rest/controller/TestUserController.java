package ru.rest.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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

@RunWith(PowerMockRunner.class)
@PrepareForTest(OrderController.class)
public class TestUserController {

    /**
     * Тестирует метод {@link UserController#getUsers(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно обрабатывает запрос на получение
     * списка всех пользователей. Метод создает мок-объект {@link HttpExchange},
     * который используется для отправки JSON-ответа с заранее заданным списком
     * пользователей. После вызова тестируемого метода проверяется, что ответ
     * был сформирован и отправлен корректно.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testGetUsers() throws IOException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Настройка ожидаемых данных согласно таблице
        List<User> mockUsers = Arrays.asList(
                new User("John Doe", "john.doe@example.com"),
                new User("Jane Smith", "jane.smith@example.com"),
                new User("Bob Johnson", "bob.johnson@example.com"),
                new User("Sarah Lee", "sarah.lee@example.com"),
                new User("Tom Williams", "tom.williams@example.com")
        );

        // Настройка mock для отправки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Мокаем статический метод getUsersFromDatabase
        PowerMockito.mockStatic(UserController.class);
        when(UserService.getUsersFromDatabase()).thenReturn(mockUsers);

        // Вызов метода getUsers
        UserController.getUsers(exchange);

        // Проверка, что ответ отправлен корректно
        String response = outputStream.toString(StandardCharsets.UTF_8);
        String expectedResponse = "["
                + "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"},"
                + "{\"id\":2,\"name\":\"Jane Smith\",\"email\":\"jane.smith@example.com\"},"
                + "{\"id\":3,\"name\":\"Bob Johnson\",\"email\":\"bob.johnson@example.com\"},"
                + "{\"id\":4,\"name\":\"Sarah Lee\",\"email\":\"sarah.lee@example.com\"},"
                + "{\"id\":5,\"name\":\"Tom Williams\",\"email\":\"tom.williams@example.com\"}"
                + "]";
        assertEquals(expectedResponse, response);
    }

    /**
     * Тестирует метод {@link UserController#getUserById(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно обрабатывает запрос на получение
     * пользователя по его идентификатору. Метод создает мок-объект {@link HttpExchange},
     * настраивает путь запроса для пользователя с ID 1, и возвращает
     * соответствующего пользователя. Проверяется, что ответ был сформирован и
     * отправлен корректно.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testGetUserById_UserFound() throws IOException, URISyntaxException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Создание пользователя
        User mockUser = new User("John Doe", "john.doe@example.com");

        // Настройка пути запроса
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/users/1"));

        // Мокаем статический метод UserService.getUserById
        PowerMockito.mockStatic(UserService.class);
        when(UserService.getUserById(1)).thenReturn(mockUser);

        // Настройка mock для отправки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода getUserById
        UserController.getUserById(exchange);

        // Проверка, что ответ отправлен корректно
        String response = outputStream.toString(StandardCharsets.UTF_8);
        String expectedResponse = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";
        assertEquals(expectedResponse, response);
    }

    /**
     * Тестирует метод {@link UserController#getUserById(HttpExchange)} на случай,
     * когда пользователь не найден.
     * <p>
     * Этот тест проверяет, что метод корректно отправляет ответ с кодом 404 и
     * сообщением "User not found", если пользователь с заданным идентификатором
     * не существует. Метод создает мок-объект {@link HttpExchange}, настраивает
     * путь запроса для несуществующего пользователя и мокаем метод, чтобы он
     * возвращал null. Проверяется, что ответ имеет статус 404 и содержит
     * соответствующее сообщение.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testGetUserById_UserNotFound() throws IOException, URISyntaxException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Настройка пути запроса
        when(exchange.getRequestURI()).thenReturn(new java.net.URI("/users/99"));

        // Мокаем статический метод UserService.getUserById
        PowerMockito.mockStatic(UserService.class);
        when(UserService.getUserById(99)).thenReturn(null);

        // Настройка mock для отправки ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода getUserById
        UserController.getUserById(exchange);

        // Проверка кода ответа и сообщения
        assertEquals(404, exchange.getResponseCode());
        String response = outputStream.toString(StandardCharsets.UTF_8);
        assertEquals("User not found", response);
    }

    /**
     * Тестирует метод {@link UserController#createUser(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно обрабатывает создание нового пользователя.
     * Метод создает мок-объект {@link HttpExchange}, который возвращает предварительно
     * заданные данные пользователя. После вызова тестируемого метода, проверяется,
     * что ответ содержит корректные данные о новом пользователе.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testCreateUser() throws IOException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Пример пользователя в JSON-формате
        String jsonUser = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        // Настройка возвращаемого потока данных для readUserFromRequest
        InputStream inputStream = new ByteArrayInputStream(jsonUser.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        // Мокаем другие необходимые методы
        User mockUser = new User("John Doe", "john.doe@example.com");
        when(UserController.readUserFromRequest(exchange)).thenReturn(mockUser);

        // Настройка возврата ID нового пользователя
        when(UserService.insertUserIntoDatabase(mockUser)).thenReturn(1);

        // Создаем ByteArrayOutputStream для перехвата ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода createUser
        UserController.createUser(exchange);

        // Проверка, что ответ содержит правильные данные о новом пользователе
        String response = outputStream.toString();
        assertEquals("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}", response);
    }

    /**
     * Тестирует метод {@link UserController#updateUser(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно обрабатывает обновление пользователя.
     * Метод создает мок-объект {@link HttpExchange}, который возвращает предварительно
     * заданные данные пользователя для обновления. После вызова тестируемого метода,
     * проверяется, что ответ содержит обновленные данные о пользователе.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testUpdateUser() throws IOException, URISyntaxException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Настройка идентификатора пользователя
        String userId = "1";
        when(exchange.getRequestURI()).thenReturn(new URI("/users/" + userId));

        // Пример обновленного пользователя в JSON-формате
        String jsonUser = "{\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}";

        // Настройка возвращаемого потока данных
        InputStream inputStream = new ByteArrayInputStream(jsonUser.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        // Мокаем другие необходимые методы
        User updatedUser = new User("Jane Doe", "jane.doe@example.com");
        when(UserController.readUserFromRequest(exchange)).thenReturn(updatedUser);

        // Настройка успешного обновления
        when(UserService.updateUserInDatabase(updatedUser)).thenReturn(true);

        // Создаем ByteArrayOutputStream для перехвата ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода updateUser
        UserController.updateUser(exchange);

        // Проверка, что ответ содержит правильные обновленные данные о пользователе
        String response = outputStream.toString();
        assertEquals("{\"id\":1,\"name\":\"Jane Doe\",\"email\":\"jane.doe@example.com\"}", response);
    }

    /**
     * Тестирует метод {@link UserController#deleteUser(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно обрабатывает запрос на удаление пользователя.
     * Метод создает мок-объект {@link HttpExchange}, который возвращает идентификатор пользователя.
     * После вызова тестируемого метода проверяем, что был отправлен правильный ответ HTTP.
     * </p>
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testDeleteUser() throws IOException, URISyntaxException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);
        String userId = "1";

        // Настройка URI для получения идентификатора пользователя
        when(exchange.getRequestURI()).thenReturn(new URI("/users/" + userId));

        // Мокаем успешное удаление пользователя
        when(UserService.deleteUserFromDatabase(Integer.parseInt(userId))).thenReturn(true);

        // Вызов метода deleteUser
        UserController.deleteUser(exchange);

        // Проверка, что был отправлен ответ 204 No Content
        verify(exchange).sendResponseHeaders(204, 0);
    }

    /**
     * Тестирует метод {@link UserController#deleteUser(HttpExchange)} на случай,
     * если пользователь не найден.
     *
     * @throws IOException если возникла ошибка при обработке HTTP-запроса
     */
    @Test
    public void testDeleteUserNotFound() throws IOException, URISyntaxException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);
        String userId = "1";

        // Настройка URI для получения идентификатора пользователя
        when(exchange.getRequestURI()).thenReturn(new URI("/users/" + userId));

        // Мокаем случай, когда пользователь не найден
        when(UserService.deleteUserFromDatabase(Integer.parseInt(userId))).thenReturn(false);

        // Создаем ByteArrayOutputStream для перехвата ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        // Вызов метода deleteUser
        UserController.deleteUser(exchange);

        // Проверка, что был отправлен ответ 404 Not Found
        verify(exchange).sendResponseHeaders(404, 0);
        String response = outputStream.toString();
        assertEquals("User not found", response);
    }

    /**
     * Тестирует метод {@link UserController#readUserFromRequest(HttpExchange)}.
     * <p>
     * Этот тест проверяет, что метод корректно десериализует объект пользователя
     * из тела HTTP-запроса. Метод создает мок-объект {@link HttpExchange},
     * который возвращает заранее заданный JSON, представляющий пользователя.
     * </p>
     *
     * @throws IOException если возникла ошибка при чтении данных из запроса
     */
    @Test
    public void testReadUserFromRequest() throws IOException {
        // Создание мок-объекта HttpExchange
        HttpExchange exchange = mock(HttpExchange.class);

        // Пример пользователя в JSON-формате
        String jsonUser = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        // Настройка возвращаемого потока данных
        InputStream inputStream = new ByteArrayInputStream(jsonUser.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        // Вызов метода readUserFromRequest
        User user = UserController.readUserFromRequest(exchange);

        // Проверка, что данные пользователя соответствуют ожидаемым
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }
}
