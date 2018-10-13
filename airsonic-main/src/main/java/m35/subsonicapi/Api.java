package m35.subsonicapi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.airsonic.player.util.StringUtil;
import org.subsonic.restapi.*;

public class Api {


    
    /*
    Used to test connectivity with the server. Takes no extra parameters.
    Returns an empty <subsonic-response> element on success.
    */
    public void ping() {
    }

    /*
    Get details about the software license. Takes no extra parameters. Please note that access to the REST API requires that the server has a valid license (after a 30-day trial period). To get a license key you must upgrade to Subsonic Premium.
    Returns a <subsonic-response> element with a nested <license> element on success.     
    */
    public License getLicense() {
        License license = new License();
        license.setValid(true);
        return license;
    }

    /*
    Returns all configured top-level music folders. Takes no extra parameters.
    Returns a <subsonic-response> element with a nested <musicFolders> element on success.
    */
    public MusicFolders getMusicFolders() {
        MusicFolders musicFolders = new MusicFolders();
        MusicFolder musicFolder = new MusicFolder();
        //return musicFolders;
    }

    /*
    Returns an indexed structure of all artists. 
    Returns a <subsonic-response> element with a nested <indexes> element on success.
    */
    public Indexes getIndexes(
            Integer musicFolderId,   // If specified, only return artists in the music folder with the given ID. See getMusicFolders. 
            Long ifModifiedSince  // [opt] If specified, only return a result if the artist collection has changed since the given time (in milliseconds since 1 Jan 1970). 
    ) {
        Objects.requireNonNull(musicFolderId, "musicFolderId required");
        Indexes indexes = new Indexes();
        //return indexes;
    }

    /*
    Returns a listing of all files in a music directory. Typically used to get list of albums for an artist, or list of songs for an album. 
    Returns a <subsonic-response> element with a nested <directory> element on success.
    */
    public Directory getMusicDirectory(
            String id   // A string which uniquely identifies the music folder. Obtained by calls to getIndexes or getMusicDirectory. 
    ) {
        Objects.requireNonNull(id, "id required");
        Directory directory = new Directory();
        //return directory;
    }

    /*
    Returns all genres.
    Returns a <subsonic-response> element with a nested <genres> element on success.
    */
    public Genres getGenres() {
        Genres genres = new Genres();
        Genre genre = new Genre();
        //return genres;
    }

    /*
    Similar to getIndexes, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <artists> element on success.
    */
    public ArtistsID3 getArtists(
            Integer musicFolderId    // [opt] If specified, only return artists in the music folder with the given ID. See getMusicFolders. 
    ) {
        ArtistsID3 artists = new ArtistsID3();
        IndexID3 indexID3 = new IndexID3();
        //return artists;
    }

    /*
    Returns details for an artist, including a list of albums. This method organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <artist> element on success.
    */
    // The API lies, the XSD only allows ArtistWithAlbumsID3
    public ArtistWithAlbumsID3 getArtist(
            String id       // The artist ID.
    ) {
        Objects.requireNonNull(id, "id required");
        ArtistWithAlbumsID3 artist = new ArtistWithAlbumsID3();
        //return artist;
    }

    /*
    Returns details for an album, including a list of songs. This method organizes music according to ID3 tags. 
    urns a <subsonic-response> element with a nested <album> element on success.
    */
    public AlbumWithSongsID3 getAlbum(
            String id   // The album ID.
    ) {
        Objects.requireNonNull(id, "id required");
        AlbumWithSongsID3 album = new AlbumWithSongsID3();
        //return album;
    }
    
    /*
    Returns details for a song. 
    Returns a <subsonic-response> element with a nested <song> element on success.
    */
    public Child getSong(
            String id   // The song ID.
    ) {
        Objects.requireNonNull(id, "id required");
        Child song = new Child();
        //return song;
    }

    /*
    Returns all video files. 
    Returns a <subsonic-response> element with a nested <videos> element on success.
    */
    public Videos getVideos() {
        Videos videos = new Videos();
        Child child = new Child();
        return videos;
    }

    /*
    Returns details for a video, including information about available audio tracks, subtitles (captions) and conversions. 
    Returns a <subsonic-response> element with a nested <videoInfo> element on success.
    */
    public VideoInfo getVideoInfo(
            String id // The video ID.
    ) {
        Objects.requireNonNull(id, "id required");
        VideoInfo videoInfo = new VideoInfo();
        throw new UnsupportedOperationException();
    }
    
    /*
    Returns artist info with biography, image URLs and similar artists, using data from last.fm.
    Returns a <subsonic-response> element with a nested <artistInfo> element on success.
    */
    public ArtistInfo getArtistInfo(
            String id,  // The artist, album or song ID.
            Integer count,   // [default:20]   Max number of similar artists to return.
            Boolean includeNotPresent    // [default:false] Whether to return artists that are not present in the media library.
    ) {
        Objects.requireNonNull(id, "id required");
        ArtistInfo artistInfo = new ArtistInfo();
        //return artistInfo;
    }
    
    /*
    Similar to getArtistInfo, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <artistInfo2> element on success.
    */
    public ArtistInfo2 getArtistInfo2(
        String id,  // The artist ID.
        Integer count, // [default:20] Max number of similar artists to return.
        Boolean includeNotPresent // [default:false]   Whether to return artists that are not present in the media library.        
    ) {
        Objects.requireNonNull(id, "id required");
        ArtistInfo2 artistInfo = new ArtistInfo2();
        //return artistInfo;
    }
    
