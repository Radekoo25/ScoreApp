package pl.radeko.scoreapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.radeko.scoreapp.repository.entity.Result;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;

import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {
    List<Result> findAllByOrderByPointsDescGoalsScoredDescGoalsLostAsc();
    List<Result> findAllByGroupOrderByPlace(Group group);
    List<Result> findAllByGroupAndTeamTournamentId(Group group, Long id);
    List<Result> findAllByGroupAndAndTeamTournamentIdOrderByPlace(Group group, Long id);
    Result findByGroupAndPlaceAndTeamTournamentId(Group group, int place, Long id);
    List<Result> findAllByTeamTournamentId(Long id);
    Result findByTeamId(Long id);
    void deleteAllByTeamTournamentId(Long id);
}
