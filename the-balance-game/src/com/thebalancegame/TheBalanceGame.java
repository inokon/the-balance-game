package com.thebalancegame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.thebalancegame.levels.arcademode.ArcadeLevel1;
import com.thebalancegame.levels.arcademode.ArcadeLevel10;
import com.thebalancegame.levels.arcademode.ArcadeLevel2;
import com.thebalancegame.levels.arcademode.ArcadeLevel3;
import com.thebalancegame.levels.arcademode.ArcadeLevel4;
import com.thebalancegame.levels.arcademode.ArcadeLevel5;
import com.thebalancegame.levels.arcademode.ArcadeLevel6;
import com.thebalancegame.levels.arcademode.ArcadeLevel7;
import com.thebalancegame.levels.arcademode.ArcadeLevel8;
import com.thebalancegame.levels.arcademode.ArcadeLevel9;


public class TheBalanceGame extends Game {
	GameScreen gameScreen;
	GameScreen currentLevel;
	int currentLevelNum;
	int currentAreaNum;

	@Override
	public void create() {
		switchToLevel(1, 1);
	}
	
	public GameScreen switchToGame() {
		if (gameScreen == null)
			gameScreen = new GameScreen(this);
		return gameScreen;
	}
	
	public void switchToLevel(int area, int level) {
		switch (area) {
			case 1:
				switch (level) {
					case 1:
						currentLevel = new ArcadeLevel1(this);
						break;
					case 2:
						currentLevel = new ArcadeLevel2(this);
						break;
					case 3:
						currentLevel = new ArcadeLevel3(this);
						break;
					case 4:
						currentLevel = new ArcadeLevel4(this);
						break;
					case 5:
						currentLevel = new ArcadeLevel5(this);
						break;
					case 6:
						currentLevel = new ArcadeLevel6(this);
						break;
					case 7:
						currentLevel = new ArcadeLevel7(this);
						break;
					case 8:
						currentLevel = new ArcadeLevel8(this);
						break;
					case 9:
						currentLevel = new ArcadeLevel9(this);
						break;
					case 10:
						currentLevel = new ArcadeLevel10(this);
						break;
				}
				break;
			// TODO: Survival stages
			/*case 2:
				switch (level) {
					case 1:
						currentLevel = new SurvivalLevel1(this);
						break;
					case 2:
						currentLevel = new SurvivalLevel2(this);
						break;
					case 3:
						currentLevel = new SurvivalLevel3(this);
						break;
					case 4:
						currentLevel = new SurvivalLevel4(this);
						break;
					case 5:
						currentLevel = new SurvivalLevel5(this);
						break;
					case 6:
						currentLevel = new SurvivalLevel6(this);
						break;
					case 7:
						currentLevel = new SurvivalLevel7(this);
						break;
					case 8:
						currentLevel = new SurvivalLevel8(this);
						break;
					case 9:
						currentLevel = new SurvivalLevel9(this);
						break;
					case 10:
						currentLevel = new SurvivalLevel10(this);
						break;
				}*/
		}
		if (currentLevel == null)
			Gdx.app.exit();
		currentAreaNum = area;
		currentLevelNum = level;
		System.gc();
		setScreen(currentLevel);
		System.gc();
	}
	
	public void resetScreen() {
		if (currentLevel == null)
			Gdx.app.exit();
		System.gc();
		setScreen(currentLevel);
		System.gc();
	}
	
	public void nextLevel() {
		if (currentLevelNum > 9)
			currentLevelNum = 9;
		switchToLevel(currentAreaNum, ++currentLevelNum);
	}
	
	public void prevLevel() {
		if (currentLevelNum < 1)
			currentLevelNum = 1;
		switchToLevel(currentAreaNum,--currentLevelNum);
	}
	
	public void exit() {
		currentLevel.rayHandler.dispose();
		Gdx.app.exit();
	}

}
