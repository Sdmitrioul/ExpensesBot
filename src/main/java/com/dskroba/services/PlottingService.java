package com.dskroba.services;

import com.dskroba.base.Utils;
import com.dskroba.base.exception.CustomException;
import com.dskroba.type.Currency;
import org.knowm.xchart.*;
import org.knowm.xchart.style.CategoryStyler;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class PlottingService {
    public byte[] pieChart(String plotName, Map<String, Double> data, Currency currency) {
        PieChart chart = new PieChartBuilder()
                .width(800)
                .height(600)
                .title(plotName)
                .build();
        PieStyler styler = chart.getStyler();

        Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        Font monoFontBold = new Font(Font.MONOSPACED, Font.BOLD, 14);

        styler.setLegendVisible(true);
        styler.setLegendPosition(Styler.LegendPosition.OutsideE);
        styler.setPlotContentSize(0.7);
        styler.setChartTitleFont(monoFontBold);
        styler.setLegendFont(monoFont);

        Map<String, String> labels = Utils.createLabels(data, currency);
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            String label = labels.get(entry.getKey());
            chart.addSeries(label, entry.getValue());
        }

        try {
            return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            throw new CustomException("Unable to create pie chart.", e);
        }
    }

    public byte[] barChart(String plotName, Map<String, Double> dataMonthBefore, Map<String, Double> dataCurrentMonth, Currency currency) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(1000)
                .height(600)
                .title(plotName)
                .xAxisTitle("Expense Category")
                .yAxisTitle("Amount " + currency.value())
                .build();

        CategoryStyler styler = chart.getStyler();
        styler.setLegendPosition(Styler.LegendPosition.InsideNW);
        styler.setPlotGridVerticalLinesVisible(false);
        styler.setAxisTitlesVisible(true);
        styler.setDecimalPattern("#0.00");
        styler.setOverlapped(false);

        Set<String> allCategories = new LinkedHashSet<>();
        allCategories.addAll(dataMonthBefore.keySet());
        allCategories.addAll(dataCurrentMonth.keySet());

        List<String> categories = new ArrayList<>(allCategories);
        List<Double> previousMonthValues = new ArrayList<>();
        List<Double> currentMonthValues = new ArrayList<>();

        for (String category : categories) {
            previousMonthValues.add(dataMonthBefore.getOrDefault(category, 0.0));
            currentMonthValues.add(dataCurrentMonth.getOrDefault(category, 0.0));
        }

        chart.addSeries("Previous month", categories, previousMonthValues);
        chart.addSeries("Current month", categories, currentMonthValues);

        try {
            return BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            throw new CustomException("Unable to create bar chart.", e);
        }
    }
}
