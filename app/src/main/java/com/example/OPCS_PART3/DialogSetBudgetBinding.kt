package com.example.OPCS_PART3


import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class DialogSetBudgetBinding private constructor(val root: View) {
    val etBudgetAmount: EditText = root.findViewById(R.id.etBudgetAmount)
    val spinnerPeriod: Spinner = root.findViewById(R.id.spinnerPeriod)
    val btnCancel: Button = root.findViewById(R.id.btnCancel)
    val btnSave: Button = root.findViewById(R.id.btnSave)

    companion object {
        fun inflate(layoutInflater: LayoutInflater): DialogSetBudgetBinding {
            val root = layoutInflater.inflate(R.layout.dialog_set_budget, null)
            return DialogSetBudgetBinding(root)
        }
    }
}