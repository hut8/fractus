/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package us.fract.strategy;

/**
 *
 * @author bowenl2
 */
public interface PacketStrategy {
    public void dispatch(byte[] contents);
}
