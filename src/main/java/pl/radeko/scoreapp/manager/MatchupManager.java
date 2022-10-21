package pl.radeko.scoreapp.manager;

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
    final private int max = 5;
    final private int min = 0;

    @Autowired
    public MatchupManager(MatchupRepository matchupRepository) {

        this.matchupRepository = matchupRepository;
    }

    public MatchupRepository getMatchupRepository() {
        return matchupRepository;
    }

    private void save(Matchup matchup) {

        matchupRepository.save(matchup);
    }

    public Iterable<Matchup> findAll() {
        return matchupRepository.findAll();
    }

    public Iterable<Matchup> findAllByMatchupType(MatchupType matchupType) {
        return matchupRepository.findAllByMatchupType(matchupType);
    }

    public void updateMatchup(Long id, int teamA_score, int teamB_score) {

        Matchup temp = matchupRepository.findById(id).get();
        temp.setTeamA_score(teamA_score);
        temp.setTeamB_score(teamB_score);

        save(temp);
    }

    public void saveDefaultGroupMatchups() {

        Random random = new Random();

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        matchups.stream().forEach(t -> {
            if (t.getTeamA_score() < 0) {

                t.setTeamA_score(random.nextInt(max - min) + min);
                t.setTeamB_score(random.nextInt(max - min) + min);
                save(t);
            }
        });
    }

    public void saveDefaultPhaseMatchups(MatchupType matchupType) {

        Random random = new Random();

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAllByMatchupType(matchupType).spliterator(), false)
                .collect(Collectors.toList());

        matchups.stream().forEach(t -> {
            if (t.getTeamA_score() < 0) {

                int scoreA = random.nextInt(max - min) + min;
                int scoreB = random.nextInt(max - min) + min;

                if (scoreA == scoreB) {
                    scoreA++;
                }

                t.setTeamA_score(scoreA);
                t.setTeamB_score(scoreB);
                save(t);
            }
        });
    }

    public void createGroupMatchups(TeamRepository teamRepository) {

        if (teamRepository.count() == numberOfTeams && matchupRepository.count() == 0) {
            Group[] groups = Group.values();
            MatchupType[] matchupTypes = MatchupType.values();

            for (int k = 0; k < numberOfTeams / sizeOfGroup; k++) {

                List<Team> teams = teamRepository.findAllByGroup(groups[k]);

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

    public void createPhase1Matchups(ResultRepository resultRepository) {

        int currentGroup = 0;
        Group[] groups = Group.values();

        for (int i = 0; i < 4; i++) {

            Matchup temp1 = new Matchup();
            temp1.setMatchupType(MatchupType.PHASE_1);
            temp1.setTeamA_score(-1);
            temp1.setTeamB_score(-1);
            temp1.setTeamA(resultRepository.findByGroupAndPlace(groups[currentGroup], 1).getTeam());
            temp1.setTeamB(resultRepository.findByGroupAndPlace(groups[7 - currentGroup], 2).getTeam());
            matchupRepository.save(temp1);

            Matchup temp2 = new Matchup();
            temp2.setMatchupType(MatchupType.PHASE_1);
            temp2.setTeamA_score(-1);
            temp2.setTeamB_score(-1);
            temp2.setTeamA(resultRepository.findByGroupAndPlace(groups[currentGroup], 2).getTeam());
            temp2.setTeamB(resultRepository.findByGroupAndPlace(groups[7 - currentGroup], 1).getTeam());
            matchupRepository.save(temp2);

            currentGroup++;
        }
    }

    public void createPhase2AndHigherMatchups(MatchupType previousPhase, MatchupType currentPhase) {

        List<Matchup> matchups = StreamSupport.stream(matchupRepository.findAllByMatchupType(previousPhase).spliterator(), false)
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
}

