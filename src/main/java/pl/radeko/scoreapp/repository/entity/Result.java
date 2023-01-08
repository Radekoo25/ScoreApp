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
    @Column(name="division")
    private Group group;
    @Column(name ="place")
    private int place;
    @OneToOne
    @JoinColumn(name = "team")
    private Team team;
    @Column(name="wins")
    private int wins;
    @Column(name="draws")
    private int draws;
    @Column(name="lost")
    private int lost;
    @Column(name="goalsScored")
    private int goalsScored;
    @Column(name="goalsLost")
    private int goalsLost;
    @Column(name="points")
    private int points;

    public Result(Group group, int place, Team team, int wins, int draws, int lost, int goalsScored, int goalsLost, int points) {
        this.group = group;
        this.place = place;
        this.team = team;
        this.wins = wins;
        this.draws = draws;
        this.lost = lost;
        this.goalsScored = goalsScored;
        this.goalsLost = goalsLost;
        this.points = points;
    }


    public Result() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public void setGoalsScored(int goals_scored) {
        this.goalsScored = goals_scored;
    }

    public int getGoalsLost() {
        return goalsLost;
    }

    public void setGoalsLost(int goals_lost) {
        this.goalsLost = goals_lost;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", group=" + group +
                ", place=" + place +
                ", team=" + team +
                ", wins=" + wins +
                ", draws=" + draws +
                ", lost=" + lost +
                ", goals_scored=" + goalsScored +
                ", goals_lost=" + goalsLost +
                ", points=" + points +
                '}';
    }
}
