package geCom  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {} // instanciation is not allowed


    /**
     * ApplicationGE Class
     */
    public static geCom.IApplicationGE createApplicationGE() {
        return COM4J.createInstance( geCom.IApplicationGE.class, "{8097D7E9-DB9E-4AEF-9B28-61D82A1DF784}" );
    }

    /**
     * TimeGE Class
     */
    public static geCom.ITimeGE createTimeGE() {
        return COM4J.createInstance( geCom.ITimeGE.class, "{1AEDB68D-18A7-4CA9-B41B-3CE7E59FAB24}" );
    }

    /**
     * TimeIntervalGE Class
     */
    public static geCom.ITimeIntervalGE createTimeIntervalGE() {
        return COM4J.createInstance( geCom.ITimeIntervalGE.class, "{42DF0D46-7D49-4AE5-8EF6-9CA6E41EFEC1}" );
    }

    /**
     * CameraInfoGE Class
     */
    public static geCom.ICameraInfoGE createCameraInfoGE() {
        return COM4J.createInstance( geCom.ICameraInfoGE.class, "{645EEE5A-BD51-4C05-A6AF-6F2CF8950AAB}" );
    }

    /**
     * ViewExtentsGE Class
     */
    public static geCom.IViewExtentsGE createViewExtentsGE() {
        return COM4J.createInstance( geCom.IViewExtentsGE.class, "{D93BF052-FC68-4DB6-A4F8-A4DC9BEEB1C0}" );
    }

    /**
     * TourControllerGE Class
     */
    public static geCom.ITourControllerGE createTourControllerGE() {
        return COM4J.createInstance( geCom.ITourControllerGE.class, "{77C4C807-E257-43AD-BB3F-7CA88760BD29}" );
    }

    /**
     * SearchControllerGE Class
     */
    public static geCom.ISearchControllerGE createSearchControllerGE() {
        return COM4J.createInstance( geCom.ISearchControllerGE.class, "{A4F65992-5738-475B-9C16-CF102BCDE153}" );
    }

    /**
     * AnimationControllerGE Class
     */
    public static geCom.IAnimationControllerGE createAnimationControllerGE() {
        return COM4J.createInstance( geCom.IAnimationControllerGE.class, "{1A239250-B650-4B63-B4CF-7FCC4DC07DC6}" );
    }

    /**
     * FeatureGE Class
     */
    public static geCom.IFeatureGE createFeatureGE() {
        return COM4J.createInstance( geCom.IFeatureGE.class, "{CBD4FB70-F00B-4963-B249-4B056E6A981A}" );
    }

    /**
     * FeatureCollectionGE class
     */
    public static geCom.IFeatureCollectionGE createFeatureCollectionGE() {
        return COM4J.createInstance( geCom.IFeatureCollectionGE.class, "{9059C329-4661-49B2-9984-8753C45DB7B9}" );
    }

    /**
     * PointOnTerrainGE class
     */
    public static geCom.IPointOnTerrainGE createPointOnTerrainGE() {
        return COM4J.createInstance( geCom.IPointOnTerrainGE.class, "{1796A329-04C1-4C07-B28E-E4A807935C06}" );
    }

    /**
     * KHInterface Class
     */
    public static geCom.IKHInterface createKHInterface() {
        return COM4J.createInstance( geCom.IKHInterface.class, "{AFD07A5E-3E20-4D77-825C-2F6D1A50BE5B}" );
    }

    /**
     * KHViewInfo Class
     */
    public static geCom.IKHViewInfo createKHViewInfo() {
        return COM4J.createInstance( geCom.IKHViewInfo.class, "{A2D4475B-C9AA-48E2-A029-1DB829DACF7B}" );
    }

    /**
     * KHViewExtents Class
     */
    public static geCom.IKHViewExtents createKHViewExtents() {
        return COM4J.createInstance( geCom.IKHViewExtents.class, "{63E6BE14-A742-4EEA-8AF3-0EC39F10F850}" );
    }

    /**
     * KHFeature Class
     */
    public static geCom.IKHFeature createKHFeature() {
        return COM4J.createInstance( geCom.IKHFeature.class, "{B153D707-447A-4538-913E-6146B3FDEE02}" );
    }
}
