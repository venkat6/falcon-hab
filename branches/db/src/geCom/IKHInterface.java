package geCom  ;

import com4j.*;

/**
 * IKHInterface Interface
 */
@IID("{80A43F86-E2CD-4671-A7FA-E5627B519711}")
public interface IKHInterface extends Com4jObject {
    /**
     * property currentView
     */
    @VTID(7)
    geCom.IKHViewInfo currentView(
        int terrain);

    /**
     * property streamingProgressPercentage
     */
    @VTID(8)
    int streamingProgressPercentage();

    /**
     * method SaveScreenShot
     */
    @VTID(9)
    void saveScreenShot(
        java.lang.String fileName,
        int quality);

    /**
     * method OpenFile
     */
    @VTID(10)
    void openFile(
        java.lang.String fileName);

    /**
     * method QuitApplication
     */
    @VTID(11)
    void quitApplication();

    /**
     * method SetRenderWindowSize
     */
    @VTID(12)
    void setRenderWindowSize(
        int width,
        int height);

    /**
     * property autopilotSpeed
     */
    @VTID(13)
    double autoPilotSpeed();

    /**
     * property autopilotSpeed
     */
    @VTID(14)
    void autoPilotSpeed(
        double pVal);

    /**
     * property currentViewExtents
     */
    @VTID(15)
    geCom.IKHViewExtents currentViewExtents();

    /**
     * method setView
     */
    @VTID(16)
    void setView(
        geCom.IKHViewInfo view,
        int terrain,
        double speed);

    /**
     * method LoadKml
     */
    @VTID(17)
    void loadKml(
        Holder<java.lang.String> kmlData);

    /**
     * method getFeatureByName
     */
    @VTID(18)
    geCom.IKHFeature getFeatureByName(
        java.lang.String name);

    /**
     * method setViewParams
     */
    @VTID(19)
    void setViewParams(
        double lat,
        double lon,
        double range,
        double tilt,
        double azimuth,
        int terrain,
        double speed);

    /**
     * method setFeatureView
     */
    @VTID(20)
    void setFeatureView(
        geCom.IKHFeature feature,
        double speed);

    /**
     * method getPointOnTerrainFromScreenCoords
     */
    @VTID(21)
    double[] getPointOnTerrainFromScreenCoords(
        double screen_x,
        double screen_y);

    /**
     * method getCurrentVersion
     */
    @VTID(22)
    void getCurrentVersion(
        Holder<Integer> major,
        Holder<Integer> minor,
        Holder<Integer> build,
        Holder<geCom.appType> appType);

    /**
     * method isClientInitialized
     */
    @VTID(23)
    int isClientInitialized();

}
