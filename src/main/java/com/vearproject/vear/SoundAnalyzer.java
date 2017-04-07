package com.vearproject.vear;

import com.musicg.fingerprint.FingerprintManager;

/**
 * Created by furkanmumcu on 17/01/2017.
 */
public class SoundAnalyzer {

    private FingerprintManager manager;

    public SoundAnalyzer() {
        this.manager = new FingerprintManager();
    }

    public byte[] analyze(Sound sound) {
        return manager.extractFingerprint(sound.getWave());
    }
}
