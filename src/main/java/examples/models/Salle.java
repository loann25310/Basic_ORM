package examples.models;

import fr.lagardedev.orm.annotations.Column;
import fr.lagardedev.orm.annotations.Entity;
import fr.lagardedev.orm.annotations.Primary;

@Entity()
public class Salle {

    @Primary()
    @Column()
    public int idsalle;

    @Column()
    public String nomsalle;

}
