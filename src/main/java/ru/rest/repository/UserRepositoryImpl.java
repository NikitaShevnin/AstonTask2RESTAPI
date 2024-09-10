package ru.rest.repository;

import ru.rest.entity.User;
import ru.rest.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Реализация интерфейса UserRepository, использующая in-memory хранилище.
 */
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private long nextId = 1L;

    @Override
    public User save(User user) {


        if (user.getId() == null) {
            user.setId(nextId++);
        }
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void deleteById(Long id) {
        userMap.remove(id);
    }
}
