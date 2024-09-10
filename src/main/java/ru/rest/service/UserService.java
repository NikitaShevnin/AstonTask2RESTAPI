package ru.rest.service;

import ru.rest.entity.User;
import ru.rest.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервисный класс для управления пользователями.
 */
public class UserService {
    private final UserRepository userRepository;

    /**
     * Конструктор, создающий новый экземпляр класса UserService.
     *
     * @param userRepository репозиторий для управления пользователями
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создает нового пользователя.
     *
     * @param user пользователь, который нужно создать
     * @return созданный пользователь
     */
    public User createUser(User user) {
        // Валидация данных пользователя, если необходимо
        return userRepository.save(user);
    }

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно найти
     * @return необязательный объект, содержащий найденного пользователя, или пустой необязательный объект, если пользователь не найден
     */
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Находит всех пользователей.
     *
     * @return список всех пользователей
     */
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Обновляет существующего пользователя.
     *
     * @param user обновленный пользователь
     * @return обновленный пользователь
     */
    public User updateUser(User user) {

        // Валидация обновленных данных пользователя, если необходимо
        return userRepository.save(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
