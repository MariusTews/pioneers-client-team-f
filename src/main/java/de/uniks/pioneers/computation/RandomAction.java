package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Point3D;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ClassCanBeRecord"})
public class RandomAction {

    private final GameStorage gameStorage;
    private final PioneersService pioneersService;

    public RandomAction(GameStorage gameStorage,
                        PioneersService pioneersService) {
        this.gameStorage = gameStorage;
        this.pioneersService = pioneersService;
    }

    public void calculateSettlement(String expectedMove) {
        //get all valid settlementPosition in dependence of map
        List<String> validPositions = getAllValidPositions();
        //get all invalid settlementPositions
        List<String> allInvalidSettlementCoordinates = getAllInvalidSettlementCoordinates();

        //remove invalid settlementPositions from validPositions
        for (String string : allInvalidSettlementCoordinates) {
            validPositions.remove(string);
        }

        int randomNumSettlement = (int) (Math.random() * (validPositions.size()));
        //select one settlementPosition from all valid settlementPositions
        String selectedSettlementPosition = validPositions.get(randomNumSettlement);
        // String to int for next method call
        int x = parseX(selectedSettlementPosition);
        int y = parseY(selectedSettlementPosition);
        int z = parseZ(selectedSettlementPosition);
        int side = parseSide(selectedSettlementPosition);

        //place chosen settlement and road
        pioneersService.move(gameStorage.getId(), expectedMove,
                        x, y, z, side,
                        "settlement", null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> calculateRoad(selectedSettlementPosition, "founding-road-"
                        + expectedMove.charAt(expectedMove.length()-1)));
    }

