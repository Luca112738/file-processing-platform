module com.mycompany.br.fatec.tacoexpress {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    // FXMLLoader usa reflexão sobre os controllers (pacote Controller)
    // e sobre os modelos (pacote Model); ambos precisam ser abertos.
    opens com.mycompany.br.fatec.tacoexpress to javafx.fxml;
    opens Controller to javafx.fxml;
    opens Model to javafx.fxml;

    exports com.mycompany.br.fatec.tacoexpress;
}
