/*
 This file is part of Airsonic.

 Airsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Airsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Airsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2016 (C) Airsonic Authors
 Based upon Subsonic, Copyright 2009 (C) Sindre Mehus
 */
package org.airsonic.player.controller;

import java.io.IOException;
import org.airsonic.player.ajax.LyricsInfo;
import org.airsonic.player.ajax.LyricsService;
import org.airsonic.player.ajax.PlayQueueService;
import org.airsonic.player.command.UserSettingsCommand;
import org.airsonic.player.dao.AlbumDao;
import org.airsonic.player.dao.ArtistDao;
import org.airsonic.player.dao.MediaFileDao;
import org.airsonic.player.dao.PlayQueueDao;
import org.airsonic.player.domain.*;
import org.airsonic.player.domain.Bookmark;
import org.airsonic.player.domain.PlayQueue;
import org.airsonic.player.service.*;
import org.airsonic.player.util.Pair;
import org.airsonic.player.util.StringUtil;
import org.airsonic.player.util.Util;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.subsonic.restapi.*;
import org.subsonic.restapi.PodcastStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import m35.subsonicapi.Api;

import static org.airsonic.player.security.RESTRequestParameterProcessingFilter.decrypt;
import org.apache.commons.io.IOUtils;
import static org.springframework.web.bind.ServletRequestUtils.*;

/**
 * Multi-controller used for the REST API.
 * <p/>
 * For documentation, please refer to api.jsp.
 * <p/>
 * Note: Exceptions thrown from the methods are intercepted by RESTFilter.
 *
 * @author Sindre Mehus
 */
@Controller
@RequestMapping(value = "/rest", method = {RequestMethod.GET, RequestMethod.POST})
public class SubsonicRESTController {

    private static final Logger LOG = LoggerFactory.getLogger(SubsonicRESTController.class);

    @Autowired
    private SettingsService settingsService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private MediaFileService mediaFileService;
    @Autowired
    private MusicIndexService musicIndexService;
    @Autowired
    private TranscodingService transcodingService;
    @Autowired
    private DownloadController downloadController;
    @Autowired
    private CoverArtController coverArtController;
    @Autowired
    private AvatarController avatarController;
    @Autowired
    private UserSettingsController userSettingsController;
    @Autowired
    private LeftController leftController;
    @Autowired
    private StatusService statusService;
    @Autowired
    private StreamController streamController;
    @Autowired
    private HLSController hlsController;
    @Autowired
    private ShareService shareService;
    @Autowired
    private PlaylistService playlistService;
    @Autowired
    private LyricsService lyricsService;
    @Autowired
    private PlayQueueService playQueueService;
    @Autowired
    private JukeboxService jukeboxService;
    @Autowired
    private AudioScrobblerService audioScrobblerService;
    @Autowired
    private PodcastService podcastService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private MediaFileDao mediaFileDao;
    @Autowired
    private ArtistDao artistDao;
    @Autowired
    private AlbumDao albumDao;
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private PlayQueueDao playQueueDao;
    @Autowired
    private MediaScannerService mediaScannerService;
    private m35.subsonicapi.Api api;

    private final Map<BookmarkKey, org.airsonic.player.domain.Bookmark> bookmarkCache = new ConcurrentHashMap<BookmarkKey, org.airsonic.player.domain.Bookmark>();
    private final JAXBWriter jaxbWriter = new JAXBWriter();

    private static final String NOT_YET_IMPLEMENTED = "Not yet implemented";
    private static final String NO_LONGER_SUPPORTED = "No longer supported";

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingRequestParam(HttpServletRequest request,
                                          HttpServletResponse response,
                                          MissingServletRequestParameterException exception) throws Exception {
        error(request, response, ErrorCode.MISSING_PARAMETER, "Required param ("+exception.getParameterName()+") is missing");
    }

