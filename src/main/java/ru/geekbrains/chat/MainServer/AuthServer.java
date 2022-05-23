package ru.geekbrains.chat.MainServer;

import java.sql.*;

public class AuthServer {
    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");

            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginPass(String login, String password) {
        String sql = String.format("select nickname from users where login = '%s' and password = '%s'", login, password);
        try {
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                return resultSet.getString(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String registerUser(String login, String password) {

        //String sql = String.format("insert into users (login, password, nickname) values ('%s', '%s', '%s');", login, password, login);
        //statement.execute(sql);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into users (login, password, nickname) values (?, ?, ?);");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, login);
            preparedStatement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return getNickByLoginPass(login, password);
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}