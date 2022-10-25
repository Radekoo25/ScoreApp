package pl.radeko.scoreapp.manager;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.radeko.scoreapp.repository.entity.Team;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class TeamManagerTest {

    @Test
    void findAll() {
        //given
        TeamManager teamManager = mock(TeamManager.class);
        when(teamManager.findAll()).thenReturn(preparerMockData());
        //when
        List<Team> teams = (List<Team>) teamManager.findAll();
        //then
        MatcherAssert.assertThat(teams, Matchers.hasSize(10));
    }

    @Test
    void save() {

    }

    @Test
    void uploadTeamPhoto() {
    }

    @Test
    void updateTeamDescription() {
    }

    @Test
    void saveDefaultTeams() {
    }

    @Test
    void drawGroups() {
    }

    private List<Team> preparerMockData() {
        List<Team> teams = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            String teamName = "Drużyna" + i;
            teams.add(new Team(teamName,"--Brak--"));
        }
        return teams;
    }
}