    @RequestMapping(value = "/ping")
    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        jaxbWriter.writeResponse(request, response, res);
    }


    /**
     * CAUTION : this method is required by mobile applications and must not be removed.
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/getLicense")
    public void getLicense(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = jaxbWriter.createResponse(true);
        res.setLicense(api.getLicense());
        jaxbWriter.writeResponse(request, response, res);
    }


    @RequestMapping(value = "/getMusicFolders")
    public void getMusicFolders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = jaxbWriter.createResponse(true);
        res.setMusicFolders(api.getMusicFolders());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getIndexes")
    public void getIndexes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        long ifModifiedSince = getLongParameter(request, "ifModifiedSince", 0L);
        Response res = jaxbWriter.createResponse(true);
        res.setIndexes(api.getIndexes(musicFolderId, ifModifiedSince));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getGenres")
    public void getGenres(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = jaxbWriter.createResponse(true);
        res.setGenres(api.getGenres());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getSongsByGenre")
    public void getSongsByGenre(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        String genre = getStringParameter(request, "genre");
        Integer offset = getIntParameter(request, "offset");
        Integer count = getIntParameter(request, "count");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        res.setSongsByGenre(api.getSongsByGenre(genre, count, offset, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getArtists")
    public void getArtists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = jaxbWriter.createResponse(true);
        res.setArtists(api.getArtists(musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getSimilarSongs")
    public void getSimilarSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer count = getIntParameter(request, "count");
        Response res = jaxbWriter.createResponse(true);
        res.setSimilarSongs(api.getSimilarSongs(id, count));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getSimilarSongs2")
    public void getSimilarSongs2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer count = getIntParameter(request, "count");
        Response res = jaxbWriter.createResponse(true);
        res.setSimilarSongs2(api.getSimilarSongs2(id, count));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getTopSongs")
    public void getTopSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String artist = getStringParameter(request, "artist");
        Integer count = getIntParameter(request, "count");
        Response res = jaxbWriter.createResponse(true);
        res.setTopSongs(api.getTopSongs(artist, count));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getArtistInfo")
    public void getArtistInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer count = getIntParameter(request, "count");
        Boolean includeNotPresent = getBooleanParameter(request, "includeNotPresent");
        Response res = jaxbWriter.createResponse(true);
        res.setArtistInfo(api.getArtistInfo(id, count, includeNotPresent));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getArtistInfo2")
    public void getArtistInfo2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer count = getIntParameter(request, "count");
        Boolean includeNotPresent = getBooleanParameter(request, "includeNotPresent");
        Response res = jaxbWriter.createResponse(true);
        res.setArtistInfo2(api.getArtistInfo2(id, count, includeNotPresent));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getArtist")
    public void getArtist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = jaxbWriter.createResponse(true);
        res.setArtist(api.getArtist(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getAlbum")
    public void getAlbum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = jaxbWriter.createResponse(true);
        res.setAlbum(api.getAlbum(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getSong")
    public void getSong(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = jaxbWriter.createResponse(true);
        res.setSong(api.getSong(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getMusicDirectory")
    public void getMusicDirectory(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = jaxbWriter.createResponse(true);
        res.setDirectory(api.getMusicDirectory(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/search")
    public void search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String any = getStringParameter(request, "any");
        String artist = getStringParameter(request, "artist");
        String album = getStringParameter(request, "album");
        String title = getStringParameter(request, "title");
        Integer count = getIntParameter(request, "count");
        Integer offset = getIntParameter(request, "offset");
        Long newerThan = getLongParameter(request, "newerThan");
        Response res = jaxbWriter.createResponse(true);
        res.setSearchResult(api.search(artist, album, title, any, count, offset, newerThan));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/search2")
    public void search2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String query = getStringParameter(request, "query");
        Integer artistCount = getIntParameter(request, "artistCount");
        Integer artistOffset = getIntParameter(request, "artistOffset");
        Integer albumCount = getIntParameter(request, "albumCount");
        Integer albumOffset = getIntParameter(request, "albumOffset");
        Integer songCount = getIntParameter(request, "songCount");
        Integer songOffset = getIntParameter(request, "songOffset");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setSearchResult2(api.search2(query, artistCount, artistOffset, albumCount, albumOffset, songCount, songOffset, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/search3")
    public void search3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String query = getStringParameter(request, "query");
        Integer artistCount = getIntParameter(request, "artistCount");
        Integer artistOffset = getIntParameter(request, "artistOffset");
        Integer albumCount = getIntParameter(request, "albumCount");
        Integer albumOffset = getIntParameter(request, "albumOffset");
        Integer songCount = getIntParameter(request, "songCount");
        Integer songOffset = getIntParameter(request, "songOffset");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setSearchResult3(api.search3(query, artistCount, artistOffset, albumCount, albumOffset, songCount, songOffset, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getPlaylists")
    public void getPlaylists(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("username");
        Response res = createResponse();
        res.setPlaylists(api.getPlaylists(username));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getPlaylist")
    public void getPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = createResponse();
        res.setPlaylist(api.getPlaylist(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/jukeboxControl")
    public void jukeboxControl(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String action = getStringParameter(request, "action");
        Integer index = getIntParameter(request, "index");
        Integer offset = getIntParameter(request, "offset");
        List<String> ids = Arrays.asList(getStringParameters(request, "id"));
        Float gain = getFloatParameter(request, "gain");
        JukeboxStatus jukeboxStatusOrPlaylist = api.jukeboxControl(action, index, offset, ids, gain);
        Response res = createResponse();
        if (jukeboxStatusOrPlaylist.getClass().equals(JukeboxPlaylist.class)) {
            res.setJukeboxPlaylist((JukeboxPlaylist)jukeboxStatusOrPlaylist);
        } else {
            res.setJukeboxStatus(jukeboxStatusOrPlaylist);
        }
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/createPlaylist")
    public void createPlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String playlistId = getStringParameter(request, "playlistId");
        String name = getStringParameter(request, "name");
        List<String> songId = Arrays.asList(getStringParameters(request, "songId"));
        Response res = createResponse();
        res.setPlaylist(api.createPlaylist(playlistId, name, songId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/updatePlaylist")
    public void updatePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String playlistId = getStringParameter(request, "playlistId");
        String name = getStringParameter(request, "name");
        String comment = getStringParameter(request, "comment");
        Boolean public_ = getBooleanParameter(request, "public");
        List<Integer> songIdToAdd = Util.toIntegerList(getIntParameters(request, "songIdToAdd"));
        List<Integer> songIndexToRemove = Util.toIntegerList(getIntParameters(request, "songIndexToRemove"));
        api.updatePlaylist(playlistId, name, comment, public_, songIdToAdd, songIndexToRemove);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/deletePlaylist")
    public void deletePlaylist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.deletePlaylist(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getAlbumList")
    public void getAlbumList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = getStringParameter(request, "type");
        Integer size = getIntParameter(request, "size");
        Integer offset = getIntParameter(request, "offset");
        Integer fromYear = getIntParameter(request, "fromYear");
        Integer toYear = getIntParameter(request, "toYear");
        String genre = getStringParameter(request, "genre");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setAlbumList(api.getAlbumList(type, size, offset, fromYear, toYear, genre, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getAlbumList2")
    public void getAlbumList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String type = getStringParameter(request, "type");
        Integer size = getIntParameter(request, "size");
        Integer offset = getIntParameter(request, "offset");
        Integer fromYear = getIntParameter(request, "fromYear");
        Integer toYear = getIntParameter(request, "toYear");
        String genre = getStringParameter(request, "genre");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setAlbumList2(api.getAlbumList2(type, size, offset, fromYear, toYear, genre, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getRandomSongs")
    public void getRandomSongs(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer size = getIntParameter(request, "size");
        String genre = getStringParameter(request, "genre");
        Integer fromYear = getIntParameter(request, "fromYear");
        Integer toYear = getIntParameter(request, "toYear");
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setRandomSongs(api.getRandomSongs(size, genre, fromYear, toYear, musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getVideos")
    public void getVideos(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setVideos(api.getVideos());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getNowPlaying")
    public void getNowPlaying(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setNowPlaying(api.getNowPlaying());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        String playlist = getStringParameter(request, "playlist");
        String player = getStringParameter(request, "player");
        List<String> i = Arrays.asList(getStringParameters(request, "i"));
        String range_header = request.getHeader("Range");

        Api.BinaryResponse binaryResponse = api.download_implementation(id, playlist, player, i, range_header);
        writeBinaryResponse(response, binaryResponse);
    }
    
    private void writeBinaryResponse(HttpServletResponse response, Api.BinaryResponse binaryResponse) throws IOException {
        response.setContentType(binaryResponse.getMimeType());
        if (binaryResponse.getLength() != null) {
            response.setContentLength(binaryResponse.getLength());
        }
        try {
            IOUtils.copy(binaryResponse.getStream(), response.getOutputStream(), 4096);
        } finally {
            binaryResponse.getStream().close();
        }
    }

    @RequestMapping(value = "/stream")
    public void stream(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer maxBitRate = getIntParameter(request, "maxBitRate");
        String format = getStringParameter(request, "format");
        Boolean estimateContentLength = getBooleanParameter(request, "estimateContentLength");

        Integer offsetSeconds = getIntParameter(request, "offsetSeconds");
        String suffix = getStringParameter(request, "suffix");
        String playlist = getStringParameter(request, "playlist");
        Boolean hls = getBooleanParameter(request, "hls");
        Integer duration = getIntParameter(request, "duration");
        Integer path = getIntParameter(request, "path");
        String icy_metadata_header = request.getHeader("icy-metadata");
        String range_header = request.getHeader("Range");
        Api.BinaryResponse binaryResponse = api.stream_implementation(id, maxBitRate, format, estimateContentLength, offsetSeconds, suffix, playlist, hls, duration, path, icy_metadata_header, range_header);
        writeBinaryResponse(response, binaryResponse);
    }

    @RequestMapping(value = "/hls")
    public void hls(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        List<String> bitrate = Arrays.asList(getStringParameters(request, "bitrate"));
        String audioTrack = getStringParameter(request, "audioTrack");
        String player = getStringParameter(request, "player");
        Api.BinaryResponse binaryResponse = api.hls_implementation(id, bitrate, audioTrack, player);
        writeBinaryResponse(response, binaryResponse);
    }

    @RequestMapping(value = "/scrobble")
    public void scrobble(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Boolean submission = getBooleanParameter(request, "");
        List<Long> time = Arrays.asList(getLongParameters(request, "time"));
        List<String> id = Arrays.asList(getStringParameters(request, "id"));
        api.scrobble_implementation(id, time, submission);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/star")
    public void star(HttpServletRequest request, HttpServletResponse response) throws Exception {
        starOrUnstar(request, response, true);
    }

    @RequestMapping(value = "/unstar")
    public void unstar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        starOrUnstar(request, response, false);
    }

    private void starOrUnstar(HttpServletRequest request, HttpServletResponse response, boolean star) throws Exception {
        List<String> id = Arrays.asList(getStringParameters(request, "id"));
        List<String> albumId = Arrays.asList(getStringParameters(request, "albumId"));
        List<String> artistId = Arrays.asList(getStringParameters(request, "artistId"));
        
        if (star) {
            api.star(id, albumId, artistId);
        } else {
            api.unstar(id, albumId, artistId);
        }

        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getStarred")
    public void getStarred(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setStarred(api.getStarred(musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getStarred2")
    public void getStarred2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer musicFolderId = getIntParameter(request, "musicFolderId");
        Response res = createResponse();
        res.setStarred2(api.getStarred2(musicFolderId));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getPodcasts")
    public void getPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Boolean includeEpisodes = getBooleanParameter(request, "includeEpisodes");
        String id = getStringParameter(request, "id");
        Response res = createResponse();
        res.setPodcasts(api.getPodcasts(includeEpisodes, id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getNewestPodcasts")
    public void getNewestPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Integer count = getIntParameter(request, "count");
        Response res = createResponse();
        res.setNewestPodcasts(api.getNewestPodcasts(count));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/refreshPodcasts")
    public void refreshPodcasts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        api.refreshPodcasts();
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/createPodcastChannel")
    public void createPodcastChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url = getStringParameter(request, "url");
        api.createPodcastChannel(url);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/deletePodcastChannel")
    public void deletePodcastChannel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.deletePodcastChannel(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/deletePodcastEpisode")
    public void deletePodcastEpisode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.deletePodcastEpisode(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/downloadPodcastEpisode")
    public void downloadPodcastEpisode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.downloadPodcastEpisode(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getInternetRadioStations")
    public void getInternetRadioStations(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setInternetRadioStations(api.getInternetRadioStations());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getBookmarks")
    public void getBookmarks(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setBookmarks(api.getBookmarks());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/createBookmark")
    public void createBookmark(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Long position = getRequiredLongParameter(request, "position");
        String comment = getStringParameter(request, "comment");
        api.createBookmark(id, position, comment);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/deleteBookmark")
    public void deleteBookmark(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.deleteBookmark(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getPlayQueue")
    public void getPlayQueue(HttpServletRequest request, HttpServletResponse response) throws Exception {
        org.subsonic.restapi.PlayQueue playQueue = api.getPlayQueue();
        if (playQueue == null) {
            writeEmptyResponse(request, response);
        } else {
            Response res = createResponse();
            res.setPlayQueue(playQueue);
            jaxbWriter.writeResponse(request, response, res);
        }
    }

    @RequestMapping(value = "/savePlayQueue")
    public void savePlayQueue(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Integer> mediaFileIds = Util.toIntegerList(getIntParameters(request, "id"));
        Integer current = getIntParameter(request, "current");
        Long position = getLongParameter(request, "position");
        Date changed = new Date();
        String changedBy = getRequiredStringParameter(request, "c");

        if (!mediaFileIds.contains(current)) {
            error(request, response, ErrorCode.GENERIC, "Current track is not included in play queue");
            return;
        }

        SavedPlayQueue playQueue = new SavedPlayQueue(null, username, mediaFileIds, current, position, changed, changedBy);
        playQueueDao.savePlayQueue(playQueue);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getShares")
    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        String username = securityService.getCurrentUsername(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        List<org.airsonic.player.domain.MusicFolder> musicFolders = settingsService.getMusicFoldersForUser(username);

        Shares result = new Shares();
        for (org.airsonic.player.domain.Share share : shareService.getSharesForUser(user)) {
            org.subsonic.restapi.Share s = createJaxbShare(request, share);
            result.getShare().add(s);

            for (MediaFile mediaFile : shareService.getSharedFiles(share.getId(), musicFolders)) {
                s.getEntry().add(createJaxbChild(player, mediaFile, username));
            }
        }
        Response res = createResponse();
        res.setShares(result);
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/createShare")
    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Player player = playerService.getPlayer(request, response);
        String username = securityService.getCurrentUsername(request);

        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        if (!user.isShareRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to share media.");
            return;
        }

        List<MediaFile> files = new ArrayList<MediaFile>();
        for (int id : getRequiredIntParameters(request, "id")) {
            files.add(mediaFileService.getMediaFile(id));
        }

        org.airsonic.player.domain.Share share = shareService.createShare(request, files);
        share.setDescription(request.getParameter("description"));
        long expires = getLongParameter(request, "expires", 0L);
        if (expires != 0) {
            share.setExpires(new Date(expires));
        }
        shareService.updateShare(share);

        Shares result = new Shares();
        org.subsonic.restapi.Share s = createJaxbShare(request, share);
        result.getShare().add(s);

        List<org.airsonic.player.domain.MusicFolder> musicFolders = settingsService.getMusicFoldersForUser(username);

        for (MediaFile mediaFile : shareService.getSharedFiles(share.getId(), musicFolders)) {
            s.getEntry().add(createJaxbChild(player, mediaFile, username));
        }

        Response res = createResponse();
        res.setShares(result);
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/deleteShare")
    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        int id = getRequiredIntParameter(request, "id");

        org.airsonic.player.domain.Share share = shareService.getShareById(id);
        if (share == null) {
            error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
            return;
        }
        if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to delete shared media.");
            return;
        }

        shareService.deleteShare(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/updateShare")
    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        int id = getRequiredIntParameter(request, "id");

        org.airsonic.player.domain.Share share = shareService.getShareById(id);
        if (share == null) {
            error(request, response, ErrorCode.NOT_FOUND, "Shared media not found.");
            return;
        }
        if (!user.isAdminRole() && !share.getUsername().equals(user.getUsername())) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, "Not authorized to modify shared media.");
            return;
        }

        share.setDescription(request.getParameter("description"));
        String expiresString = request.getParameter("expires");
        if (expiresString != null) {
            long expires = Long.parseLong(expiresString);
            share.setExpires(expires == 0L ? null : new Date(expires));
        }
        shareService.updateShare(share);
        writeEmptyResponse(request, response);
    }

    private org.subsonic.restapi.Share createJaxbShare(HttpServletRequest request, org.airsonic.player.domain.Share share) {
        org.subsonic.restapi.Share result = new org.subsonic.restapi.Share();
        result.setId(String.valueOf(share.getId()));
        result.setUrl(shareService.getShareUrl(request, share));
        result.setUsername(share.getUsername());
        result.setCreated(jaxbWriter.convertDate(share.getCreated()));
        result.setVisitCount(share.getVisitCount());
        result.setDescription(share.getDescription());
        result.setExpires(jaxbWriter.convertDate(share.getExpires()));
        result.setLastVisited(jaxbWriter.convertDate(share.getLastVisited()));
        return result;
    }

    @SuppressWarnings("UnusedParameters")
    public ModelAndView videoPlayer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        Map<String, Object> map = new HashMap<String, Object>();
        int id = getRequiredIntParameter(request, "id");
        MediaFile file = mediaFileService.getMediaFile(id);

        int timeOffset = getIntParameter(request, "timeOffset", 0);
        timeOffset = Math.max(0, timeOffset);
        Integer duration = file.getDurationSeconds();
        if (duration != null) {
            map.put("skipOffsets", VideoPlayerController.createSkipOffsets(duration));
            timeOffset = Math.min(duration, timeOffset);
            duration -= timeOffset;
        }

        map.put("id", request.getParameter("id"));
        map.put("u", request.getParameter("u"));
        map.put("p", request.getParameter("p"));
        map.put("c", request.getParameter("c"));
        map.put("v", request.getParameter("v"));
        map.put("video", file);
        map.put("maxBitRate", getIntParameter(request, "maxBitRate", VideoPlayerController.DEFAULT_BIT_RATE));
        map.put("duration", duration);
        map.put("timeOffset", timeOffset);
        map.put("bitRates", VideoPlayerController.BIT_RATES);
        map.put("autoplay", getBooleanParameter(request, "autoplay", true));

        ModelAndView result = new ModelAndView("rest/videoPlayer");
        result.addObject("model", map);
        return result;
    }

    @RequestMapping(value = "/getCoverArt")
    public void getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        coverArtController.handleRequest(request, response);
    }

    @RequestMapping(value = "/getAvatar")
    public void getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        avatarController.handleRequest(request, response);
    }

    @RequestMapping(value = "/changePassword")
    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        String username = getRequiredStringParameter(request, "username");
        String password = decrypt(getRequiredStringParameter(request, "password"));

        org.airsonic.player.domain.User authUser = securityService.getCurrentUser(request);

        boolean allowed = authUser.isAdminRole()
                || username.equals(authUser.getUsername()) && authUser.isSettingsRole();

        if (!allowed) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, authUser.getUsername() + " is not authorized to change password for " + username);
            return;
        }

        org.airsonic.player.domain.User user = securityService.getUserByName(username);
        user.setPassword(password);
        securityService.updateUser(user);

        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getUser")
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        String username = getRequiredStringParameter(request, "username");

        org.airsonic.player.domain.User currentUser = securityService.getCurrentUser(request);
        if (!username.equals(currentUser.getUsername()) && !currentUser.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
            return;
        }

        org.airsonic.player.domain.User requestedUser = securityService.getUserByName(username);
        if (requestedUser == null) {
            error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
            return;
        }

        Response res = createResponse();
        res.setUser(createJaxbUser(requestedUser));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getUsers")
    public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        org.airsonic.player.domain.User currentUser = securityService.getCurrentUser(request);
        if (!currentUser.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, currentUser.getUsername() + " is not authorized to get details for other users.");
            return;
        }

        Users result = new Users();
        for (org.airsonic.player.domain.User user : securityService.getAllUsers()) {
            result.getUser().add(createJaxbUser(user));
        }

        Response res = createResponse();
        res.setUsers(result);
        jaxbWriter.writeResponse(request, response, res);
    }

    private org.subsonic.restapi.User createJaxbUser(org.airsonic.player.domain.User user) {
        UserSettings userSettings = settingsService.getUserSettings(user.getUsername());

        org.subsonic.restapi.User result = new org.subsonic.restapi.User();
        result.setUsername(user.getUsername());
        result.setEmail(user.getEmail());
        result.setScrobblingEnabled(userSettings.isLastFmEnabled());
        result.setAdminRole(user.isAdminRole());
        result.setSettingsRole(user.isSettingsRole());
        result.setDownloadRole(user.isDownloadRole());
        result.setUploadRole(user.isUploadRole());
        result.setPlaylistRole(true);  // Since 1.8.0
        result.setCoverArtRole(user.isCoverArtRole());
        result.setCommentRole(user.isCommentRole());
        result.setPodcastRole(user.isPodcastRole());
        result.setStreamRole(user.isStreamRole());
        result.setJukeboxRole(user.isJukeboxRole());
        result.setShareRole(user.isShareRole());
        // currently this role isn't supported by airsonic
        result.setVideoConversionRole(false);
        // Useless
        result.setAvatarLastChanged(null);

        TranscodeScheme transcodeScheme = userSettings.getTranscodeScheme();
        if (transcodeScheme != null && transcodeScheme != TranscodeScheme.OFF) {
            result.setMaxBitRate(transcodeScheme.getMaxBitRate());
        }

        List<org.airsonic.player.domain.MusicFolder> musicFolders = settingsService.getMusicFoldersForUser(user.getUsername());
        for (org.airsonic.player.domain.MusicFolder musicFolder : musicFolders) {
            result.getFolder().add(musicFolder.getId());
        }
        return result;
    }

    @RequestMapping(value = "/createUser")
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to create new users.");
            return;
        }

        UserSettingsCommand command = new UserSettingsCommand();
        command.setUsername(getRequiredStringParameter(request, "username"));
        command.setPassword(decrypt(getRequiredStringParameter(request, "password")));
        command.setEmail(getRequiredStringParameter(request, "email"));
        command.setLdapAuthenticated(getBooleanParameter(request, "ldapAuthenticated", false));
        command.setAdminRole(getBooleanParameter(request, "adminRole", false));
        command.setCommentRole(getBooleanParameter(request, "commentRole", false));
        command.setCoverArtRole(getBooleanParameter(request, "coverArtRole", false));
        command.setDownloadRole(getBooleanParameter(request, "downloadRole", false));
        command.setStreamRole(getBooleanParameter(request, "streamRole", true));
        command.setUploadRole(getBooleanParameter(request, "uploadRole", false));
        command.setJukeboxRole(getBooleanParameter(request, "jukeboxRole", false));
        command.setPodcastRole(getBooleanParameter(request, "podcastRole", false));
        command.setSettingsRole(getBooleanParameter(request, "settingsRole", true));
        command.setShareRole(getBooleanParameter(request, "shareRole", false));
        command.setTranscodeSchemeName(TranscodeScheme.OFF.name());

        int[] folderIds = ServletRequestUtils.getIntParameters(request, "musicFolderId");
        if (folderIds.length == 0) {
            folderIds = Util.toIntArray(org.airsonic.player.domain.MusicFolder.toIdList(settingsService.getAllMusicFolders()));
        }
        command.setAllowedMusicFolderIds(folderIds);

        userSettingsController.createUser(command);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/updateUser")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to update users.");
            return;
        }

        String username = getRequiredStringParameter(request, "username");
        org.airsonic.player.domain.User u = securityService.getUserByName(username);
        UserSettings s = settingsService.getUserSettings(username);

        if (u == null) {
            error(request, response, ErrorCode.NOT_FOUND, "No such user: " + username);
            return;
        } else if (org.airsonic.player.domain.User.USERNAME_ADMIN.equals(username)) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, "Not allowed to change admin user");
            return;
        }

        UserSettingsCommand command = new UserSettingsCommand();
        command.setUsername(username);
        command.setEmail(getStringParameter(request, "email", u.getEmail()));
        command.setLdapAuthenticated(getBooleanParameter(request, "ldapAuthenticated", u.isLdapAuthenticated()));
        command.setAdminRole(getBooleanParameter(request, "adminRole", u.isAdminRole()));
        command.setCommentRole(getBooleanParameter(request, "commentRole", u.isCommentRole()));
        command.setCoverArtRole(getBooleanParameter(request, "coverArtRole", u.isCoverArtRole()));
        command.setDownloadRole(getBooleanParameter(request, "downloadRole", u.isDownloadRole()));
        command.setStreamRole(getBooleanParameter(request, "streamRole", u.isDownloadRole()));
        command.setUploadRole(getBooleanParameter(request, "uploadRole", u.isUploadRole()));
        command.setJukeboxRole(getBooleanParameter(request, "jukeboxRole", u.isJukeboxRole()));
        command.setPodcastRole(getBooleanParameter(request, "podcastRole", u.isPodcastRole()));
        command.setSettingsRole(getBooleanParameter(request, "settingsRole", u.isSettingsRole()));
        command.setShareRole(getBooleanParameter(request, "shareRole", u.isShareRole()));

        int maxBitRate = getIntParameter(request, "maxBitRate", s.getTranscodeScheme().getMaxBitRate());
        command.setTranscodeSchemeName(TranscodeScheme.fromMaxBitRate(maxBitRate).name());

        if (hasParameter(request, "password")) {
            command.setPassword(decrypt(getRequiredStringParameter(request, "password")));
            command.setPasswordChange(true);
        }

        int[] folderIds = ServletRequestUtils.getIntParameters(request, "musicFolderId");
        if (folderIds.length == 0) {
            folderIds = Util.toIntArray(org.airsonic.player.domain.MusicFolder.toIdList(settingsService.getMusicFoldersForUser(username)));
        }
        command.setAllowedMusicFolderIds(folderIds);

        userSettingsController.updateUser(command);
        writeEmptyResponse(request, response);
    }

    private boolean hasParameter(HttpServletRequest request, String name) {
        return request.getParameter(name) != null;
    }

    @RequestMapping(value = "/deleteUser")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        org.airsonic.player.domain.User user = securityService.getCurrentUser(request);
        if (!user.isAdminRole()) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, user.getUsername() + " is not authorized to delete users.");
            return;
        }

        String username = getRequiredStringParameter(request, "username");
        if (org.airsonic.player.domain.User.USERNAME_ADMIN.equals(username)) {
            error(request, response, ErrorCode.NOT_AUTHORIZED, "Not allowed to delete admin user");
            return;
        }

        securityService.deleteUser(username);

        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getChatMessages")
    public ResponseEntity<String> getChatMessages(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.SC_GONE).body(NO_LONGER_SUPPORTED);
    }

    @RequestMapping(value = "/addChatMessage")
    public ResponseEntity<String> addChatMessage(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.SC_GONE).body(NO_LONGER_SUPPORTED);
    }

    @RequestMapping(value = "/getLyrics")
    public void getLyrics(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        String artist = request.getParameter("artist");
        String title = request.getParameter("title");
        LyricsInfo lyrics = lyricsService.getLyrics(artist, title);

        Lyrics result = new Lyrics();
        result.setArtist(lyrics.getArtist());
        result.setTitle(lyrics.getTitle());
        result.setContent(lyrics.getLyrics());

        Response res = createResponse();
        res.setLyrics(result);
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/setRating")
    public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);
        Integer rating = getRequiredIntParameter(request, "rating");
        if (rating == 0) {
            rating = null;
        }

        int id = getRequiredIntParameter(request, "id");
        MediaFile mediaFile = mediaFileService.getMediaFile(id);
        if (mediaFile == null) {
            error(request, response, ErrorCode.NOT_FOUND, "File not found: " + id);
            return;
        }

        String username = securityService.getCurrentUsername(request);
        ratingService.setRatingForUser(username, mediaFile, rating);

        writeEmptyResponse(request, response);
    }

    @RequestMapping(path = "/getAlbumInfo")
    public void getAlbumInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

        MediaFile mediaFile = this.mediaFileService.getMediaFile(id);
        if (mediaFile == null) {
            error(request, response, SubsonicRESTController.ErrorCode.NOT_FOUND, "Media file not found.");
            return;
        }

        Response res = createResponse();
        this.jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(path = "/getAlbumInfo2")
    public void getAlbumInfo2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request = wrapRequest(request);

        int id = ServletRequestUtils.getRequiredIntParameter(request, "id");

        Album album = this.albumDao.getAlbum(id);
        if (album == null) {
            error(request, response, SubsonicRESTController.ErrorCode.NOT_FOUND, "Album not found.");
            return;
        }

        Response res = createResponse();
        this.jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getVideoInfo")
    public ResponseEntity<String> getVideoInfo() throws Exception {
        return ResponseEntity.status(HttpStatus.SC_NOT_IMPLEMENTED).body(NOT_YET_IMPLEMENTED);
    }

    @RequestMapping(value = "/getCaptions")
    public ResponseEntity<String> getCaptions() {
        return ResponseEntity.status(HttpStatus.SC_NOT_IMPLEMENTED).body(NOT_YET_IMPLEMENTED);
    }

    @RequestMapping(value = "/startScan")
    public void startScan(HttpServletRequest request, HttpServletResponse response) {
        request = wrapRequest(request);
        mediaScannerService.scanLibrary();
        getScanStatus(request, response);
    }

    @RequestMapping(value = "/getScanStatus")
    public void getScanStatus(HttpServletRequest request, HttpServletResponse response) {
        request = wrapRequest(request);
        ScanStatus scanStatus = new ScanStatus();
        scanStatus.setScanning(this.mediaScannerService.isScanning());
        scanStatus.setCount((long) this.mediaScannerService.getScanCount());

        Response res = createResponse();
        res.setScanStatus(scanStatus);
        this.jaxbWriter.writeResponse(request, response, res);
    }

    private HttpServletRequest wrapRequest(HttpServletRequest request) {
        return wrapRequest(request, false);
    }

    private HttpServletRequest wrapRequest(final HttpServletRequest request, boolean jukebox) {
        final String playerId = createPlayerIfNecessary(request, jukebox);
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                // Returns the correct player to be used in PlayerService.getPlayer()
                if ("player".equals(name)) {
                    return playerId;
                }

                // Support old style ID parameters.
                if ("id".equals(name)) {
                    return mapId(request.getParameter("id"));
                }

                return super.getParameter(name);
            }
        };
    }

    private String mapId(String id) {
        if (id == null || id.startsWith(CoverArtController.ALBUM_COVERART_PREFIX) ||
                id.startsWith(CoverArtController.ARTIST_COVERART_PREFIX) || StringUtils.isNumeric(id)) {
            return id;
        }

        try {
            String path = StringUtil.utf8HexDecode(id);
            MediaFile mediaFile = mediaFileService.getMediaFile(path);
            return String.valueOf(mediaFile.getId());
        } catch (Exception x) {
            return id;
        }
    }

    private Response createResponse() {
        return jaxbWriter.createResponse(true);
    }

    private void writeEmptyResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        jaxbWriter.writeResponse(request, response, createResponse());
    }

    public void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code, String message) throws Exception {
        jaxbWriter.writeErrorResponse(request, response, code, message);
    }

    private String createPlayerIfNecessary(HttpServletRequest request, boolean jukebox) {
        String username = request.getRemoteUser();
        String clientId = request.getParameter("c");
        if (jukebox) {
            clientId += "-jukebox";
        }

        List<Player> players = playerService.getPlayersForUserAndClientId(username, clientId);

        // If not found, create it.
        if (players.isEmpty()) {
            Player player = new Player();
            player.setIpAddress(request.getRemoteAddr());
            player.setUsername(username);
            player.setClientId(clientId);
            player.setName(clientId);
            player.setTechnology(jukebox ? PlayerTechnology.JUKEBOX : PlayerTechnology.EXTERNAL_WITH_PLAYLIST);
            playerService.createPlayer(player);
            players = playerService.getPlayersForUserAndClientId(username, clientId);
        }

        // Return the player ID.
        return !players.isEmpty() ? String.valueOf(players.get(0).getId()) : null;
    }

    private Locale getUserLocale(HttpServletRequest request) {
        return settingsService.getUserSettings(securityService.getCurrentUsername(request)).getLocale();
    }

    public enum ErrorCode {

        GENERIC(0, "A generic error."),
        MISSING_PARAMETER(10, "Required parameter is missing."),
        PROTOCOL_MISMATCH_CLIENT_TOO_OLD(20, "Incompatible Airsonic REST protocol version. Client must upgrade."),
        PROTOCOL_MISMATCH_SERVER_TOO_OLD(30, "Incompatible Airsonic REST protocol version. Server must upgrade."),
        NOT_AUTHENTICATED(40, "Wrong username or password."),
        NOT_AUTHORIZED(50, "User is not authorized for the given operation."),
        NOT_FOUND(70, "Requested data was not found.");

        private final int code;
        private final String message;

        ErrorCode(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class BookmarkKey extends Pair<String, Integer> {
        private BookmarkKey(String username, int mediaFileId) {
            super(username, mediaFileId);
        }

        static BookmarkKey forBookmark(org.airsonic.player.domain.Bookmark b) {
            return new BookmarkKey(b.getUsername(), b.getMediaFileId());
        }
    }
}
