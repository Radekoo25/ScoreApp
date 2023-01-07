package pl.radeko.scoreapp.manager;

import org.springframework.stereotype.Service;
import pl.radeko.scoreapp.repository.TournamentRepository;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.entity.Tournament;

@Service
public class TournamentManager {

    private TournamentRepository tournamentRepository;

    public TournamentManager(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public TournamentRepository getTournamentRepository() {
        return tournamentRepository;
    }

    public Iterable<Tournament> findAll() {
        return tournamentRepository.findAll();
    }

    public void delete(Long id) {
        tournamentRepository.deleteById(id);
    }

    public void save(Tournament tournament) {
        tournamentRepository.save(tournament);
    }
}
