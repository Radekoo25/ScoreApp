package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.manager.TournamentManager;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.enums.MatchupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/matchups")
public class MatchupApi {

    private final MatchupManager matchups;
    private final TournamentManager tournaments;

    @Autowired
    public MatchupApi(MatchupManager matchups, TournamentManager tournaments) {
        this.matchups = matchups;
        this.tournaments = tournaments;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("matchups", matchups.findAll());
        model.addAttribute("tournaments", tournaments.findAll());
        return "/matchups/matchups_index";
    }

    @GetMapping("/index/{id}")
    public String homeTournament(@PathVariable Long id, Model model) {
        model.addAttribute("tournaments", tournaments.findAll());
        model.addAttribute("tournament_id", tournaments.getTournamentRepository().findById(id).get().getId());
        model.addAttribute("tournament_name", tournaments.getTournamentRepository().findById(id).get().getName());

        model.addAttribute("group_a", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_A, id));
        model.addAttribute("group_b", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_B, id));
        model.addAttribute("group_c", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_C, id));
        model.addAttribute("group_d", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_D, id));
        model.addAttribute("group_e", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_E, id));
        model.addAttribute("group_f", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_F, id));
        model.addAttribute("group_g", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_G, id));
        model.addAttribute("group_h", matchups.findAllByMatchupTypeAndTournament(MatchupType.GROUP_H, id));

        model.addAttribute("phase_1", matchups.findAllByMatchupTypeAndTournament(MatchupType.PHASE_1, id));
        model.addAttribute("phase_2", matchups.findAllByMatchupTypeAndTournament(MatchupType.PHASE_2, id));
        model.addAttribute("phase_3", matchups.findAllByMatchupTypeAndTournament(MatchupType.PHASE_3, id));
        model.addAttribute("phase_4", matchups.findAllByMatchupTypeAndTournament(MatchupType.PHASE_4, id));

        return "/matchups/matchups_index_tournament";
    }

    @GetMapping("/error")
    public String error(@ModelAttribute("error") int error) {

        return "error";
    }

    @PostMapping("/matchup/update/{id}")
    public String prepareMatchupForUpdate(@PathVariable Long id, Model model) {
        model.addAttribute("matchup", matchups.findMatchupById(id).get());
        return "/matchups/updateMatchup";
    }

    @PostMapping("/matchup/update/save/{id}")
    public RedirectView updateMatchup(@PathVariable Long id, @ModelAttribute Matchup matchup) {
        int condition =  matchups.updateMatchup(id, matchup.getTeamA_score(), matchup.getTeamB_score());
        if(condition == 0) {
            return new RedirectView("/api/matchups/index");
        }
        else if (condition == 1) {
            RedirectView redirectView = new RedirectView("/api/matchups/error");
            redirectView.addStaticAttribute("error", 200);
            return redirectView;
        }
        else {
            RedirectView redirectView = new RedirectView("/api/matchups/error");
            redirectView.addStaticAttribute("error", 201);
            return redirectView;
        }
    }

    @PostMapping("/filldefaultmatchups/{id}")
    public RedirectView saveDefaultGroupMatchups(@PathVariable Long id) {
        matchups.saveDefaultMatchups(id);
        return new RedirectView("/api/matchups/index/"+id);
    }
}
