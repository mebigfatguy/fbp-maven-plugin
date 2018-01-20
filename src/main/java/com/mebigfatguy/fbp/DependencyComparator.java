/** fbp-maven-plugin - a FindBugs Project file generation tool for maven
 * Copyright 2017-2018 MeBigFatGuy.com
 * Copyright 2017-2018 Dave Brosius
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
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
