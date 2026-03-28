package com.example.qlct;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OverviewFragment extends Fragment {

    private DataManager dataManager;
    private NumberFormat nf;

    private TextView tvTotalIncome, tvTotalExpense, tvBalance;
    private TextView tvInsightTopCategory, tvInsightWarning, tvInsightHighestDay, tvInsightTrend;
    private PieChart pieChart;
    private BarChart barChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        dataManager = DataManager.getInstance(requireContext());
        nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvBalance = view.findViewById(R.id.tvBalance);
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);

        tvInsightTopCategory = view.findViewById(R.id.tvInsightTopCategory);
        tvInsightWarning = view.findViewById(R.id.tvInsightWarning);
        tvInsightHighestDay = view.findViewById(R.id.tvInsightHighestDay);
        tvInsightTrend = view.findViewById(R.id.tvInsightTrend);

        setupSummary();
        setupPieChart();
        setupBarChart();
        setupInsights();

        return view;
    }

    private void setupSummary() {
        double income = dataManager.getTotalIncome();
        double expense = dataManager.getTotalExpense();
        double balance = dataManager.getBalance();

        tvTotalIncome.setText("₫" + nf.format(income));
        tvTotalExpense.setText("₫" + nf.format(expense));
        tvBalance.setText("₫" + nf.format(balance));
    }

    private void setupPieChart() {
        List<Transaction> transactions = dataManager.getAllTransactions();
        Map<String, Double> expensesByCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.isExpense()) {
                double current = expensesByCategory.containsKey(t.getCategory()) ? expensesByCategory.get(t.getCategory()) : 0.0;
                expensesByCategory.put(t.getCategory(), current + t.getAmount());
            }
        }

        List<PieEntry> entries = new ArrayList<>();
        int[] colors = {
            Color.parseColor("#4CAF50"), Color.parseColor("#2196F3"),
            Color.parseColor("#FFC107"), Color.parseColor("#FF5722"),
            Color.parseColor("#9C27B0"), Color.parseColor("#00BCD4")
        };

        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Chi Tiêu");
        pieChart.setCenterTextSize(16f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        List<Transaction> transactions = dataManager.getAllTransactions();
        List<String> last7DaysLabels = new ArrayList<>();
        Map<String, Double> last7DaysExpenses = new HashMap<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        
        for (int i = 6; i >= 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, -i);
            String dateLabel = sdf.format(c.getTime());
            last7DaysLabels.add(dateLabel);
            last7DaysExpenses.put(dateLabel, 0.0);
        }

        for (Transaction t : transactions) {
            if (t.isExpense()) {
                String dateLabel = sdf.format(t.getDate());
                if (last7DaysExpenses.containsKey(dateLabel)) {
                    double current = last7DaysExpenses.get(dateLabel);
                    last7DaysExpenses.put(dateLabel, current + t.getAmount());
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < last7DaysLabels.size(); i++) {
            String label = last7DaysLabels.get(i);
            float amount = last7DaysExpenses.get(label).floatValue();
            entries.add(new BarEntry(i, amount));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Chi tiêu");
        dataSet.setColor(Color.parseColor("#E53935")); // Màu giống nút Expense
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(last7DaysLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void setupInsights() {
        List<Transaction> transactions = dataManager.getAllTransactions();
        if (transactions == null || transactions.isEmpty()) {
            return;
        }

        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Double> dailyTotals = new HashMap<>();
        
        double todayExpense = 0;
        double last7DaysExpense = 0;
        double thisWeekExpense = 0;
        double lastWeekExpense = 0;

        Calendar currentCal = Calendar.getInstance();
        int currentYear = currentCal.get(Calendar.YEAR);
        int currentWeek = currentCal.get(Calendar.WEEK_OF_YEAR);
        int currentMonth = currentCal.get(Calendar.MONTH);

        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.clear(Calendar.MINUTE);
        currentCal.clear(Calendar.SECOND);
        currentCal.clear(Calendar.MILLISECOND);
        long todayStart = currentCal.getTimeInMillis();
        long last7DaysStart = todayStart - (7L * 24 * 60 * 60 * 1000);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String highestDayDate = "-";
        double highestDayAmount = 0;

        Calendar tCal = Calendar.getInstance();

        for (Transaction t : transactions) {
            if (t.isExpense() && t.getDate() != null) {
                // 1. Category Totals
                String cat = t.getCategory();
                double catTotal = categoryTotals.containsKey(cat) ? categoryTotals.get(cat) : 0;
                categoryTotals.put(cat, catTotal + t.getAmount());

                tCal.setTime(t.getDate());
                int tYear = tCal.get(Calendar.YEAR);
                int tWeek = tCal.get(Calendar.WEEK_OF_YEAR);
                int tMonth = tCal.get(Calendar.MONTH);
                long tTime = t.getDate().getTime();

                // 3. Daily totals (in current month)
                if (tYear == currentYear && tMonth == currentMonth) {
                    String dateStr = sdf.format(t.getDate());
                    double dayTotal = dailyTotals.containsKey(dateStr) ? dailyTotals.get(dateStr) : 0;
                    dayTotal += t.getAmount();
                    dailyTotals.put(dateStr, dayTotal);
                    if (dayTotal > highestDayAmount) {
                        highestDayAmount = dayTotal;
                        highestDayDate = dateStr;
                    }
                }

                // 2. Warning (today vs last 7 days avg)
                if (tTime >= todayStart) {
                    todayExpense += t.getAmount();
                } else if (tTime >= last7DaysStart && tTime < todayStart) {
                    last7DaysExpense += t.getAmount();
                }

                // 4. Trend (this week vs last week)
                if (tYear == currentYear) {
                    if (tWeek == currentWeek) {
                        thisWeekExpense += t.getAmount();
                    } else if (tWeek == currentWeek - 1) {
                        lastWeekExpense += t.getAmount();
                    }
                }
            }
        }

        // 1. Top Spending Category
        String topCategory = "-";
        double maxCatAmount = 0;
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxCatAmount) {
                maxCatAmount = entry.getValue();
                topCategory = entry.getKey();
            }
        }
        if (maxCatAmount > 0) {
            tvInsightTopCategory.setText("🏆 Bạn chi tiêu nhiều nhất cho: " + topCategory);
        }

        // 2. Warning
        double avgLast7Days = last7DaysExpense / 7.0;
        if (todayExpense > avgLast7Days && todayExpense > 0) {
            tvInsightWarning.setText("⚠️ Bạn đang chi tiêu nhiều hơn bình thường trong hôm nay");
            tvInsightWarning.setTextColor(Color.parseColor("#E53935")); // Red
        } else {
            tvInsightWarning.setText("✅ Chi tiêu hôm nay vẫn trong tầm kiểm soát");
            tvInsightWarning.setTextColor(Color.parseColor("#4CAF50")); // Green
        }

        // 3. Highest Spending Day
        if (highestDayAmount > 0) {
            tvInsightHighestDay.setText("🔥 Bạn chi nhiều nhất vào ngày: " + highestDayDate);
        }

        // 4. Trend
        if (lastWeekExpense > 0 || thisWeekExpense > 0) {
            if (thisWeekExpense > lastWeekExpense) {
                tvInsightTrend.setText("📈 Chi tiêu của bạn tăng trong tuần này");
                tvInsightTrend.setTextColor(Color.parseColor("#E53935")); // Red
            } else {
                tvInsightTrend.setText("📉 Bạn đang tiết kiệm nhiều hơn trong tuần này");
                tvInsightTrend.setTextColor(Color.parseColor("#4CAF50")); // Green
            }
        } else {
            tvInsightTrend.setText("⚖️ Mức chi tiêu duy trì ổn định");
        }
    }
}
