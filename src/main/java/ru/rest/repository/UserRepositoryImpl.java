package ru.rest.repository;

import ru.rest.entity.User;
import ru.rest.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация интерфейса UserRepository, использующая in-memory хранилище.
 */
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1L);

    @Override
    public User save(User user) {
        Long userId = (long) user.getId();
        if (userId == null) {
            userId = nextId.getAndIncrement();
            user.setId(Math.toIntExact(userId));
        }
        userMap.put(userId, user);
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
