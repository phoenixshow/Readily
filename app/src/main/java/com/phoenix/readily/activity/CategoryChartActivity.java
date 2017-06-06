package com.phoenix.readily.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.phoenix.readily.R;
import com.phoenix.readily.activity.base.FrameActivity;
import com.phoenix.readily.entity.CategoryTotal;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.List;

import static jxl.format.PaperSize.C;
import static jxl.format.PaperSize.D;

public class CategoryChartActivity extends FrameActivity {
    private List<CategoryTotal> categoryTotalList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariable();
        View pie_view = categoryStatistics();
        appendMainBody(pie_view);
        removeBottomBox();
        setTitle();
    }

    private void initVariable() {
        categoryTotalList = (List<CategoryTotal>) getIntent().
                getSerializableExtra("total");
    }

    private void setTitle(){
        setTopBarTitle(getString(R.string.category_total));
    }

    private View categoryStatistics(){
        int[] color = new int[]{Color.parseColor("#FF5552"),
                Color.parseColor("#2A94F1"),
                Color.parseColor("#F12792"),
                Color.parseColor("#FFFF52"),
                Color.parseColor("#84D911"),
                Color.parseColor("#5255FF")};
        //得到一个渲染器
        DefaultRenderer defaultRenderer = buildCategoryRederer(color);
        //获取数据源//参数一：标题，不会显示，柱状图和折线图才会显示
        CategorySeries categorySeries = buildCategoryDataset("测试饼图",
                categoryTotalList);
        //获取一个饼视图//参数二：数据源，参数三：渲染器
        View pie_view = ChartFactory.getPieChartView(this, categorySeries,
                defaultRenderer);
        return pie_view;

    }

    //构建数据源
    private CategorySeries buildCategoryDataset(String title,
                         List<CategoryTotal> values) {
        CategorySeries categorySeries = new CategorySeries(title);
        for (CategoryTotal value : values) {
            //参数一：每个瓣的标题，二：实际的数据，瓣的大小
            categorySeries.add(value.categoryName+": "+
                value.count+"条\r\n合计: "+value.sumAmount+"元",
                    Double.parseDouble(value.sumAmount));
        }
        return categorySeries;
    }

    //构建渲染器
    private DefaultRenderer buildCategoryRederer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        //显示缩放按钮，默认不显示
        renderer.setZoomButtonsVisible(true);
        //设置图表标题的文字大小
        renderer.setChartTitleTextSize(30);
        //设置图表的标题，居中顶部显示
        renderer.setChartTitle("消费类别统计");
        //设置标签文字大小
        renderer.setLabelsTextSize(15);
        //设置介绍说明文字的大小
        renderer.setLegendTextSize(15);
        //设置标签文字颜色
        renderer.setLabelsColor(Color.BLUE);

        int color = 0;
        //遍历统计数组
        for (int i = 0; i < categoryTotalList.size(); i++) {
            //创建一个序列渲染器（每一个数据就生成一个扇瓣）
            SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
            //设置扇瓣颜色
            ssr.setColor(colors[color]);
            //把扇瓣添加到渲染器中
            renderer.addSeriesRenderer(ssr);
            color++;
            if (color >= colors.length){
                color = 0;
            }
        }
        return renderer;
    }
}
