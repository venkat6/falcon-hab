package falcon.components.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.Timer;

/**
 * An indicator capable of displaying several states including
 * nominal, marginal, abnormal and disabled states.  Also supports basic
 * alert functionality.  The displayed text can also change to reflect the
 * current state of the indicator.
 * 
 * @author Ethan Harstad
 *
 */
public class StatusIndicator extends JButton {
	
	private String mNominalLabel = "";
	private String mMarginalLabel = "";
	private String mAbnormalLabel = "";
	private int mState = -1;
	private boolean mAlert = false;
	private boolean mAlertMarginal = false;
	private boolean mAlertAbnormal = true;
	private static boolean mFlash = false;
	private static Timer timer;
	
	// Status Constants
	public static final int STATUS_NOMINAL = 0;		// Normal status, Green
	public static final int STATUS_MARGINAL = 1;	// Marginal status, Yellow
	public static final int STATUS_ABNORMAL = 2;	// Abnormal status, Red
	public static final int STATUS_DISABLED = -1;	// Disabled
	
	/**
	 * Default constructor.  Creates an indicator with no display text.
	 * The new indicator is in the disabled state upon creation.
	 * The indicator will flash on Abnormal status only.
	 */
	public StatusIndicator() {
		super();
		setState(STATUS_DISABLED);	// default state
		// create a timer to use to handle redrawing for flashing alarm
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mFlash = !mFlash;
				repaint();
			}});
		timer.start();
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearAlert();
				repaint();
			}
		});
	}

	/**
	 * Creates an indicator with the same label for all states.
	 * The new indicator is in the disabled state upon creation.
	 * The indicator will flash on Abnormal status only.
	 * @param label
	 */
	public StatusIndicator(String label) {
		this();
		mNominalLabel = label;
		mMarginalLabel = label;
		mAbnormalLabel = label;
	}
	
	/**
	 * Creates an indicator with the given labels for each state.
	 * Also allows the flash functionality to be set for marginal and abnormal states.
	 * The new indicator is in the disabled state upon creation.
	 * @param NominalLabel
	 * @param MarginalLabel
	 * @param AbnormalLabel
	 * @param AlertMarginal
	 * @param AlertAbnormal
	 */
	public StatusIndicator(String NominalLabel, String MarginalLabel, String AbnormalLabel, boolean AlertMarginal, boolean AlertAbnormal) {
		this();
		mNominalLabel = NominalLabel;
		mMarginalLabel = MarginalLabel;
		mAbnormalLabel = AbnormalLabel;
		mAlertMarginal = AlertMarginal;
		mAlertAbnormal = AlertAbnormal;
	}
	
	/**
	 * Sets the current state of the indicator.
	 * The given state should be one of:
	 * 		StatusIndicator.STATUS_NOMINAL
	 * 		StatusIndicator.STATUS_MARGINAL
	 * 		StatusIndicator.STATUS_ABNORMAL
	 * 		StatusIndicator.STATUS_DISABLED
	 * @param state
	 */
	public void setState(int state) {
		if(mState == state) return;
		mState = state;
		
		if(mState == STATUS_NOMINAL) {
			setText(mNominalLabel);
			setEnabled(true);
		} else if(mState == STATUS_MARGINAL) {
			setText(mMarginalLabel);
			setEnabled(true);
			if(mAlertMarginal) setAlert();
		} else if(mState == STATUS_ABNORMAL) {
			setText(mAbnormalLabel);
			setEnabled(true);
			if(mAlertAbnormal) setAlert();
		} else {
			setText(mAbnormalLabel);
			setEnabled(false);
		}
	}
	
	/**
	 * Puts the indicator into an alerted state (flashing).
	 */
	public void setAlert() {
		Toolkit.getDefaultToolkit().beep();
		mAlert = true;
	}
	
	/**
	 * Clears the indicators alerted status.
	 */
	public void clearAlert() {
		mAlert = false;
	}
	
	/**
	 * Repaints the Status Indicator.  Should not be called directly in order
	 * to avoid overloading the redrawing engine.
	 */
	@Override
	public void paintComponent(Graphics g) {
		if(mState == STATUS_NOMINAL) {
			setBackground(Color.GREEN);
		} else if(mState == STATUS_MARGINAL) {
			if(mAlert && mAlertMarginal) {
				if(mFlash) {
					setBackground(new Color(128,128,0));
				} else {
					setBackground(Color.YELLOW);
				}
			} else setBackground(Color.YELLOW);
		} else if(mState == STATUS_ABNORMAL) {
			if(mAlert && mAlertAbnormal) {
				if(mFlash) {
					setBackground(new Color(128,0,0));
				} else {
					setBackground(Color.RED);
				}
			} else setBackground(Color.RED);
		}
		super.paintComponent(g);
	}

}
