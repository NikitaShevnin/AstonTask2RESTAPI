package ru.rest.repository;

import ru.rest.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория для управления пользователями.
 */
public interface UserRepository {
    /**
     * Сохраняет нового пользователя.
     *
     * @param user пользователь, которого нужно сохранить
     * @return сохраненный пользователь
     */
    User save(User user);

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно найти
     * @return необязательный объект, содержащий найденного пользователя, или пустой необязательный объект, если пользователь не найден
     */
    Optional<User> findById(Long id);

    /**
     * Находит всех пользователей.
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить
     */
    void deleteById(Long id);
}

