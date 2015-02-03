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
package pl.exsio.frameset.vaadin.bootstrap.spring;

import com.vaadin.server.SystemMessages;
import com.vaadin.server.SystemMessagesInfo;
import com.vaadin.server.SystemMessagesProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author exsio
 */
public class FramesetSystemMessagesProvider implements SystemMessagesProvider {

    public static final String DEFAULT_IMPLEMENTATION = "DEFAULT";
    private final ConcurrentMap<Locale, SystemMessages> systemMessagesCache = new ConcurrentHashMap<>();
    private SpringSystemMessagesProvider systemMessagesBean;

    public FramesetSystemMessagesProvider(ApplicationContext applicationContext, String systemMessagesBeanName) {
        if (systemMessagesBeanName.equals(DEFAULT_IMPLEMENTATION)) {
            MessageSource messageSource = applicationContext.getBean(MessageSource.class);
            this.systemMessagesBean = new DefaultSpringSystemMessagesProvider();
            ((DefaultSpringSystemMessagesProvider) this.systemMessagesBean).setMessageSource(messageSource);
        } else {
            this.systemMessagesBean = applicationContext.getBean(systemMessagesBeanName, SpringSystemMessagesProvider.class);
        }
    }

    @Override
    public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
        Locale locale = systemMessagesInfo.getLocale();

        if (this.systemMessagesCache.containsKey(locale)) {
            return this.systemMessagesCache.get(locale);
        }

        SystemMessages systemMessages = this.systemMessagesBean.getSystemMessages(locale);
        this.systemMessagesCache.put(locale, systemMessages);

        return systemMessages;
    }
}
