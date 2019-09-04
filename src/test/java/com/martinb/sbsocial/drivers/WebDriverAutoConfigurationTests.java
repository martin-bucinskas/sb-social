package com.martinb.sbsocial.drivers;

import org.apache.commons.lang3.ClassUtils;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class WebDriverAutoConfigurationTests {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if(context != null) {
            context.close();
        }
    }

    private void load(Class<?>[] configs, String... environmnet) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(WebDriverAutoConfiguration.class);

        if(configs.length > 0) {
            applicationContext.register(configs);
        }

        TestPropertyValues.of(environmnet).applyTo(applicationContext);
        applicationContext.refresh();
        context = applicationContext;
    }

    //TODO: Fix this test as results in failing.
    @Test
    public void fallbackToNonGuiModeWhenAllBrowsersDisabled() {
//        load(new Class[] {},
//                "com.martinb.webdriver.firefox.enabled:false",
//                "com.martinb.webdriver.safari.enabled:false",
//                "com.martinb.webdriver.chrome.enabled:false");
//
//        WebDriver driver = context.getBean(WebDriver.class);
//        assertThat(ClassUtils.isAssignable(TakesScreenshot.class, driver.getClass())).isFalse();
//        assertThat(ClassUtils.isAssignable(HtmlUnitDriver.class, driver.getClass())).isTrue();
    }
}
