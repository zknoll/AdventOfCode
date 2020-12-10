package adventofcode2019.day7

class AmplifierBank {
    private val amps: HashMap<String, Amplifier> = hashMapOf()
    private val wireMap: HashMap<Amplifier, Amplifier> = hashMapOf()
    var feedbackEnabled = false
    var inputAmp: String? = null
    var outputAmp: String? = null

    fun addAmp(amp: Amplifier) {
        amps[amp.name] = amp
    }

    fun wireAmpsTogether(ampName1: String, ampName2: String) {
        val outAmp = amps[ampName1]!!
        val inAmp = amps[ampName2]!!
        wireMap[outAmp] = inAmp
    }

    fun getOutput(initialInput: Int = 0): Int {
        val inAmp = inputAmp
        val outAmp = outputAmp
        if (inAmp == null || outAmp == null) {
            throw Exception("Unable to start amplifier bank, no input or output set")
        }
        var currentAmp = amps[inputAmp]!!
        currentAmp.supplyInput(initialInput)
        while (true) {
            println("executing amplifier ${currentAmp.name}")
            currentAmp.execute()
            if (feedbackEnabled && currentAmp.hasHalted()) {
                println("exited because halted")
                break
            }
            val inputToNext = currentAmp.outputValue
            if (wireMap[currentAmp] == null) {
                break
            }
            currentAmp = wireMap[currentAmp]!!
            currentAmp.supplyInput(inputToNext!!)
        }
        return amps[outputAmp]!!.outputValue as Int
    }

}