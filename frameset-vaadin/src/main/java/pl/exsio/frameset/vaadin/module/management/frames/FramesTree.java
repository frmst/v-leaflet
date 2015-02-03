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
package pl.exsio.frameset.vaadin.module.management.frames;

import com.vaadin.addon.jpacontainer.EntityProvider;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeTargetDetails;
import java.util.HashSet;
import java.util.Set;
import pl.exsio.frameset.core.dao.FrameDao;
import pl.exsio.frameset.core.entity.factory.CoreEntityFactory;
import pl.exsio.frameset.core.model.Frame;
import pl.exsio.frameset.core.repository.provider.CoreRepositoryProvider;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.security.context.SecurityContext;
import pl.exsio.frameset.vaadin.component.InitializableVerticalLayout;
import pl.exsio.frameset.vaadin.entity.VaadinFrameImpl;
import pl.exsio.frameset.vaadin.ui.support.dialog.ConfirmationDialog;
import pl.exsio.frameset.vaadin.ui.support.dialog.PromptDialog;
import pl.exsio.nestedj.ex.InvalidNodesHierarchyException;

/**
 *
 * @author exsio
 */
public class FramesTree extends InitializableVerticalLayout {

    private Tree tree;

    private transient EntityProvider entityProvider;

    private transient final SecurityContext security;

    private final transient Set<ItemClickEvent.ItemClickListener> treeClickListeners;

    private transient CoreRepositoryProvider coreRepositories;

    private transient CoreEntityFactory coreEntities;

    public FramesTree(SecurityContext security) {
        this.security = security;
        this.treeClickListeners = new HashSet<>();
        this.setStyleName("frames-management-tree");
    }

    public void setEntityProvider(EntityProvider entityProvider) {
        this.entityProvider = entityProvider;
    }

    @Override
    protected void doInit() {
        this.drawTree();;
    }

    protected void drawTree() {
        this.removeAllComponents();
        JPAContainer<Frame> frames = this.initTree();
        this.addComponent(this.getControls(frames));
        this.addComponent(this.tree);
        this.setMargin(true);
        this.hookTreeListeners();
    }

