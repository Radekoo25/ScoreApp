package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.manager.ResultManager;
import pl.radeko.scoreapp.manager.TeamManager;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Result;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/api/teams/")
public class TeamApi {

    private final TeamManager teams;
    private final ResultManager results;
    private final MatchupManager matchups;

    @Autowired
    public TeamApi(TeamManager teams, ResultManager results, MatchupManager matchups) {
        this.teams = teams;
        this.results = results;
        this.matchups = matchups;
    }

    @GetMapping("/index")
    public String home(Model model) {

        model.addAttribute("teams", teams.findAll());

        return "teams_index";
    }

    @PostMapping("/team/update/{id}")
    public String prepareMatchupForUpdate(@PathVariable Long id, Model model) {
        model.addAttribute("team", teams.findTeamById(id).get());
        return "updateTeam";
    }

    @PostMapping("/matchup/update/save/{id}")
    public RedirectView updateMatchup(@PathVariable Long id, @ModelAttribute("matchup") Matchup matchup) {
        matchups.updateMatchup(id, matchup.getTeamA_score(), matchup.getTeamB_score(), results.getResultRepository());
        results.updateResult(matchups.getMatchupRepository().findById(id).get());
        return new RedirectView("/api/matchups/index");
    }

    @GetMapping("/all")
    public Iterable<Team> getAll() {return teams.findAll();}

    @PostMapping("/addteam")
    public void addTeam(@RequestBody Team team) {teams.save(team);}

    @PutMapping("/filldefault")
    public void saveDefaultTeams() {teams.saveDefaultTeams();}

    @PutMapping("/drawgroups")
    public void drawGroups() {
        teams.drawGroups();
        results.createGroupResults(teams.getTeamRepository());
        matchups.createGroupMatchups(teams.getTeamRepository());
    }

    @PostMapping("/updatedescription/{id}")
    public void updateTeamDescription(@PathVariable Long id, @RequestParam String description) {
        teams.updateTeamDescription(id, description);
    }

    @PostMapping("/uploadphoto/{id}")
    public void uploadTeamPhoto(@PathVariable Long id, @RequestParam("File") MultipartFile file) throws IOException {
        teams.uploadTeamPhoto(id, file);
    }

    @GetMapping("/group/{id}")
    public Iterable<Result> getResultsByGroup(@PathVariable int id) {
        Group[] groups = Group.values();
        return results.findAllByGroup(groups[id]);
    }

    @GetMapping("/phase/{id}")
    public Iterable<Matchup> getMatchupsByType(@PathVariable int id) {
        MatchupType[] matchupTypes = MatchupType.values();
        return matchups.findAllByMatchupType(matchupTypes[id]);
    }
}
