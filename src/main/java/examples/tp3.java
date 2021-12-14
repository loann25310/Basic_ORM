package examples;

import fr.lagardedev.orm.Connection;
import fr.lagardedev.orm.ConnectionParameters;
import fr.lagardedev.orm.Repository;
import examples.models.Entreprise;
import examples.models.Etudiant;

public class tp3 {

    public static void main(String[] args) throws Exception {
        Connection connection = new Connection(new ConnectionParameters("127.0.0.1", 5432, "dev", "dev", "mainbase"));

        Repository<Etudiant> etudiantRepository = new Repository<>(Etudiant.class, connection);
        Etudiant etudiant = etudiantRepository.find(3);

        System.out.println(etudiant.toString());

        etudiant = etudiantRepository.find(5);

        etudiantRepository.save(etudiant);
        etudiant.nometu = "OK";
        etudiant.noetudiant = 0;
        System.out.println(etudiant.toString());
        etudiantRepository.save(etudiant);
        System.out.println(etudiant.toString());
        etudiantRepository.delete(etudiant);
        System.out.println(etudiant.toString());

        Entreprise entreprise = new Entreprise();
        entreprise.nomentreprise = "Test nouvelle entreprise";
        Repository<Entreprise> entrepriseRepository = new Repository<>(Entreprise.class, connection);
        entrepriseRepository.save(entreprise);

        // Repository<>
    }

}
