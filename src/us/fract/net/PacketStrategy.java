/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.net;

/**
 *
 * @author bowenl2
 */
public interface PacketStrategy {
    public void dispatch(byte[] contents);
}
