/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Falcon;

/**
 *
 * @author Joe
 */
public interface PacketListener {

    public void packetReceivedEvent(String hdrStr, String dataStr);
    
}
