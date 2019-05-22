package me.jakerg.rougelike;

/**
 * From starter code
 * @author gutierr8
 *
 */
public class WorldBuilder {
	private int width;
	private int height;
	private Tile[][] tiles;
	
	/**
	 * Make a new world based on width and height
	 * @param width Width of world
	 * @param height Height of world
	 */
	public WorldBuilder(int width, int height) {
	    this.width = width;
	    this.height = height;
	    this.tiles = new Tile[width][height];
	}
	
	/**
	 * Build world
	 * @return World with tiles
	 */
	public World build() {
	    return new World(tiles);
	}
	
	/**
	 * Make caves of world with smoothness 8
	 * @return Instance
	 */
	public WorldBuilder makeCaves() {
		return randomizeTiles().smooth(8);
	}
	
	/**
	 * Make random tiles
	 * @return WorldBuilder instance
	 */
	private WorldBuilder randomizeTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Math.random() < 0.5 ? Tile.FLOOR : Tile.WALL;
            }
        }
        return this;
    }
	
	/**
	 * Smooth out random tiles
	 * @param times Number of times to do it
	 * @return WorldBuilder but with smooth tiles
	 */
	private WorldBuilder smooth(int times) {
        Tile[][] tiles2 = new Tile[width][height];
        for (int time = 0; time < times; time++) {

         for (int x = 0; x < width; x++) {
             for (int y = 0; y < height; y++) {
              int floors = 0;
              int rocks = 0;

              for (int ox = -1; ox < 2; ox++) {
                  for (int oy = -1; oy < 2; oy++) {
                   if (x + ox < 0 || x + ox >= width || y + oy < 0
                        || y + oy >= height)
                       continue;

                   if (tiles[x + ox][y + oy] == Tile.FLOOR)
                       floors++;
                   else
                       rocks++;
                  }
              }
              tiles2[x][y] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
             }
         }
         tiles = tiles2;
        }
        return this;
    }
}
