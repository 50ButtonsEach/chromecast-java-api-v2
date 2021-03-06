/*
 * Copyright 2014 Vitaly Litvak (vitavaque@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package su.litvak.chromecast.api.v2;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonProperty;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Volume settings
 */
public class Volume {
    final static Float default_increment = 0.05f;
    final static String default_controlType = "attenuation";
    @JsonProperty
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public final Float level;
    @JsonProperty
    public final boolean muted;

    @JsonProperty
    public final Float increment;
    @JsonProperty
    public final Double stepInterval;
    @JsonProperty
    public final String controlType;

    public Volume() {
        level = new Float(-1);
        muted = false;
        increment = default_increment;
        stepInterval = default_increment.doubleValue();
        controlType = default_controlType;
    }

    public Volume(@JsonProperty("level") Float level,
            @JsonProperty("muted") boolean muted,
            @JsonProperty("increment") Float increment,
            @JsonProperty("stepInterval") Double stepInterval,
            @JsonProperty("controlType") String controlType
    ) {
        this.level = level;
        this.muted = muted;
        if (increment != null && increment > 0f) {
            this.increment = increment;
        } else {
            this.increment = default_increment;
        }
        if(stepInterval!=null && stepInterval > 0d) {
            this.stepInterval = stepInterval;
        } else {
            this.stepInterval = default_increment.doubleValue();
        }
        this.controlType = controlType;
    }

    @Override
    public int hashCode () {
        return Arrays.hashCode(new Object[]{this.level, this.muted, this.increment,
            this.stepInterval, this.controlType});
    }

    @Override
    public boolean equals (final Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Volume))  return false;
        final Volume that = (Volume) obj;
        return this.level == null ? that.level == null : this.level.equals(that.level)
                && this.muted == that.muted
                && this.increment == null ? that.increment == null : this.increment.equals(that.increment)
                        && this.stepInterval == null ? that.stepInterval == null : this.stepInterval.equals(that.stepInterval)
                        && this.controlType == null ? that.controlType == null : this.controlType.equals(that.controlType);
    }

    @Override
    public String toString () {
        return String.format("Volume{level: %s, muted: %b, increment: %s, stepInterval: %s, controlType: %s}",
                this.level, this.muted, this.increment, this.stepInterval, this.controlType);
    }

}
