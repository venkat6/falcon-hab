package geCom  ;

import com4j.*;

/**
 * IApplicationGE Interface
 */
@IID("{2830837B-D4E8-48C6-B6EE-04633372ABE4}")
public interface IApplicationGE extends Com4jObject {
    /**
     * method GetCamera
     */
    @VTID(7)
    geCom.ICameraInfoGE getCamera(
        int considerTerrain);

    /**
     * method SetCamera
     */
    @VTID(8)
    void setCamera(
        geCom.ICameraInfoGE camera,
        double speed);

    /**
     * method SetCameraParams
     */
    @VTID(9)
    void setCameraParams(
        double lat,
        double lon,
        double alt,
        geCom.AltitudeModeGE altMode,
        double range,
        double tilt,
        double azimuth,
        double speed);

    /**
     * property StreamingProgressPercentage
     */
    @VTID(10)
    int streamingProgressPercentage();

    /**
     * method SaveScreenShot
     */
    @VTID(11)
    void saveScreenShot(
        java.lang.String fileName,
        int quality);

    /**
     * method OpenKmlFile
     */
    @VTID(12)
    void openKmlFile(
        java.lang.String fileName,
        int suppressMessages);

    /**
     * method LoadKmlData
     */
    @VTID(13)
    void loadKmlData(
        Holder<java.lang.String> kmlData);

    /**
     * property AutoPilotSpeed
     */
    @VTID(14)
    double autoPilotSpeed();

    /**
     * property AutoPilotSpeed
     */
    @VTID(15)
    void autoPilotSpeed(
        double pVal);

    /**
     * property ViewExtents
     */
    @VTID(16)
    geCom.IViewExtentsGE viewExtents();

    /**
     * method GetFeatureByName
     */
    @VTID(17)
    geCom.IFeatureGE getFeatureByName(
        java.lang.String name);

    /**
     * method GetFeatureByHref
     */
    @VTID(18)
    geCom.IFeatureGE getFeatureByHref(
        java.lang.String href);

    /**
     * method SetFeatureView
     */
    @VTID(19)
    void setFeatureView(
        geCom.IFeatureGE feature,
        double speed);

    /**
     * method GetPointOnTerrainFromScreenCoords
     */
    @VTID(20)
    geCom.IPointOnTerrainGE getPointOnTerrainFromScreenCoords(
        double screen_x,
        double screen_y);

    /**
     * property VersionMajor
     */
    @VTID(21)
    int versionMajor();

    /**
     * property VersionMinor
     */
    @VTID(22)
    int versionMinor();

    /**
     * property VersionBuild
     */
    @VTID(23)
    int versionBuild();

    /**
     * property VersionAppTye
     */
    @VTID(24)
    geCom.AppTypeGE versionAppType();

    /**
     * method IsInitialized
     */
    @VTID(25)
    int isInitialized();

    /**
     * method IsOnline
     */
    @VTID(26)
    int isOnline();

    /**
     * method Login
     */
    @VTID(27)
    void login();

    /**
     * method Logout
     */
    @VTID(28)
    void logout();

    /**
     * method ShowDescriptionBalloon
     */
    @VTID(29)
    void showDescriptionBalloon(
        geCom.IFeatureGE feature);

    /**
     * method HideDescriptionBalloons
     */
    @VTID(30)
    void hideDescriptionBalloons();

    /**
     * method GetHighlightedFeature
     */
    @VTID(31)
    geCom.IFeatureGE getHighlightedFeature();

    /**
     * method GetMyPlaces
     */
    @VTID(32)
    geCom.IFeatureGE getMyPlaces();

    /**
     * method GetTemporaryPlaces
     */
    @VTID(33)
    geCom.IFeatureGE getTemporaryPlaces();

    /**
     * method GetLayersDatabases
     */
    @VTID(34)
    geCom.IFeatureCollectionGE getLayersDatabases();

    @VTID(34)
    @ReturnValue(defaultPropertyThrough={geCom.IFeatureCollectionGE.class})
    geCom.IFeatureGE getLayersDatabases(
        int index);

    /**
     * property ElevationExaggeration
     */
    @VTID(35)
    double elevationExaggeration();

    /**
     * property ElevationExaggeration
     */
    @VTID(36)
    void elevationExaggeration(
        double pExaggeration);

    /**
     * method GetMainHwnd
     */
    @VTID(37)
    int getMainHwnd();

    /**
     * property TourController
     */
    @VTID(38)
    geCom.ITourControllerGE tourController();

    /**
     * property SearchController
     */
    @VTID(39)
    geCom.ISearchControllerGE searchController();

    /**
     * property AnimationController
     */
    @VTID(40)
    geCom.IAnimationControllerGE animationController();

    /**
     * method GetRenderHwnd
     */
    @VTID(41)
    int getRenderHwnd();

}
