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
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import m35.subsonicapi.Api;
import org.airsonic.player.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestUtils;
import static org.springframework.web.bind.ServletRequestUtils.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.subsonic.restapi.*;

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

    private m35.subsonicapi.Api api;

    private final JAXBWriter jaxbWriter = new JAXBWriter();

    private static final String NOT_YET_IMPLEMENTED = "Not yet implemented";
    private static final String NO_LONGER_SUPPORTED = "No longer supported";

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public void handleMissingRequestParam(HttpServletRequest request,
                                          HttpServletResponse response,
                                          MissingServletRequestParameterException exception) throws Exception {
        error(request, response, ErrorCode.MISSING_PARAMETER, "Required param ("+exception.getParameterName()+") is missing");
    }

    public void error(HttpServletRequest request, HttpServletResponse response, ErrorCode code, String message) throws Exception {
        jaxbWriter.writeErrorResponse(request, response, code, message);
    }
    
    @RequestMapping(value = "/ping")
    public void ping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        api.ping();
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
        List<Long> time = Util.toLongList(getLongParameters(request, "time"));
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
        List<String> id = Arrays.asList(getStringParameters(request, "id"));
        String current = getStringParameter(request, "current");
        Long position = getLongParameter(request, "position");
        String c = getRequiredStringParameter(request, "c"); // changed by, required
        api.savePlayQueue_implementation(id, current, position, c);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getShares")
    public void getShares(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setShares(api.getShares());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/createShare")
    public void createShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<String> id = Arrays.asList(getStringParameters(request, "id"));
        String description = getStringParameter(request, "description");
        Long expires = getLongParameter(request, "expires");
        Response res = createResponse();
        Shares shares = new Shares();
        shares.getShare().add(api.createShare(id, description, expires));
        res.setShares(shares);
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/deleteShare")
    public void deleteShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        api.deleteShare(id);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/updateShare")
    public void updateShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        String description = getStringParameter(request, "description");
        Long expires = getLongParameter(request, "expires");
        api.updateShare(id, description, expires);
        writeEmptyResponse(request, response);
    }

    @SuppressWarnings("UnusedParameters")
    public ModelAndView videoPlayer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer timeOffset = getIntParameter(request, "timeOffset");
        String u = getStringParameter(request, "u");
        String p = getStringParameter(request, "p");
        String c = getStringParameter(request, "c");
        String v = getStringParameter(request, "v");
        Integer maxBitRate = getIntParameter(request, "maxBitRate");
        Boolean autoplay = getBooleanParameter(request, "autoplay");
        return null;
    }

    @RequestMapping(value = "/getCoverArt")
    public void getCoverArt(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Integer size = ServletRequestUtils.getIntParameter(request, "size");
        Api.BinaryResponse binaryResponse = api.getCoverArt(id, size);
        writeBinaryResponse(response, binaryResponse);
    }

    @RequestMapping(value = "/getAvatar")
    public void getAvatar(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getStringParameter(request, "username");
        String id = getStringParameter(request, "id");
        Boolean forceCustom = getBooleanParameter(request, "forceCustom");
        api.getAvatar_implementation(username, id, forceCustom);
    }

    @RequestMapping(value = "/changePassword")
    public void changePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getStringParameter(request, "username");
        String password = getStringParameter(request, "password");
        api.changePassword(username, password);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/getUser")
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getRequiredStringParameter(request, "username");
        Response res = createResponse();
        res.setUser(api.getUser(username));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getUsers")
    public void getUsers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Response res = createResponse();
        res.setUsers(api.getUsers());
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/createUser")
    public void createUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getStringParameter(request, "username");
        String password = getStringParameter(request, "password");
        String email = getStringParameter(request, "email");
        Boolean ldapAuthenticated = getBooleanParameter(request, "ldapAuthenticated");
        Boolean adminRole = getBooleanParameter(request, "adminRole");
        Boolean settingsRole = getBooleanParameter(request, "settingsRole");
        Boolean streamRole = getBooleanParameter(request, "streamRole");
        Boolean jukeboxRole = getBooleanParameter(request, "jukeboxRole");
        Boolean downloadRole = getBooleanParameter(request, "downloadRole");
        Boolean uploadRole = getBooleanParameter(request, "uploadRole");
        Boolean playlistRole = getBooleanParameter(request, "playlistRole");
        Boolean coverArtRole = getBooleanParameter(request, "coverArtRole");
        Boolean commentRole = getBooleanParameter(request, "commentRole");
        Boolean podcastRole = getBooleanParameter(request, "podcastRole");
        Boolean shareRole = getBooleanParameter(request, "shareRole");
        Boolean videoConversionRole = getBooleanParameter(request, "videoConversionRole");
        List<Integer> musicFolderId = Util.toIntegerList(getIntParameters(request, "musicFolderId"));

        api.createUser(username, password, email, ldapAuthenticated, adminRole, 
                settingsRole, streamRole, jukeboxRole, downloadRole, uploadRole, 
                playlistRole, coverArtRole, commentRole, podcastRole, shareRole, 
                videoConversionRole, musicFolderId);
        
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/updateUser")
    public void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getStringParameter(request, "username");
        String password = getStringParameter(request, "password");
        String email = getStringParameter(request, "email");
        Boolean ldapAuthenticated = getBooleanParameter(request, "ldapAuthenticated");
        Boolean adminRole = getBooleanParameter(request, "adminRole");
        Boolean settingsRole = getBooleanParameter(request, "settingsRole");
        Boolean streamRole = getBooleanParameter(request, "streamRole");
        Boolean jukeboxRole = getBooleanParameter(request, "jukeboxRole");
        Boolean downloadRole = getBooleanParameter(request, "downloadRole");
        Boolean uploadRole = getBooleanParameter(request, "uploadRole");
        Boolean coverArtRole = getBooleanParameter(request, "coverArtRole");
        Boolean commentRole = getBooleanParameter(request, "commentRole");
        Boolean podcastRole = getBooleanParameter(request, "podcastRole");
        Boolean shareRole = getBooleanParameter(request, "shareRole");
        Boolean videoConversionRole = getBooleanParameter(request, "videoConversionRole");
        List<Integer> musicFolderId = Util.toIntegerList(getIntParameters(request, "musicFolderId"));
        Integer maxBitRate = getIntParameter(request, "maxBitRate");

        api.updateUser(username, password, email, ldapAuthenticated, adminRole, 
                settingsRole, streamRole, jukeboxRole, downloadRole, uploadRole, 
                coverArtRole, commentRole, podcastRole, shareRole, 
                videoConversionRole, musicFolderId, maxBitRate);
        
        writeEmptyResponse(request, response);
    }

    @RequestMapping(value = "/deleteUser")
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = getStringParameter(request, "username");
        api.deleteUser(username);
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
        String artist = getStringParameter(request, "artist");
        String title = getStringParameter(request, "title");
        Response res = createResponse();
        res.setLyrics(api.getLyrics(artist, title));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/setRating")
    public void setRating(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        int rating = getRequiredIntParameter(request, "rating");
        api.setRating(id, rating);
        writeEmptyResponse(request, response);
    }

    @RequestMapping(path = "/getAlbumInfo")
    public void getAlbumInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = createResponse();
        res.setAlbumInfo(api.getAlbumInfo(id)); // !! bug fix, wasn't returning anything
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(path = "/getAlbumInfo2")
    public void getAlbumInfo2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = createResponse();
        res.setAlbumInfo(api.getAlbumInfo2(id)); // !! bug fix, wasn't returning anything
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getVideoInfo")
    public void getVideoInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String id = getStringParameter(request, "id");
        Response res = createResponse();
        res.setVideoInfo(api.getVideoInfo(id));
        jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getCaptions")
    public ResponseEntity<String> getCaptions() throws Exception {
        if (false) {
            HttpServletRequest request = null;
            HttpServletResponse response = null;
            String id = getStringParameter(request, "id");
            String format = getStringParameter(request, "format");
            Response res = createResponse();
            api.getCaptions(id, format);
            // still no idea what to respond with
            jaxbWriter.writeResponse(request, response, res);
        }
        return ResponseEntity.status(HttpStatus.SC_NOT_IMPLEMENTED).body(NOT_YET_IMPLEMENTED);
    }

    @RequestMapping(value = "/startScan")
    public void startScan(HttpServletRequest request, HttpServletResponse response) {
        Response res = createResponse();
        res.setScanStatus(api.startScan());
        this.jaxbWriter.writeResponse(request, response, res);
    }

    @RequestMapping(value = "/getScanStatus")
    public void getScanStatus(HttpServletRequest request, HttpServletResponse response) {
        Response res = createResponse();
        res.setScanStatus(api.getScanStatus());
        this.jaxbWriter.writeResponse(request, response, res);
    }

    private Response createResponse() {
        return jaxbWriter.createResponse(true);
    }

    private void writeEmptyResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        jaxbWriter.writeResponse(request, response, createResponse());
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

}
