package fr.olympicinsa.riocognized.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.util.List;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.FetchType;

@Entity
@Table(name="COUNTRY")
public class Country implements Serializable{

    @Id
    private String id;
    @Column(name="NAME")
    private String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
