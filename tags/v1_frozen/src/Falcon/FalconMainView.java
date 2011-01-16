/*
 * FalconMainView.java
 *
 * Most work happens in callsignGPSEvent() method
 */

package Falcon;

import AGW_Sockets.JavaAGW;
import geCom.ClassFactory;
import geCom.IApplicationGE;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedList;
import org.mozilla.browser.MozillaPanel;
import org.mozilla.interfaces.nsIRequest;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.interfaces.nsIURI;
import org.mozilla.interfaces.nsIWebProgress;
import org.mozilla.interfaces.nsIWebProgressListener;
import org.mozilla.xpcom.Mozilla;


/**
 * The application's main frame.
 */
public class FalconMainView extends FrameView implements CallsignGPSListener, PacketListener{

    private static FalconMainView fmv_;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JDialog testStrBox;
    private JDialog testPktBox;
    private SetupFlightCtrBox fcSetupBox;
    private SetupPayloadBox payloadSetupBox;
    private Prediction origPrediction_;
    private Prediction currPrediction_;
    private JavaAGW agw_;
    private ColorChooser colorChooser_;
    private LinkedList<Position> posHis_;
    private String webPostingAddress = "http://www.sscl.iastate.edu/habet/tracking_posting_portal.php";
    private Position burstPos_;
    private final int digShown_ = 5;
    private boolean azelControlEnabled = false;

    public static void initialize(SingleFrameApplication app){
        if(fmv_==null)fmv_ = new FalconMainView(app);
    }
    
    public static FalconMainView getInstance(){
        return fmv_;
    }

    public void postInit() {

        //MozillaAutomation.blockingLoad(moz, "http://www.yahoo.com"); //$NON-NLS-1$

        //System.err.println("loading started");
        //MozillaAutomation.blockingLoad(moz, getWebPath());
        //System.err.println("loading finished");
        moz.load(getWebPath());
        while (moz.getChromeAdapter() == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            System.out.println("waiting chrome");
        }
        while (moz.getChromeAdapter().getWebBrowser() == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            System.out.println("waiting browser");
        }
        System.out.println("done waiting");
        Listener mozLis = new Listener();
        moz.getChromeAdapter().getWebBrowser().addWebBrowserListener(mozLis, nsIWebProgressListener.NS_IWEBPROGRESSLISTENER_IID);

        agw_ = new JavaAGW(this);
        agw_.registerAGWPEConnectMenuItem(agwpeConnectMI);
        agw_.registerAGWPEMonitorMenuItem(agwpeToggleMonMI);
        agw_.registerLogWindow(logPanel);
        agw_.registerStatusField(statusMessageLabel);
        PacketExchange.getInstance().addCallsignListener(this, "KB0MGQ");
        PacketExchange.getInstance().addPacketListener(this);
        posHis_ = new LinkedList<Position>();

        JFrame mainFrame = FalconMainApp.getApplication().getMainFrame();
        payloadSetupBox = new SetupPayloadBox(mainFrame, true);
        payloadSetupBox.setLocationRelativeTo(mainFrame);
        fcSetupBox = new SetupFlightCtrBox(mainFrame, true);
        fcSetupBox.setLocationRelativeTo(mainFrame);
        colorChooser_ = new ColorChooser(mainFrame, true);
        colorChooser_.setLocationRelativeTo(mainFrame);

        origPrediction_ = new Prediction(
                new Position(42.0276, -93.6532, 300.0),
                new Position(42.0276, -93.6532, 300.0),
                "winds.txt",
                "origPrediction.kml",
                Double.parseDouble(payloadSetupBox.getBMassVal()),
                Double.parseDouble(payloadSetupBox.getWeightVal()),
                Double.parseDouble(payloadSetupBox.getExcessLiftVal()),
                Double.parseDouble(payloadSetupBox.getDragCoefVal()),
                0.0,
                false,
                colorChooser_.getOrigPredLineColor());
        currPrediction_ = new Prediction(
                new Position(42.0276, -93.6532, 300.0),
                new Position(42.0276, -93.6532, 300.0),
                "winds.txt",
                "currPrediction.kml",
                Double.parseDouble(payloadSetupBox.getBMassVal()),
                Double.parseDouble(payloadSetupBox.getWeightVal()),
                Double.parseDouble(payloadSetupBox.getExcessLiftVal()),
                Double.parseDouble(payloadSetupBox.getDragCoefVal()),
                0.0,
                false,
                colorChooser_.getPredLineColor());
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = FalconMainApp.getApplication().getMainFrame();
            aboutBox = new FalconMainAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        FalconMainApp.getApplication().show(aboutBox);
    }

        @Action
    public void devToggleAddressBar() {
        if(moz.getToolbar().isVisible()){
            moz.getToolbar().setVisible(false);
        }else{
            moz.getToolbar().setVisible(true);
        }
    }

    public void useDevMode(){
        devMenu.setVisible(true);
    }

    @Action
    public void devToggleStatusBar() {
        if(moz.getStatusbar().isVisible()){
            moz.getStatusbar().setVisible(false);
            //TODO: need to resolve bug where the statusbar doesn't toggle until the toolbar is toggled
        }else{
            moz.getStatusbar().setVisible(true);
        }
    }

