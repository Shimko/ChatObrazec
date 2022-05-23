module ru.geekbrains.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;

    opens ru.geekbrains.chat to javafx.fxml;
    exports ru.geekbrains.chat.client;
    opens ru.geekbrains.chat.client to javafx.fxml;
}