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

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Tree;
import java.util.LinkedHashSet;
import java.util.Set;
import pl.exsio.frameset.core.model.Frame;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.navigation.menu.MenuItem;
import pl.exsio.frameset.navigation.menu.MenuItemImpl;
import pl.exsio.frameset.vaadin.entity.VaadinFrameImpl;
import pl.exsio.frameset.vaadin.navigation.FrameChangeEvent;
import pl.exsio.frameset.vaadin.ui.FramesetUI;

/**
 *
 * @author exsio
 */
public class TreeMenu extends Tree implements InteractiveMenu {

    private transient EntityProvider entityProvider;

    private final Set<MenuSelectionChangeListener> listeners;

    public TreeMenu() {
        this.listeners = new LinkedHashSet<>();
    }

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    public void init() {
        addStyleName("menu");
        this.build();
        this.expandItem(new Long(1));

    }

    private void build() {
        final JPAContainer<VaadinFrameImpl> frames = this.createEntityContainer();
        this.setContainerDataSource(frames);
        this.setItemCaptionMode(Tree.ItemCaptionMode.EXPLICIT);
        this.formatTreeItems(frames);
        this.handleItemClick();
        this.handleItemCollapse(frames);
    }

    private void formatTreeItems(final JPAContainer<VaadinFrameImpl> frames) {
        for (Object itemId : frames.getItemIds()) {
            VaadinFrameImpl frame = frames.getItem(itemId).getEntity();
            this.setItemCaption(itemId, t(frame.getMenuLabel()));
            this.setItemIcon(itemId, frame.getIcon());

        }
    }

    private void handleItemCollapse(final JPAContainer<VaadinFrameImpl> frames) {
        this.addCollapseListener(new CollapseListener() {

            @Override
            public void nodeCollapse(CollapseEvent event) {
                VaadinFrameImpl frame = frames.getItem(event.getItemId()).getEntity();
                if (frame.isRoot()) {
                    expandItem(event.getItemId());
                }

            }
        });
    }

    private void handleItemClick() {
        this.addItemClickListener(new ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                VaadinFrameImpl frame = ((EntityItem<VaadinFrameImpl>) event.getItem()).getEntity();
                if(!frame.isRoot()) {
                    ((FramesetUI) getUI()).getFramesetNavigator().navigateTo(frame);
                    MenuItem item = new MenuItemImpl();
                    item.setFrame(frame);
                    item.setLabel(frame.getMenuLabel());
                    MenuSelectionChangeEvent e = new MenuSelectionChangeEvent(item, null);
                    for (MenuSelectionChangeListener listener : listeners) {
                        listener.onSelectionChange(e);
                    }
                }
            }
        });
    }

    private JPAContainer<VaadinFrameImpl> createEntityContainer() throws UnsupportedFilterException {
        final JPAContainer<VaadinFrameImpl> frames = JPAContainerFactory.make(VaadinFrameImpl.class, this.entityProvider.getEntityManager());
        frames.setEntityProvider(this.entityProvider);
        frames.setParentProperty("parent");
        return frames;
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

    @Override
    public void select(Frame frame) {
        super.select(frame.getId());
        Frame parent = frame;
        while (parent instanceof Frame) {
            this.expandItem(parent.getId());
            parent = (Frame) parent.getParent();
        }
    }
}
