package pl.radeko.scoreapp.manager;

/**
 * All services to work with matchups repository.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.radeko.scoreapp.repository.MatchupRepository;
import pl.radeko.scoreapp.repository.ResultRepository;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MatchupManager {

    @Value("${scoreapp.sizeOfGroup}")
    public int sizeOfGroup;
    @Value("${scoreapp.numberOfTeams}")
    public int numberOfTeams;
    private MatchupRepository matchupRepository;
    private ResultManager resultManager;
    final private int max = 5;
    final private int min = 0;
    final private int groupMatchups = 48;
    final private int phase1Matchups = 8;
    final private int phase2Matchups = 4;
    final private int phase3Matchups = 2;
    final private int phase4Matchups = 1;

    @Autowired
    public MatchupManager(MatchupRepository matchupRepository, ResultManager resultManager) {

        this.matchupRepository = matchupRepository;
        this.resultManager = resultManager;
    }

    private void save(Matchup matchup) {

        matchupRepository.save(matchup);
        resultManager.updateResult(matchup);
        Long id = matchup.getTeamA().getTournament().getId();
        checkIfPhaseIsComplete(resultManager.getResultRepository(), id);
    }

    public void deleteByTournamentId(Long id) {
        matchupRepository.deleteAllByTeamATournamentId(id);
    }

    public Iterable<Matchup> findAll() {
        return matchupRepository.findAll();
    }

    public Iterable<Matchup> findAllByMatchupTypeAndTournament(MatchupType matchupType, Long id) {
        return matchupRepository.findAllByMatchupTypeAndAndTeamATournamentId(matchupType, id);
    }

    public Optional<Matchup> findMatchupById(Long id) {
        return matchupRepository.findById(id);
    }

    /**
     * Function set scores of both team of given matchup.
     * Inequality prevents the result from overwriting an already entered result.
     */
    public int updateMatchup(Long id, int teamA_score, int teamB_score) {

        Matchup temp = matchupRepository.findById(id).get();
        boolean isDrawInKnockoutStage = (temp.getMatchupType().ordinal() >= MatchupType.PHASE_1.ordinal() &&
                teamA_score == teamB_score);

        if(temp.getTeamA_score() < 0) {
            if(isDrawInKnockoutStage) {
                return 1;
            }
                temp.setTeamA_score(teamA_score);
                temp.setTeamB_score(teamB_score);
                save(temp);
                return 0;
        }
        return 2;
    }

    /**
     * The function fills in matchups with random values.
     * We have a mechanism here to prevent draws in the knockout stages.
     */
    public void saveDefaultMatchups(Long id) {

        Random random = new Random();

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAllByTeamATournamentId(id).spliterator(), false)
                .collect(Collectors.toList());

        matchups.stream().forEach(t -> {
            if (t.getTeamA_score() < 0) {

                int scoreA = random.nextInt(max - min) + min;
                int scoreB = random.nextInt(max - min) + min;

                if (scoreA == scoreB && t.getMatchupType().ordinal() >= MatchupType.PHASE_1.ordinal()) {
                    scoreA++;
                }

                t.setTeamA_score(scoreA);
                t.setTeamB_score(scoreB);
                save(t);
            }
        });
    }

    /**
     * This feature is responsible for creating empty group matchups.
     */
    public void createGroupMatchups(TeamRepository teamRepository, Long id) {

        if (teamRepository.findAllByTournamentId(id).size() == numberOfTeams && matchupRepository.findAllByTeamATournamentId(id).size() == 0) {
            Group[] groups = Group.values();
            MatchupType[] matchupTypes = MatchupType.values();

            for (int k = 0; k < numberOfTeams / sizeOfGroup; k++) {

                List<Team> teams = teamRepository.findAllByGroupAndTournamentId(groups[k], id);

                for (int i = 0; i <= sizeOfGroup; i++) {
                    for (int j = i + 1; j < sizeOfGroup; j++) {
                        Team teamA = teams.get(i);
                        Team teamB = teams.get(j);

                        Matchup temp = new Matchup(matchupTypes[k], teamA, teamB);
                        matchupRepository.save(temp);
                    }
                }
            }
        }
    }

    /**
     * This feature is responsible for creating empty phase1 matchups.
     * Based on group results.
     */
    public void createPhase1Matchups(ResultRepository resultRepository, Long id) {

        int currentGroup = 0;
        Group[] groups = Group.values();

        for (int i = 0; i < 4; i++) {

            Matchup temp1 = new Matchup();
            temp1.setMatchupType(MatchupType.PHASE_1);
            temp1.setTeamA_score(-1);
            temp1.setTeamB_score(-1);
            temp1.setTeamA(resultRepository.findByGroupAndPlaceAndTeamTournamentId(groups[currentGroup], 1, id).getTeam());
            temp1.setTeamB(resultRepository.findByGroupAndPlaceAndTeamTournamentId(groups[7 - currentGroup], 2, id).getTeam());
            matchupRepository.save(temp1);

            Matchup temp2 = new Matchup();
            temp2.setMatchupType(MatchupType.PHASE_1);
            temp2.setTeamA_score(-1);
            temp2.setTeamB_score(-1);
            temp2.setTeamA(resultRepository.findByGroupAndPlaceAndTeamTournamentId(groups[currentGroup], 2, id).getTeam());
            temp2.setTeamB(resultRepository.findByGroupAndPlaceAndTeamTournamentId(groups[7 - currentGroup], 1, id).getTeam());
            matchupRepository.save(temp2);

            currentGroup++;
        }
    }

    /**
     * This function is responsible for creating empty phase2 and higher matchups.
     * Based on rules given in the recruitment task.
     */
    public void createPhase2AndHigherMatchups(MatchupType previousPhase, MatchupType currentPhase, Long id) {

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAllByMatchupTypeAndAndTeamATournamentId(previousPhase, id).spliterator(), false)
                .collect(Collectors.toList());

        for (int i = 0; i < matchups.size() / 2; i++) {

            Matchup temp = new Matchup();
            Team teamA1 = (matchups.get(i).getTeamA_score() > matchups.get(i).getTeamB_score()) ?
                    matchups.get(i).getTeamA() : matchups.get(i).getTeamB();
            Team teamB1 = (matchups.get(matchups.size() - i - 1).getTeamA_score() > matchups.get(matchups.size() - i - 1).getTeamB_score()) ?
                    matchups.get(matchups.size() - i - 1).getTeamA() : matchups.get(matchups.size() - i - 1).getTeamB();

            temp.setMatchupType(currentPhase);
            temp.setTeamA_score(-1);
            temp.setTeamB_score(-1);

            temp.setTeamA(teamA1);
            temp.setTeamB(teamB1);
            matchupRepository.save(temp);
        }
    }

    /**
     * The function checks if any phase of the tournament has been completed.
     * Both group and knockout stages.
     */
    private void checkIfPhaseIsComplete(ResultRepository resultRepository, Long id) {

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAllByTeamATournamentId(id).spliterator(), false)
                .collect(Collectors.toList());
        int numberOfCompletedMatchups = 0;

        for (int i=0; i<matchups.size(); i++) {
            if (matchups.get(i).getTeamA_score() >= 0) {
                numberOfCompletedMatchups++;
            }
        }

        if(numberOfCompletedMatchups == groupMatchups ) {
            createPhase1Matchups(resultRepository, id);
        }
        else if(numberOfCompletedMatchups == groupMatchups + phase1Matchups) {
            createPhase2AndHigherMatchups(MatchupType.PHASE_1, MatchupType.PHASE_2, id);
        }
        else if(numberOfCompletedMatchups == groupMatchups + phase1Matchups + phase2Matchups) {
            createPhase2AndHigherMatchups(MatchupType.PHASE_2, MatchupType.PHASE_3, id);
        }
        else if(numberOfCompletedMatchups == groupMatchups + phase1Matchups + phase2Matchups + phase3Matchups) {
            createPhase2AndHigherMatchups(MatchupType.PHASE_3, MatchupType.PHASE_4, id);
        }
    }
}

