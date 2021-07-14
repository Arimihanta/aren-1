package fr.lirmm.aren.model.vm;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.AbstractEntity;
import fr.lirmm.aren.model.User;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana on 11/07/2021
 * @project aren-1
 */
@Entity
@Table(name = "vm_teams")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = VMTheme.class)
public class VMTeam extends AbstractEntity implements Serializable {
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
}