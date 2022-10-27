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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/api/teams/")
public class TeamApi {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
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

    @GetMapping("/team/add")
    public String prepareNewTeam(Model model) {

        model.addAttribute("team", new Team());
        return "addNewTeam";
    }

    @PostMapping("/team/add")
    public RedirectView addTeam(@ModelAttribute Team team) {

        teams.save(team);
        return new RedirectView("/api/teams/index");
    }

    @PostMapping("/team/update/{id}")
    public String prepareTeamForUpdate(@PathVariable Long id, Model model) {

        model.addAttribute("team", teams.findTeamById(id).get());
        return "updateTeam";
    }

    @PostMapping("/team/update/save/{id}")
    public RedirectView updateTeam(@PathVariable Long id, @ModelAttribute Team team) {

        teams.updateTeamDescription(id, team.getDescription());
        return new RedirectView("/api/teams/index");
    }

    @PostMapping("/filldefault")
    public RedirectView saveDefaultTeams() {

        teams.saveDefaultTeams();
        return new RedirectView("/api/teams/index");
    }

    @PostMapping("/drawgroups")
    public RedirectView drawGroups() {

        teams.drawGroups();
        results.createGroupResults(teams.getTeamRepository());
        matchups.createGroupMatchups(teams.getTeamRepository());
        return new RedirectView("/api/teams/index");
    }

    @PostMapping("/updatedescription/{id}")
    public void updateTeamDescription(@PathVariable Long id, @RequestParam String description) {

        teams.updateTeamDescription(id, description);
    }

    @PostMapping("/uploadphoto/{id}")
    public void uploadTeamPhoto(@PathVariable Long id, @RequestParam MultipartFile file) throws IOException {
        teams.uploadTeamPhoto(id, file);
    }
}