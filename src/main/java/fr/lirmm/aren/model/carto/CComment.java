package fr.lirmm.aren.model.carto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import fr.lirmm.aren.model.AbstractOwnedEntity;
import fr.lirmm.aren.model.TagSet;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author ANDRIAMBOLAHARIMIHANTA Havana
 * @created 11/10/2021 - 10:22
 * @project aren-1
 */
@Entity
@Table(name = "comments_carto")

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = CComment.class)

public class CComment extends AbstractOwnedEntity implements Serializable {
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "reformulation")
    private String reformulation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "argumentation")
    private String argumentation;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "selection")
    private String selection;

    @Column(name = "start_container")
    private String startContainer;

    @Column(name = "start_offset")
    private Long startOffset;

    @Column(name = "end_container")
    private String endContainer;

    @Column(name = "end_offset")
    private Long endOffset;

    @Basic(optional = false)
    @NotNull
    @Column(name = "moderated")
    private boolean moderated = false;

    @Basic(optional = false)
    @NotNull
    @Column(name = "signaled")
    private boolean signaled = false;

    @OneToMany(mappedBy = "parent")
    @SortNatural
    private SortedSet<CComment> comments = new TreeSet<>();

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "parent_id", referencedColumnName = "id", updatable = false)
    @ManyToOne
    private CComment parent;

    @JsonIdentityReference(alwaysAsId = true)
    @JoinColumn(name = "debate_id", referencedColumnName = "id", updatable = false)
    @ManyToOne(optional = false)
    private CDebate debate;

    public String getReformulation() {
        return reformulation;
    }

    public void setReformulation(String reformulation) {
        this.reformulation = reformulation;
    }

    public String getArgumentation() {
        return argumentation;
    }

    public void setArgumentation(String argumentation) {
        this.argumentation = argumentation;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getStartContainer() {
        return startContainer;
    }

    public void setStartContainer(String startContainer) {
        this.startContainer = startContainer;
    }

    public Long getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Long startOffset) {
        this.startOffset = startOffset;
    }

    public String getEndContainer() {
        return endContainer;
    }

    public void setEndContainer(String endContainer) {
        this.endContainer = endContainer;
    }

    public Long getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(Long endOffset) {
        this.endOffset = endOffset;
    }

    public boolean isModerated() {
        return moderated;
    }

    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    public boolean isSignaled() {
        return signaled;
    }

    public void setSignaled(boolean signaled) {
        this.signaled = signaled;
    }

    public SortedSet<CComment> getComments() {
        return comments;
    }

    public void setComments(SortedSet<CComment> comments) {
        this.comments = comments;
    }

    public CComment getParent() {
        return parent;
    }

    public void setParent(CComment parent) {
        this.parent = parent;
    }

    public CDebate getDebate() {
        return debate;
    }

    public void setDebate(CDebate debate) {
        this.debate = debate;
    }
}
