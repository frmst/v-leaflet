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

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.acls.domain.BasePermission;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.core.repository.provider.CoreRepositoryProvider;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.acl.AclManager;

/**
 *
 * @author exsio
 */
public class FramesetErrorViewImpl extends CssLayout implements FramesetErrorView {

    private transient CoreRepositoryProvider coreRepositories;

    private transient AclManager acl;

    @Autowired
    private transient ApplicationContext applicationContext;

    public void setAcl(AclManager acl) {
        this.acl = acl;
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        Frame homeFrame = this.coreRepositories.getFrameRepository().getHomeFrame();
        FramesetNavigator navigator = (FramesetNavigator) this.applicationContext.getBean("navigator");
        if (this.acl.isGranted(homeFrame, BasePermission.READ)) {
            navigator.navigateTo(homeFrame, true);
        } else {
            this.removeAllComponents();
            this.addComponent(new Label(t("frameset.navigation.errror.forbidden")));
        }
    }

    public void setCoreRepositories(CoreRepositoryProvider coreRepositories) {
        this.coreRepositories = coreRepositories;
    }

}
