package data;

import bo.Contact;
import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;






public class ContactDao {
    private Logger logger = Logger.getLogger(getClass());

    public Contact rechercherContactParNom(String pNom, String pPrenom) throws DataBaseException {
        List<Contact> list = new ArrayList<>();

        try {
            Connection c = DBConnection.getInstance();
            PreparedStatement stm = c.prepareStatement("SELECT * from etudiant where upper(nom)=? AND upper(prenom)=?");
            stm.setString(1, pNom.toUpperCase());
            stm.setString(2, pPrenom.toUpperCase());
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                list.add(resultToContact(rs));
            }

            rs.close();
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
    public void create(Contact pContact) throws DataBaseException {
        int generatedId = -1;

        try {
            Connection c = DBConnection.getInstance();
            String sqlInsert = "INSERT INTO etudiant(nom, prenom, telephone1, telephone2, adresse, email_personnel, email_professionnel, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = c.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, pContact.getNom());
            stm.setString(2, pContact.getPrenom());
            stm.setString(3, pContact.getTelephone1().trim().replaceAll("\\s", ""));
            stm.setString(4, pContact.getTelephone2().trim().replaceAll("\\s", ""));
            stm.setString(5, pContact.getAdresse());
            stm.setString(6, pContact.getEmailPerso());
            stm.setString(7, pContact.getEmailPro());
            stm.setString(8, pContact.getGenre().getGenre());

            stm.executeUpdate();

            ResultSet generatedKeys = stm.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedId = generatedKeys.getInt(1);
                pContact.setId(generatedId);
            }
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }
    }


