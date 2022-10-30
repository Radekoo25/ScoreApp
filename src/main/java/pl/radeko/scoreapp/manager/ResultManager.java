package pl.radeko.scoreapp.manager;

/**
 * All services to work with results repository.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.radeko.scoreapp.repository.MatchupRepository;
import pl.radeko.scoreapp.repository.ResultRepository;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Result;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ResultManager {

    @Value("${scoreapp.numberOfTeams}")
    public int numberOfTeams;
    @Value("${scoreapp.sizeOfGroup}")
    public int sizeOfGroup;
    private ResultRepository resultRepository;

    @Autowired
    public ResultManager(ResultRepository resultRepository) {

        this.resultRepository = resultRepository;
    }

    public ResultRepository getResultRepository() {
        return resultRepository;
    }

    public Iterable<Result> findAllByGroup(Group group) {

        return resultRepository.findAllByGroupOrderByPlace(group);
    }

    /**
     * Simple function to check, witch team won match.
     * Gives 3 points for win, 2 for draw and 0 for lost.
     */
    private int checkWin(int goalRatio) {
        if(goalRatio>0) {
            return 3;
        } else if (goalRatio == 0) {
            return 2;
        }
        else {
            return 0;
        }
    }

    /**
     * Creates related records in the Results database from the given team database.
     * All teams must be created.
     */
    public void createGroupResults(TeamRepository teamRepository) {

        if(teamRepository.count() == numberOfTeams && resultRepository.count() == 0) {
            List<Team> teams = StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());

            teams.stream().forEach(t -> {
                Result temp = new Result();
                temp.setTeam(t);
                temp.setGroup(t.getGroup());
                save(temp);
            });
        }
    }
    /**
     * Updating group results for two teams from given matchup.
     */
    public void updateResult(Matchup matchup) {

        Group[] groups = Group.values();

        if(matchup.getMatchupType().ordinal() < MatchupType.PHASE_1.ordinal())
        {
            Result tempA = new Result();
            Result tempB = new Result();

            tempA = updateResultA(matchup);
            tempB = updateResultB(matchup);

            save(tempA);
            save(tempB);

            setPlaceInGroup(tempA.getGroup());
        }
    }

    private void save(Result result) {

        resultRepository.save(result);
    }

    /**
     * Updates result of first team from given matchup.
     */
    private Result updateResultA(Matchup matchup) {

        Result temp = new Result();

        int goalRatioInMatchup = matchup.getTeamA_score()-matchup.getTeamB_score();
        int actualPoints = resultRepository.findById(matchup.getTeamA().getId()).get().getPoints();
        int actualGoalScored = resultRepository.findById(matchup.getTeamA().getId()).get().getGoals_scored();
        int actualGoalLost = resultRepository.findById(matchup.getTeamA().getId()).get().getGoals_lost();
        int actualLost = resultRepository.findById(matchup.getTeamA().getId()).get().getLost();
        int actualDraws = resultRepository.findById(matchup.getTeamA().getId()).get().getDraws();
        int actualWins = resultRepository.findById(matchup.getTeamA().getId()).get().getWins();

        temp.setId(matchup.getTeamA().getId());
        temp.setGroup(matchup.getTeamA().getGroup());
        temp.setTeam(matchup.getTeamA());
        temp.setWins(actualWins);
        temp.setDraws(actualDraws);
        temp.setLost(actualLost);
        temp.setPoints(checkWin(goalRatioInMatchup) + actualPoints);
        temp.setGoals_scored(matchup.getTeamA_score() + actualGoalScored);
        temp.setGoals_lost(matchup.getTeamB_score() + actualGoalLost);

        if(goalRatioInMatchup > 0) {
            temp.setWins(actualWins + 1);
        } else if (goalRatioInMatchup == 0) {
            temp.setDraws(actualDraws + 1);
        }
        else {
            temp.setLost(actualLost + 1);
        }

        return temp;
    }

    /**
     * Updates result of second team from given matchup.
     */
    private Result updateResultB(Matchup matchup) {

        Result temp = new Result();

        int goalRatioInMatchup = matchup.getTeamB_score()-matchup.getTeamA_score();
        int actualPoints = resultRepository.findById(matchup.getTeamB().getId()).get().getPoints();
        int actualGoalScored = resultRepository.findById(matchup.getTeamB().getId()).get().getGoals_scored();
        int actualGoalLost = resultRepository.findById(matchup.getTeamB().getId()).get().getGoals_lost();
        int actualLost = resultRepository.findById(matchup.getTeamB().getId()).get().getLost();
        int actualDraws = resultRepository.findById(matchup.getTeamB().getId()).get().getDraws();
        int actualWins = resultRepository.findById(matchup.getTeamB().getId()).get().getWins();

        temp.setId(matchup.getTeamB().getId());
        temp.setGroup(matchup.getTeamB().getGroup());
        temp.setTeam(matchup.getTeamB());
        temp.setWins(actualWins);
        temp.setDraws(actualDraws);
        temp.setLost(actualLost);
        temp.setPoints(checkWin(goalRatioInMatchup) + actualPoints);
        temp.setGoals_scored(matchup.getTeamB_score() + actualGoalScored);
        temp.setGoals_lost(matchup.getTeamA_score() + actualGoalLost);

        if(goalRatioInMatchup > 0) {
            temp.setWins(actualWins + 1);
        }
        else if (goalRatioInMatchup == 0) {
            temp.setDraws(actualDraws + 1);
        }
        else {
            temp.setLost(actualLost + 1);
        }

        return temp;
    }

    /**
     * Function allocating places for teams in the group specified at the entrance.
     * Places are allocated according to the rules given in the recruitment task.
     */
    private void setPlaceInGroup(Group group) {

        List<Result> results = StreamSupport.stream(resultRepository.findAllByGroup(group).spliterator(), false)
                .collect(Collectors.toList());

        Collections.sort(results, Comparator.comparing(Result::getPoints)
                .thenComparing(Result::getGoals_scored)
                .thenComparing(Result::getGoals_lost).reversed());

        resultRepository.findById(results.get(0).getId()).get().setPlace(1);
        resultRepository.findById(results.get(1).getId()).get().setPlace(2);
        resultRepository.findById(results.get(2).getId()).get().setPlace(3);
        resultRepository.findById(results.get(3).getId()).get().setPlace(4);

        resultRepository.saveAll(results);
    }
}
