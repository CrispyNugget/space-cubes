package com.github.limdingwen.SpaceCubes.Events;

import org.lwjgl.util.vector.Vector3f;

import com.github.limdingwen.SpaceCubes.BlockTypes.Material;

public class PlayerPlaceBlock extends EventHolder {
	public Vector3f blockLoc;
	public Material material;
	
	public PlayerPlaceBlock(Vector3f blockLoc, Material material) {
		super();
		this.blockLoc = blockLoc;
		this.material = material;
	}
}
