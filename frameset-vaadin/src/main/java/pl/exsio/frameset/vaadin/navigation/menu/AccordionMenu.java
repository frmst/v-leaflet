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
package pl.exsio.frameset.vaadin.navigation.menu;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import pl.exsio.frameset.core.model.Frame;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.navigation.menu.MenuItem;
import pl.exsio.frameset.navigation.menu.builder.MenuBuilder;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.navigation.FrameChangeEvent;
import pl.exsio.frameset.vaadin.ui.FramesetUI;

/**
 *
 * @author exsio
 */
public class AccordionMenu extends Accordion implements InteractiveMenu {

    private Frame root;

    private transient MenuBuilder builder;

    private final Set<MenuSelectionChangeListener> listeners;

    private final Map<Frame, Tab> frameMap;

    private final Map<Frame, Button> buttonsMap;

    private final Set<Button> buttons;

    public AccordionMenu() {
        this.listeners = new LinkedHashSet<>();
        this.frameMap = new HashMap<>();
        this.buttonsMap = new HashMap<>();
        this.buttons = new HashSet<>();
    }

    public void setRootFrame(Frame root) {
        this.root = root;
    }

    public void setMenuBuilder(MenuBuilder builder) {
        this.builder = builder;
    }

    private MenuItem menu;

    @Override
    public void init() {
        addStyleName("menu");
        this.menu = this.builder.build(this.root, true);
        this.build(this.menu);
    }

    private void build(MenuItem menu) {

        for (MenuItem category : menu.getChildren()) {

            VaadinFrame frame = (VaadinFrame) category.getFrame();
            VerticalLayout categoryPanel = new VerticalLayout();
            Tab tab = this.addTab(categoryPanel, t(frame.getMenuLabel()), frame.getIcon());
            this.initPanel(categoryPanel, category, tab);
        }
    }

    private VerticalLayout initPanel(VerticalLayout panel, MenuItem category, Tab tab) {

        for (final MenuItem item : category.getChildren()) {
            final VaadinFrame frame = (VaadinFrame) item.getFrame();
            final Button b = new NativeButton(t(frame.getMenuLabel()));
            b.setIcon(frame.getIcon());
            b.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ((FramesetUI) getUI()).getFramesetNavigator().navigateTo(frame, true);
                    MenuSelectionChangeEvent e = new MenuSelectionChangeEvent(item, b);
                    for (MenuSelectionChangeListener listener : listeners) {
                        listener.onSelectionChange(e);
                    }
                }
            });
            b.setStyleName("button-" + frame.getSlug());
            this.buttonsMap.put(frame, b);
            this.frameMap.put(frame, tab);
            this.buttons.add(b);
            panel.addComponent(b);
        }
        panel.setStyleName("panel-" + category.getFrame().getSlug());
        return panel;
    }

    @Override
    public void select(Frame frame) {
        this.setSelectedTab(this.frameMap.get(frame));
        this.removeSelection();
        Button selected = this.buttonsMap.get(frame);
        if (selected != null) {
            selected.addStyleName("selected");
        }
    }

    private void removeSelection() {
        for (Button b : this.buttons) {
            b.removeStyleName("selected");
        }
    }

    @Override
    public void addSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void beforeFrameChange(FrameChangeEvent event) {

    }

    @Override
    public void afterFrameChange(FrameChangeEvent event) {
        this.select(event.getNewFrame());
    }

}
