package com.lofisoftware.robotarena;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class Assets {
	
	public static TextureAtlas gameatlas;
	public static Skin gameskin;
	public static Skin uiskin;
	public static BitmapFont fontSmall;
	public static BitmapFont fontLarge;
	public static BitmapFont digitalFontLarge;
	public static BitmapFont digitalFontSmall;
	public static BitmapFont akashiFontLarge;
	public static BitmapFont akashiFontSmall;
	public static ShaderProgram fontShader;
	
	public enum GameSprite {
		GUI("gui-frame"),
		BUTTON_BLUE_SMALL("blue-button-small-24"),
		BUTTON_GRAY_LEFT("gray-button-left-24"),
		BUTTON_GRAY_MID("gray-button-mid-24"),
		BUTTON_GRAY_RIGHT("gray-button-right-24"),
		ARROW_NORTH("arrow-north-24"),
		ARROW_SOUTH("arrow-south-24"),
		ARROW_EAST("arrow-east-24"),
		ARROW_WEST("arrow-west-24"),
		REGISTER_BORDER("scifi-custom-24",24,0,0),
		CLOCK("clock-24"),
		BACK("black-x-button"),
		BUTTON_BLUE_WIDE("blue-button-wide-48x20"),

		PLAYER1("scifi-aliens-8",8,22,3),
		PLAYER2("scifi-aliens-8",8,23,3),
		PLAYER3("scifi-aliens-8",8,5,3),
		PLAYER4("scifi-aliens-8",8,2,3),
		GREEN_BLOB("scifi-aliens-8",8,3,3),
		EYEBALL("scifi-aliens-8",8,7,3),
		GREEN_CHECKED_MOB("scifi-aliens-8",8,19,3),
		WORKER_BLUE("scifi-humans-8",8,0,3),
		WORKER_RED("scifi-humans-8",8,0,4),
		WORKER_GREEN("scifi-humans-8",8,0,5),
		WORKER_YELLOW("scifi-humans-8",8,0,6),
		WORKER_WHITE("scifi-humans-8",8,0,7),
		CYCLOPS_ROBOT("scifi-aliens-8",8,0,3),
		GLOWING_BLOB("scifi-aliens-8",8,14,3),
		FIRE_SPIRIT("scifi-aliens-8",8,15,3),
		
		FINISH_FLAG("scifi-custom-8",8,1,9),
		EXCLAMATION("scifi-custom-8",8,1,10),
		HEART("scifi-items-8",8,0,28),
		BLOOD("scifi-items-8",8,4,24),
		SMALL_SKULL("lofi_obj",8,1,0),
		GREEN_CPU("scifi-starbase-8",8,9,24),
		RED_CPU("scifi-starbase-8",8,9,23),
		TERMINAL2("scifi-starbase-8",8,10,24),
		TERMINAL1("scifi-starbase-8",8,10,23),
		
		FLOOR_TAN("scifi-starbase-8",8,12,1),
		FLOOR_GREEN("scifi-starbase-8",8,12,2),
		FLOOR_BIG_CHECK("scifi-starbase-8",8,10,5),
		FLOOR_SMALL_CHECK("scifi-custom-8",8,0,4),
		
		DUNGEON_FLOOR("scifi-starbase-8",8,9,1),
		DUNGEON_WALL("scifi-starbase-8",8,1,4),
		DUNGEON_DOOR_OPEN("scifi-starbase-8",8,0,2),
		DUNGEON_STAIR_UP("scifi-starbase-8",8,9,25),
		DUNGEON_STAIR_DOWN("scifi-starbase-8",8,9,28),
		
		LASER_NORTH("scifi-starbase-8",8,15,5),
		LASER_SOUTH("scifi-starbase-8",8,15,6),
		LASER_EAST("scifi-starbase-8",8,15,7),
		LASER_WEST("scifi-starbase-8",8,15,8),
		LASER_BEAM_NS("scifi-custom-8",8,0,5),
		LASER_BEAM_EW("scifi-custom-8",8,0,6),
		
		CONVEYOR_NORTH("scifi-starbase-8",8,17,22),
		CONVEYOR_SOUTH("scifi-starbase-8",8,15,22),
		CONVEYOR_EAST("scifi-starbase-8",8,14,22),
		CONVEYOR_WEST("scifi-starbase-8",8,16,22),
		
		SMASHER_NORTH("scifi-custom-8",8,0,8),
		SMASHER_SOUTH("scifi-custom-8",8,0,10),
		SMASHER_EAST("scifi-custom-8",8,0,9),
		SMASHER_WEST("scifi-custom-8",8,0,11),
		SMASHER_MIDDLE("scifi-custom-8",8,0,12),
		SMASHER_BAR_NORTH("scifi-custom-8",8,1,0),
		SMASHER_BAR_EAST("scifi-custom-8",8,1,1),
		SMASHER_BAR_SOUTH("scifi-custom-8",8,1,2),
		SMASHER_BAR_WEST("scifi-custom-8",8,1,3),
		
		SPIKE_NORTH("scifi-custom-8",8,1,4),
		SPIKE_EAST("scifi-custom-8",8,1,5),
		SPIKE_SOUTH("scifi-custom-8",8,1,6),
		SPIKE_WEST("scifi-custom-8",8,1,7),
		
		FIRE("lofi_obj",8,6,9),
		
		WRENCH("scifi-custom-8",8,1,8),
		
		LETTER_A("lofi_font_big",8,3,10),
		LETTER_B("lofi_font_big",8,3,11),
		LETTER_C("lofi_font_big",8,3,12),
		LETTER_D("lofi_font_big",8,3,13),
		LETTER_E("lofi_font_big",8,3,14),
		LETTER_F("lofi_font_big",8,3,15),
		LETTER_G("lofi_font_big",8,4,0),
		LETTER_H("lofi_font_big",8,4,1),
		LETTER_I("lofi_font_big",8,4,2),
		LETTER_J("lofi_font_big",8,4,3),
		LETTER_K("lofi_font_big",8,4,4),
		LETTER_L("lofi_font_big",8,4,5),
		LETTER_M("lofi_font_big",8,4,6),
		LETTER_N("lofi_font_big",8,4,7),
		LETTER_O("lofi_font_big",8,4,8),
		LETTER_P("lofi_font_big",8,4,9),
		LETTER_Q("lofi_font_big",8,4,10),
		LETTER_R("lofi_font_big",8,4,11),
		LETTER_S("lofi_font_big",8,4,12),
		LETTER_T("lofi_font_big",8,4,13),
		LETTER_U("lofi_font_big",8,4,14),
		LETTER_V("lofi_font_big",8,4,15),
		LETTER_W("lofi_font_big",8,5,0),
		LETTER_X("lofi_font_big",8,5,1),
		LETTER_Y("lofi_font_big",8,5,2),
		LETTER_Z("lofi_font_big",8,5,3),
		
		HIGHLIGHT("scifi-custom-8",8,0,2),
		WHITE("scifi-custom-8",8,0,3),
		BLACK("scifi-custom-8",8,0,1),
	    EMPTY("scifi-custom-8",8,0,0);
		
		//private String name; 
		private Array<AtlasRegion> textures;
		int size, row, column;
		
		GameSprite(String name){
			//this.name = name;
			this(name,0,0,0);
		}
		
		GameSprite(String name, int size, int row, int column){
			this.size = size;
			this.column = column;
			this.row = row;
			
			if (size == 0)
				this.textures = gameatlas.findRegions(name);
			else {
				TextureRegion[][] regions = gameatlas.findRegion(name).split(size, size);
				
				if (regions != null && row >= 0 && column >= 0 && row < regions.length && column < regions[0].length) {
					TextureRegion region = regions[row][column];
					
					Array<AtlasRegion> matched = new Array();
					matched.add(new AtlasRegion(region.getTexture(),region.getRegionX(),region.getRegionY(),region.getRegionWidth(),region.getRegionHeight()));
					
					
					this.textures = matched;
				}
				else
					Gdx.app.error("Assets", "Texture not found: " + name + " row: " + row + " column: " + column);
			}
		}

		public Array<AtlasRegion> getAnimationTextures() {
			return textures;
		}

		public TextureRegion getTexture() {
			return textures.get(0);
		}
		
		public TextureRegion getTexture(int i) {
			return textures.get(i);
		}
		
	};
	
	

	public static void load() {
		gameatlas = new TextureAtlas(Gdx.files.internal("RobotArenaAtlas.atlas"));
		uiskin = new Skin(Gdx.files.internal("uiskin.json"));
		gameskin = new Skin();
		gameskin.addRegions(gameatlas);
		//fontSmall = new BitmapFont(Gdx.files.internal("default.fnt"));
		//fontLarge = new BitmapFont(Gdx.files.internal("default.fnt"));
		//textureAtlas.findRegion("calibri"), false);
		
		/*FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Akashi.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
		parameter.packer = null;
		parameter.flip = false;
		parameter.genMipMaps = false;
		parameter.minFilter = TextureFilter.Nearest;
		parameter.magFilter = TextureFilter.Nearest;
		
		fontSmall = generator.generateFont(parameter);
		
		parameter.size = 30;
		fontLarge = generator.generateFont(parameter);*/
		
		fontSmall = new BitmapFont(Gdx.files.internal("Akashi-32.fnt"));
		fontLarge = new BitmapFont(Gdx.files.internal("Akashi-64.fnt"));
		
		Texture texture = new Texture(Gdx.files.internal("Akashi-distance-32.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear); // linear filtering in nearest mipmap image
		akashiFontLarge = new BitmapFont(Gdx.files.internal("Akashi-distance-32.fnt"), new TextureRegion(texture), false);
		
		texture = new Texture(Gdx.files.internal("Akashi-distance-20.png"), true); // true enables mipmaps
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear); // linear filtering in nearest mipmap image
		akashiFontSmall = new BitmapFont(Gdx.files.internal("Akashi-distance-20.fnt"), new TextureRegion(texture), false);
		
		fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
		if (!fontShader.isCompiled()) {
		    Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
		}
		
		//generator.dispose();
	}
	
	public static TextureRegion getTexture(String textureName) {
		
		return gameskin.getRegion(textureName);
	}
	
	public static Array<AtlasRegion> getTextureArray(String textureName) {
		
		return gameatlas.findRegions(textureName);
	}
	
	public static Skin getGameskin() {
		return gameskin;
	}
	
	public static void dispose() {
		gameskin.dispose();
		uiskin.dispose();
		gameatlas.dispose();
	}
}
