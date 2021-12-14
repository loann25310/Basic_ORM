package examples.models;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.ManyToOne;
import fr.lagardedev.orm.annotations.Primary;

@Entity()
public class Jury {

    @Primary()
    @Column()
    public int idjury;

    @Column()
    public String nomjury;

    @ManyToOne(column = "idsalle")
    public Salle salle;

}
