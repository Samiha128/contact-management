package ihm;

import bll.BusinessLogicException;
import bo.Contact;
import data.DBInstaller;
import data.GroupeDao;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import bll.ContaManager;
import data.ContactDao;
import data.DataBaseException;
import bo.Contact.Genre;




public class Main {
    private static Logger LOGGER = Logger.getLogger(Main.class);
    public static void printMenu() {

        System.out.println("1- Ajouter un contact");
        System.out.println("2- Rechercher un contact par numéro");
        System.out.println("3- Rechercher un contact par nom");
        System.out.println("4- Afficher les contacts par ordre alphabétique");
        System.out.println("5- Supprimer un contact par numéro professionnel ou numéro personnel");
        System.out.println("6- Modifier un contact par numéro professionnel ou numéro personnel");
        System.out.println("7- Ajouter groupe ");
        System.out.println("8- affecter un contact a un groupe ");
        System.out.println("9- supprimer un groupe ");
        System.out.println("10- afficher un groupe ");
        System.out.println("11 - Recherche ou cas de doute d'un contact");
        System.out.println("12 - Recherche phonetique");
        System.out.println("0- Sortir");


    }

    public static void main(String[] args) throws DataBaseException {


        //création d'une instance de la classe qui gère la logique métier
        DBInstaller tableCreator = new DBInstaller();
        try {
            //On vérifie que la base de données n'est pas encore crée
            if (!DBInstaller.checkIfAlreadyInstalled()) {
                //Créer la base de données
                tableCreator.createDataBaseTables();
                LOGGER.info("La base de données est crée correctement");
            }
        } catch (Exception ex) {
            //Dans le cas d'une erreur dans la création des tables on affiche un message d'erreur
            System.err.println("Erreur lors de la création de la base de données, voir le fichier log.txt pour plus de détails");
            //On arrete l'application ici avec un code d'erreur
            System.exit(-1);
        }

        //Pour lire les données au clavier
        Scanner sc = new Scanner(System.in);
        while (true) {
            //afficher le menu
            printMenu();
            //lire le choix
            System.out.println("Saisir le numéro de votre choix:");
            int choix = sc.nextInt();
            sc.nextLine();//évite de sauter par nextInt
            switch (choix) {
                case 1:
                    System.out.println("Veuillez entrer les informations du contact :");
                    System.out.println("Nom :");
                    String nom = sc.nextLine();
                    System.out.println("Prénom :");
                    String prenom = sc.nextLine();
                    System.out.println("Téléphone 1 :");
                    String telephone1 = sc.nextLine();
                    System.out.println("Téléphone 2 :");
                    String telephone2 = sc.nextLine();
                    System.out.println("Adresse :");
                    String adresse = sc.nextLine();
                    System.out.println("Email personnel :");
                    String emailPerso = sc.nextLine();
                    System.out.println("Email professionnel :");
                    String emailPro = sc.nextLine();
                    System.out.println("Genre (MASCULIN/FEMININ) :");
                    String genreStr = sc.nextLine();

                    try {
                        // Convertir la valeur du genre en enum Genre
                        Genre genre;
                        if (genreStr.equalsIgnoreCase("MASCULIN")) {
                            genre = Genre.MASCULIN;
                        } else if (genreStr.equalsIgnoreCase("FEMININ")) {
                            genre = Genre.FEMININ;
                        } else {
                            throw new BusinessLogicException("Genre invalide");
                        }

                        // Créer un nouvel objet Contact avec les informations saisies
                        Contact newContact = new Contact(0, nom, prenom, telephone1, telephone2, adresse, emailPerso, emailPro, genre);

                        // Appeler la méthode ajouterContact avec le nouvel objet Contact
                        ContaManager contaManager = new ContaManager(); // Créer une instance de ContaManager
                        contaManager.ajouterContact(newContact); // Appeler la méthode ajouterContact sur l'instance

                        System.out.println("Le contact a été ajouté avec succès.");

                        // Appeler la méthode creerGroupesParNomContact pour créer les groupes par nom de contact
                        GroupeDao.creerGroupesParNomContact();

                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de l'ajout du contact : " + e.getMessage());
                    } catch (BusinessLogicException e) {
                        System.out.println("Une erreur est survenue lors de la vérification des doublons : " + e.getMessage());
                    }
                    break;

                case 9:
                    Scanner sc10 = new Scanner(System.in);
                    System.out.println("Veuillez saisir le nom du groupe à supprimer :");
                    String nomGroupeASupprimer = sc10.nextLine();

                    GroupeDao groupeDao = new GroupeDao();
                    try {
                        groupeDao.supprimerGroupe(nomGroupeASupprimer);
                        System.out.println("Le groupe a été supprimé avec succès.");
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la suppression du groupe : " + e.getMessage());
                    }
                    break;

                case 8:
                    System.out.println("Veuillez saisir le nom du groupe :");
                    String nomGroupe = sc.nextLine();
                    System.out.println("Veuillez saisir le nom de l'étudiant :");
                    String nomEtudiant = sc.nextLine();
                    System.out.println("Veuillez saisir le prénom de l'étudiant :");
                    String prenomEtudiant = sc.nextLine();

                    GroupeDao groupeEtudiantDao = new GroupeDao();
                    try {
                        groupeEtudiantDao.ajouterGroupeEtudiant(nomGroupe, nomEtudiant, prenomEtudiant);
                        System.out.println("Le groupe a été ajouté à l'étudiant avec succès.");
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de l'ajout du groupe à l'étudiant : " + e.getMessage());
                    }
                    break;
                case 10:
                    System.out.println("Veuillez saisir le nom du groupe à rechercher :");
                    Scanner sc11 = new Scanner(System.in);
                    String nomGroupeARechercher = sc11.nextLine();

                    GroupeDao groupeDao2 = new GroupeDao();
                    try {
                        groupeDao2.rechercherGroupe(nomGroupeARechercher);
                    } catch (DataBaseException e) {
                        String errorMessage = e.getMessage();
                        if (errorMessage.contains("n'existe pas")) {
                            String groupeNotFoundMessage = "Groupe introuvable : " + nomGroupeARechercher;
                            System.out.println(groupeNotFoundMessage);
                        } else {
                            System.out.println("Une erreur est survenue lors de la recherche du groupe : " + errorMessage);
                        }
                    }
                    break;
                case 11:
                    System.out.println("Veuillez saisir le nom à rechercher :");
                    String nomRecherche = sc.nextLine();

                    ContactDao contactDao = new ContactDao();

                    try {
                        List<Contact> contacts = contactDao.rechercherContactParNom(nomRecherche);

                        if (!contacts.isEmpty()) {
                            System.out.println("Contacts trouvés :");
                            for (int i = 0; i < contacts.size(); i++) {
                                Contact contact = contacts.get(i);
                                System.out.println("Contact " + (i + 1) + ":");
                                System.out.println(contact.toString());
                                System.out.println();
                            }
                        } else {
                            System.out.println("Aucun contact trouvé avec le nom : " + nomRecherche);
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la recherche du contact : " + e.getMessage());
                    }
                    break;
                case 12:
                    Scanner sc13 = new Scanner(System.in);
                    System.out.println("Veuillez saisir le nom à rechercher :");
                    String nomRecherche2 = sc13.nextLine();

                    try {
                        ContactDao contactDao6 = new ContactDao(); // Créer une instance de ContactDao

                        // Appeler la méthode rechercherContactParNomPhonetique avec le nom recherché
                        List<Contact> contacts = contactDao6.rechercherContactParNomPhonetique(nomRecherche2);

                        // Afficher les contacts trouvés
                        if (!contacts.isEmpty()) {
                            System.out.println("Contacts trouvés :");
                            for (Contact contact : contacts) {
                                System.out.println(contact.toString());
                            }
                        } else {
                            System.out.println("Aucun contact trouvé avec le nom : " + nomRecherche2);
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la recherche des contacts : " + e.getMessage());
                    }
                    break;











                case 2:
                    System.out.println("Veuillez entrer le numéro de téléphone :");
                    String phoneNumber = sc.nextLine();

                    try {
                        ContactDao contactDao3 = new ContactDao(); // Créez une nouvelle instance de ContactDao
                        Contact contact = contactDao3.findContactByPhoneNumber(phoneNumber);

                        if (contact != null) {
                            System.out.println("Contact trouvé :");
                            System.out.println(contact.toString()); // Affiche toutes les informations du contact
                        } else {
                            System.out.println("Aucun contact trouvé avec le numéro de téléphone : " + phoneNumber);
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la recherche du contact : " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("Insertion d'un nom de groupe");

                    Scanner sc7 = new Scanner(System.in);

                    System.out.println("Entrez le nom du groupe :");
                    String nomGroupe1 = sc7.nextLine();

                    try {
                        GroupeDao groupeDao1 = new GroupeDao(); // Créer une instance de GroupeDao
                        groupeDao1.insertNomGroupe(nomGroupe1);
                        System.out.println("Le groupe a été créé avec succès.");
                    } catch (DataBaseException e) {
                        throw new RuntimeException(e);
                    }

                    break;



                case 3:
                    System.out.println("Veuillez entrer le nom du contact :");
                    String nom1 = sc.nextLine();
                    System.out.println("Veuillez entrer le prénom du contact :");
                    String prenom1 = sc.nextLine();

                    ContactDao ContactDao = new ContactDao();
                    try {
                        List<Contact> contacts = ContactDao.rechercherContactParNom1(nom1, prenom1);
                        if (!contacts.isEmpty()) {
                            System.out.println("Contacts trouvés :");
                            for (int i = 0; i < contacts.size(); i++) {
                                Contact contact = contacts.get(i);
                                System.out.println("Contact " + (i + 1) + ":");
                                System.out.println(contact.toString());
                                System.out.println();
                            }
                        } else {
                            System.out.println("Aucun contact trouvé avec le nom : " + nom1 + " et le prénom : " + prenom1);
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la recherche du contact : " + e.getMessage());
                    }
                    break;
                case 4:
                    ContactDao contactDao1 = new ContactDao();
                    try {
                        List<Contact> allContacts = contactDao1.getAllContacts();
                        if (!allContacts.isEmpty()) {
                            System.out.println("Contacts par ordre alphabétique :");
                            for (int i = 0; i < allContacts.size(); i++) {
                                Contact contact = allContacts.get(i);
                                System.out.println("Contact " + (i + 1) + ":");
                                System.out.println(contact.toString());
                                System.out.println();
                            }
                        } else {
                            System.out.println("Aucun contact trouvé.");
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la récupération des contacts : " + e.getMessage());
                    }
                    break;
                case 5:
                    System.out.println("Veuillez saisir le numéro professionnel ou le numéro personnel du contact que vous souhaitez supprimer :");
                    Scanner scanner = new Scanner(System.in);
                    String professionalNumber = scanner.nextLine();
                    String personalNumber = scanner.nextLine();

                    ContactDao contactDao2 = new ContactDao();
                    try {
                        contactDao2.delete(professionalNumber, personalNumber);
                        System.out.println("Le contact a été supprimé avec succès.");
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la suppression du contact : " + e.getMessage());
                    }
                    break;
                case 6:
                    System.out.println("Mise à jour d'un contact :");
                    System.out.println("Veuillez entrer le numéro de téléphone du contact à mettre à jour :");
                    Scanner sc1 = new Scanner(System.in);

                    String phoneNumberToUpdate = sc.nextLine();

                    ContactDao contactDaoToUpdate = new ContactDao();
                    try {
                        Contact contactToUpdate = contactDaoToUpdate.findContactByPhoneNumber(phoneNumberToUpdate);
                        if (contactToUpdate != null) {
                            System.out.println("Contact trouvé :");
                            System.out.println(contactToUpdate.toString());
                            System.out.println("Veuillez entrer les nouvelles informations du contact :");

                            System.out.println("Nom :");
                            String nomToUpdate = sc.nextLine();
                            contactToUpdate.setNom(nomToUpdate);

                            System.out.println("Prénom :");
                            String prenomToUpdate = sc.nextLine();
                            contactToUpdate.setPrenom(prenomToUpdate);

                            System.out.println("Numéro de téléphone 1 :");
                            String telephone1ToUpdate = sc.nextLine();
                            contactToUpdate.setTelephone1(telephone1ToUpdate);

                            System.out.println("Numéro de téléphone 2 :");
                            String telephone2ToUpdate = sc.nextLine();
                            contactToUpdate.setTelephone2(telephone2ToUpdate);

                            System.out.println("Adresse :");
                            String adresseToUpdate = sc.nextLine();
                            contactToUpdate.setAdresse(adresseToUpdate);

                            System.out.println("Email personnel :");
                            String emailPersoToUpdate = sc.nextLine();
                            contactToUpdate.setEmailPerso(emailPersoToUpdate);

                            System.out.println("Email professionnel :");
                            String emailProToUpdate = sc.nextLine();
                            contactToUpdate.setEmailPro(emailProToUpdate);

                            System.out.println("Genre :");
                            String genreInputToUpdate = sc.nextLine();
                            Genre genreToUpdate = Genre.valueOf(genreInputToUpdate.toUpperCase());
                            contactToUpdate.setGenre(genreToUpdate);

                            // Update the contact
                            boolean isUpdated = contactDaoToUpdate.updateContact(contactToUpdate);
                            if (isUpdated) {
                                System.out.println("Le contact a été mis à jour avec succès.");
                            } else {
                                System.out.println("La mise à jour du contact a échoué.");
                            }
                        } else {
                            System.out.println("Aucun contact trouvé avec le numéro de téléphone : " + phoneNumberToUpdate);
                        }
                    } catch (DataBaseException e) {
                        System.out.println("Une erreur est survenue lors de la mise à jour du contact : " + e.getMessage());
                    }
                    break;
                case 0:
                    System.out.println("Bye! Merci d'utiliser mon application.");
                    System.out.println("Si vous rencontrez des problèmes, veuillez me contacter à l'adresse elmansourisamiha80@gmail.com");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;






            }















        }


        }
    }