    /*
    Returns album notes, image URLs etc, using data from last.fm. 
    Returns a <subsonic-response> element with a nested <albumInfo> element on success.
    */
    public AlbumInfo getAlbumInfo(
            String id // The album or song ID.
    ) {
        Objects.requireNonNull(id, "id required");
        AlbumInfo albumInfo = new AlbumInfo();
        //return albumInfo;
    }

    /*
    Similar to getAlbumInfo, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <albumInfo> element on success.
    */
    public AlbumInfo getAlbumInfo2(
            String id // The album ID.
    ) {
        Objects.requireNonNull(id, "id required");
        AlbumInfo albumInfo = new AlbumInfo();
        //return albumInfo;
    }
    
    
    /*
    Returns a random collection of songs from the given artist and similar artists, using data from last.fm. Typically used for artist radio features.
    Returns a <subsonic-response> element with a nested <similarSongs> element on success.
    */
    public SimilarSongs getSimilarSongs(
            String id, // The artist, album or song ID.
            Integer count // [default:50] Max number of songs to return.
    ) {
        Objects.requireNonNull(id, "id required");
        SimilarSongs similarSongs = new SimilarSongs();
        Child child = new Child();
        return similarSongs;
    }

    /*
    Similar to getSimilarSongs, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <similarSongs2> element on success.
    */
    public SimilarSongs2 getSimilarSongs2(
            String id, // The artist ID.
            Integer count // [default:50]  Max number of songs to return.
    ) {
        Objects.requireNonNull(id, "id required");
        SimilarSongs2 similarSongs = new SimilarSongs2();
        Child child = new Child();
        return similarSongs;
    }
    
    /*
    Returns top songs for the given artist, using data from last.fm. 
    Returns a <subsonic-response> element with a nested <topSongs> element on success.
    */
    public TopSongs getTopSongs(
            String artist,  // The artist name
            Integer count  // [default:50]  Max number of songs to return.
    ) {
        Objects.requireNonNull(artist, "artist required");
        TopSongs topSongs = new TopSongs();
        Child child = new Child();
        //return topSongs;
    }
    
    
    /*
    Returns a list of random, newest, highest rated etc. albums. Similar to the album lists on the home page of the Subsonic web interface. 
    Returns a <subsonic-response> element with a nested <albumList> element on success.
    */
    public AlbumList getAlbumList(
        String type, //  The list type. Must be one of the following: random, newest, highest, frequent, recent. Since 1.8.0 you can also use alphabeticalByName or alphabeticalByArtist to page through all albums alphabetically, and starred to retrieve starred albums. Since 1.10.1 you can use byYear and byGenre to list albums in a given year range or genre.
        Integer size, // [default:10] The number of albums to return. Max 500.
        Integer offset, // [default:0] The list offset. Useful if you for example want to page through the list of newest albums.
        Integer fromYear, // [req if type=year] The first year in the range. If fromYear > toYear a reverse chronological list is returned.
        Integer toYear, // [req if type=year] The last year in the range.
        String genre, // [req if type=genre] The name of the genre, e.g., "Rock".
        Integer musicFolderId // [opt] (Since 1.11.0) Only return albums in the music folder with the given ID. See getMusicFolders.
            
    ) {
        Objects.requireNonNull(type, "type required");
        AlbumList albumList = new AlbumList();
        Child child = new Child();
        //return albumList;
    }

    /*
    Similar to getAlbumList, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <albumList2> element on success.
    */
    public AlbumList2 getAlbumList2(
        String type, //  The list type. Must be one of the following: random, newest, frequent, recent, starred, alphabeticalByName or alphabeticalByArtist. Since 1.10.1 you can use byYear and byGenre to list albums in a given year range or genre.
        Integer size, // [default:10] The number of albums to return. Max 500.
        Integer offset, // [default:0] The list offset. Useful if you for example want to page through the list of newest albums.
        Integer fromYear, // [req if type=year] The first year in the range. If fromYear > toYear a reverse chronological list is returned.
        Integer toYear, // [req if type=year] The last year in the range.
        String genre, // [req if type=genre] The name of the genre, e.g., "Rock".
        Integer musicFolderId // [opt] (Since 1.12.0) Only return albums in the music folder with the given ID. See getMusicFolders.
    ) {
        AlbumList2 albumList = new AlbumList2();
        AlbumID3 albumID3 = new AlbumID3();
        //return albumList;
    }

    /*
    Returns random songs matching the given criteria. 
    Returns a <subsonic-response> element with a nested <randomSongs> element on success.
    */
    public Songs getRandomSongs(
        Integer size, // [default:10] The maximum number of songs to return. Max 500.
        String genre, // [opt] Only returns songs belonging to this genre.
        Integer fromYear, // [opt] Only return songs published after or in this year.
        Integer toYear, // [opt] Only return songs published before or in this year.
        Integer musicFolderId // [opt] Only return songs in the music folder with the given ID. See getMusicFolders.
    ) {
        Songs songs = new Songs();
        Child child = new Child();
        //return songs;
    }

    /*
    Returns songs in a given genre. 
    Returns a <subsonic-response> element with a nested <songsByGenre> element on success.
    */
    public Songs getSongsByGenre(
        Object genre, //  The genre, as returned by getGenres.
        Integer count, // [default:10] The maximum number of songs to return. Max 500.
        Integer offset, // [default:0] The offset. Useful if you want to page through the songs in a genre.
        Integer musicFolderId // [opt] (Since 1.12.0) Only return albums in the music folder with the given ID. See getMusicFolders.
    ) {
        Songs songs = new Songs();
        Child child = new Child();
        //return songs;
    }

