package com.example.OPCS_PART3

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.OPCS_PART3.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private var currentUserId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize database helper and session manager
        dbHelper = DatabaseHelper(requireContext())
        sessionManager = SessionManager(requireContext())

        // Get current user ID
        val user = sessionManager.getUserDetails()
        if (user != null) {
            currentUserId = user.id
        } else {
            Log.e("StatisticsFragment", "No user logged in")
            return
        }

        // Set up period spinner
        setupPeriodSpinner()

        // Set up charts with real data
        setupPieChart()
        setupBarChart()

        // Set up goal progress with real data
        setupGoalProgress()

        // Set up category goals with real data
        setupCategoryGoals()

        // Set up button click listener
        binding.btnApplyPeriod.setOnClickListener {
            val selectedPeriod = binding.spinnerPeriod.selectedItem.toString()
            refreshChartsWithRealData(selectedPeriod)
        }

        Log.d("StatisticsFragment", "Fragment created and initialized with user ID: $currentUserId")
    }

    private fun setupPeriodSpinner() {
        val periods = arrayOf("This Week", "This Month", "Last Month", "Last 3 Months", "Last 6 Months", "This Year")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = adapter
        binding.spinnerPeriod.setSelection(1) // Default to "This Month"
    }

    private fun setupPieChart() {
        // Configure pie chart appearance
        binding.pieChartCategories.apply {
            description.isEnabled = false
            setUsePercentValues(true)
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1400, Easing.EaseInOutQuad)
            legend.isEnabled = true
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
        }

        // Load real data
        loadPieChartDataFromDatabase()
    }

    private fun loadPieChartDataFromDatabase() {
        try {
            // Get category expense summaries from database
            val categoryExpenses = dbHelper.getTotalExpensesByCategory(currentUserId)

            if (categoryExpenses.isEmpty()) {
                // Show empty state
                binding.pieChartCategories.centerText = "No Data\nAvailable"
                binding.pieChartCategories.clear()
                return
            }

            val entries = ArrayList<PieEntry>()
            val colors = ArrayList<Int>()

            for (categoryExpense in categoryExpenses) {
                if (categoryExpense.totalSpent > 0) {
                    entries.add(PieEntry(categoryExpense.totalSpent.toFloat(), categoryExpense.categoryName))

                    // Try to parse the category color, fallback to default colors
                    try {
                        colors.add(Color.parseColor(categoryExpense.categoryColor))
                    } catch (e: Exception) {
                        colors.add(ColorTemplate.MATERIAL_COLORS[colors.size % ColorTemplate.MATERIAL_COLORS.size])
                    }
                }
            }

            if (entries.isEmpty()) {
                binding.pieChartCategories.centerText = "No Expenses\nRecorded"
                binding.pieChartCategories.clear()
                return
            }

            val dataSet = PieDataSet(entries, "Expense Categories")
            dataSet.colors = colors
            dataSet.setDrawIcons(false)
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = com.github.mikephil.charting.utils.MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(binding.pieChartCategories))
            data.setValueTextSize(11f)
            data.setValueTextColor(Color.BLACK)

            binding.pieChartCategories.data = data
            binding.pieChartCategories.highlightValues(null)
            binding.pieChartCategories.invalidate()
            binding.pieChartCategories.centerText = "Spending\nBreakdown"

            Log.d("StatisticsFragment", "Pie chart loaded with ${entries.size} categories")
        } catch (e: Exception) {
            Log.e("StatisticsFragment", "Error loading pie chart data", e)
            binding.pieChartCategories.centerText = "Error Loading\nData"
        }
    }

    private fun setupBarChart() {
        // Configure bar chart appearance
        binding.barChartMonthly.apply {
            description.isEnabled = false
            setMaxVisibleValueCount(6)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)

            val xAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)

            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false

            legend.isEnabled = true

            animateY(1500)
        }

        // Load real data
        loadBarChartDataFromDatabase()
    }

    private fun loadBarChartDataFromDatabase() {
        try {
            val entries = ArrayList<BarEntry>()
            val monthLabels = ArrayList<String>()

            // Get last 6 months data
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

            for (i in 5 downTo 0) {
                calendar.time = Date()
                calendar.add(Calendar.MONTH, -i)

                // Get start and end of month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = dateFormat.format(calendar.time)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = dateFormat.format(calendar.time)

                // Get total expenses for this month
                val monthlyTotal = dbHelper.getTotalExpensesByPeriod(currentUserId, startDate, endDate)

                entries.add(BarEntry((5 - i).toFloat(), monthlyTotal.toFloat()))
                monthLabels.add(monthFormat.format(calendar.time))
            }

            if (entries.all { it.y == 0f }) {
                // No data available
                binding.barChartMonthly.clear()
                return
            }

            val dataSet = BarDataSet(entries, "Monthly Spending")
            dataSet.setColors(Color.parseColor("#9932CC"))

            val data = BarData(dataSet)
            data.setValueTextSize(10f)
            data.barWidth = 0.5f

            binding.barChartMonthly.data = data
            binding.barChartMonthly.xAxis.valueFormatter = IndexAxisValueFormatter(monthLabels)
            binding.barChartMonthly.invalidate()

            Log.d("StatisticsFragment", "Bar chart loaded with 6 months data")
        } catch (e: Exception) {
            Log.e("StatisticsFragment", "Error loading bar chart data", e)
        }
    }

    private fun setupGoalProgress() {
        try {
            // Get current month's budget
            val currentBudget = dbHelper.getBudgetByPeriod(currentUserId, "monthly")

            if (currentBudget != null) {
                // Get current month's spending
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                // Get start of current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = dateFormat.format(calendar.time)

                // Get end of current month
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = dateFormat.format(calendar.time)

                val currentSpending = dbHelper.getTotalExpensesByPeriod(currentUserId, startDate, endDate)

                // Set up progress (assuming min goal is 50% of budget, max is the budget itself)
                val minGoal = currentBudget.amount * 0.5
                val maxGoal = currentBudget.amount

                // Calculate progress percentage
                val progressPercentage = if (maxGoal > minGoal) {
                    ((currentSpending - minGoal) / (maxGoal - minGoal) * 100).toInt().coerceIn(0, 100)
                } else {
                    0
                }

                // Update UI
                binding.tvMinGoal.text = "R${String.format("%.2f", minGoal)}"
                binding.tvMaxGoal.text = "R${String.format("%.2f", maxGoal)}"
                binding.tvCurrentSpending.text = "R${String.format("%.2f", currentSpending)}"
                binding.progressGoal.progress = progressPercentage

                Log.d("StatisticsFragment", "Goal Progress: $progressPercentage%, Spending: $currentSpending, Budget: ${currentBudget.amount}")
            } else {
                // No budget set
                binding.tvMinGoal.text = "R0.00"
                binding.tvMaxGoal.text = "No Budget Set"
                binding.tvCurrentSpending.text = "R${String.format("%.2f", dbHelper.getTotalExpensesByUser(currentUserId))}"
                binding.progressGoal.progress = 0

                Log.d("StatisticsFragment", "No budget set for user")
            }
        } catch (e: Exception) {
            Log.e("StatisticsFragment", "Error setting up goal progress", e)
        }
    }

    private fun setupCategoryGoals() {
        try {
            // Get categories with their budgets and spending
            val categoryBudgets = dbHelper.getTotalExpensesByCategory(currentUserId)

            if (categoryBudgets.isNotEmpty()) {
                // Set up RecyclerView with CategoryBudgetAdapter
                val adapter = CategoryBudgetAdapter(categoryBudgets.map {
                    CategorySummary(
                        id = it.categoryId,
                        name = it.categoryName,
                        totalSpent = it.totalSpent,
                        budget = it.budget
                    )
                })

                binding.rvCategoryGoals.layoutManager = LinearLayoutManager(requireContext())
                binding.rvCategoryGoals.adapter = adapter

                Log.d("StatisticsFragment", "Category goals set up with ${categoryBudgets.size} categories")
            }
        } catch (e: Exception) {
            Log.e("StatisticsFragment", "Error setting up category goals", e)
        }
    }

    private fun refreshChartsWithRealData(period: String) {
        try {
            Log.d("StatisticsFragment", "Refreshing charts for period: $period")

            // Refresh all charts with real data
            loadPieChartDataFromDatabase()
            loadBarChartDataFromDatabase()
            setupGoalProgress()
            setupCategoryGoals()

            Toast.makeText(requireContext(), "Charts updated for $period", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("StatisticsFragment", "Error refreshing charts", e)
            Toast.makeText(requireContext(), "Error updating charts", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        if (currentUserId != -1L) {
            refreshChartsWithRealData(binding.spinnerPeriod.selectedItem.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}