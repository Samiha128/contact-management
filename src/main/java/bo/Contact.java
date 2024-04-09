package bo;

public class Contact {
    private int id;
    private String nom;
    private String prenom;
    private String telephone1;
    private String telephone2;
    private String adresse;
    private String emailPerso;
    private String emailPro;
    private Genre genre;

    public Contact(int id, String nom, String prenom, String telephone1, String telephone2, String adresse, String emailPerso, String emailPro, Genre genre) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone1 = telephone1;
        this.telephone2 = telephone2;
        this.adresse = adresse;
        this.emailPerso = emailPerso;
        this.emailPro = emailPro;
        this.genre = genre;
    }
    public Contact() {
        // Constructeur sans argument
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public void setTelephone1(String telephone1) {
        this.telephone1 = telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public void setTelephone2(String telephone2) {
        this.telephone2 = telephone2;
    }

    public String getAdresse() {
        return adresse;
    }


    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmailPerso() {
        return emailPerso;
    }

    public void setEmailPerso(String emailPerso) {
        this.emailPerso = emailPerso;
    }

    public String getEmailPro() {
        return emailPro;
    }

    public void setEmailPro(String emailPro) {
        this.emailPro = emailPro;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public enum Genre {
        MASCULIN("male"),
        FEMININ("female");

        private final String genre;

        private Genre(String genre) {
            this.genre = genre;
        }

        public String getGenre() {
            return genre;
        }
    }
    @Override
    public String toString() {
        return "Nom : " + nom +
                "\nPrénom : " + prenom +
                "\nTéléphone 1 : " + telephone1 +
                "\nTéléphone 2 : " + telephone2 +
                "\nAdresse : " + adresse +
                "\nEmail personnel : " + emailPerso +
                "\nEmail professionnel : " + emailPro +
                "\nGenre : " + genre;
    }

}
