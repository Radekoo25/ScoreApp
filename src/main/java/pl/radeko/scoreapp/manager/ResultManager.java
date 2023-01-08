package pl.radeko.scoreapp.manager;

/**
 * All services to work with results repository.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public Iterable<Result> findAll() {
        return resultRepository.findAllByOrderByPointsDescGoalsScoredDescGoalsLostAsc();
    }

    public ResultRepository getResultRepository() {
        return resultRepository;
    }

    public void deleteByTournamentId(Long id) {
        resultRepository.deleteAllByTeamTournamentId(id);
    }

    public Iterable<Result> findAllByGroupAndTournament(Group group, Long id) {

        return resultRepository.findAllByGroupAndAndTeamTournamentIdOrderByPlace(group, id);
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
    public void createGroupResults(TeamRepository teamRepository, Long id) {

        if(teamRepository.findAllByTournamentId(id).size() == numberOfTeams && resultRepository.findAllByTeamTournamentId(id).size() == 0) {
            List<Team> teams = StreamSupport.stream(teamRepository.findAllByTournamentId(id).spliterator(), false)
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

            updateResultA(matchup);
            updateResultB(matchup);

            setPlaceInGroup(matchup.getTeamA().getGroup(), matchup.getTeamA().getTournament().getId());
        }
    }

    private void save(Result result) {

        resultRepository.save(result);
    }

    /**
     * Updates result of first team from given matchup.
     */
    private void updateResultA(Matchup matchup) {
        Result result = resultRepository.findByTeamId(matchup.getTeamA().getId());

        int goalRatioInMatchup = matchup.getTeamA_score() - matchup.getTeamB_score();
        int actualPoints = result.getPoints();
        int actualGoalScored = result.getGoalsScored();
        int actualGoalLost = result.getGoalsLost();
        int actualLost = result.getLost();
        int actualDraws = result.getDraws();
        int actualWins = result.getWins();

        result.setPoints(checkWin(goalRatioInMatchup) + actualPoints);
        result.setGoalsScored(matchup.getTeamA_score() + actualGoalScored);
        result.setGoalsLost(matchup.getTeamB_score() + actualGoalLost);

        if (goalRatioInMatchup > 0) {
            result.setWins(actualWins + 1);
        } else if (goalRatioInMatchup == 0) {
            result.setDraws(actualDraws + 1);
        } else {
            result.setLost(actualLost + 1);
        }

        resultRepository.save(result);
    }

    /**
     * Updates result of second team from given matchup.
     */
    private void updateResultB(Matchup matchup) {
        Result result = resultRepository.findByTeamId(matchup.getTeamB().getId());

        int goalRatioInMatchup = matchup.getTeamB_score() - matchup.getTeamA_score();
        int actualPoints = result.getPoints();
        int actualGoalScored = result.getGoalsScored();
        int actualGoalLost = result.getGoalsLost();
        int actualLost = result.getLost();
        int actualDraws = result.getDraws();
        int actualWins = result.getWins();

        result.setPoints(checkWin(goalRatioInMatchup) + actualPoints);
        result.setGoalsScored(matchup.getTeamB_score() + actualGoalScored);
        result.setGoalsLost(matchup.getTeamA_score() + actualGoalLost);

        if (goalRatioInMatchup > 0) {
            result.setWins(actualWins + 1);
        } else if (goalRatioInMatchup == 0) {
            result.setDraws(actualDraws + 1);
        } else {
            result.setLost(actualLost + 1);
        }

        resultRepository.save(result);
    }

    /**
     * Function allocating places for teams in the group specified at the entrance.
     * Places are allocated according to the rules given in the recruitment task.
     */
    private void setPlaceInGroup(Group group, Long id) {

        List<Result> results = StreamSupport.stream(resultRepository.findAllByGroupAndTeamTournamentId(group, id).spliterator(), false)
                .collect(Collectors.toList());

        Collections.sort(results, Comparator.comparing(Result::getPoints)
                .thenComparing(Result::getGoalsScored)
                .thenComparing(Result::getGoalsLost).reversed());

        resultRepository.findById(results.get(0).getId()).get().setPlace(1);
        resultRepository.findById(results.get(1).getId()).get().setPlace(2);
        resultRepository.findById(results.get(2).getId()).get().setPlace(3);
        resultRepository.findById(results.get(3).getId()).get().setPlace(4);

        resultRepository.saveAll(results);
    }
}
