package fr.lirmm.aren.model.carto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.AbstractDatedEntity;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 11/10/2021 - 10:18
 * @project aren-1
 */
@Entity
@Table(name = "documents_carto")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CDocument.class)

public class CDocument extends AbstractDatedEntity implements Serializable {
    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 255)
    @Column(name = "author")
    private String author;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "content")
    private String content;

    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private CCategory category;

    @OneToMany(mappedBy = "document")
    @SortNatural
    private SortedSet<CDebate> debates = new TreeSet<>();

    @Column(name = "debates_count")
    private Integer debatesCount = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CCategory getCategory() {
        return category;
    }

    public void setCategory(CCategory category) {
        this.category = category;
    }

    public SortedSet<CDebate> getDebates() {
        return debates;
    }

    public void setDebates(SortedSet<CDebate> debates) {
        this.debates = debates;
    }

    public Integer getDebatesCount() {
        return debatesCount;
    }

    public void setDebatesCount(Integer debatesCount) {
        this.debatesCount = debatesCount;
    }
}
