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
import java.util.SortedSet;
import java.util.TreeSet;

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

    @ManyToMany(mappedBy = "vmTeams")
    @SortNatural
    private SortedSet<User> members = new TreeSet<>();

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

    /**
     *
     * @return
     */
    public SortedSet<User> getMembers() {
        return members;
    }

    /**
     *
     * @param members
     */
    public void setMembers(SortedSet<User> members) {
        this.members = members;
    }

    /**
     *
     * @param user
     */
    public void addUser(User user) {
        this.members.add(user);
        user.getVmTeams().add(this);
    }

    /**
     *
     * @param user
     */
    public void removeUser(User user) {
        this.members.remove(user);
        user.getVmTeams().remove(this);
    }
}