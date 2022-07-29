package de.uniks.pioneers.computation;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.service.PioneersService;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class RoadAndFleet {

    @Inject
    public RoadAndFleet() {

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void calculateLongestRoad(PioneersService pioneersService, String gameId, String userId,
                                     ImageView longestRoadIconDisplay) {
        pioneersService.findAllPlayers(gameId).observeOn(FX_SCHEDULER)
                .subscribe(e -> {
                    HashMap<String, String> userAndGameId = new HashMap<>();
                    HashMap<HashMap<String, String>, Integer> maxValue = new HashMap<>();
                    for (Player player : e) {
                        if (player.longestRoad() != null) {
                            userAndGameId.put(player.gameId(), player.userId());
                            maxValue.put(userAndGameId, (Integer) player.longestRoad());
                        }
                    }

                    //shows the icon for longest Road
                    String imageLocation = "view/assets/longestRoadIcon.png";
                    showIcon(maxValue, userAndGameId, gameId, userId, longestRoadIconDisplay, imageLocation);
                });

    }

    private void showIcon(HashMap<HashMap<String, String>, Integer> maxValue, HashMap<String, String> userAndGameId,
                          String gameId, String userId, ImageView IconDisplay, String imageLocation) {
        if (!maxValue.isEmpty()) {
            int largestValue = Collections.max(maxValue.values());
            if ((Collections.frequency(maxValue.values(), largestValue)) == 1) {
                if (userAndGameId.get(gameId).equals(userId)) {
                    if (maxValue.get(userAndGameId).equals(largestValue)) {
                        IconDisplay.setImage(new Image(String.valueOf(Main.class.getResource(imageLocation))));
                    }
                }

            } else {
                IconDisplay.disableProperty().set(true);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void calculateLargestFleet(PioneersService pioneersService, String gameStorageId, String idStorageId, ImageView largestFleetIconDisplay) {
        pioneersService.findAllPlayers(gameStorageId).observeOn(FX_SCHEDULER)
                .subscribe(e -> {
                    HashMap<String, String> userAndGameId = new HashMap<>();
                    HashMap<HashMap<String, String>, Integer> maxValue = new HashMap<>();
                    for (Player player : e) {
                        if (player.developmentCards() != null) {
                            if (player.developmentCards().size() != 0) {
                                //needs to count development cards of every player
                                int count = 0;
                                for (DevelopmentCard d : player.developmentCards()) {
                                    if (d.type().equals("knight") && d.revealed() && !d.locked()) {
                                        count++;
                                    }
                                }
                                //Only put values in hashmap if player has at least one Knight cards
                                if (count != 0 && count >=5) {
                                    userAndGameId.put(player.gameId(), player.userId());
                                    maxValue.put(userAndGameId, count);
                                }
                            }
                        }
                    }

                    String imageLocation = "view/assets/largestFleetIcon.png";
                    //shows the icon for largestFleet
                    showIcon(maxValue, userAndGameId, gameStorageId, idStorageId, largestFleetIconDisplay, imageLocation);
                });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void showTotalDevelopmentCards(PioneersService pioneersService, String gameId, String userId, Label developmentCardsLabel) {
        pioneersService.findOnePlayer(gameId, userId).observeOn(FX_SCHEDULER)
                .subscribe(e -> {
                    if (e.developmentCards() != null) {
                        developmentCardsLabel.setText("Development cards: " + e.developmentCards().size());
                    }
                });
    }
}
