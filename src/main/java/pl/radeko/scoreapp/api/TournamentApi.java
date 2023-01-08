package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.manager.ResultManager;
import pl.radeko.scoreapp.manager.TeamManager;
import pl.radeko.scoreapp.manager.TournamentManager;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.entity.Tournament;

import javax.validation.Valid;


@Controller
@RequestMapping("/api/tournaments/")

public class TournamentApi {

    private final TournamentManager tournaments;
    private final MatchupManager matchups;
    private final TeamManager teams;
    private final ResultManager results;

    @Autowired
    public TournamentApi(TournamentManager tournaments, MatchupManager matchups, TeamManager teams, ResultManager results) {

        this.tournaments = tournaments;
        this.matchups = matchups;
        this.teams = teams;
        this.results = results;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("tournaments", tournaments.findAll());
        return "/tournaments/tournaments_index";
    }

    @GetMapping("/tournament/add")
    public String prepareNewTournament(Model model) {

        model.addAttribute("tournament", new Tournament());
        return "/tournaments/addNewTournament";
    }

    @PostMapping("/tournament/add")
    public RedirectView addTeam(@ModelAttribute("tournament") Tournament tournament) {
        tournaments.save(tournament);
        return new RedirectView("/api/tournaments/index");
    }

    @GetMapping("/delete/{id}")
    public RedirectView deleteTournament(@PathVariable Long id) {
            matchups.deleteByTournamentId(id);
            results.deleteByTournamentId(id);
            teams.deleteByTournamentId(id);
            tournaments.delete(id);
            return new RedirectView("/api/tournaments/index");
    }

    @GetMapping("/update/name/{id}")
    public String prepareTeamForUpdateDescription(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournaments.findTournament(id));
        return "/tournaments/updateTournamentName";
    }

    @PostMapping("/update/name/save/{id}")
    public RedirectView updateTeam(@PathVariable Long id, @ModelAttribute Tournament tournament) {
        tournaments.updateTournamentName(id, tournament.getName());
        return new RedirectView("/api/tournaments/index");
    }
}
