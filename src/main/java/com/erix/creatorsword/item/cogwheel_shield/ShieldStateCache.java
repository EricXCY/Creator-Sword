package com.erix.creatorsword.item.cogwheel_shield;

public class ShieldStateCache {
    public float speed = 0f;
    public boolean isCharging = false;
    public boolean isDecaying = false;
    public long chargeStart = 0L;
    public long lastDecay = 0L;
    public long lastSync = 0L;
    public float rotationAngle = 0f;

    public void reset() {
        speed = 0f;
        isCharging = false;
        isDecaying = false;
        chargeStart = 0L;
        lastDecay = 0L;
        lastSync = 0L;
        rotationAngle = 0f;
    }
}