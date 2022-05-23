module ru.geekbrains.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires org.apache.commons.io;

    requires org.kordamp.bootstrapfx.core;

    exports ru.geekbrains.chat.client;
    opens ru.geekbrains.chat.client to javafx.fxml;
}