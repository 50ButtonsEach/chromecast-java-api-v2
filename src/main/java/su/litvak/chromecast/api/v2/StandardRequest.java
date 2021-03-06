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

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Map;

/**
 * Parent class for transport object representing messages sent TO ChromeCast device
 */
abstract class StandardRequest extends StandardMessage implements Request {
    Long requestId;

    @Override
    public final void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    @Override
    public final Long getRequestId() {
        return requestId;
    }

    static class Status extends StandardRequest {}

    static class AppAvailability extends StandardRequest {
        @JsonProperty
        final String[] appId;

        AppAvailability(String... appId) {
            this.appId = appId;
        }
    }

    static class Launch extends StandardRequest {
        @JsonProperty
        final String appId;

        Launch(String appId) {
            this.appId = appId;
        }
    }

    static class Stop extends StandardRequest {
        @JsonProperty
        final String sessionId;

        Stop(String sessionId) {
            this.sessionId = sessionId;
        }
    }

    static class Load extends StandardRequest {
        @JsonProperty
        final String sessionId;
        @JsonProperty
        final Media media;
        @JsonProperty
        final boolean autoplay;
        @JsonProperty
        final double currentTime;
        @JsonProperty
        final Object customData;

        Load(String sessionId, Media media, boolean autoplay, double currentTime, final Map<String, String> customData) {
            this.sessionId = sessionId;
            this.media = media;
            this.autoplay = autoplay;
            this.currentTime = currentTime;

            this.customData = customData == null ? null : new Object() {
                @JsonProperty
                Map<String, String> payload = customData;
            };
        }
    }

    abstract static class MediaRequest extends StandardRequest {
        @JsonProperty
        final long mediaSessionId;
        @JsonProperty
        final String sessionId;

        MediaRequest(long mediaSessionId, String sessionId) {
            this.mediaSessionId = mediaSessionId;
            this.sessionId = sessionId;
        }
    }

    static class Play extends MediaRequest {
        Play(long mediaSessionId, String sessionId) {
            super(mediaSessionId, sessionId);
        }
    }

    static class Pause extends MediaRequest {
        Pause(long mediaSessionId, String sessionId) {
            super(mediaSessionId, sessionId);
        }
    }

    static class QueueUpdate extends MediaRequest {
        @JsonProperty
        final long jump;

        QueueUpdate(long mediaSessionId, String sessionId, long jump) {
            super(mediaSessionId, sessionId);
            this.jump = jump;
        }
    }

    static class Seek extends MediaRequest {
        @JsonProperty
        final double currentTime;

        Seek(long mediaSessionId, String sessionId, double currentTime) {
            super(mediaSessionId, sessionId);
            this.currentTime = currentTime;
        }
    }

    static class SetVolume extends StandardRequest {
        @JsonProperty
        final Volume volume;

        SetVolume(Volume volume) {
            this.volume = volume;
        }
    }

    static Status status() {
        return new Status();
    }

    static AppAvailability appAvailability(String... appId) {
        return new AppAvailability(appId);
    }

    static Launch launch(String appId) {
        return new Launch(appId);
    }

    static Stop stop(String sessionId) {
        return new Stop(sessionId);
    }

    static Load load(String sessionId, Media media, boolean autoplay, double currentTime, Map<String, String> customData) {
        return new Load(sessionId, media, autoplay, currentTime, customData);
    }

    static Play play(String sessionId, long mediaSessionId) {
        return new Play(mediaSessionId, sessionId);
    }

    static Pause pause(String sessionId, long mediaSessionId) {
        return new Pause(mediaSessionId, sessionId);
    }

    static QueueUpdate queueUpdate(String sessionId, long mediaSessionId, long jump) {
        return new QueueUpdate(mediaSessionId, sessionId, jump);
    }

    static Seek seek(String sessionId, long mediaSessionId, double currentTime) {
        return new Seek(mediaSessionId, sessionId, currentTime);
    }

    static SetVolume setVolume(Volume volume) {
        return new SetVolume(volume);
    }
}
