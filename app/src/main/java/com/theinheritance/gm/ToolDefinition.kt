package com.theinheritance.gm

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToolDefinition @Inject constructor() {
    val names: List<String> = listOf(
        "post_transaction", "void_entry", "highlight_line", "annotate_margin",
        "shift_trust", "speak_in_character", "advance_day", "send_letter_from_bank",
        "shift_tone", "go_silent", "foreshadow", "reveal_partial_truth"
    )
}
