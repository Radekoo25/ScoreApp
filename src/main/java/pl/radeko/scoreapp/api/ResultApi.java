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

    @Autowired
    public ResultApi(ResultManager results) {
        this.results = results;
    }

    @GetMapping("/index")
    public String home(Model model) {

        model.addAttribute("group_a", results.findAllByGroup(Group.GROUP_A));
        model.addAttribute("group_b", results.findAllByGroup(Group.GROUP_B));
        model.addAttribute("group_c", results.findAllByGroup(Group.GROUP_C));
        model.addAttribute("group_d", results.findAllByGroup(Group.GROUP_D));
        model.addAttribute("group_e", results.findAllByGroup(Group.GROUP_E));
        model.addAttribute("group_f", results.findAllByGroup(Group.GROUP_F));
        model.addAttribute("group_g", results.findAllByGroup(Group.GROUP_G));
        model.addAttribute("group_h", results.findAllByGroup(Group.GROUP_H));

        return "/results/results_index";
    }
}