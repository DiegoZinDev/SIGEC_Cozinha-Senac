module com.sigec.system.sigec {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires atlantafx.base;
    requires jakarta.mail;
    requires javafx.graphics;

    opens com.sigec.system.sigec to javafx.fxml;
    opens com.sigec.system.sigec.Controllers to javafx.fxml;

    exports com.sigec.system.sigec;
    exports com.sigec.system.sigec.Controllers;
    exports com.sigec.system.sigec.DAOS;
    exports com.sigec.system.sigec.DTBConfig;
    exports com.sigec.system.sigec.Services;
}