    public void calculateRoad(String lastBuildingPosition, String expectedMove) {
        // String to int from lastBuildingPlaced to calculate possible roadPlacements
        int x = parseX(lastBuildingPosition);
        int y = parseY(lastBuildingPosition);
        int z = parseZ(lastBuildingPosition);
        int side = parseSide(lastBuildingPosition);

        //get every possible roadPosition
        List<String> possibleRoadPlacements = getPossibleRoadPlacements(x, y, z, side);

        int randomNumRoad = (int) (Math.random() * possibleRoadPlacements.size());
        //select one possibleRoad
        String selectedRoadPosition = possibleRoadPlacements.get(randomNumRoad);
        // String to int for move call
        int xRoad = parseX(selectedRoadPosition);
        int yRoad = parseY(selectedRoadPosition);
        int zRoad = parseZ(selectedRoadPosition);
        int sideRoad = parseSide(selectedRoadPosition);

        //place chosen and road
        pioneersService.move(gameStorage.getId(), expectedMove, xRoad, yRoad, zRoad,
                        sideRoad, "road", null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    public List<String> getAllInvalidSettlementCoordinates() {
        List<Building> allBuildings = pioneersService.findAllBuildings(gameStorage.getId()).blockingFirst();
        List<String> allInvalidSettlementCoordinates = new ArrayList<>();
        for (Building building : allBuildings) {
            if (building.side().intValue() == 0) {
                //building itself
                allInvalidSettlementCoordinates.add("x" + building.x().toString() + "y" + building.y() + "z" + building.z() + "_" + building.side());
                //building place down left from current building
                allInvalidSettlementCoordinates.add("x" + building.x() + "y" + (building.y().intValue() + 1) + "z" + (building.z().intValue() - 1) + "_" + "6");
                //building place down right from current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() + 1) + "y" + building.y() + "z" + (building.z().intValue() - 1) + "_" + "6");
                //building place on top of current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() + 1) + "y" + (building.y().intValue() + 1) + "z" + (building.z().intValue() - 2) + "_" + "6");
            } else if (building.side().intValue() == 6) {
                //building itself
                allInvalidSettlementCoordinates.add("x" + building.x().toString() + "y" + building.y() + "z" + building.z() + "_" + building.side());
                //building place top left from current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() - 1) + "y" + building.y() + "z" + (building.z().intValue() + 1) + "_" + "0");
                //building place top left from current building
                allInvalidSettlementCoordinates.add("x" + building.x() + "y" + (building.y().intValue() - 1) + "z" + (building.z().intValue() + 1) + "_" + "0");
                //building place bottom of current building
                allInvalidSettlementCoordinates.add("x" + (building.x().intValue() - 1) + "y" + (building.y().intValue() - 1) + "z" + (building.z().intValue() + 2) + "_" + "0");
            }
        }
        return allInvalidSettlementCoordinates;
    }

    public List<String> getPossibleRoadPlacements(int x, int y, int z, int side) {
        List<String> possibleRoadPlacements = new ArrayList<>();

        //TODOs: get size from server in V3
        int gameFieldSize = 2;
        if (side == 0) {
            //road bottom left
            if (x != ((gameFieldSize + 1) * (-1))) {
                possibleRoadPlacements.add("x" + x + "y" + y + "z" + z + "_" + 11);
            }
            //road bottom right
            if (y != ((gameFieldSize + 1) * (-1))) {
                possibleRoadPlacements.add("x" + (x + 1) + "y" + y + "z" + (z - 1) + "_" + 7);
            }
            //road on top
            if (z != gameFieldSize * (-1)) {
                possibleRoadPlacements.add("x" + x + "y" + (y + 1) + "z" + (z - 1) + "_" + 3);
            }
        } else if (side == 6) {
            //road top left
            if (y != gameFieldSize + 1) {
                possibleRoadPlacements.add("x" + x + "y" + y + "z" + z + "_" + 7);
            }
            //road top right
            if (x != gameFieldSize + 1) {
                possibleRoadPlacements.add("x" + x + "y" + (y - 1) + "z" + (z + 1) + "_" + 11);
            }
            //road bottom
            if (z != gameFieldSize) {
                possibleRoadPlacements.add("x" + (x - 1) + "y" + y + "z" + (z + 1) + "_" + 3);
            }
        }
        return possibleRoadPlacements;
    }

    public List<String> getAllValidPositions() {
        //get current map
        Map map = pioneersService.findAllTiles(gameStorage.getId()).blockingFirst();
        List<String> allTileCoordinates = new ArrayList<>();
        List<String> allWaterTileCoordinates = new ArrayList<>();

        int gameFieldSize = this.gameStorage.getSize();

        for (Tile tile : map.tiles()) {
            allTileCoordinates.add("x" + tile.x().toString() + "y" + tile.y() + "z" + tile.z());
        }
        //top right fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + (gameFieldSize + 1) + "y" + 0 + "z" + ((gameFieldSize + 1) * (-1)));
        for (int i = 1; i <= gameFieldSize; i++) {
            //top right waterTile side
            allWaterTileCoordinates.add("x" + (gameFieldSize + 1) + "y" + (-i) + "z" + ((gameFieldSize + 1) * (-1) + i));
            //top waterTile side
            allWaterTileCoordinates.add("x" + (gameFieldSize + 1 - i) + "y" + i + "z" + ((gameFieldSize + 1) * (-1)));
        }
        //top left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + 0 + "y" + (gameFieldSize + 1) + "z" + ((gameFieldSize + 1) * (-1)));
        for (int i = 1; i <= gameFieldSize; i++) {
            //top left water side
            allWaterTileCoordinates.add("x" + (-i) + "y" + (gameFieldSize + 1) + "z" + ((gameFieldSize + 1) * (-1) + i));
        }
        //far left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + (gameFieldSize + 1) + "z" + 0);
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom left waterTile side
            allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + (gameFieldSize + 1 - i) + "z" + i);
        }
        //bottom left fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + ((gameFieldSize + 1) * (-1)) + "y" + 0 + "z" + (gameFieldSize + 1));
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom waterTile side
            allWaterTileCoordinates.add("x" + ((gameFieldSize + 1 - i) * (-1)) + "y" + (-i) + "z" + (gameFieldSize + 1));
        }
        //bottom right fixed waterTile it always appears in any map size
        allWaterTileCoordinates.add("x" + 0 + "y" + ((gameFieldSize + 1) * (-1)) + "z" + (gameFieldSize + 1));
        for (int i = 1; i <= gameFieldSize; i++) {
            //bottom right waterTile side
            allWaterTileCoordinates.add("x" + i + "y" + ((gameFieldSize + 1) * (-1)) + "z" + (gameFieldSize + 1 - i));
        }