    /*
    Returns what is currently being played by all users. Takes no extra parameters. 
    Returns a <subsonic-response> element with a nested <nowPlaying> element on success.
    */
    public NowPlaying getNowPlaying() {
        NowPlaying nowPlaying = new NowPlaying();
        NowPlayingEntry nowPlayingEntry = new NowPlayingEntry();
        return nowPlaying;
    }

    /*
    Returns starred songs, albums and artists. 
    Returns a <subsonic-response> element with a nested <starred> element on success.
    */
    public Starred getStarred(
            Integer musicFolderId // [opt] (Since 1.12.0) Only return results from the music folder with the given ID. See getMusicFolders.
    ) {
        Starred starred = new Starred();
        return starred;
    }

    /*
    Similar to getStarred, but organizes music according to ID3 tags. 
    */
    public Starred2 getStarred2(
            Integer musicFolderId // [opt] (Since 1.12.0) Only return results from the music folder with the given ID. See getMusicFolders.
    ) {
        Starred2 starred = new Starred2();
        return starred;
    }


    /*
    Deprecated since 1.4.0, use search2 instead.
    Returns a listing of files matching the given search criteria. Supports paging through the result. 
    Returns a <subsonic-response> element with a nested <searchResult> element on success.
    */
    @Deprecated
    public SearchResult search(
        String artist, // [opt] Artist to search for.
        String album, // [opt] Album to searh for.
        String title, // [opt] Song title to search for.
        Object any, // [opt] Searches all fields.
        Integer count, // [default:20] Maximum number of results to return.
        Integer offset, // [default:0] Search result offset. Used for paging.
        Long newerThan // [opt] Only return matches that are newer than this. Given as milliseconds since 1970.
    ) {
        SearchResult searchResult = new SearchResult();
        return searchResult;
    }

    /*
    Returns albums, artists and songs matching the given search criteria. Supports paging through the result. 
    Returns a <subsonic-response> element with a nested <searchResult2> element on success.
    */
    public SearchResult2 search2(
        String query, //  Search query.
        Integer artistCount, // [default:20] Maximum number of artists to return.
        Integer artistOffset, // [default:0] Search result offset for artists. Used for paging.
        Integer albumCount, // [default:20] Maximum number of albums to return.
        Integer albumOffset, // [default:0] Search result offset for albums. Used for paging.
        Integer songCount, // [default:20] Maximum number of songs to return.
        Integer songOffset, // [default:0] Search result offset for songs. Used for paging.
        Integer musicFolderId // [opt] (Since 1.12.0) Only return results from the music folder with the given ID. See getMusicFolders.
    ) {
        SearchResult2 searchResult = new SearchResult2();
        return searchResult;
    }

    /*
    Similar to search2, but organizes music according to ID3 tags. 
    Returns a <subsonic-response> element with a nested <searchResult3> element on success.
    */
    public SearchResult3 search3(
        String query, //  Search query.
        Integer artistCount, // [default:20] Maximum number of artists to return.
        Integer artistOffset, // [default:0] Search result offset for artists. Used for paging.
        Integer albumCount, // [default:20] Maximum number of albums to return.
        Integer albumOffset, // [default:0] Search result offset for albums. Used for paging.
        Integer songCount, // [default:20] Maximum number of songs to return.
        Integer songOffset, // [default:0] Search result offset for songs. Used for paging.
        Integer musicFolderId // [opt] (Since 1.12.0) Only return results from music folder with the given ID. See getMusicFolders.
    ) {
        SearchResult3 searchResult = new SearchResult3();
        return searchResult;
    }

    /*
    Returns all playlists a user is allowed to play. 
    Returns a <subsonic-response> element with a nested <playlists> element on success.
    */
    public Playlists getPlaylists(
            String username // [opt] (Since 1.8.0) If specified, return playlists for this user rather than for the authenticated user. The authenticated user must have admin role if this parameter is used. 
    ) {
        Playlists playlists = new Playlists();
        return playlists;
    }

    /*
    Returns a listing of files in a saved playlist. 
    Returns a <subsonic-response> element with a nested <playlist> element on success.
    */
    // The api lies, only PlaylistWithSongs
    public PlaylistWithSongs getPlaylist(
            String id //   ID of the playlist to return, as obtained by getPlaylists.
    ) {
        Objects.requireNonNull(id, "id required");
        PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();
        throw new UnsupportedOperationException();
    }

    /*
    Creates (or updates) a playlist. 
    Since 1.14.0 the newly created/updated playlist is returned. In earlier versions an empty <subsonic-response> element is returned. 
    */
    // The api lies, only PlaylistWithSongs
    public PlaylistWithSongs createPlaylist(
        String playlistId, // [req if updating] The playlist ID.
        String name, // [req if updating] The human-readable name of the playlist.
        List<String> songId // [opt] ID of a song in the playlist. Use one songId parameter for each song in the playlist.
    ) {
        PlaylistWithSongs playlistWithSongs = new PlaylistWithSongs();
        //return playlistWithSongs;
    }