    private void hookTreeListeners() {
        for (ItemClickEvent.ItemClickListener listener : this.treeClickListeners) {
            this.tree.addItemClickListener(listener);
        }
        this.tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                tree.select(event.getItemId());
            }

        });
    }

    private JPAContainer<Frame> createContainer() {
        final JPAContainer<Frame> frames = new JPAContainer(this.coreEntities.getFrameClass()) {

            @Override
            public boolean areChildrenAllowed(Object itemId) {
                return getChildren(itemId).size() > 0;
            }
        };
        frames.sort(new String[]{"lft"}, new boolean[]{true});
        frames.setEntityProvider(this.entityProvider);
        return frames;
    }

    private JPAContainer<Frame> initTree() {
        final JPAContainer<Frame> frames = this.createContainer();

        frames.setParentProperty("parent");
        this.tree = new Tree("", frames);

        this.tree.setItemCaptionMode(Tree.ItemCaptionMode.EXPLICIT);
        this.tree.setStyleName("frameset-frames-tree");
        for (Object itemId : frames.getItemIds()) {
            this.tree.setItemCaption(itemId, t(frames.getItem(itemId).getEntity().getTitle()));
            this.tree.setItemIcon(itemId, FontAwesome.TABLET);

        }
        if (this.security.canAdminister()) {
            this.tree.setDragMode(Tree.TreeDragMode.NODE);
            this.setTreeDropHandler(frames);
        }
        for (Object rootId : frames.rootItemIds()) {
            this.tree.expandItemsRecursively(rootId);
        }
        return frames;
    }

    private void setTreeDropHandler(final JPAContainer<Frame> frames) {
        this.tree.setDropHandler(new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                Transferable t = event.getTransferable();

                if (t.getSourceComponent() != tree) {
                    return;
                }

                TreeTargetDetails target = (TreeTargetDetails) event.getTargetDetails();

                Object sourceItemId = t.getData("itemId");
                Object targetItemId = target.getItemIdOver();
                Frame sourceFrame = frames.getItem(sourceItemId).getEntity();
                Frame targetFrame = frames.getItem(targetItemId).getEntity();

                VerticalDropLocation location = target.getDropLocation();
                FrameDao<Frame> frameDao = coreRepositories.getFrameRepository();
                try {
                    // Drop right on an item -> make it a child
                    if (location == VerticalDropLocation.MIDDLE) {
                        frameDao.insertAsLastChildOf(sourceFrame, targetFrame);
                    } // Drop at the top of a subtree -> make it previous
                    else if (location == VerticalDropLocation.TOP && !targetFrame.isRoot()) {
                        frameDao.insertAsPrevSiblingOf(sourceFrame, targetFrame);
                    } // Drop below another item -> make it next 
                    else if (location == VerticalDropLocation.BOTTOM && !targetFrame.isRoot()) {
                        frameDao.insertAsNextSiblingOf(sourceFrame, targetFrame);
                    }
                    frames.refresh();
                } catch (InvalidNodesHierarchyException ex) {
                    Notification.show(t("core.management.frames.tree.wrong_operation"), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
    }

    private HorizontalLayout getControls(final JPAContainer<Frame> frames) {

        HorizontalLayout controls = new HorizontalLayout();
        final Button addButton = createAddFrameButton(frames);
        final Button removeButton = createRemoveFrameButton(frames);
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        controls.setMargin(true);
        controls.addComponent(addButton);
        controls.addComponent(removeButton);
        controls.setSpacing(true);
        if (this.security.canCreate()) {
            this.tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    addButton.setEnabled(true);
                }
            });
        }
        if (this.security.canCreate()) {
            this.tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
                @Override
                public void itemClick(ItemClickEvent event) {
                    removeButton.setEnabled(true);
                }
            });
        }
        return controls;
    }

    private Button createRemoveFrameButton(final JPAContainer<Frame> frames) {
        final Button removeButton = new Button(t("core.management.frames.button.remove"), FontAwesome.TIMES);
        removeButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                ConfirmationDialog.show(t("core.management.frames.deletion.confirmation"), new ConfirmationDialog.Handler() {

                    @Override
                    public void handle(Button.ClickEvent event) {
                        Frame targetFrame = frames.getItem(tree.getValue()).getEntity();
                        if (!targetFrame.isRoot()) {
                            FrameDao<Frame> frameDao = coreRepositories.getFrameRepository();
                            frameDao.removeSingle(targetFrame);
                            frames.removeItem(targetFrame.getId());
                            frames.refresh();
                        } else {
                            Notification.show(t("core.management.frames.tree.wrong_operation"), Notification.Type.ERROR_MESSAGE);
                        }
                    }
                }, null);

            }
        });
        return removeButton;
    }

    private Button createAddFrameButton(final JPAContainer<Frame> frames) {
        final Button addButton = new Button(t("core.management.frames.button.add"), FontAwesome.PLUS);
        addButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                PromptDialog.show("addition.prompt", new PromptDialog.Handler() {

                    @Override
                    public void handle(Button.ClickEvent event, String name) {

                        Frame newFrame = this.createFrame(name);
                        Frame targetFrame = frames.getItem(tree.getValue()).getEntity();

                        try {
                            FrameDao<Frame> frameDao = coreRepositories.getFrameRepository();
                            newFrame = frameDao.insertAsLastChildOf(newFrame, targetFrame);
                            drawTree();

                        } catch (InvalidNodesHierarchyException ex) {
                            Notification.show(t("core.management.frames.tree.wrong_operation"), Notification.Type.ERROR_MESSAGE);
                        }

                    }

                    private VaadinFrameImpl createFrame(String name) {

                        VaadinFrameImpl frame = new VaadinFrameImpl();
                        frame.setTitle(name);
                        frame.setMenuLabel(name);
                        frame.setSlug(name.toLowerCase().replaceAll("[^a-z0-9-]", "-"));
                        return frame;
                    }
                });
            }
        });
        return addButton;
    }

    public void addTreeItemClickListener(ItemClickEvent.ItemClickListener listener) {
        this.treeClickListeners.add(listener);
        this.tree.addItemClickListener(listener);
    }

    public void setCoreRepositories(CoreRepositoryProvider coreRepositories) {
        this.coreRepositories = coreRepositories;
    }

    public void setCoreEntities(CoreEntityFactory coreEntities) {
        this.coreEntities = coreEntities;
    }

}
