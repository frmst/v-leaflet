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
package pl.exsio.frameset.vaadin.bootstrap.servlet;

import com.vaadin.server.*;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import pl.exsio.frameset.vaadin.bootstrap.spring.FramesetApplicationContext;
import pl.exsio.frameset.vaadin.bootstrap.spring.FramesetSystemMessagesProvider;
import pl.exsio.frameset.vaadin.bootstrap.vaadin.ui.provider.FramesetUIProvider;

/**
 *
 * @author exsio
 */
public class FramesetServlet extends VaadinServlet {

    private static final String SYSTEM_MESSAGES_BEAN_NAME_PARAMETER = "systemMessagesBeanName";

    private static final String CONTEXT_CONFIG_LOCATION_PARAMETER = "contextConfigLocation";

    private transient ApplicationContext applicationContext;

    private String systemMessagesBeanName = "";

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());

        if (config.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER) != null) {
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            context.setParent(this.applicationContext);
            context.setConfigLocation(config.getInitParameter(CONTEXT_CONFIG_LOCATION_PARAMETER));
            context.setServletConfig(config);
            context.setServletContext(config.getServletContext());
            context.refresh();

            this.applicationContext = context;
        }

        if (config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER) != null) {
            this.systemMessagesBeanName = config.getInitParameter(SYSTEM_MESSAGES_BEAN_NAME_PARAMETER);
        }

        if (FramesetApplicationContext.getApplicationContext() == null) {
            FramesetApplicationContext.setApplicationContext(applicationContext);
        }

        super.init(config);
    }

    protected void initializeApplication(VaadinServletService service) {
        if (this.systemMessagesBeanName != null && !"".equals(this.systemMessagesBeanName)) {
            FramesetSystemMessagesProvider messagesProvider = new FramesetSystemMessagesProvider(this.applicationContext, this.systemMessagesBeanName);
            service.setSystemMessagesProvider(messagesProvider);
        }

        String uiProviderProperty = service.getDeploymentConfiguration().getApplicationOrSystemProperty(Constants.SERVLET_PARAMETER_UI_PROVIDER, null);

        if (uiProviderProperty == null) {
            service.addSessionInitListener(new SessionInitListener() {
                @Override
                public void sessionInit(SessionInitEvent event) throws ServiceException {
                    event.getSession().addUIProvider(new FramesetUIProvider());
                }
            });
        }
    }

    @Override
    protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
        final VaadinServletService service = super.createServletService(deploymentConfiguration);
        this.initializeApplication(service);
        return service;
    }
}
