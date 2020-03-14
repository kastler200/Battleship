
public class Gameboard {
	public static enum Status{
		empty, hit, miss
	}

	int NUMROWS;
	Status[][] tileStatus;
	//	Boolean[][] containsBoat;
	int [][] shipSaves;

	public Gameboard(int n) {
		NUMROWS = n;
		tileStatus = new Status[NUMROWS][NUMROWS];
		shipSaves = new int[NUMROWS][NUMROWS];

		for (int i = 0; i < NUMROWS; i++) {
			for (int j = 0; j < NUMROWS; j++) {
				tileStatus[i][j] = Status.empty;
			}
		}

		//empty ship tile = -1 since selectedShip starts at 0
		for (int i = 0; i < NUMROWS; i++) {
			for (int j = 0; j < NUMROWS; j++) {
				shipSaves[i][j] = -1;
			}
		}
	}

	public Status getTileStatus(int x, int y) {
		return tileStatus[x][y];
	}

	//check if tile contains any ship
	public boolean containsShip(int x, int y) {
		if (shipSaves[x][y] == -1) {
			return false;
		}else {
			return true;
		}
	}

	//set tile to the correct ship number
	public void setShip(int x, int y, int z) {
		shipSaves[x][y] = z;
	}

	//set tile status to hit or miss
	public void setTileStatus(int x, int y, Status s) {
		tileStatus[x][y] = s;
	}

	//check if tile x, y has a ship and if that ship has been sunk
	public boolean checkForSink(int tileX, int tileY) {
		int countHits = 0;
		int countShipTiles = 0;
		int shipNum = shipSaves[tileX][tileY];
		if (shipNum != -1) {
			for (int i = 0; i < NUMROWS; i++) {
				for (int j = 0; j < NUMROWS; j++) {
					if (shipSaves[i][j] == shipNum) {
						countShipTiles++;
						if (tileStatus[i][j] == Status.hit) {
							countHits++;
						}
					}
				}
			}
		}

		if (countHits == countShipTiles) {
			return true;
		}else {
			return false;
		}
	}

	//return number of spots taken up by ships. This will allow checking for a win.
	public int getTotalShipSpots() {
		int SHIPSPOTS = 0;
		for (int i = 0; i < NUMROWS; i++) {
			for (int j = 0; j < NUMROWS; j++) {
				if (shipSaves[i][j] != -1) {
					SHIPSPOTS++;
				}
			}
		}
		return SHIPSPOTS;
	}

	//return hit spots to compare to ship spots
	public int getTotalHitSpots() {
		int HITSPOTS = 0;
		for (int i = 0; i < NUMROWS; i++) {
			for (int j = 0; j < NUMROWS; j++) {
				if (tileStatus[i][j] == Status.hit) {
					HITSPOTS++;
				}
			}
		}
		return HITSPOTS;
	}

}
