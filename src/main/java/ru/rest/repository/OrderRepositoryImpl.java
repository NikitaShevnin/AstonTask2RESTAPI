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
    /**
     * Внутреннее хранилище заказов, реализованное с использованием {@link HashMap}.
     */
    private final Map<Long, Order> orderMap = new HashMap<>();

    /**
     * Генератор уникальных идентификаторов заказов.
     */
    private final AtomicLong nextId = new AtomicLong(1L);

    /**
     * Сохраняет заказ в хранилище.
     *
     * @param order Заказ, который необходимо сохранить.
     * @return Сохраненный заказ.
     */
    @Override
    public Order save(Order order) {
        Long orderId = order.getId();
        if (orderId == null) {
            orderId = nextId.getAndIncrement();
            order.setId(Math.toIntExact(orderId));
        }
        orderMap.put(orderId, order);
        return order;
    }

    /**
     * Находит заказ в хранилище по идентификатору.
     *
     * @param id Идентификатор заказа.
     * @return Заказ, если он найден, иначе {@link Optional#empty()}.
     */
    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMap.get(id));
    }

    /**
     * Находит все заказы, хранящиеся в хранилище.
     *
     * @return Список всех заказов.
     */
    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }

    /**
     * Удаляет заказ из хранилища по идентификатору.
     *
     * @param id Идентификатор заказа, который необходимо удалить.
     */
    @Override
    public void deleteById(Long id) {
        orderMap.remove(id);
    }
}


