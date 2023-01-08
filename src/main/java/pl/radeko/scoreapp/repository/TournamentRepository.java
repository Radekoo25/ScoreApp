package pl.radeko.scoreapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.entity.Tournament;
import pl.radeko.scoreapp.repository.enums.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends CrudRepository<Tournament, Long> {
    List<Tournament> findAll();
    void deleteById(Long id);

}
