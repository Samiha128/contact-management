package bll;
import bo.Contact;
import bo.groupe;
import data.ContactDao;
import data.DataBaseException;
import data.GroupeDao;
import org.apache.log4j.Logger;
import java.util.List;


public class ContaManager {
    private static Logger LOGGER = Logger.getLogger(ContaManager.class);

    private ContactDao contactDao = new ContactDao();
    private GroupeDao groupeDao = new GroupeDao();

    public ContaManager() throws DataBaseException {
    }

    public void ajouterContact(Contact contact) throws DataBaseException, BusinessLogicException {
        // Vérifier d'abord que le contact n'existe pas
        Contact existingContact = contactDao.rechercherContactParNom(contact.getNom(), contact.getPrenom());
        // Si le contact existe déjà, remonter une exception
        if (existingContact != null) {
            throw new BusinessLogicException("Le contact existe déjà");
        }
        // Sinon, enregistrer le contact
        contactDao.create(contact);
    }
    public void modifierContact(Contact contact) throws DataBaseException, BusinessLogicException {
        // Vérifier d'abord que le contact existe
        Contact existingContact = contactDao.rechercherContactParNom(contact.getNom(), contact.getPrenom());
        // Si le contact n'existe pas, remonter une exception
        if (existingContact == null) {
            throw new BusinessLogicException("Le contact n'existe pas");
        }
        // Modifier le contact
        contactDao.updateContact(contact);
    }










}
