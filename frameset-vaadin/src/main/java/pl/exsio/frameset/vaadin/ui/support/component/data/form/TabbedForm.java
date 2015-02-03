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
package pl.exsio.frameset.vaadin.ui.support.component.data.form;

import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
public class TabbedForm extends Form {

    private TabSheet tabs;

    private Map<String, Set<String>> config;

    private Map<String, Integer> tabsMap;

    private Map<String, String> propertiesMap;

    public void init(Map<String, Set<String>> config) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        this.setLayout(layout);
        this.tabs = new TabSheet();
        this.tabs.setSizeFull();
        this.getLayout().addComponent(this.tabs);
        this.config = config;
        this.tabsMap = new HashMap<>();
        this.propertiesMap = new HashMap<>();
        this.setSizeFull();
        this.initTabs();
    }

    private void initTabs() {
        int tabIndex = 0;
        for (String tabName : this.config.keySet()) {
            VerticalLayout outerLayout = new VerticalLayout() {
                {
                    FormLayout tabLayout = new FormLayout();
                    tabLayout.setMargin(true);
                    tabLayout.setSizeFull();
                    addComponent(tabLayout);
                }
            };
            outerLayout.setMargin(true);
            outerLayout.setSizeFull();
            Tab tab = this.tabs.addTab(outerLayout, tabName);
            tab.setCaption(t(tabName));
            this.tabsMap.put(tabName, tabIndex);
            tabIndex++;
            for (String property : this.config.get(tabName)) {
                this.propertiesMap.put(property, tabName);
            }
        }
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        try {
            Tab tab = this.getTab(this.propertiesMap.get(propertyId));
            FormLayout tabLayout = (FormLayout) ((VerticalLayout) tab.getComponent()).getComponent(0);
            tabLayout.addComponent(field);
        } catch (NullPointerException ex) {
            throw new RuntimeException("no configuration for property: " + propertyId);
        }
    }

    public Tab getTab(String tabName) {
        if (this.config.containsKey(tabName)) {
            return this.tabs.getTab(this.tabsMap.get(tabName));
        } else {
            return null;
        }
    }

    public TabSheet getTabs() {
        return this.tabs;
    }

}
