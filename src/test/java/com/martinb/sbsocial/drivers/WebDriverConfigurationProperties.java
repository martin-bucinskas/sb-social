package com.martinb.sbsocial.drivers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("com.martinb.webdriver")
public class WebDriverConfigurationProperties {

    private Firefox firefox = new Firefox();
    private Safari safari = new Safari();
    private Chrome chrome = new Chrome();

    public Firefox getFirefox() {
        return firefox;
    }

    public Safari getSafari() {
        return safari;
    }

    public Chrome getChrome() {
        return chrome;
    }

    @Data
    static class Firefox {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }
    }

    @Data
    static class Safari {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }
    }

    @Data
    static class Chrome {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }
    }
}
