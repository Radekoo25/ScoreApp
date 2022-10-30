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
import pl.radeko.scoreapp.repository.entity.Team;

import javax.validation.Valid;
import java.io.IOException;


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
        return "/teams/teams_index";
    }

    @GetMapping("/error")
    public String error(@ModelAttribute("error") int error) {

        return "error";
    }

    @GetMapping("/team/add")
    public String prepareNewTeam(Model model) {

        model.addAttribute("team", new Team());
        return "/teams/addNewTeam";
    }

    @PostMapping("/team/add")
    public RedirectView addTeam(@ModelAttribute("team") @Valid Team team) {

        if(teams.save(team)) {
            return new RedirectView("/api/teams/index");
        }
        else {
            RedirectView redirectView = new RedirectView("/api/teams/error");
            redirectView.addStaticAttribute("error", 100);
            return redirectView;
        }
    }

    @GetMapping("/team/update/description/{id}")
    public String prepareTeamForUpdateDescription(@PathVariable Long id, Model model) {

        model.addAttribute("team", teams.findTeamById(id).get());
        return "/teams/updateTeamDescription";
    }

    @PostMapping("/team/update/description/save/{id}")
    public RedirectView updateTeam(@PathVariable Long id, @ModelAttribute Team team) {

        teams.updateTeamDescription(id, team.getDescription());
        return new RedirectView("/api/teams/index");
    }

    @PostMapping("/filldefault")
    public RedirectView saveDefaultTeams() {

        if(teams.saveDefaultTeams()) {
            return new RedirectView("/api/teams/index");
        }
        else {
            RedirectView redirectView = new RedirectView("/api/teams/error");
            redirectView.addStaticAttribute("error", 100);
            return redirectView;
        }
    }

    @PostMapping("/drawgroups")
    public RedirectView drawGroups() {
        int condition = teams.drawGroups();
        if(condition == 0) {
            results.createGroupResults(teams.getTeamRepository());
            matchups.createGroupMatchups(teams.getTeamRepository());
            return new RedirectView("/api/teams/index");
        }
        else if (condition == 1) {
            RedirectView redirectView = new RedirectView("/api/teams/error");
            redirectView.addStaticAttribute("error", 110);
            return redirectView;
        }
        else {
            RedirectView redirectView = new RedirectView("/api/teams/error");
            redirectView.addStaticAttribute("error", 111);
            return redirectView;
        }
    }

    @GetMapping("photo/upload/{id}")
    public String prepareFileForUpload(@PathVariable Long id, Model model) {

        model.addAttribute("team", teams.findTeamById(id).get());
        return "/teams/uploadPhoto";
    }

    @PostMapping("photo/upload/save/{id}")
    public RedirectView UploadFile(@PathVariable Long id, @ModelAttribute Team team, @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {

        teams.storeFile(id, file);
        return new RedirectView("/api/teams/index");
    }
}