package com.vearproject.vear;

import com.musicg.fingerprint.FingerprintSimilarity;

/**
 * Created by furkanmumcu on 17/01/2017.
 */
public class SoundComparator {

    private FingerprintSimilarity fs;

    public SoundComparator() {

    }

    public float compare (Sound s1, Sound s2){
        fs = s1.getWave().getFingerprintSimilarity(s2.getWave());
        return fs.getSimilarity();
    }
}
