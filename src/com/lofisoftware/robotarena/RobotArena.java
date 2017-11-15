package com.lofisoftware.robotarena;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quint;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.screens.GameplayScreen;
import com.lofisoftware.robotarena.screens.RobotArenaMenu;
import com.lofisoftware.robotarena.tween.VectorAccessor;

public class RobotArena extends Game {

	public static final String NAME = "Robot Arena";
	public static final String VERSION = "0.0.1 Pre-Alpha";
	public static final String LOG = "Robot Arena";
	
	public static final int GAME_MENU = 1;
	public static final int GAME_PLAY = 2;
	public static final int GAME_LOST = 3;
	public static final int GAME_WON = 4;
	public static final int GAME_CREDITS = 5;
	public static final int GAME_HELP = 6;
	public static final int GAME_RESUME = 7;
	public static final int GAME_INTRO = 8;
	public static final int GAME_EXIT = 9;
	
	private static RobotArenaMenu game_menu;
	private static GameplayScreen game_play;	
/*	private static RobotArenaCredits game_credits;
	private static RobotArenaWin game_won;
	private static RobotArenaLoose game_lost;
	private static RobotArenaHelp game_help;
	private static RobotArenaIntro game_intro;*/
	
	private FPSLogger fps;
	private static TweenManager tweenManager;
	//private GameplayScreen gamescreen;
	private static int game_state;
	private static int new_game_state;
	
	

	
	@Override
	public void create () {
		
		Assets.load();

		fps = new FPSLogger();
		
		tweenManager = new TweenManager();

		// default is 3, yet for rgba color setting we need to raise to 4
		Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(Vector3.class, new VectorAccessor());
		//Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
		
		new_game_state = GAME_MENU;
		game_state = GAME_PLAY;

	}

	public RobotArena() {
		super();
		
	}

	@Override
	public void render () {
		
		if (game_state != new_game_state) {
			game_state = new_game_state;
			
			switch (game_state) {
			
			case GAME_MENU:
				if (game_menu == null)
					game_menu = new RobotArenaMenu();

				setScreen(game_menu);
				break;
			case GAME_PLAY:
				if (game_play != null)
					game_play.dispose();
				game_play = new GameplayScreen();

				setScreen(game_play);
				break;
/*			case GAME_INTRO:
				game_intro = new ThirdPlanetIntro();

				setScreen(game_intro);
				break;
			case GAME_RESUME:
				if (game_play == null)
					game_play = new ThirdPlanetGame();

				setScreen(game_play);
				break;
			case GAME_HELP:
				if (game_help == null)
					game_help = new ThirdPlanetHelp();

				setScreen(game_help);
				break;
			case GAME_CREDITS:
				if (game_credits == null)
					game_credits = new ThirdPlanetCredits();

				setScreen(game_credits);
				break;
			case GAME_LOST:
				if (game_lost == null)
					game_lost = new ThirdPlanetLoose();

				setScreen(game_lost);
				break;
			case GAME_WON:
				if (game_won == null)
					game_won = new ThirdPlanetWin();

				setScreen(game_won);
				break;*/
			case GAME_EXIT:
				if (game_play != null)
					game_play.dispose();

				Gdx.app.exit();
				break;
			default:
				new_game_state = GAME_MENU;	
			}
		}
		
		getTweenManager().update(Gdx.graphics.getDeltaTime());
		
		super.render();
		//fps.log();
		
	}
	
	public static void changeGameState(int state) {
		new_game_state = state;
	}

	
	@Override
	public void dispose() {
		if (game_play != null)
			game_play.dispose();
		Assets.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		if (game_play != null)
			game_play.resize(width, height);
	}
	
	public static TweenManager getTweenManager() {
		return tweenManager;
	}
	
}
