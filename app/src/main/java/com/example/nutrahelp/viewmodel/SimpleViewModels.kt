package com.example.nutrahelp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutrahelp.data.A1CDao
import com.example.nutrahelp.data.A1CEntryEntity
import com.example.nutrahelp.data.AlcoholDao
import com.example.nutrahelp.data.AlcoholEntryEntity
import com.example.nutrahelp.data.BodyFatDao
import com.example.nutrahelp.data.BodyFatEntryEntity
import com.example.nutrahelp.data.BpDao
import com.example.nutrahelp.data.BpEntryEntity
import com.example.nutrahelp.data.BloodSugarDao
import com.example.nutrahelp.data.BloodSugarEntryEntity
import com.example.nutrahelp.data.CaffeineDao
import com.example.nutrahelp.data.CaffeineEntryEntity
import com.example.nutrahelp.data.CalorieDao
import com.example.nutrahelp.data.CalorieEntryEntity
import com.example.nutrahelp.data.CheckInDao
import com.example.nutrahelp.data.CheckInEntryEntity
import com.example.nutrahelp.data.CholesterolDao
import com.example.nutrahelp.data.CholesterolEntryEntity
import com.example.nutrahelp.data.EnergyDao
import com.example.nutrahelp.data.EnergyEntryEntity
import com.example.nutrahelp.data.ExerciseDao
import com.example.nutrahelp.data.ExerciseEntryEntity
import com.example.nutrahelp.data.FiberDao
import com.example.nutrahelp.data.FiberEntryEntity
import com.example.nutrahelp.data.Glp1Dao
import com.example.nutrahelp.data.Glp1EntryEntity
import com.example.nutrahelp.data.GutDao
import com.example.nutrahelp.data.GutEntryEntity
import com.example.nutrahelp.data.HeartRateDao
import com.example.nutrahelp.data.HeartRateEntryEntity
import com.example.nutrahelp.data.HungerDao
import com.example.nutrahelp.data.HungerEntryEntity
import com.example.nutrahelp.data.InflammationDao
import com.example.nutrahelp.data.InflammationEntryEntity
import com.example.nutrahelp.data.JournalDao
import com.example.nutrahelp.data.JournalEntryEntity
import com.example.nutrahelp.data.LabDao
import com.example.nutrahelp.data.LabEntryEntity
import com.example.nutrahelp.data.MacroDao
import com.example.nutrahelp.data.MacroEntryEntity
import com.example.nutrahelp.data.MealTimingDao
import com.example.nutrahelp.data.MealTimingEntryEntity
import com.example.nutrahelp.data.MeasurementDao
import com.example.nutrahelp.data.MeasurementEntryEntity
import com.example.nutrahelp.data.MilestoneDao
import com.example.nutrahelp.data.MilestoneEntryEntity
import com.example.nutrahelp.data.MindfulMealDao
import com.example.nutrahelp.data.MindfulMealEntryEntity
import com.example.nutrahelp.data.MoodDao
import com.example.nutrahelp.data.MoodEntryEntity
import com.example.nutrahelp.data.NsvDao
import com.example.nutrahelp.data.NsvEntryEntity
import com.example.nutrahelp.data.NutraHelpDatabase
import com.example.nutrahelp.data.NutrientDao
import com.example.nutrahelp.data.NutrientEntryEntity
import com.example.nutrahelp.data.ProteinDao
import com.example.nutrahelp.data.ProteinEntryEntity
import com.example.nutrahelp.data.SensitivityDao
import com.example.nutrahelp.data.SensitivityEntryEntity
import com.example.nutrahelp.data.SideEffectDao
import com.example.nutrahelp.data.SideEffectEntryEntity
import com.example.nutrahelp.data.SodiumDao
import com.example.nutrahelp.data.SodiumEntryEntity
import com.example.nutrahelp.data.StepDao
import com.example.nutrahelp.data.StepEntryEntity
import com.example.nutrahelp.data.StressDao
import com.example.nutrahelp.data.StressEntryEntity
import com.example.nutrahelp.data.SugarDao
import com.example.nutrahelp.data.SugarEntryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoodViewModel(
    app: Application,
    private val dao: MoodDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).moodDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MoodEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MoodEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class EnergyViewModel(
    app: Application,
    private val dao: EnergyDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).energyDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: EnergyEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: EnergyEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class GutViewModel(
    app: Application,
    private val dao: GutDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).gutDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: GutEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: GutEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class InflammationViewModel(
    app: Application,
    private val dao: InflammationDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).inflammationDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: InflammationEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: InflammationEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class SensitivityViewModel(
    app: Application,
    private val dao: SensitivityDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).sensitivityDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: SensitivityEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: SensitivityEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class BloodSugarViewModel(
    app: Application,
    private val dao: BloodSugarDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).bloodSugarDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: BloodSugarEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: BloodSugarEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class BpViewModel(
    app: Application,
    private val dao: BpDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).bpDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: BpEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: BpEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class StressViewModel(
    app: Application,
    private val dao: StressDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).stressDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: StressEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: StressEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class ExerciseViewModel(
    app: Application,
    private val dao: ExerciseDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).exerciseDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: ExerciseEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: ExerciseEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class MeasurementViewModel(
    app: Application,
    private val dao: MeasurementDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).measurementDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MeasurementEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MeasurementEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class CholesterolViewModel(
    app: Application,
    private val dao: CholesterolDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).cholesterolDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: CholesterolEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: CholesterolEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class HeartRateViewModel(
    app: Application,
    private val dao: HeartRateDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).heartRateDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: HeartRateEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: HeartRateEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class A1CViewModel(
    app: Application,
    private val dao: A1CDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).a1cDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: A1CEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: A1CEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class StepViewModel(
    app: Application,
    private val dao: StepDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).stepDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: StepEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: StepEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class BodyFatViewModel(
    app: Application,
    private val dao: BodyFatDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).bodyFatDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: BodyFatEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: BodyFatEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class FiberViewModel(
    app: Application,
    private val dao: FiberDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).fiberDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: FiberEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: FiberEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class AlcoholViewModel(
    app: Application,
    private val dao: AlcoholDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).alcoholDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: AlcoholEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: AlcoholEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class CaffeineViewModel(
    app: Application,
    private val dao: CaffeineDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).caffeineDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: CaffeineEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: CaffeineEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class SugarViewModel(
    app: Application,
    private val dao: SugarDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).sugarDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: SugarEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: SugarEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class SodiumViewModel(
    app: Application,
    private val dao: SodiumDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).sodiumDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: SodiumEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: SodiumEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class MacroViewModel(
    app: Application,
    private val dao: MacroDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).macroDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MacroEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MacroEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class ProteinViewModel(
    app: Application,
    private val dao: ProteinDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).proteinDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: ProteinEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: ProteinEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class CalorieViewModel(
    app: Application,
    private val dao: CalorieDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).calorieDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: CalorieEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: CalorieEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class NutrientViewModel(
    app: Application,
    private val dao: NutrientDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).nutrientDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: NutrientEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: NutrientEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class HungerViewModel(
    app: Application,
    private val dao: HungerDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).hungerDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: HungerEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: HungerEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class MealTimingViewModel(
    app: Application,
    private val dao: MealTimingDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).mealTimingDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MealTimingEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MealTimingEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class MindfulMealViewModel(
    app: Application,
    private val dao: MindfulMealDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).mindfulMealDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MindfulMealEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MindfulMealEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class MilestoneViewModel(
    app: Application,
    private val dao: MilestoneDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).milestoneDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: MilestoneEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: MilestoneEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class NsvViewModel(
    app: Application,
    private val dao: NsvDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).nsvDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: NsvEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: NsvEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class SideEffectViewModel(
    app: Application,
    private val dao: SideEffectDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).sideEffectDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: SideEffectEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: SideEffectEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class LabViewModel(
    app: Application,
    private val dao: LabDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).labDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: LabEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: LabEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class JournalViewModel(
    app: Application,
    private val dao: JournalDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).journalDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: JournalEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: JournalEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class CheckInViewModel(
    app: Application,
    private val dao: CheckInDao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).checkInDao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: CheckInEntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: CheckInEntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}

class Glp1ViewModel(
    app: Application,
    private val dao: Glp1Dao,
) : AndroidViewModel(app) {
    constructor(app: Application) : this(app, NutraHelpDatabase.getInstance(app).glp1Dao())

    val entries = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insert(entry: Glp1EntryEntity) = viewModelScope.launch { dao.insert(entry) }
    fun delete(entry: Glp1EntryEntity) = viewModelScope.launch { dao.delete(entry) }
    fun deleteAll() = viewModelScope.launch { dao.deleteAll() }
}