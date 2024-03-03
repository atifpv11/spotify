package com.driver;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class SpotifyService {

    //Auto-wire will not work in this case, no need to change this and add autowire

    SpotifyRepository spotifyRepository = new SpotifyRepository();

    public User createUser(String name, String mobile){
        return spotifyRepository.createUser(name,mobile);
    }

    public Artist createArtist(String name) {
        return spotifyRepository.createArtist(name);
    }

    public Album createAlbum(String title, String artistName) {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
        Artist artist=getOrCreateArtist(artistName);
        return spotifyRepository.createAlbum(title, artistName);
    }

    public Song createSong(String title, String albumName, int length) throws Exception {
        Album album=getAlbumByName(albumName);
        return spotifyRepository.createSong(title, albumName, length);
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=getUserByMobile(mobile);
        return spotifyRepository.createPlaylistOnLength(mobile, title, length);
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=getUserByMobile(mobile);
        return spotifyRepository.createPlaylistOnName(mobile, title, songTitles);
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=getUserByMobile(mobile);
        Playlist playlist=getPlaylistByTitle(playlistTitle);
        return spotifyRepository.findPlaylist(mobile, playlistTitle);
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=getUserByMobile(mobile);
        Song song=getSongByTitle(songTitle);
        return spotifyRepository.likeSong(mobile, songTitle);
    }

    public String mostPopularArtist() {
        return spotifyRepository.mostPopularArtist();
    }

    public String mostPopularSong() {
        return spotifyRepository.mostPopularSong();
    }

    private Artist getOrCreateArtist(String artistName){
        for(Artist artist: spotifyRepository.artists){
            if(artist.getName().equals(artistName))
                return artist;
        }
        return createArtist(artistName);
    }

    private Album getAlbumByName(String albumName) throws Exception{
        for(Album album: spotifyRepository.albums){
            if(album.getTitle().equals(albumName))
                return album;
        }
        throw new AlbumNotFound("Album does not exist");
    }

    private User getUserByMobile(String mobile) throws Exception{
        for(User user:spotifyRepository.users){
            if(user.getMobile().equals(mobile))
                return user;
        }
        throw new AlbumNotFound("User does not exist");
    }

    private Playlist getPlaylistByTitle(String playlistTitle) throws Exception {
        for(Playlist playlist: spotifyRepository.playlists){
            if(playlist.getTitle().equals(playlistTitle))
                return playlist;
        }
        throw new AlbumNotFound("Playlist does not exist");
    }

    private Song getSongByTitle(String songTitle) throws Exception {
        for(Song song: spotifyRepository.songs){
            if(song.getTitle().equals(songTitle))
                return song;
        }
        throw new AlbumNotFound("Song does not exist");
    }
}
