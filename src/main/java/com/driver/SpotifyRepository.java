package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album=new Album(title);
        albums.add(album);
        Artist artist=findArtist(artistName);
        if(artist!=null){
            List<Album> artistAlbums = artistAlbumMap.getOrDefault(artist,new ArrayList<>());
            artistAlbums.add(album);
            artistAlbumMap.put(artist,artistAlbums);
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song=new Song(title,length);
        Album album=findAlbum(albumName);
        if(album==null)
            throw new AlbumNotFound("Album does not exist");
        List<Song> albumSongs = albumSongMap.getOrDefault(album,new ArrayList<>());
        albumSongs.add(song);
        albumSongMap.put(album,albumSongs);
        songs.add(song);
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        User user=findUser(mobile);
        if(user==null)
            throw new AlbumNotFound("User does not exist");
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user,playlist);
        List<Song> playlistSongs=getSongsByLength(length);
        playlistSongMap.put(playlist,playlistSongs);
        List<User> playListListeners= playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        playListListeners.add(user);
        playlistListenerMap.put(playlist,playListListeners);
        List<Playlist> userPlaylists = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user=findUser(mobile);
        if(user==null)
            throw new AlbumNotFound("User does not exist");
        Playlist playlist=new Playlist(title);
        playlists.add(playlist);
        creatorPlaylistMap.put(user,playlist);
        List<Song> playlistSongs=getSongsByTitles(songTitles);
        playlistSongMap.put(playlist,playlistSongs);
        List<User> playListListeners= playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        playListListeners.add(user);
        playlistListenerMap.put(playlist,playListListeners);
        List<Playlist> userPlaylists = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        userPlaylists.add(playlist);
        userPlaylistMap.put(user,userPlaylists);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        User user=findUser(mobile);
        if(user==null)
            throw new AlbumNotFound("User does not exist");
        Playlist playlist=findPlaylistByTitle(playlistTitle);
        if(playlist==null)
            throw new AlbumNotFound("Playlist does not exist");
        List<User> playListListeners= playlistListenerMap.getOrDefault(playlist,new ArrayList<>());
        if(!playListListeners.contains(user))
        {
            playListListeners.add(user);
            playlistListenerMap.put(playlist,playListListeners);
        }
        List<Playlist> userPlaylists = userPlaylistMap.getOrDefault(user,new ArrayList<>());
        if(!userPlaylists.contains(playlist))
        {
            userPlaylists.add(playlist);
            userPlaylistMap.put(user,userPlaylists);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user=findUser(mobile);
        if(user==null)
            throw new AlbumNotFound("User does not exist");
        Song song=findSongByTitle(songTitle);
        if(song==null)
            throw new AlbumNotFound("Song does not exist");
        List<User> likedUsers=songLikeMap.getOrDefault(song,new ArrayList<>());
        if(!likedUsers.contains(user))
        {
            likedUsers.add(user);
            songLikeMap.put(song,likedUsers);
            Artist artist=findArtistBySong(song);
            if(artist!=null)
                artist.setLikes(artist.getLikes()+1);
            song.setLikes(song.getLikes()+1);
        }
        return song;
    }

    public String mostPopularArtist() {
        String artist = null;
        int maxLikes=0;
        for(Artist searchArtist: artists){
            if(searchArtist.getLikes()>maxLikes)
                artist=searchArtist.getName();
        }
        return artist;
    }

    public String mostPopularSong() {
        String song = null;
        int maxLikes=0;
        for(Song searchSong: songs){
            if(searchSong.getLikes()>maxLikes)
                song=searchSong.getTitle();
        }
        return song;
    }

    private Artist findArtist(String artistName){
        for(Artist artist:artists){
            if(artist.getName().equals(artistName))
                return artist;
        }
        return null;
    }

    private Album findAlbum(String albumName){
        for(Album album:albums){
            if(album.getTitle().equals(albumName))
                return album;
        }
        return null;
    }

    private User findUser(String mobile){
        for(User user:users){
            if(user.getMobile().equals(mobile)){
                return user;
            }
        }
        return null;
    }

    private List<Song> getSongsByLength(int length){
        List<Song> songList=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length)
                songList.add(song);
        }
        return songList;
    }

    private List<Song> getSongsByTitles(List<String> songTitles){
        List<Song> songList = new ArrayList<>();
        for(String songTitle:songTitles){
            Song song=findSongByTitle(songTitle);
            if(song!=null)
                songList.add(song);
        }
        return songList;
    }

    private Song findSongByTitle(String title){
        for(Song song:songs){
            if(song.getTitle().equals(title))
                return song;
        }
        return null;
    }

    private Playlist findPlaylistByTitle(String title){
        for(Playlist playlist:playlists){
            if(playlist.getTitle().equals(title))
                return playlist;
        }
        return null;
    }

    private Artist findArtistBySong(Song song){
        for(Map.Entry<Artist,List<Album>> entry:artistAlbumMap.entrySet()){
            List<Song> songs = albumSongMap.get(entry.getValue());

            if(songs!=null && songs.contains(song))
                return entry.getKey();
        }
        return null;
    }
}
