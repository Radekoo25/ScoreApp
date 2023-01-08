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
import pl.radeko.scoreapp.manager.TournamentManager;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.entity.Tournament;

import javax.validation.Valid;
import java.io.IOException;


@Controller
@RequestMapping("/api/teams/")
public class TeamApi {

    private final TeamManager teams;
    private final ResultManager results;
    private final MatchupManager matchups;
    private final TournamentManager tournaments;

    @Autowired
    public TeamApi(TeamManager teams, ResultManager results, MatchupManager matchups, TournamentManager tournaments) {

        this.teams = teams;
        this.results = results;
        this.matchups = matchups;
        this.tournaments = tournaments;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("teams", teams.findAll());
        model.addAttribute("tournaments", tournaments.findAll());
        return "/teams/teams_index";
    }

    @GetMapping("/index/{id}")
    public String homeTournament(@PathVariable Long id, Model model) {
        model.addAttribute("teams", teams.findTeamByTournamentId(id));
        model.addAttribute("tournaments", tournaments.findAll());
        model.addAttribute("tournament_id", tournaments.getTournamentRepository().findById(id).get().getId());
        model.addAttribute("tournament_name", tournaments.getTournamentRepository().findById(id).get().getName());
        return "/teams/teams_index_tournament";
    }

    @GetMapping("/error")
    public String error(@ModelAttribute("error") int error) {

        return "error";
    }

    @GetMapping("/team/add/{id}")
    public String prepareNewTeam(@PathVariable Long id, Model model) {
        model.addAttribute("team", new Team());
        model.addAttribute("tournament_id", id);
        return "/teams/addNewTeam";
    }

    @PostMapping("/team/add/{id}")
    public RedirectView addTeam(@PathVariable Long id, @ModelAttribute("team") @Valid Team team, Model model) {
        model.addAttribute("tournament_id", id);
        if(teams.save(team, id)) {
            return new RedirectView("/api/teams/index/"+id);
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

    @PostMapping("/filldefault/{id}")
    public RedirectView saveDefaultTeams(@PathVariable Long id) {
        if(teams.saveDefaultTeams(id)) {
            return new RedirectView("/api/teams/index/"+id);
        }
        else {
            RedirectView redirectView = new RedirectView("/api/teams/error");
            redirectView.addStaticAttribute("error", 100);
            return redirectView;
        }
    }

    @PostMapping("/drawgroups/{id}")
    public RedirectView drawGroups(@PathVariable Long id) {
        int condition = teams.drawGroups(id);
        if(condition == 0) {
            results.createGroupResults(teams.getTeamRepository(), id);
            matchups.createGroupMatchups(teams.getTeamRepository(), id);
            return new RedirectView("/api/teams/index/"+id);
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