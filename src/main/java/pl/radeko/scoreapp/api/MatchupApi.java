package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.manager.ResultManager;
import pl.radeko.scoreapp.manager.TeamManager;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.enums.MatchupType;

@RestController
@RequestMapping("/api/matchups")
public class MatchupApi {

    private final MatchupManager matchups;
    private final TeamManager teams;
    private final ResultManager results;

    @Autowired
    public MatchupApi(MatchupManager matchups, TeamManager teams, ResultManager results) {
        this.matchups = matchups;
        this.teams = teams;
        this.results = results;
    }

    @GetMapping("/all")
    public Iterable<Matchup> getAll() {
        return matchups.findAll();
    }

    @PutMapping("/creategroupmatchups")
    public void createGroupMatchups() {

        matchups.createGroupMatchups(teams.getTeamRepository());
    }

    @PutMapping("/completematchup/{id}")
    public void updateMatchup(@PathVariable Long id, @RequestBody Matchup matchup) {
        matchups.updateMatchup(id, matchup.getTeamA_score(), matchup.getTeamB_score());
        results.updateResult(matchups.getMatchupRepository().findById(id).get());
    }

    @PutMapping("/filldefaultgroupmatchups")
    public void saveDefaultGroupMatchups() {
        matchups.saveDefaultGroupMatchups();
        results.updateAllResults(matchups.getMatchupRepository());
    }

    @PutMapping("/createphaseone")
    public void createPhase1Matchups() {
        matchups.createPhase1Matchups(results.getResultRepository());
    }

    @PutMapping("/filldefaultphaseonematchups")
    public void saveDefaultPhase1Matchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_1);
    }

    @PutMapping("/createquarterfinals")
    public void createQuarterfinalMatchups() {
        matchups.createPhase2AndHigherMatchups(MatchupType.PHASE_1, MatchupType.PHASE_2);
    }

    @PutMapping("/filldefaultquarterfinals")
    public void saveDefaultQuarterfinalMatchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_2);
    }

    @PutMapping("/createsemifinals")
    public void createSemifinalMatchups() {
        matchups.createPhase2AndHigherMatchups(MatchupType.PHASE_2, MatchupType.PHASE_3);
    }

    @PutMapping("/filldefaultsemifinals")
    public void saveDefaultSemifinalMatchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_3);
    }

    @PutMapping("/createfinal")
    public void createFinalMatchup() {
        matchups.createPhase2AndHigherMatchups(MatchupType.PHASE_3, MatchupType.PHASE_4);
    }

    @PutMapping("/filldefaultfinal")
    public void saveDefaultFinalMatchup() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_4);
    }

}