        List<String> validPositions = new ArrayList<>();
        for (String string : allTileCoordinates) {
            validPositions.add(string + "_0");
            validPositions.add(string + "_6");
        }

        for (String string : allWaterTileCoordinates) {
            int z = Integer.parseInt(string.substring(string.indexOf("z") + 1));
            if (z < 0) {
                validPositions.add(string + "_6");
            } else if (z > 0) {
                validPositions.add(string + "_0");
            }
        }
        return validPositions;
    }

    public void automaticRob(String playerID) {
        this.pioneersService.findOneState(gameStorage.getId())
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    // safe current robber position
                    Point3D robberPos = move.robber();

                    // If no buildings are available, set rob on default
                    Point3D defaultPos = new Point3D(0, 0, 0);

                    this.pioneersService.findAllBuildings(gameStorage.getId())
                            .observeOn(FX_SCHEDULER)
                            .subscribe(buildings -> {
                                // Filter all buildings on the map, get the first building and set on tile
                                for (Building building : buildings) {
                                    if (!(building.type().equals(ROAD) || building.owner().equals(playerID))) {
                                        // only for shorter comparison
                                        Point3D newRobPos = new Point3D(building.x(), building.y(), building.z());
                                        if (!newRobPos.equals(robberPos)) {
                                            pioneersService.move(gameStorage.getId(), ROB_ACTION, building.x(),
                                                            building.y(), building.z(), null, null,
                                                            building.owner(), null)
                                                    .observeOn(FX_SCHEDULER)
                                                    .subscribe();
                                            return;
                                        }
                                    }
                                }

                                // when only buildings from the current user are available and the robber is not already
                                // on the default tile, set on default
                                if (robberPos == null || !robberPos.equals(defaultPos)) {
                                    pioneersService.move(gameStorage.getId(), ROB_ACTION, 0, 0, 0, null, null,
                                                    null, null)
                                            .observeOn(FX_SCHEDULER)
                                            .subscribe();
                                    return;
                                }
                                Building anyBuilding = buildings.get(0);
                                // if default is current robber's position -> other position
                                pioneersService.move(gameStorage.getId(), ROB_ACTION, anyBuilding.x(), anyBuilding.y(),
                                                anyBuilding.z(), null, null, null, null)
                                        .observeOn(FX_SCHEDULER)
                                        .subscribe();
                            });
                });
    }

    public void automaticDrop(HashMap<String, Integer> playerRes) {
        // check how much has to be dropped
        int total = 0;
        for (int amount : playerRes.values()) {
            total += amount;
        }
        total = total / 2;
        // iterate through all resources and choose till the total amount is reached
        HashMap<String, Integer> discardMap = new HashMap<>();
        int currentAmount = 0;
        for (String resource : playerRes.keySet()) {
            for (int i = 1; i < playerRes.get(resource) + 1; i++) {
                // count the amount of "chosen" resources and stop if enough resources are selected
                currentAmount += 1;
                if (currentAmount == total) {
                    discardMap.put(resource, i * (-1));
                    pioneersService.move(gameStorage.getId(), DROP_ACTION, null, null, null, null, null, null, discardMap)
                            .observeOn(FX_SCHEDULER)
                            .subscribe();
                    return;
                }
            }
            // put all the specific resource to the discardMap
            discardMap.put(resource, playerRes.get(resource) * (-1));
        }

        pioneersService.move(gameStorage.getId(), DROP_ACTION, null, null, null, null, null, null, discardMap)
                .observeOn(FX_SCHEDULER)
                .subscribe();
    }

    private int parseX (String parse) {
        return Integer.parseInt(parse.substring(parse.indexOf("x") + 1, parse.indexOf("y")));
    }
    private int parseY (String parse) {
       return Integer.parseInt(parse.substring(parse.indexOf("y") + 1, parse.indexOf("z")));
    }
    private int parseZ (String parse) {
        return Integer.parseInt(parse.substring(parse.indexOf("z") + 1, parse.indexOf("_")));
    }

    private int parseSide (String parse) {
        return Integer.parseInt(parse.substring(parse.indexOf("_") + 1));
    }
}
