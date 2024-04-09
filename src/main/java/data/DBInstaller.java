package data;
;
import java.io.IOException;

import java.sql.*;
import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import util.FileManager;


public class DBInstaller {
    private static Logger LOGGER = Logger.getLogger(DBInstaller.class);
    public static void createDataBaseTables() throws DataBaseException {
        try {
            Connection con = DBConnection.getInstance();

            String sql = """
                    CREATE TABLE etudiant (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        nom VARCHAR(50),
                        prenom VARCHAR(50),
                        telephone1 VARCHAR(15),
                        telephone2 VARCHAR(15),
                        adresse VARCHAR(100),
                        email_personnel VARCHAR(100),
                        email_professionnel VARCHAR(100),
                        genre VARCHAR(10)
                    );
                                        
                    CREATE TABLE groupe (
                        idg INT PRIMARY KEY AUTO_INCREMENT,
                        nomg VARCHAR(50)
                    );
                                        
                    CREATE TABLE etudiant_groupe (
                        id_etudiant_groupe INT PRIMARY KEY AUTO_INCREMENT,
                        id_etudiant INT,
                        id_groupe INT,
                        FOREIGN KEY (id_etudiant) REFERENCES etudiant(id),
                        FOREIGN KEY (id_groupe) REFERENCES groupe(idg)
                    );
                          
                    """;
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception ex) {
            LOGGER.error(ex);
            throw new DataBaseException(ex);
        }
    }




    public static boolean checkIfAlreadyInstalled() throws IOException {

        String userHomeDirectory = System.getProperty("user.home");
        Properties dbProperties = DbPropertiesLoader.loadPoperties("conf.properties");
        String dbName = dbProperties.getProperty("db.name");
        String dataBaseFile = userHomeDirectory + "\\" + dbName + ".mv.db";
        return FileManager.fileExists(dataBaseFile);
    }

}
