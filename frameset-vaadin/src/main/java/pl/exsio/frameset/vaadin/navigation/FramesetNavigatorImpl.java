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
package pl.exsio.frameset.vaadin.navigation;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.security.acls.domain.BasePermission;
import pl.exsio.frameset.core.model.EmptyModule;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.core.model.Module;
import pl.exsio.frameset.core.repository.provider.CoreRepositoryProvider;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.routing.modulelocator.ModuleLocator;
import pl.exsio.frameset.security.acl.AclManager;
import pl.exsio.frameset.vaadin.ex.InvalidNavigationTargetException;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.model.VaadinModule;
import pl.exsio.frameset.vaadin.navigation.target.ComponentContainerNavigationTarget;
import pl.exsio.frameset.vaadin.navigation.target.NavigationTarget;
import pl.exsio.frameset.vaadin.navigation.target.ViewDisplayNavigationTarget;

/**
 *
 * @author exsio
 */
public class FramesetNavigatorImpl implements FramesetNavigator, ViewChangeListener {

    protected final static int EVENT_AFTER_FRAME_CHANGE = 1;

    protected final static int EVENT_BEFORE_FRAME_CHANGE = -1;

    protected NavigationTarget navigationTarget;

    protected transient ModuleLocator moduleLocator;

    protected transient CoreRepositoryProvider coreRepositories;

    protected Navigator navigator;

    protected transient AclManager acl;

    protected FramesetErrorView errorView;

    /**
     * Map of all Frames that are navigable. Navigable Frame is a Frame, that
     * points to a valid VaadinModule
     */
    protected final Map<String, Frame> navigableFrames;

    /**
     * Delimiter that is used to split UriFragment into Frame slugs. Vaadin
     * can't handle '/' sign in an UriFragment, because it is used to separate
     * the fragment itself from View arguments.
     */
    protected String pathSegmentDelimiter;

    /**
     * Currently loaded Frame.
     */
    protected Frame currentFrame;

    protected final Set<FrameChangeListener> frameChangeListeners;

    protected final Set<ViewChangeListener> viewChangeListeners;

    protected final Map<Frame, String> pathMap;

    protected UI ui;

    public FramesetNavigatorImpl() {
        this.navigableFrames = new HashMap<>();
        this.frameChangeListeners = new HashSet<>();
        this.viewChangeListeners = new HashSet<>();
        this.pathMap = new HashMap<>();
    }

    @Override
    public boolean navigateTo(Frame frame) {
        return this.navigateTo(frame, false);
    }

