package com.example.debts

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class tela_RelatorioGastos : AppCompatActivity() {
    lateinit var grafico: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_relatorio_gastos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        grafico = findViewById(R.id.line_chart)
        var lineDataSet1: LineDataSet = LineDataSet(dataValue(), "Data set 1")
        val dataSets: ArrayList<LineDataSet> = arrayListOf()
        dataSets.add(lineDataSet1)

        var data: LineData = LineData(dataSets as List<ILineDataSet>?)
        grafico.data = data
        grafico.invalidate()

    }

    private fun dataValue(): ArrayList<Entry> {
        val dataVals = arrayListOf(Entry())

        dataVals.add(Entry(0f, 20f))
        dataVals.add(Entry(1f, 24f))
        dataVals.add(Entry(2f, 2f))

        return dataVals
    }

}