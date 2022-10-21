package pl.radeko.scoreapp.repository.entity;

import pl.radeko.scoreapp.repository.enums.MatchupType;

import javax.persistence.*;

@Entity
@Table(name = "Matchups")
public class Matchup {
    @Id
    @Column(name="matchup_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="matchup_type")
    @Enumerated(EnumType.STRING)
    private MatchupType matchupType;
    @ManyToOne
    @JoinColumn(name = "teamA")
    private Team teamA;
    @Column(name="teamA_score")
    private int teamA_score;
    @ManyToOne
    @JoinColumn(name = "teamB")
    private Team teamB;

    public Team getTeamA() {
        return teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    @Column(name="teamB_score")
    private int teamB_score;

    public Matchup(MatchupType matchupType, Team teamA, int teamA_score, Team teamB, int teamB_score) {
        this.matchupType = matchupType;
        this.teamA = teamA;
        this.teamA_score = teamA_score;
        this.teamB = teamB;
        this.teamB_score = teamB_score;
    }

    public Matchup(MatchupType matchupType, Team teamA, Team teamB) {
        this.matchupType = matchupType;
        this.teamA = teamA;
        this.teamA_score = -1;
        this.teamB = teamB;
        this.teamB_score = -1;
    }

    public Matchup() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchupType getMatchupType() {
        return matchupType;
    }

    public void setMatchupType(MatchupType matchupType) {
        this.matchupType = matchupType;
    }

    public void setTeamA(Team teamA_id) {
        this.teamA = teamA_id;
    }

    public void setTeamB(Team teamB_id) {
        this.teamB = teamB_id;
    }

    public int getTeamA_score() {
        return teamA_score;
    }

    public void setTeamA_score(int teamA_score) {
        this.teamA_score = teamA_score;
    }

    public int getTeamB_score() {
        return teamB_score;
    }

    public void setTeamB_score(int teamB_score) {
        this.teamB_score = teamB_score;
    }

    @Override
    public String toString() {
        return "Matchup{" +
                "id=" + id +
                ", matchupType=" + matchupType +
                ", teamA_id=" + teamA +
                ", teamA_score=" + teamA_score +
                ", teamB_id=" + teamB +
                ", teamB_score=" + teamB_score +
                '}';
    }
}