    @Override
    public boolean navigateTo(Frame frame, boolean force) {
        if (this.acl.isGranted(frame, BasePermission.READ)) {
            String path = this.getPath(frame);
            if (force || !this.getCurrentState().equals(path)) {
                if (this.navigableFrames.containsKey(path)) {
                    this.navigator.navigateTo(path);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            Notification.show(t("core.access_denied"), Notification.Type.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean navigateHome() {
        Frame home = this.coreRepositories.getFrameRepository().getHomeFrame();
        if (this.acl.isGranted(home, BasePermission.READ)) {
            this.navigateTo(home, true);
            Page.getCurrent().setUriFragment(this.getPath(home));
            return true;
        } else {
            return false;
        }
    }

    protected void dispatchFrameChangeEvent(Frame frame, int eventMode) {
        FrameChangeEvent event = new FrameChangeEvent(this.currentFrame, frame);
        for (FrameChangeListener listener : this.frameChangeListeners) {
            if (eventMode == EVENT_BEFORE_FRAME_CHANGE) {
                listener.beforeFrameChange(event);
            } else if (eventMode == EVENT_AFTER_FRAME_CHANGE) {
                listener.afterFrameChange(event);
            }
        }
    }

    @Override
    public void addFrameChangeListener(FrameChangeListener listener) {
        this.frameChangeListeners.add(listener);
    }

    @Override
    public void removeFrameChangeListener(FrameChangeListener listener) {
        this.frameChangeListeners.remove(listener);
    }

    protected String sanitizePath(String path) {
        if (path != null && path.startsWith("!")) {
            path = path.substring(1);
        }
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    @Override
    public void init(UI ui, String pathSegmentDelimiter, NavigationTarget navigationTarget) throws InvalidNavigationTargetException {
        this.pathSegmentDelimiter = pathSegmentDelimiter;
        this.navigationTarget = navigationTarget;
        this.ui = ui;
        this.navigator = this.buildNavigator(this.createNavigator());
        this.addViewChangeListener(this);
    }

    @Override
    public void refresh() throws InvalidNavigationTargetException {
        this.navigator = this.buildNavigator(this.createNavigator());
        for (ViewChangeListener listener : this.viewChangeListeners) {
            this.navigator.addViewChangeListener(listener);
        }
    }

    protected Navigator createNavigator() throws InvalidNavigationTargetException {
        if (this.navigationTarget instanceof ViewDisplayNavigationTarget) {
            return new Navigator(this.ui, (ViewDisplayNavigationTarget) this.navigationTarget);
        } else if (this.navigationTarget instanceof ComponentContainerNavigationTarget) {
            return new Navigator(this.ui, (ComponentContainerNavigationTarget) this.navigationTarget);
        } else {
            throw new InvalidNavigationTargetException(this.navigationTarget.getClass());
        }
    }

    protected Navigator buildNavigator(Navigator navigator) {

        for (Frame frame : getFramesAsList()) {
            if (!frame.isRoot()) {
                if (this.acl.isGranted(frame, BasePermission.READ)) {
                    Module module = this.moduleLocator.locate(frame);
                    if (module instanceof EmptyModule) {
                        continue;
                    } else if (module instanceof VaadinModule) {
                        String path = this.getPath(frame);
                        navigator.addView(path, (VaadinModule) module);
                        this.navigableFrames.put(path, frame);
                    } else {
                        Notification.show("there is no module '" + frame.getModuleId() + "'", Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        }

        navigator.setErrorView(this.errorView);
        return navigator;
    }

    @Override
    public String getPath(Frame frame) {
        if (!this.pathMap.containsKey(frame)) {
            this.pathMap.put(frame, this.buildPath(frame));
        }
        return this.pathMap.get(frame);

    }

    private String buildPath(Frame frame) {
        StringBuilder sb = new StringBuilder("");
        List<Frame> parents = (List<Frame>) this.coreRepositories.getFrameRepository().getParents(frame);
        for (int i = parents.size() - 1; i >= 0; i--) {
            Frame parent = parents.get(i);
            if (!parent.isRoot()) {
                sb.append(parent.getSlug());
                sb.append(this.pathSegmentDelimiter);
            }
        }
        sb.append(frame.getSlug());
        String slug = sb.toString();
        if (slug.startsWith(this.pathSegmentDelimiter)) {
            slug = slug.substring(1);
        }
        return slug;
    }

    @Override
    public String getCurrentState() {
        return this.sanitizePath(this.navigator.getState());
    }

    @Override
    public void addViewChangeListener(ViewChangeListener listener) {
        this.viewChangeListeners.add(listener);
        this.navigator.addViewChangeListener(listener);
    }

    @Override
    public void removeViewChangeListener(ViewChangeListener listener) {
        this.viewChangeListeners.remove(listener);
        this.navigator.removeViewChangeListener(listener);
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        String path = event.getViewName();
        if (this.navigableFrames.containsKey(path)) {
            Frame frame = this.navigableFrames.get(path);
            if (event.getNewView() instanceof VaadinModule) {
                VaadinModule module = (VaadinModule) event.getNewView();
                module.setFrame((VaadinFrame) frame);
            }
            this.dispatchFrameChangeEvent(frame, EVENT_BEFORE_FRAME_CHANGE);
            this.currentFrame = frame;
        }
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        String path = this.getCurrentState();
        if (this.navigableFrames.containsKey(path)) {
            Frame frame = this.navigableFrames.get(path);
            Page.getCurrent().setTitle(t(frame.getTitle()));
            this.dispatchFrameChangeEvent(frame, EVENT_AFTER_FRAME_CHANGE);
            this.currentFrame = frame;
            if (event.getNewView() instanceof VaadinModule) {
                ((VaadinModule) event.getNewView()).addStyleName("frameset-module-" + frame.getModuleId());
            }
        } else {
            this.navigateHome();
        }

    }

    protected Iterable<Frame> getFramesAsList() {
        return this.coreRepositories.getFrameRepository().getTreeAsList(getRootFrame());
    }

    protected Frame getRootFrame() {
        return this.coreRepositories.getFrameRepository().getRootFrame();
    }

    public void setModuleLocator(ModuleLocator locator) {
        this.moduleLocator = locator;
    }

    public void setCoreRepositories(CoreRepositoryProvider coreRepositories) {
        this.coreRepositories = coreRepositories;
    }

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    public void setErrorView(FramesetErrorView errorView) {
        this.errorView = errorView;
    }
}
