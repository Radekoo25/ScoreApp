package pl.radeko.scoreapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.radeko.scoreapp.repository.entity.Result;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;

import java.util.List;

@Repository
public interface ResultRepository extends CrudRepository<Result, Long> {
    List<Result> findAllByGroup(Group group);
    Result findByGroupAndPlace(Group group, int place);

}
