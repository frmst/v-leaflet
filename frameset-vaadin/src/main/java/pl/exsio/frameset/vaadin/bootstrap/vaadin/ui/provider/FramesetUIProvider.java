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
package pl.exsio.frameset.vaadin.bootstrap.vaadin.ui.provider;

import com.vaadin.server.*;
import com.vaadin.ui.UI;
import pl.exsio.frameset.vaadin.bootstrap.spring.FramesetApplicationContext;

/**
 *
 * @author exsio
 */
public class FramesetUIProvider extends UIProvider {

    protected static final String BEAN_NAME_PARAMETER = "beanName";

    public FramesetUIProvider() {
    }

    @Override
    public UI createInstance(UICreateEvent event) {
        return (UI) FramesetApplicationContext.getApplicationContext().getBean(getUIBeanName(event.getRequest()));
    }

    @Override
    public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {

        return (Class<? extends UI>) FramesetApplicationContext.getApplicationContext().getType(getUIBeanName(event.getRequest()));
    }

    @Override
    public boolean isPreservedOnRefresh(UICreateEvent event) {
        if (isSessionScopedUI(event.getRequest())) {
            return true;
        }

        return super.isPreservedOnRefresh(event);
    }

    public boolean isSessionScopedUI(VaadinRequest request) {
        return !FramesetApplicationContext.getApplicationContext().isPrototype(getUIBeanName(request));
    }

    protected String getUIBeanName(VaadinRequest request) {
        String vaadinBeanName = "ui";

        Object uiBeanName = request.getService().getDeploymentConfiguration().getApplicationOrSystemProperty(BEAN_NAME_PARAMETER, null);
        if (uiBeanName != null && uiBeanName instanceof String) {
            vaadinBeanName = uiBeanName.toString();
        }

        return vaadinBeanName;
    }
}
