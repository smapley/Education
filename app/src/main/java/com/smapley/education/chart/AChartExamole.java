package com.smapley.education.chart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.smapley.education.R;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class AChartExamole {

    private double[] values = new double[6];

    private Context context;

    /**
     * @param value
     */
    public AChartExamole(double value[]) {
        for (int i = 0; i < value.length; i++) {
            values[i] = value[i];
        }
    }

    /**
     * @return
     */
    public Intent ececute(Context context) {
        this.context = context;
        int[] colors = new int[]{Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.YELLOW, Color.GREEN};
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        CategorySeries categorySeries = new CategorySeries("Vehicles Chart");
        categorySeries.add(context.getString(R.string.gradecontent_item6)+" ", values[0]);
        categorySeries.add(context.getString(R.string.gradecontent_item7)+" ", values[1]);
        categorySeries.add(context.getString(R.string.gradecontent_item8)+" ", values[2]);
        categorySeries.add(context.getString(R.string.gradecontent_item9)+" ", values[3]);
        categorySeries.add(context.getString(R.string.gradecontent_item10)+" ", values[4]);
        categorySeries.add(context.getString(R.string.gradecontent_item11)+" ", values[5]);
        return ChartFactory.getPieChartIntent(context, categorySeries, renderer, "traffic");

    }


    private DefaultRenderer buildCategoryRenderer(int[] colors) {
        // TODO Auto-generated method stub
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setBackgroundColor(Color.GRAY);
        renderer.setApplyBackgroundColor(true);
        renderer.setChartTitle(context.getString(R.string.gradecontent_dialog2));
        renderer.setChartTitleTextSize(80);
        renderer.setLegendTextSize(50);
        renderer.setLegendHeight(200);
        renderer.setLabelsTextSize(40);
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }

        return renderer;
    }
}
