package com.github.limdingwen.SpaceCubes.World;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;
import com.github.limdingwen.SpaceCubes.Rendering.RenderEngine;

public class Chunk {
	public Block[][][] blocks = new Block[World.CHUNK_LENGTH][World.CHUNK_HEIGHT][World.CHUNK_LENGTH];
	private boolean isLoaded = false;
	
	// Positions are based on chunk coords
	//
	// -----
	// |0,0 |
	// |    | (1 unit is one chunk.)
	// -----
	//
	protected int positionX;
	protected int positionY;
	
	public boolean render = true;
	
	public Chunk(Block[][][] b, int x, int y) {
		if (b != null) blocks = b.clone();
		
		positionX = x;
		positionY = y;
	}
	
	public int getPositionX() {
		return positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public Chunk generate() {
		System.out.println("(Re)generating chunk " + this.toString()+ "....");
		
		// Generate air and initialize chunk
		
		for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
			for (int iy = 0; iy < World.CHUNK_HEIGHT; iy++) {
				for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
					blocks[ix][iy][iz] = new Block(Material.AIR.id, Material.AIR.meta, new Vector3i(ix,iy,iz), new Vector2i(positionX, positionY));
				}
			}
		}
		
		// Generate dirt
	
		for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
			for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
				for (int iy = 0; iy < 65; iy++) {
					if (iy < 64)
					blocks[ix][iy][iz].material = Material.DIRT.id;
					
					if (iy >= 63 && iy < 64) {
						if (Math.floor(Math.random() * 2) == 0) {
							blocks[ix][iy][iz].material = Material.GRASS.id;
							blocks[ix][iy][iz].meta = Material.GRASS.meta;
						}
					}
					else if (iy == 64) {
						if (Math.floor(Math.random() * 4) == 0) {
							blocks[ix][iy][iz].material = Material.GRASS.id;
							blocks[ix][iy][iz].meta = Material.GRASS.meta;
						}
					}
				}
			}
		}
		
		System.out.println("Generated dirt.");
		
		// Generate stone
		
		for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
			for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
				for (int iy = 0; iy < 60; iy++) {
					if (Math.floor(Math.random() * (1 + iy/600)) == 0) {
						if (Math.floor(Math.random() * (0.5 + iy/40)) == 0) {
							blocks[ix][iy][iz].material = Material.ROCK.id;
						}
						else {
							blocks[ix][iy][iz].material = Material.CRACKEDROCK.id;
							blocks[ix][iy][iz].meta = Material.CRACKEDROCK.meta;
						}
					}
				}
			}
		}
		
		System.out.println("Generated rock");
		
		// Generate voidrock
		
		for (int ix = 0; ix < World.CHUNK_LENGTH; ix++) {
			for (int iz = 0; iz < World.CHUNK_LENGTH; iz++) {
				for (int iy = 0; iy < 3; iy++) {
					if (iy == 0) {
						blocks[ix][iy][iz].material = Material.VOIDROCK.id;
					}
					else if (iy == 1) {
						if (Math.floor(Math.random() * 4) == 0) {
							blocks[ix][iy][iz].material = Material.VOIDROCK.id;
						}
					}
					else if (iy == 2) {
						if (Math.floor(Math.random() * 8) == 0) {
							blocks[ix][iy][iz].material = Material.VOIDROCK.id;
						}
					}
				}
			}
		}
		
		System.out.println("Generated voidrock layer.");
		System.out.println("Finished (re)generating chunk " + this.toString() + "!");
				
		return this;
	}
	
	public void moveGraphics() {
		GraphicsMoveRunnable runnable = new GraphicsMoveRunnable(blocks, positionX, positionY);
		runnable.run();
		
		Debug.info("Updated graphics and collision to chunk coords relative.");
	}
	
	public void update(Vector3i pos, boolean spread, boolean counted) {
		int cl = World.CHUNK_LENGTH;
		
		Block.updateBlock(blocks[(int) pos.x][(int) pos.y][(int) pos.z], true, true);
		
		if (spread) {
			try {
				Block.updateBlock(blocks[(int) pos.x + 1][(int) pos.y][(int) pos.z], true, true);
			}
			catch (Exception e) {
			}

			try {
				Block.updateBlock(blocks[(int) pos.x - 1][(int) pos.y][(int) pos.z], true, true);
			}
			catch (Exception e) {
			}

			try {
				Block.updateBlock(blocks[(int) pos.x][(int) pos.y + 1][(int) pos.z], true, true);
			}
			catch (Exception e) {
			}

			try {
				Block.updateBlock(blocks[(int) pos.x][(int) pos.y - 1][(int) pos.z], true, true);
			}
			catch (Exception e) {
			}

			try {
				Block.updateBlock(blocks[(int) pos.x][(int) pos.y][(int) pos.z + 1], true, true);
			}
			catch (Exception e) {
			}

			try {
				Block.updateBlock(blocks[(int) pos.x][(int) pos.y][(int) pos.z - 1], true, true);
			}
			catch (Exception e) {
			}

			// Spread update to nearby blocks at nearby chunks.

			boolean morex = false;
			boolean lessx = false;
			boolean morez = false;
			boolean lessz = false;

			if (pos.x+1>=cl) morex = true;
			if (pos.x-1<0) lessx = true;
			if (pos.z+1>=cl) morez = true;
			if (pos.z-1<0) lessz = true;

			if (morex||lessx||morez||lessz) {
				World world = RenderEngine.world;
				Chunk[][] cs = world.chunks;
				Vector2i cpos = World.getChunkPosAtBlockCoords(pos);
				
				if (morex) {
					try {
						cs[cpos.x+1][cpos.y].update(new Vector3i(
								15-pos.x,
								pos.y,
								pos.z), false, true);
					}
					catch (Exception e) {
					}
				}

				if (lessx) {
					try {
						cs[(int) (cpos.x-1)][(int) (cpos.y)].update(new Vector3i(
								15-pos.x,
								pos.y,
								pos.z), false, true);
					}
					catch (Exception e) {
					}
				}

				if (morez) {
					try {
						cs[(int) (cpos.x)][(int) (cpos.y+1)].update(new Vector3i(
								pos.x,
								pos.y,
								15-pos.z), false, true);
					}
					catch (Exception e) {
					}
				}

				if (lessz) {
					try {
						cs[(int) (cpos.x)][(int) (cpos.y-1)].update(new Vector3i(
								pos.x,
								pos.y,
								15-pos.z), false, true);
					}
					catch (Exception e) {
					}
				}
			}
		}
	}

	public void loadChunk() {
		isLoaded = true;

		for (int cx = 0; cx < World.CHUNK_LENGTH; cx++) {
			for (int cy = 0; cy < World.CHUNK_HEIGHT; cy++) {
				for (int cz = 0; cz < World.CHUNK_LENGTH; cz++) {
					Block.updateBlock(blocks[cx][cy][cz], false, false);
				}
			}
		}
	}

	public void unloadChunk() {
		isLoaded = false;

		blocks = new Block[World.CHUNK_LENGTH][World.CHUNK_HEIGHT][World.CHUNK_LENGTH];
	}
	
	public void flagChunkUnload() {
		isLoaded = false;
	}
	
	public boolean getIsLoaded() {
		return isLoaded;
	}
}
