package com.example.policeplus.views

import CarViewModel
import CarViewModelFactory
import android.Manifest
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.example.policeplus.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel



@Composable
fun ScanScreen(
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    var hasPermission by remember { mutableStateOf(false) }
    val viewModel: CarViewModel = viewModel()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    BackHandler {
        onClose() // Navigate to Home Screen
    }

    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
            LicensePlateScannerScreen(
                viewModel = viewModel, // ✅ Pass ViewModel here
                onClose = onClose,
                onConfirm = onConfirm
            )

            // Close Button (Top Right Corner)
            IconButton(
                onClick = { onClose() },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
            }
        }
    } else {
        Text("Camera permission required!", Modifier.padding(16.dp))
    }
}


@Composable
fun CameraScreen(onImageCaptured: (Uri) -> Unit) {

    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build() }

    var previewView: PreviewView? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.surfaceProvider = view.surfaceProvider
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageCapture
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraX", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        Column (modifier = Modifier.fillMaxSize().padding(45.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom){
            Button (
                onClick = {
                    val outputFile = File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

                    imageCapture.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val uri = Uri.fromFile(outputFile)
                                onImageCaptured(uri)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("Camera", "Image capture failed: ${exception.message}")
                            }
                        }
                    )
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                )
            ) {
                Image(painter = painterResource(R.drawable.scan_button), contentDescription = "Scan Button",modifier=Modifier.size(120.dp))
            }
        }


    }
}

fun extractTextFromImage(context: Context, uri: Uri, onTextExtracted: (String) -> Unit) {
    val image = InputImage.fromFilePath(context, uri) // ✅ Use raw image without rotation
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val lines = visionText.textBlocks.flatMap { it.lines }.map { it.text.trim() }
            Log.d("OCR", "Extracted Lines: $lines")


            val orderedText = lines.joinToString(" ") // Normal order in portrait mode


            val licensePlate = extractLicensePlate(orderedText)

            if (licensePlate != null) {
                onTextExtracted(licensePlate)
            } else {
                onTextExtracted(extractLicensePlateCritical(orderedText))
            }
        }
        .addOnFailureListener { e ->
            Log.e("OCR", "Text recognition failed: ${e.message}")
        }
}



fun extractLicensePlate(text: String): String? {

    text.replace('O', '0')
        .replace('B', '8')
        .replace('S', '5')
    // Split text into lines, trim spaces, and reorder if needed
    val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    // Join lines in normal top-to-bottom order
    val cleanedText = lines.joinToString(" ") { it }
    val numericText = cleanedText.replace("[^0-9\\s]".toRegex(), "") // Keeps only numbers and spaces

    // Match "12345 678 01" OR "1234567801" (with or without spaces)
    val pattern = Regex("\\b\\d{5}\\s?\\d{3}\\s?(0[1-9]|[1-5][0-8])\\b")

    return pattern.find(numericText)?.value?.replace("\\s".toRegex(), "") // Remove spaces before returning
}


fun extractLicensePlateCritical(text: String):String{

    text.replace('O', '0')
        .replace('B', '8')
        .replace('S', '5')
    val lines = text.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

    // Join lines in normal top-to-bottom order
    val cleanedText = lines.joinToString(" ") { it }
    val numericText = cleanedText.replace("[^0-9\\s]".toRegex(), "")

    return numericText.replace("\\s".toRegex(),"")
}


@Composable
fun LicensePlateScannerScreen(
    viewModel: CarViewModel, // ✅ Pass ViewModel to fetch data
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    var extractedText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        CameraScreen { imageUri ->
            isLoading = true
            extractTextFromImage(context, imageUri) { text ->
                extractedText = text
                isLoading = false
                showConfirmationDialog = true
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).zIndex(100f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp), color = Color.White)
            }
        }

        IconButton(
            onClick = { onClose() },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
        }
    }

    if (showConfirmationDialog) {
        var editedText by remember { mutableStateOf(extractedText) }

        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm License Plate") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        label = { Text("License Plate Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.fetchCar(editedText) // ✅ Fetch car data
                    showConfirmationDialog = false
                    onConfirm() // ✅ Navigate to CarDataScreen
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}







