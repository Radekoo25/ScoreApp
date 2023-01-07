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
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Result;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.io.IOException;

@Controller
@RequestMapping("/api/results/")
public class ResultApi {

    private final ResultManager results;
    private final TournamentManager tournaments;

    @Autowired
    public ResultApi(ResultManager results, TournamentManager tournaments) {
        this.results = results;
        this.tournaments = tournaments;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("tournaments", tournaments.findAll());

        model.addAttribute("results", results.findAll());

        return "/results/results_index";
    }

    @GetMapping("/index/{id}")
    public String homeTournament(@PathVariable Long id, Model model) {
        model.addAttribute("tournaments", tournaments.findAll());
        model.addAttribute("tournament_id", tournaments.getTournamentRepository().findById(id).get().getId());
        model.addAttribute("tournament_name", tournaments.getTournamentRepository().findById(id).get().getName());

        model.addAttribute("group_a", results.findAllByGroupAndTournament(Group.GROUP_A, id));
        model.addAttribute("group_b", results.findAllByGroupAndTournament(Group.GROUP_B, id));
        model.addAttribute("group_c", results.findAllByGroupAndTournament(Group.GROUP_C, id));
        model.addAttribute("group_d", results.findAllByGroupAndTournament(Group.GROUP_D, id));
        model.addAttribute("group_e", results.findAllByGroupAndTournament(Group.GROUP_E, id));
        model.addAttribute("group_f", results.findAllByGroupAndTournament(Group.GROUP_F, id));
        model.addAttribute("group_g", results.findAllByGroupAndTournament(Group.GROUP_G, id));
        model.addAttribute("group_h", results.findAllByGroupAndTournament(Group.GROUP_H, id));

        return "/results/results_index_tournament";
    }
}