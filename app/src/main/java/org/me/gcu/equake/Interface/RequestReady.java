package org.me.gcu.equake.Interface;

import org.me.gcu.equake.Model.EQUAKE;

import java.util.List;

/**
 * Developed by: Michael A. F.
 */
public interface RequestReady {
    void onComplete(List<EQUAKE> loaded);
}
