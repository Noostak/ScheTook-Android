package com.sopt.core.designsystem.component.checkbox

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sopt.core.designsystem.theme.NoostakAndroidTheme
import com.sopt.core.designsystem.theme.NoostakTheme
import com.sopt.core.extension.noRippleClickable

@Composable
fun NoostakCheckbox(
    modifier: Modifier = Modifier,
    text: String = "",
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    textStyle: TextStyle = NoostakTheme.typography.b4SemiBold,
    borderColor: Color = NoostakTheme.colors.gray500,
    backgroundColorChecked: Color = NoostakTheme.colors.gray50,
    backgroundColorUnchecked: Color = NoostakTheme.colors.white,
    paddingValues: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 15.dp)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                color = if (isChecked) backgroundColorChecked else backgroundColorUnchecked,
                shape = RoundedCornerShape(10.dp)
            )
            .noRippleClickable { onCheckedChange(!isChecked) }
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = textStyle,
            color = NoostakTheme.colors.gray900
        )
        CircularCheckbox(
            isChecked = isChecked,
            onCheckedChange = { onCheckedChange(it) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoostakCheckboxPreview() {
    NoostakAndroidTheme {
        NoostakCheckbox(
            isChecked = true,
            text = "체크박스블라블라"
        )
    }
}
