package pl.radeko.scoreapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.entity.Tournament;
import pl.radeko.scoreapp.repository.enums.Group;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {
    List<Team> findAllByGroup(Group group);
    List<Team> findAllByGroupAndTournamentId(Group group, Long id);
    List<Team> findAllByTournament(Tournament tournament);
    List<Team> findAllByTournamentId(Long in);
    void deleteAllByTournamentId(Long id);
}
