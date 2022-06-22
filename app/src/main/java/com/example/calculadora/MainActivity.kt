package com.example.calculadora

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import java.math.BigDecimal
import java.math.RoundingMode


class MainActivity : AppCompatActivity() {

    private val operations = listOf("x", "÷", "+", "-")
    private var operationFree = true
    private var pointFree = true
    private var count = 0
    private var interAd : InterstitialAd? = null
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var equation : TextView
    private lateinit var result : TextView
    private lateinit var drawerLayout : DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListeners()
        startAds()

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_rate_us -> rateUsButtonListener()
                R.id.nav_share -> Toast.makeText(applicationContext, "Share", Toast.LENGTH_SHORT).show()
                R.id.nav_other_apps -> Toast.makeText(applicationContext, "Other Apps", Toast.LENGTH_SHORT).show()
                R.id.nav_about_us -> aboutUsButtonListener()
            }
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT)
        } else {
            super.onBackPressed()
        }
    }

    private fun rateUsButtonListener(){
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.android.chrome"))) //$packageName
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.android.chrome")))
            }
    }

    private fun aboutUsButtonListener(){
        var dialog = PrivacyPolicyDialogFragment()
        dialog.show(supportFragmentManager, "custom dialog")
    }

    /** Para que cargue un inter muchas veces, habría que separar los iniciadores y llamar a startInter() cada vez que
        el count sea 0. */
    private fun startAds() {
        val adBanner = findViewById<AdView>(R.id.banner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/8691691433", adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(interstitialAd: LoadAdError) {
                interAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interAd = interstitialAd
            }

        })
    }

    // Funciona como el onClickListener para el drawer navigation bar. NO funciona si se desliza, sólo cuando se clickea.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        count ++
        Toast.makeText(applicationContext, "Clicked $count times.", Toast.LENGTH_SHORT).show()
        checkCount()
        return toggle.onOptionsItemSelected(item)
    }

    private fun checkCount() {
        if (count == 5){
            showInterAd()
            count = 0
        }
    }

    private fun showInterAd(){
        interAd?.show(this)
    }

    private fun operatorOnClickListener(btn: Button, subtractButton: Button){
        if (operationFree) {
            if (equation.text == "") {
                if ((btn != subtractButton) && (result.text == "")) return
                equation.text = result.text
            }
            concatenateNumbers(btn.text.toString())
            operationFree = false
            pointFree = true
        }
    }

    private fun numberButtonOnClickListener(btn: Button, pointButton: Button) {
        concatenateNumbers(btn.text.toString())
        if (btn == pointButton) {
            operationFree = false
        }
    }

    private fun pointButtonOnClickListener(btn: Button){
        if (pointFree) {
            concatenateNumbers(btn.text.toString())
            operationFree = false
            pointFree = false
        }
    }

    private fun clearButtonOnClickListener(){
        cleanEquation()
        pointFree = true
        operationFree = true
    }

    private fun equalsButtonOnClickListener(){
        if (operationFree && equation.text != ""){
            solveEquation()
        }
        pointFree = true
    }

    private fun deleteButtonOnClickListener(){
        if (equation.text.isEmpty()){
            return//@setOnClickListener
        }
        val nuevaEcuacion = equation.text.dropLast(1)
        if (nuevaEcuacion.isEmpty()){
            equation.text = ""
            return//@setOnClickListener
        }
        val lastCharacter = nuevaEcuacion.last().toString()
        val caracterBorrado = equation.text[equation.text.length-1].toString()
        if (caracterBorrado in operations) operationFree = true
        else if (caracterBorrado == ".") {
            pointFree = true
            operationFree = true
        }
        else if (lastCharacter in operations) operationFree = false
        else if (lastCharacter == ".") operationFree = false
        equation.text = nuevaEcuacion
    }

    private fun setListeners(){
        val zeroButton = findViewById<Button>(R.id.zero_button)
        val oneButton = findViewById<Button>(R.id.one_button)
        val twoButton = findViewById<Button>(R.id.two_button)
        val threeButton = findViewById<Button>(R.id.three_button)
        val fourButton = findViewById<Button>(R.id.four_button)
        val fiveButton = findViewById<Button>(R.id.five_button)
        val sixButton = findViewById<Button>(R.id.six_button)
        val sevenButton = findViewById<Button>(R.id.seven_button)
        val eightButton = findViewById<Button>(R.id.eight_button)
        val nineButton = findViewById<Button>(R.id.nine_button)
        val clearButton = findViewById<Button>(R.id.clear_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)
        val equalsButton = findViewById<Button>(R.id.equals_button)
        val pointButton = findViewById<Button>(R.id.point_button)
        val divideButton = findViewById<Button>(R.id.divide_button)
        val multiplyButton = findViewById<Button>(R.id.multiply_button)
        val additionButton = findViewById<Button>(R.id.addition_button)
        val subtractButton = findViewById<Button>(R.id.subtract_button)

        result = findViewById(R.id.result_view)
        equation = findViewById(R.id.equation_view)

        val numberButtons = listOf<Button>(zeroButton, oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
            sevenButton, eightButton, nineButton)
        val operationButtons = listOf<Button>(divideButton, multiplyButton, additionButton, subtractButton)

        operationButtons.forEach { btn -> btn.setOnClickListener{operatorOnClickListener(btn, subtractButton)} }
        numberButtons.forEach { btn -> btn.setOnClickListener{numberButtonOnClickListener(btn, pointButton)} }
        pointButton.setOnClickListener { pointButtonOnClickListener(pointButton) }
        clearButton.setOnClickListener { clearButtonOnClickListener() }
        equalsButton.setOnClickListener { equalsButtonOnClickListener() }
        deleteButton.setOnClickListener { deleteButtonOnClickListener() }
    }



    @SuppressLint("SetTextI18n")
    private fun concatenateNumbers(digit:String){
        equation.text = "${equation.text}${digit}"
        operationFree = true
    }

    private fun cleanEquation(){
        equation.text = ""
        result.text = ""
    }

    private fun solveEquation(){
        val initialInputList = equation.text.split("").toMutableList()
        initialInputList.removeAt(0)
        initialInputList.removeAt(initialInputList.size - 1)
        // initialInputList = [1, 2, 3, +, 4, 5, 6]
        val initialInputParsedList = mutableListOf<String>()
        var counter = 1
        var number = ""
        for (i in initialInputList.indices){
            if ((initialInputList[i] in operations) && (i != 0)){
                initialInputParsedList.add(initialInputList[i])
                continue
            }
            if ((i == initialInputList.size - 1) || (initialInputList[i + 1] in operations)){
                for (j in i-counter+1..i){
                    number = "$number${initialInputList[j]}"
                }
                initialInputParsedList.add(number)
                number = ""
                counter = 1
            } else {
                counter ++
            }
        }
        // initialInputParsedList = [123, +, 456]
        val operatorsStack = ArrayDeque<String>()
        val orderedEquationList = mutableListOf<String>()

        for (i in initialInputParsedList.indices){
            if (initialInputParsedList[i] in operations){
                if (operatorsStack.isEmpty()){
                    operatorsStack.add(initialInputParsedList[i])
                } else {
                    val orderNewOperator = operatorOrder(initialInputParsedList[i])
                    val orderTopOperator = operatorOrder(operatorsStack.last())
                    if (orderTopOperator <  orderNewOperator){
                        operatorsStack.add(initialInputParsedList[i])
                    } else {
                        sortOperators(initialInputParsedList[i], operatorsStack, orderedEquationList)
                    }
                }
            } else {
                orderedEquationList.add(initialInputParsedList[i])
            }
        }
        while (!operatorsStack.isEmpty()){
            orderedEquationList.add(operatorsStack.removeLast())
        }
        // orderedEquationList = [10, 2, 3, *, +]
        val resultList = mutableListOf<String>()
        lateinit var n1 : BigDecimal
        lateinit var n2: BigDecimal
        var lastPos : Int
        for (i in orderedEquationList.indices){
            if (orderedEquationList[i] !in operations){
                resultList.add(orderedEquationList[i])
            } else {
                lastPos = resultList.size - 1
                n1 = resultList[lastPos - 1].toBigDecimal()
                n2 = resultList[lastPos].toBigDecimal()
                resultList[lastPos-1] = makeOperation(n1, n2, orderedEquationList[i])
                resultList.removeAt(lastPos)
            }
        }
        val res = resultList[0]
        result.text = res
        equation.text = ""
    }

    private fun sortOperators(newOperator:String, operatorsStack:ArrayDeque<String>, resultList:MutableList<String>){
        var orderTopOperator = operatorOrder(operatorsStack.last())
        val orderNewOperator = operatorOrder(newOperator)
        while (orderTopOperator >= orderNewOperator){
            resultList.add(operatorsStack.removeLast())
            if (operatorsStack.isEmpty()){
                operatorsStack.add(newOperator)
                return
            }
            orderTopOperator = operatorOrder(operatorsStack.last())
        }
    }

    private fun makeOperation(n1:BigDecimal, n2:BigDecimal, operator:String): String {
        lateinit var res: BigDecimal
        val zeroBigDecimal = BigDecimal(0)
        when(operator) {
            "+" -> res = n1.add(n2)
            "-" -> res = n1.subtract(n2)
            "x" -> res = n1.multiply(n2)
            "÷" -> res = if (n2 == zeroBigDecimal) zeroBigDecimal else n1.divide(n2, 10, RoundingMode.HALF_UP)
        }
        return removeLeftZeros(removeRightZeros(res.toString()))
    }

    private fun removeLeftZeros(num:String) : String{
        val pointIndex = num.indexOf(".")
        if (pointIndex == -1) return num
        var counter = 0
        for (i in 0..pointIndex){
            if (num[i] != '0'){
                break
            }
            counter = i
        }
        return num.removeRange(0 until counter)
    }

    private fun removeRightZeros(num:String) : String {
        val pointIndex = num.indexOf(".")
        if (pointIndex == -1) return num
        val len = num.length
        var counter = 0
        for (i in len-1 downTo pointIndex){
            if (num[i] != '0'){
                break
            }
            counter++
        }
        if (counter == 0) return num
        if (num[pointIndex + 1] != '0') counter --
        return num.removeRange(len-counter-1 until len)
    }

    private fun operatorOrder(operator: String): Int {
        when (operator){
            "-" -> return 0
            "+" -> return 0
            "÷" -> return 1
            "x" -> return 2
        }
        return -1
    }
}