    /*
    Updates a playlist. Only the owner of a playlist is allowed to update it. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void updatePlaylist(
        String playlistId, //  The playlist ID.
        String name, // [opt] The human-readable name of the playlist.
        String comment, // [opt] The playlist comment.
        Boolean public_, // [default:false?] true if the playlist should be visible to all users, false otherwise.
        List<Integer> songIdToAdd, // [opt] Add this song with this ID to the playlist. Multiple parameters allowed.
        List<Integer> songIndexToRemove // [opt] Remove the song at this position in the playlist. Multiple parameters allowed.
    ) {
    }

    
    /*
    http://your-server/rest/deletePlaylist Since 1.2.0 
    Deletes a saved playlist. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deletePlaylist(
            String id //  ID of the playlist to delete, as obtained by getPlaylists.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/stream Since 1.0.0 
    Streams a given media file. 
    Returns binary data on success, or an XML document on error (in which case the HTTP content type will start with "text/xml"). 
    */
    public BinaryResponse stream(
            String id, //  A string which uniquely identifies the file to stream. Obtained by calls to getMusicDirectory.
            Integer maxBitRate, // [opt] (Since 1.2.0) If specified, the server will attempt to limit the bitrate to this value, in kilobits per second. If set to zero, no limit is imposed. 
            String format, // [opt] (Since 1.6.0) Specifies the preferred target format (e.g., "mp3" or "flv") in case there are multiple applicable transcodings. Starting with 1.9.0 you can use the special value "raw" to disable transcoding. 
            Integer timeOffset, // [opt] Only applicable to video streaming. If specified, start streaming at the given offset (in seconds) into the video. Typically used to implement video skipping. 
            Integer size, // [opt] (Since 1.6.0) Only applicable to video streaming. Requested video size specified as WxH, for instance "640x480". 
            Boolean estimateContentLength, // [default:false] (Since 1.8.0). If set to "true", the Content-Length HTTP header will be set to an estimated value for transcoded or downsampled media. 
            Boolean converted // [default:false] (Since 1.14.0) Only applicable to video streaming. Subsonic can optimize videos for streaming by converting them to MP4. If a conversion exists for the video in question, then setting this parameter to "true" will cause the converted video to be returned instead of the original. 
    ) {
        Objects.requireNonNull(id, "id required");
    }
    
    public BinaryResponse stream_implementation(
            String id,
            Integer maxBitRate,
            String format,
            Boolean estimateContentLength,
            
            Integer offsetSeconds, // !!!
            String suffix,
            String playlist, // id
            Boolean hls,
            Integer duration,
            Integer path,
            String icy_metadata_header,
            String range_header
    ) {
        
    }

    /*
    http://your-server/rest/download Since 1.0.0 
    Downloads a given media file. Similar to stream, but this method returns the original media data without transcoding or downsampling. 
    Returns binary data on success, or an XML document on error (in which case the HTTP content type will start with "text/xml"). 
    */
    // the api lies like crazy! implimentation allows several parameters and downloading lists
    public BinaryResponse download(
            String id //  A string which uniquely identifies the file to download. Obtained by calls to getMusicDirectory.
    ) {
        return download_implementation(id, null, null, null);
    }
    
    /*
    As it is in the implementation.
    */
    public BinaryResponse download_implementation(
        String id, // song id: a single song
        String playlist, // playlist id: all files in the playlist. If 1 song, download it only. If > 1 song, combine into a zip file
        String player, // player id: all the files playing in the player. If 1 song, download it only. If > 1 song, combine into a zip file
        List<String> i, // Indexes in the list of the songs to download.
        String range_header
    ) {
        
    }

    /*
    http://your-server/rest/hls.m3u8 Since 1.8.0 
    Creates an HLS (HTTP Live Streaming) playlist used for streaming video or audio. HLS is a streaming protocol implemented by Apple and works by breaking the overall stream into a sequence of small HTTP-based file downloads. It's supported by iOS and newer versions of Android. This method also supports adaptive bitrate streaming, see the bitRate parameter. 
    Returns an M3U8 playlist on success (content type "application/vnd.apple.mpegurl"), or an XML document on error (in which case the HTTP content type will start with "text/xml"). 
    */
    public BinaryResponse hls(
            String id, //  A string which uniquely identifies the media file to stream.
            List<String> bitrate, // [opt] If specified, the server will attempt to limit the bitrate to this value, in kilobits per second. If this parameter is specified more than once, the server will create a variant playlist, suitable for adaptive bitrate streaming. The playlist will support streaming at all the specified bitrates. The server will automatically choose video dimensions that are suitable for the given bitrates. Since 1.9.0 you may explicitly request a certain width (480) and height (360) like so: bitRate=1000@480x360 
            String audioTrack // [opt] The ID of the audio track to use. See getVideoInfo for how to get the list of available audio tracks for a video. 
    ) {
        Objects.requireNonNull(id, "id required");
    }
    
    public BinaryResponse hls_implementation(
            String id,
            List<String> bitrate,
            String audioTrack,
            String player
    ) {
        
    }

    /*
    http://your-server/rest/getCaptions Since 1.14.0 
    Returns captions (subtitles) for a video. Use getVideoInfo to get a list of available captions. 
    Returns the raw video captions. 
    */
    public void getCaptions(
            String id, //  The ID of the video.
            String format // [opt] Preferred captions format ("srt" or "vtt").
    ) {
        Objects.requireNonNull(id, "id required");
        // what is this supposed to return?
        throw new UnsupportedOperationException();
    }

    public static class BinaryResponse {
        private final InputStream stream;
        private final String mimeType;
        private final Integer length;

