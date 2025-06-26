package com.example.OPCS_PART3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.OPCS_PART3.databinding.FragmentGamificationBinding
import java.text.SimpleDateFormat
import java.util.*

class GamificationFragment : Fragment() {

    private var _binding: FragmentGamificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var badgeAdapter: BadgeAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private var currentUserId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGamificationBinding.inflate(inflater, container, false)
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
            Log.e("GamificationFragment", "No user logged in")
            return
        }

        // Set up user level and XP with real data
        setupUserLevel()

        // Set up stats with real data
        setupUserStats()

        // Set up badges with real achievement system
        setupBadges()

        Log.d("GamificationFragment", "Fragment created and initialized with user ID: $currentUserId")
    }

    private fun setupUserLevel() {
        try {
            // Calculate XP based on user activity
            val totalExpenses = dbHelper.getAllExpenses(currentUserId).size
            val totalCategories = dbHelper.getAllCategories(currentUserId).size
            val totalIncome = dbHelper.getIncomeByUser(currentUserId).size

            // XP calculation: 10 XP per expense, 50 XP per category, 25 XP per income entry
            val currentXp = (totalExpenses * 10) + (totalCategories * 50) + (totalIncome * 25)

            // Calculate level based on XP (every 1000 XP = 1 level)
            val userLevel = (currentXp / 1000) + 1
            val xpInCurrentLevel = currentXp % 1000
            val maxXpForLevel = 1000

            // Determine level name based on level
            val levelName = when {
                userLevel >= 10 -> "Financial Guru"
                userLevel >= 8 -> "Budget Master"
                userLevel >= 6 -> "Expense Expert"
                userLevel >= 4 -> "Money Manager"
                userLevel >= 2 -> "Budget Tracker"
                else -> "Beginner"
            }

            // Calculate progress percentage for current level
            val progressPercentage = (xpInCurrentLevel.toFloat() / maxXpForLevel.toFloat() * 100).toInt()

            // Update UI
            binding.tvUserLevel.text = "LEVEL $userLevel"
            binding.tvLevelName.text = levelName
            binding.tvXpProgress.text = "$xpInCurrentLevel/$maxXpForLevel XP"
            binding.progressXp.progress = progressPercentage

            Log.d("GamificationFragment", "User Level: $userLevel, XP: $currentXp, Progress: $progressPercentage%")
        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error setting up user level", e)
            // Fallback to default values
            binding.tvUserLevel.text = "LEVEL 1"
            binding.tvLevelName.text = "Beginner"
            binding.tvXpProgress.text = "0/1000 XP"
            binding.progressXp.progress = 0
        }
    }

    private fun setupUserStats() {
        try {
            // Calculate days streak (consecutive days with expenses logged)
            val daysStreak = calculateDaysStreak()

            // Calculate goals met (budgets that were not exceeded)
            val goalsMet = calculateGoalsMet()

            // Calculate total badges earned
            val badgesCount = calculateEarnedBadges().size

            // Update UI
            binding.tvDaysStreak.text = daysStreak.toString()
            binding.tvGoalsMet.text = goalsMet.toString()
            binding.tvBadgesCount.text = badgesCount.toString()

            Log.d("GamificationFragment", "Days Streak: $daysStreak, Goals Met: $goalsMet, Badges: $badgesCount")
        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error setting up user stats", e)
            // Fallback to default values
            binding.tvDaysStreak.text = "0"
            binding.tvGoalsMet.text = "0"
            binding.tvBadgesCount.text = "0"
        }
    }

    private fun calculateDaysStreak(): Int {
        try {
            val expenses = dbHelper.getAllExpenses(currentUserId)
            if (expenses.isEmpty()) return 0

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = Calendar.getInstance()

            // Get unique dates with expenses
            val expenseDates = expenses.map {
                try {
                    dateFormat.parse(it.date)
                } catch (e: Exception) {
                    null
                }
            }.filterNotNull().map { date ->
                val cal = Calendar.getInstance()
                cal.time = date
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.time
            }.toSet().sortedDescending()

            if (expenseDates.isEmpty()) return 0

            // Check for consecutive days starting from today
            var streak = 0
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            for (i in 0 until 30) { // Check last 30 days max
                val currentDate = calendar.time
                if (expenseDates.contains(currentDate)) {
                    streak++
                } else if (i > 0) { // Allow for today to not have expenses yet
                    break
                }
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            }

            return streak
        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error calculating days streak", e)
            return 0
        }
    }

    private fun calculateGoalsMet(): Int {
        try {
            val categories = dbHelper.getAllCategories(currentUserId)
            var goalsMet = 0

            for (category in categories) {
                if (category.budget > 0) {
                    val categoryExpenses = dbHelper.getTotalExpensesByCategory(currentUserId)
                        .find { it.categoryId == category.id }

                    if (categoryExpenses != null && categoryExpenses.totalSpent <= category.budget) {
                        goalsMet++
                    }
                }
            }

            // Also check overall budget goals
            val monthlyBudget = dbHelper.getBudgetByPeriod(currentUserId, "monthly")
            if (monthlyBudget != null) {
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = dateFormat.format(calendar.time)

                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = dateFormat.format(calendar.time)

                val monthlySpending = dbHelper.getTotalExpensesByPeriod(currentUserId, startDate, endDate)

                if (monthlySpending <= monthlyBudget.amount) {
                    goalsMet++
                }
            }

            return goalsMet
        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error calculating goals met", e)
            return 0
        }
    }

    private fun calculateEarnedBadges(): List<Badge> {
        val earnedBadges = mutableListOf<Badge>()

        try {
            val expenses = dbHelper.getAllExpenses(currentUserId)
            val categories = dbHelper.getAllCategories(currentUserId)
            val totalSpent = dbHelper.getTotalExpensesByUser(currentUserId)
            val daysStreak = calculateDaysStreak()

            // Badge 1: First Expense
            if (expenses.isNotEmpty()) {
                earnedBadges.add(Badge(1, "First Step", "Log your first expense", true))
            }

            // Badge 2: Category Creator
            if (categories.size >= 3) {
                earnedBadges.add(Badge(2, "Category Creator", "Create 3 or more categories", true))
            }

            // Badge 3: Consistent Tracker
            if (daysStreak >= 7) {
                earnedBadges.add(Badge(3, "Consistent Tracker", "Log expenses for 7 consecutive days", true))
            }

            // Badge 4: Budget Setter
            val budgets = listOf(
                dbHelper.getBudgetByPeriod(currentUserId, "monthly"),
                dbHelper.getBudgetByPeriod(currentUserId, "weekly")
            ).filterNotNull()

            if (budgets.isNotEmpty()) {
                earnedBadges.add(Badge(4, "Budget Setter", "Set your first budget", true))
            }

            // Badge 5: Expense Expert
            if (expenses.size >= 50) {
                earnedBadges.add(Badge(5, "Expense Expert", "Log 50 expenses", true))
            }

            // Badge 6: Category Master
            if (categories.size >= 10) {
                earnedBadges.add(Badge(6, "Category Master", "Create 10 categories", true))
            }

            // Badge 7: Streak Master
            if (daysStreak >= 30) {
                earnedBadges.add(Badge(7, "Streak Master", "30-day tracking streak", true))
            }

            // Badge 8: Big Spender (spent over R10,000)
            if (totalSpent >= 10000) {
                earnedBadges.add(Badge(8, "Big Spender", "Spend over R10,000", true))
            }

        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error calculating earned badges", e)
        }

        return earnedBadges
    }

    private fun createAllBadges(): List<Badge> {
        val earnedBadges = calculateEarnedBadges()
        val earnedBadgeIds = earnedBadges.map { it.id }.toSet()

        return listOf(
            Badge(1, "First Step", "Log your first expense", earnedBadgeIds.contains(1)),
            Badge(2, "Category Creator", "Create 3 or more categories", earnedBadgeIds.contains(2)),
            Badge(3, "Consistent Tracker", "Log expenses for 7 consecutive days", earnedBadgeIds.contains(3)),
            Badge(4, "Budget Setter", "Set your first budget", earnedBadgeIds.contains(4)),
            Badge(5, "Expense Expert", "Log 50 expenses", earnedBadgeIds.contains(5)),
            Badge(6, "Category Master", "Create 10 categories", earnedBadgeIds.contains(6)),
            Badge(7, "Streak Master", "30-day tracking streak", earnedBadgeIds.contains(7)),
            Badge(8, "Big Spender", "Spend over R10,000", earnedBadgeIds.contains(8)),
            Badge(9, "Goal Achiever", "Meet 5 budget goals", false), // Future implementation
            Badge(10, "Financial Guru", "Use all app features for 30 days", false) // Future implementation
        )
    }

    private fun setupBadges() {
        try {
            // Create badges with real achievement data
            val badges = createAllBadges()

            // Set up RecyclerView
            badgeAdapter = BadgeAdapter(badges)
            binding.rvBadges.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = badgeAdapter
            }

            Log.d("GamificationFragment", "Badges set up: ${badges.count { it.isUnlocked }} earned out of ${badges.size} total")
        } catch (e: Exception) {
            Log.e("GamificationFragment", "Error setting up badges", e)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible
        if (currentUserId != -1L) {
            setupUserLevel()
            setupUserStats()
            setupBadges()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}