package pl.radeko.scoreapp.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TeamManager {

    @Value("${scoreapp.sizeOfGroup}")
    public int sizeOfGroup;
    @Value("${scoreapp.numberOfTeams}")
    public int numberOfTeams;
    private TeamRepository teamRepository;

    @Autowired
    public TeamManager(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public TeamRepository getTeamRepository() {
        return teamRepository;
    }

    public Iterable<Team> findAll() {
        return teamRepository.findAll();
    }

    public void save(Team team) {

        if (teamRepository.count() <= numberOfTeams) {
            teamRepository.save(team);
        }
    }

    public void uploadTeamPhoto(Long id, MultipartFile file) throws IOException {

        teamRepository.findById(id).get().setPhoto(file.getBytes());
        teamRepository.save(teamRepository.findById(id).get());
    }

    public void updateTeamDescription(Long id, String description) {
        teamRepository.findById(id).get().setDescription(description);
        teamRepository.save(teamRepository.findById(id).get());
    }

    public void saveDefaultTeams() {

        for (int i = (int) teamRepository.count() + 1; i <= numberOfTeams; i++) {
            String teamName = "Drużyna" + i;
            Team team = new Team(teamName, "--Brak--");
            teamRepository.save(team);
        }
    }

    public void drawGroups() {
        if (teamRepository.count() == numberOfTeams) {

            List<Team> teams = StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            List<Team> newListOrder = shuffleList(teams);
            updateTeamGroup(newListOrder);
        }
    }

    private List<Team> shuffleList(List<Team> teams) {
        int currentGroup = -1;
        Group[] groups = Group.values();
        List<Team> shuffledTeams = new ArrayList<>(teams);

        Collections.shuffle(shuffledTeams);

        for (int i = 0; i < numberOfTeams; i++) {
            if (i % sizeOfGroup == 0)
                currentGroup++;
            shuffledTeams.get(i).setGroup(groups[currentGroup]);
        }
        return shuffledTeams;
    }

    private void updateTeamGroup(List<Team> teams) {
        teams.stream().forEach(t -> {
            Team temp = teamRepository.findById(t.getId()).get();
            temp.setGroup(t.getGroup());
            teamRepository.save(temp);
        });
    }
}
