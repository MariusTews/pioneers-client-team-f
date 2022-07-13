package de.uniks.pioneers.computation;

import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Map;
import de.uniks.pioneers.model.Tile;
import de.uniks.pioneers.service.GameStorage;
import de.uniks.pioneers.service.PioneersService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class RandomMove {

    private final GameStorage gameStorage;
    private final PioneersService pioneersService;

    public RandomMove(GameStorage gameStorage,
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
        int x = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("x") + 1, selectedSettlementPosition.indexOf("y")));
        int y = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("y") + 1, selectedSettlementPosition.indexOf("z")));
        int z = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("z") + 1, selectedSettlementPosition.indexOf("_")));
        int side = Integer.parseInt(selectedSettlementPosition.substring(selectedSettlementPosition.indexOf("_") + 1));

        //get every possible roadPosition in dependence of chosen settlementPosition
        List<String> possibleRoadPlacements = getPossibleRoadPlacements(x, y, z, side);

        int randomNumRoad = (int) (Math.random() * possibleRoadPlacements.size());
        //select one roadPosition from all valid roadPositions
        String selectedRoadPosition = possibleRoadPlacements.get(randomNumRoad);
        // String to int for move call
        int xRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("x") + 1, selectedRoadPosition.indexOf("y")));
        int yRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("y") + 1, selectedRoadPosition.indexOf("z")));
        int zRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("z") + 1, selectedRoadPosition.indexOf("_")));
        int sideRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("_") + 1));

        //place chosen settlement and road
        pioneersService.move(gameStorage.getId(), expectedMove,
                        x, y, z, side,
                        "settlement", null, null)
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> pioneersService.move(gameStorage.getId(), expectedMove, xRoad, yRoad, zRoad,
                                sideRoad, "road", null, null)
                        .observeOn(FX_SCHEDULER)
                        .subscribe());
    }

    public void calculateRoad(String lastBuildingPosition, String expectedMove) {
        // String to int from lastBuildingPlaced to calculate possible roadPlacements
        int x = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("x") + 1, lastBuildingPosition.indexOf("y")));
        int y = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("y") + 1, lastBuildingPosition.indexOf("z")));
        int z = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("z") + 1, lastBuildingPosition.indexOf("_")));
        int side = Integer.parseInt(lastBuildingPosition.substring(lastBuildingPosition.indexOf("_") + 1));

        //get every possible roadPosition
        List<String> possibleRoadPlacements = getPossibleRoadPlacements(x, y, z, side);

        int randomNumRoad = (int) (Math.random() * possibleRoadPlacements.size());
        //select one possibleRoad
        String selectedRoadPosition = possibleRoadPlacements.get(randomNumRoad);
        // String to int for move call
        int xRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("x") + 1, selectedRoadPosition.indexOf("y")));
        int yRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("y") + 1, selectedRoadPosition.indexOf("z")));
        int zRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("z") + 1, selectedRoadPosition.indexOf("_")));
        int sideRoad = Integer.parseInt(selectedRoadPosition.substring(selectedRoadPosition.indexOf("_") + 1));

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

}
