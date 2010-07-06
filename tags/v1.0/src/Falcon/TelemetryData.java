/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

/**
 *
 * @author Joe
 */
public class TelemetryData {

    private int sequenceNumber_;
    private int[] analogData_; //has to be 5 elements in length
    private boolean[] digitalData_; //has to be 8 elements in length

    public TelemetryData(int seqNum, int[] aD, boolean[] dD){
        sequenceNumber_ = seqNum;
        if(aD.length==5){
            analogData_ = new int[]{aD[0],aD[1],aD[2],aD[3],aD[4]};
        }
        else{
            analogData_ = new int[5];
        }
        if(dD.length==8){
            digitalData_ = new boolean[]{dD[0],dD[1],dD[2],dD[3],dD[4],dD[5],dD[6],dD[7]};
        }else{
            digitalData_ = new boolean[8];
        }
    }

    public int getSequenceNumber(){
        return sequenceNumber_;
    }

    public int[] getAnalogData(){
        return analogData_;
    }

    public boolean[] getDigitalData(){
        return digitalData_;
    }

}