package data;

import bo.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
public class GroupeDao {
    private static Connection connection;

    public GroupeDao() throws DataBaseException {
        connection = DBConnection.getInstance();
    }


    public void insertNomGroupe(String nomGroupe) throws DataBaseException {
        String sqlInsert = "INSERT INTO groupe (nomg) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
            statement.setString(1, nomGroupe);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataBaseException("Erreur lors de l'insertion du nom de groupe : " + ex.getMessage(), ex);
        }
    }
    public void ajouterGroupeEtudiant(String nomGroupe, String nom, String prenom) throws DataBaseException {
        try {
            // Récupérer l'ID de l'étudiant en fonction du nom et du prénom
            int idEtudiant = getIdEtudiantByNomPrenom(nom, prenom);

            // Récupérer l'ID du groupe en fonction du nom de groupe
            int idGroupe = getIdGroupeByNom(nomGroupe);

            // Insérer l'enregistrement dans la table de liaison "etudiant_groupe"
            String sqlInsert = "INSERT INTO etudiant_groupe (id_etudiant, id_groupe) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
                statement.setInt(1, idEtudiant);
                statement.setInt(2, idGroupe);
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataBaseException("Erreur lors de l'ajout du groupe à l'étudiant : " + ex.getMessage(), ex);
        }
    }

    private int getIdEtudiantByNomPrenom(String nom, String prenom) throws SQLException {
        String sqlSelect = "SELECT id FROM etudiant WHERE nom = ? AND prenom = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setString(1, nom);
            statement.setString(2, prenom);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        throw new SQLException("Étudiant introuvable : " + nom + " " + prenom);
    }

    private int getIdGroupeByNom(String nomGroupe) throws SQLException {
        String sqlSelect = "SELECT idg FROM groupe WHERE nomg = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setString(1, nomGroupe);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("idg");
                }
            }
        }
        throw new SQLException("Groupe introuvable : " + nomGroupe);
    }
    public void supprimerGroupe(String nomGroupe) throws DataBaseException {
        try {
            // Récupérer l'ID du groupe en fonction du nom de groupe
            int idGroupe = getIdGroupeByNom(nomGroupe);

            // Supprimer les enregistrements correspondants dans la table "etudiant_groupe"
            String sqlDeleteEtudiantGroupe = "DELETE FROM etudiant_groupe WHERE id_groupe = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlDeleteEtudiantGroupe)) {
                statement.setInt(1, idGroupe);
                statement.executeUpdate();
            }

            // Supprimer le groupe de la table "groupe"
            String sqlDeleteGroupe = "DELETE FROM groupe WHERE nomg = ?";
            try (PreparedStatement statement = connection.prepareStatement(sqlDeleteGroupe)) {
                statement.setString(1, nomGroupe);
                statement.executeUpdate();
            }

        } catch (SQLException ex) {
            throw new DataBaseException("Erreur lors de la suppression du groupe : " + ex.getMessage(), ex);
        }
    }
    public void rechercherGroupe(String nomGroupe) throws DataBaseException {
        try {
            // Récupérer l'ID du groupe en fonction du nom de groupe
            int idGroupe = getIdGroupeByNom(nomGroupe);

            if (idGroupe == -1) {
                System.out.println("Le groupe " + nomGroupe + " n'existe pas.");
                return;
            }

            // Récupérer les ID des contacts associés au groupe depuis la table "groupe_contact"
            List<Integer> idContacts = getIdContactsByGroupe(idGroupe);

            // Afficher les informations des contacts
            System.out.println("Contacts du groupe " + nomGroupe + ":");
            for (int idContact : idContacts) {
                bo.Contact contact = getContactById(idContact);
                System.out.println(contact.getNom() + " " + contact.getPrenom());
            }
        } catch (SQLException ex) {
            throw new DataBaseException("Erreur lors de la recherche du groupe : " + ex.getMessage(), ex);
        }
    }

    private List<Integer> getIdContactsByGroupe(int idGroupe) throws SQLException {
        List<Integer> idContacts = new ArrayList<>();
        String sqlSelect = "SELECT id_etudiant FROM etudiant_groupe WHERE id_groupe = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setInt(1, idGroupe);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    idContacts.add(resultSet.getInt("id_etudiant"));
                }
            }
        }
        return idContacts;
    }

    private bo.Contact getContactById(int idContact) throws SQLException {
        String sqlSelect = "SELECT * FROM etudiant WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setInt(1, idContact);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    bo.Contact contact = new bo.Contact();
                    contact.setId(resultSet.getInt("id"));
                    contact.setNom(resultSet.getString("nom"));
                    contact.setPrenom(resultSet.getString("prenom"));
                    // ... Set other properties of the contact

                    return contact;
                }
            }
        }
        throw new SQLException("Contact introuvable avec l'ID : " + idContact);
    }
    public static  void creerGroupesParNomContact() throws DataBaseException {
        try {
            // Récupérer tous les noms de contact distincts depuis la table "etudiant"
            List<String> nomsContacts = getDistinctNomsContacts();

            for (String nomContact : nomsContacts) {
                // Vérifier si un groupe avec le même nom existe déjà dans la table "groupe"
                if (isGroupeExistant(nomContact)) {
                    System.out.println("Un groupe avec le nom '" + nomContact + "' existe déjà. ");
                    continue;
                }

                // Créer un groupe avec le même nom dans la table "groupe"
                int idGroupe = creerGroupe(nomContact);

                // Récupérer les ID des contacts ayant le même nom depuis la table "etudiant"
                List<Integer> idContacts = getIdContactsByNomContact(nomContact);

                // Insérer l'ID du groupe dans la table "etudiant_groupe" pour chaque ID de contact correspondant
                for (int idContact : idContacts) {
                    insererEtudiantGroupe(idContact, idGroupe);
                }

                System.out.println("Groupe créé pour le contact : " + nomContact);
            }
        } catch (SQLException ex) {
            throw new DataBaseException("Erreur lors de la création des groupes par nom de contact : " + ex.getMessage(), ex);
        }
    }

    private static List<String> getDistinctNomsContacts() throws SQLException {
        List<String> nomsContacts = new ArrayList<>();
        String sqlSelect = "SELECT DISTINCT nom FROM etudiant";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                nomsContacts.add(resultSet.getString("nom"));
            }
        }
        return nomsContacts;
    }

    private static boolean isGroupeExistant(String nomGroupe) throws SQLException {
        String sqlSelect = "SELECT COUNT(*) AS count FROM groupe WHERE nomg = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setString(1, nomGroupe);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0;
                }
            }
        }
        return false;
    }

    private static int creerGroupe(String nomGroupe) throws SQLException {
        String sqlInsert = "INSERT INTO groupe (nomg) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, nomGroupe);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 1) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Erreur lors de la création du groupe.");
    }

    private static List<Integer> getIdContactsByNomContact(String nomContact) throws SQLException {
        List<Integer> idContacts = new ArrayList<>();
        String sqlSelect = "SELECT id FROM etudiant WHERE nom = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlSelect)) {
            statement.setString(1, nomContact);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    idContacts.add(resultSet.getInt("id"));
                }
            }
        }
        return idContacts;
    }

    private static void insererEtudiantGroupe(int idContact, int idGroupe) throws SQLException {
        String sqlInsert = "INSERT INTO etudiant_groupe (id_etudiant, id_groupe) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sqlInsert)) {
            statement.setInt(1, idContact);
            statement.setInt(2, idGroupe);
            statement.executeUpdate();
        }
    }







    // Autres méthodes de la classe GroupeDao si nécessaires
}
