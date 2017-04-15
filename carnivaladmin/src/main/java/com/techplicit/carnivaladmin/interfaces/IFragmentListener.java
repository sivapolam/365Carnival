package com.techplicit.carnivaladmin.interfaces;

/**
 * @Interface Fragment Listener
 */

public interface IFragmentListener {
    /**
     * On Selection of Band in dialog, update the selected band text
     *
     * @param message - Selected band name
     */
    void updateSelectedBand(String message);
}
