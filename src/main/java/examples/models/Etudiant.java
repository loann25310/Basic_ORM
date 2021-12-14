package examples.models;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.ManyToOne;
import fr.lagardedev.orm.annotations.Primary;

@Entity()
public class Etudiant {

    @Primary()
    @Column()
    public int noetudiant;

    @Column()
    public String nometu;

    @Column()
    public boolean tuteurpresent;

    @ManyToOne(column = "identreprise")
    public Entreprise entreprise;

    @ManyToOne(column = "noprofesseur")
    public Professeur professeur;

    @Override
    public String toString() {
        return "Etudiant{" +
                "noetudiant=" + noetudiant +
                ", nometu='" + nometu + '\'' +
                ", tuteurpresent=" + tuteurpresent +
                ", entreprise=" + entreprise +
                ", professeur=" + professeur +
                '}';
    }
}
