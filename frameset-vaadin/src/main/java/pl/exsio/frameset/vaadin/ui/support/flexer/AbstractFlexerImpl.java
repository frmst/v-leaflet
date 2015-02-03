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
import java.util.NavigableMap;
import java.util.TreeMap;


public abstract class AbstractFlexerImpl implements Flexer {

    protected final NavigableMap<Integer, Double> constraintsMap;
    
    public AbstractFlexerImpl() {
        this.constraintsMap = new TreeMap<>();
    }
    
    @Override
    public Flexer addConstraint(int size, double expandRatio) {
        this.constraintsMap.put(size, expandRatio);
        return this;
    }

    @Override
    public Flexer removeConstraint(int size) {
        this.constraintsMap.remove(size);
        return this;
    }

    @Override
    public void attach() {
        Page.getCurrent().addBrowserWindowResizeListener(this);
    }

    @Override
    public void detach() {
        Page.getCurrent().removeBrowserWindowResizeListener(this);
    }
    
    protected float getClosestExpandRatio(int size) {
        if(this.constraintsMap.size() > 0) {
            int nearestSize = 0;
            for(int constraintSize: this.constraintsMap.navigableKeySet()) {
                if(size > constraintSize) {
                    nearestSize = constraintSize;
                }
            }
            if(this.constraintsMap.containsKey(nearestSize)) {
                return this.constraintsMap.get(nearestSize).floatValue();
            } else {
                return this.constraintsMap.get(this.constraintsMap.navigableKeySet().first()).floatValue();
            }
        } else {
            return 1;
        }
    }
    
}
