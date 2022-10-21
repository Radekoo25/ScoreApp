package pl.radeko.scoreapp.repository.entity;

import pl.radeko.scoreapp.repository.enums.Group;

import javax.persistence.*;

@Entity
@Table(name = "Results")
public class Result {
    @Id
    @Column(name="result_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "team")
    private Team team;
    @Column(name="points")
    private int points;
    @Column(name="goals_scored")
    private int goals_scored;
    @Column(name="goals_lost")
    private int goals_lost;
    @Column(name="division")
    private Group group;
    @Column(name ="place")
    private int place;

    public Result(Team team, int points, int goals_scored, int goals_lost, Group group, int place) {
        this.team = team;
        this.points = points;
        this.goals_scored = goals_scored;
        this.goals_lost = goals_lost;
        this.group = group;
        this.place = place;
    }

    public Result() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoals_scored() {
        return goals_scored;
    }

    public void setGoals_scored(int goals_scored) {
        this.goals_scored = goals_scored;
    }

    public int getGoals_lost() {
        return goals_lost;
    }

    public void setGoals_lost(int goals_lost) {
        this.goals_lost = goals_lost;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }
}
