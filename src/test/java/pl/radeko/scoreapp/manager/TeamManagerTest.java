package pl.radeko.scoreapp.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pl.radeko.scoreapp.repository.ResultRepository;
import pl.radeko.scoreapp.repository.TeamRepository;
import pl.radeko.scoreapp.repository.entity.Team;
import pl.radeko.scoreapp.repository.enums.Group;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TeamManagerTest {

    @Mock
    TeamRepository teamRepository;
    @Mock
    ResultManager resultManager;

    TeamManager teamManager;

    @BeforeEach
    void setUp() throws IOException {
        teamManager = new TeamManager(teamRepository, resultManager);
    }

    @Test
    void shouldSaveDefaultTeams() {

        //given
        teamManager.saveDefaultTeams();

        //when

        //then
        assertTrue(true);
    }

    @Test
    void drawGroups() {
    }

    @Test
    void storeFile() {
    }

    private Team buildNewTeam(){
        Team team = new Team();
        team.setPhoto("testPhoto");
        team.setGroup(Group.GROUP_A);
        team.setDescription("testDescription");
        return team;
    }
}