/* 
 * The MIT License
 *
 * Copyright 2014 exsio.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.exsio.frameset.vaadin.ui;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pl.exsio.jin.locale.provider.LocaleProvider;
import pl.exsio.jin.translator.Translator;
import pl.exsio.frameset.vaadin.component.InitializableComponent;
import pl.exsio.frameset.vaadin.ex.FramesetInitializationException;
import pl.exsio.frameset.vaadin.navigation.FramesetNavigator;
import pl.exsio.frameset.vaadin.navigation.target.NavigationTarget;

/**
 *
 * @author exsio
 */
public abstract class FramesetUI extends UI {

    static {
        SLF4JBridgeHandler.install();
    }

    private transient FramesetNavigator navigator;

    private Viewport viewport;

    private NavigationTarget navigationTarget;

    private transient Translator translator;

    private transient LocaleProvider localeProvider;

    @Autowired
    private transient ApplicationContext applicationContext;

    @Override
    protected void init(VaadinRequest request) {
        try {
            this.setProductionErrorHandlerIfAppropriate();
            this.getTranslator().init();
            this.setLocale(this.getLocaleProvider().getLocale());
            this.getFramesetNavigator().init(this, this.getPathSegmentDelimiter(), this.getNavigationTarget());
            this.setContent(this.getAndInitViewport());

        } catch (Throwable t) {
            throw new FramesetInitializationException("There were errors during initialization of Frameset", t);
        }
    }

    protected String getPathSegmentDelimiter() {
        return ".";
    }

    public static FramesetUI getCurrent() {
        return (FramesetUI) UI.getCurrent();
    }

    protected Viewport getAndInitViewport() {
        Viewport viewport = this.getViewport();
        if (viewport instanceof InitializableComponent) {
            ((InitializableComponent) viewport).init();
        }
        return viewport;
    }

    protected void setProductionErrorHandlerIfAppropriate() {
        DeploymentConfiguration conf = getSession().getConfiguration();
        if (conf.isProductionMode()) {
            this.setErrorHandler(new ErrorHandler() {

                @Override
                public void error(com.vaadin.server.ErrorEvent event) {
                    Notification.show(event.getThrowable().getMessage(), Notification.Type.TRAY_NOTIFICATION);
                }
            });
        }
    }

    public Translator getTranslator() {
        if (!(this.translator instanceof Translator)) {
            this.translator = this.applicationContext.getBean(Translator.class);
        }
        return this.translator;
    }

    public NavigationTarget getNavigationTarget() {
        if (!(this.navigationTarget instanceof NavigationTarget)) {
            this.navigationTarget = this.applicationContext.getBean(NavigationTarget.class);
        }
        return this.navigationTarget;
    }

    public Viewport getViewport() {
        if (!(this.viewport instanceof Viewport)) {
            this.viewport = this.applicationContext.getBean(Viewport.class);
        }
        return this.viewport;
    }

    public FramesetNavigator getFramesetNavigator() {
        if (!(this.navigator instanceof FramesetNavigator)) {
            this.navigator = this.applicationContext.getBean(FramesetNavigator.class);
        }
        return this.navigator;
    }

    public LocaleProvider getLocaleProvider() {
        if (!(this.localeProvider instanceof LocaleProvider)) {
            this.localeProvider = this.applicationContext.getBean(LocaleProvider.class);
        }
        return this.localeProvider;
    }
}
