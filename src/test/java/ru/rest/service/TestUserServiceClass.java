package ru.rest.service;

import org.junit.Before;
import org.junit.Test;
import ru.rest.entity.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Тестовый класс для проверки функциональности службы пользователей (UserService).
 * Этот класс содержит тесты, которые проверяют основные операции работы с пользователями,
 * такие как получение пользователя, вставка, обновление и удаление пользователей.
 */
public class TestUserServiceClass {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/Task2RestApi";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private Connection connection;

    /**
     * Устанавливает соединение с базой данных перед выполнением тестов.
     *
     * @throws SQLException если не удалось установить соединение с базой данных
     */
    @Before
    public void setUp() throws SQLException {
        // Устанавливаем соединение с базой
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        // Здесь можно добавить код для настройки тестовых данных, если необходимо.
    }

    /**
     * Тестирует получение всех пользователей из базы данных.
     * Проверяет, что список пользователей не равен null и содержит ожидаемое количество пользователей.
     */
    @Test
    public void testGetUsersFromDatabase() {
        List<User> users = UserService.getUsersFromDatabase();

        // Проверяем количество пользователей
        assertEquals(6, users.size()); // Согласно MySQLSetup, должно быть 6 пользователей

        // Проверяем данные первого пользователя
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("john.doe@example.com", users.get(0).getEmail());
    }

    /**
     * Тестирует получение пользователя по его идентификатору.
     * Проверяет, что пользователь с указанным идентификатором существует и его данные соответствуют ожидаемым.
     */
    @Test
    public void testGetUserById() {
        User user = UserService.getUserById(1); // Получаем пользователя с id 1

        // Проверяем данные пользователя
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    /**
     * Тестирует получение пользователя по несуществующему идентификатору.
     * Проверяет, что результат равен null для несуществующего идентификатора.
     */
    @Test
    public void testGetUserByInvalidId() {
        User user = UserService.getUserById(999); // Проверяем несуществующий id
        assertNull(user); // Ожидаем, что пользователь не найден
    }

    /**
     * Тестирует вставку нового пользователя в базу данных.
     * Проверяет, что идентификатор нового пользователя больше 0 и что пользователь действительно добавлен в базу данных.
     */
    @Test
    public void testInsertUserIntoDatabase() {
        User newUser = new User(1, "Alice Wonder", "alice.wonder@example.com");
        int newUserId = UserService.insertUserIntoDatabase(newUser);

        // Проверяем, что идентификатор нового пользователя больше 0
        assertTrue(newUserId > 0);

        // Дополнительно можно проверить, что пользователь действительно добавился
        User retrievedUser = UserService.getUserById(newUserId);
        assertNotNull(retrievedUser);
        assertEquals("Alice Wonder", retrievedUser.getName());
        assertEquals("alice.wonder@example.com", retrievedUser.getEmail());
    }

    /**
     * Тестирует обновление существующего пользователя в базе данных.
     * Проверяет, что пользователь был успешно обновлен и его данные соответствуют ожидаемым.
     */
    @Test
    public void testUpdateUserInDatabase() {
        User userToUpdate = new User(1, "Updated Name", "updated.email@example.com");
        userToUpdate.setId(1); // Обновляем существующего пользователя с id 1

        boolean isUpdated = UserService.updateUserInDatabase(userToUpdate);

        // Проверяем, что пользователь был успешно обновлен
        assertTrue(isUpdated);

        // Проверяем, что данные обновились
        User updatedUser = UserService.getUserById(1);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated.email@example.com", updatedUser.getEmail());
    }

    /**
     * Тестирует удаление пользователя из базы данных.
     * Проверяет, что пользователь успешно удален и что он больше не существует в базе данных.
     */
    @Test
    public void testDeleteUserFromDatabase() {
        boolean isDeleted = UserService.deleteUserFromDatabase(6); // Удаляем пользователя с id 6

        // Проверяем, что пользователь успешно удален
        assertTrue(isDeleted);

        // Проверяем, что пользователь действительно удален
        User deletedUser = UserService.getUserById(6);
        assertNull(deletedUser);
    }
}