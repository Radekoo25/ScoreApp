package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.radeko.scoreapp.manager.MatchupManager;
import pl.radeko.scoreapp.manager.ResultManager;
import pl.radeko.scoreapp.manager.TeamManager;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.enums.MatchupType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
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


        return "matchups_index";
    }

    @PostMapping("/matchup/update/{id}")
    public String prepareMatchupForUpdate(@PathVariable Long id, Model model) {
        model.addAttribute("matchup", matchups.findMatchupById(id).get());
        return "updateMatchup";
    }

    @PostMapping("/matchup/update/save/{id}")
    public RedirectView updateMatchup(@PathVariable Long id, @ModelAttribute("matchup") Matchup matchup) {
        matchups.updateMatchup(id, matchup.getTeamA_score(), matchup.getTeamB_score(), results.getResultRepository());
        results.updateResult(matchups.getMatchupRepository().findById(id).get());
        return new RedirectView("/api/matchups/index");
    }

    @PutMapping("/filldefaultgroupmatchups")
    public void saveDefaultGroupMatchups() {
        matchups.saveDefaultGroupMatchups(results.getResultRepository());
    }

    @PutMapping("/filldefaultphaseonematchups")
    public void saveDefaultPhase1Matchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_1, results.getResultRepository());
    }


    @PutMapping("/filldefaultquarterfinals")
    public void saveDefaultQuarterfinalMatchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_2, results.getResultRepository());
    }


    @PutMapping("/filldefaultsemifinals")
    public void saveDefaultSemifinalMatchups() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_3, results.getResultRepository());
    }


    @PutMapping("/filldefaultfinal")
    public void saveDefaultFinalMatchup() {
        matchups.saveDefaultPhaseMatchups(MatchupType.PHASE_4, results.getResultRepository());
    }

}
