package com.simpledeveloper.averse.helpers;

public class PoetsSyncEvent {

    private boolean isSynced;

    public PoetsSyncEvent(boolean isSynced) {
        this.isSynced = isSynced;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
