// Part of Tritone, an app by Nathan Krantz-Fire. https://github.com/zippynk/tritone

//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at http://mozilla.org/MPL/2.0/.

package com.zippynk.tritone;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TritoneApp extends ApplicationAdapter implements InputProcessor {
	private Camera camera;
	private Viewport viewport;
	private SpriteBatch batch;
	private Texture playIMG;
	private Texture pauseIMG;
	private Texture progressBackgroundIMG;
	private Texture progressForegroundIMG;
	private Texture rewindIMG;
	private Texture fastForwardIMG;
	private ArrayList<Song> songs = new ArrayList();
	private ArrayList<Song> playQueue = new ArrayList();
	private boolean nowPlaying = false;
	private boolean paused = false;
	private Circle playPauseButton;
	private Circle rewindButton;
	private Circle fastForwardButton;
	private int currentSongIndex;
	private Music currentSong;
	private ClickListener clickListener;
	private Texture currentSongArtwork;
	private float currentXScaleFactor;
	private float currentYScaleFactor;
	private float currentSmallerScaleFactor;
	private BitmapFont font;
	private Texture defaultArt;

	@Override
	public void create () {
		batch = new SpriteBatch();
		playIMG = new Texture("Play.png");
		playIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		pauseIMG = new Texture("Pause.png");
		pauseIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		progressBackgroundIMG = new Texture("ProgressBackground.png");
		progressBackgroundIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		progressForegroundIMG = new Texture("ProrgressForeground.png");
		progressForegroundIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		rewindIMG = new Texture("Rewind.png");
		rewindIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fastForwardIMG = new Texture("FastForward.png");
		fastForwardIMG.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		defaultArt = new Texture("DefaultAlbumCover.png");
		defaultArt.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		//camera = new OrthographicCamera();
		//viewport = new FitViewport(640, 480, camera);
		clickListener = new ClickListener();
		currentXScaleFactor = (float) Gdx.graphics.getWidth()/640;
		currentYScaleFactor = (float) Gdx.graphics.getHeight()/480;
		if (currentXScaleFactor > currentYScaleFactor) {
			currentSmallerScaleFactor = currentYScaleFactor;
		}
		else {
			currentSmallerScaleFactor = currentXScaleFactor;
		}
		playPauseButton = new Circle();
		playPauseButton.set(320*currentXScaleFactor,100*currentYScaleFactor,50*currentSmallerScaleFactor);
		rewindButton = new Circle();
		rewindButton.set(170*currentXScaleFactor,100*currentYScaleFactor,50*currentSmallerScaleFactor);
		fastForwardButton = new Circle();
		fastForwardButton.set(470*currentXScaleFactor,100*currentYScaleFactor,50*currentSmallerScaleFactor);
		songs.add(new Song("Korobeiniki.mp3",56));
		songs.add(new Song("In the Hall of the Mountain King.mp3",154));
		songs.add(new Song("The Liberty Bell.mp3",221));
		songs.add(new Song("Washington Post March.mp3",130));
		songs.add(new Song("Winter.mp3",197));
		songs.add(new Song("Arirang.ogg",88));
		songs.add(new Song("Promenade.ogg",100));
		songs.add(new Song("Vltava.ogg",770));

		currentSongIndex = 0;
		Gdx.input.setInputProcessor(this);
		fillQueue();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		// the following code draws the buttons
		batch.draw(rewindIMG, rewindButton.x-50*currentXScaleFactor, rewindButton.y-50*currentYScaleFactor, 100*currentSmallerScaleFactor, 100*currentSmallerScaleFactor);
		if (paused || !nowPlaying) {
			batch.draw(playIMG, playPauseButton.x-50*currentXScaleFactor, playPauseButton.y-50*currentYScaleFactor, 100*currentSmallerScaleFactor, 100*currentSmallerScaleFactor);
		}
		else {
			batch.draw(pauseIMG, playPauseButton.x-50*currentXScaleFactor, playPauseButton.y-50*currentYScaleFactor, 100*currentSmallerScaleFactor, 100*currentSmallerScaleFactor);
		}
		batch.draw(fastForwardIMG, fastForwardButton.x-50*currentXScaleFactor, fastForwardButton.y-50*currentYScaleFactor, 100*currentSmallerScaleFactor, 100*currentSmallerScaleFactor);
		// done drawing buttons
		batch.draw(progressBackgroundIMG, rewindButton.x-50*currentXScaleFactor, 170*currentYScaleFactor, (fastForwardButton.x-50*currentXScaleFactor)+(100*currentSmallerScaleFactor)-(rewindButton.x-50*currentXScaleFactor), 15*currentSmallerScaleFactor);
		if (nowPlaying) {
			batch.draw(progressForegroundIMG, ((rewindButton.x-55*currentXScaleFactor)+(((fastForwardButton.x-50*currentXScaleFactor)+(100*currentSmallerScaleFactor)-(rewindButton.x-50*currentXScaleFactor))*(currentSong.getPosition()/playQueue.get(currentSongIndex).getDuration()))), 167.5F*currentYScaleFactor, 20*currentSmallerScaleFactor, 20*currentSmallerScaleFactor);
			batch.draw(currentSongArtwork, (320*currentXScaleFactor)-(100*currentSmallerScaleFactor), 250*currentYScaleFactor, 200*currentSmallerScaleFactor, 200*currentSmallerScaleFactor);
		}
		else {
			batch.draw(defaultArt, (320*currentXScaleFactor)-(100*currentSmallerScaleFactor), 250*currentYScaleFactor, 200*currentSmallerScaleFactor, 200*currentSmallerScaleFactor);
			batch.draw(progressForegroundIMG, rewindButton.x-55*currentXScaleFactor, 167.5F*currentYScaleFactor, 20*currentSmallerScaleFactor, 20*currentSmallerScaleFactor);
		}
		batch.end();
	}

	public void resize(int width, int height) {
		//		viewport.update(width,height);
		currentXScaleFactor = (float) width/ (float) 640;
		currentYScaleFactor = (float) height/ (float) 480;
		if (currentXScaleFactor > currentYScaleFactor) {
			currentSmallerScaleFactor = currentYScaleFactor;
		}
		else {
			currentSmallerScaleFactor = currentXScaleFactor;
		}
	}

	public void loadUpSong(Song song, boolean stopCurrent) {
		if (stopCurrent) {
			currentSong.stop();
			currentSong.dispose();
		}
		nowPlaying = true;
		paused = false;
		currentSongArtwork = new Texture(song.getAlbumCoverPath());
		currentSongArtwork.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		currentSong = Gdx.audio.newMusic(Gdx.files.internal(song.getFilename()));
		currentSong.setOnCompletionListener(new Music.OnCompletionListener() {

			@Override
			public void onCompletion(Music music) {
				currentSongIndex += 1;
				if (playQueue.size() < currentSongIndex + 1) {
					fillQueue();
				}
				try {
					try {
						loadUpSong(playQueue.get(currentSongIndex),true);
					}
					catch (java.lang.IndexOutOfBoundsException e) {
						return;
					}
				}
				catch (java.lang.IndexOutOfBoundsException e) {
					return;
				}

			}
		});
		currentSong.setLooping(false);
		currentSong.play();
	}

	public void fillQueue() {
		ArrayList<Song> songs2temp = new ArrayList<Song>(songs);
		int sizeOfSongs2temp = songs2temp.size();
		for (int i = 0; i < sizeOfSongs2temp; i++) {
			int indexOfSongToAdd = (int) (Math.random()*songs2temp.size());
			System.out.println(indexOfSongToAdd);
			playQueue.add(songs2temp.get(indexOfSongToAdd));
			songs2temp.remove(indexOfSongToAdd);
		}
		for (int i = 0; i < playQueue.size(); i++) {
			System.out.println(playQueue.get(i));
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//System.out.println("clicked " +(screenX) +" " +(Gdx.graphics.getHeight()-screenY));
		//System.out.println(currentXScaleFactor +" " +currentYScaleFactor);
		if (playPauseButton.contains(screenX,Gdx.graphics.getHeight()-screenY)) {
			//System.out.println("playpause " +(screenX) +" " +(Gdx.graphics.getHeight()-screenY));
			if (nowPlaying) {
				if (paused) {
					paused = false;
					currentSong.play();
				}
				else {
					paused = true;
					currentSong.pause();
				}
			}
			else {
				if (playQueue.size() < currentSongIndex + 1) {
					fillQueue();
				}
				try {
					loadUpSong(playQueue.get(currentSongIndex),false);
				}
				catch (java.lang.IndexOutOfBoundsException e) {
					return false;
				}
			}
		}
		else if (fastForwardButton.contains(screenX,Gdx.graphics.getHeight()-screenY)) {
			//System.out.println("fastforward " +(screenX) +" " +(Gdx.graphics.getHeight()-screenY));
			if (nowPlaying) {
				currentSongIndex += 1;
				if (playQueue.size() < currentSongIndex + 1) {
					fillQueue();
				}
				try {
					loadUpSong(playQueue.get(currentSongIndex),true);
				}
				catch (java.lang.IndexOutOfBoundsException e) {
					return false;
				}
			}
		}
		else if (rewindButton.contains(screenX,Gdx.graphics.getHeight()-screenY)) {
			//System.out.println("rewind " +(screenX) +" " +(Gdx.graphics.getHeight()-screenY));
			////System.out.println("rewinding");
			if (nowPlaying) {
				if ((!(currentSong.getPosition() > 1)) && (currentSongIndex > 0)) {
					currentSongIndex -= 1;
				}
				try {
					loadUpSong(playQueue.get(currentSongIndex),true);
				}
				catch (java.lang.IndexOutOfBoundsException e) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
