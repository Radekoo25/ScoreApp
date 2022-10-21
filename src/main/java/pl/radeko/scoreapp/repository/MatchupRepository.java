package pl.radeko.scoreapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.enums.MatchupType;

import java.util.List;

@Repository
public interface MatchupRepository extends CrudRepository<Matchup, Long> {

    List<Matchup> findAllByMatchupType(MatchupType matchupType);
}