    private String getWebPath() {
        String s = "";
        try {
            File dir1 = new File(".");
            s = "file:///";
            s += dir1.getCanonicalPath();
            s = s.replace('\\', '/');
            String webMapsFile = "/gmap3.htm";
            s += webMapsFile;
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Using web file: " + s);
        return s;
    }


        private class Listener implements nsIWebProgressListener {
        @Override
        public void onLocationChange(nsIWebProgress webProgress, nsIRequest request, nsIURI location) {
        }

        @Override
        public void onProgressChange(nsIWebProgress webProgress, nsIRequest request,
                int curSelfProgress, int maxSelfProgress, int curTotalProgress, int maxTotalProgress) {
//                System.out.println("Progress(curSelf,maxSelf,curTot,maxTot:"
//                        +curSelfProgress+","+maxSelfProgress+","
//                        +curTotalProgress+","+maxTotalProgress);
        }

        @Override
        public void onSecurityChange(nsIWebProgress webProgress, nsIRequest request, long state) {
        }

        @Override
        public void onStateChange(nsIWebProgress webProgress, nsIRequest request, long stateFlags,
                long status) {
            if ((stateFlags & nsIWebProgressListener.STATE_IS_NETWORK) != 0
                    && (stateFlags & nsIWebProgressListener.STATE_START) != 0) {
//                pageLoadStarted();
                System.out.println("pageloadstarted");
            }

            if ((stateFlags & nsIWebProgressListener.STATE_IS_NETWORK) != 0
                    && (stateFlags & nsIWebProgressListener.STATE_STOP) != 0) {
//                pageLoadStopped(status != 0);
                System.out.println("pageloadstopped");
                mapLoaded();
            }
        }

        @Override
        public void onStatusChange(nsIWebProgress webProgress, nsIRequest request, long status,
                String message) {

        }

        @Override
        public nsISupports queryInterface(String iid) {
            return Mozilla.queryInterface(this, iid);
        }
    }

    @Action
    public void testButton() {
        if(moz.getChromeAdapter()==null){
            System.out.println("nullWEB");
        }
        System.out.println("testbutton");
        //GoogleMapsHandler.writeBalloonPath(moz, origPrediction_.getPrediction());
        //GoogleMapsHandler.updateMarker(moz, origPrediction_.getPrediction().getLast(), "pland");
        GoogleMapsHandler.testLine(moz);
    }

    private void mapLoaded(){
        //moz.jsexec("launch.show();");
    }

    @Action
    public void agwpeConnect() {
        agw_.mnuStart();
    }

    @Action
    public void agwpeOnMonitor() {
        agw_.monitorAGW();
    }

    @Action
    public void agwpeReqDisc() {
        agw_.mnuDisc();
    }

    @Action
    public void agwpeSetHost() {
        agw_.mnuHost();
    }

    @Action
    public void agwpeGetVer() {
        agw_.getAGWVersion();
    }

    @Action
    public void agwpeDebug() {
        agw_.mnuDebug();
    }

    @Action
    public void devTestStr() {
        if (testStrBox == null) {
            JFrame mainFrame = FalconMainApp.getApplication().getMainFrame();
            testStrBox = new devTestStrBox(mainFrame,true);
            testStrBox.setLocationRelativeTo(mainFrame);
        }
        FalconMainApp.getApplication().show(testStrBox);

    }

    @Action
    public void setupEnterFCInfo() {
        FalconMainApp.getApplication().show(fcSetupBox);

    }

    @Action
    public void setupPayload() {
        FalconMainApp.getApplication().show(payloadSetupBox);
    }

    @Action
    public void sendTrackingGE() {
        KmlWriter.writeKml(
                "LX-joe.kml",
                posHis_,
                "HABET Tracking",
                colorChooser_.getPayloadLineColor());
        System.out.println("tracking sending to googleearth");
        IApplicationGE ge = ClassFactory.createApplicationGE();
        try{
        File currdir = new File(".");
        ge.openKmlFile(currdir.getCanonicalPath() + "\\LX-joe.kml", 1);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    @Action
    public void sendCurrPredGE() {
        KmlWriter.writeKml(
                "currPrediction.kml",
                currPrediction_.getPrediction(),
                "HABET Current Prediction",
                colorChooser_.getPredLineColor());
        System.out.println("prediction sending to googleearth");
        IApplicationGE ge = ClassFactory.createApplicationGE();
        //Holder hStr = new Holder(origPrediction_.getKmlData());
        //ge.loadKmlData(hStr);//crashes java
        try{
        File currdir = new File(".");

        ge.openKmlFile(currdir.getCanonicalPath() + "\\currPrediction.kml", 1);

            //File currdir = new File(".");
            //Process p = Runtime.getRuntime().exec("C:\\Program Files (x86)\\Google\\Google Earth\\googleearth.exe " + "LX-joe.kml");
            //System.out.println("C:\\Program Files (x86)\\Google\\Google Earth\\googleearth.exe " + "C:\\" + currdir.getCanonicalPath() + "\\LX-joe.kml");
            //p.waitFor();
            //TODO: get googleearth exec location from registry
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }


    @Action
    public void sendOPredGE() {
        KmlWriter.writeKml(
                "origPrediction.kml",
                origPrediction_.getPrediction(),
                "HABET Original Prediction",
                colorChooser_.getOrigPredLineColor());
        System.out.println("origPred sending to googleearth");
        IApplicationGE ge = ClassFactory.createApplicationGE();
        try{
        File currdir = new File(".");
        ge.openKmlFile(currdir.getCanonicalPath() + "\\origPrediction.kml", 1);
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public void callsignGPSEvent(Position p, String aprsStr){
        //logPanel.append(hdrStr + "\n");
        //logPanel.append(dataStr + "\n");
        posHis_.add(p);
        GoogleMapsHandler.updateMarker(moz, p, "bCurr");
        GoogleMapsHandler.writeBalloonPath(moz, posHis_, colorChooser_.getPayloadLineColor());
        updateTrackPay(p);
        updatePrediction(p);
        updateAzel(p);
        updateWebsiteTracking(p);
        updateAscentRate();
        updateHeading();
        updateGndSpd();
        updateBurst();

        
        //TODO: check for burst from packets received
        //this is classified as the position before two drops in alt
        //TODO: add recovery tracking options under another callsign
        logPanel.append(aprsStr + "\n");
        appendLogFile(payloadSetupBox.getFlightNumber()+"_logfile.txt",aprsStr + "\n");
    }

    public void packetReceivedEvent(String sHdr, String sData){
        if(devListenAllCheckBox.isSelected()){
            logPanel.append("ListenAllStation Header>>" + sHdr + "\n");
            logPanel.append("ListenAllStation Data>>" + sData + "\n");
            appendLogFile("falconlog.txt",sData + "\n");
        }

    }

    //FIXME This currently looks through history for max altitude each time it is called
    //		Only need to do it once after burst is detected.  Maybe remove call from GPSEvent method and
    //		add a listener for the menu item?  Automatic detection should also trigger the box to be checked
    public void updateBurst(){
        //must have at least two 500ft drops after two rises

        if(devHasBurstCheckBox.isSelected()){
            double maxAlt = 0;
            int index = 0;
            int maxAltIndex = 0;
            for (Position p : posHis_) {
                if (p.getAlt() > maxAlt) {
                    maxAlt = p.getAlt();
                    maxAltIndex = index;
                    break;
                }
                index++;
            }

            burstPos_ = new Position(posHis_.get(maxAltIndex));
            trackBurAltText.setText(burstPos_.getReadableAlt());
            trackBurLatText.setText(burstPos_.getReadableLat());
            trackBurLonText.setText(burstPos_.getReadableLong());
            trackBurTimeText.setText(burstPos_.getTimstampString());
        }

//        for(int i = posHis_.size()-1; i>=0; i--){
//            if(i>=2){
//                if(posHis_.get(i) < maxAlt && posHis_.get(i-1) < maxAlt){
//                    burstPos_ = pos
//                }
//
//            }
//        }
    }

    public void updateWebsiteTracking(Position p){
        String postStr = "HABET Tracking for an Unmanned Free Balloon<br />"
                        + "Flight Number: " + payloadSetupBox.getFlightNumber() + "<br />"
                        + "APRS Callsign: " + payloadSetupBox.getCallsignVal() + "<br />"
                        + "Latitude: " + p.getReadableLat() + " (Decimal Degrees)<br />"
                        + "Longitude: " + p.getReadableLong() + " (Decimal Degrees)<br />"
                        + "Altitude: " + p.getReadableAlt() + " meters<br />"
                        + "Ascent Rate: " + trackPayAscText.getText() + " ft/min<br />"
                        + "Timestamp: " + p.getTimstampString() + "\n";


        //System.out.println("Posting data...");
        try {
            // Construct data
            String data = URLEncoder.encode("tracking_update", "UTF-8") + "=" + URLEncoder.encode(postStr, "UTF-8");

            // Send data
            URL url = new URL(webPostingAddress);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
                System.out.println(line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    @Action
    public void devTestPkt() {
        if (testPktBox == null) {
            JFrame mainFrame = FalconMainApp.getApplication().getMainFrame();
            testPktBox = new devTestPktBox(mainFrame,true);
            testPktBox.setLocationRelativeTo(mainFrame);
        }
        FalconMainApp.getApplication().show(testPktBox);
    }

    @Action
    public void devListenAllToggle(){
        //devListenAllCheckBox.isSelected(); value will change - FYI
    }

    private void updateTrackPay(Position p){
        trackPayLatText.setText(p.getReadableLat());
        trackPayLonText.setText(p.getReadableLong());
        trackPayAltText.setText(p.getReadableAlt());
        trackPayXmitText.setText(p.getTimstampString());
        if(posHis_.size()>1){
            //calculate heading, groundspeed, ascentrate


        }
        //logPanel.append("ParsedPosition:" + p.toString() + "\n");

    }

    private void updateAscentRate(){
        int numForAvg = posHis_.size()>=5 ? 5 : posHis_.size();
        if(numForAvg == 0 || posHis_.size() < 2){
            trackPayAscText.setText("unknown");
            return;
        }
        double dAscRateTotals = 0.0;
        for(int i = 0; i<numForAvg-1; i++){
            long timeInMilliseconds = posHis_.get(posHis_.size()-i-1).getTimestamp().getTime() 
                    - posHis_.get(posHis_.size()-i-2).getTimestamp().getTime();
            double timeInMinutes = timeInMilliseconds / 60000.0;
            double altDiffMeters = (posHis_.get(posHis_.size()-i-1).getAlt() - posHis_.get(posHis_.size()-i-2).getAlt());
            double altDiffFeet = altDiffMeters * 3937.0 / 1200.0;
            dAscRateTotals = dAscRateTotals + altDiffFeet / timeInMinutes;
        }

        double ascRateAvg = dAscRateTotals / (numForAvg - 1);
        String ascRateStr = Functions.dblToPrettyStr(ascRateAvg, digShown_);
        trackPayAscText.setText(ascRateStr);
    }

    private void updateGndSpd(){
        if(posHis_.size()>=2){
            Double spd = Functions.calcVelocity(posHis_.get(posHis_.size()-2), posHis_.getLast());
            trackPaySpdText.setText(Functions.dblToPrettyStr(spd, digShown_));
        }else{
            trackPaySpdText.setText("unknown");
        }
    }

    private void updateHeading(){
        if(posHis_.size()>=2){
            Double[] dArr = AzelCalc.calc(posHis_.get(posHis_.size()-2), posHis_.getLast());
            String heading = ""+(Math.floor(dArr[0]*1000000.0)/1000000.0);
            trackPayHdgText.setText(heading);
        }
    }

    private void updateAzel(Position p){
        Double[] dArr = AzelCalc.calc(fcSetupBox.getFcPos(), p);
        String az = ""+(Math.floor(dArr[0]*1000000.0)/1000000.0);
        String el = ""+(Math.floor(dArr[1]*1000000.0)/1000000.0);
        if(azelControlEnabled) {
            eharstad.sscl.falcon.RotorControl.setAzEl(Math.floor(dArr[0]*1000000.0)/1000000.0, Math.floor(dArr[1]*1000000.0)/1000000.0);
        }
        azelAzText.setText(az);
        azelElText.setText(el);
    }

    private void updatePrediction(Position p){
        Prediction pred = new Prediction(
                new Position(42.0276,-93.6532,300.0),
                p,
                "winds.txt",
                "currPrediction.kml",
                Double.parseDouble(payloadSetupBox.getBMassVal()),
                Double.parseDouble(payloadSetupBox.getWeightVal()),
                Double.parseDouble(payloadSetupBox.getExcessLiftVal()),
                Double.parseDouble(payloadSetupBox.getDragCoefVal()),
                0.0,
                devHasBurstCheckBox.isSelected(),
                colorChooser_.getPredLineColor()
            );
        GoogleMapsHandler.writePredictionPath(moz,pred.getPrediction(),colorChooser_.getPredLineColor());
        Position pBurst = pred.getBurstPosition();
        predBurLatText.setText(pBurst.getReadableLat());
        predBurLonText.setText(pBurst.getReadableLong());
        predBurAltText.setText(pBurst.getReadableAlt());
        predBurTimeText.setText(""+pred.getTimeToBurstSec()/60.0 + " minutes");

        Position pLand = pred.getLandingPosition();
        predLndLatText.setText(pLand.getReadableLat());
        predLndLonText.setText(pLand.getReadableLong());
        predLndAltText.setText(pLand.getReadableAlt());
        predLndTimeText.setText(""+pred.getTimeToLandSec()/60.0 + " minutes");

    }

    @Action
    public void toggleOrigPredLine() {
        if(setupShowOPredMI.isSelected()){
            System.out.println("selected");
        }else{
            System.out.println("not selected");
        }
    }


    public void updateCallsign(String cs){
        PacketExchange.getInstance().removeCallsignListener(this);
        PacketExchange.getInstance().addCallsignListener(this, cs);
    }

    public void appendLogFile(String fname, String s){
        File logfile = new File(fname);
        if(!logfile.exists()){
            try{
                logfile.createNewFile();
            }catch(IOException ioe){
                System.out.println(ioe);
            }
        }
        try{
            FileWriter fw = new FileWriter(logfile, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.print(s);
            fw.close();
        }catch(IOException ioe){
            System.out.println(ioe);
        }

    }

    @Action
    public void chooseColor() {
        FalconMainApp.getApplication().show(colorChooser_);
    }
    
    private FalconMainView(SingleFrameApplication app) {
        super(app);
        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        //postInit();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        setupMenu = new javax.swing.JMenu();
        setupFlightCenterMI = new javax.swing.JMenuItem();
        setupPayloadMI = new javax.swing.JMenuItem();
        setupShowOPredMI = new javax.swing.JCheckBoxMenuItem();
        chooseColorMI = new javax.swing.JMenuItem();
        agwpeMenu = new javax.swing.JMenu();
        agwpeConnectMI = new javax.swing.JMenuItem();
        agwpeToggleMonMI = new javax.swing.JMenuItem();
        agwpeReqDiscMI = new javax.swing.JMenuItem();
        agwpeSetHostMI = new javax.swing.JMenuItem();
        agwpeGetVerMI = new javax.swing.JMenuItem();
        devMenu = new javax.swing.JMenu();
        devListenAllCheckBox = new javax.swing.JCheckBoxMenuItem();
        devHasBurstCheckBox = new javax.swing.JCheckBoxMenuItem();
        devAddressToggleMI = new javax.swing.JMenuItem();
        devStatusToggleMI = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        agwpeDebugMI = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        trackingPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        trackPayLatLabel = new javax.swing.JLabel();
        trackPayLatText = new javax.swing.JTextField();
        trackPayTitleLabel = new javax.swing.JLabel();
        trackPayLonLabel = new javax.swing.JLabel();
        trackPayLonText = new javax.swing.JTextField();
        trackPayAltLabel = new javax.swing.JLabel();
        trackPayAltText = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        trackRecLatLabel = new javax.swing.JLabel();
        trackRecLatText = new javax.swing.JTextField();
        trackRecTitleLabel = new javax.swing.JLabel();
        trackRecLonLabel = new javax.swing.JLabel();
        trackRecLonText = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        trackPayAscLabel = new javax.swing.JLabel();
        trackPayAscText = new javax.swing.JTextField();
        trackBurTitleLabel = new javax.swing.JLabel();
        trackBurLatLabel = new javax.swing.JLabel();
        trackBurLatText = new javax.swing.JTextField();
        trackBurstLonLabel = new javax.swing.JLabel();
        trackBurLonText = new javax.swing.JTextField();
        trackBurAltLabel = new javax.swing.JLabel();
        trackBurAltText = new javax.swing.JTextField();
        jSeparator6 = new javax.swing.JSeparator();
        trackBurTimeLabel = new javax.swing.JLabel();
        trackBurTimeText = new javax.swing.JTextField();
        trackPaySpdLabel = new javax.swing.JLabel();
        trackPaySpdText = new javax.swing.JTextField();
        trackPayHdgLabel = new javax.swing.JLabel();
        trackPayHdgText = new javax.swing.JTextField();
        trackPayXmitLabel = new javax.swing.JLabel();
        trackPayXmitText = new javax.swing.JTextField();
        trackSendGEButton = new javax.swing.JButton();
        predictionPanel = new javax.swing.JPanel();
        predBurTitleLabel = new javax.swing.JLabel();
        predBurLatLabel = new javax.swing.JLabel();
        predBurLatText = new javax.swing.JTextField();
        predBurLonLabel = new javax.swing.JLabel();
        predBurLonText = new javax.swing.JTextField();
        predBurAltLabel = new javax.swing.JLabel();
        predBurAltText = new javax.swing.JTextField();
        jSeparator4 = new javax.swing.JSeparator();
        predLndTitleLabel = new javax.swing.JLabel();
        predLndLatLabel = new javax.swing.JLabel();
        predLndLatText = new javax.swing.JTextField();
        predLndLonLabel = new javax.swing.JLabel();
        predLndLonText = new javax.swing.JTextField();
        predLndAltLabel = new javax.swing.JLabel();
        predLndAltText = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        predSendGEButton = new javax.swing.JButton();
        predBurTimeLabel = new javax.swing.JLabel();
        predBurTimeText = new javax.swing.JTextField();
        predLndTimeLabel = new javax.swing.JLabel();
        predLndTimeText = new javax.swing.JTextField();
        predSendOPredGEButton = new javax.swing.JButton();
        azelPanel = new javax.swing.JPanel();
        azelTitleLabel = new javax.swing.JLabel();
        azelAzLabel = new javax.swing.JLabel();
        azelAzText = new javax.swing.JTextField();
        azelElLabel = new javax.swing.JLabel();
        azelElText = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        azelControlCheckbox = new javax.swing.JCheckBox();
        logScrollPane = new javax.swing.JScrollPane();
        logPanel = new javax.swing.JTextArea();
        mapPanel = new javax.swing.JPanel();

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(Falcon.FalconMainApp.class).getContext().getResourceMap(FalconMainView.class);
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(Falcon.FalconMainApp.class).getContext().getActionMap(FalconMainView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        fileMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        menuBar.add(fileMenu);

        setupMenu.setText(resourceMap.getString("setupMenu.text")); // NOI18N
        setupMenu.setName("setupMenu"); // NOI18N

        setupFlightCenterMI.setAction(actionMap.get("setupEnterFCInfo")); // NOI18N
        setupFlightCenterMI.setText(resourceMap.getString("setupFlightCenterMI.text")); // NOI18N
        setupFlightCenterMI.setName("setupFlightCenterMI"); // NOI18N
        setupMenu.add(setupFlightCenterMI);

        setupPayloadMI.setAction(actionMap.get("setupPayload")); // NOI18N
        setupPayloadMI.setText(resourceMap.getString("setupPayloadMI.text")); // NOI18N
        setupPayloadMI.setName("setupPayloadMI"); // NOI18N
        setupMenu.add(setupPayloadMI);

        setupShowOPredMI.setAction(actionMap.get("toggleOrigPredLine")); // NOI18N
        setupShowOPredMI.setSelected(true);
        setupShowOPredMI.setText(resourceMap.getString("setupShowOPredMI.text")); // NOI18N
        setupShowOPredMI.setName("setupShowOPredMI"); // NOI18N
        setupMenu.add(setupShowOPredMI);

        chooseColorMI.setAction(actionMap.get("chooseColor")); // NOI18N
        chooseColorMI.setText(resourceMap.getString("chooseColorMI.text")); // NOI18N
        chooseColorMI.setName("chooseColorMI"); // NOI18N
        setupMenu.add(chooseColorMI);

        devMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        menuBar.add(setupMenu);

        agwpeMenu.setText(resourceMap.getString("agwpeMenu.text")); // NOI18N
        agwpeMenu.setName("agwpeMenu"); // NOI18N

        agwpeConnectMI.setAction(actionMap.get("agwpeConnect")); // NOI18N
        agwpeConnectMI.setText(resourceMap.getString("agwpeConnectMI.text")); // NOI18N
        agwpeConnectMI.setName("agwpeConnectMI"); // NOI18N
        agwpeMenu.add(agwpeConnectMI);

        agwpeToggleMonMI.setAction(actionMap.get("agwpeOnMonitor")); // NOI18N
        agwpeToggleMonMI.setText(resourceMap.getString("agwpeToggleMonMI.text")); // NOI18N
        agwpeToggleMonMI.setName("agwpeToggleMonMI"); // NOI18N
        agwpeMenu.add(agwpeToggleMonMI);

        agwpeReqDiscMI.setAction(actionMap.get("agwpeReqDisc")); // NOI18N
        agwpeReqDiscMI.setText(resourceMap.getString("agwpeReqDiscMI.text")); // NOI18N
        agwpeReqDiscMI.setName("agwpeReqDiscMI"); // NOI18N
        agwpeMenu.add(agwpeReqDiscMI);

        agwpeSetHostMI.setAction(actionMap.get("agwpeSetHost")); // NOI18N
        agwpeSetHostMI.setText(resourceMap.getString("agwpeSetHostMI.text")); // NOI18N
        agwpeSetHostMI.setName("agwpeSetHostMI"); // NOI18N
        agwpeMenu.add(agwpeSetHostMI);

        agwpeGetVerMI.setAction(actionMap.get("agwpeGetVer")); // NOI18N
        agwpeGetVerMI.setText(resourceMap.getString("agwpeGetVerMI.text")); // NOI18N
        agwpeGetVerMI.setName("agwpeGetVerMI"); // NOI18N
        agwpeMenu.add(agwpeGetVerMI);

        menuBar.add(agwpeMenu);

        devMenu.setText(resourceMap.getString("devMenu.text")); // NOI18N
        devMenu.setName("devMenu"); // NOI18N

        devListenAllCheckBox.setAction(actionMap.get("devListenAllToggle")); // NOI18N
        devListenAllCheckBox.setText(resourceMap.getString("devListenAllCheckBox.text")); // NOI18N
        devListenAllCheckBox.setName("devListenAllCheckBox"); // NOI18N
        devMenu.add(devListenAllCheckBox);

        devHasBurstCheckBox.setText(resourceMap.getString("devHasBurstCheckBox.text")); // NOI18N
        devHasBurstCheckBox.setName("devHasBurstCheckBox"); // NOI18N
        devMenu.add(devHasBurstCheckBox);

        devAddressToggleMI.setAction(actionMap.get("devToggleAddressBar")); // NOI18N
        devAddressToggleMI.setIcon(resourceMap.getIcon("devAddressToggleMI.icon")); // NOI18N
        devAddressToggleMI.setText(resourceMap.getString("devAddressToggleMI.text")); // NOI18N
        devAddressToggleMI.setName("devAddressToggleMI"); // NOI18N
        devMenu.add(devAddressToggleMI);

        devStatusToggleMI.setAction(actionMap.get("devToggleStatusBar")); // NOI18N
        devStatusToggleMI.setText(resourceMap.getString("devStatusToggleMI.text")); // NOI18N
        devStatusToggleMI.setName("devStatusToggleMI"); // NOI18N
        devMenu.add(devStatusToggleMI);

        jMenuItem5.setAction(actionMap.get("devTestStr")); // NOI18N
        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        devMenu.add(jMenuItem5);

        jMenuItem6.setAction(actionMap.get("devTestPkt")); // NOI18N
        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        devMenu.add(jMenuItem6);

        agwpeDebugMI.setAction(actionMap.get("agwpeDebug")); // NOI18N
        agwpeDebugMI.setText(resourceMap.getString("agwpeDebugMI.text")); // NOI18N
        agwpeDebugMI.setName("agwpeDebugMI"); // NOI18N
        devMenu.add(agwpeDebugMI);

        devMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        menuBar.add(devMenu);
        devMenu.setVisible(false);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        helpMenu.add(jMenuItem3);

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        helpMenu.getPopupMenu().setLightWeightPopupEnabled(false);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(783, 30));

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jSeparator1.setName("jSeparator1"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 661, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11))
        );

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        trackingPanel.setName("trackingPanel"); // NOI18N

        jButton1.setAction(actionMap.get("testButton")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        trackPayLatLabel.setText(resourceMap.getString("trackPayLatLabel.text")); // NOI18N
        trackPayLatLabel.setName("trackPayLatLabel"); // NOI18N

        trackPayLatText.setEditable(false);
        trackPayLatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayLatText.setText(resourceMap.getString("trackPayLatText.text")); // NOI18N
        trackPayLatText.setName("trackPayLatText"); // NOI18N

        trackPayTitleLabel.setFont(resourceMap.getFont("trackPayTitleLabel.font")); // NOI18N
        trackPayTitleLabel.setText(resourceMap.getString("trackPayTitleLabel.text")); // NOI18N
        trackPayTitleLabel.setName("trackPayTitleLabel"); // NOI18N

        trackPayLonLabel.setText(resourceMap.getString("trackPayLonLabel.text")); // NOI18N
        trackPayLonLabel.setName("trackPayLonLabel"); // NOI18N

        trackPayLonText.setEditable(false);
        trackPayLonText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayLonText.setText(resourceMap.getString("trackPayLonText.text")); // NOI18N
        trackPayLonText.setName("trackPayLonText"); // NOI18N

        trackPayAltLabel.setText(resourceMap.getString("trackPayAltLabel.text")); // NOI18N
        trackPayAltLabel.setName("trackPayAltLabel"); // NOI18N

        trackPayAltText.setEditable(false);
        trackPayAltText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayAltText.setText(resourceMap.getString("trackPayAltText.text")); // NOI18N
        trackPayAltText.setName("trackPayAltText"); // NOI18N

        jSeparator2.setName("jSeparator2"); // NOI18N

        trackRecLatLabel.setText(resourceMap.getString("trackRecLatLabel.text")); // NOI18N
        trackRecLatLabel.setName("trackRecLatLabel"); // NOI18N

        trackRecLatText.setEditable(false);
        trackRecLatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackRecLatText.setName("trackRecLatText"); // NOI18N

        trackRecTitleLabel.setFont(resourceMap.getFont("trackRecTitleLabel.font")); // NOI18N
        trackRecTitleLabel.setText(resourceMap.getString("trackRecTitleLabel.text")); // NOI18N
        trackRecTitleLabel.setName("trackRecTitleLabel"); // NOI18N

        trackRecLonLabel.setText(resourceMap.getString("trackRecLonLabel.text")); // NOI18N
        trackRecLonLabel.setName("trackRecLonLabel"); // NOI18N

        trackRecLonText.setEditable(false);
        trackRecLonText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackRecLonText.setName("trackRecLonText"); // NOI18N

        jSeparator3.setName("jSeparator3"); // NOI18N

        trackPayAscLabel.setText(resourceMap.getString("trackPayAscLabel.text")); // NOI18N
        trackPayAscLabel.setName("trackPayAscLabel"); // NOI18N

        trackPayAscText.setEditable(false);
        trackPayAscText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayAscText.setName("trackPayAscText"); // NOI18N

        trackBurTitleLabel.setFont(resourceMap.getFont("trackBurTitleLabel.font")); // NOI18N
        trackBurTitleLabel.setText(resourceMap.getString("trackBurTitleLabel.text")); // NOI18N
        trackBurTitleLabel.setName("trackBurTitleLabel"); // NOI18N

        trackBurLatLabel.setText(resourceMap.getString("trackBurLatLabel.text")); // NOI18N
        trackBurLatLabel.setName("trackBurLatLabel"); // NOI18N

        trackBurLatText.setEditable(false);
        trackBurLatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackBurLatText.setName("trackBurLatText"); // NOI18N

        trackBurstLonLabel.setText(resourceMap.getString("trackBurstLonLabel.text")); // NOI18N
        trackBurstLonLabel.setName("trackBurstLonLabel"); // NOI18N

        trackBurLonText.setEditable(false);
        trackBurLonText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackBurLonText.setName("trackBurLonText"); // NOI18N

        trackBurAltLabel.setText(resourceMap.getString("trackBurAltLabel.text")); // NOI18N
        trackBurAltLabel.setName("trackBurAltLabel"); // NOI18N

        trackBurAltText.setEditable(false);
        trackBurAltText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackBurAltText.setName("trackBurAltText"); // NOI18N

        jSeparator6.setName("jSeparator6"); // NOI18N

        trackBurTimeLabel.setText(resourceMap.getString("trackBurTimeLabel.text")); // NOI18N
        trackBurTimeLabel.setName("trackBurTimeLabel"); // NOI18N

        trackBurTimeText.setEditable(false);
        trackBurTimeText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackBurTimeText.setName("trackBurTimeText"); // NOI18N

        trackPaySpdLabel.setText(resourceMap.getString("trackPaySpdLabel.text")); // NOI18N
        trackPaySpdLabel.setName("trackPaySpdLabel"); // NOI18N

        trackPaySpdText.setEditable(false);
        trackPaySpdText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPaySpdText.setName("trackPaySpdText"); // NOI18N

        trackPayHdgLabel.setText(resourceMap.getString("trackPayHdgLabel.text")); // NOI18N
        trackPayHdgLabel.setName("trackPayHdgLabel"); // NOI18N

        trackPayHdgText.setEditable(false);
        trackPayHdgText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayHdgText.setName("trackPayHdgText"); // NOI18N

        trackPayXmitLabel.setText(resourceMap.getString("trackPayXmitLabel.text")); // NOI18N
        trackPayXmitLabel.setName("trackPayXmitLabel"); // NOI18N

        trackPayXmitText.setEditable(false);
        trackPayXmitText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackPayXmitText.setName("trackPayXmitText"); // NOI18N

        trackSendGEButton.setAction(actionMap.get("sendTrackingGE")); // NOI18N
        trackSendGEButton.setText(resourceMap.getString("trackSendGEButton.text")); // NOI18N
        trackSendGEButton.setName("trackSendGEButton"); // NOI18N

        javax.swing.GroupLayout trackingPanelLayout = new javax.swing.GroupLayout(trackingPanel);
        trackingPanel.setLayout(trackingPanelLayout);
        trackingPanelLayout.setHorizontalGroup(
            trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayLatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayLatText, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayLonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayLonText, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayAltLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayAltText, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayAscLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayAscText, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPaySpdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPaySpdText, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayHdgLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayHdgText, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackPayXmitLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackPayXmitText, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(trackPayTitleLabel)
                    .addComponent(trackBurTitleLabel)
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackBurLatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackBurLatText, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackBurstLonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackBurLonText, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackBurAltLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackBurAltText, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackBurTimeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackBurTimeText, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                    .addComponent(jSeparator6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(trackRecTitleLabel)
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackRecLatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackRecLatText, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addGroup(trackingPanelLayout.createSequentialGroup()
                        .addComponent(trackRecLonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(trackRecLonText, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addComponent(trackSendGEButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        trackingPanelLayout.setVerticalGroup(
            trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(trackingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(trackPayTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayLatLabel)
                    .addComponent(trackPayLatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayLonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(trackPayLonLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayAltLabel)
                    .addComponent(trackPayAltText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayAscLabel)
                    .addComponent(trackPayAscText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPaySpdLabel)
                    .addComponent(trackPaySpdText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayHdgLabel)
                    .addComponent(trackPayHdgText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackPayXmitLabel)
                    .addComponent(trackPayXmitText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trackBurTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackBurLatLabel)
                    .addComponent(trackBurLatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackBurstLonLabel)
                    .addComponent(trackBurLonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackBurAltLabel)
                    .addComponent(trackBurAltText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackBurTimeLabel)
                    .addComponent(trackBurTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trackRecTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackRecLatLabel)
                    .addComponent(trackRecLatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(trackingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(trackRecLonLabel)
                    .addComponent(trackRecLonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trackSendGEButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("trackingPanel.TabConstraints.tabTitle"), trackingPanel); // NOI18N

        predictionPanel.setName("predictionPanel"); // NOI18N

        predBurTitleLabel.setFont(resourceMap.getFont("predBurTitleLabel.font")); // NOI18N
        predBurTitleLabel.setText(resourceMap.getString("predBurTitleLabel.text")); // NOI18N
        predBurTitleLabel.setName("predBurTitleLabel"); // NOI18N

        predBurLatLabel.setText(resourceMap.getString("predBurLatLabel.text")); // NOI18N
        predBurLatLabel.setName("predBurLatLabel"); // NOI18N

        predBurLatText.setEditable(false);
        predBurLatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predBurLatText.setName("predBurLatText"); // NOI18N

        predBurLonLabel.setText(resourceMap.getString("predBurLonLabel.text")); // NOI18N
        predBurLonLabel.setName("predBurLonLabel"); // NOI18N

        predBurLonText.setEditable(false);
        predBurLonText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predBurLonText.setName("predBurLonText"); // NOI18N

        predBurAltLabel.setText(resourceMap.getString("predBurAltLabel.text")); // NOI18N
        predBurAltLabel.setName("predBurAltLabel"); // NOI18N

        predBurAltText.setEditable(false);
        predBurAltText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predBurAltText.setName("predBurAltText"); // NOI18N

        jSeparator4.setName("jSeparator4"); // NOI18N

        predLndTitleLabel.setFont(resourceMap.getFont("predLndTitleLabel.font")); // NOI18N
        predLndTitleLabel.setText(resourceMap.getString("predLndTitleLabel.text")); // NOI18N
        predLndTitleLabel.setName("predLndTitleLabel"); // NOI18N

        predLndLatLabel.setText(resourceMap.getString("predLndLatLabel.text")); // NOI18N
        predLndLatLabel.setName("predLndLatLabel"); // NOI18N

        predLndLatText.setEditable(false);
        predLndLatText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predLndLatText.setName("predLndLatText"); // NOI18N

        predLndLonLabel.setText(resourceMap.getString("predLndLonLabel.text")); // NOI18N
        predLndLonLabel.setName("predLndLonLabel"); // NOI18N

        predLndLonText.setEditable(false);
        predLndLonText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predLndLonText.setName("predLndLonText"); // NOI18N

        predLndAltLabel.setText(resourceMap.getString("predLndAltLabel.text")); // NOI18N
        predLndAltLabel.setName("predLndAltLabel"); // NOI18N

        predLndAltText.setEditable(false);
        predLndAltText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predLndAltText.setName("predLndAltText"); // NOI18N

        jSeparator5.setName("jSeparator5"); // NOI18N

        predSendGEButton.setAction(actionMap.get("sendCurrPredGE")); // NOI18N
        predSendGEButton.setText(resourceMap.getString("predSendGEButton.text")); // NOI18N
        predSendGEButton.setName("predSendGEButton"); // NOI18N

        predBurTimeLabel.setText(resourceMap.getString("predBurTimeLabel.text")); // NOI18N
        predBurTimeLabel.setName("predBurTimeLabel"); // NOI18N

        predBurTimeText.setEditable(false);
        predBurTimeText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predBurTimeText.setName("predBurTimeText"); // NOI18N

        predLndTimeLabel.setText(resourceMap.getString("predLndTimeLabel.text")); // NOI18N
        predLndTimeLabel.setName("predLndTimeLabel"); // NOI18N

        predLndTimeText.setEditable(false);
        predLndTimeText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        predLndTimeText.setName("predLndTimeText"); // NOI18N

        predSendOPredGEButton.setAction(actionMap.get("sendOPredGE")); // NOI18N
        predSendOPredGEButton.setText(resourceMap.getString("predSendOPredGEButton.text")); // NOI18N
        predSendOPredGEButton.setName("predSendOPredGEButton"); // NOI18N

        javax.swing.GroupLayout predictionPanelLayout = new javax.swing.GroupLayout(predictionPanel);
        predictionPanel.setLayout(predictionPanelLayout);
        predictionPanelLayout.setHorizontalGroup(
            predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(predictionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(predBurTitleLabel)
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predBurLatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predBurLatText, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predBurLonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predBurLonText, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predBurAltLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predBurAltText, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, predictionPanelLayout.createSequentialGroup()
                        .addComponent(predBurTimeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predBurTimeText, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(predLndTitleLabel)
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predLndLatLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predLndLatText, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE))
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predLndLonLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predLndLonText, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predLndAltLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predLndAltText, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                    .addGroup(predictionPanelLayout.createSequentialGroup()
                        .addComponent(predLndTimeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(predLndTimeText, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addComponent(predSendGEButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(predSendOPredGEButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        predictionPanelLayout.setVerticalGroup(
            predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(predictionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(predBurTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predBurLatLabel)
                    .addComponent(predBurLatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predBurLonLabel)
                    .addComponent(predBurLonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predBurAltLabel)
                    .addComponent(predBurAltText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predBurTimeLabel)
                    .addComponent(predBurTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(predLndTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predLndLatLabel)
                    .addComponent(predLndLatText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predLndLonLabel)
                    .addComponent(predLndLonText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predLndAltLabel)
                    .addComponent(predLndAltText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(predictionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(predLndTimeLabel)
                    .addComponent(predLndTimeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(predSendGEButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(predSendOPredGEButton)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("predictionPanel.TabConstraints.tabTitle"), predictionPanel); // NOI18N

        azelPanel.setName("azelPanel"); // NOI18N

        azelTitleLabel.setFont(resourceMap.getFont("azelTitleLabel.font")); // NOI18N
        azelTitleLabel.setText(resourceMap.getString("azelTitleLabel.text")); // NOI18N
        azelTitleLabel.setName("azelTitleLabel"); // NOI18N

        azelAzLabel.setText(resourceMap.getString("azelAzLabel.text")); // NOI18N
        azelAzLabel.setName("azelAzLabel"); // NOI18N

        azelAzText.setEditable(false);
        azelAzText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        azelAzText.setName("azelAzText"); // NOI18N

        azelElLabel.setText(resourceMap.getString("azelElLabel.text")); // NOI18N
        azelElLabel.setName("azelElLabel"); // NOI18N

        azelElText.setEditable(false);
        azelElText.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        azelElText.setName("azelElText"); // NOI18N

        jSeparator7.setName("jSeparator7"); // NOI18N

        azelControlCheckbox.setText(resourceMap.getString("azelControlCheckbox.text")); // NOI18N
        azelControlCheckbox.setToolTipText(resourceMap.getString("azelControlCheckbox.toolTipText")); // NOI18N
        azelControlCheckbox.setName("azelControlCheckbox"); // NOI18N
        azelControlCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                azelControlCheckboxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout azelPanelLayout = new javax.swing.GroupLayout(azelPanel);
        azelPanel.setLayout(azelPanelLayout);
        azelPanelLayout.setHorizontalGroup(
            azelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(azelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(azelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator7, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                    .addGroup(azelPanelLayout.createSequentialGroup()
                        .addComponent(azelAzLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(azelAzText, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                    .addGroup(azelPanelLayout.createSequentialGroup()
                        .addComponent(azelElLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(azelElText, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                    .addComponent(azelTitleLabel)
                    .addComponent(azelControlCheckbox, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        azelPanelLayout.setVerticalGroup(
            azelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(azelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(azelTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(azelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(azelAzLabel)
                    .addComponent(azelAzText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(azelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(azelElLabel)
                    .addComponent(azelElText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(azelControlCheckbox)
                .addContainerGap(413, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(resourceMap.getString("azelPanel.TabConstraints.tabTitle"), azelPanel); // NOI18N

        logScrollPane.setName("logScrollPane"); // NOI18N

        logPanel.setColumns(20);
        logPanel.setRows(5);
        logPanel.setBorder(null);
        logPanel.setName("logPanel"); // NOI18N
        logScrollPane.setViewportView(logPanel);

        jTabbedPane1.addTab(resourceMap.getString("logScrollPane.TabConstraints.tabTitle"), logScrollPane); // NOI18N

        jSplitPane1.setLeftComponent(jTabbedPane1);

        mapPanel.setName("mapPanel"); // NOI18N
        mapPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mapPanelResized(evt);
            }
        });
        mapPanel.setLayout(new java.awt.BorderLayout());

        moz = new MozillaPanel(MozillaPanel.VisibilityMode.FORCED_HIDDEN, MozillaPanel.VisibilityMode.FORCED_HIDDEN); //This will force to hide the tool bar and status bar
        //moz.load(getWebPath()); //$NON-NLS-1$
        moz.setMinimumSize(new Dimension(500,200));
        mapPanel.add(moz);

        jSplitPane1.setRightComponent(mapPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void mapPanelResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_mapPanelResized
        System.out.println("MapPanel resized");
        moz.reload();
        //TODO: figure out how to not call reload too many times
    }//GEN-LAST:event_mapPanelResized

    private void azelControlCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_azelControlCheckboxItemStateChanged
        azelControlEnabled = !azelControlEnabled;
    }//GEN-LAST:event_azelControlCheckboxItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem agwpeConnectMI;
    private javax.swing.JMenuItem agwpeDebugMI;
    private javax.swing.JMenuItem agwpeGetVerMI;
    private javax.swing.JMenu agwpeMenu;
    private javax.swing.JMenuItem agwpeReqDiscMI;
    private javax.swing.JMenuItem agwpeSetHostMI;
    private javax.swing.JMenuItem agwpeToggleMonMI;
    private javax.swing.JLabel azelAzLabel;
    private javax.swing.JTextField azelAzText;
    private javax.swing.JCheckBox azelControlCheckbox;
    private javax.swing.JLabel azelElLabel;
    private javax.swing.JTextField azelElText;
    private javax.swing.JPanel azelPanel;
    private javax.swing.JLabel azelTitleLabel;
    private javax.swing.JMenuItem chooseColorMI;
    private javax.swing.JMenuItem devAddressToggleMI;
    private javax.swing.JCheckBoxMenuItem devHasBurstCheckBox;
    private javax.swing.JCheckBoxMenuItem devListenAllCheckBox;
    private javax.swing.JMenu devMenu;
    private javax.swing.JMenuItem devStatusToggleMI;
    private javax.swing.JButton jButton1;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea logPanel;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel mapPanel;
    private MozillaPanel moz;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel predBurAltLabel;
    private javax.swing.JTextField predBurAltText;
    private javax.swing.JLabel predBurLatLabel;
    private javax.swing.JTextField predBurLatText;
    private javax.swing.JLabel predBurLonLabel;
    private javax.swing.JTextField predBurLonText;
    private javax.swing.JLabel predBurTimeLabel;
    private javax.swing.JTextField predBurTimeText;
    private javax.swing.JLabel predBurTitleLabel;
    private javax.swing.JLabel predLndAltLabel;
    private javax.swing.JTextField predLndAltText;
    private javax.swing.JLabel predLndLatLabel;
    private javax.swing.JTextField predLndLatText;
    private javax.swing.JLabel predLndLonLabel;
    private javax.swing.JTextField predLndLonText;
    private javax.swing.JLabel predLndTimeLabel;
    private javax.swing.JTextField predLndTimeText;
    private javax.swing.JLabel predLndTitleLabel;
    private javax.swing.JButton predSendGEButton;
    private javax.swing.JButton predSendOPredGEButton;
    private javax.swing.JPanel predictionPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem setupFlightCenterMI;
    private javax.swing.JMenu setupMenu;
    private javax.swing.JMenuItem setupPayloadMI;
    private javax.swing.JCheckBoxMenuItem setupShowOPredMI;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel trackBurAltLabel;
    private javax.swing.JTextField trackBurAltText;
    private javax.swing.JLabel trackBurLatLabel;
    private javax.swing.JTextField trackBurLatText;
    private javax.swing.JTextField trackBurLonText;
    private javax.swing.JLabel trackBurTimeLabel;
    private javax.swing.JTextField trackBurTimeText;
    private javax.swing.JLabel trackBurTitleLabel;
    private javax.swing.JLabel trackBurstLonLabel;
    private javax.swing.JLabel trackPayAltLabel;
    private javax.swing.JTextField trackPayAltText;
    private javax.swing.JLabel trackPayAscLabel;
    private javax.swing.JTextField trackPayAscText;
    private javax.swing.JLabel trackPayHdgLabel;
    private javax.swing.JTextField trackPayHdgText;
    private javax.swing.JLabel trackPayLatLabel;
    private javax.swing.JTextField trackPayLatText;
    private javax.swing.JLabel trackPayLonLabel;
    private javax.swing.JTextField trackPayLonText;
    private javax.swing.JLabel trackPaySpdLabel;
    private javax.swing.JTextField trackPaySpdText;
    private javax.swing.JLabel trackPayTitleLabel;
    private javax.swing.JLabel trackPayXmitLabel;
    private javax.swing.JTextField trackPayXmitText;
    private javax.swing.JLabel trackRecLatLabel;
    private javax.swing.JTextField trackRecLatText;
    private javax.swing.JLabel trackRecLonLabel;
    private javax.swing.JTextField trackRecLonText;
    private javax.swing.JLabel trackRecTitleLabel;
    private javax.swing.JButton trackSendGEButton;
    private javax.swing.JPanel trackingPanel;
    // End of variables declaration//GEN-END:variables




}
