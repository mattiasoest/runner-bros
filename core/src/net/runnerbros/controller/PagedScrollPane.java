package net.runnerbros.controller;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * Created by mattiasosth on 19/07/2014.
 */
public class PagedScrollPane extends ScrollPane {

    private boolean wasPanDragFling = false;

    private Table content;


    public PagedScrollPane(Actor widget) {
        super(widget);
        setup();
        this.setScrollingDisabled(false, true);
    }

    public PagedScrollPane() {
        super(null);
        setup();
        this.setScrollingDisabled(false, true);
    }

    private void setup() {
        content = new Table();
        content.defaults().space(50);
        super.setWidget(content);
    }

    public void addPages(Actor... pages) {
        for (Actor page : pages) {
            addPage(page);
        }
    }

    public void addPage(Actor page) {
        content.add(page).expandY().fillY();
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        if (wasPanDragFling && !isPanning() && !isDragging() && !isFlinging()) {
            wasPanDragFling = false;
            scrollToPage();
        }
        else {
            if (isPanning() || isDragging() || isFlinging()) {
                wasPanDragFling = true;
            }
        }
    }

//    @Override
//    public void setWidget(Actor widget) {
//        throw new UnsupportedOperationException("Use PagedScrollPane#addPage");
////        addPage(widget);
//    }


    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        if (content != null) {
            for (Cell cell : content.getCells()) {
                cell.width(width);
            }
            content.invalidate();
        }
    }

    public void setPageSpacing(float pageSpacing) {
        if (content != null) {
            content.defaults().space(pageSpacing);
            for (Cell cell : content.getCells()) {
                cell.space(pageSpacing);
            }
            content.invalidate();
        }
    }

    private void scrollToPage() {
        final float width = getWidth();
        final float scrollX = getScrollX();
        final float maxX = getMaxX();

        if (scrollX >= maxX || scrollX <= 0) {
            return;
        }

        Array<Actor> pages = content.getChildren();
        float pageX = 0;
        float pageWidth = 0;
        if (pages.size > 0) {
            for (Actor a : pages) {
                pageX = a.getX();
                pageWidth = a.getWidth();
                 if (scrollX < (pageX + pageWidth * 0.5f)) {
                    break;
                }
            }
            setScrollX(MathUtils.clamp(pageX - (width - pageWidth) / 2, 0, maxX));
        }
    }
}
