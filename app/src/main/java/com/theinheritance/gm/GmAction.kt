package com.theinheritance.gm

import com.theinheritance.simulation.BusinessState

/** The spec calls the run state `GameState`; it is the simulation's [BusinessState]. */
typealias GameState = BusinessState

/** Marker for all 172 GM tool calls across the six action surfaces. */
interface GmAction

data class MalformedAction(val name: String, val reason: String) : GmAction

// Shared kernel enums used by the action surfaces.
enum class Tone { WARM, COLD, EVASIVE, PLAYFUL, GRIEVING, ANGRY }
enum class Weather { CLEAR, RAIN, STORM, HEATWAVE, FOG }
enum class Season { SPRING, SUMMER, AUTUMN, WINTER }

data class Deadline(val label: String, val day: Int)
