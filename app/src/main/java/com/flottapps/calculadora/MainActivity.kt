package com.flottapps.calculadora

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.TypedValue
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.flottapps.calculadora.Calculator.Companion.prefs
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayDeque

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private val ADDITION = "+"
    private val SUBTRACT = "-"
    private val MULTIPLY = "×"
    private val DIVISION = "÷"
    private val operations = listOf(ADDITION, SUBTRACT, MULTIPLY, DIVISION) //listOf("×", "÷", "+", "-")
    private var operationFree = true
    private var pointFree = true
    private var vibration = prefs.getVibrationConfig()
    private var sound = prefs.getSoundConfig()
    private var reading = prefs.getReadingConfig()
    private var darkMode = prefs.getDarkModeConfig()

    private lateinit var equation : TextView
    private lateinit var result : TextView
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var mediaPlayer : MediaPlayer
    private lateinit var tts : TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        loadAppTheme()
        loadLocate()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)
        tts.speak(".", TextToSpeech.QUEUE_FLUSH, null, "")
        mediaPlayer = MediaPlayer.create(this, R.raw.button_sound)
        setListeners()
        startAds()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_rate_us -> rateUsButtonListener()
                R.id.nav_share -> shareButtonListener()
                R.id.nav_about_us -> aboutUsButtonListener()
                R.id.language -> languageButtonListener()
            }
            true
        }
        setNavigationDrawerSwitchListeners(navView)
    }



    // Loads the app theme: dark or light.
    private fun loadAppTheme() {
        if (darkMode) AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
    }

    // Listener of the share button.
    private fun shareButtonListener() {
        val sharingText = "Download this App!\nhttps://play.google.com/store/apps/details?id=$packageName"
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingText)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        startActivity(Intent.createChooser(shareIntent, "Share Via"))

    }

    // TTS onInit. When loaded, sets the language to the default.
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    // TTS onDestroy. Shutdown TTS when the activity is destroyed.
    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    // Gets the language stored in prefs and sets it to the Locate
    private fun loadLocate() {
        val language = prefs.getLanguageConfig()
        setLocate(language)
    }

    // Listeners of Sound, Vibration, Reading, Language and DarkMode.
    private fun setNavigationDrawerSwitchListeners(navView: NavigationView) {
        val soundItem = navView.menu.findItem(R.id.sound)
        val soundSwitch = soundItem.actionView as SwitchCompat
        soundSwitch.isChecked = sound

        val vibrationItem = navView.menu.findItem(R.id.vibrate)
        val vibrationSwitch = vibrationItem.actionView as SwitchCompat
        vibrationSwitch.isChecked = vibration

        val readingItem = navView.menu.findItem(R.id.reading)
        val readingSwitch = readingItem.actionView as SwitchCompat
        readingSwitch.isChecked = reading

        soundSwitch.setOnClickListener { setSwitchListener(soundSwitch, "sound") }
        vibrationSwitch.setOnClickListener { setSwitchListener(vibrationSwitch, "vibration") }

        readingSwitch.setOnClickListener {
            reading = readingSwitch.isChecked
            prefs.saveReadingConfig(reading)
        }

        val languageItem = navView.menu.findItem(R.id.language)
        if (prefs.getLanguageConfig() == "es") languageItem.setActionView(R.layout.flag_spanish)
        else languageItem.setActionView(R.layout.flag_english)

        val darkModeItem = navView.menu.findItem(R.id.dark_mode)
        val darkModeSwitch = darkModeItem.actionView as SwitchCompat
        darkModeSwitch.isChecked = darkMode
        darkModeSwitch.setOnClickListener {
            darkMode = darkModeSwitch.isChecked
            prefs.saveDarkModeConfig(darkMode)
            loadAppTheme()
        }
    }

    // Listener of VibrationSwitch and SoundSwitch.
    private fun setSwitchListener(switch: SwitchCompat, feature: String){
        if (feature == "vibration") {
            vibration = switch.isChecked
            prefs.saveVibrationConfig(vibration)
        } else {
            sound = switch.isChecked
            prefs.saveSoundConfig(sound)
        }
    }

    // Reads the given string in the TTS.
    private fun read(string: String){
        if (reading){
            tts.speak(string.replace(".", getString(R.string.point)), TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    // Checks if vibration and sound are enabled, then calls their respective functions.
    private fun vibrateSound(){
        if (vibration) vibrate()
        if (sound) mediaPlayer.start()
    }

    // Vibrate function. It is called when the Vibration Switch is enabled.
    private fun vibrate(){
        val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE) )
        }else{
            @Suppress("DEPRECATION")
            vib.vibrate(55)
        }
    }

    // DrawerLayout onBackPressed. It is called when the back is pressed and the NavigationView is open.
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Listener of the Rate Us Button. It opens the app package in Google Play or Internet.
    private fun rateUsButtonListener(){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))) //$packageName
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    // Listener of the About Us Button. It creates the PrivacyPolicyDialogFragment and shows it.
    private fun aboutUsButtonListener(){
        //val dialog = PrivacyPolicyDialogFragment()
        PrivacyPolicyDialogFragment().show(supportFragmentManager, "custom dialog")
    }

    // Listener of the Language Button. It creates an AlertDialog and shows it.
    // If the first option is chosen (listLanguages[0]) -> set locates to ("es").
    // If the second option is chosen -> set locates to ("en".
    private fun languageButtonListener(){
        val listLanguages = arrayOf("Spanish", "English")
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setSingleChoiceItems(listLanguages,-1) { dialog, which ->
            if (which == 0){
                setLocate("es")

            } else if (which == 1){
                setLocate("en")
            }
            dialog.dismiss()
            recreate()
        }
        mBuilder.create().show()
    }

    @Suppress("DEPRECATION")
    // Set locale to the given language (String) and stores it in prefs.
    private fun setLocate(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        prefs.saveLanguageConfig(language)
    }

    // Starts the Banner Ad.
    private fun startAds() {
        val adBanner = findViewById<AdView>(R.id.banner)
        val adRequest = AdRequest.Builder().build()
        adBanner.loadAd(adRequest)
    }



    // Listener of Operator Buttons.
    private fun operatorOnClickListener(btn: Button, subtractButton: Button){
        vibrateSound()
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

    // Listener of Number Buttons.
    private fun numberButtonOnClickListener(btn: Button, zeroButton: Button) {
        vibrateSound()
        if (btn == zeroButton) {
            if (equation.text == "0") return
        }
        concatenateNumbers(btn.text.toString())

    }

    // Listener of Point Button.
    private fun pointButtonOnClickListener(btn: Button){
        vibrateSound()
        if (pointFree) {
            if (equation.text == "" || equation.text.last().toString() in operations) concatenateNumbers("0")
            concatenateNumbers(btn.text.toString())
            operationFree = false
            pointFree = false
        }

    }

    // Listener of Clear Button.
    private fun clearButtonOnClickListener(){
        read(getString(R.string.delete))
        vibrateSound()
        cleanEquation()
        pointFree = true
        operationFree = true
    }

    // Listener of Equals Button.
    private fun equalsButtonOnClickListener(){
        vibrateSound()
        if (operationFree && equation.text.isNotEmpty()){
            checkPoint()
            solveEquation()
            read("= ${result.text}")
        }
        pointFree = true

    }

    // Listener of Delete Button.
    private fun deleteButtonOnClickListener(){
        vibrateSound()
        read(getString(R.string.delete))
        if (equation.text.isEmpty()){
            return//@setOnClickListener
        }
        val newEquation = equation.text.dropLast(1)
        if (newEquation.isEmpty()){
            equation.text = ""
            return//@setOnClickListener
        }
        val lastCharacter = newEquation.last().toString()
        val removedCharacter = equation.text[equation.text.length-1].toString()
        if (removedCharacter in operations) {
            operationFree = true
            if (checkPoint()) pointFree = false
        }

        else if (removedCharacter == ".") {
            pointFree = true
            operationFree = true
        }
        if (lastCharacter in operations) {
            operationFree = false
        }
        else if (lastCharacter == ".") {
            operationFree = false
        }
        equation.text = newEquation

    }

    // Checks if the equation contains a Point.
    // Return true if it does, false otherwise.
    private fun checkPoint() : Boolean{
        val len = equation.text.length
        var c = 0
        for (i in len-1 downTo 0){
            if (equation.text[i].toString() in operations){
                c = i
            }
        }
        if (c == 0) c = len
        return (equation.text.subSequence(len-c, len-1).contains('.'))
    }

    // Finds all the Views and calls their respective listeners.
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



        val numberButtons = listOf<Button>(zeroButton, oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
            sevenButton, eightButton, nineButton)

        val operationButtons = listOf<Button>(divideButton, multiplyButton, additionButton, subtractButton)

        operationButtons.forEach { btn -> btn.setOnClickListener{operatorOnClickListener(btn, subtractButton)} }
        numberButtons.forEach { btn -> btn.setOnClickListener{numberButtonOnClickListener(btn, zeroButton)} }
        pointButton.setOnClickListener { pointButtonOnClickListener(pointButton) }
        clearButton.setOnClickListener { clearButtonOnClickListener() }
        equalsButton.setOnClickListener { equalsButtonOnClickListener() }
        deleteButton.setOnClickListener { deleteButtonOnClickListener() }
        deleteButton.setOnLongClickListener {
            clearButtonOnClickListener()
            return@setOnLongClickListener true
        }

        result = findViewById(R.id.result_view)
        equation = if (Build.VERSION.SDK_INT <= 25){
            operationButtons.forEach { it.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F) }
            equalsButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F)
            findViewById<AppCompatTextView>(R.id.equation_view_compat)
        } else { findViewById(R.id.equation_view) }
    }

    //replace("-", "--") en operatorsListener
    @SuppressLint("SetTextI18n")
    // Concatenates the Equation with the given digit.
    private fun concatenateNumbers(digit:String){
        if (result.text == getString(R.string.error)) result.text = ""
        read (digit)
        var nDigit = digit
        if (digit == "−") nDigit = "-"
        if (equation.text.isNotEmpty() && result.text.isNotEmpty()){
            equation.text = result.text
            result.text = ""
        }
        equation.text = "${equation.text}${nDigit}"
        operationFree = true
    }

    // Cleans the equation and the result views.
    private fun cleanEquation(){
        equation.text = ""
        result.text = ""
    }

    // Solves the equation and puts the result in the result view.
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
                if (resultList[lastPos-1] == getString(R.string.error)){
                    divisionByZeroError()
                    return
                }
                resultList.removeAt(lastPos)
            }
        }
        val res = resultList[0]
        result.text = removeRightZeros(res)
    }

    // Sets the text of the result to "Error".
    private fun divisionByZeroError(){
        result.text = getString(R.string.error)
    }

    // Sorts the operator in the stack.
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

    // Makes the respective operations with the given numbers. Returns the result.
    private fun makeOperation(n1:BigDecimal, n2:BigDecimal, operator:String): String {
        lateinit var res: BigDecimal
        val zeroBigDecimal = BigDecimal(0)
        when(operator) {
            ADDITION -> res = n1.add(n2)
            SUBTRACT -> res = n1.subtract(n2)
            MULTIPLY -> res = n1.multiply(n2)
            DIVISION -> if (n2 == zeroBigDecimal) return getString(R.string.error)
                        else if (n1 == zeroBigDecimal) res = zeroBigDecimal
                        else res = n1.divide(n2, 10, RoundingMode.HALF_UP)
        }
        return removeLeftZeros(removeRightZeros(res.toString()))
    }

    // Removes the insignificant zeros of the left, and returns the "clean" number (in a String).
    // Example -> removeLeftZeros("010") -> "10"
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

    // Removes the insignificant zeros of the right, and returns the "clean" number (in a String).
    // Example -> removeRightZeros("10.50") -> "10.5"
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

    // Returns the order of the given operator.
    // Order (ascendant): subtract/addition -> division -> multiply
    private fun operatorOrder(operator: String): Int {
        when (operator){
            SUBTRACT -> return 0
            ADDITION -> return 0
            DIVISION -> return 1
            MULTIPLY -> return 2
        }
        return -1
    }
}