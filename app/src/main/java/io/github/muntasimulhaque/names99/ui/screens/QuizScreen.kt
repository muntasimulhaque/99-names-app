package io.github.muntasimulhaque.names99.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.muntasimulhaque.names99.R
import io.github.muntasimulhaque.names99.data.Name
import io.github.muntasimulhaque.names99.data.NamesRepository

private data class Question(val name: Name, val options: List<String>)

private fun buildQuiz(all: List<Name>, count: Int = 10): List<Question> =
    all.shuffled().take(count).map { name ->
        val distractors = all.filter { it.number != name.number }
            .shuffled().take(3).map { it.title }
        Question(name, (distractors + name.title).shuffled())
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(navController: NavController) {
    val context = LocalContext.current
    val all = remember { NamesRepository.load(context) }
    var quiz by remember { mutableStateOf(buildQuiz(all)) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<String?>(null) }
    var finished by remember { mutableStateOf(false) }

    fun restart() {
        quiz = buildQuiz(all); index = 0; score = 0; selected = null; finished = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quiz)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (finished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "$score / ${quiz.size}",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    when {
                        score == quiz.size -> "Masha'Allah — a perfect round."
                        score >= quiz.size / 2 -> "Well done. Keep going."
                        else -> "Keep at it — the flashcards will help."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                Button(onClick = { restart() }) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Try another round")
                }
            }
            return@Scaffold
        }

        val q = quiz[index]
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (index + 1) / quiz.size.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Question ${index + 1} of ${quiz.size}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = q.name.arabic,
                fontFamily = FontFamily.Serif,
                fontSize = 44.sp,
                lineHeight = 60.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(q.name.transliteration, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(24.dp))

            q.options.forEach { option ->
                val isCorrect = option == q.name.title
                val revealed = selected != null
                val colors = when {
                    revealed && isCorrect -> MaterialTheme.colorScheme.primary
                    revealed && option == selected -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.outline
                }
                OutlinedButton(
                    onClick = {
                        if (selected == null) {
                            selected = option
                            if (isCorrect) score++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    border = BorderStroke(
                        width = if (revealed && (isCorrect || option == selected)) 2.dp else 1.dp,
                        color = colors
                    )
                ) {
                    Text(
                        option,
                        color = if (revealed && (isCorrect || option == selected)) colors
                        else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (index + 1 >= quiz.size) finished = true
                    else { index++; selected = null }
                },
                enabled = selected != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(if (index + 1 >= quiz.size) "See result" else "Next")
            }
        }
    }
}
