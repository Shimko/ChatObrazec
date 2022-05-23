module ru.geekbrains.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires sqlite.jdbc;
    requires org.apache.commons.io;


    exports ru.geekbrains.chat.client;
    opens ru.geekbrains.chat.client to javafx.fxml;
}