        public BinaryResponse(InputStream stream, String mimeType, Integer length) {
            this.stream = stream;
            this.mimeType = mimeType;
            this.length = length;
        }

        public InputStream getStream() {
            return stream;
        }

        public String getMimeType() {
            return mimeType;
        }
        
        public Integer getLength() {
            return length;
        }
    }
    
    /*
    http://your-server/rest/getCoverArt Since 1.0.0 
    Returns a cover art image. 
    Returns the cover art image in binary form. 
    */
    public BinaryResponse getCoverArt(
            String id, //  The ID of a song, album or artist.
            Integer size // [opt] If specified, scale image to this size.
    ) {
        Objects.requireNonNull(id, "id required");
        // size = square width and height
        int iSize = size == null ? 128 : size;
        BufferedImage bufferedImage = new BufferedImage(iSize, iSize, BufferedImage.TYPE_INT_RGB);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return new BinaryResponse(new ByteArrayInputStream(baos.toByteArray()), StringUtil.getMimeType("png"));
        } catch (IOException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*
    http://your-server/rest/getLyrics Since 1.2.0 
    Searches for and returns lyrics for a given song. 
    Returns a <subsonic-response> element with a nested <lyrics> element on success. The <lyrics> element is empty if no matching lyrics was found. 
    */
    public Lyrics getLyrics(
            String artist, // [opt] The artist name.
            String title // [opt] The song title.
    ) {
        Lyrics lyrics = new Lyrics();
        return lyrics;
    }

    /*
    http://your-server/rest/getAvatar Since 1.8.0 
    Returns the avatar (personal image) for a user. 
    Returns the avatar image in binary form. 
    */
    public BinaryResponse getAvatar(
            String username //  The user in question.
    ) {
        BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
    }
    
    public BinaryResponse getAvatar_implementation(
        String username,
        String id, // getSystemAvatar
        Boolean forceCustom // force custom avatar
    ) {
        
    }

    /*
    http://your-server/rest/star Since 1.8.0 
    Attaches a star to a song, album or artist. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void star(
            List<String> id, // [opt] The ID of the file (song) or folder (album/artist) to star. Multiple parameters allowed.
            List<String> albumId, // [opt] The ID of an album to star. Use this rather than id if the client accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed. 
            List<String> artistId // [opt] The ID of an artist to star. Use this rather than id if the client accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed. 
    ) {
    }

    /*
    http://your-server/rest/unstar Since 1.8.0 
    Removes the star from a song, album or artist. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void unstar(
            List<String> id, // [opt] The ID of the file (song) or folder (album/artist) to unstar. Multiple parameters allowed.
            List<String> albumId, // [opt] The ID of an album to unstar. Use this rather than id if the client accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed. 
            List<String> artistId // [opt] The ID of an artist to unstar. Use this rather than id if the client accesses the media collection according to ID3 tags rather than file structure. Multiple parameters allowed. 
    ) {
    }

    /*
    http://your-server/rest/setRating Since 1.6.0 
    Sets the rating for a music file. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void setRating(
            String id, //  A string which uniquely identifies the file (song) or folder (album/artist) to rate.
            int rating //  The rating between 1 and 5 (inclusive), or 0 to remove the rating.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/scrobble Since 1.5.0 
    Registers the local playback of one or more media files. Typically used when playing media that is cached on the client. This operation includes the following: 
    * "Scrobbles" the media files on last.fm if the user has configured his/her last.fm credentials on the Subsonic server (Settings > Personal). 
    * Updates the play count and last played timestamp for the media files. (Since 1.11.0) 
    * Makes the media files appear in the "Now playing" page in the web app, and appear in the list of songs returned by getNowPlaying (Since 1.11.0)
    Since 1.8.0 you may specify multiple id (and optionally time) parameters to scrobble multiple files. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void scrobble(
            String id, //  A string which uniquely identifies the file to scrobble.
            Long time, // [opt] (Since 1.8.0) The time (in milliseconds since 1 Jan 1970) at which the song was listened to. 
            Boolean submission // [default:true] Whether this is a "submission" or a "now playing" notification.
    ) {
        Objects.requireNonNull(id, "id required");
        scrobble_implementation(Collections.singletonList(id), Collections.singletonList(time), submission);
    }
    
    // implementation allows for multiple
    public void scrobble_implementation(
        List<String> id,
        List<Long> time,
        Boolean submission
    ) {
        assert id.size() == time.size();
                
        TODO;
    }

    /*
    http://your-server/rest/getShares Since 1.6.0 
    Returns information about shared media this user is allowed to manage. Takes no extra parameters. 
    Returns a <subsonic-response> element with a nested <shares> element on success. 
    */
    public Shares getShares() {
        Shares shares = new Shares();
        Share share = new Share();
        return shares;
    }

    /*
    http://your-server/rest/createShare Since 1.6.0 
    Creates a public URL that can be used by anyone to stream music or video from the Subsonic server. The URL is short and suitable for posting on Facebook, Twitter etc. Note: The user must be authorized to share (see Settings > Users > User is allowed to share files with anyone). 
    Returns a <subsonic-response> element with a nested <shares> element on success, which in turns contains a single <share> element for the newly created share. 
    */
    public Share createShare(
            List<String> id, //  ID of a song, album or video to share. Use one id parameter for each entry to share.
            String description, // [opt] A user-defined description that will be displayed to people visiting the shared media.
            Long expires // [opt] The time at which the share expires. Given as milliseconds since 1970.
    ) {
        Objects.requireNonNull(id, "id required");
        if (id.isEmpty())
            throw new IllegalArgumentException();
        Share share = new Share();
        return share;
    }

    /*
    http://your-server/rest/updateShare Since 1.6.0 
    Updates the description and/or expiration date for an existing share. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void updateShare(
            String id, //  ID of the share to update.
            String description, // [opt] A user-defined description that will be displayed to people visiting the shared media.
            Long expires // [opt] The time at which the share expires. Given as milliseconds since 1970, or zero to remove the expiration. 
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/deleteShare Since 1.6.0 
    Deletes an existing share. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deleteShare(
            String id //  ID of the share to delete.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/getPodcasts Since 1.6.0 
    Returns all Podcast channels the server subscribes to, and (optionally) their episodes. This method can also be used to return details for only one channel - refer to the id parameter. A typical use case for this method would be to first retrieve all channels without episodes, and then retrieve all episodes for the single channel the user selects. 
    Returns a <subsonic-response> element with a nested <podcasts> element on success. 
    */
    public Podcasts getPodcasts(
            Boolean includeEpisodes, // [default:true] (Since 1.9.0) Whether to include Podcast episodes in the returned result.
            String id // [opt] (Since 1.9.0) If specified, only return the Podcast channel with this ID.
    ) {
        Podcasts podcasts = new Podcasts();
        PodcastChannel podcastChannel = new PodcastChannel();
        return podcasts;
    }
    
    /*
    http://your-server/rest/getNewestPodcasts Since 1.13.0 
    Returns the most recently published Podcast episodes. 
    Returns a <subsonic-response> element with a nested <newestPodcasts> element on success. 
    */
    public NewestPodcasts getNewestPodcasts(
            Integer count // [default:20] The maximum number of episodes to return.
    ) {
        NewestPodcasts newestPodcasts = new NewestPodcasts();
        PodcastEpisode podcastEpisode = new PodcastEpisode();
        return newestPodcasts;
    }

    /*
    http://your-server/rest/refreshPodcasts Since 1.9.0 
    Requests the server to check for new Podcast episodes. Note: The user must be authorized for Podcast administration (see Settings > Users > User is allowed to administrate Podcasts). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void refreshPodcasts() {
    }

    /*
    http://your-server/rest/createPodcastChannel Since 1.9.0 
    Adds a new Podcast channel. Note: The user must be authorized for Podcast administration (see Settings > Users > User is allowed to administrate Podcasts). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void createPodcastChannel(
            String url //  The URL of the Podcast to add.
    ) {
    }

    /*
    http://your-server/rest/deletePodcastChannel Since 1.9.0 
    Deletes a Podcast channel. Note: The user must be authorized for Podcast administration (see Settings > Users > User is allowed to administrate Podcasts). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deletePodcastChannel(
            String id //  The ID of the Podcast channel to delete.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/deletePodcastEpisode Since 1.9.0 
    Deletes a Podcast episode. Note: The user must be authorized for Podcast administration (see Settings > Users > User is allowed to administrate Podcasts). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deletePodcastEpisode(
            String id //  The ID of the Podcast episode to delete.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/downloadPodcastEpisode Since 1.9.0 
    Request the server to start downloading a given Podcast episode. Note: The user must be authorized for Podcast administration (see Settings > Users > User is allowed to administrate Podcasts). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void downloadPodcastEpisode(
            String id //  The ID of the Podcast episode to download.
    ) {
        Objects.requireNonNull(id, "id required");
    }

      
    /*
    http://your-server/rest/jukeboxControl Since 1.2.0 
    Controls the jukebox, i.e., playback directly on the server's audio hardware. 
    Note: The user must be authorized to control the jukebox (see Settings > Users > User is allowed to play files in jukebox mode). 
    Returns a <jukeboxStatus> element on success, unless the get action is used, in which case a nested <jukeboxPlaylist> element is returned.
    */
    public JukeboxStatus jukeboxControl(
            String action, //  The operation to perform. Must be one of: get, status (since 1.7.0), set (since 1.7.0), start, stop, skip, add, clear, remove, shuffle, setGain 
            Integer index, // [opt] Used by skip and remove. Zero-based index of the song to skip to or remove. 
            Integer offset, // [opt] (Since 1.7.0) Used by skip. Start playing this many seconds into the track. 
            List<String> id, // [opt] Used by add and set. ID of song to add to the jukebox playlist. Use multiple id parameters to add many songs in the same request. 
                              //       (set is similar to a clear followed by a add, but will not change the currently playing track.) 
            Float gain // [opt] Used by setGain to control the playback volume. A float value between 0.0 and 1.0.
    ) {
        Objects.requireNonNull(action, "action required");
        JukeboxStatus jukeboxStatus = new JukeboxPlaylist();
        jukeboxStatus.setPlaying(false);
        jukeboxStatus.setCurrentIndex(0);
        jukeboxStatus.setGain(1.0f);
        switch (action) {
            case "get":
                JukeboxPlaylist jukeboxPlaylist = new JukeboxPlaylist();
                jukeboxPlaylist.setPlaying(false);
                jukeboxPlaylist.setCurrentIndex(0);
                jukeboxPlaylist.setGain(1.0f);
                Child child = new Child();
                return jukeboxPlaylist;
            case "status":
                return jukeboxStatus;
            case "set":
                Objects.requireNonNull(id, "id required");
                return jukeboxStatus;
            case "start":
                return jukeboxStatus;
            case "stop":
                return jukeboxStatus;
            case "skip":
                Objects.requireNonNull(index, "index required");
                Objects.requireNonNull(offset, "offset required");
                return jukeboxStatus;
            case "add":
                Objects.requireNonNull(id, "id required");
                return jukeboxStatus;
            case "clear":
                return jukeboxStatus;
            case "remove":
                Objects.requireNonNull(index, "index required");
                return jukeboxStatus;
            case "shuffle":
                return jukeboxStatus;
            case "setGain":
                Objects.requireNonNull(gain, "gain required");
                return jukeboxStatus;
            default:
                throw new IllegalArgumentException();
        }
    }

    /*
    http://your-server/rest/getInternetRadioStations Since 1.9.0 
    Returns all internet radio stations. Takes no extra parameters. 
    Returns a <subsonic-response> element with a nested <internetRadioStations> element on success. 
    */
    public InternetRadioStations getInternetRadioStations() {
        InternetRadioStations internetRadioStations = new InternetRadioStations();
        InternetRadioStation internetRadioStation = new InternetRadioStation();
        return internetRadioStations;
    }

    /*
    http://your-server/rest/createInternetRadioStation Since 1.16.0 
    Adds a new internet radio station. Only users with admin privileges are allowed to call this method. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void createInternetRadioStation(
            String streamUrl, //  The stream URL for the station.
            String name, //  The user-defined name for the station.
            String homepageUrl // [opt] The home page URL for the station.
    ) {
    }

    /*
    http://your-server/rest/updateInternetRadioStation Since 1.16.0 
    Updates an existing internet radio station. Only users with admin privileges are allowed to call this method. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void updateInternetRadioStation(
            String id, //  The ID for the station.
            String streamUrl, //  The stream URL for the station.
            String name, //  The user-defined name for the station.
            String homepageUrl // [opt] The home page URL for the station.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/deleteInternetRadioStation Since 1.16.0 
    Deletes an existing internet radio station. Only users with admin privileges are allowed to call this method. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deleteInternetRadioStation(
            String id //  The ID for the station.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/getChatMessages Since 1.2.0 
    Returns the current visible (non-expired) chat messages. 
    Returns a <subsonic-response> element with a nested <chatMessages> element on success. 
    */
    public ChatMessages getChatMessages(
            String since // [opt] Only return messages newer than this time (in millis since Jan 1 1970).
    ) {
        ChatMessages chatMessages = new ChatMessages();
        ChatMessage chatMessage = new ChatMessage();
        return chatMessages;
    }

    /*
    http://your-server/rest/addChatMessage Since 1.2.0 
    Adds a message to the chat log. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void addChatMessage(
            String message //  The chat message.
    ) {
    }

    /*
    http://your-server/rest/getUser Since 1.3.0 
    Get details about a given user, including which authorization roles and folder access it has. Can be used to enable/disable certain features in the client, such as jukebox control. 
    Returns a <subsonic-response> element with a nested <user> element on success. 
    */
    public User getUser(
            String username //  The name of the user to retrieve. You can only retrieve your own user unless you have admin privileges. 
    ) {
        User user = new User();
    }

    /*
    http://your-server/rest/getUsers Since 1.8.0 
    Get details about all users, including which authorization roles and folder access they have. Only users with admin privileges are allowed to call this method. 
    Returns a <subsonic-response> element with a nested <users> element on success. 
    */
    public Users getUsers() {
        Users users = new Users();
        User user = new User();
        //return users;
    }

    /*
    http://your-server/rest/createUser Since 1.1.0 
    Creates a new Subsonic user, using the following parameters: 
    Returns an empty <subsonic-response> element on success. 
    */
    public void createUser(
            String username, //  The name of the new user.
            String password, //  The password of the new user, either in clear text of hex-encoded (see above).
            String email, //  The email address of the new user.
            Boolean ldapAuthenticated, // [default:false] Whether the user is authenicated in LDAP.
            Boolean adminRole, // [default:false] Whether the user is administrator.
            Boolean settingsRole, // [default:true] Whether the user is allowed to change personal settings and password.
            Boolean streamRole, // [default:true] Whether the user is allowed to play files.
            Boolean jukeboxRole, // [default:false] Whether the user is allowed to play files in jukebox mode.
            Boolean downloadRole, // [default:false] Whether the user is allowed to download files.
            Boolean uploadRole, // [default:false] Whether the user is allowed to upload files.
            Boolean playlistRole, // [default:false] Whether the user is allowed to create and delete playlists. Since 1.8.0, changing this role has no effect. 
            Boolean coverArtRole, // [default:false] Whether the user is allowed to change cover art and tags.
            Boolean commentRole, // [default:false] Whether the user is allowed to create and edit comments and ratings.
            Boolean podcastRole, // [default:false] Whether the user is allowed to administrate Podcasts.
            Boolean shareRole, // [default:false] (Since 1.8.0) Whether the user is allowed to share files with anyone.
            Boolean videoConversionRole, // [default:false] (Since 1.15.0) Whether the user is allowed to start video conversions.
            List<Integer> musicFolderId // [opt] All folders (Since 1.12.0) IDs of the music folders the user is allowed access to. Include the parameter once for each folder.
    ) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Objects.requireNonNull(email);
    }

