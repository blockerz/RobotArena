package com.lofisoftware.robotarena.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lofisoftware.robotarena.Assets;

public class Button {

	public Button(float x, float y, float width, float height, String string,
			TextureRegion buttonTexture, TextureRegion buttonPressedTexture) {

		frame.set(x,y,width,height);
		text = string;
		texture = buttonTexture;
		texturePressed = buttonPressedTexture;
		
	}
	public Rectangle frame = new Rectangle(0,0,1,1);
	public String text = "";
	public TextureRegion texture;
	public TextureRegion texturePressed;
	public boolean touched;
	
	public void draw(SpriteBatch batch) {
		batch.draw(touched?this.texturePressed:this.texture, this.frame.x, this.frame.y, 0, 0, this.frame.width, this.frame.height, 1, 1, 0);
		batch.setShader(Assets.fontShader);
		Assets.akashiFontLarge.draw(batch, this.text, this.frame.x + (this.frame.width/2) - (Assets.akashiFontLarge.getBounds(this.text).width/2), this.frame.y + (this.frame.height/2) + (Assets.akashiFontLarge.getBounds(this.text).height));
		batch.setShader(null);
		
	}
	
	public boolean touch(Vector3 touch, boolean down) {
		if (down && frame.contains(new Vector2(touch.x,touch.y)))
			touched = true;
		else if(touched && !down && frame.contains(new Vector2(touch.x,touch.y))) {
			touched = false;
			return true;
		}
		else
			touched = false;
		
		return false;
	}
}
