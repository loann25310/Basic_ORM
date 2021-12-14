package examples.models;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.Primary;

@Entity()
public class Entreprise {

    @Primary()
    @Column()
    public int identreprise;

    @Column()
    public String nomentreprise;

    @Override
    public String toString() {
        return "Entreprise{" +
                "identreprise=" + identreprise +
                ", nomentreprise='" + nomentreprise + '\'' +
                '}';
    }
}
