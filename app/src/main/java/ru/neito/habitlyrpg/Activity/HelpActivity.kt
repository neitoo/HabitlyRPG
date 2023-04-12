package ru.neito.habitlyrpg.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_help.*
import ru.neito.habitlyrpg.R

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val container = findViewById<LinearLayout>(R.id.linearHelp)

        arrowBackSettingsBtn.setOnClickListener {
            onBackPressed()
        }

        addButton(container, "Очки здоровья")
        addButton(container, "Очки опыта")
        addButton(container, "Монеты")
        addButton(container, "Магазин")
        addButton(container, "Боссы")
    }

    private fun addButton(container: LinearLayout, buttonText: String) {
        // Создаем копию шаблона кнопки
        val buttonLayout = layoutInflater.inflate(R.layout.help_layout, container, false)

        // Находим кнопку и текстовое поле в шаблоне
        val button = buttonLayout.findViewById<Button>(R.id.viewHelpButton)
        val infoContainer = buttonLayout.findViewById<LinearLayout>(R.id.helpInfoContainer)
        val infoTextView = buttonLayout.findViewById<TextView>(R.id.helpText)

        // Устанавливаем текст на кнопке и в текстовом поле
        button.text = buttonText
        infoTextView.text = "Информация о $buttonText"

        // Устанавливаем обработчик нажатия на кнопку
        button.setOnClickListener {
            // При нажатии на кнопку меняем видимость текстового поля
            infoContainer.visibility = if (infoContainer.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        // Добавляем созданную кнопку в контейнер
        container.addView(buttonLayout)
    }
}