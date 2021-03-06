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
package pl.exsio.frameset.vaadin.ui.support.flexer;

import com.vaadin.server.Page;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;

public abstract class AbstractOrderedLayoutFlexerImpl extends AbstractFlexerImpl {

    protected final AbstractOrderedLayout container;

    protected final Component subject;

    public AbstractOrderedLayoutFlexerImpl(AbstractOrderedLayout container, Component subject) {
        this.container = container;
        this.subject = subject;
    }

    @Override
    public void attach() {
        super.attach();
        this.setSubjectExpandRatio(this.getSize());

    }

    @Override
    public void browserWindowResized(Page.BrowserWindowResizeEvent event) {
        this.setSubjectExpandRatio(this.getSize(event));
    }

    protected void setSubjectExpandRatio(int size) {
        this.container.setExpandRatio(this.subject, this.getClosestExpandRatio(size));
    }

    protected abstract int getSize();

    protected abstract int getSize(Page.BrowserWindowResizeEvent event);

}
