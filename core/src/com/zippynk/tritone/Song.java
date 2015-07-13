// Part of Tritone, an app by Nathan Krantz-Fire. https://github.com/zippynk/tritone

//  This Source Code Form is subject to the terms of the Mozilla Public
//  License, v. 2.0. If a copy of the MPL was not distributed with this
//  file, You can obtain one at http://mozilla.org/MPL/2.0/.

package com.zippynk.tritone;

public class Song {
	private String filename;
	private String songname;
	private String artist;
	private String albumCoverPath;
	private float duration;
	
	public Song(String myfilename, float myduration, String mysongname, String myartist, String myalbumCoverPath) {
		filename = myfilename;
		songname = mysongname;
		artist = myartist;
		albumCoverPath = myalbumCoverPath;
		duration = myduration;
	}
	
	public Song(String myfilename, float myduration, String mysongname, String myartist) {
		filename = myfilename;
		songname = mysongname;
		artist = myartist;
		albumCoverPath = "DefaultAlbumCover.png";
		duration = myduration;
	}
	
	public Song(String myfilename, float myduration, String mysongname) {
		filename = myfilename;
		songname = mysongname;
		artist = "Unknown Artist";
		albumCoverPath = "DefaultAlbumCover.png";
		duration = myduration;
	}
	
	public Song(String myfilename, float myduration) {
		filename = myfilename;
		songname = myfilename;
		artist = "Unknown Artist";
		albumCoverPath = "DefaultAlbumCover.png";
		duration = myduration;
	}
	
	public float getDuration() {
		return duration;
	}
	public String getFilename() {
		return filename;
	}
	public String getSongname() {
		return songname;
	}
	public String getArtist() {
		return artist;
	}
	public String getAlbumCoverPath() {
		return albumCoverPath;
	}

}
