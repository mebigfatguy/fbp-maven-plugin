package com.mebigfatguy.fbp;

import java.util.Comparator;

import org.apache.maven.model.Dependency;

public class DependencyComparator implements Comparator<Dependency> {

    @Override
    public int compare(Dependency d1, Dependency d2) {

        int cmp = d1.getGroupId().compareTo(d2.getGroupId());
        if (cmp != 0) {
            return cmp;
        }

        cmp = d1.getArtifactId().compareTo(d2.getArtifactId());
        if (cmp != 0) {
            return cmp;
        }

        return d1.getVersion().compareTo(d2.getVersion());
    }

}
