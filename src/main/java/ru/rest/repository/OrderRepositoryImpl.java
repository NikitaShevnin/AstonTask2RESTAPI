package ru.rest.repository;

import ru.rest.entity.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Реализация интерфейса OrderRepository, использующая in-memory хранилище.
 */
public class OrderRepositoryImpl implements OrderRepository {
    private final Map<Long, Order> orderMap = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1L);

    @Override
    public Order save(Order order) {
        Long orderId = (long) order.getId();
        if (orderId == null) {
            orderId = nextId.getAndIncrement();
            order.setId(Math.toIntExact(orderId));
        }
        orderMap.put(orderId, order);
        return order;
    }


    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMap.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    @Override
    public void deleteById(Long id) {
        orderMap.remove(id);
    }
}

