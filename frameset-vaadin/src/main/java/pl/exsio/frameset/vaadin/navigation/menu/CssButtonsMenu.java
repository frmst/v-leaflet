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

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.NativeButton;
import java.util.LinkedHashSet;
import java.util.Set;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.navigation.menu.MenuItem;
import pl.exsio.frameset.navigation.menu.builder.MenuBuilder;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.navigation.FrameChangeEvent;
import pl.exsio.frameset.vaadin.ui.FramesetUI;
import static pl.exsio.jin.translationcontext.TranslationContext.t;

/**
 *
 * @author exsio
 */
public class CssButtonsMenu extends CssLayout implements InteractiveMenu {

    private Frame root;

    private transient MenuBuilder builder;

    private final Set<Button> buttons;

    private MenuItem menu;

    private final Set<MenuSelectionChangeListener> listeners;

    public CssButtonsMenu() {

        this.buttons = new LinkedHashSet<>();
        this.listeners = new LinkedHashSet<>();
    }

    public void setRootFrame(Frame root) {
        this.root = root;
    }

    public void setMenuBuilder(MenuBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void select(Frame frame) {
        this.doSelect(this.menu, frame);

    }

    private boolean doSelect(MenuItem menu, Frame frame) {
        for (MenuItem item : menu.getChildren()) {
            if (item.getFrame().equals(frame)) {
                this.clearMenuSelection();
                this.ensureVisibility(item);
                Button b = (Button) item.getParam("button");
                if (b instanceof Button) {
                    b.addStyleName("selected");
                    this.dispatchSelectionChangeEvent(item, b);
                    return true;
                }
            }
            if (this.doSelect(item, frame)) {
                return true;
            }
        }
        return false;
    }

    private void ensureVisibility(MenuItem item) {
        CssLayout container = (CssLayout) item.getParam("container");
        if (container instanceof CssLayout) {
            container.setVisible(true);
        }
        if (item.getParent() instanceof MenuItem) {
            this.ensureVisibility(item.getParent());
        }
    }

    @Override
    public void init() {
        addStyleName("menu");
        addStyleName("menu-level-0");
        this.menu = this.builder.build(this.root, true);
        this.build(this.menu, 1, this);

    }

    private void build(MenuItem menu, final int level, ComponentContainer container) {
        for (final MenuItem item : menu.getChildren()) {
            Button b = new NativeButton(t(item.getLabel()));
            b.addStyleName("menu-item-" + item.getFrame().getKey());
            b.setIcon(((VaadinFrame) item.getFrame()).getIcon());
            b.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ((FramesetUI) getUI()).getFramesetNavigator().navigateTo(item.getFrame());
                    if (item.getChildren().size() > 0) {
                        CssLayout submenu = (CssLayout) item.getParam("submenu");
                        submenu.setVisible(!submenu.isVisible());
                    }
                }
            });
            this.buttons.add(b);
            container.addComponent(b);
            item.addParam("button", b);
            item.addParam("container", container);
            if (item.getChildren().size() > 0) {
                CssLayout subContainer = new CssLayout() {
                    {
                        addStyleName("sub-menu");
                        addStyleName("menu-level-" + level);
                        setVisible(false);
                    }
                };

                this.build(item, level + 1, subContainer);
                container.addComponent(subContainer);

                item.addParam("submenu", subContainer);
            }
        }
    }

    private void clearMenuSelection() {
        for (Button b : this.buttons) {
            b.removeStyleName("selected");
        }
    }

    @Override
    public void beforeFrameChange(FrameChangeEvent event) {
    }

    @Override
    public void afterFrameChange(FrameChangeEvent event) {
        this.select(event.getNewFrame());
    }

    @Override
    public void addSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.remove(listener);
    }

    private void dispatchSelectionChangeEvent(MenuItem item, Button button) {
        MenuSelectionChangeEvent event = new MenuSelectionChangeEvent(item, button);
        for (MenuSelectionChangeListener listener : this.listeners) {
            listener.onSelectionChange(event);
        }
    }

}