    private Contact resultToContact(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        String prenom = rs.getString("prenom");
        String telephone1 = rs.getString("telephone1");
        String telephone2 = rs.getString("telephone2");
        String adresse = rs.getString("adresse");
        String emailPerso = rs.getString("email_personnel");
        String emailPro = rs.getString("email_professionnel");
        String genreStr = rs.getString("genre");
        Contact.Genre genre = Contact.Genre.MASCULIN;
        if (genreStr.equalsIgnoreCase("female")) {
            genre = Contact.Genre.FEMININ;
        }

        return new Contact(id, nom, prenom, telephone1, telephone2, adresse, emailPerso, emailPro, genre);
    }
    public List<Contact> getAllContacts() throws DataBaseException {
        List<Contact> contacts = new ArrayList<>();

        try {
            Connection c = DBConnection.getInstance();
            Statement stm = c.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM etudiant");

            while (rs.next()) {
                contacts.add(resultToContact(rs));
            }

            // Trier la liste des contacts par nom, puis prénom
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact c1, Contact c2) {
                    int result = c1.getNom().compareToIgnoreCase(c2.getNom());
                    if (result == 0) {
                        result = c1.getPrenom().compareToIgnoreCase(c2.getPrenom());
                    }
                    return result;
                }
            });

            rs.close();
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }

        return contacts;
    }




    public void afficheAll() {
        ContactDao contactDao = new ContactDao();
        try {
            List<Contact> allContacts = contactDao.getAllContacts();
            for (Contact contact : allContacts) {
                System.out.println(contact);
            }
        } catch (DataBaseException e) {
            // Gérer l'exception
            e.printStackTrace();
        }
    }
    private static final String UPDATE_CONTACT_SQL = "UPDATE ETUDIANT SET Nom = ?, Prenom = ?, Telephone1 = ?, Telephone2 = ?, "
            + "Adresse = ?, Email_personnel = ?, Email_professionnel = ?, Genre = ? WHERE Telephone1 = ? OR Telephone2 = ?";
    private static final String SELECT_ID_GROUPE_SQL = "SELECT id_groupe FROM etudiant_groupe WHERE id_etudiant = ?";
    private static final String UPDATE_GROUPE_SQL = "UPDATE groupe SET nomg = ? WHERE idg = ?";
    private static final String SELECT_CONTACT_ID_SQL = "SELECT id FROM ETUDIANT WHERE Telephone1 = ? OR Telephone2 = ?";

    public boolean updateContact(Contact contact) throws DataBaseException {
        boolean rowUpdated = false;
        try {
            Connection c = DBConnection.getInstance();
            PreparedStatement statement = c.prepareStatement(UPDATE_CONTACT_SQL);
            statement.setString(1, contact.getNom());
            statement.setString(2, contact.getPrenom());
            statement.setString(3, contact.getTelephone1());
            statement.setString(4, contact.getTelephone2());
            statement.setString(5, contact.getAdresse());
            statement.setString(6, contact.getEmailPerso());
            statement.setString(7, contact.getEmailPro());
            statement.setString(8, contact.getGenre().toString());
            statement.setString(9, contact.getTelephone1());
            statement.setString(10, contact.getTelephone2());

            rowUpdated = statement.executeUpdate() > 0;

            if (rowUpdated) {
                // Récupération de l'ID du contact après la mise à jour
                PreparedStatement selectContactIdStatement = c.prepareStatement(SELECT_CONTACT_ID_SQL);
                selectContactIdStatement.setString(1, contact.getTelephone1());
                selectContactIdStatement.setString(2, contact.getTelephone2());

                ResultSet contactIdResultSet = selectContactIdStatement.executeQuery();
                if (contactIdResultSet.next()) {
                    int contactId = contactIdResultSet.getInt("id");

                    // Récupération de l'ID du groupe de l'étudiant
                    PreparedStatement selectGroupeIdStatement = c.prepareStatement(SELECT_ID_GROUPE_SQL);
                    selectGroupeIdStatement.setInt(1, contactId);

                    ResultSet groupeIdResultSet = selectGroupeIdStatement.executeQuery();
                    if (groupeIdResultSet.next()) {
                        int groupeId = groupeIdResultSet.getInt("id_groupe");

                        // Mise à jour du nom du groupe
                        PreparedStatement updateGroupeStatement = c.prepareStatement(UPDATE_GROUPE_SQL);
                        updateGroupeStatement.setString(1, contact.getNom());
                        updateGroupeStatement.setInt(2, groupeId);

                        updateGroupeStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }
        return rowUpdated;
    }



    public void delete(String professionalNumber, String personalNumber) throws DataBaseException {
        // avec transaction
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement selectIdStatement = null;
        PreparedStatement deleteGroupeStatement = null;

        try {
            connection = DBConnection.getInstance();
            connection.setAutoCommit(false);

            // Supprimer les espaces supplémentaires dans les numéros de téléphone
            String cleanProfessionalNumber = professionalNumber.trim().replaceAll("\\s", "");
            String cleanPersonalNumber = personalNumber.trim().replaceAll("\\s", "");

            // Supprimer les enregistrements de la table etudiant_groupe correspondant aux numéros de téléphone
            selectIdStatement = connection.prepareStatement("SELECT id FROM etudiant WHERE telephone1 = ? OR telephone2 = ?");
            selectIdStatement.setString(1, cleanProfessionalNumber);
            selectIdStatement.setString(2, cleanPersonalNumber);
            ResultSet resultSet = selectIdStatement.executeQuery();

            List<Integer> studentIds = new ArrayList<>();
            while (resultSet.next()) {
                int studentId = resultSet.getInt("id");
                studentIds.add(studentId);
            }

            deleteGroupeStatement = connection.prepareStatement("DELETE FROM etudiant_groupe WHERE id_etudiant = ?");
            for (int studentId : studentIds) {
                deleteGroupeStatement.setInt(1, studentId);
                deleteGroupeStatement.executeUpdate();
            }

            // Supprimer les enregistrements de la table etudiant correspondant aux numéros de téléphone
            deleteStatement = connection.prepareStatement("DELETE FROM etudiant WHERE telephone1 = ? OR telephone2 = ?");
            deleteStatement.setString(1, cleanProfessionalNumber);
            deleteStatement.setString(2, cleanPersonalNumber);
            int rowsAffected = deleteStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new DataBaseException("Le contact avec le numéro professionnel " + professionalNumber + " ou le numéro personnel " + personalNumber + " n'existe pas.");
            }

            connection.commit();
        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e) {
                    logger.error("Erreur lors de l'annulation de la transaction : ", e);
                }
            }

            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        } finally {
            if (deleteStatement != null) {
                try {
                    deleteStatement.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture du PreparedStatement : ", e);
                }
            }

            if (deleteGroupeStatement != null) {
                try {
                    deleteGroupeStatement.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture du PreparedStatement : ", e);
                }
            }

            if (selectIdStatement != null) {
                try {
                    selectIdStatement.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture du PreparedStatement : ", e);
                }
            }

            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture de la connexion : ", e);
                }
            }
        }
    }




    public Contact findContactByPhoneNumber(String phoneNumber) throws DataBaseException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getInstance();
            statement = connection.prepareStatement("SELECT * FROM etudiant WHERE telephone1 = ? OR telephone2 = ?");
            statement.setString(1, phoneNumber);
            statement.setString(2, phoneNumber);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Contact contact = resultToContact(resultSet);

                return contact;
            } else {
                System.out.println("Aucun contact trouvé avec le numéro de téléphone : " + phoneNumber);
                return null;
            }
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture du ResultSet : ", e);
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture du PreparedStatement : ", e);
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Erreur lors de la fermeture de la connexion : ", e);
                }
            }
        }
    }
    public List<Contact> rechercherContactParNom1(String pNom, String pPrenom) throws DataBaseException {
        List<Contact> list = new ArrayList<>();

        try {
            Connection c = DBConnection.getInstance();
            PreparedStatement stm = c.prepareStatement("SELECT * FROM etudiant WHERE upper(nom) = ? OR upper(prenom) = ?");
            stm.setString(1, pNom.toUpperCase());
            stm.setString(2, pPrenom.toUpperCase());
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                list.add(resultToContact(rs));
            }

            rs.close();
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }

        return list;
    }
    // Inside ContactDao class
    public List<Contact> rechercherContactParNom(String nomRecherche) throws DataBaseException {
        List<Contact> list = new ArrayList<>();
        JaroWinklerDistance jaroWinklerDistance = new JaroWinklerDistance();
        double similarityThreshold = 0.5; // Seuil de similarité

        try {
            Connection connection = DBConnection.getInstance();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM etudiant");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String nomContact = resultSet.getString("nom");
                double similarity = jaroWinklerDistance.apply(nomContact, nomRecherche);

                if (similarity >= similarityThreshold) {
                    Contact contact = resultToContact(resultSet);
                    list.add(contact);
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }

        return list;
    }
    public List<Contact> rechercherContactParNomPhonetique(String nomRecherche) throws DataBaseException {
        List<Contact> list = new ArrayList<>();

        try {
            Connection connection = DBConnection.getInstance();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM etudiant");
            ResultSet resultSet = statement.executeQuery();

            DoubleMetaphone doubleMetaphone = new DoubleMetaphone();

            while (resultSet.next()) {
                String nom = resultSet.getString("nom");

                if (doubleMetaphone.isDoubleMetaphoneEqual(nom, nomRecherche)) {
                    Contact contact = resultToContact(resultSet);
                    list.add(contact);
                }
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            logger.error("Erreur à cause de : ", ex);
            throw new DataBaseException(ex);
        }

        return list;
    }









}
