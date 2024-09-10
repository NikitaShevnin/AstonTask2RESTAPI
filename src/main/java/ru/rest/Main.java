package ru.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Main {
    public static void main(String[] args) {
        // Настройка HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("com.mysql.cj.jdbc.Driver"); // замените на ваш JDBC URL
        config.setUsername("Task2RestApi"); // замените на имя пользователя
        config.setPassword("qwerty"); // замените на пароль

        // Создание источника данных
        DataSource dataSource = new HikariDataSource(config);

        // Здесь вы можете инициализировать ваши DAO и мапперы с использованием MapStruct

        // Пример создания DAO
        // MyDao myDao = new MyDao(dataSource);
        // Пример использования маппера
        // MyMapper myMapper = Mappers.getMapper(MyMapper.class);

        // Здесь можно запустить другие части вашего приложения, например, сервер
        // Например, используя встроенный HTTP сервер на Java, такой как HttpServer
    }
}
