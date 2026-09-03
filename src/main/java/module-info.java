module com.sigec.system.sigec {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires jbcrypt;

    opens com.sigec.system.sigec to javafx.fxml;
    exports com.sigec.system.sigec;
}