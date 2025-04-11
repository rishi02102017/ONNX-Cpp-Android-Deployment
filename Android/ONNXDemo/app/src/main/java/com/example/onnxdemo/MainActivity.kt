package com.example.onnxdemo

import java.nio.FloatBuffer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import ai.onnxruntime.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val outputView: TextView = findViewById(R.id.outputView)

        Thread {
            try {
                // Load model from assets
                val modelBytes = assets.open("mnist_opset11.onnx").readBytes()

                // Initialize ONNX runtime
                val env = OrtEnvironment.getEnvironment()
                val session = env.createSession(modelBytes)

                // Read model input info
                val inputName = session.inputNames.iterator().next()
                val inputShape = session.inputInfo[inputName]?.info as TensorInfo
                Log.d("ONNX", "Input shape: ${inputShape.shape.contentToString()}")

                // Prepare dummy input (1x1x32x32 filled with zeros)

                val inputData = FloatArray(1 * 1 * 28 * 28) { 0f }
                val inputBuffer = FloatBuffer.wrap(inputData)
                val inputTensor = OnnxTensor.createTensor(env, inputBuffer, longArrayOf(1, 1, 28, 28))

                // Run inference
                val output = session.run(mapOf(inputName to inputTensor))[0].value

                // Format output
                val resultText = when (output) {
                    is Array<*> -> output.contentDeepToString()
                    is FloatArray -> output.contentToString()
                    else -> "Unknown output type: ${output?.javaClass}"
                }

                Log.d("ONNX", "Output: $resultText")

                // Update UI
                runOnUiThread {
                    outputView.text = getString(R.string.inference_output, resultText)
                }

            } catch (e: Exception) {
                Log.e("ONNX", "Error running inference", e)
                runOnUiThread {
                    outputView.text = "Inference failed: ${e.message}"
                }
            }
        }.start()
    }
}
