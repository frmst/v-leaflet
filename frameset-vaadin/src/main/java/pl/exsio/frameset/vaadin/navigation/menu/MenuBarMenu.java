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

import com.vaadin.ui.MenuBar;
import java.util.LinkedHashSet;
import java.util.Set;
import pl.exsio.frameset.core.model.Frame;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.navigation.menu.builder.MenuBuilder;
import pl.exsio.frameset.vaadin.model.VaadinFrame;
import pl.exsio.frameset.vaadin.ui.FramesetUI;

/**
 *
 * @author exsio
 */
public class MenuBarMenu extends MenuBar implements Menu {

    private Frame root;

    private transient MenuBuilder builder;

    private final Set<MenuSelectionChangeListener> listeners;

    public MenuBarMenu() {
        this.listeners = new LinkedHashSet<>();
    }

    public void setRootFrame(Frame root) {
        this.root = root;
    }

    public void setMenuBuilder(MenuBuilder builder) {
        this.builder = builder;
    }

    private pl.exsio.frameset.navigation.menu.MenuItem menu;

    @Override
    public void init() {
        addStyleName("menu");
        this.menu = this.builder.build(this.root, true);
        this.build(this.menu, null);
    }

    private void build(pl.exsio.frameset.navigation.menu.MenuItem menu, MenuItem barItem) {
        for (final pl.exsio.frameset.navigation.menu.MenuItem item : menu.getChildren()) {
            MenuItem subBarItem = null;
            if (barItem instanceof MenuItem) {
                subBarItem = barItem.addItem(t(item.getLabel()), ((VaadinFrame) item.getFrame()).getIcon(), getCommand(item.getFrame(), item));
            } else {
                subBarItem = this.addItem(t(item.getLabel()), ((VaadinFrame) item.getFrame()).getIcon(), null);
            }
            if (item.getChildren().size() > 0) {
                this.build(item, subBarItem);
            }
        }
    }

    @Override
    public void select(Frame frame) {
    }

    private MenuBar.Command getCommand(final Frame frame, final pl.exsio.frameset.navigation.menu.MenuItem item) {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                ((FramesetUI) getUI()).getFramesetNavigator().navigateTo(frame, true);
                MenuSelectionChangeEvent e = new MenuSelectionChangeEvent(item, null);
                for (MenuSelectionChangeListener listener : listeners) {
                    listener.onSelectionChange(e);
                }
            }
        };
    }

    @Override
    public void addSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeSelectionChangeListener(MenuSelectionChangeListener listener) {
        this.listeners.remove(listener);
    }

}
