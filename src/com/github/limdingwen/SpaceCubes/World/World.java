package com.github.limdingwen.SpaceCubes.World;

import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.Debug;
import com.github.limdingwen.SpaceCubes.BlockTypes.Block;
import com.github.limdingwen.SpaceCubes.BlockTypes.Material;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector2i;
import com.github.limdingwen.SpaceCubes.DataTypes.Vector3i;
import com.github.limdingwen.SpaceCubes.Rendering.BlockRenderEngine;

public class World {
	public static final int WORLD_LENGTH = 30;
	
	public Chunk[][] chunks = new Chunk[WORLD_LENGTH][WORLD_LENGTH];
	public static final int CHUNK_LENGTH = 16;
	public static final int CHUNK_HEIGHT = 128;
	
	public int time = 0;
	
	public static final int DAWN = 0;
	public static final int MORNING = 1000;
	public static final int AFTERNOON = 7600;
	public static final int EVENING = 15400;
	public static final int NIGHT = 16800;
	public static final int MIDNIGHT = 21600;
	public static final int END = 28800;
	
	public World() {
	}
	
	public void generate() {
		System.out.println("(Re)generating world " + this.toString());
		
		int total = (chunks.length + 1) * chunks.length;
		int progress = 0;
		System.out.println("Worldgen " + this.toString() + " completion " + progress + "/" + total + "!");

		for (int ix = 0; ix < chunks.length; ix++) {
			progress++;
			
			for (int iz = 0; iz < chunks.length; iz++) {
				progress++;
								
				chunks[ix][iz] = new Chunk(null, ix, iz).generate();
				
				Debug.info("Worldgen " + this.toString() + " completion " + progress + "/" + total + "!");
			}
		}
		
		Debug.info("Worldgen " + this.toString() + " completion " + progress + "/" + total + "!");
	}
	
	public static Vector3i coordRealToBlock(Vector3f pos) {
		Vector3i blockCoords = new Vector3i(
				(int) Math.floor(pos.x / (BlockRenderEngine.doubleBs)),
				(int) Math.floor(pos.y / (BlockRenderEngine.doubleBs)),
				(int) Math.floor(pos.z / (BlockRenderEngine.doubleBs)));
		
		return blockCoords;
	}
	
	public static Vector3f coordBlockToReal(Vector3i pos) {
		Vector3f blockCoords = new Vector3f(
				pos.x * (BlockRenderEngine.doubleBs),
				pos.y * (BlockRenderEngine.doubleBs),
				pos.z * (BlockRenderEngine.doubleBs));
		
	//	System.out.println(blockCoords);

		return blockCoords;
	}
	
	public void changeBlockAtToRealCoord(Vector3f pos, Material mat, boolean force) {
		changeBlockAtToBlockCoord(
				coordRealToBlock(pos),mat, force);
	}
	
	public void changeBlockAtToBlockCoord(Vector3i pos, Material mat, boolean force) {
		Vector2i chunkCoords = new Vector2i(
				pos.x / CHUNK_LENGTH,
				pos.z / CHUNK_LENGTH);
		
		chunkCoords.x = (int) Math.floor(chunkCoords.x);
		chunkCoords.y = (int) Math.floor(chunkCoords.y);
		
		Vector3i localBlockCoords = new Vector3i(
				pos.x % CHUNK_LENGTH,
				pos.y,
				pos.z % CHUNK_LENGTH);
				
		try {
			Block block = chunks[(int) chunkCoords.x]
					[(int) chunkCoords.y].blocks
					[(int) localBlockCoords.x]
							[(int) localBlockCoords.y]
									[(int) localBlockCoords.z];
			if (!force) {
				if (block.material == Material.AIR.id)	{
					block.material = mat.id;
					block.meta = mat.meta;
				}
			}
			else {
				block.material = mat.id;
				block.meta = mat.meta;
			}
			
			// Update chunks
			
			Chunk chunk = chunks[(int) chunkCoords.x]
					[(int) chunkCoords.y];
			chunk.update(localBlockCoords, true, true);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			Debug.warning("Chunk refrence not in world!");
		}
	}
	
	public Chunk getChunkAtBlockCoords(Vector3i pos) {
		Vector2i chunkCoords = new Vector2i(
				pos.x / CHUNK_LENGTH,
				pos.z / CHUNK_LENGTH);
		
		chunkCoords.x = (int) Math.floor(chunkCoords.x);
		chunkCoords.y = (int) Math.floor(chunkCoords.y);
		
		try {
			return chunks[chunkCoords.x][chunkCoords.y];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			return null; // Return null if chunk does not exist at that position
		}
	}
	
	public static Vector2i getChunkPosAtBlockCoords(Vector3i pos) {
		Vector2i chunkCoords = new Vector2i(
				pos.x / CHUNK_LENGTH,
				pos.z / CHUNK_LENGTH);
		
		chunkCoords.x = (int) Math.floor(chunkCoords.x);
		chunkCoords.y = (int) Math.floor(chunkCoords.y);
		
		return chunkCoords;
	}
	
	public static Vector3i getLocalBlockCoords(Vector3i pos) {
		Vector3i localBlockCoords = new Vector3i(
				pos.x % CHUNK_LENGTH,
				pos.y,
				pos.z % CHUNK_LENGTH);
		
		localBlockCoords.x = (int) Math.floor(localBlockCoords.x);
		localBlockCoords.y = (int) Math.floor(localBlockCoords.y);
		localBlockCoords.z = (int) Math.floor(localBlockCoords.z);
		
		return localBlockCoords;
	}
	
	public World setChunks(Chunk[][] chu) {
		chunks = chu;
		
		return this; // Chainability
	}
	
	public void moveGraphics() {
		for (int ix = 0; ix < WORLD_LENGTH; ix++) {
			for (int iz = 0; iz < WORLD_LENGTH; iz++) {
				if (chunks[ix][iz].getIsLoaded()) chunks[ix][iz].moveGraphics();
			}
		}
	}
	
	public Block getTopBlock(int x, int y) {
		Vector3i vec = new Vector3i(x, 0, y);
		
		Chunk ch = getChunkAtBlockCoords(vec);
		Vector3i bl = getLocalBlockCoords(vec);
		
		// Do raycast
		for (int i = CHUNK_HEIGHT-1; i >= 0; i--) {
			Block bloc = ch.blocks[bl.x][i][bl.z];
			
			if (Material.getMaterialFromID(bloc.material, bloc.meta) != Material.AIR) {
				return bloc;
			}
		}
		
		return null; // If no block is found
	}
	
	public static Vector3i toFirstChunk(Vector3i pos) {
		return new Vector3i(
				pos.x % CHUNK_LENGTH,
				pos.y,
				pos.z % CHUNK_LENGTH);
	}
}
