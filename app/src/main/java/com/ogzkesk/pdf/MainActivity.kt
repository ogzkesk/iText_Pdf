package com.ogzkesk.pdf

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ogzkesk.pdf.ui.theme.PdfTheme
import com.ogzkesk.showToast
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PdfTheme {
                Main()
            }
        }
    }
}

@Composable
fun Main() {

    val context = LocalContext.current
    val mPdf = remember { MPdf(context) }
    val coroutineScope = rememberCoroutineScope()
    var nameText by remember { mutableStateOf("") }
    var surnameText by remember { mutableStateOf("") }
    var schoolText by remember { mutableStateOf("") }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            Toast.makeText(context, "permissionResult : $it", Toast.LENGTH_SHORT).show()
        }
    )
    val documentHandler = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            println(it?.path)
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    Scaffold { padd ->
        Column(
            modifier = Modifier
                .padding(padd)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = nameText, onValueChange = { nameText = it })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = surnameText, onValueChange = { surnameText = it })
            Spacer(modifier = Modifier.height(16.dp))
            TextField(value = schoolText, onValueChange = { schoolText = it })
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val dummyTestModels1 = getDummyTestModels1()
                        val dummyTestModels2 = getDummyTestModels2()
                        val pdf = mPdf.create(dummyTestModels1, dummyTestModels2)
                        val result = mPdf.exportPdf(pdf)
                        if (result) {
                            context.showToast("Pdf saved successfully")
                        }
                    }
                },
                content = {
                    Text(text = "create")
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    documentHandler.launch(arrayOf("application/pdf"))
                },
                content = {
                    Text(text = "open")
                }
            )
        }
    }
}

private fun getDummyTestModels1(): List<TestModel1> {
    return listOf(
        TestModel1(0, "Test1", "Test1", "Bulent"),
        TestModel1(1, "Test2", "Test2", "Bulent"),
        TestModel1(2, "Test3", "Test3", "Bulent"),
        TestModel1(3, "Test4", "Test4", "Bulent"),
        TestModel1(4, "Test5", "Test5", "Bulent"),
    )
}

private fun getDummyTestModels2(): List<TestModel2> {
    return listOf(
        TestModel2(0, "Test1", "Test1"),
        TestModel2(1, "Test2", "Test2"),
        TestModel2(2, "Test3", "Test3"),
        TestModel2(3, "Test4", "Test4"),
    )
}




