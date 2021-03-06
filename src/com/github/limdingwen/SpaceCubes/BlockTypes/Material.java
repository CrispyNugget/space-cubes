package com.github.limdingwen.SpaceCubes.BlockTypes;

import org.lwjgl.util.vector.Vector3f;

public class Material {
	// IDs
	
	public static final Material AIR = new Material().setName("Air").setColor(0, 0, 0).setTransparent().
			setNonCollidable().setNotSolid().setSeeThru().setID((byte) 0);
	public static final Material DIRT = new Material().setName("Dirt").setColor(0.8f, 0.47f, 0.27f).setID((byte) 1);
	public static final Material ROCK = new Material().setName("Rock").setColor(1, 1, 1).setID((byte) 2);
	public static final Material VOIDROCK = new Material().setName("VoidRock").setColor(1, 0, 0).setID((byte) 3);
	public static final Material GRASS = new Material().setName("Grass").setColor(0, 1, 0).setID((byte) 1).setMeta((byte) 0x1);
	public static final Material CRACKEDROCK = new Material().setName("Cracked Rock").setColor(0.7f, 0.7f, 0.7f).setID((byte) 2).setMeta((byte) 0x1);
	
	public Vector3f color;
	public boolean transparent = false;
	public boolean collidable = true;
	public boolean fullCollider = true;
	public boolean seeThru = false;
	public String name = "Unnamed";
	public boolean notSolid = false;
	public byte id;
	public byte meta = 0x00000000;
	
	public Material setColor(float r, float g, float b) {
		color = new Vector3f(r, g, b);
		return this;
	}
	
	public Material setTransparent() {
		transparent = true;
		return this;
	}
	
	public Material setNonCollidable() {
		collidable = false;
		return this;
	}
	
	public Material setNonFullCollider() {
		fullCollider = false;
		return this;
	}
	
	public Material setName(String n) {
		name = n;
		return this;
	}
	
	public Material setNotSolid() {
		notSolid = true;
		return this;
	}
	
	public Material setID(byte i) {
		id = i;
		return this;
	}
	
	public Material setMeta(byte m) {
		meta = m;
		return this;
	}
	
	public Material setSeeThru() {
		seeThru = true;
		return this;
	}
	
	public static Material getMaterialFromID(byte id, byte meta) {
		switch (id) {
		case 0: return AIR;
		case 1:
			switch (meta) {
				case 0: return DIRT;
				case 1: return GRASS;
				default: return AIR;
			}
		case 2:
			switch (meta) {
				case 0: return ROCK;
				case 1: return CRACKEDROCK;
				default: return AIR;
			}
		case 3: return VOIDROCK;
		default: return AIR;
		}
	}
}