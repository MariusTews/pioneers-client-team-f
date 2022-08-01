package de.uniks.pioneers.service;

import com.sun.javafx.application.PlatformImpl;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

public class AlertServiceTest {

    AlertService alertService = Mockito.mock(AlertService.class);

    @Test
    public void hasKnightCardTest() {
        List<DevelopmentCard> devCards = new ArrayList<>();
        DevelopmentCard d1 = new DevelopmentCard("knight", true, false);
        DevelopmentCard d2 = new DevelopmentCard("victory-point", true, false);
        devCards.add(d1);
        devCards.add(d2);

        Player player = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards);

        List<DevelopmentCard> devCards_2 = new ArrayList<>();
        DevelopmentCard d11 = new DevelopmentCard("knight", true, false);
        devCards_2.add(d11);

        Player player_2 = new Player("id", "3", "#223", true, 2, null, null,
                4, 5, null, devCards_2);


        PlatformImpl.startup(() -> {
            alertService.alertForEachCard(player, player_2);
            Mockito.verify(alertService, times(1)).alertForEachCard(player, player_2);
        });


    }
}
