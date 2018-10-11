/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package m35.subsonicapi;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Michael
 */
public class CoverArtStuff {
    private static class CoverArt {
        String ownerId;
        String imageFile;
        CoverArtImageType type;
        int actualWidth;
        int actualHeight;
        List<CoverArtCache> cachedImaged;
        String mimeType;
    }
    
    private enum CoverArtImageType {
        Image {
            @Override
            public BufferedImage getImage(String imageFile) {
                throw new UnsupportedOperationException();
            }
        }, 
        MusicMetaData {
            @Override
            public BufferedImage getImage(String imageFile) {
                throw new UnsupportedOperationException();
            }
        }, 
        Video {
            @Override
            public BufferedImage getImage(String imageFile) {
                throw new UnsupportedOperationException();
            }
        };
        public static final int VIDEO_THUMBNAIL_SECONDS = 10;
        public abstract BufferedImage getImage(String imageFile);
    }
    
    private enum CoverArtStyle {
        SingleImage,
        Collage,
        FallbackCover
    }
    
    private static class CoverArtCache {
        Date lastGenerated;
        int squareSize;
        String cachedImageFile;
        String mimeType;
        CoverArtStyle style;
    }

    
}
