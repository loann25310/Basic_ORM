package examples.models;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.Primary;

@Entity()
public class Professeur {

    @Primary()
    @Column()
    public int noprofesseur;

    @Column()
    public String nomprofesseur;

    @Override
    public String toString() {
        return "Professeur{" +
                "noprofesseur=" + noprofesseur +
                ", nomprofesseur='" + nomprofesseur + '\'' +
                '}';
    }
}
