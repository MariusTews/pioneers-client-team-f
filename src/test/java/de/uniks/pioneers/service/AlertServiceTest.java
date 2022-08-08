package de.uniks.pioneers.service;

import com.sun.javafx.application.PlatformImpl;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class AlertServiceTest {
    AlertService alertService = new AlertService();

    @Test
    public void hasKnightCardTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", false, false);
        DevelopmentCard d2 = new DevelopmentCard("victory-point", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", false, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);

        PlatformImpl.startup(() -> alertService.alertForEachCard(player, player_2));

        Assertions.assertNotEquals(devCards.size(), devCards_2.size());
    }

    @Test
    public void hasKnightTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", false, false);
        DevelopmentCard d2 = new DevelopmentCard("knight", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", false, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);

        PlatformImpl.startup(() -> alertService.alertForEachCard(player, player_2));

        Assertions.assertNotEquals(devCards.size(), devCards_2.size());
    }

    @Test
    public void hasRoadBuildingTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", false, false);
        DevelopmentCard d2 = new DevelopmentCard("road-building", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", false, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);

        PlatformImpl.startup(() -> alertService.alertForEachCard(player, player_2));

        Assertions.assertNotEquals(devCards.size(), devCards_2.size());
    }

    @Test
    public void hasYearOfPlentyTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", false, false);
        DevelopmentCard d2 = new DevelopmentCard("year-of-plenty", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", false, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);

        PlatformImpl.startup(() -> alertService.alertForEachCard(player, player_2));

        Assertions.assertNotEquals(devCards.size(), devCards_2.size());
    }

    @Test
    public void hasMonoPolyTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", false, false);
        DevelopmentCard d2 = new DevelopmentCard("monopoly", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", false, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);

        PlatformImpl.startup(() -> alertService.alertForEachCard(player, player_2));

        Assertions.assertNotEquals(devCards.size(), devCards_2.size());
    }
}
