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
package pl.exsio.frameset.vaadin.bootstrap.spring.scope;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 *
 * @author exsio
 */
public class UIScope implements Scope, DetachListener {

    public static final String SCOPE_NAME = "ui";

    private static final Log log = LogFactory.getLog(UIScope.class);

    private final Map<String, Object> beans = new ConcurrentHashMap<>();

    private final Map<String, Runnable> callbacks = new ConcurrentHashMap<>();

    private final Map<UI, String> sessions = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {

        String key = this.key(name);
        if (key != null) {
            Object bean = beans.get(key);

            if (bean == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Bean not found in scope: [" + key + "]. Creating new one");
                }
                bean = objectFactory.getObject();
                beans.put(key, bean);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Bean found in scope: [" + key + "]");
                }
            }

            return bean;
        }

        throw new RuntimeException("No UI found in scope, while trying to find bean '"+name+"'");
    }

    @Override
    public Object remove(String name) {
        return beans.remove(this.key(name));
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        callbacks.put(this.key(name), callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        Integer uiId = null;

        UI ui = UI.getCurrent();
        if (ui == null) {
            UIid id = CurrentInstance.get(UIid.class);
            if (id != null) {
                uiId = id.getUiId();
            }
        } else if (ui != null) {
            if (!sessions.containsKey(ui)) {
                ui.addDetachListener(this);
                sessions.put(ui, VaadinSession.getCurrent().getSession().getId());
            }

            uiId = ui.getUIId();
        }

        return uiId != null ? getConversationId(uiId) : null;
    }

    private String getConversationId(Integer id) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null) {
            log.info("Request Conversation id without session");
            return null;
        }

        return session.getSession().getId() + ":" + id.toString();
    }

    private String getConversationId(UI ui) {
        return sessions.get(ui) + ":" + ui.getUIId();
    }

    protected String key(String name) {
        String id = getConversationId();

        return id != null ? id + "_" + name : null;
    }

    private void removeBeans(UI ui) {
        Set<String> keys = beans.keySet();
        Iterator<String> iter = keys.iterator();

        while (iter.hasNext()) {
            String key = iter.next();
            String prefix = getConversationId(ui);

            if (key.startsWith(prefix)) {
                iter.remove();
                if (log.isDebugEnabled()) {
                    log.debug("Removed bean [" + key + "]");
                }
                Runnable callback = callbacks.remove(key);
                if (callback != null) {
                    callback.run();
                }
            }
        }

    }

    @Override
    public synchronized void detach(DetachEvent event) {
        UI ui = (UI) event.getConnector();
        if (log.isDebugEnabled()) {
            log.debug("UI [" + ui.getUIId() + "] detached, destroying scoped beans");
        }

        removeBeans(ui);
        sessions.remove(ui);

    }

    private class UIid {

        private Integer uiId;

        /**
         * @param uiId
         */
        public UIid(Integer uiId) {
            this.uiId = uiId;
        }

        public Integer getUiId() {
            return uiId;
        }

        public void setUiId(Integer uiId) {
            this.uiId = uiId;
        }

    }
}
