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
package pl.exsio.frameset.vaadin.navigation.target;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.vaadin.model.VaadinModule;
import pl.exsio.frameset.vaadin.navigation.FramesetNavigator;

/**
 *
 * @author exsio
 */
public class TabbedNavigationTarget extends TabSheet implements ViewDisplayNavigationTarget {

    
    private transient ApplicationContext applicationContext;
    
    private boolean updateSelection = true;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        final FramesetNavigator navigator = (FramesetNavigator) this.applicationContext.getBean(FramesetNavigator.NAVIGATOR_BEAN_NAME);
        this.addSelectedTabChangeListener(new SelectedTabChangeListener() {

            @Override
            public void selectedTabChange(SelectedTabChangeEvent event) {
                if(updateSelection) {
                    VaadinModule module = (VaadinModule) event.getTabSheet().getSelectedTab();
                    Page currentPage = Page.getCurrent();
                    currentPage.setUriFragment(navigator.getPath(module.getFrame()), false);
                    currentPage.setTitle(t(module.getFrame().getTitle()));
                }
            }
        });
    }

    @Override
    public void showView(View view) {
        this.updateSelection = false;
        if (view instanceof VaadinModule) {
            VaadinModule module = (VaadinModule) view;
            Tab tab = this.getTab((Component) module);
            if (tab instanceof Tab) {
                this.setSelectedTab(tab);
            } else {
                Tab newTab = this.addTab((Component) module, t(module.getFrame().getTitle()), (Resource) module.getFrame().getIcon());
                newTab.setClosable(true);
                this.setSelectedTab(newTab);
            }
        }
        this.updateSelection = true;
    }

}
