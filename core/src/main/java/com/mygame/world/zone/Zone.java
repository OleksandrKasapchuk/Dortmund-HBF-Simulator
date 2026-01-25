package com.mygame.world.zone;

import com.badlogic.gdx.math.Rectangle;

public abstract class Zone {

    protected final String id;
    protected final Rectangle area;
    protected boolean enabled = true;

    protected Zone(String id, Rectangle area) {
        this.id = id;
        this.area = area;
    }

    public String getId(){ return id; }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public Rectangle getArea() {
        return area;
    }
    public abstract void onInteract();
}