    /*
    http://your-server/rest/updateUser Since 1.10.1 
    Modifies an existing Subsonic user, using the following parameters: 
    Returns an empty <subsonic-response> element on success. 
    */
    // where did playlistRole go??
    public void updateUser(
            String username, //  The name of the user.
            String password, // [opt] The password of the user, either in clear text of hex-encoded (see above).
            String email, // [opt] The email address of the user.
            Boolean ldapAuthenticated, // [opt] Whether the user is authenicated in LDAP.
            Boolean adminRole, // [opt] Whether the user is administrator.
            Boolean settingsRole, // [opt] Whether the user is allowed to change personal settings and password.
            Boolean streamRole, // [opt] Whether the user is allowed to play files.
            Boolean jukeboxRole, // [opt] Whether the user is allowed to play files in jukebox mode.
            Boolean downloadRole, // [opt] Whether the user is allowed to download files.
            Boolean uploadRole, // [opt] Whether the user is allowed to upload files.
            Boolean coverArtRole, // [opt] Whether the user is allowed to change cover art and tags.
            Boolean commentRole, // [opt] Whether the user is allowed to create and edit comments and ratings.
            Boolean podcastRole, // [opt] Whether the user is allowed to administrate Podcasts.
            Boolean shareRole, // [opt] Whether the user is allowed to share files with anyone.
            Boolean videoConversionRole, // false (Since 1.15.0) Whether the user is allowed to start video conversions.
            List<Integer> musicFolderId, // [opt] (Since 1.12.0) IDs of the music folders the user is allowed access to. Include the parameter once for each folder.
            Integer maxBitRate // [opt] (Since 1.13.0) The maximum bit rate (in Kbps) for the user. Audio streams of higher bit rates are automatically downsampled to this bit rate. Legal values: 0 (no limit), 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320. 
    ) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
    }

    /*
    http://your-server/rest/deleteUser Since 1.3.0 
    Deletes an existing Subsonic user, using the following parameters: 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deleteUser(
            String username //  The name of the user to delete.
    ) {
    }

    /*
    http://your-server/rest/changePassword Since 1.1.0 
    Changes the password of an existing Subsonic user, using the following parameters. You can only change your own password unless you have admin privileges. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void changePassword(
            String username, //  The name of the user which should change its password.
            String password //  The new password of the new user, either in clear text of hex-encoded (see above).
    ) {
    }

    /*
    http://your-server/rest/getBookmarks Since 1.9.0 
    Returns all bookmarks for this user. A bookmark is a position within a certain media file. 
    Returns a <subsonic-response> element with a nested <bookmarks> element on success. 
    */
    public Bookmarks getBookmarks() {
        Bookmarks bookmarks = new Bookmarks();
        Bookmark bookmark = new Bookmark();
        return bookmarks;
    }

    /*
    http://your-server/rest/createBookmark Since 1.9.0 
    Creates or updates a bookmark (a position within a media file). Bookmarks are personal and not visible to other users. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void createBookmark(
            String id, //  ID of the media file to bookmark. If a bookmark already exists for this file it will be overwritten. 
            Long position, //  The position (in milliseconds) within the media file.
            String comment // [opt] A user-defined comment.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/deleteBookmark Since 1.9.0 
    Deletes the bookmark for a given file. 
    Returns an empty <subsonic-response> element on success. 
    */
    public void deleteBookmark(
            String id //  ID of the media file for which to delete the bookmark. Other users' bookmarks are not affected.
    ) {
        Objects.requireNonNull(id, "id required");
    }

    /*
    http://your-server/rest/getPlayQueue Since 1.12.0 
    Returns the state of the play queue for this user (as set by savePlayQueue). This includes the tracks in the play queue, the currently playing track, and the position within this track. Typically used to allow a user to move between different clients/apps while retaining the same play queue (for instance when listening to an audio book). 
    Returns a <subsonic-response> element with a nested <playQueue> element on success, or an empty <subsonic-response> if no play queue has been saved. 
    */
    public PlayQueue getPlayQueue() {
        PlayQueue playQueue = new PlayQueue();
        //return playQueue;
    }

    /*
    http://your-server/rest/savePlayQueue Since 1.12.0 
    Saves the state of the play queue for this user. This includes the tracks in the play queue, the currently playing track, and the position within this track. Typically used to allow a user to move between different clients/apps while retaining the same play queue (for instance when listening to an audio book). 
    Returns an empty <subsonic-response> element on success. 
    */
    public void savePlayQueue(
            List<String> id, //  ID of a song in the play queue. Use one id parameter for each song in the play queue.
            String current, // [opt] The ID of the current playing song.
            Long position // [opt] The position in milliseconds within the currently playing song.
    ) {
        Objects.requireNonNull(id, "id required");
    }
    
    public void savePlayQueue_implementation(
            List<String> id,
            String current,
            Long position,
            String c // changed by, required
    ) {
        
    }

    /*
    http://your-server/rest/getScanStatus Since 1.15.0 
    Returns the current status for media library scanning. Takes no extra parameters. 
    Returns a <subsonic-response> element with a nested <scanStatus> element on success. 
    */
    public ScanStatus getScanStatus() {
        ScanStatus scanStatus = new ScanStatus();
        scanStatus.setScanning(false);
        return scanStatus;
    }

    /*
    http://your-server/rest/startScan Since 1.15.0 
    Initiates a rescan of the media libraries. Takes no extra parameters. 
    Returns a <subsonic-response> element with a nested <scanStatus> element on success. 
    */
    public ScanStatus startScan() {
        ScanStatus scanStatus = new ScanStatus();
        scanStatus.setScanning(false);
        return scanStatus;
    }


}
