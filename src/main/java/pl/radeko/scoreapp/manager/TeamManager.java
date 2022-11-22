package pl.radeko.scoreapp.manager;

/**
 * All services to work with team repository.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Matchup;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TeamManager {

    @Value("${scoreapp.sizeOfGroup}")
    public int sizeOfGroup;
    @Value("${scoreapp.numberOfTeams}")
    public int numberOfTeams;
    private final String UPLOAD_DIR = "src/main/resources/static/photos";
    private TeamRepository teamRepository;
    private ResultManager resultManager;
    private final Path fileStorageLocation;

    @Autowired
    public TeamManager(TeamRepository teamRepository, ResultManager resultManager) throws IOException {
        this.teamRepository = teamRepository;
        this.resultManager = resultManager;
        this.fileStorageLocation = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
        Files.createDirectories(this.fileStorageLocation);
    }

    public TeamRepository getTeamRepository() {
        return teamRepository;
    }

    public Iterable<Team> findAll() {
        return teamRepository.findAll();
    }

    public Optional<Team> findTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public boolean save(Team team) {

        if (teamRepository.count() < numberOfTeams) {
            team.setPhoto("Logo.png");
            teamRepository.save(team);
            return true;
        }
        else {
            return false;
        }
    }

    public void updateTeamDescription(Long id, String description) {
        teamRepository.findById(id).get().setDescription(description);
        teamRepository.save(teamRepository.findById(id).get());
    }

    /**
     * A function for filling the base with default teams.
     * It does not overwrite already created teams.
     */
    public boolean saveDefaultTeams() {

        if (teamRepository.count() < numberOfTeams) {
            for (int i = (int) teamRepository.count() + 1; i <= numberOfTeams; i++) {
                String teamName = "Drużyna" + i;
                Team team = new Team(teamName, "--Brak--");
                team.setPhoto("Logo.png");
                teamRepository.save(team);
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * A function used to draw teams to individual groups.
     * Overwrites the Group field for all teams in the base.
     */
    public int drawGroups() {
        if (teamRepository.count() == numberOfTeams && resultManager.getResultRepository().count() == 0) {

            List<Team> teams = StreamSupport.stream(teamRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            List<Team> newListOrder = shuffleList(teams);
            updateTeamGroup(newListOrder);
            return 0;
        }
        else if (teamRepository.count() < numberOfTeams) {
            return 1;
        }
        else {
            return 2;
        }
    }

    /**
     * A function for shuffling elements in the list given at the input.
     * @param teams Should be a List of all teams in database.
     */
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

    /**
     * A function for getting file extension.
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String[] fileNameParts = fileName.split("\\.");

        return fileNameParts[fileNameParts.length - 1];
    }

    /**
     * A function for saving downloaded photo in file system. Location of directory is given in final String, UPLOAD_DIR.
     */
    public void storeFile(Long id, MultipartFile file) throws IOException {

        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
            String fileName = teamRepository.findById(id).get().getId().toString() + "team_photo." + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            teamRepository.findById(id).get().setPhoto(fileName);
            teamRepository.save(teamRepository.findById(id).get());
        }
    }
}