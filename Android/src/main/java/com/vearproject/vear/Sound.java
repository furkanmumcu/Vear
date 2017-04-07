package com.vearproject.vear;
import com.musicg.wave.Wave;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by furkanmumcu on 17/01/2017.
 */
public class Sound {

    private byte[] source;
    private int direction;
    private boolean isDirectional;
    private Wave wave;
    private double duration; //?
    static private AtomicInteger nextId = new AtomicInteger();
    private final int id;

    public Sound (String path) {
        this.id = nextId.incrementAndGet();
        this.wave = new Wave(path);
    }

    public int getId() {
        return id;
    }

    public byte[] getSource(){
        return wave.getBytes();
    }

    public Wave getWave() {
        return wave;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isDirectional() {
        return isDirectional;
    }

    public void setDirectional(boolean directional) {
        isDirectional = directional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sound)) return false;

        Sound sound = (Sound) o;

        if (direction != sound.direction) return false;
        if (isDirectional != sound.isDirectional) return false;
        if (Double.compare(sound.duration, duration) != 0) return false;
        if (id != sound.id) return false;
        if (!Arrays.equals(source, sound.source)) return false;
        return wave != null ? wave.equals(sound.wave) : sound.wave == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = Arrays.hashCode(source);
        result = 31 * result + direction;
        result = 31 * result + (isDirectional ? 1 : 0);
        result = 31 * result + (wave != null ? wave.hashCode() : 0);
        temp = Double.doubleToLongBits(duration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + id;
        return result;
    }
}
