package com.lofisoftware.robotarena.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lofisoftware.robotarena.Assets.GameSprite;
	
public enum Tile {
	// Name, Texture Name, visible, passable
	FLOOR_TAN("Floor", GameSprite.FLOOR_TAN, true, true),
	FLOOR_GREEN("Floor", GameSprite.FLOOR_GREEN, true, true),
	FLOOR_BIG_CHECK("Floor", GameSprite.FLOOR_BIG_CHECK, true, true),
	FLOOR_SMALL_CHECK("Floor", GameSprite.FLOOR_SMALL_CHECK, true, true),
	
	BLOOD("Blood", GameSprite.BLOOD, true, true),
	SMALL_SKULL("Pile of Bones", GameSprite.SMALL_SKULL, true, true),
	GREEN_CPU("CPU", GameSprite.GREEN_CPU, true, false),
	RED_CPU("CPU", GameSprite.RED_CPU, true, false),
	TERMINAL2("CPU", GameSprite.TERMINAL2, true, false),
	TERMINAL1("CPU", GameSprite.TERMINAL1, true, false),
	DUNGEON_FLOOR("Floor", GameSprite.DUNGEON_FLOOR, true, true),
	DUNGEON_WALL("Wall", GameSprite.DUNGEON_WALL, true, false),
	DUNGEON_DOOR_OPEN("Door", GameSprite.DUNGEON_DOOR_OPEN, true, true),
	DUNGEON_STAIR_UP("Stairs up", GameSprite.DUNGEON_STAIR_UP, true, true),
	DUNGEON_STAIR_DOWN("Stairs down", GameSprite.DUNGEON_STAIR_DOWN, true, true),
	
	LETTER_A("A", GameSprite.LETTER_A, true, true),
	LETTER_B("B", GameSprite.LETTER_B, true, true),
	LETTER_C("C", GameSprite.LETTER_C, true, true),
	LETTER_D("D", GameSprite.LETTER_D, true, true),
	LETTER_E("E", GameSprite.LETTER_E, true, true),
	LETTER_F("F", GameSprite.LETTER_F, true, true),
	LETTER_G("G", GameSprite.LETTER_G, true, true),
	LETTER_H("H", GameSprite.LETTER_H, true, true),
	LETTER_I("I", GameSprite.LETTER_I, true, true),
	LETTER_J("J", GameSprite.LETTER_J, true, true),
	LETTER_K("K", GameSprite.LETTER_K, true, true),
	LETTER_L("L", GameSprite.LETTER_L, true, true),
	LETTER_M("M", GameSprite.LETTER_M, true, true),
	LETTER_N("N", GameSprite.LETTER_N, true, true),
	LETTER_O("O", GameSprite.LETTER_O, true, true),
	LETTER_P("P", GameSprite.LETTER_P, true, true),
	LETTER_Q("Q", GameSprite.LETTER_Q, true, true),
	LETTER_R("R", GameSprite.LETTER_R, true, true),
	LETTER_S("S", GameSprite.LETTER_S, true, true),
	LETTER_T("T", GameSprite.LETTER_T, true, true),
	LETTER_U("U", GameSprite.LETTER_U, true, true),
	LETTER_V("V", GameSprite.LETTER_V, true, true),
	LETTER_W("W", GameSprite.LETTER_W, true, true),
	LETTER_X("X", GameSprite.LETTER_X, true, true),
	LETTER_Y("Y", GameSprite.LETTER_Y, true, true),
	LETTER_Z("Z", GameSprite.LETTER_Z, true, true),
	
	HIGHLIGHT("",GameSprite.HIGHLIGHT,true,true),
	WHITE("",GameSprite.WHITE,true,true),
	BLACK("",GameSprite.BLACK,true,true),
    EMPTY("",GameSprite.EMPTY,false,true);
 
	public static final int TILE_WIDTH = 8;
	public static final int TILE_HEIGHT = 8;
	
    private String name;
    private GameSprite sprite;
    private boolean visible = true;
    private boolean passable = true;
    
    Tile(String name, GameSprite sprite){
        this(name, sprite, true, false);
    }

	private Tile(String name, GameSprite sprite, boolean visible, boolean passable) {
		this.visible = visible;
		this.passable = passable;
		this.name = name;
		this.sprite = sprite;
	}
	 
	public String getName() {
		return name;
	}
	
	public TextureRegion getTextureRegion() {
		return sprite.getTexture();
	}
	
	public boolean isVisible() {
		return visible;
	}

	public boolean isPassable() {
		return passable;
	}

	public static Tile getTileByName(String name) {
		for( Tile t : Tile.values()) {
			if (t.getName().equalsIgnoreCase(name))
				return t;	
		}
		return EMPTY;
	}
}

