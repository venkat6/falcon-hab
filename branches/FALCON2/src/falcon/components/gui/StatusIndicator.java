package falcon.components.gui;

//TODO full test and document

import java.awt.Color;
import java.awt.Graphics;
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
public class StatusIndicator extends JButton implements ActionListener {
	
	private String mNominalLabel = "";
	private String mMarginalLabel = "";
	private String mAbnormalLabel = "";
	private int mState = -1;
	private boolean mAlert = false;
	private boolean mAlertMarginal = false;
	private boolean mAlertAbnormal = true;
	private static boolean mFlash;
	private static Timer timer;
	
	public static final int STATUS_NOMINAL = 0;
	public static final int STATUS_MARGINAL = 1;
	public static final int STATUS_ABNORMAL = 2;
	public static final int STATUS_DISABLED = -1;
	
	public StatusIndicator() {
		super();
		setState(STATUS_DISABLED);
		timer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mFlash = !mFlash;
				repaint();
			}});
		timer.start();
		addActionListener(this);
	}

	public StatusIndicator(String label) {
		this();
		mNominalLabel = label;
		mMarginalLabel = label;
		mAbnormalLabel = label;
	}
	
	public StatusIndicator(String NominalLabel, String MarginalLabel, String AbnormalLabel, boolean AlertMarginal, boolean AlertAbnormal) {
		this();
		mNominalLabel = NominalLabel;
		mMarginalLabel = MarginalLabel;
		mAbnormalLabel = AbnormalLabel;
		mAlertMarginal = AlertMarginal;
		mAlertAbnormal = AlertAbnormal;
	}
	
	public void setState(int state) {
		if(mState == state) return;
		mState = state;
		
		if(mState == STATUS_NOMINAL) {
			setText(mNominalLabel);
			setEnabled(true);
		} else if(mState == STATUS_MARGINAL) {
			setText(mMarginalLabel);
			setEnabled(true);
		} else if(mState == STATUS_ABNORMAL) {
			setText(mAbnormalLabel);
			setEnabled(true);
		} else {
			setText(mAbnormalLabel);
			setEnabled(false);
		}
		
		if((state == STATUS_MARGINAL && mAlertMarginal) ||
		   (state == STATUS_ABNORMAL && mAlertAbnormal)) {
			setAlert();
		}
	}
	
	public void setAlert() {
		mAlert = true;
	}
	
	public void clearAlert() {
		mAlert = false;
	}
	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		clearAlert();
		repaint();
	}

}
