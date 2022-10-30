package pl.radeko.scoreapp.repository.entity;

import pl.radeko.scoreapp.repository.enums.Group;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.nio.file.Path;
import java.util.Arrays;

@Entity
@Table(name = "Teams")
public class Team {
    @Id
    @Column(name="team_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    @NotEmpty(message = "Team name cant be empty!!")
    @Size(min = 5, max =250)
    private String name;
    @Column(name="description")
    private String description;
    @Column(name="photo")
    private String photo;
    @Column(name="division")
    @Enumerated(EnumType.STRING)
    private Group group;

    public Team(String name, String description, String photo) {
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.group = null;
    }

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
        this.group = null;
        this.photo = "Logo.png";
    }

    public Team() {
        this.group = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", photo=" + photo +
                ", group=" + group +
                '}';
    }
}
