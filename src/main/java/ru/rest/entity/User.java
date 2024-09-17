package ru.rest.entity;

/**
 * Класс {@code User} представляет пользователя в системе.
 *
 * @author [Ваше Имя]
 * @version 1.0
 */
public class User {
    private int id;
    private String name;
    private String email;

    /**
     * Создает новый экземпляр {@code User} с указанными идентификатором, именем и электронной почтой.
     *
     * @param id    идентификатор пользователя
     * @param name  имя пользователя
     * @param email электронная почта пользователя
     */
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    /**
     * Возвращает идентификатор пользователя.
     *
     * @return идентификатор пользователя
     */
    public int getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор пользователя.
     *
     * @param id идентификатор пользователя
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name имя пользователя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает электронную почту пользователя.
     *
     * @return электронная почта пользователя
     */
    public String getEmail() {
        return email;
    }

    /**
     * Устанавливает электронную почту пользователя.
     *
     * @param email электронная почта пользователя
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
