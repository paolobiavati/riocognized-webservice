package fr.olympicinsa.riocognized.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import org.springframework.util.Assert;

@Entity
@Table(name = "ATHLETE")
public class Athlete implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(name = "LAST_NAME")
    private String name;
    @NotNull
    @Column(name = "FIRST_NAME")
    private String surname;
    @NotNull
    @Column(name = "CONTENT")
    private String content;
    @NotNull
    @ManyToOne
    private Country country;
    @NotNull
    @ManyToOne
    private Sport sport;
    @Temporal(TemporalType.DATE)
    private Date dOb;
    @Column(name = "PRIVACY", columnDefinition = "boolean default false")
    private Boolean privacy = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "athlete_timetable", joinColumns = {
        @JoinColumn(name = "athlete_id")}, inverseJoinColumns = {
        @JoinColumn(name = "timetable_id")})
    private Set<Timetable> timetables = new HashSet<Timetable>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ImageFace> faces = new HashSet<>();

    @JsonIgnore
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "image_id")
    private Image image;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> description = new HashMap<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getDoB() {
        return this.dOb;
    }

    public void setDoB(Date dOb) {
        this.dOb = dOb;
    }

    public Boolean getPrivacy() {
        return this.privacy;
    }

    public void setPrivacy(Boolean privacy) {
        this.privacy = privacy;
    }

    public void setStringDoB(String dOb) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("dd/MM/yyyy");
        try {
            Date date = formatter.parse(dOb);
            this.dOb = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    public String getStringDoB() {
        if (dOb != null) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        formatter.applyPattern("dd/MM/yyyy");
        return formatter.format(dOb);
        } 
        return "";
    }
    
    public void setDescription(String name, String value) {

        Assert.hasText(name);

        if (value == null) {
            this.description.remove(value);
        } else {
            this.description.put(name, value);
        }
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSurname() {
        return surname;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public Sport getSport() {
        return this.sport;
    }

    @JsonProperty("racing")
    public Boolean isRacing() {
        Boolean racing = false;
        Iterator iter = this.timetables.iterator();
        Date now = new Date();
        while (iter.hasNext()) {
            Timetable timetable = (Timetable) iter.next();
            if ((now.compareTo(timetable.getEndDate()) < 0) && (now.compareTo(timetable.getStartDate()) > 0)) {
                racing = true;
            }
        }
        return racing;
    }

    @JsonProperty("age")
    public Integer getAge() {
        Date current = new Date();
        final Calendar calend = new GregorianCalendar();
        calend.set(Calendar.HOUR_OF_DAY, 0);
        calend.set(Calendar.MINUTE, 0);
        calend.set(Calendar.SECOND, 0);
        calend.set(Calendar.MILLISECOND, 0);
        int result = 0;
        if (dOb != null) {
            calend.setTimeInMillis(current.getTime() - dOb.getTime());
            result = calend.get(Calendar.YEAR) - 1970;
            result += (float) calend.get(Calendar.MONTH) / (float) 12;
        }
        return result;
    }

    @JsonProperty("image_url")
    public String getURL() {
        return "http://olympic-insa.fr.nf:8083/image/download/" + image.getId();

    }

    public Set<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetable(Set<Timetable> timetables) {
        this.timetables = timetables;
    }

    public Set<ImageFace> getFaces() {
        return faces;
    }

    public void setFaces(Set<ImageFace> faces) {
        this.faces = faces;
    }

    @JsonIgnore
    public String getFullName() {
        return name + ", " + surname;
    }
}
