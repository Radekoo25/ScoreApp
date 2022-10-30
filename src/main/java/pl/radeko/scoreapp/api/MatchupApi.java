package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.enums.MatchupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/matchups")
public class MatchupApi {

    private final MatchupManager matchups;

    @Autowired
    public MatchupApi(MatchupManager matchups) {
        this.matchups = matchups;
    }

    @GetMapping("/index")
    public String home(Model model) {

        model.addAttribute("group_a", matchups.findAllByMatchupType(MatchupType.GROUP_A));
        model.addAttribute("group_b", matchups.findAllByMatchupType(MatchupType.GROUP_B));
        model.addAttribute("group_c", matchups.findAllByMatchupType(MatchupType.GROUP_C));
        model.addAttribute("group_d", matchups.findAllByMatchupType(MatchupType.GROUP_D));
        model.addAttribute("group_e", matchups.findAllByMatchupType(MatchupType.GROUP_E));
        model.addAttribute("group_f", matchups.findAllByMatchupType(MatchupType.GROUP_F));
        model.addAttribute("group_g", matchups.findAllByMatchupType(MatchupType.GROUP_G));
        model.addAttribute("group_h", matchups.findAllByMatchupType(MatchupType.GROUP_H));

        model.addAttribute("phase_1", matchups.findAllByMatchupType(MatchupType.PHASE_1));
        model.addAttribute("phase_2", matchups.findAllByMatchupType(MatchupType.PHASE_2));
        model.addAttribute("phase_3", matchups.findAllByMatchupType(MatchupType.PHASE_3));
        model.addAttribute("phase_4", matchups.findAllByMatchupType(MatchupType.PHASE_4));

        return "/matchups/matchups_index";
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

    @PostMapping("/filldefaultmatchups")
    public RedirectView saveDefaultGroupMatchups() {
        matchups.saveDefaultMatchups();
        return new RedirectView("/api/matchups/index");
    }
}
