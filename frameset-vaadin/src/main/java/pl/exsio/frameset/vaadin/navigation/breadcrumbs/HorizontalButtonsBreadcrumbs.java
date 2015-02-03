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
package pl.exsio.frameset.vaadin.navigation.breadcrumbs;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import pl.exsio.frameset.core.model.Frame;
import static pl.exsio.jin.translationcontext.TranslationContext.t;
import pl.exsio.frameset.navigation.breadcrumbs.builder.BreadcrumbsBuilder;
import pl.exsio.frameset.vaadin.navigation.FrameChangeEvent;
import pl.exsio.frameset.vaadin.navigation.FrameChangeListener;
import pl.exsio.frameset.vaadin.ui.FramesetUI;

/**
 *
 * @author exsio
 */
public class HorizontalButtonsBreadcrumbs extends HorizontalLayout implements Breadcrumbs, FrameChangeListener {
    
    private transient BreadcrumbsBuilder builder;
    
    private Set<Button> buttons;

    public HorizontalButtonsBreadcrumbs() {
        this.setMargin(new MarginInfo(true, false, false, false));
    }

    public void setBreadcrumbsBuilder(BreadcrumbsBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void update(Frame frame) {
        this.buttons = new LinkedHashSet<>();
        this.removeAllComponents();
        Set<Frame> breadcrumbs = this.builder.build(frame);
        Iterator<Frame> i = breadcrumbs.iterator();
        while(i.hasNext()) {
            final Frame breadcrumb = i.next();
            Button b = new Button(t(breadcrumb.getMenuLabel()));
            b.addStyleName("link");
            b.addStyleName("breadcrumb-" + breadcrumb.getKey());
            b.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    ((FramesetUI) getUI()).getFramesetNavigator().navigateTo(breadcrumb, true);
                }  
            });
            this.addComponent(b);
            if(i.hasNext()) {
                this.addComponent(new Label("&nbsp;>&nbsp;", ContentMode.HTML));
            }
            this.buttons.add(b);
        }
    }

    @Override
    public void beforeFrameChange(FrameChangeEvent event) {
    }

    @Override
    public void afterFrameChange(FrameChangeEvent event) {
        this.update(event.getNewFrame());
    }
    
    
    
}
